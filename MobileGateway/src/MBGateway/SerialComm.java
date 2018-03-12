package MBGateway;

import java.io.IOException;

import net.tinyos.message.*;
import net.tinyos.packet.*;
import net.tinyos.util.PrintStreamMessenger;

/**
 * establish serial communication between the base station mote and the computer 
 */

public class SerialComm implements MessageListener {
	private MoteIF moteIF;
	private PhoenixSource phoenix;
	static String source;
//	private int testflag = 0;
	private String nodeType = "UNKNOWN";

	public SerialComm(String src) {
//		moteIF.registerListener(new SerialReplyMsg(), this);
		this.source = src;
		this.phoenix = BuildSource.makePhoenix(source, PrintStreamMessenger.err);
//		this.moteIF = new MoteIF(phoenix);
//		this.moteIF.registerListener(new SerialReplyMsg(), this);
//		testflag = 19;
	}
	
	public SerialComm(){
		
	}
	
	// send command packets
	public void SubSend(short cmd, short node_count, int[] node_list, short channel) {
		SerialCmdMsg payload = new SerialCmdMsg();
		try {
			try {
				Thread.sleep(200);
			}
			catch (InterruptedException exception) {
			}
			
			/*	static final int ABORT = 0;	// dissemination aborted, return to original status
				static final int DISS = 1;	// start dissemination
				static final int REPORT = 2;
				static final int REPORT_SUBSET = 3;
				static final int REBOOT = 4;
			 * */
			switch(cmd) {
				case Constants.ABORT:
				case Constants.DISS:
					System.out.print("\nSending command to: ");
					for(int j = 0; j < node_count; j++) {
						System.out.print(node_list[j] + " ");
					}
					break;
				case Constants.REPORT:
					System.out.print("\nDetecting neighbors...");
					break;
				case Constants.REPORT_SUBSET:
					System.out.print("\nDetecting targets: ");
					for(int j = 0; j < node_count; j++) {
						System.out.print(node_list[j] + " ");
					}
					break;
				case Constants.REBOOT:
					System.out.print("\nReboot target: " + node_list[0]);
					
					break;
				case Constants.SET_ADDRESS:
					System.out.print("\nSet the address of " + node_list[0] + " to " + node_list[1] + "...");
					System.out.print("\n\tPlease send a DETECT command later to check the new ID");
					
					break;
				default:
					break;
			
			}
			
			System.out.println('\n');
			payload.set_cmd(cmd);
			payload.set_nodeCount(node_count);
			payload.set_nodeList(node_list);
			payload.set_newChannel(channel);
		//	System.out.println("SerialComm before send");

			moteIF.send(0, payload);
		//	System.out.println("SerialComm after send");

//			System.out.println(payload.toString());
	//		System.out.println("cmd sent");
		}
		catch (IOException exception) {
			System.err.println("Exception thrown when sending packets. Exiting.");
			System.err.println(exception);
		}
	}
		
	public void messageReceived(int to, Message message) {
//		System.out.println("msg received");
		SerialReplyMsg msg = (SerialReplyMsg) message;
		float voltage = 0;
		switch(msg.get_nodeType()) {
			case Constants.IRIS_MOTE:
				nodeType = "IRIS";
			//	if(voltage != 0) {
					// if there is no sensorboard attached with the mote,
					// voltage reading will be 0.
					voltage = 1100*1024 / msg.get_nodeVoltage();
			//	}
				break;
			case Constants.MICAZ_MOTE:
				nodeType = "MICAz";
			//	if(voltage != 0) {
					voltage = 1223*1024 / msg.get_nodeVoltage();
			//	}
				break;
			case Constants.TELOSB_MOTE:
				nodeType = "TelosB";
				voltage = (float)msg.get_nodeVoltage() * 5 * 1000 / 4096;
				break;
			case Constants.EPIC_MOTE:
				nodeType = "Epic";
				break;
			case Constants.MULLE_MOTE:
				nodeType = "Mulle";
				break;
			case Constants.TINYNODE_MOTE:
				nodeType = "TinyNode";
				break;
			case Constants.UNKNOWN:
				nodeType = "UNKNOWN";
				break;	
		}
		// update the type field in the record list
/*		if(recordList.size() > 0){
			// if the record list is not empty, find the node in the target list, and set the type field
			int i;
			for(i = 0; i < recordList.size(); i++) {
				if(msg.get_nodeid() == recordList.get(i).nodeid) {
					// found the node
					recordList.get(i).set_type(nodeType);
				}
			}
		}
*/			
		if(msg.get_cmd() == Constants.NODE_READY) {
			System.out.print("Node " + msg.get_nodeid() + " is READY\tType: " + nodeType + "\tVersion: " + msg.get_appVersion());
			System.out.println("\tVoltage (mV): " + voltage);
			try {
				Logs.log("\tNode " + msg.get_nodeid() + " is READY\tType: " + nodeType + "\tVersion: " + msg.get_appVersion() + "\tVoltage (mV): " + voltage + '\n');
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
      	}	
		else if(msg.get_cmd() == Constants.NODE_ABORTED) {
			System.out.println("Node " + msg.get_nodeid() + " is ABORTED!");
			try {
				Logs.log("Node " + msg.get_nodeid() + " is ABORTED!\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		else if(msg.get_cmd() == Constants.NODE_REPORT) {
			System.out.print("Node " + msg.get_nodeid() + "\tType: " + nodeType + "\tVersion: " + msg.get_appVersion());
			System.out.println("\tVoltage (mV): " + voltage);
			try {
				Logs.log("\tNode " + msg.get_nodeid() + "\tType: " + nodeType + "\tVersion: " + msg.get_appVersion() + "\tVoltage (mV): " + voltage + '\n');
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if(msg.get_cmd() == Constants.NODE_REPORT_SUBSET) {
			System.out.print("Node " + msg.get_nodeid() + "\tType: " + nodeType + "\tVersion: " + msg.get_appVersion());
			System.out.println("\tVoltage (mV): " + voltage);
			try {
				Logs.log("\tNode " + msg.get_nodeid() + "\tType: " + nodeType + "\tVersion: " + msg.get_appVersion() + "\tVoltage (mV): " + voltage + '\n');
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(msg.get_cmd() == Constants.NODE_REBOOT) {
			System.out.println("Node " + msg.get_nodeid() + " is REBOOTED!");
			try {
				Logs.log("Node " + msg.get_nodeid() + " is REBOOTED!\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(msg.get_cmd() == Constants.NODE_NO_RESPONSE) {
			System.out.println("Node " + msg.get_nodeid() + " NO RESPONSE!");
			try {
				Logs.log("\tNode " + msg.get_nodeid() + " NO RESPONSE!\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// How to make sure that it is a proper time to shut down the phoenix connection
		
	}
	
	/**
	 * Stop the connection for new serial packets and Deluge commands
	 * Always stop the phoenix before issuing next serial command/Deluge command
	 * */
	public void stop(){
//		System.out.println("testflag is: " + testflag);

		phoenix.shutdown();
	}
	
	/**
	 * establish a serial connection, then send serial packets
	 **/
	public void sendCmd(short cmd, short node_count, int[] node_list, short channel) {
		// build phoenix
		phoenix = BuildSource.makePhoenix(source, PrintStreamMessenger.err);
		moteIF = new MoteIF(phoenix);
		moteIF.registerListener(new SerialReplyMsg(), this);
		
		SubSend(cmd, node_count, node_list, channel);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
//		MoteIF mif;
		SerialComm serial;
		serial = new SerialComm("serial@/dev/ttyUSB1:57600");
		
		int i = 0;
		short cmd = Constants.REPORT;		// 
		short count = 0;
		int[] node_list = new int[Constants.MAX_NUM_IDS];
		short newch = 13;
		
		while(i < 5) {
			serial.sendCmd(cmd, count, node_list, newch);
			
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			serial.phoenix.shutdown();
			i++;
		}
		
		
//		phoenix = BuildSource.makePhoenix(source, PrintStreamMessenger.err);
//		mif = new MoteIF(phoenix);
//		serial = new SerialComm(mif);
		//
//		serial.sendPackets(nodeCmd, (short)targetCount, targetList, newChannel);

	}

}
