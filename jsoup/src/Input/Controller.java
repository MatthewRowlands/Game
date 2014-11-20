package Input;

import Main.Display;

public class Controller {

	public double x, y, z, rotation, xa, za, rotationa;
	public static boolean turnleft = false;
	public static boolean turnright = false;
	public static boolean walk = false;
	public static boolean crouchwalk = false;
	public static boolean sprintwalk = false;
	public static boolean pronewalk = false;
	public static boolean fall = true;
	double mousespeed = 1;
	double rotationSpeed = 0.0025 * Display.MouseChange;
	double walkSpeed = 0.5;
	double jumpheight = 1;
	double maxheight = 10*jumpheight;
	double crouchheight = -0.3;
	double bumheight = -0.75;
	double xMove = 0;
	double zMove = 0;
	
	public void tick(boolean forward, boolean back, boolean left,
			boolean right, boolean jump, boolean crouch, boolean sprint, boolean F1, boolean MEGARUN, boolean prone, boolean reload) {
		
		if(!Display.Pause){
		rotationSpeed = 0.0025 * Display.MouseChange;
		walkSpeed = 0.5 * Display.MoveSpeed;
		jumpheight = 1 * Display.JumpHeight;
		crouchheight = -0.3;
		bumheight = -0.75;
		xMove = 0;
		zMove = 0;

		if (forward) {
			zMove++;
			walk = true;
		}

		if (back) {
			zMove--;
			walk = true;
		}

		if (left) {
			xMove--;
			walk = true;
		}

		if (right) {
			xMove++;
			walk = true;
		}

		if (turnleft) {
			rotationa -= rotationSpeed * (double)(mousespeed/10);
		}

		if (turnright) {
			rotationa += rotationSpeed * (double)(mousespeed/10);
		}
		
		if (jump && !crouch && !prone) {
			if(y <= 0){
			fall = false;
			}
		}

		if(fall && y > 0){
			y -= jumpheight/8;
		}
		if(!fall && y < maxheight){
			y += jumpheight;
		}
		if( y >= maxheight-jumpheight){
			fall = true;
		}
		
		if (crouch && y <= 0) {
			walkSpeed = 0.2 * Display.MoveSpeed;
			y += crouchheight;
			crouchwalk = true;
		}
		if (prone && y <= 0) {
			walkSpeed = 0.1 * Display.MoveSpeed;
			y += bumheight;
			pronewalk=true;
		}
		
		if (sprint && !crouch && !jump) {
			walkSpeed = 1 * Display.MoveSpeed;
			walk = true;
			sprintwalk = true;
		}
		
		if (F1) {
			System.exit(0);
		}
		
		if(reload){
			Display.Reload();
		}
		
		if (MEGARUN) {
			walkSpeed = 5 * Display.MoveSpeed;
		}
		
		if(!forward && !back && !left && !right){
			walk = false;
		}
		if(!crouch){
			crouchwalk = false;
		}
		if(!sprint){
			sprintwalk = false;
		}
		
		if(!prone){
			pronewalk = false;
		}


		xa += (xMove * Math.cos(rotation) + zMove * Math.sin(rotation))
				* walkSpeed;
		za += (zMove * Math.cos(rotation) - xMove * Math.sin(rotation))
				* walkSpeed;

		x += xa;
		z += za;
		y *= 0.9;

		xa *= 0.1;
		za *= 0.1;
		rotation += rotationa;
		rotationa *= 0.8;
		
		mousespeed = Display.MouseSpeed;
		}
	}
}
