package Input;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import Main.Display;

public class InputHandler implements KeyListener, FocusListener, MouseListener,
		MouseMotionListener {

	public boolean[] key = new boolean[68836];
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

	public void mouseDragged(MouseEvent e) {
		mouseDX = e.getX();
		mouseDY = e.getY();
		mouseX = e.getX();
		mouseY = e.getY();
		dragged = true;
	}

	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	public void mouseClicked(MouseEvent e) {
		MouseClick = true;
	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {
		Display.MousePressed();
		MouseButton = e.getButton();
		
		mousePX = e.getX();
		mousePY = e.getY();
		mouseX = e.getX();
		mouseY = e.getY();
	}

	public void mouseReleased(MouseEvent e) {
		Display.MouseReleased();
		MouseButton = 0;
		dragged = false;
	}

	public void focusGained(FocusEvent e) {

	}

	public void focusLost(FocusEvent e) {
		for (int i = 0; i < key.length; i++) {
			key[i] = false;
		}
	}

	public void keyPressed(KeyEvent e) {
		keyCode = e.getKeyCode();

		if (keyCode > 0 && keyCode < key.length) {
			key[keyCode] = true;
		}
		
		if(keyCode == KeyEvent.VK_ESCAPE){
			Display.pause();
		}
	}

	public void keyReleased(KeyEvent e) {
		keyCode = e.getKeyCode();

		if (keyCode > 0 && keyCode < key.length) {
			key[keyCode] = false;
		}
	}

	public void keyTyped(KeyEvent e) {

	}

}
