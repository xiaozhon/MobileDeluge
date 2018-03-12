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

configuration ReprogrammerNodeC {}
implementation {
	components ReprogrammerNodeP as App;
	components MainC;
	components LedsC;
	components DelugeC, DelugeP;
	DelugeC.Leds -> LedsC;
	App.stop -> DelugeP;
	App.Boot -> MainC;
	App.Leds -> LedsC;

	components DisseminationC;
	App.DisseminationControl -> DisseminationC;
	
	// change channel
#if defined(PLATFORM_IRIS)
	App.ChangeChannel -> ActiveMessageC.RadioChannel;
#else
	components CC2420ControlC;
	App.ChangeChannel -> CC2420ControlC;
#endif

	components ActiveMessageC;
	// Enable/Disable LPL
	App.LowPowerListening -> ActiveMessageC;
	components SystemLowPowerListeningC;
  	App.SystemLowPowerListening -> SystemLowPowerListeningC;
	
	// timers
	components new TimerMilliC() as InitDelayTimerC;
	components new TimerMilliC() as ResetTimerC;
	App.InitDelayTimer -> InitDelayTimerC;
	App.ResetTimer -> ResetTimerC;	// long timer, when fires, reset
	
	components new TimerMilliC() as RetxTimerC;
	App.RetxTimer-> RetxTimerC;
	
	components new AMReceiverC(AM_CMD_MSG) as RadioReceiverC;
	App.RadioReceive -> RadioReceiverC;
	
	components new AMSenderC(AM_NODE_REPLY) as ReplySenderC;
	App.ReplySend -> ReplySenderC;
	App.ReplyPacket -> ReplySenderC;
	App.ReplyAck -> ActiveMessageC;
	
	App.AMPacket -> ActiveMessageC;
	
#if defined(PLATFORM_TELOSB)
	components new InternalVoltageC();	// modified voltage component for telosb motes.
	App.Voltage -> InternalVoltageC;
#else
	components new VoltageC();
	App.Voltage -> VoltageC;
#endif

	// change node id
	components ActiveMessageAddressC;
	App.ActiveMessageAddress -> ActiveMessageAddressC;
//	components new VoltageC();
//	App.Voltage -> VoltageC;
	
}
