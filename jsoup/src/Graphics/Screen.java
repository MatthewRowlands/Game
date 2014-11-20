package Graphics;

import java.util.ArrayList;

import Connection.Client;
import Entity.Enemy;
import Entity.Objects;
import Main.Game;

public class Screen extends Render {
	private Render3D render;
	public ArrayList<double[]> positions = new ArrayList<double[]>();
	public ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	public ArrayList<Objects> objects = new ArrayList<Objects>();
	public ArrayList<Objects> bullets = new ArrayList<Objects>();
	int width, height;
	
	public Screen(int width, int height) {	
		super(width, height);
		this.width = width;
		this.height = height;
		render = new Render3D(width, height);
	}

	public void render(Game game) {
		for (int i = 0; i < width * height; i++) {
			pixels[i] = 0;
		}
		
		render.floor(game);
		
		for(double[] v3f : positions){
			if(positions.indexOf(v3f) != Client.clientnumber-1){
			renderBlock(v3f[0]/8,v3f[1]/8,v3f[2]/8, 1, 0.5, 1, 3);
			}
		}
		for(Enemy e : enemies){
			renderBlock(e.x/8,e.y/8,e.z/8, 1, 1, 1, 2);
		}
		for(Objects e : objects){
			renderBlock(e.x/8,e.y/8,e.z/8, 1, 0.5, 1, 1);
		}
		for(Objects e : bullets){
			if(e.maxdistreached){
				
			}else{
			renderBlock(e.x/8,e.y/8,e.z/8, 0.05, 0.025, 0.05, 0);
			}
		}
		render.renderDistanceLimiter();
		draw(render, 0, 0);
	}
	
	private void CheckCollision() {
		for(Enemy e : enemies){
			for(Objects e2 : bullets){
				for(Objects e3 : objects){
					double x1, y1, z1;
					double x2, y2, z2;
					double x3, y3, z3;
					
					x1 = e.x;
					y1 = e.y;
					z1 = e.z;
					
					x2 = e2.x;
					y2 = e2.y;
					z2 = e2.z;
					
					x3 = e3.x;
					y3 = e3.y;
					z3 = e3.z;
					
					if(x2 >= x1 && x2 <= x1 + 8 && z2 >= z1 && z2 <= z1 + 8){
						e.chase = false;
					}
				}
			}
		}
	}

	public void tick(){
		CheckCollision();
		for(Enemy e : enemies){
			e.tick();
		}
		for(Objects e : bullets){
			e.tick();
		}
	}
	
	public void renderBlock(double x, double y, double z, double sizex, double sizey, double sizez, int texture){	
		if(sizey > 0.5){
			
			render.renderWall(x, 	     x,         z + sizez, z,         0.5, y, texture);//left
			render.renderWall(x + sizex, x + sizex, z,         z + sizez, 0.5, y, texture);//right
			render.renderWall(x,         x + sizex, z,         z,         0.5, y, texture);//front
			render.renderWall(x + sizex, x,         z + sizez, z + sizez, 0.5, y, texture);//back
			
			render.renderWall(x + sizex, x + sizex, z,         z + sizez, 0.5, y+sizey/2, texture);
			render.renderWall(x + sizex, x,         z + sizez, z + sizez, 0.5, y+sizey/2, texture);
			render.renderWall(x,         x,         z + sizez, z,         0.5, y+sizey/2, texture);
			render.renderWall(x,         x + sizex, z,         z,         0.5, y+sizey/2, texture);
			
		}
		else
		{
			render.renderWall(x, 	     x,         z + sizez, z,         sizey, y, texture);//left
			render.renderWall(x + sizex, x + sizex, z,         z + sizez, sizey, y, texture);//right
			render.renderWall(x,         x + sizex, z,         z,         sizey, y, texture);//front
			render.renderWall(x + sizex, x,         z + sizez, z + sizez, sizey, y, texture);//back
		}
	}

}
