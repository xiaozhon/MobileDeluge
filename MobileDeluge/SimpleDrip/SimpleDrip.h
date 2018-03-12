#ifndef SIMPLE_DRIP_H
#define SIMPLE_DRIP_H

enum {
	AM_DISSEMINATION_MESSAGE = 0x60,
	AM_DISSEMINATION_PROBE_MESSAGE = 0x61,
	
	DISSEMINATION_SEQNO_UNKNOWN = 0,

//	REPEAT_RATE = 5000,

	MIN_RATE = 2000,
	MAX_RATE = 60000,
};

typedef nx_struct dissemination_message {
	nx_uint16_t key;
	nx_uint32_t seqno;
	nx_uint8_t (COUNT(0) data)[0]; // Deputy place-holder, field will probably be removed when we Deputize Drip
} dissemination_message_t;


#endif
