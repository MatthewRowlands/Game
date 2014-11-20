package Connection;

import java.net.Socket;

public class QueueData extends Thread{

	public void run(){
		while(true){
			sendData(null, null);
		}
	}
	public void sendData(Vector3f v3f, Socket s){
		
	}
}
