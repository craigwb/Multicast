import java.io.IOException;
import java.net.Socket;

public class CoordMan {
	int coords = -1;
	int last_message_sent = 0;
	String messages[] = new String[10000];
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

	public CoordMan() {

	}

	public void add_coord(Socket socket, CoordMan man) {
		coords++;
		coordinators[coords] = new Coordinator(socket, man);

	}

	public void multicast(String s) {
		for (int i = 0; i <= getCoords(); i++) {
			if (coordinators[i].connected && coordinators[i].send_message)
				try {
					coordinators[i].multicast(last_message_sent + " multicast message: " + s);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}
}