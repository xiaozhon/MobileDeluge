/*
 * Example application to test the use of SimpleDrip
 */

#include "EasyDissemination.h"

module EasyDisseminationC{
	uses {
		interface Boot;
		interface SplitControl as RadioControl;
		interface StdControl as DisseminationControl;
		interface DisseminationValue<diss_msg_t>;
		interface DisseminationUpdate<diss_msg_t>;
		interface Timer<TMilli>;
		interface Leds;
	}
}

implementation {

	uint16_t data;
	
	diss_msg_t dMsg;
	
	uint16_t rcvCount;
	event void Boot.booted() {
		data = 0;
		call RadioControl.start();
	}
	
	event void RadioControl.startDone(error_t err) {
		if(err != SUCCESS) {
			call RadioControl.start();
			return;
		} else {
			call DisseminationControl.start();
			if(TOS_NODE_ID == 0) {
			//	call Leds.led0Toggle();
				call Timer.startPeriodic(5000);
			}
		}
	}
	
	event void RadioControl.stopDone(error_t err) { }

	event void Timer.fired() {
		call Leds.led1Toggle();
		data = data + 1;
		dMsg.data = data;
		if(TOS_NODE_ID == 0) {
			dbg("EasyDiss", "EasyDiss Timer: call Update.change(), data is: %hu \n", data);
			call DisseminationUpdate.change(&dMsg);
			
		}
	}
	
	event void DisseminationValue.changed() {
		const diss_msg_t *newVal = call DisseminationValue.get();
		
	//	call Leds.led0Toggle();
	//	call Leds.led1Toggle();
		rcvCount++;
		
		dbg("EasyDiss", "EasyDiss Value: received packets, new data is: %hu, rcvCount: %hu \n", newVal -> data, rcvCount);
		
/*		if (rcvCount & 0x1) {
			call Leds.led0On();
     	}
      	else {
			call Leds.led0Off();
      	}
      	if (rcvCount & 0x2) {
			call Leds.led1On();
    	}
      	else {
			call Leds.led1Off();
      	}
      	if (rcvCount & 0x4) {
			call Leds.led2On();
      	}
      	else {
			call Leds.led2Off();
      	}
*/   
        if (newVal -> data & 0x1) {
			call Leds.led0On();
     	}
      	else {
			call Leds.led0Off();
      	}
      	if (newVal -> data & 0x2) {
			call Leds.led1On();
    	}
      	else {
			call Leds.led1Off();
      	}
      	if (newVal -> data & 0x4) {
			call Leds.led2On();
      	}
      	else {
			call Leds.led2Off();
      	}
      	
	}

}
