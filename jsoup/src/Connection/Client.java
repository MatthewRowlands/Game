package Connection;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import Launcher.Launcher;
import Main.Display;

public class Client extends Thread{
	
	private Socket socket = null;
	private ObjectInputStream inStream = null;
	private ObjectOutputStream outStream = null;
	String name = "bob";
	int port;
	String ip;
	Vector3f v3f = new Vector3f(0, 0, 0);
	public static int clientnumber = 0;
	boolean run = true;
	int frames = 0;
	int tickCount = 0;
	public ArrayList<double[]> positions = new ArrayList<double[]>();

	public Client(int port, String ip, String name) {
		this.port = port;
		this.ip = ip;
		this.name = name;
		
		try {
		socket = new Socket(ip, port);
		System.out.println("Connected");
		outStream = new ObjectOutputStream(socket.getOutputStream());//send name
		outStream.writeObject(name);
		
		inStream = new ObjectInputStream(socket.getInputStream());//recieve client number
		clientnumber = (int) inStream.readObject();
		
		Display.BeginNetworkUpdate();
		
		}catch (Exception e) {
			System.err.println("Client Error: " + e.getMessage());
			System.err.println("Localized: " + e.getLocalizedMessage());
			System.err.println("Stack Trace: " + e.getStackTrace());
			run = false;
		}
	}
	
	@Override
	public void run(){
		double unprocessedSeconds = 0;
		long previousTime = System.nanoTime();
		double secondsPerTick = 1 / Display.WINDOW_TICK_RATE;
		boolean ticked = false;
		while (run) {
			Update();			
			long currentTime = System.nanoTime();
			long passedTime = currentTime - previousTime;
			previousTime = currentTime;
			unprocessedSeconds += passedTime / 1000000000.0;

			while (unprocessedSeconds > secondsPerTick) {
				tick();
				unprocessedSeconds -= secondsPerTick;
				ticked = true;
				
				if(tickCount % Display.WINDOW_NETWORK_TICK_RATE == 0){
					positions = networkUpdate();
				}
				
				if (tickCount % Display.WINDOW_TICK_RATE == 0) {
					previousTime += 1000;
					frames = 0;
				}
				if (ticked) {
				}
			}
		}
	}

	private void tick() {
		
	}

	private void Update() {
		
	}

	@SuppressWarnings("unchecked")
	public synchronized ArrayList<double[]> networkUpdate() {
		try {
			v3f = new Vector3f(Display.x, Display.y, Display.z);
			
			double x = v3f.x;
			double y = v3f.y;
			double z = v3f.z;
			int ping = 0;
			
			double[] xyz = new double[4];
			xyz[0] = x;
			xyz[1] = y;
			xyz[2] = z;
			xyz[3] = ping;
			outStream = new ObjectOutputStream(socket.getOutputStream());//send position
			outStream.writeObject(xyz);
			
			inStream = new ObjectInputStream(socket.getInputStream());//recieve position(s)	
			ArrayList<double[]> players = (ArrayList<double[]>) inStream.readObject();
			Display.ping = (int) players.get(clientnumber-1)[3];
			return players;
		
		} catch (Exception e) {
			System.err.println("Client Error: " + e.getMessage());
			System.err.println("Localized: " + e.getLocalizedMessage());
			System.err.println("Stack Trace: " + e.getStackTrace());
		}
		return null;
	}
}