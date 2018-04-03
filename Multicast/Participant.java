import java.io.*;
import java.net.*;
import java.nio.file.*;
import static java.nio.file.StandardOpenOption.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Participant {
	private mult_listener mult;
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private BufferedReader stdIn;
	private BufferedWriter bw;
	private boolean quit = false;
	public static void main(String args[]) {
		// initial config
		String ID = "";
		int port = 0;
		BufferedReader configBufferedReader;
		BufferedWriter bw = null;
		String configLine;
		String host = "";
		try {
			configBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
			configLine = configBufferedReader.readLine();
			ID = configLine;
			configLine = configBufferedReader.readLine();
			String s = "System Time: " + System.currentTimeMillis() + " Participant Started\n";
			bw = new BufferedWriter(new FileWriter(configLine, true));
			bw.append(s);
			bw.flush();
			System.out.println(configLine);
			configLine = configBufferedReader.readLine();
			Scanner scan = new Scanner(configLine);
			host = scan.next();
			port = scan.nextInt();
			scan.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		// end initial config
		new Participant(host, port, ID, bw);
	}

	public Participant(String host, int port, String ID, BufferedWriter bw) {
		try {
			this.bw = bw;
			System.out.println("Connecting to host " + host + " on port " + port + ".");
			try {
				socket = new Socket(host, port);
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (Exception e) {
				e.printStackTrace();
			}

			/** {@link UnknownHost} object used to read from console */
			stdIn = new BufferedReader(new InputStreamReader(System.in));
			ServerSocket ss=null;
			while (!quit) {
				System.out.print(ID + ": ");
				String userInput = stdIn.readLine();
				Scanner args = new Scanner(userInput);
				String arg1 = args.next();
				out.println(userInput);
				if (userInput.equals("quit")) {
					quit=true;
				} else if (arg1.equals("register")) {
					// ServerSocket multi_accept = new ServerSocket(args.nextInt());
					try {
						if (args.hasNextInt()) {
						ss = new ServerSocket(args.nextInt());
						//System.out.println("Allow system 5 seconds to connect.");
						mult = new mult_listener(ss.accept());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (arg1.equals("deregister")) {
					ss.close();
					mult = null;
					TimeUnit.SECONDS.sleep(2);
				} else if (arg1.equals("reconnect")) {
					try {
						if (args.hasNextInt()) {
							ss = new ServerSocket(args.nextInt());
							//System.out.println("Allow system 5 seconds to connect.");
							mult = new mult_listener(ss.accept());
							}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (arg1.equals("disconnect")) {
					ss.close();
					mult = null;
					TimeUnit.SECONDS.sleep(2);
				} else if (arg1.equals("msend")) {
				}
				String server_says = in.readLine();

				System.out.println(server_says);

			}

			/** Closing all the resources */
			out.close();
			in.close();
			stdIn.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class mult_listener extends Thread implements Runnable {
		Socket multic;
		BufferedReader in;

		mult_listener(Socket socket) {
			this.multic = socket;
			try {
				in = new BufferedReader(new InputStreamReader(multic.getInputStream()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Started listening for multicast");
			start();
		}

		public void run() {
			String message;
			try {
				// while ((message = in.readLine()) != null) {
				while ((message = in.readLine()) != null) {
					// System.out.println(message);
					bw.append("System Time: " + System.currentTimeMillis() + " Message Number In Session: " + message
							+ "\n");
					bw.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}
}
