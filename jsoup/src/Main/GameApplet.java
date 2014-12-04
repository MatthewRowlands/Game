package Main;

import java.applet.Applet;
import java.awt.BorderLayout;

import Launcher.Launcher;

public class GameApplet extends Applet{
	private static final long serialVersionUID = 1L;
	
	private Launcher l;
	
	@Override
	public void init(){
		l = Display.getLauncherInstance();
		setSize(800,400);
		setLayout(new BorderLayout());
		add(l);
	}
	
	@Override
	public void start(){
		l.startMenu();
	}
	
	@Override
	public void stop(){
		l.stopMenu();
	}
}
