/*
 * Example application to test the use of SimpleDrip
 */
#include "EasyDissemination.h"

configuration EasyDisseminationAppC { }

implementation {
	components EasyDisseminationC as App;
	components MainC, LedsC;
	
	App.Boot -> MainC;
	App.Leds -> LedsC;
	
	components new TimerMilliC() as TimerC;
	App.Timer -> TimerC;
	
	components ActiveMessageC;
	App.RadioControl -> ActiveMessageC;
	
	components new DisseminatorC(diss_msg_t, DISS_KEY);
	App.DisseminationValue -> DisseminatorC;
	App.DisseminationUpdate -> DisseminatorC;
	
	components DisseminationC;
	App.DisseminationControl -> DisseminationC;
	


}
