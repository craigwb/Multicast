import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Coordinator extends Thread {
	protected Socket socket;
	public Socket multicast;
	private CoordMan man;
	public boolean connected = false;// for multicast
	public boolean send_message = false;


	public Coordinator(Socket socket, CoordMan man) {
		this.socket = socket;
		this.man = man;
		System.out.println("New client connected from " + socket.getInetAddress().getHostAddress());
		start();
	}

	public void multicast(String s) {
		try {
			multicast.getOutputStream().write((s + "\n").getBytes());
			multicast.getOutputStream().flush();
		} catch (IOException e) {
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
				if (arg1.equalsIgnoreCase("register")) {
					if (args.hasNextInt()) {
						send_message = true;
						connected = true;
						TimeUnit.SECONDS.sleep(1);
						multicast = new Socket(socket.getInetAddress(), args.nextInt());
						out.write("server: registered\n".getBytes());
						out.flush();
					} else {
						out.write("server: bad port\n".getBytes());
						out.flush();
					}
				} else if (arg1.equalsIgnoreCase("deregister")) {
					connected = false;
					send_message = false;
					multicast = null;
					out.write("server: deregistered\n".getBytes());
					out.flush();
				} else if (arg1.equalsIgnoreCase("reconnect")) {
					if (args.hasNextInt()) {
						send_message = true;
						connected = true;
						multicast = new Socket(socket.getInetAddress(), args.nextInt());
						out.write("server: reconnected\n".getBytes());
						out.flush();
						man.get_missed(this);
					} else {
						out.write("server: bad port\n".getBytes());
						out.flush();
					}
				} else if (arg1.equalsIgnoreCase("disconnect")) {
					send_message = false;
					multicast = null;
					out.write("server: disconnected\n".getBytes());
					out.flush();
				} else if (arg1.equalsIgnoreCase("msend")) {
					if(send_message) {
					String m = args.nextLine();
					man.multicast(m);
					out.write("server: mutlicasted message\n".getBytes());
					out.flush();
					}else {
						out.write("server: Please join the multicast group by registering\n".getBytes());
						out.flush();
					}
				} else
					out.write("command was not understood\n".getBytes());
				out.flush();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
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
		// initial config file
		boolean quit = false;
		int timeThresh = 0;
		int port = 0;
		BufferedReader configBufferedReader;
		String configLine;
		try {
			configBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
			configLine = configBufferedReader.readLine();
			port = Integer.parseInt(configLine);
			configLine = configBufferedReader.readLine();
			timeThresh = Integer.parseInt(configLine);
		} catch (Exception e) {
			System.out.println(e);
		}
		// end initial config
		ServerSocket server = null;
		CoordMan manny = new CoordMan(timeThresh);
		try {
			server = new ServerSocket(port);
			while (true) {
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