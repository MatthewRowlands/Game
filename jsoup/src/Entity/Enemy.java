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
	
	public Enemy(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void tick() {
		if(chase)
		chasePlayer();
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
