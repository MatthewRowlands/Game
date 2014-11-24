package Entity;

import java.io.Serializable;

import Main.Display;

public class Objects implements Serializable{
	double initialx;
	double initialy;
	double initialz;
	public double x;
	public double y;
	public double z;
	public double speed = 5;
	public double flashspeed = 2;
	public double drop = 0.000025;
	public double flashdrop = 0.0025;
	public double maxdistance = 100000;
	public double heightstep = 0.15;
	public boolean bullet = false;
	public boolean flash = false;
	double rotationsin=0;
	double rotationcos=0;
	double rotationy = 0;
	double accuracy = Display.accuracy;
	public boolean maxdistreached = false;
	
	public Objects(double x, double y, double z){
		this.x = x;
		this.y = y+heightstep;
		this.z = z;
		this.initialx = x;
		this.initialy = y+heightstep;
		this.initialz = z;
	}

	public void tick() {
		if(bullet){
		x+=rotationsin*speed;
		y+=rotationy*speed;
		z+=rotationcos*speed;
		rotationy-=drop;
		if(x - initialx > maxdistance){
			bullet = false;
			maxdistreached = true;
		}
		if(z - initialz > maxdistance){
			bullet = false;
			maxdistreached = true;
		}
		if(x - initialx < -maxdistance){
			bullet = false;
			maxdistreached = true;
		}
		if(z - initialz < -maxdistance){
			bullet = false;
			maxdistreached = true;
		}
		if(y <= -Display.floorpos/2 || y >= Display.ceilingpos/2){
			//make a bullet impact
			Display.PlaySound("/audio/Hard_Hit.wav");
			bullet = false;
			maxdistreached = true;
		}
		}
		if(flash){
			x+=rotationsin*flashspeed;
			y+=rotationy*flashspeed;
			z+=rotationcos*flashspeed;
			rotationy-=flashdrop;
			if(y < -4){
				Display.PlaySound("/audio/Flashbang.wav");
				flash = false;
				bullet = false;
				maxdistreached = true;
			}
		}
	}
	
	public void UseBulletMechanism(double rotationsin, double rotationcos, double rotationy){
		this.bullet = true;
		this.accuracy = Display.accuracy;
		this.rotationsin = rotationsin+(Math.random()*accuracy)-accuracy/2;
		this.rotationcos = rotationcos+(Math.random()*accuracy)-accuracy/2;
		this.rotationy  = rotationy/4+((Math.random()*accuracy/2)-accuracy/4)-0.25;
	}

	public void UseFlashMechanism(double rotationsin, double rotationcos, double rotationy) {
		this.flash = true;
		this.accuracy = Display.accuracy;
		this.rotationsin = rotationsin+(Math.random()*accuracy)-accuracy/2;
		this.rotationcos = rotationcos+(Math.random()*accuracy)-accuracy/2;
		this.rotationy = rotationy/4+((Math.random()*accuracy/2)-accuracy/4)-0.25;
	}
}
