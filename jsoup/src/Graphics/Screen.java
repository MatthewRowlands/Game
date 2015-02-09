package Graphics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Connection.Client;
import Entity.Enemy;
import Entity.Objects;
import Main.Display;
import Main.Game;
import Model.Face;
import Model.Model;

public class Screen extends Render{
	public Render3D render;
	Display d;
	public ArrayList<double[]> positions = new ArrayList<double[]>();
	public ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	public ArrayList<Objects> objects = new ArrayList<Objects>();
	public ArrayList<Objects> bullets = new ArrayList<Objects>();
	public ArrayList<Model> models = new ArrayList<Model>();
	//public ArrayList<Sprite> sprites = new ArrayList<Sprite>();
	
	int width, height;
	
	Texture player = new Texture("/textures/Ground2.png");
	Texture enemy = new Texture("/textures/Enemy.png");
	Texture object = new Texture("/textures/Ground4.png");
	Texture bullet = new Texture("/textures/Fire.png");

	public Screen(int width, int height, Display d) {	
		super(width, height);
		this.width = width;
		this.height = height;
		this.d = d;
		render = new Render3D(width, height, d);
		//objects.add(new Objects(-5, 4, 800, d, 16));
/*		for(int x = 1; x <= 50; x++){
			for(int y = 0; y <= 20; y+=4){
				for(int z = 1; z <= 50; z++){
					if(x == 1 || x == 50 || z == 1 || z == 50){
						objects.add(new Objects(-(25*8)+(x*8), y, -(25*2)+(z*8), d, 16));
					}
				}
			}
		}
		for(int x = 2; x <= 49; x++){
			objects.add(new Objects(-(25*8)+(x*8), -1, 40, d, 16));
			objects.get(objects.size()-1).setYSize(0.375);
			objects.add(new Objects(-(25*8)+(x*8), 12, 40, d, 16));
			objects.add(new Objects(-(25*8)+(x*8), 16, 40, d, 16));
			objects.add(new Objects(-(25*8)+(x*8), 20, 40, d, 16));
		}*/
		
/*		for(int i = -30; i < 31; i++){
			for(int c = 0; c < 25; c++){
			double y = -(((i*6)*(i*6))/800)+40;
			objects.add(new Objects(i*4, y, c*8,  d, 16));
			}
		}*/
		
		newWave(0,0,0);
		newWave(100,0,0);
		newWave(200,0,0);
		newWave(300,0,0);
		newWave(400,0,0);
		newWave(500,0,0);
		objects.get(objects.size()-1).spawner = true;
		models.add(new Model());//TODO implement properly
		models.get(models.size()-1).LoadModel("untitled");
	}

	private void newWave(int xpos, int ypos, int zpos) {
		int length = 1000;
		int gapbetweenrings = 8;
		int radius = 20;
		int detail = 30;
		int waveheight = 320;
		int numberofcolorsections = 30;
		int wavelength = 360;
		double hfg = 20;
		double dg = 360/detail;
		for(int z = length; z > 0; z--){
			for(int x = 0; x < detail; x++){
				double xx, yy, zz;
				xx = (radius*2)*Math.cos(Math.toRadians(x*dg));
				double yyy = radius*Math.sin(Math.toRadians(x*dg))+hfg;
				double wave = waveheight*Math.cos(Math.toRadians((z*wavelength)/length));
				yy = yyy+wave;
				zz = z*gapbetweenrings;
				double colorextension = (length/(numberofcolorsections/3));
				double color = ((z%colorextension)*24)/colorextension;
				objects.add(new Objects(xx+xpos, yy+ypos, zz+zpos, d, (int) color));
			}
		}	
	}

	public void render(Game game) {
		for (int i = 0; i < width * height; i++) {
			pixels[i] = 0;
		}
		
		d.activebullets = 0;
		render.floor(game);
		RenderObjects();
		render.renderDistanceLimiter();
		draw(render, 0, 0);
	}
	
	private void RenderObjects() {
		for(Iterator<double[]> iterator = positions.iterator(); iterator.hasNext();){
			double[] v3f = iterator.next();
			if(positions.indexOf(v3f) != Client.clientnumber-1){
			renderBlock(v3f[0]/8,v3f[1]/8,v3f[2]/8, 1, 0.5, 1, player);
			}
		}
		for(Iterator<Enemy> iterator = enemies.iterator(); iterator.hasNext();){
			Enemy e = iterator.next();
			if(!e.dead){
			renderBlock(e.x/8,e.y/8,e.z/8, 1, 1, 1, enemy);
			render.renderSprite(e.x/8+0.5,e.y/8 - 2,e.z/8+0.5, 400, 40, 0x7cfc00, (int) ((e.health / e.maxhealth) * 400)-200);
			render.renderSprite(e.x/8+0.5,e.y/8 - 2,e.z/8+0.5, 400, 40, 0xFF0000, 200);
			}
		}
		for(Iterator<Objects> iterator = objects.iterator(); iterator.hasNext();){
			Objects o = iterator.next();
			renderBlock(o.x/8,o.y/8,o.z/8, 1, o.ys, 1, new Texture(o.color));
		}
		for(Iterator<Objects> iterator = bullets.iterator(); iterator.hasNext();){
			Objects b = iterator.next();
			if(!b.flash){
			d.activebullets++;
			}
			if(b.maxdistreached){
			d.activebullets--;
			}else{
				if(b.bullet){
					renderBlock(b.x/8,b.y/16,b.z/8, 0.05, 0.025, 0.05, bullet);
				}else if(b.flash)
					renderBlock(b.x/8,b.y/8,b.z/8, 0.1, 0.1, 0.1, bullet);
			}
		}	
		for(Iterator<Model> iterator = models.iterator(); iterator.hasNext();){
			for(Iterator<Face> iterator2 = iterator.next().model.iterator(); iterator2.hasNext();){
				Face f = iterator2.next();
				render.renderFace(f, f.t);
			}
		}
	}

	public void CheckCollision(int p) {
		d.enemiesattacking = 0;
		d.collisionright = false;
		d.collisionback = false;
		d.collisionfront = false;
		d.collisionleft = false;
		
		for(Iterator<Enemy> iteratore = enemies.iterator(); iteratore.hasNext();){
			Enemy e = iteratore.next();
			double x1, y1, z1;
			x1 = e.x;
			y1 = e.y;
			z1 = e.z;
			
			if(!e.dead){		
				if(x1 >= d.x - 16 && x1 <= d.x + 8 && z1 >= d.z - 16 && z1 <= d.z + 8 && d.y < y1 + 32 && d.y + 32 > y1){
					e.chase = false;
					e.attacking = true;
					if(d.HEALTH > 0 && p != 1){
					d.HEALTH --;
					}
					d.enemiesattacking++;
					
					//TODO: needs fixing ->
					/* Not relative to direction facing
					 * Use sine/cosine? 
					 * Remake but using xMove and yMove?
					 */
					if(x1 >= d.x){
						d.collisionright = true;
					}
					if(x1 <= d.x){
						d.collisionleft = true;
					}
					if(z1 >= d.z){
						d.collisionfront = true;
					}
					if(z1 <= d.z){
						d.collisionback = true;
					}
				}else{
					e.chase = true;
					e.attacking = false;
				}
			}
			
			for(Iterator<Objects> iteratorb = bullets.iterator(); iteratorb.hasNext();){
				Objects b = iteratorb.next();
				double x2, y2, z2;	
				x2 = b.x;
				y2 = b.y;
				z2 = b.z;
					
				if(b.bullet && x2 >= x1 && x2 <= x1 + 8 && z2 >= z1 && z2 <= z1 + 8 && y2 >= y1 -8 && y2 <= y1 +8){
					if(b.canhurt(e)){
						double dmgtodo = (d.WeaponDamage+(Math.random()*3)-1);
						e.health-=dmgtodo;
					}
					b.canthurt(e);
				}
			}
		}
	}
	
	public void tick(int ups){
		for(Iterator<Enemy> iterator = enemies.iterator(); iterator.hasNext();){
			Enemy e = iterator.next();
			if(!e.dead){
				e.tick(ups);
			}else{
				iterator.remove();
			}
		}
		for(Iterator<Objects> iterator = bullets.iterator(); iterator.hasNext();){
			Objects b = iterator.next();
			if(!b.maxdistreached){
				b.tick(ups);
			}else{
				iterator.remove();
			}
		}
		CheckCollision(0);
	}
	
	public void renderBlock(double x, double y, double z, double sizex, double sizey, double sizez, Texture t){	
		if(sizey > 0.5){
			
			render.renderWall(x, 	     x,         z + sizez, z,         0.5, y, t);//left
			render.renderWall(x + sizex, x + sizex, z,         z + sizez, 0.5, y, t);//right
			render.renderWall(x,         x + sizex, z,         z,         0.5, y, t);//front
			render.renderWall(x + sizex, x,         z + sizez, z + sizez, 0.5, y, t);//back
			
			render.renderWall(x + sizex, x + sizex, z,         z + sizez, 0.5, y+sizey/2, t);
			render.renderWall(x + sizex, x,         z + sizez, z + sizez, 0.5, y+sizey/2, t);
			render.renderWall(x,         x,         z + sizez, z,         0.5, y+sizey/2, t);
			render.renderWall(x,         x + sizex, z,         z,         0.5, y+sizey/2, t);
			
		}
		else
		{
			render.renderWall(x, 	     x,         z + sizez, z,         sizey, y, t);//left
			render.renderWall(x + sizex, x + sizex, z,         z + sizez, sizey, y, t);//right
			render.renderWall(x,         x + sizex, z,         z,         sizey, y, t);//front
			render.renderWall(x + sizex, x,         z + sizez, z + sizez, sizey, y, t);//back
		}
	}

}
