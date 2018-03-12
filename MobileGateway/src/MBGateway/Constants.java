package MBGateway;

public class Constants {
	// all the final variables must match those in Reprogrammer.h
	static final int ABORT = 0;	// dissemination aborted, return to original status
	static final int DISS = 1;	// start dissemination
	static final int REPORT = 2;
	static final int REPORT_SUBSET = 3;
	static final int REBOOT = 4;
	static final int SET_ADDRESS = 5;
	
	static final int INIT_MSG = 255;

	
	// base to PC
	static final int NODE_READY = 51;	// mote is ready
	static final int NODE_ABORTED = 52;// mote returned to original status
	static final int NODE_REPORT = 53;
	static final int NODE_REPORT_SUBSET = 54;
	static final int NODE_REBOOT = 55;
	static final int NODE_SET_ADDRESS = 56;
	static final int NODE_NO_RESPONSE = 0xFF;
	
	// max number of target node ids
	static final int MAX_NUM_IDS = 15;
	// definition of platforms

	static final int IRIS_MOTE = 0xA1;
	static final int MICAZ_MOTE = 0xA2;
	static final int TELOSB_MOTE = 0xA3;
	static final int EPIC_MOTE = 0xA4;
	static final int MULLE_MOTE = 0xA5;
	static final int TINYNODE_MOTE = 0xA6;
	static final int UNKNOWN = 0xFF;	
	
	public static final int ID = 0;
	public static final int TYPE = 1;
	public static final int FLAG = 2;
	public static final int TIME = 3;
}
