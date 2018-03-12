/**
 * Reprogrammer to reprogram a single node, use Deluge to disseminate new code image.
 * Both base station and target node will change to another channel, thus, the dissemination
 * will have no effects to the rest of the network.
 *
 * This is the base station application
 *
 * Step 1: Send command from PC to start the procedure. Needed information is: cmd, target id, new channel
 * Step 2: Base station and node will use a Three-Way-Handshaking to establish a reliable link.
 * Step 3: start to disseminate and reprogram
 *
 * @author: Xiaoyang Zhong
 * @version $Revision: 3.0 $ $Date: 2014-03-03 17:24:21 $
**/

#include "Reprogrammer.h"
#include "Timer.h"
module ReprogrammerBaseP {
	uses {
		interface Boot;		// at booted, stop deluge
//		interface StdControl as DisseminationControl;
		interface Leds;

	// sink app, we don't need serial control, because Deluge Base Station already
	// started that
		// serial communication
		interface AMSend as SerialSend;
		interface Receive as SerialReceive;
		interface Packet as SerialPacket;
		
		// radio communication
		interface AMSend as RadioSend;
		interface Packet as RadioPacket;
		interface Receive as ReplyReceive;
	
		// timers
		interface Timer<TMilli> as RetxTimer;	// retransmit timer
		interface Timer<TMilli> as ReplyTimer;
		
		//TODO: different interface to change channel for 
		//		different mote platforms
	#if defined(PLATFORM_IRIS)
		// iris motes
		interface RadioChannel as ChangeChannel;
	#else
		// change channel
		// micaz and telosb motes
		interface CC2420Config as ChangeChannel;
	#endif
		// used to disable/enable LPL
		// Disable: set remote wakeup interval to 0
		// Enable: set the values back to DEFAULT
		interface SystemLowPowerListening;
		
		interface Random;
		interface ParameterInit<uint16_t> as SeedInit;
		interface LocalTime<TMilli>;
	}
}

implementation {
	message_t r_packet;
	message_t s_packet;

	bool radioBusy = FALSE;
	bool serialBusy = FALSE;
	
	uint8_t newChannel = CC2420_DEF_CHANNEL;	// default new channel is the original channel
	uint16_t targetNode = 0xFFFF;
	
	// command send to node
	uint8_t baseCmd = 0xFF;
	
	// replied node status
	uint8_t nodeStatus = 0xFF;
	uint8_t replyType = UNKNOWN;
	uint16_t replyNode = 0xFFFF;
	uint8_t nodeVersion = 0;
	uint16_t nodeVoltage = 0;
	
	uint8_t replyCount = 0;
	bool isNewReply = TRUE;
	uint16_t replyList[MAX_NUM_IDS];
	
	// retreis
	int8_t num_retries;		// if use uint8_t, once the num_retires reaches 0, it goes to 255
							// so the sending will not stop
	uint8_t randCode = 0;
	
	// target nodes list
	uint16_t nodeList[MAX_NUM_IDS];
	uint8_t nodeCount = 0;

//	uint8_t reply_rcv = 0;	// current version does not need this
	
	// functions and tasks
	void reset();	// reset base station to original status
	task void sendRadioMessage();
	task void sendSerialMessage();
	
	event void Boot.booted() {
		// TODO: some operations maybe needed.
//		call DisseminationControl.start();
	}
	
	// reset base station: 
	// enable LPL, change to original channel
	void reset() {
		uint8_t i;
		// enable LPL
		call SystemLowPowerListening.setDefaultRemoteWakeupInterval(LPL_DEF_REMOTE_WAKEUP);

		// reset channel before next broadcasting			
		call ChangeChannel.setChannel(CC2420_DEF_CHANNEL);
	#if defined (PLATFORM_IRIS)
	#else
		call ChangeChannel.sync();
	#endif
		
		for(i = 0; i < replyCount; i++) {
			replyList[i] = 0xFFFF;
		}
		
	}
	
	/*********************************** tasks *******************************************/	
	task void sendRadioMessage() {
		uint8_t i;
		cmd_msg_t* r_msg = (cmd_msg_t*)call RadioPacket.getPayload(&r_packet, sizeof(cmd_msg_t));
		
		r_msg -> cmd = baseCmd;
		r_msg -> nodeCount = nodeCount;	// number of target ids
		r_msg -> randCode = randCode;
		
		for(i = 0; i < nodeCount; i++) {
			r_msg -> nodeList[i] = nodeList[i];	
		}
		r_msg -> newChannel = newChannel;
		
		// no longer need this, this is a broadcast message
//		call PacketAcknowledgements.requestAck(&r_packet);	// request layer 2 acknowledgement
		
		if(!radioBusy)
			if (call RadioSend.send(0xFFFF, &r_packet, sizeof(cmd_msg_t)) == SUCCESS) {
				radioBusy = TRUE;
				call Leds.led1Toggle();
   	  		}
	}
	
	task void sendSerialMessage() {
		serial_reply_msg_t* s_msg = (serial_reply_msg_t*)call SerialPacket.getPayload(&s_packet, sizeof(serial_reply_msg_t));
		s_msg -> cmd = nodeStatus;
		s_msg -> nodeType = replyType;
		s_msg -> nodeid = replyNode;
		s_msg -> appVersion = nodeVersion;
		s_msg -> nodeVoltage = nodeVoltage;
		s_msg -> replyCount = replyCount;
			
		if(!serialBusy)
			if (call SerialSend.send(AM_BROADCAST_ADDR, &s_packet, sizeof(serial_reply_msg_t)) == SUCCESS) {
				serialBusy = TRUE;
	//			call Leds.led2Toggle();
      		}
	}
	
	
	/*********************************** Receive events ***********************************************/
	/**
 	* Sink: received cmd from PC, broadcast the command to motes
	**/	
	event message_t* SerialReceive.receive(message_t* msg, void* payload, uint8_t len) {
//		call DisseminationControl.start();
		if(len != sizeof(serial_cmd_msg_t)) {
		//	call Leds.led0Toggle();
			return msg;
		} else {
			uint8_t i;
			// get command, node list and new channel
			serial_cmd_msg_t* s_msg = (serial_cmd_msg_t*)payload;
			
	//		seqno++;
			
			// generate rand code for the command
			call SeedInit.init((uint16_t)call LocalTime.get());
			randCode = call Random.rand16();
		//	randCode++;
			
			baseCmd = s_msg -> cmd;
			newChannel = s_msg -> newChannel;
			
			nodeCount = s_msg -> nodeCount;
			for (i = 0; i < nodeCount; i++) {
				nodeList[i] = s_msg -> nodeList[i];
			}
			
			num_retries = MAX_RETX;
			
//			num_retries = 0;
			
			call Leds.led0Toggle();		
			
			if(call ReplyTimer.isRunning()){
				call ReplyTimer.stop();
			}	
		
			switch(baseCmd) {
				case ABORT:
				// ABORT will only be sent in the new channel, thus, base does not
				// need to reset to original channel
					
				// set to new channel, in case that base station is rebooted.
				// LPL has no effect here
					call ChangeChannel.setChannel(newChannel);
				#if defined (PLATFORM_IRIS)
				#else
					call ChangeChannel.sync();
				#endif
				
					post sendRadioMessage();
					break;
					
				case DISS:
				// DISS command
				// Base needs to reset to original channel.
					reset();
					// if some node didn't respond, wait for this time to change to new channel
					call ReplyTimer.startOneShot(REPLY_WAIT_INTERVAL);
					replyCount = 0;
					
					post sendRadioMessage();
					break;
				case REPORT:
					reset();
					// REPORT command should be sent in the original channel, but after senddone, 
					// the base should stay in the new channel
					call ReplyTimer.startOneShot(REPLY_WAIT_INTERVAL);	
																		
					post sendRadioMessage();
					break;
				case REPORT_SUBSET:	// Detect a subset of nodes
					reset();
					call ReplyTimer.startOneShot(REPLY_WAIT_INTERVAL);	
					replyCount = 0;					
					post sendRadioMessage();
					
					break;
				case REBOOT:	// reboot the target node
					reset();
					call ReplyTimer.startOneShot(REPLY_WAIT_INTERVAL);	

					post sendRadioMessage();
					break;
				case SET_ADDRESS:	// set node id
					reset();
					call ReplyTimer.startOneShot(REPLY_WAIT_INTERVAL);	

					post sendRadioMessage();
					break;
				default:
					break;
			} 
			
			return msg;
		}
	}
	
	/************************************* Radio sendDone events *******************************************/	

	event void RadioSend.sendDone(message_t* msg, error_t error) {
		radioBusy = FALSE;
		call RetxTimer.startOneShot(RETX_INTERVAL);
	}
	
	// retransmission
	event void RetxTimer.fired() {
		// if retransmissions < MAX_RETIRES, resend
		if(--num_retries) {
			if (baseCmd == DISS && replyCount < nodeCount) {
				// DISS cmd, some replies are not received
				post sendRadioMessage();
			} else {
				// other cmd, resend
				post sendRadioMessage();
			}
		} /*else {
			// change to the new channel
			if(call ReplyTimer.isRunning()){
				call ReplyTimer.stop();
			}
			
			// change to new channel
			// all nodes have been notified, change channel
			// in this case, user will not know which packet is not responding
			call ChangeChannel.setChannel(newChannel);
			#if defined (PLATFORM_IRIS)
			#else
			call ChangeChannel.sync();
			#endif
			call SystemLowPowerListening.setDefaultRemoteWakeupInterval(0);
		}*/
	}
	
		/************************************* Radio Receive *********************************************/
	/**
	 * Receive reply from target nodes
	 *
	 * Node replies are sent in the original channel with layer-2 acknowledgement enabled. If node 
	 * does not receive the ack of its reply message, it reset to the original state.
	 *
	 * There are still chances that Base's does not receive node's reply, but node changes to the 
	 * new channel. In this case the node will switch to the new channel for a shorter reset interval
	 **/
	
	event message_t* ReplyReceive.receive(message_t* msg, void* payload, uint8_t len) {
		if(len != sizeof(node_reply_msg_t)) {
			return msg;
		} else {
			uint8_t i;
			node_reply_msg_t* reply_msg = (node_reply_msg_t*)payload;
			replyNode = reply_msg -> nodeid;
			replyType = reply_msg -> nodeType;
			nodeVersion = reply_msg -> appVersion;
			nodeVoltage = reply_msg -> voltage;
			
			// TODO: test the correctness of this logic
			isNewReply = TRUE;
			
			call Leds.led2Toggle();
			
			for(i = 0; i < replyCount; i++) {
				if (replyList[i] == replyNode) {
					isNewReply = FALSE;
					break;
					// the reply from this node is already received, ignore it
//					return msg;
				}
			}
			
			// new reply, record the node
			if(isNewReply == TRUE) {
				replyList[replyCount] = replyNode;
				replyCount++;
			} else {
				// duplicated reply, ignore
				return msg;
			}
			
			// a new replied node, add to list
//			replyList[replyCount] = replyNode;
//			replyCount++;
			
			switch(reply_msg -> cmd) {
				// actually Base will only receive nodes' packets with DISS command
				// but we still add the ABORT logic here
				case ABORT:
					nodeStatus = NODE_ABORTED;
					post sendSerialMessage();	// notify the gateway
					break;
				case DISS:
					nodeStatus = NODE_READY;
					post sendSerialMessage();	// notify the gateway
					
					if (replyCount == nodeCount) {
						// if all nodes have replied, stop the wait timer, change to new
						// channel
						if(call ReplyTimer.isRunning()){
							call ReplyTimer.stop();
						}
				
						// change to new channel
						// all nodes have been notified, change channel
						// in this case, user will not know which packet is not responding
						call ChangeChannel.setChannel(newChannel);
					#if defined (PLATFORM_IRIS)
					#else
						call ChangeChannel.sync();
					#endif
						call SystemLowPowerListening.setDefaultRemoteWakeupInterval(0);
					} else {
						// still waiting
					}
					
					break;
				case REPORT:
					nodeStatus = NODE_REPORT;
					post sendSerialMessage();
					break;
				case REPORT_SUBSET:
					nodeStatus = NODE_REPORT_SUBSET;
					
					post sendSerialMessage();
					
					if (replyCount == nodeCount) {
						// if all nodes have replied, stop the wait timer, change to new
						// channel
						if(call ReplyTimer.isRunning()){
							call ReplyTimer.stop();
						}
				
						// change to new channel
						// all nodes have been notified, change channel
						// in this case, user will not know which packet is not responding
						call ChangeChannel.setChannel(newChannel);
					#if defined (PLATFORM_IRIS)
					#else
						call ChangeChannel.sync();
					#endif
						call SystemLowPowerListening.setDefaultRemoteWakeupInterval(0);
					} else {
						// still waiting
					}
					
					break;
				case REBOOT:
					nodeStatus = NODE_REBOOT;
					post sendSerialMessage();
					break;
				default:
					break;
			}

			return msg;

		}
	
	}
	
	
	
	event void ReplyTimer.fired() {
		replyCount = 0;
		// do not wait any more
		// all nodes have been notified, change channel and LPL
		call ChangeChannel.setChannel(newChannel);
	#if defined (PLATFORM_IRIS)
	#else
		call ChangeChannel.sync();
	#endif
		call SystemLowPowerListening.setDefaultRemoteWakeupInterval(0);

	}
	
	event void SerialSend.sendDone(message_t* msg, error_t error) {
		serialBusy = FALSE;
		if(error != SUCCESS) {
			post sendSerialMessage();
		}
	}

/************************ channel change done *****************************************/
#if defined (PLATFORM_IRIS)
	event void ChangeChannel.setChannelDone() {
		// Disable LPL
	}
#else
	event void ChangeChannel.syncDone(error_t error) {}
#endif

}	

