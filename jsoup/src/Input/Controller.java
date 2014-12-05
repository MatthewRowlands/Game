package Input;

import Main.Display;

public class Controller extends Thread{

	public double x, y, z, rotationx, rotationy, xa, ya, za, rotationax, rotationay;
	public static boolean turnleft = false;
	public static boolean turnright = false;
	public static boolean turndown = false;
	public static boolean turnup = false;
	public static boolean walk = false;
	public static boolean crouchwalk = false;
	public static boolean sprintwalk = false;
	public static boolean pronewalk = false;
	public static boolean fall = true;
	double mousespeed = 1;
	double rotationSpeedx = 0.0025 * Display.MouseChangex;
	double rotationSpeedy = 0.0025 * Display.MouseChangey;
	double walkSpeed = 0.5;
	double jumpheight = 1;
	double maxheight = 10*jumpheight;
	double crouchheight = -0.3;
	double bumheight = -0.75;
	double xMove = 0;
	double zMove = 0;
	
	public void tick(boolean forward, boolean back, boolean left,
			boolean right, boolean jump, boolean crouch, boolean sprint, boolean F1, boolean MEGARUN, boolean prone, boolean reload, boolean changewep1, boolean changewep2) {
		
		if(!Display.Pause){
		rotationSpeedx = 0.0025 * Display.MouseChangex;
		rotationSpeedy = 0.0025 * Display.MouseChangey;
		walkSpeed = 0.5 * Display.MoveSpeed;
		jumpheight = 1 * Display.JumpHeight;
		crouchheight = -0.3;
		bumheight = -0.75;
		xMove = 0;
		zMove = 0;

		if (forward /*&& !Display.collisionfront*/) {
			zMove++;
			walk = true;
		}

		if (back /*&& !Display.collisionback*/) {
			zMove--;
			walk = true;
		}

		if (left /*&& !Display.collisionleft*/) {
			xMove--;
			walk = true;
		}

		if (right /*&& !Display.collisionright*/) {
			xMove++;
			walk = true;
		}

		if (turnleft) {
			rotationax -= rotationSpeedx * (mousespeed/10);
		}

		if (turnright) {
			rotationax -= rotationSpeedx * (mousespeed/10);
		}
		
		if (turnup) {
			//if(rotationy <= 5){
			rotationay += rotationSpeedy * (mousespeed/10);
			//}else{
			//	rotationy = 5;
			//}
		}

		if (turndown) {
			//if(rotationy >= 0){
			rotationay -= rotationSpeedy * (mousespeed/10);
			//}else{
			//	rotationy = 0;
			//}
		}
		
		if (jump && !crouch && !prone) {
			if(!Display.flymode){
				if(y <= 0){
				fall = false;
				}
			}else{
				if(y < Display.ceilingpos-5)
				y+=5*Display.MoveSpeed;	
			}
		}

		if(!Display.flymode){
			if(fall && y > 0){
				y -= jumpheight/8;
			}
			if(!fall && y < maxheight){
				y += jumpheight;
			}
			if( y >= maxheight-jumpheight){
				fall = true;
			}
		}
		
		if (crouch) {
			if(!Display.flymode){
				if(y <= 0){
					walkSpeed = 0.2 * Display.MoveSpeed;
					y += crouchheight;
					crouchwalk = true;
				}
			}else{
				if(y > Display.floorpos/2)
				y-=5*Display.MoveSpeed;
			}
		}
		if (prone && y <= 0) {
			walkSpeed = 0.1 * Display.MoveSpeed;
			y += bumheight;
			pronewalk=true;
		}
		if (sprint && !crouch && !jump && !prone) {
			walkSpeed = 1 * Display.MoveSpeed;
			walk = true;
			sprintwalk = true;
			Display.startaccuracy=Display.initialaccuracy*2;
		}else{
			Display.startaccuracy=Display.initialaccuracy;
		}
		
		if (F1) {
			System.exit(0);
		}
		
		if(reload){
			Display.Reload();
		}
		
		if(changewep1){
			Display.ChangeWeapon(1);
		}
		if(changewep2){
			Display.ChangeWeapon(2);
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


		xa += (xMove * Math.cos(rotationx) + zMove * Math.sin(rotationx))
				* walkSpeed;
		za += (zMove * Math.cos(rotationx) - xMove * Math.sin(rotationx))
				* walkSpeed;

		x += xa;
		z += za;
		if(!Display.flymode){
		y *= 0.9;
		}

		xa *= 0.1;
		za *= 0.1;
		
		rotationx += rotationax;
		rotationy += rotationay;
		
		rotationax *= 0.8; 
		rotationay *= 0.8; 
		
		mousespeed = Display.MouseSpeed;
		}
	}
}
