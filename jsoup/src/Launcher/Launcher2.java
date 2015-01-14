package Launcher;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

import Connection.Server;
import Input.InputHandler;
import Main.Display;

public class Launcher2 implements Runnable {

	JFrame f = new JFrame();
	Display display;
	InputHandler input;
	Thread thread;
	private boolean running = false;
	double num = 0;
	double amount = 1;
	double time = 0;
	Color c = Color.WHITE;
	String startuptext = "Loading...";
	boolean renderloadscreen = true;
	boolean renderstartmenu = false;
	double startmenuxpos = -200;
	int ticks = 0;
	public long fpstime;
	
	public Launcher2() {
		display = new Display(f, Display.w, Display.h);

		input = new InputHandler(null);
		f.addKeyListener(input);
		f.addFocusListener(input);
		f.addMouseListener(input);
		f.addMouseMotionListener(input);
		f.addMouseWheelListener(input);

		startMenu();
		new AnimationThread().start();
	}
	
	public void run(){
		double unprocessedSeconds = 0;
		long previousTime = System.nanoTime();
		double secondsPerTick = 1 / 600;
		
		while (true) {
			long currentTime = System.nanoTime();
			long passedTime = currentTime - previousTime;
			previousTime = currentTime;
			unprocessedSeconds += passedTime / 1000000000.0;
			
			while (unprocessedSeconds > secondsPerTick) {
				ticks++;
				tick();
				unprocessedSeconds -= secondsPerTick;
				if (ticks >= 600) {
					previousTime += 1000;
					ticks = 0;
				}
			}
		}
	}
	
	public void tick() {
		if(renderstartmenu && startmenuxpos < 100){
			startmenuxpos+=0.0000005;
		}
		if(renderstartmenu && startmenuxpos >= 100){ 
			display.startgame();
			stopMenu();
			renderstartmenu = false;
		}
	}
	
	public void startMenu() {
		running = true;
		thread = new Thread(this, "Menu");
		thread.start();
	}

	public void stopMenu() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	class AnimationThread extends Thread {

		@Override
		public void run() {
			while (running) {
				long start = System.nanoTime();
				drawGraphics();
				long stop = System.nanoTime();
				fpstime = stop - start;
			}
		}

		public void drawGraphics() {
			try {
				Graphics g = null;
				try {
					g = (Graphics) display.bufferStrategy.getDrawGraphics();
					render(g);
				} finally {
					if (g != null)
						g.dispose();
				}
				display.bufferStrategy.show();
			} catch (Exception err) {
			}
		}
	}

	private void render(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, display.w, display.h);
		if(renderloadscreen) renderLoadScreen(g);
		if(renderstartmenu) renderStartMenu(g);
		g.setColor(Color.GREEN);
		g.drawString(""+ticks+"/"+startmenuxpos, 10, 30);
	}
	
	private void renderStartMenu(Graphics g) {
		try {
			g.drawImage(ImageIO.read(Launcher2.class.getResource("/StartMenuBackground.jpg")), 0, 0, display.w, display.h, null);
		} catch (IOException e) {
		}
		Color c=new Color(1f,1f,1f,.6f);
		g.setColor(c);
		g.fillRect((int) startmenuxpos, 0, 400, display.h);	
	}

	private void renderLoadScreen(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillOval(display.w/2-48, display.h/2-48, 96, 96);
		loadbar(g, display.w - 220, display.h - 20, 200, 10, num, 400);
		
		if(num <= 395){
			iterate();
		}else{
			num = 400;
			startuptext = "Done!";
			renderloadscreen = false;
			renderstartmenu = true;
		}	
	}

	public void loadbar(Graphics g, int x, int y, int xSize, int ySize, double numcurrent, double nummax){
		c = new Color((int)((numcurrent/400)*255),255,255);
		g.setColor(Color.GRAY);
		g.fillRect(x-(int)(ySize/5), y-(int)(ySize/5), xSize+(int)(ySize/2.5), ySize+(int)(ySize/2.5));
		g.setColor(Color.WHITE);
		g.fillRect(x, y, (int)((numcurrent/nummax) * xSize), ySize);
	}

	public void iterate() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
		}
		num += 4;
	}

	public static void main(String[] args) {
		new Launcher2();
	}
}
