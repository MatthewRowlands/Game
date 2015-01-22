package Entity;

import java.io.Serializable;

import Main.Display;

public class Enemy implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2577643837486522721L;
	
	public double x;
	public double y;
	public double z;
	public double speed = 0.5;
	public boolean chase = true;
	public boolean dead = false;
	public boolean attacking = false;
	public double maxhealth = 100;
	public double health = 100;
	public double displayhealth = health/100;
	private Display d;
	
	public Enemy(double x, double y, double z, Display d){
		this.x = x;
		this.y = y;
		this.z = z;
		this.d = d;
	}

	public void tick(int ups) {
		if(!dead){
			displayhealth = health/100;
			if(health <= 0){
				health = 0;
				d.kills++;
				dead = true;
			}
			if(chase){
			//chasePlayer(ups);
			}
		}
		if(y > 0){
			y-=d.gravity/(ups/60);
		}else y = 0;
	}

	private void chasePlayer(int ups) {
		double dx = (d.x) - x;
		//double dy = (Display.y) - y;
		double dz = (d.z) - z;
		
		double distance = Math.sqrt((dx*dx)+(dz*dz));
		
		if(distance < 1){
			
		}else{
			dx/=distance;
			dz/=distance;
			
			x+=dx*(speed/(ups/60));
			z+=dz*(speed/(ups/60));
			//y=Display.y;
		}
	}	
}
