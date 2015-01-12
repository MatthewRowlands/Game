package Input;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import Main.Display;

public class InputHandler extends Thread implements KeyListener, FocusListener, MouseListener,
		MouseMotionListener, MouseWheelListener {

	public boolean[] key = new boolean[68836];
	public boolean[] keyhold = new boolean[68836];
	public static int mouseX;
	public static int mouseY;
	public static int mouseDX;//d=drag
	public static int mouseDY;
	public static int mousePX;
	public static int mousePY;
	public static int MouseButton;
	public static boolean MousePressed = false;
	public static boolean dragged = false;
	public static int keyCode = 1;
	public static boolean MouseClick = false;
	Display d;
	
	public InputHandler(Display d){
		this.d = d;
		for (int i = 0; i < keyhold.length; i++) {
		keyhold[i] = true;
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseDX = e.getX();
		mouseDY = e.getY();
		mouseX = e.getX();
		mouseY = e.getY();
		dragged = true;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		MouseClick = true;
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(d != null)
		d.MousePressed();
		MouseButton = e.getButton();
		
		mousePX = e.getX();
		mousePY = e.getY();
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(d != null)
		d.MouseReleased();
		MouseButton = 0;
		dragged = false;
	}

	@Override
	public void focusGained(FocusEvent e) {

	}

	@Override
	public void focusLost(FocusEvent e) {
		/*for (int i = 0; i < key.length; i++) {
			key[i] = false;
		}*/
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keyCode = e.getKeyCode();
		
		if(keyhold[keyCode] == true){
			if (keyCode > 0 && keyCode < key.length) {
				key[keyCode] = true;
			}
		}else{
			if (keyCode > 0 && keyCode < key.length) {
				key[keyCode] ^= true;
			}
		}
		
		if(keyCode == KeyEvent.VK_ESCAPE){
			if(d != null)
			d.pause();
		}
		if(keyCode == KeyEvent.VK_F12){

		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keyCode = e.getKeyCode();
		
		if(keyhold[keyCode] == true){
			if (keyCode > 0 && keyCode < key.length) {
				key[keyCode] = false;
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int increment = e.getScrollAmount();
		int amount = e.getUnitsToScroll()/increment;
		if(d != null){
			if(d.ScrollLevel < 80 && d.ScrollLevel+amount > 0){
			d.ScrollLevel += amount;
			}
		}
	}
}
