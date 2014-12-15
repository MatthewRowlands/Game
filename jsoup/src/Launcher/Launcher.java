package Launcher;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import Connection.Server;
import Input.InputHandler;
import Main.Display;
import Main.RunGame;

public class Launcher extends Canvas implements Runnable{
	private static final long serialVersionUID = 1L;

	protected JPanel window = new JPanel();
	
	private int width = 800;
	private int height = 400;
	protected int button_width = 80;
	protected int button_height = 40;
	int MousePressed = 0;
	
	private boolean running = false;
	public boolean skipasadmin = false;
	public boolean skipasspectator = false;
	
	Configuration config = new Configuration();
	Save save = new Save();
	Thread thread;
	JFrame frame = new JFrame();
	Options opt;
	
	public Launcher(){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e){
			e.printStackTrace();
		}
		frame.setUndecorated(true);
		frame.setTitle("Launcher");
		frame.setSize(new Dimension(width, height));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
		window.setLayout(null);	
		InputHandler input = new InputHandler();
		addKeyListener(input);
		addFocusListener(input);
		addMouseListener(input);
		addMouseMotionListener(input);
		frame.repaint();
	}
	
	public void updateFrame(){
		if(InputHandler.dragged){
			Point p = frame.getLocation();
			frame.setLocation(p.x + InputHandler.mouseDX - InputHandler.mousePX, p.y + InputHandler.mouseDY - InputHandler.mousePY);
		}
		if(InputHandler.keyCode == KeyEvent.VK_F1){
			System.exit(0);
		}
	}
	
	public void startMenu(){
		running = true;
		thread = new Thread(this, "Menu");
		thread.start();
	}
	
	public void stopMenu(){
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		if(skipasadmin){
			frame.dispose();
			System.out.println("Window closed");
			StartGame(true, "localHost", "Admin", Server.port);
			stopMenu();
		}
		running = true;
		requestFocus();
		while(running){
			try{
			renderMenu();
			}catch (Exception e){
			}
			updateFrame();
		}	
	}

	public void renderMenu() throws Exception{
		BufferStrategy bufferStrategy = this.getBufferStrategy();

		if (bufferStrategy == null) {
			this.createBufferStrategy(2);
			return;
		}
		Graphics g = bufferStrategy.getDrawGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 800, 400);
		
		MousePressed = InputHandler.MouseButton;
		try {
			g.drawImage(ImageIO.read(Display.class.getResource("/Menuimg.jpg")), 0, 0, 800, 400, null);
			
			Color c = new Color(255, 255, 255, 90 );
			
			g.setColor(Color.RED);
			g.drawString("< Press F1 At Any Time To Exit >", 20, 380);
			
			g.setFont(new Font("Verdana", 0, 24));
			g.setColor(Color.GRAY);
			g.drawString("Singleplayer", 600, 120);
			g.drawString("Multiplayer", 600, 160);
			g.drawString("Host Game", 600, 200);
			g.drawString("Loadout", 600, 240);
			g.drawString("Options", 600, 280);
			g.drawString("Exit", 600, 320);
			
			if(InputHandler.mouseX >= 500 && InputHandler.mouseX <= 760){
				if(InputHandler.mouseY > 85 && InputHandler.mouseY <= 125){
					g.setColor(c);
					g.fillRect(500,95,260,30);
						g.drawImage(ImageIO.read(Display.class.getResource("/Menuarrow.png")), 760, 97, 40, 25, null);
						g.setColor(Color.white);
						g.drawString("Singleplayer", 600, 120);
					if(MousePressed == 1){
						Display.PlaySound("/audio/Tick.wav");
						InputHandler.MouseButton = 0;
						StartGame(false, "localHost", "BOB", 1500);
					}
				}
				if(InputHandler.mouseY > 125 && InputHandler.mouseY <= 165){
					g.setColor(c);
					g.fillRect(500,135,260,30);
						g.drawImage(ImageIO.read(Display.class.getResource("/Menuarrow.png")), 760, 137, 40, 25, null);
						g.setColor(Color.white);
						g.drawString("Multiplayer", 600, 160);
					if(MousePressed == 1){
						InputHandler.MouseButton = 0;
						frame.dispose();
						String ip = "localHost";
						String un = "TEST_PLAYER";
						String port = Display.DEFAULT_PORT;
						if(Display.WINDOW_FAST_JOIN == 0){
						ip = JOptionPane.showInputDialog("Enter IP: ");
						port = JOptionPane.showInputDialog("Enter Port: ");
						}
						un = JOptionPane.showInputDialog("Enter Username: ");
						try{
						StartGame(true, ip, un, Integer.parseInt(port));
						}catch (Exception e){
							System.err.println("Invalid Entry\n> "+port);
							frame.dispose();
							new Launcher().startMenu();
						}
					}
				}
				if(InputHandler.mouseY > 165 && InputHandler.mouseY <= 205){
					g.setColor(c);
					g.fillRect(500,175,260,30);
						g.drawImage(ImageIO.read(Display.class.getResource("/Menuarrow.png")), 760, 177, 40, 25, null);
						g.setColor(Color.white);
						g.drawString("Host Game", 600, 200);
					if(MousePressed == 1){
						InputHandler.MouseButton = 0;
						frame.dispose();
						Display.launcher = this;
						try{
							String port = Display.DEFAULT_PORT;
							if(Display.WINDOW_FAST_JOIN != 1){
							port = JOptionPane.showInputDialog("Enter Port: ");
							}
							new Server(Integer.parseInt(port)).start();
						}catch (Exception e){
							frame.dispose();
							new Launcher().startMenu();
						}
						stopMenu();
					}
				}
				if(InputHandler.mouseY > 205 && InputHandler.mouseY <= 245){
					g.setColor(c);
					g.fillRect(500,215,260,30);
						g.drawImage(ImageIO.read(Display.class.getResource("/Menuarrow.png")), 760, 217, 40, 25, null);
						g.setColor(Color.white);
						g.drawString("Loadout", 600, 240);
					if(MousePressed == 1){
						InputHandler.MouseButton = 0;
						frame.dispose();
						new Loadout();
						stopMenu();
					}
				}
				if(InputHandler.mouseY > 245 && InputHandler.mouseY <= 285){
					g.setColor(c);
					g.fillRect(500,255,260,30);
						g.drawImage(ImageIO.read(Display.class.getResource("/Menuarrow.png")), 760, 257, 40, 25, null);
						g.setColor(Color.white);
						g.drawString("Options", 600, 280);
					if(MousePressed == 1){
						InputHandler.MouseButton = 0;
						frame.dispose();
						new Options();
						stopMenu();
					}
				}
				if(InputHandler.mouseY > 285 && InputHandler.mouseY <= 325){
					g.setColor(c);
					g.fillRect(500,295,260,30);
						g.drawImage(ImageIO.read(Display.class.getResource("/Menuarrow.png")), 760, 297, 40, 25, null);
						g.setColor(Color.white);
						g.drawString("Exit", 600, 320);
					if(MousePressed == 1){
						System.exit(0);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		g.dispose();
		bufferStrategy.show();
	}
	
	public void StartGame(boolean multiplayer, String ip, String un, int port){
		frame.dispose();
		config.loadConfiguration("res/settings/config.xml");
		save.loadConfiguration("res/settings/loadout.xml");
		new RunGame(multiplayer, ip, un, port);
	}
}
