import java.io.*;
import java.util.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.InetAddress;
public class participant{ 
	public static void main(String[] args) throws UnknownHostException { 
		boolean exit = false;
		//initialize configuration here
		int ID=0;
		int port=0;
		BufferedReader configBufferedReader;
		String configLine;
		String host = "";
		Socket send_coordinator;
		PrintStream coordinator_printer = null;
		try { 
	    configBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
	    configLine = configBufferedReader.readLine();
	    ID = Integer.parseInt(configLine);
	    configLine = configBufferedReader.readLine(); 
	    FileWriter fw = new FileWriter(configLine, true);
		BufferedWriter bw = new BufferedWriter(fw);
	    //initializing log file
	    String s = "initialized\n";
	    bw.write(s);
	    bw.close();
	    
	    //end initializing log file
	    configLine = configBufferedReader.readLine();
	    Scanner scan = new Scanner(configLine);
	    host = scan.next();
	    port = scan.nextInt();
	    scan.close();
	    send_coordinator = new Socket(host,port);
	    coordinator_printer = new PrintStream(send_coordinator.getOutputStream());
		}
		catch (Exception e) {
			System.out.println(e);
		}
		Scanner keyboard = new Scanner(System.in);
		String user_input;
		while(!exit) { //the forever loop until exit command
			user_input = keyboard.nextLine();
			//System.out.println(user_input+"\n"+host+" "+port+"\n"+ID);
			Scanner scan = new Scanner(user_input);
			String arg1 = scan.next();
			if(arg1.equalsIgnoreCase("register")) {
				coordinator_printer.println("$771\n"+ID+"\n"+InetAddress.getLocalHost().getHostName());
			}
			if(arg1.equalsIgnoreCase("deregister")) {
				
			}
			if(arg1.equalsIgnoreCase("disconnect")) {
				
			}
			if(arg1.equalsIgnoreCase("reconnect")) {
				
			}
			if(arg1.equalsIgnoreCase("msend")) {
				
			}

			
		}
		keyboard.close();
		
		//return 0;
	}
	
	participant(){
		
	}
}