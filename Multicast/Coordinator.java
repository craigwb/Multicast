import java.io.*;
import java.net.*;
import java.util.Scanner;


public class Coordinator extends Thread {
    protected Socket socket;
    private Socket multicast;
    private String messages[];
    int missed[] = new int[10000];
    private Coordinator coordinators[];
    private boolean connected = false;//for multicast
    private boolean send_message = false;
    private int last_message_sent;
    private int last_message_recieved;
    private int coords;
    private Coordinator(Socket socket, String s[], Coordinator coordinators[], int i, int x) {
        this.socket = socket;
        this.coordinators = coordinators;
        messages = s;
        last_message_sent = i;
        coords = x;
        System.out.println("New client connected from " + socket.getInetAddress().getHostAddress());
        start();
    }

    public void run() {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String request;
            while ((request = br.readLine()) != null) {
                System.out.println("Message received:" + request);
                Scanner args = new Scanner(request);
                String arg1 = args.next();
                if(arg1.equalsIgnoreCase("register")) {
                	send_message=true;
                	connected = true;
                	out.write("server: registered\n".getBytes());
                	ServerSocket ss = new ServerSocket(args.nextInt());
                	multicast = ss.accept();
                }else if(arg1.equalsIgnoreCase("deregister")) {
                	connected = false;
                	send_message=false;
                	multicast.close();
                	out.write("server: deregistered\n".getBytes());
                }else if(arg1.equalsIgnoreCase("reconnect")) {
                	send_message=true;
                	ServerSocket ss = new ServerSocket(args.nextInt());
                	multicast = ss.accept();
                	out.write("server: reconnected\n".getBytes());
                }else if(arg1.equalsIgnoreCase("deconnect")) {
                	send_message=false;
                	multicast.close();
                	out.write("server: deconnected\n".getBytes());
                }else if(arg1.equalsIgnoreCase("msend")) {
                	messages[last_message_sent]=args.next();
                	for(int i=0;i<=coords;i++) {
                		if(coordinators[i].connected && coordinators[i].send_message) 
                			coordinators[i].multicast.getOutputStream().write(("multicast: "+messages[last_message_sent]+"\n").getBytes());
                		
                	}
                	last_message_sent++;
                }else out.write("command was not understood\n".getBytes());
                
                
                //request += '\n';
                //out.write(request.getBytes());
            }

        } catch (IOException ex) {
            System.out.println("Unable to get streams from client");
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("SocketServer Example");
        String messages[] = new String[10000];
      //initial config file
    	boolean quit = false;
		int timeThresh=0;
		int port=0;
		BufferedReader configBufferedReader;
		String configLine;
		try { 
	    configBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
	    configLine = configBufferedReader.readLine();
	    port = Integer.parseInt(configLine);
	    configLine = configBufferedReader.readLine();
	    timeThresh = Integer.parseInt(configLine);
		}
		catch (Exception e) {
			System.out.println(e);
		}
		//end initial config 
        ServerSocket server = null;
        Coordinator coordinators[] = new Coordinator[100];
        int i = 0;
        int x = 0;
        try {
            server = new ServerSocket(port);
            while (true) {
                /**
                 * create a new {@link SocketServer} object for each connection
                 * this will allow multiple client connections
                 */
                coordinators[i] = new Coordinator(server.accept(),messages,coordinators,x,i);
                i++;
            }
        } catch (IOException ex) {
            System.out.println("Unable to start server.");
        } finally {
            try {
                if (server != null)
                    server.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}