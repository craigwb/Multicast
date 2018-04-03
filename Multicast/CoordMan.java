import java.io.IOException;
import java.net.Socket;

public class CoordMan {
	int coords = -1;
	int last_message_sent = 0;
	long timeThresh;
	long startTime = System.nanoTime();
	Message messages[] = new Message[10000];
	private Coordinator coordinators[] = new Coordinator[10000];

	public void incCoords() {
		coords++;
	}

	public void incMessages() {
		last_message_sent++;
	}

	public int getCoords() {
		return coords;
	}

	public int getLastMessage() {
		return last_message_sent;
	}

	public CoordMan(int x) {
		timeThresh = x  * 1000000000;
	}

	public void add_coord(Socket socket, CoordMan man) {
		coords++;
		coordinators[coords] = new Coordinator(socket, man);

	}

	public void multicast(String s) {
		for (int i = 0; i <= getCoords(); i++) {
			if (coordinators[i].connected && coordinators[i].send_message)
				try {
					messages[last_message_sent] = new Message(s);
					coordinators[i].multicast(last_message_sent + " multicast message: " + s);
					last_message_sent++;
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}
	public void get_missed(Coordinator c) {
		
		long current_time = System.nanoTime();
		for(int i = last_message_sent-1; i>-1;i--) {
			System.out.println(i);
			if (current_time - messages[i].time > timeThresh) {
				for (int x = i+1; x<last_message_sent;x++) {
					c.multicast(x+ " multicast message: " + messages[i].message);
				}
				i=-1;
			}else {
				for (int x = i; x<last_message_sent;x++) {
					c.multicast(x+ " multicast message: " + messages[i].message);
				}
				i=-1;
			}
		}
	}
	
	public class Message{
		public String message;
		public long time;
		Message(String m){
			time  = System.nanoTime();
			message = m;
		}
		
		
	}
}