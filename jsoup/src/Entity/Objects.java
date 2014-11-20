package Entity;

import Main.Display;

public class Objects {
	double initialx;
	double initialy;
	double initialz;
	public double x;
	public double y;
	public double z;
	public double speed = 10;
	public double maxdistance = 200;
	
	public boolean bullet = false;
	double rotationsin=0;
	double rotationcos=0;
	public boolean maxdistreached = false;
	
	public Objects(double x, double y, double z){
		this.x = x;
		this.y = y+4;
		this.z = z;
		this.initialx = x;
		this.initialy = y+4;
		this.initialz = z;
	}

	public void tick() {
		if(bullet){
		x+=rotationsin*speed;
		z+=rotationcos*speed;
		}
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
	}
	
	public void UseBulletMechanism(double rotationsin, double rotationcos){
		this.bullet = true;
		this.rotationsin = rotationsin;
		this.rotationcos = rotationcos;
	}
}
