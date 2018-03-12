package MBGateway;
/**
 * MobileGateway for MobileDeluge.
 * 
 *
 * @author XiaoyangZhong <xiaozhon@iupui.edu>
 * @date August 28 2014
 */

import java.io.*;

import java.util.Date;
import java.text.SimpleDateFormat;


public class MobileGateway{
  
	public MobileGateway() {
	}
  
	private static void usage() {
		System.err.println("usage: MobileGateway [-comm <source> <new Channel>] \n");
	}

	public static void printOptions() {
		System.out.println("\n******************************************************** ");
		System.out.println("* Please Select Operation:                             *");
		System.out.println("* 0. Start a new iteration     * 8. Detect Neighbors   *");
		System.out.println("* 1. Inject New Image	       * 9. Detect Target Set  *");	// -i
		System.out.println("* 2. Connect Target Node       * 10. Reboot Target     *");	// cmd: DISS
		System.out.println("* 3. Disseminate and Reprogram * 11. Set Node Address  *");	// -dr
		System.out.println("* 4. Stop Image Dissemination  * 111. Exit             *");	// -s
		System.out.println("* 5. Abort Reprogramming       *                       *");	// cmd: ABORT
		System.out.println("* 6. Print Image Information   *                       *");	// -p
		System.out.println("* 7. Erase a Volume            *                       *");	// -e
		System.out.println("******************************************************** ");
		System.out.print("* Input Operation Number: ");
	}

	private static void initialization() throws IOException {
		
		// check the record file
		System.out.println("Check initial settings...");
		RecordOperator.check_record_file();
		System.out.println("**Initial checking completed!");		
		
	}
	
	
	public void startup(String[] args) throws IOException {

		int option;
		int retval;
		String id_string;
		String[] ids_list;

		String opt_string;
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      	String time = df.format(new Date());
      	String tmpLog = null;
		
      	Operations op = new Operations();
		
		if (args.length != 3) {
			usage();
			System.exit(1);
		}
		else {
			if (!args[0].equals("-comm")) {
				usage();
				System.exit(1);
			}
		}
		
		initialization();
		op.init(args);
		
		printOptions();

		while(true) {
		
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			option = 0;
			opt_string = br.readLine();
			
			if (opt_string.length() == 0) {
				printOptions();
				continue;
			} else {
				try {
					option = Integer.parseInt(opt_string);
				} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				
					System.out.println("\nInvalid Selection!");
					printOptions();
					continue;
				}
			} 
	//		System.out.println("The option is: " + option);
			switch (option) {
				case 0:
					op.ClearRecordFlags();
					printOptions();
					break;
				case 1:	// inject
					op.DelugeInject();
					printOptions();
					break;
				case 2:	// get target node ready
					System.out.print("Input Target Node ID List To Be Connected: ");
					
					br = new BufferedReader(new InputStreamReader(System.in));
				
					// get the list of nodeids
					id_string = br.readLine();
					ids_list = id_string.split(" ");
					
					op.connect(ids_list);
					
					break;
				case 3:	// disseminate and reprogramming
					op.Deluge_dr();
					printOptions();
					break;
				case 4:	// stop dissemination
					op.Deluge_s();
					printOptions();
					break;
				case 5:	// abort
					System.out.println("Input Target Node ID List to be Aborted (input 'a' for all nodes): ");
					
					br = new BufferedReader(new InputStreamReader(System.in));
				
					// get the list of nodeids
					id_string = br.readLine();
					ids_list = id_string.split(" ");
					
					op.abort(ids_list);
					
					break;
				case 6:	// print base station info. (-p)
					op.Deluge_p();
					printOptions();
					break;
				case 7:	// erase
					op.Deluge_e();
					printOptions();
					break;
				case 8:	// detect neighbors
					op.detect();
					break;
					/////////////////////////////////////
				case 9:	// detect a subset of targets
					System.out.print("Input Target Node ID List To Be Detected: ");
					
					br = new BufferedReader(new InputStreamReader(System.in));
				
					// get the list of nodeids
					id_string = br.readLine();
					ids_list = id_string.split(" ");
					
					op.detect_subset(ids_list);
					break;
				case 10:
					System.out.print("Input Target Node ID To Be Rebooted: ");
					
					br = new BufferedReader(new InputStreamReader(System.in));
				
					// get the list of nodeids
					id_string = br.readLine();
					ids_list = id_string.split(" ");
					
					op.reboot(ids_list);
					break;
					
				case 11:
					// Set node id. This command should only be sent to one node. 
					// The input format would be: <target_node new_node id>
					// separate by "space"
					System.out.print("Input Target Node ID and New ID <target_id new_id>: ");
					
					br = new BufferedReader(new InputStreamReader(System.in));
				
					// get the list of nodeids
					id_string = br.readLine();
					ids_list = id_string.split(" ");
					
					if(ids_list.length != 2){
						System.out.println("ERROR: Input format not accepted.");
						System.out.println("       Correct format: <target_id new_id>.");
						break;
					}
					
					op.set_id(ids_list);
					break;
				case 111:	// exit
					
					op.exit();
					break;
				default:
					printOptions();
					break;
			}
			
	//		}// if
		}// while

	}
	
	public static void main(String[] args) throws Exception {
		
		if (args.length != 3) {
			usage();
			System.exit(1);
		}
		else {
			if (!args[0].equals("-comm")) {
				usage();
				System.exit(1);
			}
		}
	
		MobileGateway gateway = new MobileGateway();
		// use this string for testing
//		String[] args_t = {"-comm", "serial@/dev/ttyUSB1:57600", "13"};
		gateway.startup(args);
				
	}
}
