import java.io.IOException;
import java.net.Socket;

public class CoordMan{
    	int coords = -1;
    	int last_message_sent = 0;
    	String messages[];
    	private Coordinator coordinators[] = new Coordinator[10000];
    	public void incCoords() {coords++;}
    	public void incMessages() {last_message_sent++;}
    	public int getCoords() { return coords;}
    	public int getLastMessage() {return last_message_sent;}
    	public String send_message(String s) {
    		messages[last_message_sent] = s;
    		return s;
    	}
    	public CoordMan(){
    		
    	}
    	public void add_coord(Socket socket, CoordMan man) {
    		coords++;
    		coordinators[coords] = new Coordinator(socket, man);
    		
    	}
    	public void multicast(String s) {
    		System.out.println(coords);
    		for(int i=0;i<=getCoords();i++) {
        		if(coordinators[i].connected && coordinators[i].send_message)
					try {
						System.out.println(s);
						coordinators[i].multicast(s);
					} catch (Exception e) {
						e.printStackTrace();
					}
        	}
    	}
    }