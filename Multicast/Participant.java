import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Participant {
	private mult_listener mult;
	public static void main(String args[]) {
		// initial config
				boolean exit = false;
				String ID = "";
				int port = 0;
				BufferedReader configBufferedReader;
				BufferedWriter bw = null;
				String configLine;
				String host = "";
				Socket send_coordinator;
				PrintStream coordinator_printer = null;
				try {
					configBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
					configLine = configBufferedReader.readLine();
					ID = configLine;
					configLine = configBufferedReader.readLine();
					FileWriter fw = new FileWriter(configLine, true);
					bw = new BufferedWriter(fw);
					String s = "initialized\n";
					bw.write(s);

					configLine = configBufferedReader.readLine();
					Scanner scan = new Scanner(configLine);
					host = scan.next();
					port = scan.nextInt();
					scan.close();
				} catch (Exception e) {
					System.out.println(e);
				}
				// end initial config
        new Participant(host, port);
    }

    public Participant(String host, int port) {
        try {
            System.out.println("Connecting to host " + host + " on port " + port + ".");

            Socket echoSocket = null;
            PrintWriter out = null;
            BufferedReader in = null;

            try {
                echoSocket = new Socket(host, 8081);
                out = new PrintWriter(echoSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            } catch (UnknownHostException e) {
                System.err.println("Unknown host: " + host );
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Unable to get streams from server");
                System.exit(1);
            }

            /** {@link UnknownHost} object used to read from console */
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                System.out.print("client: ");
                String userInput = stdIn.readLine();
                Scanner args = new Scanner(userInput);
                String arg1 = args.next();
                out.println(userInput);
                if (userInput.equals("quit")) {
                    break;
                }else if (arg1.equals("register")) {
                	//ServerSocket multi_accept = new ServerSocket(args.nextInt());
                	try {
                		TimeUnit.SECONDS.sleep(2);
                		mult = new mult_listener(new Socket(host,args.nextInt()));
                	} catch (Exception e) {
                		e.printStackTrace();
                	}
                }else if (arg1.equals("deregister")) {
                	mult = null;
                }else if (arg1.equals("reconnect")) {
                	try {
                		mult = new mult_listener(new Socket(host,args.nextInt()));
                	} catch (Exception e) {
                		e.printStackTrace();
                	}
                }else if (arg1.equals("deconnect")) {
                	mult=null;
                }else if (arg1.equals("msend")) {
                }
                String server_says = in.readLine();
                
                System.out.println(server_says);
                
                
            }

            /** Closing all the resources */
            out.close();
            in.close();
            stdIn.close();
            echoSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    class mult_listener extends Thread{
    	Socket multic;
    	mult_listener(Socket socket){
    		this.multic = socket;
    		System.out.println("Starting listening for multicast");
    		start();
    	}
    	public void run() {
    		String message;
    		BufferedReader in;
			try {
				in = new BufferedReader(new InputStreamReader(multic.getInputStream()));
	            while ((message = in.readLine()) != null) {
	            	//System.out.println("got something");
					System.out.println(message);
	    		}
			} catch (Exception e) {
				e.printStackTrace();
			}
    		
    	}
    	
    }
}