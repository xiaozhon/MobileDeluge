COMPONENT=DelugeBaseAppC
BOOTLOADER=tosboot
CFLAGS += -DDELUGE_BASESTATION

# This value should be equal or smaller than the one in the App
#CFLAGS += -DTOSH_DATA_LENGTH=57	## if the running application is of version 50
	
#CFLAGS += -DTOSH_DATA_LENGTH=69		### if the running application is of version 38~44

# Default LPL
CFLAGS += -DLOW_POWER_LISTENING
CFLAGS += -DLPL_DEF_LOCAL_WAKEUP=0		# DO NOT change this value!!
# Base sends longer than motes, motes sends for 1 second
CFLAGS += -DLPL_DEF_REMOTE_WAKEUP=1024
CFLAGS += -DDELAY_AFTER_RECEIVE=20


CFLAGS += -DCC2420_DEF_CHANNEL=26
CFLAGS += -DRF230_DEF_CHANNEL=26

#CFLAGS += -DCC2420_DEF_RFPOWER=11

CFLAGS += -I../
CFLAGS += -I../../Deluge
#CFLAGS += -I../../SimpleDrip
#CFLAGS += -I$(TOSDIR)/lib/rfxlink/util



# Timeout for acks in IRIS motes (microseconds)
CFLAGS += -DSOFTWAREACK_TIMEOUT=2000

# Timeout for MICAZ nodes (based on the number of CCA checks)
CFLAGS += -DMAX_LPL_CCA_CHECKS=1500

# Define hardware ACKs for MICAZ nodes
CFLAGS += -DCC2420_HW_ACKNOWLEDGEMENTS



BUILD_EXTRA_DEPS += SerialCmdMsg.class SerialReplyMsg.class #SerialComm.class
CLEAN_EXTRA = *.class SerialCmdMsg.java SerialReplyMsg.java

#SerialComm.class: $(wildcard *.java) SerialCmdMsg.java
#	javac -target 1.4 -source 1.4 *.java

SerialCmdMsg.class: $(wildcard *.java) SerialCmdMsg.java
	javac -target 1.4 -source 1.4 *.java

SerialCmdMsg.java:
	mig java -target=null $(CFLAGS) -java-classname=SerialCmdMsg ../Reprogrammer.h serial_cmd_msg -o $@

SerialReplyMsg.class: $(wildcard *.java) SerialReplyMsg.java
	javac -target 1.4 -source 1.4 *.java

SerialReplyMsg.java:
	mig java -target=null $(CFLAGS) -java-classname=SerialReplyMsg ../Reprogrammer.h serial_reply_msg -o $@

include $(MAKERULES)
