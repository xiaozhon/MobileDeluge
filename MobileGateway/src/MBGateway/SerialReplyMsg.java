package MBGateway;/**
 * This class is automatically generated by mig. DO NOT EDIT THIS FILE.
 * This class implements a Java interface to the 'SerialReplyMsg'
 * message type.
 */

public class SerialReplyMsg extends net.tinyos.message.Message {

    /** The default size of this message type in bytes. */
    public static final int DEFAULT_MESSAGE_SIZE = 8;

    /** The Active Message type associated with this message. */
    public static final int AM_TYPE = 137;

    /** Create a new SerialReplyMsg of size 8. */
    public SerialReplyMsg() {
        super(DEFAULT_MESSAGE_SIZE);
        amTypeSet(AM_TYPE);
    }

    /** Create a new SerialReplyMsg of the given data_length. */
    public SerialReplyMsg(int data_length) {
        super(data_length);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new SerialReplyMsg with the given data_length
     * and base offset.
     */
    public SerialReplyMsg(int data_length, int base_offset) {
        super(data_length, base_offset);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new SerialReplyMsg using the given byte array
     * as backing store.
     */
    public SerialReplyMsg(byte[] data) {
        super(data);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new SerialReplyMsg using the given byte array
     * as backing store, with the given base offset.
     */
    public SerialReplyMsg(byte[] data, int base_offset) {
        super(data, base_offset);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new SerialReplyMsg using the given byte array
     * as backing store, with the given base offset and data length.
     */
    public SerialReplyMsg(byte[] data, int base_offset, int data_length) {
        super(data, base_offset, data_length);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new SerialReplyMsg embedded in the given message
     * at the given base offset.
     */
    public SerialReplyMsg(net.tinyos.message.Message msg, int base_offset) {
        super(msg, base_offset, DEFAULT_MESSAGE_SIZE);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new SerialReplyMsg embedded in the given message
     * at the given base offset and length.
     */
    public SerialReplyMsg(net.tinyos.message.Message msg, int base_offset, int data_length) {
        super(msg, base_offset, data_length);
        amTypeSet(AM_TYPE);
    }

    /**
    /* Return a String representation of this message. Includes the
     * message type name and the non-indexed field values.
     */
    public String toString() {
      String s = "Message <SerialReplyMsg> \n";
      try {
        s += "  [cmd=0x"+Long.toHexString(get_cmd())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [nodeid=0x"+Long.toHexString(get_nodeid())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [nodeType=0x"+Long.toHexString(get_nodeType())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [appVersion=0x"+Long.toHexString(get_appVersion())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [nodeVoltage=0x"+Long.toHexString(get_nodeVoltage())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [replyCount=0x"+Long.toHexString(get_replyCount())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      return s;
    }

    // Message-type-specific access methods appear below.

    /////////////////////////////////////////////////////////
    // Accessor methods for field: cmd
    //   Field type: short, unsigned
    //   Offset (bits): 0
    //   Size (bits): 8
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'cmd' is signed (false).
     */
    public static boolean isSigned_cmd() {
        return false;
    }

    /**
     * Return whether the field 'cmd' is an array (false).
     */
    public static boolean isArray_cmd() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'cmd'
     */
    public static int offset_cmd() {
        return (0 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'cmd'
     */
    public static int offsetBits_cmd() {
        return 0;
    }

    /**
     * Return the value (as a short) of the field 'cmd'
     */
    public short get_cmd() {
        return (short)getUIntBEElement(offsetBits_cmd(), 8);
    }

    /**
     * Set the value of the field 'cmd'
     */
    public void set_cmd(short value) {
        setUIntBEElement(offsetBits_cmd(), 8, value);
    }

    /**
     * Return the size, in bytes, of the field 'cmd'
     */
    public static int size_cmd() {
        return (8 / 8);
    }

    /**
     * Return the size, in bits, of the field 'cmd'
     */
    public static int sizeBits_cmd() {
        return 8;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: nodeid
    //   Field type: int, unsigned
    //   Offset (bits): 8
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'nodeid' is signed (false).
     */
    public static boolean isSigned_nodeid() {
        return false;
    }

    /**
     * Return whether the field 'nodeid' is an array (false).
     */
    public static boolean isArray_nodeid() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'nodeid'
     */
    public static int offset_nodeid() {
        return (8 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'nodeid'
     */
    public static int offsetBits_nodeid() {
        return 8;
    }

    /**
     * Return the value (as a int) of the field 'nodeid'
     */
    public int get_nodeid() {
        return (int)getUIntBEElement(offsetBits_nodeid(), 16);
    }

    /**
     * Set the value of the field 'nodeid'
     */
    public void set_nodeid(int value) {
        setUIntBEElement(offsetBits_nodeid(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'nodeid'
     */
    public static int size_nodeid() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'nodeid'
     */
    public static int sizeBits_nodeid() {
        return 16;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: nodeType
    //   Field type: short, unsigned
    //   Offset (bits): 24
    //   Size (bits): 8
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'nodeType' is signed (false).
     */
    public static boolean isSigned_nodeType() {
        return false;
    }

    /**
     * Return whether the field 'nodeType' is an array (false).
     */
    public static boolean isArray_nodeType() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'nodeType'
     */
    public static int offset_nodeType() {
        return (24 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'nodeType'
     */
    public static int offsetBits_nodeType() {
        return 24;
    }

    /**
     * Return the value (as a short) of the field 'nodeType'
     */
    public short get_nodeType() {
        return (short)getUIntBEElement(offsetBits_nodeType(), 8);
    }

    /**
     * Set the value of the field 'nodeType'
     */
    public void set_nodeType(short value) {
        setUIntBEElement(offsetBits_nodeType(), 8, value);
    }

    /**
     * Return the size, in bytes, of the field 'nodeType'
     */
    public static int size_nodeType() {
        return (8 / 8);
    }

    /**
     * Return the size, in bits, of the field 'nodeType'
     */
    public static int sizeBits_nodeType() {
        return 8;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: appVersion
    //   Field type: short, unsigned
    //   Offset (bits): 32
    //   Size (bits): 8
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'appVersion' is signed (false).
     */
    public static boolean isSigned_appVersion() {
        return false;
    }

    /**
     * Return whether the field 'appVersion' is an array (false).
     */
    public static boolean isArray_appVersion() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'appVersion'
     */
    public static int offset_appVersion() {
        return (32 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'appVersion'
     */
    public static int offsetBits_appVersion() {
        return 32;
    }

    /**
     * Return the value (as a short) of the field 'appVersion'
     */
    public short get_appVersion() {
        return (short)getUIntBEElement(offsetBits_appVersion(), 8);
    }

    /**
     * Set the value of the field 'appVersion'
     */
    public void set_appVersion(short value) {
        setUIntBEElement(offsetBits_appVersion(), 8, value);
    }

    /**
     * Return the size, in bytes, of the field 'appVersion'
     */
    public static int size_appVersion() {
        return (8 / 8);
    }

    /**
     * Return the size, in bits, of the field 'appVersion'
     */
    public static int sizeBits_appVersion() {
        return 8;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: nodeVoltage
    //   Field type: int, unsigned
    //   Offset (bits): 40
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'nodeVoltage' is signed (false).
     */
    public static boolean isSigned_nodeVoltage() {
        return false;
    }

    /**
     * Return whether the field 'nodeVoltage' is an array (false).
     */
    public static boolean isArray_nodeVoltage() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'nodeVoltage'
     */
    public static int offset_nodeVoltage() {
        return (40 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'nodeVoltage'
     */
    public static int offsetBits_nodeVoltage() {
        return 40;
    }

    /**
     * Return the value (as a int) of the field 'nodeVoltage'
     */
    public int get_nodeVoltage() {
        return (int)getUIntBEElement(offsetBits_nodeVoltage(), 16);
    }

    /**
     * Set the value of the field 'nodeVoltage'
     */
    public void set_nodeVoltage(int value) {
        setUIntBEElement(offsetBits_nodeVoltage(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'nodeVoltage'
     */
    public static int size_nodeVoltage() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'nodeVoltage'
     */
    public static int sizeBits_nodeVoltage() {
        return 16;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: replyCount
    //   Field type: short, unsigned
    //   Offset (bits): 56
    //   Size (bits): 8
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'replyCount' is signed (false).
     */
    public static boolean isSigned_replyCount() {
        return false;
    }

    /**
     * Return whether the field 'replyCount' is an array (false).
     */
    public static boolean isArray_replyCount() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'replyCount'
     */
    public static int offset_replyCount() {
        return (56 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'replyCount'
     */
    public static int offsetBits_replyCount() {
        return 56;
    }

    /**
     * Return the value (as a short) of the field 'replyCount'
     */
    public short get_replyCount() {
        return (short)getUIntBEElement(offsetBits_replyCount(), 8);
    }

    /**
     * Set the value of the field 'replyCount'
     */
    public void set_replyCount(short value) {
        setUIntBEElement(offsetBits_replyCount(), 8, value);
    }

    /**
     * Return the size, in bytes, of the field 'replyCount'
     */
    public static int size_replyCount() {
        return (8 / 8);
    }

    /**
     * Return the size, in bits, of the field 'replyCount'
     */
    public static int sizeBits_replyCount() {
        return 8;
    }

}