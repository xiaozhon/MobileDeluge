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
**/

configuration ReprogrammerBaseC {}
implementation {
	components ReprogrammerBaseP as App;
	components LedsC;
	components MainC;
	components DelugeC;	
	App.Boot -> MainC;
	App.Leds -> LedsC;
	DelugeC.Leds -> LedsC;
	
	// Change channels, IRIS mote and micaz mote
	components ActiveMessageC;
#if defined(PLATFORM_IRIS)
	App.ChangeChannel -> ActiveMessageC.RadioChannel;
#else
	components CC2420ControlC;
	App.ChangeChannel -> CC2420ControlC;
#endif

	// Enable/disable LPL
	components SystemLowPowerListeningC;
  	App.SystemLowPowerListening -> SystemLowPowerListeningC;
	
	// serial communications
	// receive gateway commands
	components SerialActiveMessageC as SerialAM;
	App.SerialReceive -> SerialAM.Receive[AM_SERIAL_CMD_MSG];
	App.SerialPacket -> SerialAM.Packet;
	
	// send reply to gateway
	App.SerialSend -> SerialAM.AMSend[AM_SERIAL_REPLY_MSG];
	
	
	
	// radio communication
	// send command to nodes
	components new AMSenderC(AM_CMD_MSG) as RadioSenderC;
	App.RadioSend -> RadioSenderC;
	App.RadioPacket -> RadioSenderC;
	
	// Receive node reply
	components new AMReceiverC(AM_NODE_REPLY) as ReplyReceiverC;
	App.ReplyReceive -> ReplyReceiverC;
	
	// timers
	components new TimerMilliC() as RetxTimerC;
	components new TimerMilliC() as ReplyTimerC;

	App.RetxTimer -> RetxTimerC;	 
	App.ReplyTimer -> ReplyTimerC;	// wait for reply
	// layer 2 auto ack
//	App.PacketAcknowledgements -> ActiveMessageC;
	
	components RandomC;
	App.Random -> RandomC;
	App.SeedInit -> RandomC;
	
	components LocalTimeMilliC;
  	App.LocalTime -> LocalTimeMilliC;
  	
//  	components DisseminationC;
//	App.DisseminationControl -> DisseminationC;
  	
}
