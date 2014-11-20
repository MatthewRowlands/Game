package Main;

import java.awt.event.KeyEvent;

import Entity.Enemy;
import Graphics.Screen;
import Input.Controller;
import Level.Level;

public class Game {

	public int time;
	public Controller controls;
	public Level level;
	public boolean spectator = false;

	public Game() {
		controls = new Controller();
		level = new Level(0,0);
	}

	public void tick(boolean[] key) {
		time++;
		if(!spectator){
		boolean forward = key[KeyEvent.VK_W];
		boolean back = key[KeyEvent.VK_S];
		boolean left = key[KeyEvent.VK_A];
		boolean right = key[KeyEvent.VK_D];
		boolean jump = key[KeyEvent.VK_SPACE];
		boolean crouch = key[KeyEvent.VK_CONTROL];
		boolean sprint = key[KeyEvent.VK_SHIFT];
		boolean F1 = key[KeyEvent.VK_F1];
		boolean MEGARUN = key[KeyEvent.VK_V];
		boolean prone = key[KeyEvent.VK_BACK_SLASH];
		boolean reload = key[KeyEvent.VK_R];

		controls.tick(forward, back, left, right, jump, crouch, sprint, F1, MEGARUN, prone, reload);
		}
	}
}
