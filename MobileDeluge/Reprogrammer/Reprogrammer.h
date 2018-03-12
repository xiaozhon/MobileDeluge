#ifndef REPROGRAMMER_H
#define REPROGRAMMER_H

// definition of commands
enum {
	// PC to base, base to mote
	ABORT = 0,		// dissemination aborted, return to original status
	DISS = 1,		// start dissemination
	REPORT = 2,
	REPORT_SUBSET = 3,
	REBOOT = 4,
	SET_ADDRESS = 5,
	
	
	// base to PC
	NODE_READY = 51,	// mote is ready
	NODE_ABORTED = 52,	// mote returned to original status
	NODE_REPORT = 53,
	NODE_REPORT_SUBSET = 54,
	NODE_REBOOT = 55,
	NODE_SET_ADDRESS = 56,
	NODE_NO_RESPONSE = 0xFF,
	

};

// definition of msgs
enum {
  	AM_CMD_MSG = 0x99,
  	AM_NODE_REPLY = 0x98,
	AM_SERIAL_CMD_MSG = 0x88,
	AM_SERIAL_REPLY_MSG = 0x89,
};

enum {
	RESET_INTERVAL = 300000,	// 5 minutes to reset
	SHORT_RESET_INTERVAL = 180000,		// 3 minute short reset interval
	
	REPLY_WAIT_INTERVAL = 10000,
	RETX_INTERVAL = 250,		// retransmission interval
	MAX_RETX = 3,				// max number of retransmissions
	
	MAX_NUM_IDS = 15,
};


// definition of platforms
enum {
	IRIS_MOTE = 0xA1,
	MICAZ_MOTE = 0xA2,
	TELOSB_MOTE = 0xA3,		// currently only support micaz, iris, telosb.
	EPIC_MOTE = 0xA4,
	MULLE_MOTE = 0xA5,
	TINYNODE_MOTE = 0xA6,
	UNKNOWN = 0xFF,
	// TODO: add other platforms definition
};

// radio packet
typedef nx_struct cmd_msg {
	nx_uint8_t randCode;
	nx_uint8_t cmd;
  	nx_uint8_t  nodeCount;	// number of target ids
	nx_uint16_t nodeList[MAX_NUM_IDS];	// target id list
	nx_uint8_t newChannel;
} cmd_msg_t;


// serial command from gateway
typedef nx_struct serial_cmd_msg {
  	nx_uint8_t cmd;
  	nx_uint8_t  nodeCount;	// number of target ids
	nx_uint16_t nodeList[MAX_NUM_IDS];	// target id list
	nx_uint8_t newChannel;
} serial_cmd_msg_t;


// serial command from gateway
typedef nx_struct serial_reply_msg {
  	nx_uint8_t cmd;
  	nx_uint16_t  nodeid;	
	nx_uint8_t nodeType;
	nx_uint8_t appVersion;
	nx_uint16_t nodeVoltage;
	nx_uint8_t replyCount;
} serial_reply_msg_t;


typedef nx_struct node_reply_msg {
	nx_uint8_t cmd;
	nx_uint16_t nodeid;
	nx_uint8_t  nodeType;
	nx_uint8_t appVersion;
	nx_uint16_t voltage;
	// TODO: other information of interest
} node_reply_msg_t;


#if defined(PLATFORM_TELOSB)
	#define MOTE_TYPE TELOSB_MOTE
#elif defined(PLATFORM_EPIC)
  	#define MOTE_TYPE EPIC_MOTE
#elif defined(PLATFORM_MICAZ)
	#define MOTE_TYPE MICAZ_MOTE
#elif defined(PLATFORM_IRIS)
	#define MOTE_TYPE IRIS_MOTE
#elif defined(PLATFORM_MULLE)
	#define MOTE_TYPE MULLE_MOTE
#elif defined(PLATFORM_TINYNODE)
	#define MOTE_TYPE TINYNODE_MOTE
#else
	#define MOTE_TYPE UNKNOWN
#endif

#ifndef APP_VERSION
#define APP_VERSION 0
#endif

// header file endif
#endif
