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
	public double maxdistance = 100000;
	public double heightstep = 0.15;
	public boolean bullet = false;
	public boolean flash = false;
	double rotationsin=0;
	double rotationcos=0;
	double accuracy = Display.accuracy;
	double randomheightstep = 0;
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
		y+=randomheightstep*speed;
		z+=rotationcos*speed;
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
		if(y <= -4){
			bullet = false;
			maxdistreached = true;
		}
		}
		if(flash){
			x+=rotationsin*speed;
			y+=randomheightstep*speed;
			z+=rotationcos*speed;
			y-=0.1;
			if(y < -4){
				Display.PlaySound("/audio/Flashbang.wav");
				flash = false;
			}
		}
	}
	
	public void UseBulletMechanism(double rotationsin, double rotationcos){
		this.bullet = true;
		this.accuracy = Display.accuracy;
		this.rotationsin = rotationsin+(Math.random()*accuracy)-accuracy/2;
		this.rotationcos = rotationcos+(Math.random()*accuracy)-accuracy/2;
		this.randomheightstep  = (Math.random()*accuracy/2)-accuracy/4;
	}

	public void UseFlashMechanism(double rotationsin, double rotationcos) {
		this.flash = true;
		this.accuracy = Display.accuracy;
		this.rotationsin = rotationsin+(Math.random()*accuracy)-accuracy/2;
		this.rotationcos = rotationcos+(Math.random()*accuracy)-accuracy/2;
		this.randomheightstep  = (Math.random()*accuracy/2)-accuracy/4;
	}
}
