package Connection;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import Launcher.Launcher;
import Main.Display;

public class Server implements Runnable{

	private static Thread thread;
	static String serverport = Display.DEFAULT_PORT;
	boolean run = false;
	static JFrame f = new JFrame();
	static JPanel p = new JPanel();
	static JLabel info = new JLabel();
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	private boolean connection = false;
	private boolean serverisrunning = true;
	public static int port;
	static JTextArea jt = new JTextArea();
	public static boolean showtimestamp = false;
	private JScrollPane sc = new JScrollPane(jt,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar
			.getInstance().getTime());
	String defaulttext = System.getProperty("user.name") + " / " + getIp()
			+ " > ";
	String error = "Error - ";
	String badcom = " Is Not Recognised As A Valid Command";
	String baduse = "Incorrect Use";
	String tftext = "";
	ObjectInputStream inStream = null;
	ObjectOutputStream outStream = null;
	JTextField tf = new JTextField("/");
	static ArrayList<EchoThread> clients = new ArrayList<EchoThread>();
	static ArrayList<String> clientnames = new ArrayList<String>();
	public static ArrayList<double[]> clientpos = new ArrayList<double[]>();
	public static double[][][][] positions = new double[1][1][1][1];
	Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
	int tickCount = 0;
	int frames = 0;

	public Server(int port) {
		Server.port = port;
		doStuff();
	}
	
	public void doStuff(){

		f.setTitle("Server");
		f.setBounds(ss.width - 750, 50, 701, 350);
		f.setResizable(false);
		f.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		f.setAlwaysOnTop(true);
		f.setLayout(null);
		f.add(p);
		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				if (connection) {
					try {
						socket.close();
						serverSocket.close();
					} catch (IOException e) {
					}
				}
				System.exit(0);
			}
		});

		f.add(sc);
		sc.setBounds(151, 0, 560, 300);
		jt.setBackground(Color.BLACK);
		jt.setForeground(Color.WHITE);
		jt.setLineWrap(true);
		jt.setEditable(false);
		jt.setFont(new Font("Arial", Font.PLAIN, 12));
		append("Hello World");

		f.add(info);
		info.setOpaque(true);
		info.setBounds(0, 0, 150, 19);
		info.setBackground(Color.RED);
		info.setText("Clients Connected: " + clients.size());

		f.add(tf);
		tf.setBounds(151, 298, 544, 23);
		tf.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tftext = tf.getText();
				RunCommand(tftext);
				tf.setText("/");
			}
		});
		tf.setBackground(Color.BLACK);
		tf.setForeground(Color.WHITE);

		f.getContentPane().setBackground(Color.BLACK);
		f.setVisible(true);
	}
	
	public synchronized void start() {
		if (run)
			return;
		run = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public synchronized void stop() {
		if (!run)
			return;
		run = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	protected void RunCommand(String txt) {
		serverMessage("'" + txt.replace("/", "") + "'\n");
		if (txt.contains("/")) {
			if (txt.equals("/")) {
				return;
			}
			String t = txt.toLowerCase().replace("/", "");
			if (t.contains("setshowtime")) {
				if (t.endsWith(" true")) {
					showtimestamp = true;
					append("ShowTimeStamp - true");
				} else if (t.endsWith(" false")) {
					showtimestamp = false;
					append("ShowTimeStamp - false");
				} else {
					append(error + baduse + " '" + txt + "' "
							+ "\nCorrect Use: SetShowTime true/false");
				}
			} else if (t.contains("say ")) {
				message(t.replace("say ", ""));
				t = "";
				RestartServer();
			} else if (t.contains("hello")) {
				append("Hi " + System.getProperty("user.name"));
			} else if (t.contains("stop")) {
				serverMessage("Server Starting\nInitialising socket connections:");
				serverisrunning = false;
			} else if (t.contains("play")) {
				Display.getLauncherInstance().skipasadmin = true;
			} else if (t.contains("spectate")) {
				Launcher l = Display.getLauncherInstance();
				l.skipasspectator = true;
			} else if (t.contains("kick")) {
				int indexOf = t.indexOf('c');
				String name = t.substring(indexOf + 2).trim();
				serverMessage("Kicking Player " + name);
				for (int i = 0; i < clientnames.size(); i++) {
					if (clientnames.get(i).toLowerCase().contains(name)) {
						append("\n'" + name + "' was found: "
								+ clientnames.get(i));
						p.remove(clients.get(i).clientinfo);
						p.revalidate();
						f.validate();
						f.repaint();
						clients.get(i).clientinfo.setVisible(false);
						clients.remove(i);
						clientnames.remove(i);
						info.setText("Clients Connected: " + clients.size());
					}
				}
			} else {// if none of the other commands
				append(error + "'" + t + "'" + badcom);
			}
		} else {
			// dunno
		}
	}
	
	public static void removeClient(int i) throws Exception {
		p.remove(clients.get(i).clientinfo);
		p.revalidate();
		f.validate();
		f.repaint();
		clients.get(i).clientinfo.setVisible(false);
		clients.remove(i);
		clientnames.remove(i);
		clientpos.remove(i);
		info.setText("Clients Connected: " + clients.size());
		
		//for(int ii = i+1; ii < clients.size()-1; ii++){
		//	EchoThread c2 = clients.get(ii);
		//	clients.set(ii-1, c2);
		//}
	}

	public void message(String msg) {
		timeStamp = new SimpleDateFormat("HH:mm-ss").format(Calendar
				.getInstance().getTime());
		jt.append(" [" + timeStamp + "] " + msg);
		jt.append("\n" + defaulttext);
		jt.setCaretPosition(jt.getDocument().getLength());
	}

	public void append(String text) {
		timeStamp = new SimpleDateFormat("HH:mm-ss").format(Calendar
				.getInstance().getTime());
		if (showtimestamp) {
			jt.append(" [" + timeStamp + "] ");
		}
		jt.append(text);
		jt.append("\n" + defaulttext);
		jt.setCaretPosition(jt.getDocument().getLength());
	}

	public static void serverMessage(String text) {
		jt.append(text);
		jt.setCaretPosition(jt.getDocument().getLength());
	}
	
	@Override
	public void run() {
		serverMessage("Loading...\n");
		for(int b = 0; b < 231; b++){
			for(int a = 0; a < 1000000; a++){
				if(a % 1000000 == 0){
					//serverMessage("#");
				}
			}
		}
		serverMessage("Done!\n");
		
		try{
		serverSocket = new ServerSocket(port);
		serverSocket.setPerformancePreferences(0, 1, 2);
		append("\nPort: " + port + "\nLocal IP address: "
				+ InetAddress.getLocalHost().getHostAddress()
				+ "\nExternal IP address: " + getIp()
				+ "\nType 'stop' to stop listening for clients...\n"
				+ "Server Buffer Size: "+serverSocket.getReceiveBufferSize());
		}catch(Exception e){
		}
		
		double unprocessedSeconds = 0;
		long previousTime = System.nanoTime();
		double secondsPerTick = 1 / Display.gametickrate;
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
				
				if(tickCount % Display.networktickrate == 0){
				networkUpdate();
				}
				
				if (tickCount % Display.gametickrate == 0) {
					previousTime += 1000;
					frames = 0;
				}
				if (ticked) {
				}
			}
		}
	}
	
	private void networkUpdate() {

	}

	private void tick() {
		
	}

	public void Update() {
		try {
			if (serverisrunning) {
				if (clients.size() < 15) {
					socket = serverSocket.accept();

					inStream = new ObjectInputStream(socket.getInputStream());// recieve username
					String name = (String) inStream.readObject();
					
					clientnames.add(name);
					System.out.println(name + " Connected");
					
					outStream = new ObjectOutputStream(socket.getOutputStream());// send client number
					outStream.writeObject(clients.size() + 1);
					
					clientpos.add(new double[4]);
					
					clients.add(new EchoThread(socket));
					clients.get(clients.size() - 1).canUpdate = true;
					clients.get(clients.size() - 1).start();
					clients.get(clients.size() - 1).clientindex = (clients
							.size());
					clients.get(clients.size() - 1).clientname = clientnames
							.get((clients.size() - 1));
					
					
					connection = true;

					timeStamp = new SimpleDateFormat("HH:mm-ss")
							.format(Calendar.getInstance().getTime());
					append("Client Connected: " + clients.size() + "\n"
							+ clients + "\n" + "Client IP: "
							+ socket.getRemoteSocketAddress() + "\n\n###### "
							+ name + " Connected " + timeStamp + " ######\n");
				}

				info.setText("Clients Connected: " + clients.size());
			}

		} catch (Exception e) {
			RestartServer();
		}
	}

	public static String getIp() {
		URL whatismyip;
		try {
			whatismyip = new URL("http://checkip.amazonaws.com");

			BufferedReader in = null;
			in = new BufferedReader(new InputStreamReader(
					whatismyip.openStream()));
			String ip = in.readLine();

			if (in != null) {
				in.close();
			}

			return ip;
		} catch (Exception e) {
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public static void RestartServer(){
		serverMessage("Restarting...");
		f.dispose();
		new Server(Integer.parseInt(serverport)).start();
		thread.stop();
	}

	public static void main(String[] args) {
		//if(Display.WINDOW_FAST_JOIN != 1){
		//serverport = JOptionPane.showInputDialog("Enter Port: ");
		//}
		new Server(Integer.parseInt(serverport)).start();
	}
}