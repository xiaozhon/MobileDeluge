package MBGateway;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/*
 * This class put everything in to a log file
 * */
public class Logs {
	public static boolean does_log_exist(){
		File f = new File("data/logs.txt");
		if(f.exists()){
			return true;
		} else {
			return false;
		}
	}
	
	
	
	public static void log(String s) throws IOException {
		if(does_log_exist()){
			// log already existed, append the String into the log
			
			FileWriter outf = new FileWriter("data/logs.txt", true);
			outf.write(s);
			outf.close();
			
		} else{
			// create log file and put the String into the log
			// create the file
			// create the directory first if necessary
			System.out.println("Log file does not exist, create log file...");

			File dir = new File("data");
			if(!dir.exists()){
				if(!dir.mkdir()) {
					System.out.println("\nCreate 'data' directory failed!");
				} else {
					System.out.println("\nSuccessfully created 'data' directory");
				}
			}
			FileWriter outf = new FileWriter("data/logs.txt");
			BufferedWriter bw = new BufferedWriter(outf);
			bw.write(s);
			bw.close();
			System.out.println("Log file created successfully!");
		}
	}
	
	public static void main(String[] args) throws IOException {
		log("The first log\n");
		log("\nThe second log\n");
		log("\nThe third log\n");
	}
}
