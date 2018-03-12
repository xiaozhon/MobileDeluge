package MBGateway;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.tinyos.message.MoteIF;
import net.tinyos.packet.BuildSource;
import net.tinyos.util.PrintStreamMessenger;

public class Operations {
	
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  	private String time = df.format(new Date());
	
  	private String imagePath;
  	private FileNameExtensionFilter filter;

  	private String deluge_cmd;
  	
  	private String source;
  	private SerialComm serial;

	private int[] targetList = new int[Constants.MAX_NUM_IDS];
	private short targetCount = 0;
	
	private List<Nodes> recordList = new ArrayList<Nodes>();
	private int recordCount = 0;
	
	boolean newCycle = false;
	private short nodeCmd = 0xFF;
	private short newChannel = 26;
	
	private String log_string;
	
  	public Operations(){
		
	}
	
	/*
	 * Pass the args from Main function to this initialization function
	 * */
	public void init(String[] args) {
		
		
		imagePath = System.getProperty("user.dir");	// set default image path to current folder
		filter = new FileNameExtensionFilter("XML file","xml");
		
		source = args[1];
		serial = new SerialComm(source);
		
		newChannel = (short) Integer.parseInt(args[2]);
		
		// send an initial message to the DelugeBase
		nodeCmd = Constants.INIT_MSG;
		for(int i = 0; i < Constants.MAX_NUM_IDS; i++) {
			targetList[i] = 0xFFFF;
		}
		serial.sendCmd(nodeCmd, targetCount, targetList, newChannel);

		
	}
	
	private static void runDelugeCmd(String newCmd) {
		int line_count = 0;
		try {
		//	phoenix.shutdown(); // shut down the phoenix connection before deluge command
		//	System.out.println("start executing deluge command");  
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(newCmd);
            InputStream input_s = process.getInputStream();  
           
            InputStreamReader is_reader = new InputStreamReader(input_s);  

   			BufferedReader buffer = new BufferedReader(is_reader);  
    		String line = null;  
    		while ((line = buffer.readLine()) != null) {  
    //			line_count++;
        		System.out.println(line);  
    		}  
    		input_s.close();  
    		is_reader.close();  
    		buffer.close();  	
    //		System.out.println("deluge command finished, the number of lines are: " + line_count);  
//    		return 1;
		}catch(Exception ex){
			System.out.println("Deluge Command Execution Error!");
//			return 0;
		}
		
//		printOptions();
	}
	
	/*
	 * Check whether the node is already in the record list
	 * */
	private boolean hasSeen(int id) {
		// TODO Auto-generated method stub
		for(int i = 0; i < recordList.size(); i++) {
			if(id == recordList.get(i).nodeid){
				return true;
			}
		}
		return false;
	}
	
	
	/*
	 * Option 0, start over a new iteration, clear all the flags in the record file
	 * */
	public void ClearRecordFlags() throws IOException{
		// get nodes flag ready for the new iteration
		System.out.println("Preparing records...");
		RecordOperator.clear_flags();
		System.out.println("Records are ready!");
//		printOptions();
		
		time = df.format(new Date());
		Logs.log("\n###########################################################################\n");
		Logs.log("###########################################################################\n");
		Logs.log(time + ":\n");
		Logs.log("\tStart a new iteration!\n");
	}
	
	
	/*
	 * Option 1, inject image. Use return value to control that, the mobile gateway
	 * will show the options after the command is completed
	 * */
	public void DelugeInject() {
	//	phoenix.shutdown();
		int retval = 0;
		serial.stop();
		JFileChooser fc = new JFileChooser(imagePath);	

		fc.setDialogTitle("Select Image");
		fc.setFileFilter(filter);
		
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            //This is where a real application would open the file.
            System.out.println("Opening: " + file.getName() + "\n" );
            imagePath = fc.getSelectedFile().getPath();
            // by default, we let the new image always stored on slot 1
 			deluge_cmd ="tos-deluge " + source + " -i 1 "+ imagePath;
 			System.out.println(deluge_cmd);
 		
 			runDelugeCmd(deluge_cmd);
      //  	System.out.println("retval is: " + retval);

 		//	return retval;
        } else {
        	System.out.println("Image File Not Selected!\n");
        //	return 0;
 //       	printOptions();
        }
	}
	
	
	/*
	 * Option 2, connect target nodes
	 * */
	public void connect(String[] ids_list){
		serial.stop();
		// always send a "-s" command to Deluge Base when trying to connect new targets. 
		// In case of connecting wrong targets whcile deluge base is sending image
		Deluge_s();		
		
		serial.stop();
		
		int i;
		boolean id_error = false;
		for(i = 0; i < targetList.length; i++){
			targetList[i] = 0;
		}
		nodeCmd = Constants.DISS;
		
		if (newCycle) {
			// this is a new cycle, clear the dissList
			recordCount = 0;
			// remove all the elements in the list
			// from the final effect, list.removeAll and list.clear are the same,
			// list.clear is faster
			//recordList.removeAll(recordList);
			recordList.clear();
		
			for(i = 0; i < ids_list.length; i++) {
				try {
					targetList[i] = Integer.decode(ids_list[i]);
				} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
					id_error = true;
					break;
				} 
				recordCount++;
				Nodes node = new Nodes();
				node.nodeid = targetList[i];
				node.to_be_updated = 1;
				// add the node to the list
				recordList.add(node);
			}
			newCycle = false;
		} else {
			int count = recordList.size();
			
			for(i = 0; i < ids_list.length; i++) {
				try {
					targetList[i] = Integer.decode(ids_list[i]);
					
				} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
					id_error = true;
					break;
				//	continue;
				} 
				if(hasSeen(targetList[i])){
					continue;
				}
				recordCount++;
				Nodes node = new Nodes();
				node.nodeid = targetList[i];
				node.to_be_updated = 1;
				// add the node to the list
				recordList.add(node);		
			}
		}
		
/*				for(i = 0; i < ids_list.length; i++) {
			try {
				dissList[0][i] = Integer.decode(ids_list[i]);
				dissList[1][i] = 1;
			//	dissCount++;
		//		System.out.println(ids_list[i]);
			} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
				id_error = true;
				continue;
			} 
		}
*/				
	
		if (id_error) {
			// if there some id is not valid
			System.out.println("\nInvalid Node ID!\n");
	//		printOptions();
	//		continue;	
	//		return 0;
			return;
		}
		
		targetCount = (short)ids_list.length;
	//	nodeCount = (short)ids_list.length;
		

//		phoenix = BuildSource.makePhoenix(source, PrintStreamMessenger.err);

//		mif = new MoteIF(phoenix);
//		serial = new MobileGateway(mif);
//		serial.sendPackets(nodeCmd, (short)targetCount, targetList, newChannel);
		
		serial.sendCmd(nodeCmd, targetCount, targetList, newChannel);
		
		time = df.format(new Date());
		try {
			Logs.log('\n' + time + '\n');
			Logs.log("\tConnecting to nodes: ");
			log_string = '\t' + ids_list[0];
		
			for(i = 1; i < ids_list.length; i ++){
				log_string = log_string + '\t' + ids_list[i];
			}
			Logs.log(log_string + '\n');
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//		return 1;
	}
	
	/*
	 * Option 3: deluge command: -dr
	 * */
	
	public void Deluge_dr() throws IOException {
		serial.stop();
		
		deluge_cmd = "tos-deluge " + source + " -dr 1";
		System.out.println(deluge_cmd);
		runDelugeCmd(deluge_cmd);
		// after the command executed, update the flags in the record
		
		time = df.format(new Date());
		Logs.log('\n' + time + '\n');
		Logs.log("\tDissemination and reprogramming started!\n");
		
		for(int i = 0;  i < recordCount; i++) {
			
			Nodes tmp = recordList.get(i);
			// the second field is the "FLAG"
			if(i == 0){
				log_string = '\t' + Integer.toString(tmp.nodeid);
			} else{
				log_string = log_string + '\t' + Integer.toString(tmp.nodeid);
			}
			
			RecordOperator.update(Integer.toString(tmp.nodeid), tmp.type, Constants.FLAG, "YES");
		}
		newCycle = true;
		
		Logs.log("\tUpdate the flags for nodes: \n");
		Logs.log(log_string + '\n');
		
	//	return retval;
	}
	
	/*
	 * Option 4: Deluge command: -s
	 * */
	
	public void Deluge_s() {
		serial.stop();
		deluge_cmd = "tos-deluge " + source + " -s";
		System.out.println(deluge_cmd);
		runDelugeCmd(deluge_cmd);
		
		time = df.format(new Date());
		try {
			Logs.log('\n' + time + '\n');
			Logs.log("\tStop command issued!\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	//	return 1;
	}
	
	/*
	 * Option 5, abort
	 * */
	
	public void abort(String[] ids_list) throws IOException{
		serial.stop();
		int i;
		
		for(i = 0; i < targetList.length; i++){
			targetList[i] = 0;
		}
		
		nodeCmd = Constants.ABORT;
		if(ids_list[0].equals("a")) {
			// no need to change the node list
			System.out.println("All nodes will be aborted!");
			
			time = df.format(new Date());
			Logs.log('\n' + time + '\n');
			
			
			if(recordList.size() == 0) {
				System.out.println("(No nodes in the memory, is the gateway just started?)");
				Logs.log("Try to abort all nodes, but no nodes are in the memory, maybe the gateway is just started.\n");
			} else {
				Logs.log("\tAbort all the nodes: \n");
				// use recordList to abort all nodes
				
			
				for(i = 0; i < recordList.size(); i++){
					Nodes tmp = recordList.get(i);
					targetList[i] = tmp.nodeid;
				}
				
				targetCount = (short)recordList.size();
				recordList.clear();
				
				log_string = '\t' + Integer.toString(targetList[0]);
				for(i = 1; i < targetCount; i ++){
					log_string = log_string + '\t' + Integer.toString(targetList[i]);
				}
				Logs.log(log_string + '\n');
			}
			recordCount = recordList.size();
	
		} else {
		//	ids_list = id_string.split(" ");
			boolean id_error = false;
			// clear target list
		//	for(i = 0; i < targetList.length; i++){
		//		targetList[i] = 0;
		//	}
			System.out.println("target nodes will be aborted!");
			for(i = 0; i < ids_list.length; i++) {
				try {
					targetList[i] = Integer.decode(ids_list[i]);
					if(recordCount == 0) {	// recordList is empty, do not check the record list
			//			System.out.println("RecordList is empty, continue!");
				//		continue;
					} else {
						for(int j = 0; j < recordCount; j++){		// BUG: if no nodes in recordList, IndexOutOfRange exception
							Nodes tmpNode = recordList.get(j);
							if(targetList[i] == tmpNode.nodeid) {
								recordList.remove(j);
								break;
							}
						//	if(dissList[0][j] == abortList[i]) {
						//		// if the node is aborted, reset the flag of the node in the dissList
						//		dissList[1][j] = 0;
						//	}
						}
					}
					
				} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
					id_error = true;
					continue;
				} 
			}
			if (id_error) {
				// if there some id is not valid
				System.out.println("\nInvalid Node ID!\n");
		//		printOptions();
				return;	
			}
			recordCount = recordList.size();
			targetCount = (short)ids_list.length;
			
			time = df.format(new Date());
			Logs.log('\n' + time + '\n');
			Logs.log("\tAbort the nodes: \n");
			log_string = '\t' + Integer.toString(targetList[0]);
			
			for(i = 1; i < ids_list.length; i ++){
				log_string = log_string + '\t' + Integer.toString(targetList[i]);
			}
			Logs.log(log_string + '\n');
		
		}
		
		serial.sendCmd(nodeCmd, (short)targetCount, targetList, newChannel);
		
	//	return 1;
	}
	
	/*
	 * Option 6, deluge command: -p
	 * */
	public void Deluge_p() {
		serial.stop();
		deluge_cmd = "tos-deluge " + source + " -p 1";
		System.out.println(deluge_cmd);
		runDelugeCmd(deluge_cmd);
		
	}
	
	/*
	 * Option 7, deluge command: erase a image
	 * */
	
	public void Deluge_e() {
		serial.stop();
		deluge_cmd = "tos-deluge " + source + " -e 1";
		System.out.println(deluge_cmd);
		runDelugeCmd(deluge_cmd);
	}
	
	/*
	 * Option 8, detect neighbors
	 * */
	public void detect() throws IOException {
		serial.stop();
		nodeCmd = Constants.REPORT;
		int i;
	//	System.out.println("Detecting neighbors... ");

		targetCount = 0;
		for(i = 0; i < targetList.length; i++) {
			// clear the target list
			targetList[i] = 0xFFFF;
		}
	//	System.out.println("Before send");

		serial.sendCmd(nodeCmd, targetCount, targetList, newChannel);
	//	System.out.println("After send ");

		time = df.format(new Date());
		Logs.log('\n' + time + '\n');
		Logs.log("\tDetecting neighbors...\n");
		
//		return 1;
	}
	
	/*
	 * Option 9, detect a subset of nodes
	 * */
	public void detect_subset(String[] ids_list){
		serial.stop();
		int i;
		boolean id_error = false;
//		int[] subsetList = new int[Constants.MAX_NUM_IDS];
//		short subsetCount = 0;
		targetCount = 0;
		
		for(i = 0; i < targetList.length; i++){
			targetList[i] = 0;
		}
		nodeCmd = Constants.REPORT_SUBSET;
			
		for(i = 0; i < ids_list.length; i++) {
			try {
				targetList[i] = Integer.decode(ids_list[i]);
				
			} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
				id_error = true;
				break;
			//	continue;
			} 	
		}	
	
		if (id_error) {
			// if there some id is not valid
			System.out.println("\nInvalid Node ID!\n");
			return;
		}
		
		targetCount = (short)ids_list.length;
		
		serial.sendCmd(nodeCmd, targetCount, targetList, newChannel);
		
		time = df.format(new Date());
		try {
			Logs.log('\n' + time + '\n');
			Logs.log("\t Detect a subset of targets: ");
			log_string = '\t' + ids_list[0];
		
			for(i = 1; i < ids_list.length; i ++){
				log_string = log_string + '\t' + ids_list[i];
			}
			Logs.log(log_string + '\n');
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	/*
	 * Option 10, reboot a target node. Current we only reboot a single node for robustness
	 * */
	public void reboot(String[] ids_list){
		
		
		serial.stop();
		int i;
		boolean id_error = false;
//		int[] subsetList = new int[Constants.MAX_NUM_IDS];
//		short subsetCount = 0;
		targetCount = 0;
		for(i = 0; i < targetList.length; i++){
			targetList[i] = 0;
		}
		nodeCmd = Constants.REBOOT;
			
		try {
			targetList[0] = Integer.decode(ids_list[0]);
		} catch (NumberFormatException e) {
		// TODO Auto-generated catch block
			id_error = true;
		//	continue;
		} 	
		
		if (id_error) {
			// if there some id is not valid
			System.out.println("\nInvalid Node ID!\n");
			return;
		}
		
		if(ids_list.length > 1){
			// if more than 1 target node, ignore the rest of the targets
			System.out.println("More than 1 target nodes are detected, only the first target will be rebooted!");
		}	
		
		targetCount = 1;
		
		serial.sendCmd(nodeCmd, targetCount, targetList, newChannel);
		
		time = df.format(new Date());
		try {
			Logs.log('\n' + time + '\n');
			Logs.log("\t Reboot a single node: ");
			log_string = '\t' + ids_list[0];
		
			Logs.log(log_string + '\n');
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/*
	 * Option 11, change the id of the target node. There are at least two elements in the ids_list
	 * */
	public void set_id(String[] ids_list){
		
		
		serial.stop();
		int i;
		boolean id_error = false;
//		int[] subsetList = new int[Constants.MAX_NUM_IDS];
//		short subsetCount = 0;
		
		for(i = 0; i < targetList.length; i++){
			targetList[i] = 0;
		}
		nodeCmd = Constants.SET_ADDRESS;
			
		try {
			targetList[0] = Integer.decode(ids_list[0]);
			targetList[1] = Integer.decode(ids_list[1]);
		} catch (NumberFormatException e) {
		// TODO Auto-generated catch block
			id_error = true;
		//	continue;
		} 	
		
		if (id_error) {
			// if there some id is not valid
			System.out.println("\nInvalid Input!\n");
			return;
		}
		
		targetCount = 2;
		
		serial.sendCmd(nodeCmd, targetCount, targetList, newChannel);
		
		time = df.format(new Date());
		try {
			Logs.log('\n' + time + '\n');
			Logs.log("\t Set the node address of " + targetList[0] + " to " + targetList[1] + ".\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/*
	 * Option 111, exit
	 * */
	public void exit() throws IOException {
		serial.stop();
		System.out.println("Exit Mobile Deluge...\n");
		time = df.format(new Date());
		Logs.log('\n' + time + '\n');
		Logs.log("\tExit Mobile Deluge...\n");
		System.exit(1);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String test = "test";
		Operations op = new Operations();
		try {
			op.abort(test.split(" "));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
