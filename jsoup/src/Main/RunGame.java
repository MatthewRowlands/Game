package Main;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class RunGame {
	public String ip = "localHost";
	public String username = "Admin";
	public boolean bool = true;
	
	public RunGame(boolean multiplayer, String ip, String un, int port){
		if(un.equals("Admin")){
			bool = false;
		}
		
		this.ip = ip;
		this.username = un;

		JFrame f = new JFrame();
		Display game = new Display(f);
		if(multiplayer){
		game.startMultiplayer(port, ip, un);
		}
		game.start();
		stopMenuThread();
	}
	
	private void stopMenuThread(){
		Display.getLauncherInstance().stopMenu();
	}
}
