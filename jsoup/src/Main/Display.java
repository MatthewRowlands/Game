package Main;

import java.awt.BorderLayout;
import java.awt.BufferCapabilities;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.UIManager;

import Connection.Client;
import Entity.Enemy;
import Entity.Objects;
import Entity.Weapon;
import Graphics.Screen;
import Input.Controller;
import Input.InputHandler;
import Launcher.Launcher;
import Launcher.Options;
import Log.Dump;
import Log.Log;
import Logic.WeaponLogic;
import Model.Model;

public class Display extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	
	public static double gametickrate = 120.0;
	public static double networktickrate = 1.0;
	public boolean VSYNC = false;
	public static String DEFAULT_PORT = "12500";
	public boolean FIX_MOUSE = false;
	public boolean flymode = false;
	
	public static int PING = 0;
	public int ping = 0;
	int time = 0;
	int tickCount = 0;
	int frames = 0;
	int ups = 0;
	float fps = 0;
	long fpstime = 0;
	long totalfps = 0;
	public float averagefps = 0;
	public long t1=0;
	public long t2=0;
	public double[] pings = new double[16];
	
	public boolean canUpdate = false;
	
	public static Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
	public static int w = ss.width;
	public static int h = ss.height;
	public static int width = w / 2;
	public static int height = h / 2;
	public static Launcher launcher;
	public JFrame f;
	public String title = "3D Game";
	public Client client;
	public Screen screen;
	public boolean run = false;
	private Game game;
	private Thread thread;
	private BufferedImage img;
	private int[] pixels;
	
	protected Robot r;
	private InputHandler input;
	private int newmX = 0;
	private int newmY = 0;
	public double MouseChangex;
	public double MouseChangey;
	public int selection = 0;
	public int blockcount=0;
	public double x = 0;
	public double y = 0;
	public double z = 0;
	public boolean Pause = false;	
	boolean MousePressed = false;
	public boolean canfire = true;
	boolean alreadydone = false;
	boolean acc = false;

	public double rotationcos = 0;
	public double rotationsin = 0;
	public double rotation = 0;
	public double rotationy = 0;
	public boolean collisionleft = false;
	public boolean collisionfront = false;
	public boolean collisionright = false;
	public boolean collisionback = false;
	
	public static Weapon w1 = new Weapon(1);
	public static Weapon w2 = new Weapon(3);
	int wep = 1;
	public double recoil = (startaccuracy * startaccuracy)*getCurrentWeapon().recoil;	
	public double maxrecoil = recoil * 4;
	public static double initialaccuracy = w1.accuracy;
	public static double startaccuracy = initialaccuracy;
	public static double accuracy = startaccuracy;
	public static double firerate = w1.firerate;
	public static double WeaponDamage = w1.WeaponDamage;
	public static int firemode = w1.firemode;
	public int sFlashAmmo = 3;
	public static int sWeaponAmmo = w1.WeaponAmmo;
	public int FlashAmmo = sFlashAmmo;
	public static int WeaponAmmo = sWeaponAmmo;
	public int activebullets = 0;
	public boolean Reload = false;
	public long guntime = System.currentTimeMillis();
	public long flashtime = System.currentTimeMillis();
	public long reloadtime = System.currentTimeMillis();
	public static double reloadspeed = w1.reloadspeed*1000;
	public boolean reloading = false;
	public boolean donereloadsound = false;

	public int ScrollLevel = 8;
	public int brightness = 20000;
	public static boolean fullscreen = true;
	public double floorpos = 8;
	public double ceilingpos = 2048/2;
	public static double MoveSpeed = 1;
	public static double JumpHeight = 1;
	public static double MouseSpeed = 5;
	public int RenderDist = 4000000;
	public double gravity = 9.81;

	int Armor = 100;
	int StartArmor = 100;
	public int enemiesattacking = 0;
	public int kills = 0;
	public double StartHEALTH = 30000;
	public double HEALTH = 30000;
	
	BufferedImage cursor = new BufferedImage(16, 16,
			BufferedImage.TYPE_INT_ARGB);
	Cursor blank = Toolkit.getDefaultToolkit().createCustomCursor(
			cursor, new Point(0, 0), "blank");
	WeaponLogic wl = new WeaponLogic(this);
	NetworkThread nw;
	AnimationThread a;
	GraphicsDevice gd = GraphicsEnvironment
			.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	GraphicsConfiguration gc = gd.getDefaultConfiguration();
	BufferCapabilities bufferCapabilities;
	public BufferStrategy bufferStrategy;
	
	public Display(JFrame f, int width, int height) {
		Display.width = width; 
		Display.height = height;
		this.f = f;
		f.setUndecorated(true);
		if (gd.isFullScreenSupported() && fullscreen) {
			gd.setFullScreenWindow(f);
			DisplayMode dm = new DisplayMode(width, height, 32,
					DisplayMode.REFRESH_RATE_UNKNOWN);
			gd.setDisplayMode(dm);
			FIX_MOUSE = false;
		} else {
			f.setSize(getGameWidth(), getGameHeight());
			f.setTitle(title+" ");
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setLocationRelativeTo(null);
			f.setVisible(true);
		}

		f.setVisible(true);
		
		f.createBufferStrategy(3);
		bufferStrategy = f.getBufferStrategy();
		bufferCapabilities = gc.getBufferCapabilities();
		acc = gc.getBufferCapabilities().getBackBufferCapabilities()
				.isAccelerated();
	}
	public void startgame(){
		try{
			if (gd.isFullScreenSupported() && fullscreen) {
			DisplayMode dm = new DisplayMode(width, height, 32,
					DisplayMode.REFRESH_RATE_UNKNOWN);
			gd.setDisplayMode(dm);
			} else {
				f.setSize(getGameWidth(), getGameHeight());
				f.setTitle(title+" ");
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setLocationRelativeTo(null);
				f.setVisible(true);
			}

			f.setVisible(true);
			
			f.createBufferStrategy(3);
			bufferStrategy = f.getBufferStrategy();
			bufferCapabilities = gc.getBufferCapabilities();
			acc = gc.getBufferCapabilities().getBackBufferCapabilities()
					.isAccelerated();
			
			f.getContentPane().setCursor(blank);
			
			Dimension size = new Dimension(WIDTH, HEIGHT);
			setPreferredSize(size);
			setMinimumSize(size);
			setMaximumSize(size);
			screen = new Screen(getGameWidth(), getGameHeight(), this);
			game = new Game(this);
			img = gc.createCompatibleImage(getGameWidth(), getGameHeight());
			//VolatileImage vimg = gc.createCompatibleVolatileImage(getGameWidth(), getGameHeight());//?????????????????????????????????????????????????????
			pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
			
			input = new InputHandler(this);
			f.addKeyListener(input);
			f.addFocusListener(input);
			f.addMouseListener(input);
			f.addMouseMotionListener(input);
			f.addMouseWheelListener(input);

			r = new Robot();
		
			nw = new NetworkThread();
			if(canUpdate)nw.start();
			a = new AnimationThread();
			a.start();
			f.requestFocus();
		}catch (Exception e){
			e.printStackTrace();
		}
		start();
	}
	class NetworkThread extends Thread {
		int ticks = 0;
		public void run(){
			ActionListener actListner = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					ticks += 1;
					networkUpdate();
					if (ticks == networktickrate) {
						ticks = 0;
					}
				}
			};

			Timer timer = new Timer((int) (1000 / networktickrate), actListner);
			timer.start();
		}
	}
	public static Launcher getLauncherInstance() {
		if (launcher == null) {
			launcher = new Launcher();
		}
		return launcher;
	}
	public JFrame getFrame() {
		if (f == null) {
			f = new JFrame();
		}
		return f;
	}
	public void pause() {
		Pause ^= true;
		if (Pause) {
			getFrame().getContentPane().setCursor(Cursor.getDefaultCursor());
		} else {
			getFrame().getContentPane().setCursor(blank);
		}
	}
	public int getGameWidth() {
		return width;
	}
	public int getGameHeight() {
		return height;
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
			Dump.Dump(e.toString());
			System.exit(1);
		}
	}
	@Override
	public void run() {
		double unprocessedSeconds = 0;
		long previousTime = System.nanoTime();
		double secondsPerTick = 1 / gametickrate;
		boolean ticked = false;
		r.mouseMove((w / 2), (h / 2));
		
		while (run && !thread.isInterrupted()) {
			if(!VSYNC){
			}
			screen.CheckCollision(1);
			frames++;
			long currentTime = System.nanoTime();
			long passedTime = currentTime - previousTime;
			previousTime = currentTime;
			unprocessedSeconds += passedTime / 1000000000.0;
			requestFocus();
			
			while (unprocessedSeconds > secondsPerTick) {
				if(VSYNC){
				}
				tick(120);
				unprocessedSeconds -= secondsPerTick;
				ticked = true;
				
				fps = 1e9f / fpstime;
				
				if (tickCount % gametickrate == 0) {
					/*double x = this.x + Math.sin(Math.random()*500)*500;
					double z = this.z + Math.sin(Math.random()*500)*500;*/
					for(Objects o : screen.objects){
						if(o.spawner)
							screen.enemies.add(new Enemy(o.x,o.y,o.z, this));
					}
					PING = ping;
					ups = frames;
					previousTime += 1000;
					frames = 0;
					time++;
				}
				if (ticked) {
				}
			}
		}
		f.dispose();
	}
	private void networkUpdate() {
		screen.positions = client.positions;
	}
	public static void setMoveSpeed(int movespeed){
		MoveSpeed = movespeed;
	}
	public static void setJumpHeight(int jumpheight){
		JumpHeight = jumpheight;
	}
	public static void setMouseSpeed(int mousespeed) {
		MouseSpeed = mousespeed;
	}
	public void tick(int ups) {
		if(Math.abs(accuracy) > maxrecoil+startaccuracy) accuracy = maxrecoil+startaccuracy;
		recoil = Math.abs((startaccuracy/64) - (initialaccuracy/32))*getCurrentWeapon().recoil;	
		maxrecoil = recoil * 3;
		
		tickCount++;
		game.tick(input.key, ups);
		
		if(!Pause){
			newmX = InputHandler.mouseX;
			newmY = InputHandler.mouseY;
	
			if (newmX > w/2) {
				Controller.turnright = true;
			}
			else if (newmX < w/2) {
				Controller.turnleft = true;
			}
			else if (newmX == w/2) {
				Controller.turnleft = false;
				Controller.turnright = false;
			}
			if (newmY > h/2) {
				Controller.turndown = true;
			}
			else if (newmY < h/2) {
				Controller.turnup = true;
			}
			else if (newmY == h/2) {
				Controller.turndown = false;
				Controller.turnup = false;
			}
	
			MouseChangex = (width/2) - newmX;
			if(getGameHeight() != ss.height)
				MouseChangey = (height/2) - newmY + (fullscreen? 0 : 15);
			else
				MouseChangey = Math.abs((height/2) - newmY + (fullscreen? 0 : 15));
			
			if(!fullscreen)
				r.mouseMove((w / 2), (h / 2));
			else
				r.mouseMove((width / 2), (height / 2));
			
			ShootingMechanism();
			if(HEALTH < StartHEALTH && HEALTH > 0){
			HEALTH+=0.1/(ups/60);
			}else{
				if(HEALTH > StartHEALTH)
				HEALTH = StartHEALTH;
			}
		}
		screen.tick(ups);
	}
	private void ShootingMechanism() {
		if(WeaponAmmo > 0 && !reloading){
			if(MousePressed && firemode == 1 && InputHandler.MouseButton == 1){
				wl.SemiAutoFire(1/firerate);
			}
			if(MousePressed && firemode > 4 && firemode != 200 && InputHandler.MouseButton == 1){
				if(firerate < 3){
					wl.ShotgunSemiFire(1/firerate);
				}else{
					wl.ShotgunFullFire(1/firerate);
				}
			}
			if(MousePressed && firemode == 2 && InputHandler.MouseButton == 1){
				wl.FullAutoFire(1/firerate);
			}
			if(MousePressed && firemode == 200 && InputHandler.MouseButton == 1){
				wl.Circle(1/firerate);
			}
			WeaponAmmo = getCurrentWeapon().getRemainingAmmo();
		}else{
			Reload();
		}
		if(FlashAmmo > 0){
			if(MousePressed && InputHandler.MouseButton == 3 && FlashAmmo > 0){
				wl.ThrowFlashBang();
				//screen.objects.add(new Objects(x, y, z, this));
			}
		}
		if(!MousePressed){
			canfire = true;
			if(accuracy > startaccuracy){
				accuracy-= 0.005;
			}else{
				accuracy = startaccuracy;
			}
		}	
		if(InputHandler.MouseButton == 0){
			MousePressed = false;
		}		
	}
	public void Reload() {
		if(accuracy > startaccuracy)
			accuracy-= 0.015;
		else
			accuracy = startaccuracy;
		if(!reloading){
			reloadtime = System.currentTimeMillis();
			reloading = true;
		}
		if(reloading){
			if((System.currentTimeMillis()-reloadtime) > (reloadspeed-1000) && !donereloadsound){
				PlaySound("/Audio/Reload.wav");
				donereloadsound = true;
			}
			if((System.currentTimeMillis()-reloadtime) > reloadspeed){
				reloading = false;
				donereloadsound = false;
				WeaponAmmo = sWeaponAmmo;
				getCurrentWeapon().remainingammo = WeaponAmmo;
			}
		}
	}
	public static void PlaySound(String file) {
		Clip clip = null;
	    try {
	        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Display.class.getResource(file));
	        clip = AudioSystem.getClip();
	        clip.open(audioInputStream);	        
	        clip.start();
	    } catch(Exception e) {
	    	Log.Log(e.toString(), false);
	    }
	}	
	public void ChangeWeapon(int weaponnum){
		reloading = false;
		if(weaponnum == 1){
			wep = 1;
		}else if(weaponnum == 2){
			wep = 2;
		}else{
			if(wep == 1){
				wep = 2;
			}else if(wep == 2){
				wep = 1;
			}else{
				wep = 1;
			}
		}
		
		Weapon w = getCurrentWeapon();
		initialaccuracy = w.accuracy;
		startaccuracy = initialaccuracy;
		accuracy = startaccuracy;
		firerate = w.firerate;
		firemode = w.firemode;
		sWeaponAmmo = w.WeaponAmmo;
		WeaponAmmo = sWeaponAmmo;
		reloadspeed = w.reloadspeed * 1000;
		WeaponDamage = w.WeaponDamage;
		WeaponAmmo = w.getRemainingAmmo();
	}
	public Weapon getCurrentWeapon(){
		if(wep == 1){
			return w1;
		}
		else if(wep == 2){
			return w2;
		}
		else{
			return null;
		}
	}
	public void render(Graphics2D g) {
		screen.render(game);
		
		for (int i = 0; i < getGameWidth() * getGameHeight(); i++) {
			pixels[i] = screen.pixels[i];
		}

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, w, h);
		g.drawImage(img, 0, 0, getGameWidth() + 10, getGameHeight() + 10, null);
		
		if(HEALTH <= 0){
			HEALTH = 0;
			g.setColor(Color.RED);
			g.setFont(new Font("Chiller", Font.PLAIN, 72));
			g.drawString("YOU DIED", width/2-100, height/2-240);
			if(!Pause)
			pause();
		}
		
		if(!Pause){	
		drawInfoBoardNorth(g);
		drawHUD(g);
		//drawInfoBoardSouth(g);
		//drawMiniMap(g);
		//drawRotationMap(g);
		}
		if (Pause)
			drawPauseMenu(g);

		//g.dispose();
		//bufferStrategy.show();
	}
	private void drawInfoBoardNorth(Graphics2D g) {
		g.setFont(new Font("Verdana", Font.PLAIN, 10));
		g.setColor(Color.WHITE);
		g.drawString("UPS: " + ups+" Ping: "+PING+" Threads: "+Thread.activeCount(), 20, 20);
		g.drawString("X/Y/Z: "+x+","+y+","+z+" Rotation C/S: "+rotationsin+"/"+rotationcos, 20, 30);
		g.drawString("Textures: "+blockcount, 20, 40);
		g.drawString("VSYNC: "+VSYNC, 20, 50);
		g.drawString("Weapong Firerate: "+firerate, 20, 80);
		g.drawString("Active Bullets: "+activebullets, 20, 90);
		g.drawString("Flash Grenades: "+FlashAmmo, 20, 100);
		g.drawString("Weapon Ammo: "+WeaponAmmo, 20, 110);
		g.drawString("Reloading: "+reloading, 20, 120);
		g.drawString("Enemies Attacking: "+enemiesattacking, 20, 130);
		g.drawString("Right: "+collisionright+" Left: "+collisionleft+" Front: "+collisionfront+" Back: "+collisionback, 20, 140);
		for(int i = 0; i < screen.positions.size(); i++){
			double[] v3f = new double[4];
			v3f[0] = screen.positions.get(i)[0];	
			v3f[1] = screen.positions.get(i)[1];
			v3f[2] = screen.positions.get(i)[2];
			v3f[3] = screen.positions.get(i)[3];
			if(game.time % gametickrate == 0){
				pings[i] = v3f[3];
			}
			
			g.drawString("Player["+(i+1)+"] x:"
			+v3f[0]+" y:"
			+v3f[1]+" z:"
			+v3f[2]+" Ping: "+pings[i], 
			20, 150+(i*10));
		}	
		
		g.setColor((fps > 60? Color.GREEN : Color.RED));
		g.drawString("FPS: " +(int)fps, 20, 200);
		g.setColor((acc? Color.GREEN : Color.RED));
		g.drawString("Accelerated: "+(acc? "Yes" : "No"), 80, 200);
		
		//g.setColor(Color.BLACK);
		//g.fillRect(0, 220, 300, height-60 - 210);
		
		g.setColor((screen.enemies.size() < 1000? Color.CYAN : Color.RED));
		g.drawString("Enemies Size: " +screen.enemies.size(), 20, 240);
		g.setColor((screen.objects.size() < 1000? Color.CYAN : Color.RED));
		g.drawString("Objects Size: " +screen.objects.size(), 20, 260);
		g.setColor((screen.models.size() < 1000? Color.CYAN : Color.RED));
		g.drawString("Models Size: " +screen.models.size(), 20, 280);	
		g.setColor((screen.bullets.size() < 1000? Color.CYAN : Color.RED));
		g.drawString("Bullets Size: " +screen.bullets.size(), 20, 300);

/*		for(Iterator<Objects> iterator = screen.bullets.iterator(); iterator.hasNext();){
			Objects b = iterator.next();
			int index = screen.bullets.indexOf(b)+1;
			if(300+(index*10) < height-70){
			g.setColor(Color.BLUE);
			g.drawString("[Bullet - "+index+"] "+b.distancetravelled, 20, 300+(index*10));
			}else{
				b = screen.bullets.get(screen.bullets.size()-1);
				g.setColor(Color.BLUE);
				g.drawString("...", 20, height-70);
				g.drawString("[Bullet - "+screen.bullets.size()+"] "+b.distancetravelled, 20, height-60);
			}
		}*/
	}
	private void drawHUD(Graphics2D g) {
		double accuracy = initialaccuracy * 100;
		double acc = this.accuracy * 300;
		Color cc = Color.CYAN;
		int xSize = 5;
		int ySize = 1;
		Rectangle left, right, top, bottom;
		left = new Rectangle((int) (width/2 - accuracy * xSize)-xSize,(int)(height/2), xSize, ySize);
		right = new Rectangle((int)(width/2 + accuracy * xSize)+2,      (int)(height/2), xSize, ySize);
		top = new Rectangle((int) (width/2),(int)(height/2 - accuracy * xSize)-xSize, ySize, xSize);
		bottom = new Rectangle((int) (width/2),(int)(height/2 + accuracy * xSize)+2,       ySize, xSize);
		g.setColor(cc); 
		g.fill(left);
		g.fill(right);
		g.fill(top);
		g.fill(bottom);
		g.setColor(cc);
		g.fillRect(width/2, height/2, 1, 1);//middle dot
		g.fillRect(width/2 - 2, (int) (height/2 -3 -acc), 5, 1);//top
		g.fillRect(width/2 - 2, (int) (height/2 +4 +acc), 5, 1);//bottom
		g.fillRect((int)(width/2 - 3 -acc), height/2 -2, 1, 5);
		g.fillRect((int)(width/2 + 4 +acc), height/2 -2, 1, 5);
		
        g.setColor(new Color(0, 0, 0, 0.8f));
        g.fillRect(0, height - 50, (300 + width/7)/2, 50);
        g.setPaint(new GradientPaint(
       	     new Point((300 + width/7)/2, height - 60), 
       	     new Color(0.0f, 0.0f, 0.0f, 0.8f), 
       	     new Point(300 + width/7, height - 60), 
       	     new Color(0.0f, 0.0f, 0.0f, 0.0f)));
        g.fillRect((300 + width/7)/2, height - 50, (300 + width/7)/2, 50);
       
		g.setColor((HEALTH/StartHEALTH)*100 <= 34 ? Color.BLACK : Color.GRAY);
		g.fillRect(22, height-40, 12, 36);
		g.fillRect(10, height-28, 36, 12);
		g.setColor((HEALTH/StartHEALTH)*100 <= 34 ? Color.RED : Color.WHITE);
		g.fillRect(24, height-38, 8, 32);
		g.fillRect(12, height-26, 32, 8);
		g.setFont(new Font("Verdana", Font.BOLD, 24));
		g.drawString(""+(int)((HEALTH/StartHEALTH)*100), 65-(""+(int)((HEALTH/StartHEALTH)*100)).length()*4, height-13);
		bar(g, 120, height - 26, width/14, height/90, HEALTH, StartHEALTH, g.getColor());
		
		//try {
		//	//g.drawImage(ImageIO.read(Display.class.getResource((Armor <= 0)?"/Textures/sheildbroke.png" : "/Textures/sheild.png")), 120 + width/14 + 10, height-48, 48, 48, null);
		//} catch (IOException e) {}
		g.setColor((Armor/StartArmor)*100 <= 0 ? Color.GRAY : Color.WHITE);
		g.setFont(new Font("Verdana", Font.BOLD, 24));
		g.drawString(""+(int)((Armor/StartArmor)*100), 120 + width/14 + 76-(""+(int)((Armor/StartArmor)*100)).length()*4, height-13);
		bar(g, 120 + width/14 + 140, height - 26, width/14, height/90, Armor, StartArmor, g.getColor());
		
		int heightt = height/2-100;
		if(wep==2) heightt = height/2-20;
        g.setPaint(new GradientPaint(
       	     new Point(width, heightt), 
       	     new Color(0.2f, 0.2f, 0.2f, 0.4f), 
       	     new Point(width-500, heightt), 
       	     new Color(0.2f, 0.2f, 0.2f, 0.0f)));
        g.fillRect(width-500, heightt, 500, 80);
        g.setFont(new Font("Verdana", Font.PLAIN, 18));
        g.setColor(Color.WHITE);
        g.drawString("1", width-20, height/2-80);
        g.drawString("2", width-20, height/2);
        if(wep == 1)
        	g.drawString(""+w1.name, width-(w1.name.length()*18)+10, heightt+70);
        else if (wep == 2)
        	g.drawString(""+w2.name, width-(w2.name.length()*18)+10, heightt+70);
        
		//try {
		//	g.drawImage(ImageIO.read(Display.class.getResource("/Textures/skullbanner.png")), width - 202, 200, 180, 180, null);
		//} catch (IOException e) {}
		g.setFont(new Font("Chiller", Font.BOLD, 20));
		g.setColor(Color.RED);
    	g.drawString("x"+kills, width-150, 325);
	}
	public void bar(Graphics g, int x, int y, int xSize, int ySize, double numcurrent, double nummax, Color c){
		g.setColor((c.equals(Color.WHITE))? Color.GRAY : Color.BLACK);
		g.fillRect(x-(int)(ySize/5), y-(int)(ySize/5), xSize+(int)(ySize/2.5), ySize+(int)(ySize/2.5));
		g.setColor(c);
		g.fillRect(x, y, (int)((numcurrent/nummax) * xSize), ySize);
	}
	private void drawInfoBoardSouth(Graphics2D g) {
		int centrex = width - 100;
		int centrey = height - 100;
		g.setColor(Color.WHITE);
		g.drawString("Current Weapon: "+getCurrentWeapon().name, centrex-100, centrey-200);
		g.drawString("Damage: "+getCurrentWeapon().WeaponDamage, centrex-100, centrey-185);
		g.drawString("Rounds Per Second: "+getCurrentWeapon().firerate, centrex-100, centrey-170);
		g.drawString("Ammo: "+WeaponAmmo+"/"+getCurrentWeapon().WeaponAmmo, centrex-100, centrey-155);
		g.drawString("Reload Time: "+getCurrentWeapon().reloadspeed, centrex-100, centrey-140);
		if(firemode == 2){
		g.drawString("Firemode: FULLY-AUTO", centrex-100, centrey-125);
		}
		if(firemode == 1){
		g.drawString("Firemode: SEMI-AUTO", centrex-100, centrey-125);
		}
		if(firemode == 3){
		g.drawString("Firemode: BURST", centrex-100, centrey-125);
		}
		if(firemode > 6 && firemode != 200){
			if(firerate < 1){
				g.drawString("Firemode: PUMP-ACTION [Shotgun]", centrex-100, centrey-125);
			}else if(firerate >= 1 && firerate <= 3){
				g.drawString("Firemode: SEMI-AUTO [Shotgun]", centrex-100, centrey-125);
			}else if(firerate > 3){
				g.drawString("Firemode: FULL-AUTO [Shotgun]", centrex-100, centrey-125);
			}
		}	
		if(firemode == 200){
			g.drawString("Firemode: CIRCLE", centrex-100, centrey-125);
		}
	}
	private VolatileImage createVolatileImage(int width, int height, int transparency) {	
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
		VolatileImage image = null;
	 
		image = gc.createCompatibleVolatileImage(width, height, transparency);
	 
		int valid = image.validate(gc);
	 
		if (valid == VolatileImage.IMAGE_INCOMPATIBLE) {
			image = this.createVolatileImage(width, height, transparency);
			return image;
		}
	 
		return image;
	}
	@SuppressWarnings("unused")
	private void drawMiniMap(Graphics2D g) {//this is extremely inefficient
		int centrex = width - 100;
		int centrey = 100;
		int minimapscale = ScrollLevel*width/1000;				

		float percentage = .4f;
        int brightness = (int)(256 - 256 * percentage);
        g.setColor(new Color(0,0,0,brightness));
        g.fillRect(centrex - 100, 0, 200, 200);
		
		g.setColor(Color.GREEN);
		g.fillRect(centrex, centrey, (16)/minimapscale+3, (16)/minimapscale+3);
		
		//g.drawLine(centrex+(int)(10*rotationsin), centrey+(int)(10*rotationcos), centrex-(int)(2*rotationsin), centrey-(int)(2*rotationcos));
		
		for(Enemy e : screen.enemies){
			int posx = (int)(e.x-x)/minimapscale+centrex;
			int posy = (int)(e.z-z)/minimapscale+centrey;
			
			if(posx > centrex - 100 && posx < centrex + 100 && posy > centrey - 100 && posy < centrey + 100 && !e.dead){
			g.setColor(Color.RED);
			g.fillRect(posx, posy, (16+(int)e.y)/minimapscale+3, (16+(int)e.y)/minimapscale+3);
			}
			
			//double depth = (e.x - width / 2.0) / height;
			//double xpos = depth * rotationcos + e.z * rotationsin;
			//double ypos = e.z * rotationcos - depth * rotationsin;
			//int zsize = (int) ((e.z-z)*rotationcos);
			//g.setColor(Color.RED);
			//g.fillRect((int) xpos, (int) ypos, 100, 20);
		}
		for(double[] d : screen.positions){
			if(screen.positions.indexOf(d) != Client.clientnumber-1){
				int posx = (int)(d[0]-x)/minimapscale+centrex;
				int posy = (int)(d[2]-z)/minimapscale+centrey;
				
				if(posx > centrex - 100 && posx < centrex + 100 && posy > centrey - 100 && posy < centrey + 100){
				g.setColor(Color.BLUE);
				g.fillRect(posx, posy, (16+(int)d[1])/minimapscale+3, (16+(int)d[1])/minimapscale+3);
				g.drawString("x:"+(int)d[0]+" y:"+(int)d[1]+" z:"+(int)d[2],posx-50, posy);
				}
			}
		}
		for(Objects e : screen.bullets){
			double posx = (e.x-x)/minimapscale+centrex;
			double posy = (e.z-z)/minimapscale+centrey;
			
			if(posx > centrex - 100 && posx < centrex + 100 && posy > centrey - 100 && posy < centrey + 100 && !e.maxdistreached){
			g.setColor(Color.YELLOW);
			g.fillRect((int)posx, (int)posy, 1, 1);
			}
			
			if(e.flash){
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, w, h);
				//if facing
			}
		}	
		for(Objects e : screen.objects){
			int posx = (int)(e.x-x)/minimapscale+centrex;
			int posy = (int)(e.z-z)/minimapscale+centrey;
			
			if(posx > centrex - 100 && posx < centrex + 100 && posy > centrey - 100 && posy < centrey + 100){
			g.setColor(Color.BLUE);
			g.fillRect(posx, posy, (16+(int)e.y)/minimapscale+3, (16+(int)e.y)/minimapscale+3);
			}
		}
		for(Model e : screen.models){
			int posx = (int)(e.model.get(0).vertices.get(0).x-x)/minimapscale+centrex;
			int posy = (int)(e.model.get(0).vertices.get(0).z-z)/minimapscale+centrey;
			
			if(posx > centrex - 100 && posx < centrex + 100 && posy > centrey - 100 && posy < centrey + 100){
			g.setColor(Color.GRAY);
			g.fillRect(posx, posy, 5, 5);
			}
		}
	}
	@SuppressWarnings("unused")
	private void drawRotationMap(Graphics2D g) {
		int centrey = height - 100;
		
		g.setColor(Color.BLACK);
		g.fillRect(0, centrey - 100, 200, 200);
		g.setColor(Color.RED);
		g.fillRect(0, centrey-100, 200, 40);
		g.fillRect(0, centrey+60, 200, 40);	
		g.setColor(Color.DARK_GRAY);
		g.fillRect(92, centrey - 100, 16, 200);
		if(centrey-(int)(rotationy*16)+16 > centrey-60 && centrey-(int)(rotationy*16)+21 < centrey+60){
			g.setColor(Color.BLUE);
		}else{
			g.setColor(Color.RED);
		}
		g.fillRect(95, centrey - 100, 10, 200);
		g.setColor(Color.GRAY);
		g.fillRect(0, centrey - 12, 200, 24);
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, centrey - 10, 200, 20);
			for(int x = 0; x < 108; x++){			
				g.setColor(Color.RED);
				g.fillOval(100+(int)(Math.sin(x)*50), centrey + (int)(Math.cos(x)*50), 2, 2);
				g.setColor(Color.GREEN);
				int xpos = (int)(((double)(x*200))/108);
				int ypos = (int)(Math.sin(x)*200/36);
				g.fillRect(xpos, centrey+ypos, 1, 1);
				g.setColor(Color.PINK);
				int xpos2 = (int)(((double)(x*200))/108);
				int ypos2 = (int)(Math.cos(x)*200/36);
				g.fillRect(xpos2, centrey+ypos2, 1, 1);
			}
			
			g.setColor(Color.GREEN);
			g.fillRect(0, centrey-(int)((rotationy*16))+16, 200, 2);
			g.fillRect((int)((rotation*4+100) % 200), centrey - 100, 2, 200);
			
			g.setColor(Color.YELLOW);
			g.fillOval(95+(int)(rotationcos*50), centrey + (int)(rotationsin*50), 10, 10);	
			g.fillOval(95, centrey-(int)((rotationy*16))+11, 10, 10);
			g.fillOval((int)((rotation*4+100) % 200)-4, centrey + (int)((rotationsin*rotationcos*200)/36) - 5, 10, 10);
			
			g.setColor(Color.RED);
			g.fillOval((int)((rotation*4+100) % 200)-4, centrey-(int)(rotationy*16)+11, 10, 10);
	}
	private void drawPauseMenu(Graphics g) {
		int MousePressed = InputHandler.MouseButton;

		g.setFont(new Font("Arial", Font.PLAIN, 30));
		Color c = new Color(255, 255, 255, 90);
		g.setColor(!(HEALTH <= 0)? Color.WHITE : Color.GRAY);
		g.drawString("Resume", width/2 - 55, height/2 - 90);
		g.setColor(Color.WHITE);
		g.drawString("Options", width/2 - 50, height/2 - 30);
		g.drawString("Main Menu", width/2 - 68, height/2 + 30);
		g.drawString("Exit", width/2 - 25, height/2 + 90);

		if (InputHandler.mouseX >= width/2 - 75 && InputHandler.mouseX <= width/2 + 75) {
			if (InputHandler.mouseY > height/2 - 110 && InputHandler.mouseY <= height/2 - 70) {
				g.setColor(c);
				g.fillRect(width/2 - 85, height/2 - 115, 180, 30);
				g.setColor(!(HEALTH <= 0)? Color.CYAN : Color.GRAY);
				g.drawString("Resume", width/2 - 55, height/2 - 90);
				if (MousePressed == 1 && !(HEALTH <= 0)) {
					InputHandler.MouseButton = 0;
					pause();
				}
			}
			if (InputHandler.mouseY > height/2 - 50 && InputHandler.mouseY <= height/2 - 10) {
				g.setColor(c);
				g.fillRect(width/2 - 85, height/2 - 55, 180, 30);
				g.setColor(Color.cyan);
				g.drawString("Options", width/2 - 50, height/2 - 30);
				if (MousePressed == 1) {
					f.dispose();
					new Options();
				}
			}
			if (InputHandler.mouseY > height/2 + 10 && InputHandler.mouseY <= height/2 + 50) {
				g.setColor(c);
				g.fillRect(width/2 - 85, height/2 + 5, 180, 30);
				g.setColor(Color.cyan);
				g.drawString("Main Menu", width/2 - 68, height/2 + 30);
				if (MousePressed == 1) {
					InputHandler.MouseButton = 0;
					f.dispose();
					launcher = new Launcher();
					stop();
				}
			}
			if (InputHandler.mouseY > height/2 + 70 && InputHandler.mouseY <= height/2 + 110) {
				g.setColor(c);
				g.fillRect(width/2 - 85, height/2 + 65 , 180, 30);
				g.setColor(Color.cyan);
				g.drawString("Exit", width/2 - 25, height/2 + 90);
				if (MousePressed == 1) {
					System.exit(0);
				}
			}
		}
	}
	class AnimationThread extends Thread {

		@Override
		public void run() {
			while (run) {
				long start = System.nanoTime();
				drawGraphics();
				long stop = System.nanoTime();
				fpstime = stop - start;
			}
		}

		public void drawGraphics() {
			try {
				Graphics2D g2 = null;
				try {
					g2 = (Graphics2D) bufferStrategy.getDrawGraphics();
			        g2.setRenderingHint(
			                RenderingHints.KEY_ANTIALIASING,
			                RenderingHints.VALUE_ANTIALIAS_ON);
					render(g2);
				} finally {
					if (g2 != null)
						g2.dispose();
				}
				bufferStrategy.show();
			} catch (Exception err) {
			}
		}
	}
	public static void main(String args[]) {
		//firstTimeSetup();
		getLauncherInstance().startMenu();
	}
	private static void firstTimeSetup() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch (Exception e){
		}
		int result = JOptionPane.showConfirmDialog((Component) null, "This appears to be first time setup, please select a location to install",
		        "Warning!", JOptionPane.OK_CANCEL_OPTION);
		if(result == JOptionPane.CANCEL_OPTION){
			System.exit(0);
		}
		JFileChooser jf2 = new JFileChooser();
		int returnVal = jf2.showSaveDialog(null);

		String saveTo = jf2.getSelectedFile().getPath();
		File f = new File(saveTo);
		f.mkdirs();
		
        int num = 0;
        JProgressBar current = new JProgressBar(0, 10000);
        current.setStringPainted(true);
        current.setForeground(Color.green);
        current.setValue(0);
        JDialog dlg = new JDialog();
        JLabel info = new JLabel("Progress: "+(num % 10000));
        dlg.pack();
        dlg.setTitle("Installing...");
        dlg.add(BorderLayout.SOUTH, current);
        dlg.add(BorderLayout.NORTH, info);
        dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dlg.setSize(300, 100);
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);
        iterate(num, current, info);
        dlg.dispose();
		int done = JOptionPane.showConfirmDialog((Component) null, "Done!",
		        "Installation Complete", JOptionPane.OK_CANCEL_OPTION);
		if(result == JOptionPane.CANCEL_OPTION){
			System.exit(0);
		}
	}
    public static void iterate(int num, JProgressBar current, JLabel info) {
       	int fakenum = 0;
       	double accel = 1;
        while (num <= 10000) {
        	fakenum += 1000*(Math.random()/100);
            current.setValue(num);
            info.setText("Progress: "+(fakenum));
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) { }
            num += accel;
            accel+=0.001;
        }
    }
	public void startMultiplayer(int port, String ip, String un) {
		client = new Client(port, ip, un, this);
		client.start();
	}
	public void BeginNetworkUpdate() {
		canUpdate = true;		
	}
	public void MousePressed() {
		MousePressed = true;
	}
	public void MouseReleased() {
		MousePressed = false;		
	}
}
