package Graphics;

import java.util.ArrayList;

import Input.Controller;
import Log.Dump;
import Log.Log;
import Main.Display;
import Main.Game;
import Model.Face;
import Model.Vertex;

public class Render3D extends Render {

	public double[] zBuffer;
	public double[] zBufferWall;
	public double renderDistance = 0;
	double forward, right, up, cosine, sine, walking, rotation, rotationy;

	double floorpos = 0;
	double ceilingpos = 0;
	int c = 0;
	int num = 1;
	
	public Texture floor = new Texture("/textures/Fire.png");
	public Texture roof = new Texture("/textures/Night Sky.png");
	private Display d;
	
	public Render3D(int width, int height, Display d) {
		super(width, height);
		zBuffer = new double[width * height];
		zBufferWall = new double[width];
		this.d = d;
		floorpos = d.floorpos;
		ceilingpos = d.ceilingpos;
		renderDistance = d.RenderDist;
	}

	public void floor(Game game) {	
		for(int x = 0; x < width; x++){
			zBufferWall[x] = 0;
		}
		
		forward = game.controls.z;
		right = game.controls.x;
		up = game.controls.y;
		walking = 0;
		rotation = game.controls.rotationx;
		cosine = Math.cos(rotation);
		sine = Math.sin(rotation);
		rotationy = game.controls.rotationy;
		
		d.x = (int) right;
		d.y = (int) up;
		d.z = (int) forward;
		d.rotationsin = sine;
		d.rotationcos = cosine;
		d.rotationy = rotationy;
		d.rotation = rotation;
		
		for (int y = 0; y < height; y++) {
			double ceiling = (y - height*(rotationy) / 2.0) / height;
			double z = (floorpos + up) / ceiling;
			c = 0;
			
			if (Controller.walk) {
				walking = Math.sin(game.time / 6.0) * 0.4;
				z = (floorpos + up + walking) / ceiling;
			}
			if (Controller.crouchwalk && Controller.walk) {
				walking = Math.sin(game.time / 6.0) * 0.2;
				z = (floorpos + up + walking) / ceiling;
			}
			if (Controller.sprintwalk && Controller.walk) {
				walking = Math.sin(game.time / 6.0) * 0.8;
				z = (floorpos + up + walking) / ceiling;
			}
			if (Controller.pronewalk && Controller.walk) {
				walking = Math.sin(game.time / 6.0) * 0.1;
				z = (floorpos + up + walking) / ceiling;
			}
			
			if (ceiling < 0) {
				z = (ceilingpos - up) / -ceiling;
				c = 1;
				if (Controller.walk) {
					z = (ceilingpos - up - walking) / -ceiling;
				}
			}

			for (int x = 0; x < width; x++) {
				double depth = (x - width / 2.0) / height;
				depth *= z;
				double xx = depth * cosine + z * sine;
				double yy = z * cosine - depth * sine;
				int xPix;
				int yPix;
				if(c == 0){
					xPix = (int) ((xx + right)*16);
					yPix = (int) ((yy + forward)*16);
				}else{
					xPix = (int) ((xx + right)/32);
					yPix = (int) ((yy + forward)/32);	
				}
				zBuffer[x + y * width] = z;
				if(c == 0){
					if(up > -d.floorpos/2)
						pixels[x + y * width] = floor.r.pixels[(xPix & 1023)+floor.texVar+(yPix & 1023) * 1024];
					else
						pixels[x + y * width] = 0;
				}else{
					if(up < d.ceilingpos)
						pixels[x + y * width] = roof.r.pixels[(xPix & 1023)+roof.texVar+(yPix & 1023) * 1024];
					else
						pixels[x + y * width] = 0;
				}
				if (z > renderDistance*50000) {
					pixels[x + y * width] = 0x7EC0EE;
				}
			}
		}
	}

	public void renderWall(double xLeft, double xRight, double zDistanceLeft, double zDistanceRight, double yBottom, double yTop, Texture t) {

		if(yTop < -0.5){
			return;
		}
		
		double upCorrect = 0.0625;
		double rightCorrect = 0.0625;
		double forwardCorrect = 0.0625;
		double walkCorrect = -0.0625;
		
		double xcLeft = ((xLeft / 2) - (right * rightCorrect)) * 2;
		double zcLeft = ((zDistanceLeft / 2) - (forward * forwardCorrect)) * 2;

		double rotLeftSideX = xcLeft * cosine - zcLeft * sine;
		double yCornerTL = (((-yTop) - (-up * upCorrect + (walking * walkCorrect))) * 2) / rotationy;
		double yCornerBL = ((( + yBottom - yTop) - (-up * upCorrect + (walking * walkCorrect))) * 2) / rotationy;
		double rotLeftSideZ = zcLeft * cosine + xcLeft * sine;

		double xcRight = ((xRight / 2) - (right * rightCorrect)) * 2;
		double zcRight = ((zDistanceRight / 2) - (forward * forwardCorrect)) * 2;

		double rotRightSideX = xcRight * cosine - zcRight * sine;
		double yCornerTR = (((-yTop) - (-up * upCorrect + (walking * walkCorrect))) * 2) / rotationy;
		double yCornerBR = ((( + yBottom - yTop) - (-up * upCorrect + (walking * walkCorrect))) * 2) / rotationy;
		double rotRightSideZ = zcRight * cosine + xcRight * sine;

		double tex30 = 0;
		double tex40 = 128;
		double clip = 0.5;
		
		if(rotLeftSideZ < clip && rotRightSideZ < clip){
			return;
		}
		
		if(rotLeftSideZ < clip){
			double clip0 = (clip - rotLeftSideZ) / (rotRightSideZ - rotLeftSideZ);
			rotLeftSideZ = rotLeftSideZ + (rotRightSideZ - rotLeftSideZ) * clip0;
			rotLeftSideX = rotLeftSideX + (rotRightSideX - rotLeftSideX) * clip0;
			tex30 = tex30 + (tex40 - tex30) * clip0;
		}
		
		if(rotRightSideZ < clip){
			double clip0 = (clip - rotLeftSideZ) / (rotRightSideZ - rotLeftSideZ);
			rotRightSideZ = rotLeftSideZ + (rotRightSideZ - rotLeftSideZ) * clip0;
			rotRightSideX = rotLeftSideX + (rotRightSideX - rotLeftSideX) * clip0;
			tex40 = tex30 + (tex40 - tex30) * clip0;
		}
		
		double xPixelLeft = (rotLeftSideX / rotLeftSideZ * height + (width / 2));
		double xPixelRight = (rotRightSideX / rotRightSideZ * height + (width / 2));

		
		if (xPixelLeft >= xPixelRight) {
			return;
		}

		int xPixelLeftint = (int) (xPixelLeft);
		int xPixelRightint = (int) (xPixelRight);

		if (xPixelLeftint < 0) {
			xPixelLeftint = 0;
		}
		if (xPixelRightint > width) {
			xPixelRightint = width;
		}

		double yPixelLeftTop = ((yCornerTL / rotLeftSideZ * height) + (height / 2.0))  * rotationy;
		double yPixelLeftBottom = ((yCornerBL / rotLeftSideZ * height) + (height / 2.0))  * rotationy;
		double yPixelRightTop = ((yCornerTR / rotRightSideZ * height) + (height / 2.0))  * rotationy;
		double yPixelRightBottom = ((yCornerBR / rotRightSideZ * height) + (height / 2.0))  * rotationy;

		double tex1 = 1 / rotLeftSideZ;
		double tex2 = 1 / rotRightSideZ;
		double tex3 = tex30 / rotLeftSideZ;
		double tex4 = tex40 / rotRightSideZ - tex3;
		
		for (int x = xPixelLeftint; x < xPixelRightint; x++) {
			double pixelRotation = (x - xPixelLeft) / (xPixelRight - xPixelLeft);
			double zWall = (tex1 + (tex2 - tex1) * pixelRotation);
			
			if(zBufferWall[x] > zWall){
				continue;
			}
			zBufferWall[x] = zWall;
			
			int xTexture = (int)((tex3 + tex4 * pixelRotation) / zWall * 4);

			double yPixelTop = yPixelLeftTop + (yPixelRightTop - yPixelLeftTop) * pixelRotation;
			double yPixelBottom = yPixelLeftBottom + (yPixelRightBottom - yPixelLeftBottom) * pixelRotation;

			int yPixelTopint = (int) (yPixelTop);
			int yPixelBottomint = (int) (yPixelBottom);

			if (yPixelTopint < 0) {
				yPixelTopint = 0;
			}
			if (yPixelBottomint > height) {
				yPixelBottomint = height;
			}

			for (int y = yPixelTopint; y < yPixelBottomint; y++) {
				double pixelRotationY = (y - yPixelTop) / (yPixelBottom - yPixelTop);
				int yTexture = (int)(8 * pixelRotationY * 4);
				try {
					pixels[x + y * width] = t.r.pixels[((xTexture) & 127)+t.texVar+((yTexture) & 127) * 128];
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
					Log.Log(e.toString(), false);
					continue;
				}
				zBuffer[x + y * width] = 1 / (tex1 + (tex2 - tex1) * pixelRotation) * 10/(renderDistance);
			}
		}
	}

	public void renderFace(Face f, Texture t) {

		double xLeft=0;
		double xRight=0;
		double zDistanceLeft=0; 
		double zDistanceRight=0;
		double yBottom=0; 
		double yTop=0;
		
		ArrayList<Vertex> vert = f.vertices;
		
		for(int i = 0; i < vert.size(); i++){
			Vertex cvert = vert.get(i);
			if(i == 0){
				xLeft = cvert.x;
				xRight = cvert.x;
				zDistanceLeft = cvert.z;
				zDistanceRight = cvert.z;
				yBottom = (cvert.y+1)/2;
				yTop = (cvert.y)/2;
			}
			if(cvert.x < xLeft){
				xLeft = cvert.x;
			}
			if(cvert.x > xRight){
				xRight = cvert.x;
			}
			if(cvert.z < zDistanceLeft){
				zDistanceLeft = cvert.z;
			}
			if(cvert.z > zDistanceRight){
				zDistanceRight = cvert.z;
			}
			if(cvert.y > yTop){
				yTop = cvert.y;
			}
			if(cvert.y < yBottom){
				yBottom = cvert.y;
			}
		}
		
		double upCorrect = 0.0625;
		double rightCorrect = 0.0625;
		double forwardCorrect = 0.0625;
		double walkCorrect = -0.0625;
		
		double xcLeft = ((xLeft / 2) - (right * rightCorrect)) * 2;
		double zcLeft = ((zDistanceLeft / 2) - (forward * forwardCorrect)) * 2;

		double rotLeftSideX = xcLeft * cosine - zcLeft * sine;
		double yCornerTL = (((-yTop) - (-up * upCorrect + (walking * walkCorrect))) * 2) / rotationy;
		double yCornerBL = ((( + yBottom - yTop) - (-up * upCorrect + (walking * walkCorrect))) * 2) / rotationy;
		double rotLeftSideZ = zcLeft * cosine + xcLeft * sine;

		double xcRight = ((xRight / 2) - (right * rightCorrect)) * 2;
		double zcRight = ((zDistanceRight / 2) - (forward * forwardCorrect)) * 2;

		double rotRightSideX = xcRight * cosine - zcRight * sine;
		double yCornerTR = (((-yTop) - (-up * upCorrect + (walking * walkCorrect))) * 2) / rotationy;
		double yCornerBR = ((( + yBottom - yTop) - (-up * upCorrect + (walking * walkCorrect))) * 2) / rotationy;
		double rotRightSideZ = zcRight * cosine + xcRight * sine;

		double tex30 = 0;
		double tex40 = 128;
		double clip = 0.5;
		
		if(rotLeftSideZ < clip && rotRightSideZ < clip){
			return;
		}
		
		if(rotLeftSideZ < clip){
			double clip0 = (clip - rotLeftSideZ) / (rotRightSideZ - rotLeftSideZ);
			rotLeftSideZ = rotLeftSideZ + (rotRightSideZ - rotLeftSideZ) * clip0;
			rotLeftSideX = rotLeftSideX + (rotRightSideX - rotLeftSideX) * clip0;
			tex30 = tex30 + (tex40 - tex30) * clip0;
		}
		
		if(rotRightSideZ < clip){
			double clip0 = (clip - rotLeftSideZ) / (rotRightSideZ - rotLeftSideZ);
			rotRightSideZ = rotLeftSideZ + (rotRightSideZ - rotLeftSideZ) * clip0;
			rotRightSideX = rotLeftSideX + (rotRightSideX - rotLeftSideX) * clip0;
			tex40 = tex30 + (tex40 - tex30) * clip0;
		}
		
		double xPixelLeft = (rotLeftSideX / rotLeftSideZ * height + (width / 2));
		double xPixelRight = (rotRightSideX / rotRightSideZ * height + (width / 2));

		
		if (xPixelLeft >= xPixelRight) {
			return;
		}

		int xPixelLeftint = (int) (xPixelLeft);
		int xPixelRightint = (int) (xPixelRight);

		if (xPixelLeftint < 0) {
			xPixelLeftint = 0;
		}
		if (xPixelRightint > width) {
			xPixelRightint = width;
		}

		double yPixelLeftTop = ((yCornerTL / rotLeftSideZ * height) + (height / 2.0))  * rotationy;
		double yPixelLeftBottom = ((yCornerBL / rotLeftSideZ * height) + (height / 2.0))  * rotationy;
		double yPixelRightTop = ((yCornerTR / rotRightSideZ * height) + (height / 2.0))  * rotationy;
		double yPixelRightBottom = ((yCornerBR / rotRightSideZ * height) + (height / 2.0))  * rotationy;

		double tex1 = 1 / rotLeftSideZ;
		double tex2 = 1 / rotRightSideZ;
		double tex3 = tex30 / rotLeftSideZ;
		double tex4 = tex40 / rotRightSideZ - tex3;
		
		for (int x = xPixelLeftint; x < xPixelRightint; x++) {
			double pixelRotation = (x - xPixelLeft) / (xPixelRight - xPixelLeft);
			double zWall = (tex1 + (tex2 - tex1) * pixelRotation);
			
			/*if(zBufferWall[x] > zWall){
				continue;
			}
			zBufferWall[x] = zWall;*/
			
			int xTexture = (int)((tex3 + tex4 * pixelRotation) / zWall *4);

			double yPixelTop = yPixelLeftTop + (yPixelRightTop - yPixelLeftTop) * pixelRotation;
			double yPixelBottom = yPixelLeftBottom + (yPixelRightBottom - yPixelLeftBottom) * pixelRotation;

			int yPixelTopint = (int) (yPixelTop);
			int yPixelBottomint = (int) (yPixelBottom);

			if (yPixelTopint < 0) {
				yPixelTopint = 0;
			}
			if (yPixelBottomint > height) {
				yPixelBottomint = height;
			}

			for (int y = yPixelTopint; y < yPixelBottomint; y++) {
				double pixelRotationY = (y - yPixelTop) / (yPixelBottom - yPixelTop);
				int yTexture = (int)(8 * pixelRotationY *32);
				try {
					pixels[x + y * width] = t.r.pixels[((xTexture) & 1023)+t.texVar+((yTexture) & 1023) * 1024];
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
					Log.Log(e.toString(), false);
					continue;
				}
				zBuffer[x + y * width] = 1 / (tex1 + (tex2 - tex1) * pixelRotation) * 10/(renderDistance);
			}
		}
	}
	
	public void renderDistanceLimiter() {
		for (int i = 0; i < width * height; i++) {
			int colour = pixels[i];
			int brightness = (int) (renderDistance / (zBuffer[i]));

			if (brightness < d.brightness) {
				brightness = d.brightness;
			}
			if (brightness > 255) {
				brightness = 255;
			}

			int r = (colour >> 16) & 0xff;
			int g = (colour >> 8) & 0xff;
			int b = (colour) & 0xff;

			r = r * brightness / 255;
			g = g * brightness / 255;
			b = b * brightness / 255;

			pixels[i] = (r << 16 | g << 8 | b);
		}
	}
}


