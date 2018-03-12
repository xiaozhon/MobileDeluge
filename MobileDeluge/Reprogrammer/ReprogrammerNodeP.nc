/**
 * Reprogrammer to reprogram a single node, use Deluge to disseminate new code image.
 * Both base station and target node will change to another channel, thus, the dissemination
 * will have no effects to the rest of the network.
 *
 * This is the Mote application
 *
 * Step 1: Use a Three-Way-Handshaking to establish a reliable link with base station.
 * Step 2: start to disseminate and reprogram
 *
 * @author: Xiaoyang Zhong
**/

#include "Reprogrammer.h"
#include "Timer.h"

// Header file for wdt to reboot the mote
#if defined(PLATFORM_IRIS) || defined(PLATFORM_MICAZ)
#include <avr/wdt.h>
#endif

module ReprogrammerNodeP {
	uses {
		interface StdControl as DisseminationControl;
		interface Boot;		// at booted, stop deluge
		interface Leds;
		
		// change channel
	#if defined(PLATFORM_IRIS)
		// iris motes
		interface RadioChannel as ChangeChannel;
	#else
		// micaz and telosb motes
		interface CC2420Config as ChangeChannel;
	#endif

		// receive command
		// send reply with the information of mote type
		interface Receive as RadioReceive;
		interface AMSend as ReplySend;
		interface Packet as ReplyPacket;
		interface PacketAcknowledgements as ReplyAck;
		
		// use this to get base station's address
		interface AMPacket;

		// timers
		interface Timer<TMilli> as InitDelayTimer;	// when init delay timer fires, stop dissemination
		interface Timer<TMilli> as ResetTimer;	// long timer, when fires, reset
		
		interface Timer<TMilli> as RetxTimer;
	//	interface Random;						// add some randomness to reduce collision
		
		// Disable/enable LPL
		// Disable: set remote wakeup interval and local wakeup interval to 0
		// Enable: set the two values back to DEFAULT
		interface LowPowerListening;
		interface SystemLowPowerListening;
		
		interface Read<uint16_t> as Voltage;
		
		interface ActiveMessageAddress;	// Change the node id
		
		command void stop();	// from Deluge to stop the REQ, DATA and DEV
		
	}
}

implementation {

	uint8_t newChannel = 0;
	uint8_t newCmd = 0xFF;
	uint8_t state = 0;
	uint16_t baseAddr = 0XFFFF;
	
	uint16_t voltage = 0;
	
	message_t reply_packet;
	bool radioBusy = FALSE;
	
	uint8_t num_retries = 0;
	
	uint8_t randCode = 0xFF;	// use randCode to filter duplicates
	
	bool isForMe = FALSE;
	
	enum {
		BOOTED = 10,
		DISS_STARTED = 11,
		ABORTED = 12,
	};
	
	// functions
	void reset();	// reset the mote to original channel

	task void sendReplyMessage() {
		node_reply_msg_t* reply_msg = (node_reply_msg_t*)call ReplyPacket.getPayload(&reply_packet, sizeof(node_reply_msg_t));
		reply_msg -> cmd = newCmd;
		reply_msg -> nodeid = call AMPacket.address();
		reply_msg -> nodeType = MOTE_TYPE;
		reply_msg -> appVersion = APP_VERSION;
		reply_msg -> voltage = voltage;
		
		call ReplyAck.requestAck(&reply_packet);	// request layer 2 acknowledgement
		
		if(!radioBusy) ;
			if (call ReplySend.send(baseAddr, &reply_packet, sizeof(node_reply_msg_t)) == SUCCESS) {
				radioBusy = TRUE;
			#if defined(LED_ENABLED)
				call Leds.led2Toggle();						// timer fired, green led blink
			#endif
      		}

	}

	event void Boot.booted() {
		// A delay timer is used here because Deluge starts beacons at booted
		// event, we must call "stop" after that
		state = BOOTED;
		call InitDelayTimer.startOneShot(500);
	}
	
	// After delay timer fires, motes stop deluge beacon (drip messages).
	event void InitDelayTimer.fired() {
		if(state == BOOTED) {
			call DisseminationControl.stop();
		}
	}
	
	// reset, put the node to original state
	void reset() {
		// stop deluge
		call DisseminationControl.stop();
		call stop();
		// enable LPL
		call SystemLowPowerListening.setDefaultRemoteWakeupInterval(LPL_DEF_REMOTE_WAKEUP);
		call LowPowerListening.setLocalWakeupInterval(LPL_DEF_LOCAL_WAKEUP);

		// reset channel 			
		call ChangeChannel.setChannel(CC2420_DEF_CHANNEL);		// or RF230_DEF_CHANNEL, they are the same
	#if defined (PLATFORM_IRIS)
	#else
		call ChangeChannel.sync();
	#endif
		// turn off leds to save power
		call Leds.led0Off();
		call Leds.led1Off();
		call Leds.led2Off();

		if(call ResetTimer.isRunning())
			call ResetTimer.stop();
	}
	
	/**
	 * Mote receives command from base station
	**/	
	event message_t* RadioReceive.receive(message_t* msg, void* payload, uint8_t len) {
		if(len != sizeof(cmd_msg_t)) {
			return msg;
		} else {
			uint8_t i;
			cmd_msg_t* cmd_msg = (cmd_msg_t*)payload;
	
		// TODO: how to efficiently filter the duplicated cmd?
		#if defined(LED_ENABLED)
			call Leds.led1Toggle();						// timer fired, green led blink
		#endif
			
			if(randCode != cmd_msg -> randCode) {		// 
				randCode = cmd_msg -> randCode;
			} else {	
				// oldSeqno equals to cmd_msg -> seqno 
				// duplicated cmd received
				return msg;
			}
			
			////////////////////////////////////////
			newCmd = cmd_msg -> cmd;
			baseAddr = call AMPacket.source(msg);
			
			switch(newCmd) {
				case REPORT:
					num_retries = MAX_RETX;
					// send reply message
					call Voltage.read();
//					post sendReplyMessage();
					break;
				case DISS:
					isForMe = FALSE;
					for (i = 0; i < cmd_msg -> nodeCount; i++) {
						if(cmd_msg-> nodeList[i] == call AMPacket.address()) {
							isForMe = TRUE;
							break;
						}
					}
					
					if (isForMe) {
						newChannel = cmd_msg -> newChannel;
			//			baseAddr = call AMPacket.source(msg);
						
						state = DISS_STARTED;
						num_retries = MAX_RETX;
						
			//			call DisseminationControl.start();
			//			call ResetTimer.startOneShot(RESET_INTERVAL);	// start reset timer after the reply is sent	

						// send reply message
						call Voltage.read();
//						post sendReplyMessage();
					}
					break;
				case ABORT:
					isForMe = FALSE;
					for (i = 0; i < cmd_msg -> nodeCount; i++) {
						if(cmd_msg-> nodeList[i] == call AMPacket.address()) {
							isForMe = TRUE;
							break;
						}
					}
					
					if(isForMe){
						// reset
						reset();
					}
					break;
				case REPORT_SUBSET:
					isForMe = FALSE;
					for (i = 0; i < cmd_msg -> nodeCount; i++) {
						if(cmd_msg-> nodeList[i] == call AMPacket.address()) {
							isForMe = TRUE;
							break;
						}
					}
					
					if (isForMe) {
						newChannel = cmd_msg -> newChannel;
						
					//	state = DISS_STARTED;
						num_retries = MAX_RETX;
						
						// send reply message
						call Voltage.read();
//						post sendReplyMessage();
					}
					break;
				case REBOOT:	// reboot the mote
					isForMe = FALSE;
					for (i = 0; i < cmd_msg -> nodeCount; i++) {
						if(cmd_msg-> nodeList[i] == call AMPacket.address()) {
							isForMe = TRUE;
							break;
						}
					}
					
					if (isForMe) {
					#if defined(PLATFORM_IRIS) || defined(PLATFORM_MICAZ)
						wdt_enable(WDTO_30MS);
					#elif defined(PLATFORM_TELOSB)
						WDTCTL = 0;
					#endif
					}
				
				
					break;	
				case SET_ADDRESS:
					if (cmd_msg -> nodeCount != 2) {
						// may be a corrupted packet, ignore
						break;
					} else if (cmd_msg -> nodeList[0] == call AMPacket.address()) {
						// if the command is for me
						call ActiveMessageAddress.setAddress(call ActiveMessageAddress.amGroup(), cmd_msg -> nodeList[1]);
						
					}
					break;

				default:
					break;
			}
			
			/////////////////////////////////////////
			return msg;
		}
	}
	
	event void Voltage.readDone(error_t err, uint16_t reading) {
		if(err != SUCCESS) {
			voltage = 0xFFFF;
		} else {
			voltage = reading;
		}
		post sendReplyMessage();
	}
	
	event void ResetTimer.fired() {
		reset();
	}
	

	event void ReplySend.sendDone(message_t* msg, error_t error) {
		radioBusy = FALSE;
		
		// if DelugeBase does not acked
		if(!call ReplyAck.wasAcked(msg)) {
			// if retransmissions < MAX_RETIRES, resend immediately
			if(--num_retries) {
				call RetxTimer.startOneShot(RETX_INTERVAL);
		//		post sendReplyMessage();
			} else {
			
				if(newCmd == DISS) {
					// get ready to be reprogrammed anyway, but start a shorter reset timer.
					call SystemLowPowerListening.setDefaultRemoteWakeupInterval(0);
					call LowPowerListening.setLocalWakeupInterval(0);
			
				#if defined (PLATFORM_IRIS)
					call ChangeChannel.setChannel(newChannel);			
				#else
					call ChangeChannel.setChannel(newChannel);					
					call ChangeChannel.sync();
				#endif
					call ResetTimer.startOneShot(SHORT_RESET_INTERVAL);		// no reponse, start short reset interval
					
					// start drip message
					call DisseminationControl.start();
			//		call Leds.led2Toggle();
				}			
			}
			
		}else {
			// acked
			switch(newCmd) {
				case REPORT:
					break;
				case DISS:
					// ACK is received by Base, handshaking done!
					// disable LPL, change to new channel
					call SystemLowPowerListening.setDefaultRemoteWakeupInterval(0);
					call LowPowerListening.setLocalWakeupInterval(0);
			
					#if defined (PLATFORM_IRIS)
					call ChangeChannel.setChannel(newChannel);			
				#else
					call ChangeChannel.setChannel(newChannel);					
					call ChangeChannel.sync();
				#endif
					call ResetTimer.startOneShot(RESET_INTERVAL);		// start long reset interval	
				
			//		call Leds.led2Toggle();
					call DisseminationControl.start();
					break;
				default:
					break;
			
			} // switch
			
			
		}// else
	}
	
	event void RetxTimer.fired() {
		post sendReplyMessage();
	}
		
/***************************** change channel events*********************/
#if defined (PLATFORM_IRIS)
	event void ChangeChannel.setChannelDone() {

	}
#else
	event void ChangeChannel.syncDone(error_t error) {

	}
#endif


	async event void ActiveMessageAddress.changed() {
//		call Leds.led1Toggle();
	}
}

