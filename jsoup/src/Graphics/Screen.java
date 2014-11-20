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
			renderBlock(e.x/8,e.y/8,e.z/8, 1, 0.5, 1, 2);
		}
		for(Objects e : objects){
			renderBlock(e.x/8,e.y/8,e.z/8, 1, 0, 1, 1);
		}
		for(Objects e : bullets){
			if(e.maxdistreached){
				
			}else{
			renderBlock(e.x/8,e.y/8,e.z/8, 0.2, 0, 0.2, 3);
			}
		}
		render.renderDistanceLimiter();
		draw(render, 0, 0);
	}
	
	public void tick(){
		for(Enemy e : enemies){
			e.tick();
		}
		for(Objects e : bullets){
			e.tick();
		}
	}
	
	public void renderBlock(double x, double y, double z, double sizex, double sizey, double sizez, int texture){
		render.renderWall(x, x, z + sizez, z,     y, texture);//left
		render.renderWall(x + sizex, x + sizex, z,     z + sizez, y, texture);//right
		render.renderWall(x,     x + sizex, z, z, y, texture);//front
		render.renderWall(x + sizex, x,     z + sizez, z + sizez, y, texture);//back
		
		
		render.renderWall(x + sizex, x + sizex, z, z + sizez, y+sizey, texture);
		render.renderWall(x + sizex, x, z + sizez, z + sizez, y+sizey, texture);
		
		render.renderWall(x, x, z + sizez, z, y+sizey, texture);
		render.renderWall(x, x + sizex, z, z, y+sizey, texture);
	}

}
