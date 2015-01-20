package Entity;

import java.io.Serializable;
import java.util.ArrayList;

import Main.Display;

public class Objects implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4912823895350326561L;
	
	double initialx;
	double initialy;
	double initialz;
	public double x;
	public double y;
	public double z;
	public double heightstep = 0.15;
	public double speed = 20;
	public double flashspeed = 2;
	public double drop = 0.00015;
	public double flashdrop = 0.0025;
	public double maxdistance = 500;
	public boolean bullet = false;
	public boolean flash = false;
	public boolean spawner = false;
	public boolean spawn = false;
	public double spawnrate = 1000;
	public long spawntime1 = System.currentTimeMillis();
	public long spawntime2 = System.currentTimeMillis();
	double rotationsin=0;
	double rotationcos=0;
	double rotationy = 0;
	double accuracy = 0;
	public double distancetravelled = 0;
	public boolean maxdistreached = false;
	ArrayList<Enemy> hurtenemies = new ArrayList<Enemy>();

	private Display d;

	public Objects(double x, double y, double z, Display d) {
		//super(x, y, z);
		this.x = x;
		this.y = y+heightstep;
		this.z = z;
		this.initialx = x;
		this.initialy = y+heightstep;
		this.initialz = z;
		this.d = d;
		accuracy = d.accuracy;
	}
	
	public void tick(int ups) {
		if(bullet){
		x+=rotationsin*(speed)/(ups/60);
		y+=rotationy*(speed)/(ups/60);
		z+=rotationcos*(speed)/(ups/60);
		rotationy-=drop/(ups/60);
		distancetravelled = (x - initialx) + (y  -initialy) + (z - initialz);
		if(y <= -d.floorpos/2 || y >= d.ceilingpos/2){
			//make a bullet impact
			d.PlaySound("/audio/Hard_Hit.wav");
			maxdistreached = true;
		}
		}
		if(flash){
			x+=rotationsin*flashspeed;
			y+=rotationy*flashspeed;
			z+=rotationcos*flashspeed;
			rotationy-=flashdrop;
			if(y < -4){
				d.PlaySound("/Audio/Flashbang.wav");
				maxdistreached = true;
			}
		}
	}
	
	public void UseBulletMechanism(double rotationsin, double rotationcos, double rotationy){
		this.bullet = true;
		this.accuracy = d.accuracy;
		this.rotationsin = rotationsin/2+(Math.random()*accuracy)-accuracy/2;
		this.rotationcos = rotationcos/2+(Math.random()*accuracy)-accuracy/2;
		this.rotationy  = rotationy/4+((Math.random()*accuracy/2)-accuracy/4)-0.25;
	}

	public void UseFlashMechanism(double rotationsin, double rotationcos, double rotationy) {
		this.flash = true;
		this.accuracy = d.accuracy;
		this.rotationsin = rotationsin/2+(Math.random()*accuracy)-accuracy/2;
		this.rotationcos = rotationcos/2+(Math.random()*accuracy)-accuracy/2;
		this.rotationy = rotationy/4+((Math.random()*accuracy/2)-accuracy/4)-0.25;
	}

	public void UseSpawnerMechanism(int spawnrate) {
		this.spawner = true;
		this.spawnrate = spawnrate;
		spawntime1 = System.currentTimeMillis();
		spawntime2 = System.currentTimeMillis();
	}

	public void canthurt(Enemy e) {
		if(!e.dead)
		hurtenemies.add(e);
	}
	public boolean canhurt(Enemy e){
		if(hurtenemies.contains(e))
			return false;
		else
			return true;
	}
}
