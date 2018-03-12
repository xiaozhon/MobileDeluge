package MBGateway;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class RecordOperator {
	
	public static int totalRecords = 0;
	public static final int ID = 0;
	public static final int TYPE = 1;
	public static final int FLAG = 2;
	public static final int TIME = 3;
	
	public static void println(String text) {
		System.out.println(text);
	}
	public static boolean does_record_exist(){
		File xlsxFile = new File("data/records.txt");
		if(xlsxFile.exists()){
			return true;
		} else {
			return false;
		}
	}
	// delete the file
	// TODO: we don't need it right now
	public static void delete(String xlsxName) {
			
	}
	
	// This function clear all the flags in the records (set to "NO")
	public static void clear_flags() throws IOException {
		if(!does_record_exist()) {
			System.out.println("Record file does not exist!");
			return;
		}
		int i;
		FileReader inf = new FileReader("data/records.txt");
		
		BufferedReader br = new BufferedReader(inf);
		String line;
		String outLines = null;
		int lineCount = 0;
		while((line = br.readLine()) != null) {
			lineCount++;
			if(lineCount <= 2) {
				// leave the first two lines
				if(lineCount == 1) {
					outLines = line + '\n';
				} else {
					outLines = outLines + line + '\n';
				}
				continue;
			}
			String[] s = line.split("\t");
	//		println("the length of " + lineCount + " is: " + s.length);
			if(s.length == 4) {	// the record line
				// the flag
				for(i = 0; i < FLAG; i++) {
					outLines = outLines + s[i] + '\t';
				}
				outLines = outLines + "NO";
				for(i = FLAG + 1; i < s.length; i++) {
					outLines = outLines + '\t' + s[i]; 
				}
				outLines = outLines + '\n';
			} else {
				// this is a blank line, add the "\n" flag
				outLines = outLines + '\n';
			}
		}
		br.close();
		
		FileWriter outf = new FileWriter("data/records.txt");
		BufferedWriter bw = new BufferedWriter(outf);
		bw.write(outLines);
        bw.close();

	}
	
	// create the file
	public static void check_record_file() throws IOException {
		if (does_record_exist()) {
			// count the number of records in the file
			FileReader inf = new FileReader("data/records.txt");
			BufferedReader br = new BufferedReader(inf);
			
			totalRecords = 0;
			String line;
			while((line = br.readLine()) != null) {
				String[] s = line.split("\t");
				if(s.length > 0) {
					totalRecords++;
				}
			}	
			br.close();
			if(totalRecords >= 2) {
				totalRecords = totalRecords - 2;
			}// the first line is not a record
			println("Total number of records is: " + totalRecords);
			
		} else {
			// create the file
			// create the directory first if necessary
			println("Record does not exist, create record file...");
			File dir = new File("data");
			if(!dir.exists()){
				if(!dir.mkdir()) {
					System.out.println("\nCreate 'data' directory failed!");
				} else {
					System.out.println("\nSuccessfully created 'data' directory");
				}
			}
			
			FileWriter outf = new FileWriter("data/records.txt");
			BufferedWriter bw = new BufferedWriter(outf);
			bw.write("ID\tType\tis_updated\tupdate_time\n");
			bw.write("--------------------------------------------\n");
            bw.close();
            println("Record file created successfully!");
		
		}
	}

	
	public static void delete_row(String[] idList, int count) throws IOException {
		if(!does_record_exist()) {
			System.out.println("Record file does not exist!");
			return;
		} 
		int[] deleted =  new int[idList.length];
		int i;
		for(i = 0; i < idList.length; i++) {
			deleted[i] = 0;
		}
		FileReader inf = new FileReader("data/records.txt");

		BufferedReader br = new BufferedReader(inf);
		String line;
		String outLines = null;
		int lineCount = 0;
		int toBeDeleted = 0;
		while((line = br.readLine()) != null) {
			lineCount++;
			if(lineCount <= 2) {
				if(lineCount == 1) {
					outLines = line + "\n";
				} else {
					outLines = outLines + line + "\n";
				}
				continue;
			}
			
			String[] s = line.split("\t");
			toBeDeleted = 0;
			if(s.length > 0) {
				for(i = 0; i < count; i++) {
					if(s[0].equals(idList[i])){
						// the line is found, we should skip this line
//						System.out.println(line);
						deleted[i] = 1;
						toBeDeleted = 1;
						totalRecords--;
						break;
					}
				}
				if(toBeDeleted == 1) {
					// the line should be deleted, skip it
					println("Node " + idList[i] + " is deleted!");
				} else {
					// the line is not to be deleted, keep it
					outLines = outLines + line + "\n";
				}
				
			} else {
				// an empty line
				outLines = outLines + "\n";
			}
		}	
		br.close();
		for(i = 0; i < idList.length; i++) {
			if(deleted[i] == 0) {
				println("Node " + idList[i] + " is not in the record!" );
			}
		}
		
		FileWriter outf = new FileWriter("data/records.txt");
		BufferedWriter bw = new BufferedWriter(outf);
		bw.write(outLines);
        bw.close();

	}

	// Add row
	public static void add_row(String nodeid, String type, String flag, String time) throws IOException {
		if(!does_record_exist()) {
			System.out.println("Record file does not exist!");
			return;
		} 
		
		FileReader inf = new FileReader("data/records.txt");

		BufferedReader br = new BufferedReader(inf);
		String line;
		String outLines = null;
		int lineCount = 0;
		while((line = br.readLine()) != null) {
//			System.out.println(line);
			lineCount++;
			if(lineCount <= 2) {
				if(lineCount == 1) {
					outLines = line + '\n';
				} else {
					outLines = outLines + line + '\n';
				}
				continue;
			}
			String[] s = line.split("\t");
			if(s.length > 0) {
				outLines = outLines + line + '\n';

				if(s[0].equals(nodeid)){
					println("Node " + nodeid + " is already in the record!");
					br.close();
					return;
				}
		
			} else {
				outLines = outLines + '\n';

			}
		}	
		br.close();
		String newLine = nodeid + "\t" + type + "\t" + flag + "\t" + "'" + time + "'";
		outLines = outLines + newLine + '\n';
		
		FileWriter outf = new FileWriter("data/records.txt");
		BufferedWriter bw = new BufferedWriter(outf);
		bw.write(outLines);
        bw.close();
        
        totalRecords++;
		println("totalRecords update: " + totalRecords);
		
	}
	
	public static void update(String nodeid, String type, int targetField, String newVal) throws IOException {
		if(!does_record_exist()) {
			System.out.println("Record file does not exist!");
			return;
		}
		int i;
		FileReader inf = new FileReader("data/records.txt");
		
		BufferedReader br = new BufferedReader(inf);
		String line;
		String targetLine = null;
		String outLines = null;
		int lineCount = 0;
		while((line = br.readLine()) != null) {
			lineCount++;
			if(lineCount <= 2) {
				// leave the first two lines
				if(lineCount == 1) {
					outLines = line + '\n';
				} else {
					outLines = outLines + line + '\n';
				}
				continue;
			}
			String[] s = line.split("\t");
			if(s.length > 0) {
				if(s[0].equals(nodeid)) {
					// if the node is found
					targetLine = line;
					int fieldIdx = 0;
		//			println("Line found! + " + targetLine);
					
					switch(targetField) {
						case ID:
							// s[0]
							fieldIdx = 0;

							break;
						case TYPE:
							// s[1]
							fieldIdx = 1;
							
							break;
						case FLAG:
							// s[2]
							// then update the flag, the time has to be updated also.
							fieldIdx = 2;
							
							break;
						case TIME:
							// s[3]
							fieldIdx = 3;
							newVal = "'" + newVal + "'";
							break;
						default:
							fieldIdx = -1;
//							return;
							break;
					}
					
					if(fieldIdx >= 0) {
						if(s[fieldIdx].equals(newVal)) {
							// the same value, no need to update
			//				println("-----------------Update: Stored value is the same as the newVal!");
							outLines = outLines + line + '\n';
						} else {
			//				println("-------------stored value is " + s[fieldIdx] + ", newval is " + newVal);
							for(i = 0; i < fieldIdx; i++) {
								outLines = outLines + s[i] + '\t';
							}
							if(fieldIdx == FLAG && newVal == "YES"){
								// the flag field, if the newVal is yes, update the time information
								SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								String time = df.format(new Date());
								outLines = outLines + newVal + '\t' + "'" + time + "'";
							} else {
								outLines = outLines + newVal;
								for(i = fieldIdx + 1; i < s.length; i++) {
									outLines = outLines + '\t' + s[i]; 
								}
							}
						
							outLines = outLines + '\n';
							
						}
						
					} else {
						outLines = outLines + line + '\n';
						println("The field " + targetField + " is not in the record!");
					}
				} else { // if not equals
					// not the target line, add this line to outLines
					outLines = outLines + line + '\n';
				}
			} else{
				// this is a blank line, add the "\n" flag
				outLines = outLines + '\n';
			}
		}
		br.close();
		
		
		FileWriter outf = new FileWriter("data/records.txt");
		BufferedWriter bw = new BufferedWriter(outf);
		bw.write(outLines);
        bw.close();
        
        if(targetLine == null) {
        	// the updated node is not in the record, add it to the record
			println("Node " + nodeid + " is not in the record!");
			// TODO: create record for this node, after the update function finished the file accessing
			println("Creating a record for node " + nodeid + " ...");
			// // type is unknown, a big problem? Put the UNKNOWN to the record
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = df.format(new Date());
			if(targetField == FLAG) {
				add_row(nodeid, type, newVal, time);	 
			} else{ 
				add_row(nodeid, type, "NO", time);
        	}
        }
		
	}

	public static void display() throws IOException {
		if(!does_record_exist()) {
			System.out.println("Record file does not exist!");
			return;
		} 
		FileReader inf = new FileReader("data/records.txt");

		BufferedReader br = new BufferedReader(inf);
		String line;
		
		System.out.println("\n**************************************************");
		System.out.println("Display all records:");
		while((line = br.readLine()) != null) {
			System.out.println(line);

		}	
		br.close();
		println("The number of total Records is: " + totalRecords);
	}
	
	public static void display(String[] idList, int count) throws IOException {
		if(!does_record_exist()) {
			System.out.println("Record file does not exist!");
			return;
		} 
		int[] displayed =  new int[idList.length];
		int i;
		for(i = 0; i < idList.length; i++) {
			displayed[i] = 0;
		}
		FileReader inf = new FileReader("data/records.txt");

		BufferedReader br = new BufferedReader(inf);
		String line;
		System.out.println("\n**************************************************");
		System.out.println("Display selected records:");
		while((line = br.readLine()) != null) {
//			System.out.println(line);
			String[] s = line.split("\t");
			if(s.length > 0) {
				for(i = 0; i < count; i++) {
					if(s[0].equals(idList[i])){
						System.out.println(line);
						displayed[i] = 1;
					}
				}
			}
//			for(int i = 0; i < s.length; i++) {
//				System.out.println(s[i]);
//			}
		}	
		br.close();
		for(i = 0; i < idList.length; i++) {
			if(displayed[i] == 0) {
				System.out.println("  *Node " + idList[i] + " is not in the record!");
			}
		}
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		check_record_file();
		
		
		display();
		String[] idList = new String[3];
		idList[0] = "22";
		idList[1] = "23";
		idList[2] = "34";
		display(idList, 3);
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      	String time = df.format(new Date());
		add_row("22", "MICAZ", "NO", time);
		
      	time = df.format(new Date());
		add_row("23", "MICAZ", "NO", time);
		
      	time = df.format(new Date());
		add_row("24", "IRIS", "NO", time);
		
      	time = df.format(new Date());
		add_row("25", "TELOSB", "NO", time);
		
		display();
		
		/* Test case 4: restore
		 * 	update_id
		 *  update_flag
		 *  update_type
		 *  update_time
		 * */
		System.out.println("\n-------------------- Test 3 ----------------------");

		System.out.println("\nUpdate id");
		update("22", "MICAZ", ID, "100"); 
		display();
		
		System.out.println("\nUpdate flag");
		update("23", "MICAZ", FLAG, "YES"); 
		display();   
		
		System.out.println("\nUpdate type");
		update("24", "IRIS", TYPE, "TELOSB");
		display();
		
		System.out.println("\nUpdate time");
      	time = df.format(new Date());
		update("25", "TELOSB", TIME, time); 
		display();
		
		System.out.println("\n-------------------- Test 4 ----------------------");

		System.out.println("\nRestore id");
		update("100", "MICAZ", ID, "22"); 
		display();
		
		
		System.out.println("\nRestore flag");
		// if flag is updated, also update time
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		update("23", "MICAZ", FLAG, "NO"); 
		display();   
		
		System.out.println("\nRestore type");
		update("24", "IRIS", TYPE, "IRIS");
		display();
		
//		System.out.println("\nUpdate all time");
 //     	time = df.format(new Date());
//		update("22", TIME, time); 
//		update("23", TIME, time); 
//		update("24", TIME, time); 
//		update("25", TIME, time); 
//		display();
		
//		display();
		
		System.out.println("\n-------------------- Test 5 ----------------------");
		
      	System.out.println("Delete nodes 22, 24, 34. Node 34 is not in the record...");
		idList[0] = "22";
		idList[1] = "24";
		idList[2] = "34";
      	delete_row(idList, 3);
      	display();
      	
      	
      	System.out.println("\n-------------------- Test 6 ----------------------");
		
      	System.out.println("Add line 22 24 back to the record");
		add_row("22", "MICAZ", "NO", time);
		add_row("24", "IRIS", "NO", time);
		
		display();
      	
/*		System.out.println("\n-------------------- Test 7 clear_flags() ----------------------");
		
		update("22", FLAG, "YES");
		update("23", FLAG, "YES");
		display();
		clear_flags();
      	display();
  */    	
      	try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      	System.out.println("\n-------------------- Test 8 update the same value ----------------------");
		update("22", "MICAZ", FLAG, "NO");
		display();
      	
		System.out.println("\n-------------------- Test 9 update a unknown id ----------------------");
		update("26", "TELOSB", FLAG, "YES");
		display();
	}

}
