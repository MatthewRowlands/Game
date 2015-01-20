package Connection;

import java.awt.Color;
import java.awt.Font;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JFrame;
import javax.swing.JLabel;

import Main.Display;

public class EchoThread extends Thread{
	volatile boolean finished = false;
	boolean canUpdate = false;
	protected Socket socket;
	boolean isrunning = false;
	boolean edited = false;
	ObjectInputStream inStream = null;
	ObjectOutputStream outStream = null;
	Vector3f v3f = new Vector3f(0, 0, 0);
	double x = 0;
	double y = 0;
	double z = 0;
	JLabel clientinfo = new JLabel();
	JFrame f;
	int clientindex = 0;
	String clientname = "";
	int numberofclients = Server.clients.size();
	
	int tickCount = 0;
	int frames = 0;
	long t1 = System.nanoTime();
	long t2 = System.nanoTime();
	double ping = 0;
	double PING = ping;

	public EchoThread(Socket clientSocket) {
		this.socket = clientSocket;
		try {
			socket.setSoTimeout(20000);
		} catch (SocketException e) {
		}

		f = Server.f;
		f.add(clientinfo);
		clientinfo.setOpaque(true);
		clientinfo.setBounds(0, 20 + (20 * Server.clients.size()), 150, 19);
		clientinfo.setBackground(Color.GREEN);

	}
	
	@Override
	public void run(){
		double unprocessedSeconds = 0;
		long previousTime = System.nanoTime();
		double secondsPerTick = 1 / Display.gametickrate;
		boolean ticked = false;
		while (!finished) {
			Update();			
			long currentTime = System.nanoTime();
			long passedTime = currentTime - previousTime;
			previousTime = currentTime;
			unprocessedSeconds += passedTime / 1000000000.0;
	
			while (unprocessedSeconds > secondsPerTick) {
				tick();
				unprocessedSeconds -= secondsPerTick;
				ticked = true;
				tickCount++;
				
				if(tickCount % Display.networktickrate == 0){
				networkUpdate();
				}
				
				if (tickCount % Display.gametickrate == 0) {
					PING = ping;
					System.out.println("aaaaaaaaaaa");
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

	public synchronized void networkUpdate() {
		if(canUpdate){
			numberofclients = Server.clients.size();
			try {
				
				inStream = new ObjectInputStream(socket.getInputStream());
				double[] xyz = (double[]) inStream.readObject();	
				
				x = xyz[0];
				y = xyz[1];
				z = xyz[2];

				t1 = System.nanoTime();

				xyz[3] = (double)((double)(t1-t2)/1000)/1000;
				
				ping = xyz[3];
				
				clientinfo.setText("[" + clientindex + "] " + clientname + ": "
						+ (int)x + "," + (int)y + "," + (int)z+" Ping: "+String.format("%.2g%n", ping));
				FontToFit(clientinfo);
				
				Server.clientpos.set(clientindex-1, xyz);
				//Server.positions[clientindex-1][0][0] = x;
				t2 = System.nanoTime();
				
				outStream = new ObjectOutputStream(socket.getOutputStream());
				outStream.writeObject(Server.clientpos);

			} catch (Exception e) {
				
			System.out.println();
					
				try {
					end(e);
				} catch (Exception e1) {
				}
				return;
			}
		}
	}
	
	
	public void end(Exception e) throws Exception {
		Server.removeClient(clientindex-1);
		Server.serverMessage(clientname + " Disconnected");
		inStream.close();
		outStream.close();
		socket.shutdownInput();
		socket.shutdownOutput();
	    socket.close();
	    System.out.println(e.getMessage());
		finished = true;
	}

	public void FontToFit(JLabel label) {
		Font labelFont = label.getFont();
		String labelText = label.getText();

		double stringWidth = label.getFontMetrics(labelFont).stringWidth(
				labelText);
		double componentWidth = label.getWidth();

		// Find out how much the font can grow in width.
		double widthRatio = componentWidth / stringWidth;

		double newFontSize = (int) (labelFont.getSize() * widthRatio);
		double componentHeight = label.getHeight();

		// Pick a new font size so it will not be larger than the height of
		// label.
		double fontSizeToUse = Math.min(newFontSize, componentHeight);

		// Set the label's font size to the newly determined size.
		label.setFont(new Font(labelFont.getName(), Font.PLAIN,
				(int) fontSizeToUse - 1));
	}
}
