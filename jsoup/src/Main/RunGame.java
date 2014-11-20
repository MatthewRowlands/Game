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
		
		BufferedImage cursor = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blank = Toolkit.getDefaultToolkit().createCustomCursor(cursor, new Point(0, 0), "blank");
		Display game = new Display();
		JFrame f = Display.getFrame();
		f.add(game);
		f.setSize(Display.getGameWidth(), Display.getGameHeight());
		f.setUndecorated((Display.WINDOW_TEST_MODE == 0));
		f.setAlwaysOnTop(!bool);
		f.getContentPane().setCursor(blank);
		f.setTitle(Display.title+" "+un);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLocationRelativeTo(null);
		f.setVisible(true);

		if(multiplayer){
		Display.startMultiplayer(port, ip, un);
		}
		game.start();
		stopMenuThread();
	}
	
	private void stopMenuThread(){
		Display.getLauncherInstance().stopMenu();
	}
}
