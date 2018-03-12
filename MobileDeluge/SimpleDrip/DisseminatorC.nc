/**
 * The DisseminatorC component, provides interfaces DisseminationValue and DisseminationUpdate
 *
 * @author Xiaoyang Zhong <xiaozhon@iupui.edu>
 * @version $Revision: 1.0 $ $Date: 2014-02-28 22:56:21 $
 */
 
 #include "SimpleDrip.h"
 
generic configuration DisseminatorC(typedef t, uint16_t key) {
	provides interface DisseminationValue<t>;
	provides interface DisseminationUpdate<t>;
}

implementation {
	components new DisseminationImplP(t, key) as SimpleDripP;
	components DripStateP;
	
	DisseminationValue = SimpleDripP;
	DisseminationUpdate = SimpleDripP;
	
	SimpleDripP.DripState -> DripStateP;
	
	components new AMSenderC(AM_DISSEMINATION_MESSAGE) as DisseminationSendC;
	SimpleDripP.AMSend -> DisseminationSendC.AMSend;

	components new AMReceiverC(AM_DISSEMINATION_MESSAGE) as DisseminationReceiveC;
	SimpleDripP.Receive -> DisseminationReceiveC.Receive;
	
	components new TimerMilliC() as sendTimerC;
	SimpleDripP.sendTimer -> sendTimerC;

}

