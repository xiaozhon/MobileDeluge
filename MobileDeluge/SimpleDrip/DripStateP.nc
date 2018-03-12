/**
 * Implementation of drip state.
 *
 * @author Xiaoyang Zhong <xiaozhon@iupui.edu>
 * @version $Revision: 1.0 $ $Date: 2014-02-28 22:56:21 $
 */

#include "SimpleDrip.h"
#include "Timer.h"

module DripStateP {
	provides interface StdControl;
	provides interface DripState;
} 

implementation {

	enum {
		RUNNING = 1,
		STOPPED = 2,
	};
	uint16_t state;
	
	// Upon start, set the state to RUNNING
	command error_t StdControl.start() {	
		state = RUNNING;
		dbg("DripStateP", "DripStateP: Dissemination started! \n");
		return SUCCESS;
	}

	command error_t StdControl.stop() {
		state = STOPPED;
		dbg("DripStateP", "DripStateP: Dissemination stopped! \n");
		return SUCCESS;
	}
	
	// Return the state of SimpleDrip
	command bool DripState.isRunning() {
		if (state == RUNNING) {
			return TRUE;
		} else {
			return FALSE;
		}
	}
	// set SimpleState, currently do nothing
	command void DripState.set(uint16_t newState) {
	}
}
