package Entity;

import Graphics.Screen;
import Main.Display;

public class Enemy {

	public double x;
	public double y;
	public double z;
	public double speed = 0.5;
	public boolean chase = true;
	public boolean dead = false;
	public boolean attacking = false;
	public double health = 100;
	public double displayhealth = health/100;
	
	public Enemy(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void tick() {
		if(!dead){
			displayhealth = health/100;
			if(health <= 0){
				health = 0;
				dead = true;
			}
			if(chase)
			chasePlayer();
		}
	}

	private void chasePlayer() {
		double dx = (Display.x) - x;
		//double dy = (Display.y) - y;
		double dz = (Display.z) - z;
		
		double distance = Math.sqrt((dx*dx)+(dz*dz));
		
		if(distance < 1){
			
		}else{
			dx/=distance;
			dz/=distance;
			
			x+=dx*speed;
			z+=dz*speed;
			//y=Display.y;
		}
	}	
}
