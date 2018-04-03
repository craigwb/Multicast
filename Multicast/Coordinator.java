
import java.io.*;
import java.net.*;
import java.util.Scanner;




public class Coordinator extends Thread {
    protected Socket socket;
    public Socket multicast;
    private CoordMan man;
    public boolean connected = false;//for multicast
    public boolean send_message = false;
    private int last_message_recieved;
    public Coordinator(Socket socket, CoordMan man) {
        this.socket = socket;
        this.man = man;
        System.out.println("New client connected from " + socket.getInetAddress().getHostAddress());
        start();
    }
    public void multicast(String s) {
    	try {
			multicast.getOutputStream().write((s+"\n").getBytes());
			multicast.getOutputStream().flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
                	multicast = new Socket(socket.getInetAddress(),args.nextInt());
                }else if(arg1.equalsIgnoreCase("deregister")) {
                	connected = false;
                	send_message=false;
                	multicast = null;
                	out.write("server: deregistered\n".getBytes());
                }else if(arg1.equalsIgnoreCase("reconnect")) {
                	send_message=true;
                	ServerSocket ss = new ServerSocket(args.nextInt());
                	multicast = ss.accept();
                	out.write("server: reconnected\n".getBytes());
                }else if(arg1.equalsIgnoreCase("deconnect")) {
                	send_message=false;
                	multicast = null;
                	out.write("server: deconnected\n".getBytes());
                }else if(arg1.equalsIgnoreCase("msend")) {
                	String m = args.next();
                	man.multicast(m);
                	out.write("server: mutlicasted message\n".getBytes());
                }else out.write("command was not understood\n".getBytes());
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
        CoordMan manny = new CoordMan();
        try {
            server = new ServerSocket(port);
            while (true) {
                /**
                 * create a new {@link SocketServer} object for each connection
                 * this will allow multiple client connections
                 */
            	manny.add_coord(server.accept(), manny);
                
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