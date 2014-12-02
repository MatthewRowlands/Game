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

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;

import Connection.Client;
import Entity.Enemy;
import Entity.Entity;
import Entity.Objects;
import Entity.Weapon;
import Graphics.Screen;
import Graphics.Texture;
import Input.Controller;
import Input.InputHandler;
import Launcher.Launcher;
import Launcher.Options;
import Model.Model;

public class Display extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	/**
	 * @author Matthew
	 * 
	 * CX15-TR9Z-8UTY-6FNV
	 * 
	 * Changelog: 
	 * Version 0.6 [25/11/14]
	 * -Minimap
	 * -Enemies
	 * -Guns
	 * -Vertical Rotation
	 * -Multiplayer
	 * -Fixed Stretching for objects (partially)
	 * -Model Loader
	 * 
	 * TODO: 
	 * -Add in actual maps 
	 * -More guns
	 * -Develop multiplayer
	 * -Use only one ArrayList of type entity
	 * -Develop model loader
	 * -Sprites
	 * -Render Face
	 * 
	 * KNOWN BUGS:
	 * -Look too far up or down and goes WIERD (stretches texture?)
	 * -Player collision detection not aligned with direction facing
	 * -Flash grenades make screen white no matter which direction facing
	 * -Minimap is inverted
	 */
	
	public static double WINDOW_FAST_JOIN = 0.0;
	public static double WINDOW_TEST_MODE = 0.0;
	public static double WINDOW_TICK_RATE = 60.0;
	public static double WINDOW_NETWORK_TICK_RATE = 1.0;
	public static boolean WINDOW_USE_VSYNC = false;
	public static String DEFAULT_PORT = "12500";
	public static boolean WINDOW_FIX_MOUSE = false;
	public static int PING = 0;
	public static int ping = 0;
	public static double[] pings = new double[16];
	public static boolean canUpdate = false;
	public static double floorpos = 8;
	public static double ceilingpos = 512;
	public static boolean flymode = true;
	int time = 0;
	
	public static Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
	public static int w = ss.width;
	public static int h = ss.height;

	public static int width = w / 2;
	public static int height = h / 2;

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
	private int newmY = 0;
	
	int tickCount = 0;
	int frames = 0;
	public static int fps = 0;
	
	public static double MouseChangex;
	public static double MouseChangey;
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
	public static int RenderDist = 100000;
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
	public static double rotation = 0;
	public static double rotationy = 0;

	public static double StartHEALTH = 300;
	public static double HEALTH = 300;
	
	public static Weapon w1 = new Weapon(1);
	public static Weapon w2 = new Weapon(3);
	static int wep = 1;
	
	public static double initialaccuracy = w1.accuracy;
	public static double startaccuracy = initialaccuracy;
	public static double accuracy = startaccuracy;
	public static double firerate = w1.firerate;
	public static double WeaponDamage = w1.WeaponDamage;
	public static int firemode = w1.firemode;
	public static int sFlashAmmo = 3;
	public static int sWeaponAmmo = w1.WeaponAmmo;
	public static int FlashAmmo = sFlashAmmo;
	public static int WeaponAmmo = sWeaponAmmo;
	
	public static int activebullets = 0;
	public static boolean Reload = false;

	
	long guntime = System.currentTimeMillis();
	long flashtime = System.currentTimeMillis();
	public static long reloadtime = System.currentTimeMillis();
	public static double reloadspeed = w1.reloadspeed*1000;
	public static boolean reloading = false;
	public static boolean donereloadsound = false;

	public static int enemiesattacking = 0;
	
	public static boolean collisionleft = false;
	public static boolean collisionfront = false;
	public static boolean collisionright = false;
	public static boolean collisionback = false;

	public static int ScrollLevel = 8;

	public static int brightness = 200;

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
		addMouseWheelListener(input);
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

	@Override
	public void run() {
		double unprocessedSeconds = 0;
		long previousTime = System.nanoTime();
		double secondsPerTick = 1 / WINDOW_TICK_RATE;
		boolean ticked = false;
		r.mouseMove((w / 2), (h / 2));
		//screen.objects.add(new Objects((int)(Math.random()*500)+x-250,0,(int)(Math.random()*500)+z-250));
		//screen.objects.get(screen.objects.size()-1).UseSpawnerMechanism(5000);
		//screen.objects.add(new Objects((int)(Math.random()*500)+x-250,0,(int)(Math.random()*500)+z-250));
		//screen.objects.get(screen.objects.size()-1).UseSpawnerMechanism(5000);
		//screen.objects.add(new Objects((int)(Math.random()*500)+x-250,0,(int)(Math.random()*500)+z-250));
		//screen.objects.get(screen.objects.size()-1).UseSpawnerMechanism(5000);
		
		while (run && !thread.isInterrupted()) {
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
					//screen.objects.add(new Objects((int)(Math.random()*500)+x-250,0,(int)(Math.random()*500)+z-250));
					if(activebullets == 0 && screen.bullets.size() > 0){
					screen.bullets.clear();
					}
					PING = ping;
					fps = frames;
					previousTime += 1000;
					frames = 0;
					time++;
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
			newmY = InputHandler.mouseY-15;
	
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
			if (newmY > h/2 && WINDOW_TEST_MODE != 1) {
				Controller.turndown = true;
			}
			if (newmY < h/2 && WINDOW_TEST_MODE != 1) {
				Controller.turnup = true;
			}
			if (newmY == h/2 && WINDOW_TEST_MODE != 1) {
				Controller.turndown = false;
				Controller.turnup = false;
			}
	
			if(WINDOW_FIX_MOUSE){
				MouseChangex = Math.abs((width/2) - newmX);
				MouseChangey = Math.abs((height/2) - newmY);
			}else{
			    MouseChangex = (width/2) - newmX;
			    MouseChangey = (height/2) - newmY+5;
			}
			
			if(WINDOW_TEST_MODE != 1){
			r.mouseMove((w / 2), (h / 2));
			}	
		}
		ShootingMechanism();
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

	private void ShootingMechanism() {
		if(WeaponAmmo > 0 && !reloading){
			if(MousePressed && firemode == 1 && InputHandler.MouseButton == 1){
				SemiAutoFire(1/firerate);
			}
			if(MousePressed && firemode > 4 && firemode != 200 && InputHandler.MouseButton == 1){
				if(firerate < 3){
					ShotgunSemiFire(1/firerate);
				}else{
					ShotgunFullFire(1/firerate);
				}
			}
			if(MousePressed && firemode == 2 && InputHandler.MouseButton == 1){
				FullAutoFire(1/firerate);
			}
			if(MousePressed && firemode == 200 && InputHandler.MouseButton == 1){
				Circle(1/firerate);
			}
			WeaponAmmo = getCurrentWeapon().getRemainingAmmo();
		}else{
			Reload();
		}
		if(FlashAmmo > 0){
			if(MousePressed && InputHandler.MouseButton == 3 && FlashAmmo > 0){
				ThrowFlashBang();
			}
		}
		if(!MousePressed){
			canfire = true;
			if(accuracy > startaccuracy){
				accuracy-= 0.015;
			}else{
				accuracy = startaccuracy;
			}
		}	
		if(accuracy > 0.25){
			accuracy-= startaccuracy/4;
		}
		if(InputHandler.MouseButton == 0){
			MousePressed = false;
		}		
	}

	public static void Reload() {
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

	private void ThrowFlashBang() {
		long checktime = System.currentTimeMillis();
		if((checktime - flashtime) > (5000)){
			screen.bullets.add(new Objects(x,y,z));
			screen.bullets.get(screen.bullets.size()-1).UseFlashMechanism(rotationsin, rotationcos, rotationy);
			flashtime = System.currentTimeMillis();
			FlashAmmo--;
		}
	}
	
	private void Circle(double timedelay) {
		long checktime = System.currentTimeMillis();
		if((checktime - guntime) > (timedelay * 1000)){
			for(int i = 0; i < 180; i++){
				if(WeaponAmmo > 0){
				screen.bullets.add(new Objects(x,y,z));
				screen.bullets.get(screen.bullets.size()-1).UseBulletMechanism(Math.sin(i), Math.cos(i), 1);
				WeaponAmmo--;
				}
			}
			guntime = System.currentTimeMillis();
			accuracy += startaccuracy/4;
			PlaySound(getCurrentWeapon().filepath);
			PlaySound("/Audio/whiz.wav");
			getCurrentWeapon().remainingammo = WeaponAmmo;
		}
	}
	
	private void SemiAutoFire(double timedelay) {
		if(canfire){
			long checktime = System.currentTimeMillis();
			if((checktime - guntime) > (timedelay * 1000)){
			screen.bullets.add(new Objects(x,y,z));
			screen.bullets.get(screen.bullets.size()-1).UseBulletMechanism(rotationsin, rotationcos, rotationy);
			guntime = System.currentTimeMillis();
			canfire = false;
			accuracy += startaccuracy;
			PlaySound(getCurrentWeapon().filepath);
			PlaySound("/Audio/whiz.wav");
			WeaponAmmo--;
			getCurrentWeapon().remainingammo = WeaponAmmo;
			}
		}
	}
	
	private void FullAutoFire(double timedelay) {
		long checktime = System.currentTimeMillis();
		if((checktime - guntime) > (timedelay * 1000)){
			screen.bullets.add(new Objects(x,y,z));
			screen.bullets.get(screen.bullets.size()-1).UseBulletMechanism(rotationsin, rotationcos, rotationy);
			guntime = System.currentTimeMillis();
			accuracy += startaccuracy/4;
			PlaySound(getCurrentWeapon().filepath);
			PlaySound("/Audio/whiz.wav");
			WeaponAmmo--;
			getCurrentWeapon().remainingammo = WeaponAmmo;
		}
	}
	
	private void ShotgunFullFire(double timedelay) {
		long checktime = System.currentTimeMillis();
		if((checktime - guntime) > (timedelay * 1000)){
			for(int i = 0; i < firemode; i++){
				screen.bullets.add(new Objects(x,y,z));
				screen.bullets.get(screen.bullets.size()-1).UseBulletMechanism(rotationsin, rotationcos, rotationy);
			}
			guntime = System.currentTimeMillis();
			accuracy += startaccuracy;
			PlaySound(getCurrentWeapon().filepath);
			PlaySound("/Audio/whiz.wav");
			WeaponAmmo--;
			getCurrentWeapon().remainingammo = WeaponAmmo;
		}
	}
	
	private void ShotgunSemiFire(double timedelay) {
		if(canfire){
			long checktime = System.currentTimeMillis();
			if((checktime - guntime) > (timedelay * 1000)){
			for(int i = 0; i < firemode; i++){
				screen.bullets.add(new Objects(x,y,z));
				screen.bullets.get(screen.bullets.size()-1).UseBulletMechanism(rotationsin, rotationcos, rotationy);
			}
			guntime = System.currentTimeMillis();
			canfire = false;
			accuracy += startaccuracy;
			PlaySound(getCurrentWeapon().filepath);
			PlaySound("/Audio/whiz.wav");
			WeaponAmmo--;
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
	    } catch(Exception ex) {
	    }
	}
	
	public static void ChangeWeapon(int weaponnum){
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
		
		if(!Pause){	
		drawInfoBoardNorth();
		drawCrosshair();
		drawInfoBoardSouth();
		drawMiniMap();
		//drawRotationMap();
		}
		
		if (Pause)
			drawPauseMenu(g);

		g.dispose();
		bufferStrategy.show();
	}

	private void drawInfoBoardNorth() {
		g.setFont(new Font("Verdana", Font.PLAIN, 10));
		g.setColor(Color.WHITE);
		g.drawString("FPS: " + fps+" Ping: "+PING+" Threads: "+Thread.activeCount(), 20, 20);
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
			if(game.time % WINDOW_TICK_RATE == 0){
				pings[i] = v3f[3];
			}
			
			g.drawString("Player["+(i+1)+"] x:"
			+v3f[0]+" y:"
			+v3f[1]+" z:"
			+v3f[2]+" Ping: "+pings[i], 
			20, 150+(i*10));
		}	
	}

	private void drawCrosshair() {
		double accuracy = Display.accuracy * 30;
		
		g.setColor(Color.BLACK); 
		g.fillRect((int) (width / 2 + accuracy * 10) - 1, height / 2 - 2, 12, 4); 
		g.fillRect(width / 2 - 2, (int) (height / 2 + accuracy * 10) - 1, 4, 12);
		g.fillRect((int) (width / 2 - 11 - accuracy * 10), height / 2 - 2, 12, 4); 
		g.fillRect(width / 2 - 2, (int) (height / 2 - 11 - accuracy * 10), 4, 12);
		 
		g.setColor(Color.WHITE); 
		g.fillRect((int) (width / 2 + accuracy * 10), height / 2 - 1, 10, 2); 
		g.fillRect(width / 2 - 1, (int) (height / 2 + accuracy * 10), 2, 10); 
		g.fillRect((int) (width / 2 - 10 - accuracy * 10), height / 2 - 1, 10, 2); 
		g.fillRect(width / 2 - 1, (int) (height / 2 - 10 - accuracy * 10), 2, 10);	
		
		g.setColor(Color.BLACK); 
		g.drawLine(width/2, height/2, (int)(width/2-MouseChangex), (int)(height/2-MouseChangey));
		g.setColor(Color.BLUE);
		g.fillOval(width/2-5, height/2-5, 10,10);
		g.setColor(Color.RED);
		g.fillOval((int)(width/2-MouseChangex)-5, (int)(height/2-MouseChangey)-5, 10,10);
	}

	private void drawInfoBoardSouth() {
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

	private void drawMiniMap() {
		int centrex = width - 100;
		int centrey = height - 100;
		int minimapscale = ScrollLevel*width/1000;

		BufferedImage img2 = null;
		try {
			img2 = ImageIO.read(Display.class.getResource(screen.render.floor.file));
		} catch (Exception e) {
		}
		float percentage = .4f;
		g.drawImage(img2, centrex - 100, centrey - 100, 200, 200, this);
        int brightness = (int)(256 - 256 * percentage);
        g.setColor(new Color(0,0,0,brightness));
        g.fillRect(centrex - 100, centrey - 100, getWidth(), getHeight());
		
		g.setColor(Color.GREEN);
		g.fillRect(centrex, centrey, (16)/minimapscale+3, (16)/minimapscale+3);
		
		g.drawLine(centrex+(int)(10*rotationsin), centrey+(int)(10*rotationcos), centrex-(int)(2*rotationsin), centrey-(int)(2*rotationcos));
		
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
			int posx = (int)(e.x-x)/minimapscale+centrex;
			int posy = (int)(e.z-z)/minimapscale+centrey;
			
			if(posx > centrex - 100 && posx < centrex + 100 && posy > centrey - 100 && posy < centrey + 100 && !e.maxdistreached){
			g.setColor(Color.YELLOW);
			g.fillRect(posx, posy, 1, 1);
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
	private void drawRotationMap() {
		int centrey = height - 100;
		
		g.setColor(Color.BLACK); 
		g.fillRect(width/2-150, 0, 300, 20);
		g.setColor(Color.GREEN); 
		g.fillRect(width/2-150, 0, (int) (HEALTH), 20);
		g.setColor(Color.BLACK);
		g.drawString("Health: "+(int)HEALTH/3, width/2 - 100, 13);
		
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
