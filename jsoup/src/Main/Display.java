package Main;

import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.*;

import sun.audio.*;

import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JFrame;

import Connection.Client;
import Entity.Enemy;
import Entity.Objects;
import Entity.Weapon;
import Graphics.Screen;
import Graphics.Texture;
import Input.Controller;
import Input.InputHandler;
import Launcher.Launcher;
import Launcher.Options;

public class Display extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	public static double WINDOW_FAST_JOIN = 0.0;
	public static double WINDOW_TEST_MODE = 0.0;
	public static double WINDOW_TICK_RATE = 60.0;
	public static double WINDOW_NETWORK_TICK_RATE = 5.0;
	public static boolean WINDOW_USE_VSYNC = false;
	public static String DEFAULT_PORT = "12500";
	public static boolean WINDOW_FIX_MOUSE = false;
	public static int PING = 0;
	public static int ping = 0;
	public static boolean canUpdate = false;
	
	public static Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
	public static int w = ss.width;
	public static int h = ss.height;

	public static int width = (int) (w / 2);
	public static int height = (int) (h / 2);

	public static JFrame f;
	public static String title = "3D Game";

	Graphics g;
	private Thread thread;
	public static Client client;
	public Screen screen;
	private Game game;
	private BufferedImage img;
	private boolean run = false;
	private int[] pixels;
	private InputHandler input;
	private int newmX = 0;
	
	int tickCount = 0;
	int frames = 0;
	private int fps = 0;
	
	public static double MouseChange;
	public static int selection = 0;
	static Robot r;
	public static int blockcount=0;
	
	public static int x = 0;
	public static int y = 0;
	public static int z = 0;

	public static Launcher launcher;

	public static double MoveSpeed = 1;
	public static double JumpHeight = 1;
	public static double MouseSpeed = 10;
	public static int RenderDist = 10000;
	public static boolean Pause = false;
	
	static BufferedImage cursor = new BufferedImage(16, 16,
			BufferedImage.TYPE_INT_ARGB);
	static Cursor blank = Toolkit.getDefaultToolkit().createCustomCursor(
			cursor, new Point(0, 0), "blank");

	public static long t1=0;
	public static long t2=0;
	
	static boolean MousePressed = false;
	static boolean canfire = true;

	public static double rotationcos = 0;
	public static double rotationsin = 0;

	public static double StartHEALTH = 300;
	public static double HEALTH = 300;
	
	static Weapon w1 = new Weapon(1);
	static Weapon w2 = new Weapon(3);
	static int wep = 1;
	
	public static double initialaccuracy = w1.accuracy;
	public static double startaccuracy = initialaccuracy;
	public static double accuracy = startaccuracy;
	public static double firerate = w1.firerate;
	public static boolean SemiAuto = w1.SemiAuto;
	public static boolean FullAuto = !SemiAuto;
	public static int sFlashAmmo = 3;
	public static int sWeaponAmmo = w1.WeaponAmmo;
	public static int FlashAmmo = sFlashAmmo;
	public static int WeaponAmmo = sWeaponAmmo;
	
	public static int activebullets = 0;
	public static boolean Reload = false;

	
	long guntime = System.currentTimeMillis();
	long flashtime = System.currentTimeMillis();
	public static long reloadtime = System.currentTimeMillis();
	static double reloadspeed = 3000.0;
	public static boolean reloading = false;
	public static boolean donereloadsound = false;

	public static int enemiesattacking = 0;
	
	public static boolean collisionleft = false;
	public static boolean collisionfront = false;
	public static boolean collisionright = false;
	public static boolean collisionback = false;

	public Display() {
		Dimension size = new Dimension(WIDTH, HEIGHT);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		screen = new Screen(getGameWidth(), getGameHeight());
		game = new Game();
		img = new BufferedImage(getGameWidth(), getGameHeight(),
				BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();

		input = new InputHandler();
		addKeyListener(input);
		addFocusListener(input);
		addMouseListener(input);
		addMouseMotionListener(input);
		
		System.out.println("Current wep:"+w1.ID);
		System.out.println("Current wep:"+w2.ID);

	}

	public static Launcher getLauncherInstance() {
		if (launcher == null) {
			launcher = new Launcher();
		}
		return launcher;
	}
	
	public static JFrame getFrame() {
		if (f == null) {
			f = new JFrame();
		}
		return f;
	}

	public static void pause() {
		Pause ^= true;
		if (Pause) {
			getFrame().getContentPane().setCursor(Cursor.getDefaultCursor());
		} else {
			getFrame().getContentPane().setCursor(blank);
		}
	}

	public static int getGameWidth() {
		return width;
	}

	public static int getGameHeight() {
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
			System.exit(1);
		}
	}

	public void run() {
		double unprocessedSeconds = 0;
		long previousTime = System.nanoTime();
		double secondsPerTick = 1 / WINDOW_TICK_RATE;
		boolean ticked = false;
		
		while (run) {
			if(!WINDOW_USE_VSYNC){
			render();
			}
			long currentTime = System.nanoTime();
			long passedTime = currentTime - previousTime;
			previousTime = currentTime;
			unprocessedSeconds += passedTime / 1000000000.0;
			requestFocus();

			while (unprocessedSeconds > secondsPerTick) {
				if(WINDOW_USE_VSYNC){
				render();
				}
				tick();
				unprocessedSeconds -= secondsPerTick;
				ticked = true;
				
				if(tickCount % WINDOW_NETWORK_TICK_RATE == 0 && canUpdate){
				networkUpdate();
				}
				
				if (tickCount % WINDOW_TICK_RATE == 0) {
					screen.enemies.add(new Enemy((int)(Math.random()*500)+x,0,(int)(Math.random()*500)+z));
					PING = ping;
					fps = frames;
					previousTime += 1000;
					frames = 0;
				}
				if (ticked) {
				}
			}
		}
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

	public void tick() {
		tickCount++;
		game.tick(input.key);
		
		if(!Pause){
			newmX = InputHandler.mouseX;
	
			if (newmX > w/2 && WINDOW_TEST_MODE != 1) {
				Controller.turnright = true;
			}
			if (newmX < w/2 && WINDOW_TEST_MODE != 1) {
				Controller.turnleft = true;
			}
			if (newmX == w/2 && WINDOW_TEST_MODE != 1) {
				Controller.turnleft = false;
				Controller.turnright = false;
			}
	
			if(WINDOW_FIX_MOUSE){
				MouseChange = Math.abs((width/2) - newmX);
			}else{
			    MouseChange = (width/2) - newmX;
			}
			
			if(WINDOW_TEST_MODE != 1){
			r.mouseMove((w / 2), h / 2);
			}	
		}
		if(WeaponAmmo > 0 && !reloading){
			if(MousePressed && SemiAuto && input.MouseButton == 1){
				SemiAutoFire(1/firerate);
			}
			if(MousePressed && FullAuto && input.MouseButton == 1){
				FullAutoFire(1/firerate);
			}
		}else{
			WeaponAmmo = 0;
			Reload();
		}
		if(FlashAmmo > 0){
			if(MousePressed && input.MouseButton == 3 && FlashAmmo > 0){
				ThrowFlashBang();
			}
		}
		if(!MousePressed){
			canfire = true;
			if(accuracy > startaccuracy){
				accuracy-= 0.05;
			}else{
				accuracy = startaccuracy;
			}
		}	
		if(accuracy > 0.25){
			accuracy-= startaccuracy/2;
		}
		if(input.MouseButton == 0){
			MousePressed = false;
		}

		if(HEALTH < StartHEALTH){
		HEALTH+=0.01;
		}else{
			HEALTH = StartHEALTH;
		}
		if(HEALTH < 0){
			HEALTH = 0;
			//you died
		}
		screen.tick();
	}

	public static void Reload() {
		if(!reloading){
			reloadtime = System.currentTimeMillis();
			reloading = true;
		}
		if(reloading){
			if((System.currentTimeMillis()-reloadtime) > (reloadspeed-1000) && !donereloadsound){
				PlaySound("/audio/Reload.wav");
				donereloadsound = true;
			}
			if((System.currentTimeMillis()-reloadtime) > reloadspeed){
				reloading = false;
				donereloadsound = false;
				WeaponAmmo = sWeaponAmmo;
			}
		}
	}

	private void ThrowFlashBang() {
		long checktime = System.currentTimeMillis();
		if((checktime - flashtime) > (5000)){
			screen.bullets.add(new Objects(x,y,z));
			screen.bullets.get(screen.bullets.size()-1).UseFlashMechanism(rotationsin, rotationcos);
			flashtime = System.currentTimeMillis();
			FlashAmmo--;
		}
	}

	private void SemiAutoFire(double timedelay) {
		if(canfire){
			screen.bullets.add(new Objects(x,y,z));
			screen.bullets.get(screen.bullets.size()-1).UseBulletMechanism(rotationsin, rotationcos);
			canfire = false;
			accuracy += startaccuracy/2;
			PlaySound("/audio/M4A1.wav");
			WeaponAmmo--;
		}
	}
	
	private void FullAutoFire(double timedelay) {
		long checktime = System.currentTimeMillis();
		if((checktime - guntime) > (timedelay * 1000)){
			screen.bullets.add(new Objects(x,y,z));
			screen.bullets.get(screen.bullets.size()-1).UseBulletMechanism(rotationsin, rotationcos);
			guntime = System.currentTimeMillis();
			accuracy += startaccuracy/4;
			PlaySound("/audio/M4A1.wav");
			WeaponAmmo--;
		}
	}

	public static void PlaySound(String file){
	    try {
	        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Display.class.getResource(file));
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.start();
	    } catch(Exception ex) {
	    }
	}
	
	public static void ChangeWeapon(int weaponnum){
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
		SemiAuto = w.SemiAuto;
		FullAuto = !SemiAuto;
		sWeaponAmmo = w.WeaponAmmo;
		WeaponAmmo = sWeaponAmmo;
		reloadspeed = w.reloadspeed * 1000;
		
		Reload();
	}
	
	public static Weapon getCurrentWeapon(){
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
	
	public void render() {
		frames++;
		BufferStrategy bufferStrategy = this.getBufferStrategy();

		if (bufferStrategy == null) {
			this.createBufferStrategy(2);
			return;
		}

		screen.render(game);

		for (int i = 0; i < getGameWidth() * getGameHeight(); i++) {
			pixels[i] = screen.pixels[i];
		}

		g = bufferStrategy.getDrawGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, w, h);
		g.drawImage(img, 0, 0, getGameWidth() + 10, getGameHeight() + 10, null);

		g.setFont(new Font("Verdana", Font.PLAIN, 10));
		g.setColor(Color.WHITE);
		g.drawString("FPS: " + fps+" Ping: "+PING, 20, 20);
		g.drawString("X/Y/Z: "+x+","+y+","+z+" Rotation C/S: "+rotationsin+"/"+rotationcos, 20, 30);
		g.drawString("Textures: "+blockcount, 20, 40);
		g.drawString("VSYNC: "+WINDOW_USE_VSYNC, 20, 50);
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
			
			g.drawString("Player["+(i+1)+"] x:"
			+v3f[0]+" y:"
			+v3f[1]+" z:"
			+v3f[2]+" Ping: "+PING, 
			20, 150+(i*10));
		}
		
		if(!Pause){
			double accuracy = this.accuracy * 30;
		g.setColor(Color.BLACK); g.fillRect((int) (width
		/ 2 + accuracy * 10) - 1, height / 2 - 2, 12, 4); g.fillRect(width /
		2 - 2, (int) (height / 2 + accuracy * 10) - 1, 4, 12);
		g.fillRect((int) (width / 2 - 11 - accuracy * 10), height / 2 - 2,
		12, 4); g.fillRect(width / 2 - 2, (int) (height / 2 - 11 - accuracy *
		10), 4, 12);
		 
		g.setColor(Color.WHITE); g.fillRect((int) (width / 2 + accuracy *
		10), height / 2 - 1, 10, 2); g.fillRect(width / 2 - 1, (int) (height
		/ 2 + accuracy * 10), 2, 10); g.fillRect((int) (width / 2 - 10 -
		accuracy * 10), height / 2 - 1, 10, 2); g.fillRect(width / 2 - 1,
		(int) (height / 2 - 10 - accuracy * 10), 2, 10);
		
		
		int centrex = width - 100;
		int centrey = height - 100;
		int minimapscale = 5;
		
		g.setColor(Color.BLACK);
		g.fillRect(centrex - 100, centrey - 100, 200, 200);
		
		g.setColor(Color.GREEN);
		g.fillRect(centrex, centrey, (16+y)/minimapscale+3, (16+y)/minimapscale+3);
		
		g.drawLine(centrex, centrey, -(int)(1000000*rotationcos), -(int)(1000000*rotationsin));
		
		for(Enemy e : screen.enemies){
			int posx = (int)(x-e.x)/minimapscale+centrex;
			int posy = (int)(z-e.z)/minimapscale+centrey;
			
			if(posx > centrex - 100 && posx < centrex + 100 && posy > centrey - 100 && posy < centrey + 100 && !e.dead){
			g.setColor(Color.RED);
			g.fillRect(posx, posy, (16+(int)e.y)/minimapscale+3, (16+(int)e.y)/minimapscale+3);
			}
		}
		
		g.setColor(Color.BLACK); 
		g.fillRect(width/2-150, 0, 300, 20);
		g.setColor(Color.GREEN); 
		g.fillRect(width/2-150, 0, (int) (HEALTH), 20);
		g.setColor(Color.BLACK);
		g.drawString("Health: "+HEALTH, width/2 - 100, 13);
		}
		
		if (Pause)
			drawPauseMenu(g);

		g.dispose();
		bufferStrategy.show();
	}

	private void drawPauseMenu(Graphics g) {
		int MousePressed = InputHandler.MouseButton;

		g.setFont(new Font("Arial", Font.PLAIN, 30));
		Color c = new Color(255, 255, 255, 90);
		g.setColor(Color.GRAY);
		g.drawString("Resume", width/2 - 55, height/2 - 90);
		g.drawString("Options", width/2 - 50, height/2 - 30);
		g.drawString("Main Menu", width/2 - 68, height/2 + 30);
		g.drawString("Exit", width/2 - 25, height/2 + 90);

		if (InputHandler.mouseX >= width/2 - 75 && InputHandler.mouseX <= width/2 + 75) {
			if (InputHandler.mouseY > height/2 - 110 && InputHandler.mouseY <= height/2 - 70) {
				g.setColor(c);
				g.fillRect(width/2 - 85, height/2 - 115, 180, 30);
				g.setColor(Color.white);
				g.drawString("Resume", width/2 - 55, height/2 - 90);
				if (MousePressed == 1) {
					InputHandler.MouseButton = 0;
					pause();
				}
			}
			if (InputHandler.mouseY > height/2 - 50 && InputHandler.mouseY <= height/2 - 10) {
				g.setColor(c);
				g.fillRect(width/2 - 85, height/2 - 55, 180, 30);
				g.setColor(Color.white);
				g.drawString("Options", width/2 - 50, height/2 - 30);
				if (MousePressed == 1) {
					f.dispose();
					new Options();
				}
			}
			if (InputHandler.mouseY > height/2 + 10 && InputHandler.mouseY <= height/2 + 50) {
				g.setColor(c);
				g.fillRect(width/2 - 85, height/2 + 5, 180, 30);
				g.setColor(Color.white);
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
				g.setColor(Color.white);
				g.drawString("Exit", width/2 - 25, height/2 + 90);
				if (MousePressed == 1) {
					System.exit(0);
				}
			}
		}
	}
	
	public static void main(String args[]) {
		try {
			r = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		getLauncherInstance();
	}

	public static void startMultiplayer(int port, String ip, String un) {
		client = new Client(port, ip, un);
		client.start();
	}

	public static void BeginNetworkUpdate() {
		canUpdate = true;		
	}

	public static void MousePressed() {
		MousePressed = true;
	}

	public static void MouseReleased() {
		MousePressed = false;		
	}
}
