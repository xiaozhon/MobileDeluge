package MBGateway;

import java.util.ArrayList;
import java.util.List;


public class TestCases {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	/*	List<Nodes> nodeList = new ArrayList<Nodes>();
		
		Nodes tmp = new Nodes();
		tmp.nodeid = 101;
		tmp.to_be_updated = 0;
		tmp.type = "MICAZ";
		tmp.version = 5;
		nodeList.add(tmp);
		
		tmp = new Nodes();
		tmp.nodeid = 102;
		tmp.to_be_updated = 0;
		tmp.type = "MICAZ";
		tmp.version = 5;
		nodeList.add(tmp);
		
		tmp = new Nodes();
		tmp.nodeid = 103;
		tmp.to_be_updated = 0;
		tmp.type = "TELOSB";
		tmp.version = 5;
		nodeList.add(tmp);
		
		for(int i = 0; i < nodeList.size(); i++){
			System.out.println(nodeList.get(i).nodeid );
			System.out.println(nodeList.get(i).to_be_updated );
			System.out.println(nodeList.get(i).type );
			System.out.println(nodeList.get(i).version );
			
//			System.out.println("node " + i + "\t" + nodeList.get(i).nodeid + '\t' + nodeList.get(i).to_be_updated + '\t' + nodeList.get(i).type + '\t' + nodeList.get(i).version);
		}
		
		nodeList.get(0).set_type("IRIS");
		System.out.println("** " + nodeList.get(0).nodeid + '\t' + nodeList.get(0).to_be_updated + '\t' + nodeList.get(0).type + '\t' + nodeList.get(0).version);
	*/
		
		/*
		 * Test for SerialComm
		 * */
		
		SerialComm serial = new SerialComm("serial@/dev/ttyUSB1:57600");
		short cmd = Constants.REPORT;		// 
		short count = 0;
		int[] node_list = new int[Constants.MAX_NUM_IDS];
		short newch = 13;
		
		serial.sendCmd(cmd, count, node_list, newch);
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		serial.stop();
		
//		SerialComm.stop();
	}
	
	

}
