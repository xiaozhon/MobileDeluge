/**
 * Implementation of Simple Drip.
 *
 * @author Xiaoyang Zhong <xiaozhon@iupui.edu>
 * @version $Revision: 1.0 $ $Date: 2014-02-28 22:56:21 $
 */

#include "SimpleDrip.h"
#include "Timer.h"
generic module DisseminationImplP(typedef t, uint16_t key) {
	provides {
		interface DisseminationValue<t>;
		interface DisseminationUpdate<t>;
	}
	
	uses {
		interface AMSend;
		interface Receive;
		interface Timer<TMilli> as sendTimer;
		
		interface DripState;
	}

}

implementation {

	t valueCache;
	uint8_t timerIncreaser;
	
	uint16_t seqno;
	
	uint16_t oldSeqno;
	message_t msg_buf;
	
	void sendObject(uint16_t newkey);
	
	/***************** DisseminationValue Commands ************************/
	
	command const t* DisseminationValue.get() {
		dbg("DissImp", "DissImp Value: command Value.get() \n");
		return &valueCache;
		
	}
	
	// before the first disseminating, the node/base can set the value.
	command void DisseminationValue.set( const t* val ) {
		if (seqno == DISSEMINATION_SEQNO_UNKNOWN) {
			valueCache = *val;
		}
	}
	
	// Receive new drip message, if it is not a duplicate (check seqno), 
	// signal DisseminationValue.changed();
	// 
	event message_t* Receive.receive(message_t* msg, void* payload, uint8_t len) {
		dissemination_message_t* dMsg = (dissemination_message_t*) payload;
		uint8_t objectSize = len - sizeof(dissemination_message_t);
		
		if (!call DripState.isRunning()) {
			dbg("DissImp", "DissImp Receive: received packets, but diss stopped, return \n");
			return msg;
		}
		
		// do nothing if the msg has already been received
		// so that the node will not signal ...changed() multiple times for the same data
		if(oldSeqno != dMsg -> seqno) {
			memcpy( &valueCache, dMsg -> data, objectSize < sizeof(t) ? objectSize : sizeof(t) );
		
			dbg("DissImp", "DissImp Receive: received packets, signal Value.changed() \n");
			signal DisseminationValue.changed();
			
			oldSeqno = dMsg -> seqno;
			dbg("DissImp", "DissImp Receive: received seqno: %hu \n", oldSeqno);
		}
		
		return msg;
	
	}
	
	/******************* DisseminationUpdate command ***********************/
	
	// Once the command is called, increment seqno, send new drip msg immediately.
	// Start the OneShot timer at send.sendDone event. The repeated sending ensure the delivery 
	// of the new msg.
	// Stop the timer every time a new msg is coming from command caller (higer layer)
	command void DisseminationUpdate.change( t* newVal ) {
		if(!call DripState.isRunning()) {
			dbg("DissImp", "DissImp Update: diss stopped, return \n");
			return;
		}
		// copy the data
		memcpy(&valueCache, newVal, sizeof(t));
		// increment the sequency number
		seqno = seqno + 1;	
		
		dbg("DissImp", "DissImp Update: increment seqno to: %hu \n", seqno);
		
		// stop the timer
//		if( call sendTimer.isRunning()) {
//			call sendTimer.stop();
//		}
	
		timerIncreaser = 1;
		if( call sendTimer.isRunning()) {
			call sendTimer.stop();
		}
		
		// send new object immediately
		sendObject(key);
//		call sendTimer.startPeriodic(REPEAT_RATE);
	}
	
	/***************************** Timer event ***************************/
	event void sendTimer.fired() {
		dbg("DissImp", "DissImp sendTimer: fired \n");
		sendObject( key );
	}
  
  
  	// send function
	void sendObject( uint16_t newkey ) {
		uint8_t objectSize = 0;
		dissemination_message_t* dMsg;

		if (!call DripState.isRunning()) {
			dbg("DissImp", "DissImp sendObject: diss stopped, return \n");
			return;
		}
		
		// build the packet
		dMsg = (dissemination_message_t*) call AMSend.getPayload( &msg_buf, sizeof(dissemination_message_t) );
		
		
		if (dMsg != NULL) {
			objectSize = sizeof(t);
			dMsg -> key = newkey;
			
			dMsg -> seqno = seqno;
  
  			dbg("DissImp", "DissImp sendObject: key is: %hu, dMsg -> seqno is: %hu \n", newkey, seqno);
			if ( dMsg->seqno != DISSEMINATION_SEQNO_UNKNOWN ) {
				if ((objectSize + sizeof(dissemination_message_t)) > call AMSend.maxPayloadLength()) {
					objectSize = call AMSend.maxPayloadLength() - sizeof(dissemination_message_t);
				}
				// copy data
				memcpy( dMsg->data, &valueCache, objectSize );
			}      
		// if dMsg == NULL, send a blank message
		call AMSend.send( AM_BROADCAST_ADDR, &msg_buf, sizeof( dissemination_message_t ) + objectSize );
		dbg("DissImp", "DissImp sendObject: packet sent \n");
		}
	}
	
	// At sendDone, start repeat timer
	event void AMSend.sendDone( message_t* msg, error_t error ) {
		dbg("DissImp", "DissImp sendDone: sendDone, start one shot timer! \n");
//		call sendTimer.startOneShot(REPEAT_RATE);
		
		if(timerIncreaser * MIN_RATE >= MAX_RATE) {
			call sendTimer. startOneShot(MAX_RATE);
			dbg("DissImp", "DissImp timer rate: timerIncreaser is: %d, interval is: %d\n", timerIncreaser, MAX_RATE);
		} else {
			call sendTimer. startOneShot(timerIncreaser * MIN_RATE);
			timerIncreaser++;
			dbg("DissImp", "DissImp timer rate: timerIncreaser is: %d, interval is: %d\n", timerIncreaser, timerIncreaser*MIN_RATE);
		}
	}

	default event void DisseminationValue.changed() { }
  
}

