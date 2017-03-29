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
	double rotationSpeedx = 0.05;
	double rotationSpeedy = 0.05;
	double walkSpeed = 0.5;
	double jumpheight = 1;
	double maxheight = 10*jumpheight;
	double crouchheight = -0.3;
	double bumheight = -0.75;
	double xMove = 0;
	double zMove = 0;
	private Display d;
	
	public Controller(Display d){
		this.d = d;
		rotationSpeedx = 0.0025 * d.MouseChangex;
		rotationSpeedy = 0.0025 * d.MouseChangey;
	}
	public void tick(int ups, boolean forward, boolean back, boolean left,
			boolean right, boolean jump, boolean crouch, boolean sprint, boolean F1, boolean MEGARUN, boolean prone, boolean reload, boolean changewep1, boolean changewep2) {
		
		if(!d.Pause){
		rotationSpeedx = 0.003 * d.MouseChangex;
		rotationSpeedy = 0.003 * d.MouseChangey;
		walkSpeed = 5 * d.MoveSpeed;
		jumpheight = 1 * d.JumpHeight/(ups/60);
		crouchheight = -0.3;
		bumheight = -0.75;
		xMove = 0;
		zMove = 0;
		
		if (forward /*&& !d.collisionfront*/) {
			zMove+=1;
			walk = true;
			if(d.flymode)
				y+=((rotationy-1)/32)*(walkSpeed*8);
		}

		if (back /*&& !d.collisionback*/) {
			zMove-=1;
			walk = true;
			if(d.flymode)
				y-=((rotationy-1)/32)*(walkSpeed*8);
		}

		if (left /*&& !d.collisionleft*/) {
			xMove-=1;
			walk = true;
		}

		if (right /*&& !d.collisionright*/) {
			xMove+=1;
			walk = true;
		}

		if (turnleft) {
			rotationax = rotationSpeedx * (mousespeed/10);
		}

		if (turnright) {
			rotationax = rotationSpeedx * (mousespeed/10);
		}
		
		if (turnup) {
			rotationay = rotationSpeedy * (mousespeed/10);
		}

		if (turndown) {
			rotationay = rotationSpeedy * (mousespeed/10);
		}
		
		if (jump && !prone) {
			if(!d.flymode){
				if(y <= 0){
				fall = false;
				}
			}else{
				if(y < d.ceilingpos-5)
				y+=5*d.MoveSpeed;	
			}
		}

		if(!d.flymode){
			if(fall && y > 0){
				d.startaccuracy = d.initialaccuracy*100;
				y -= (jumpheight/8);
			}
			if(!fall && y < maxheight){
				d.startaccuracy = d.initialaccuracy*100;
				y += jumpheight;
			}
			if(y >= maxheight){
				fall = true;
			}
		}
		
		if (crouch) {
			if(!d.flymode){
				if(y <= 0){
					walkSpeed = 0.2 * d.MoveSpeed;
					y += crouchheight;
					crouchwalk = true;
				}
			}else{
				y-=5*d.MoveSpeed;
			}
			d.startaccuracy = d.initialaccuracy/4;
		}
		if(!sprint && !crouch && !jump && !prone){
			if(y <= 0){
				if(forward || left || back || right)
					d.startaccuracy = d.initialaccuracy*8;
				else
					d.startaccuracy = d.initialaccuracy/3;
			}
		}
		if (prone) {
			if(y > -3.7){
				walkSpeed = 0.1 * d.MoveSpeed;
				y += bumheight;
				pronewalk=true;
			}else{
				y = -3.9;
			}
			if(d.startaccuracy >= d.initialaccuracy/8){
				d.startaccuracy -= 0.15*d.accuracy;
			}
		}
		if (sprint && !crouch && !jump && !prone) {
			walkSpeed = 0.3 * d.MoveSpeed;
			sprintwalk = true;
			if(forward || left || back || right)
				d.startaccuracy = d.initialaccuracy*2;
			else
				d.startaccuracy = d.initialaccuracy/3;
		}
		

		
		if(reload){
			d.Reload();
		}
		
		if(changewep1){
			d.ChangeWeapon(1);
		}
		if(changewep2){
			d.ChangeWeapon(2);
		}
		if (MEGARUN) {
			walkSpeed = 50 * d.MoveSpeed;
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


		xa = (xMove * Math.cos(rotationx) + zMove * Math.sin(rotationx))
				* walkSpeed/(ups/60);
		za = (zMove * Math.cos(rotationx) - xMove * Math.sin(rotationx))
				* walkSpeed/(ups/60);

		x += xa;
		z += za;
		
		if(!d.flymode){
			if(y <= 0)
				y *= 0.9;
		}

		/*xa *= 0.1;
		za *= 0.1;*/
		
		rotationx += rotationax;
		rotationy += rotationay;
		if(rotationy < -2) rotationy = -2;
		if(rotationy > 4) rotationy = 4;
		
		/*rotationax *= 0.8; 
		rotationay *= 0.8;*/
		
		mousespeed = d.MouseSpeed;
		}
		if (F1) {
			System.exit(0);
		}
	}
}
