package Graphics;

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
	public double renderDistance = Display.RenderDist;
	double forward, right, up, cosine, sine, walking, rotation, rotationy;

	double floorpos = Display.floorpos;
	double ceilingpos = Display.ceilingpos;
	int c = 0;
	int num = 1;
	
	public Texture floor = new Texture("/textures/Ground3.png");
	public Texture roof = new Texture("/textures/Sky.png");
	public Render3D(int width, int height) {
		super(width, height);
		zBuffer = new double[width * height];
		zBufferWall = new double[width];
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
		
		Display.x = (int) right;
		Display.y = (int) up;
		Display.z = (int) forward;
		Display.rotationsin = sine;
		Display.rotationcos = cosine;
		Display.rotationy = rotationy;
		Display.rotation = rotation;
		
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
					if(up > -Display.floorpos/2)
						pixels[x + y * width] = floor.r.pixels[(xPix & 1023)+floor.texVar+(yPix & 1023) * 1024];
					else
						pixels[x + y * width] = 0;
				}else{
					if(up < Display.ceilingpos)
						pixels[x + y * width] = roof.r.pixels[(xPix & 1023)+roof.texVar+(yPix & 1023) * 1024];
					else
						pixels[x + y * width] = 0;
				}
				if (z > renderDistance/5) {
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
		double tex40 = 8;
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
					pixels[x + y * (width+2)] = t.r.pixels[((xTexture) & 127)+t.texVar+((yTexture) & 127) * 128];
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
					Log.Log(e.toString(), false);
					continue;
				}
				zBuffer[x + y * width] = 1 / (tex1 + (tex2 - tex1) * pixelRotation) * 10/(renderDistance/5000);
			}
		}

	}

	public void renderFace(Face f, Texture t) {
			
		Vertex topl = f.vertices.get(0);
		Vertex topr = f.vertices.get(1);
		Vertex botl = f.vertices.get(2);
		Vertex botr = f.vertices.get(3);
		
		//if(yTop < -0.5){
		//	return;
		//}
		
		double upCorrect = 0.0625;
		double rightCorrect = 0.0625;
		double forwardCorrect = 0.0625;
		double walkCorrect = -0.0625;
		
		double xcLeft = ((topl.x / 2) - (right * rightCorrect)) * 2;
		double zcLeft = ((topl.z / 2) - (forward * forwardCorrect)) * 2;

		double rotLeftSideX = xcLeft * cosine - zcLeft * sine;
		double yCornerTL = (((-topl.y) - (-up * upCorrect + (walking * walkCorrect))) * 2) / rotationy;
		double yCornerBL = (((-botl.y) - (-up * upCorrect + (walking * walkCorrect))) * 2) / rotationy;
		double rotLeftSideZ = zcLeft * cosine + xcLeft * sine;

		double xcRight = ((botr.x / 2) - (right * rightCorrect)) * 2;
		double zcRight = ((botr.z / 2) - (forward * forwardCorrect)) * 2;

		double rotRightSideX = xcRight * cosine - zcRight * sine;
		double yCornerTR = (((-topr.y) - (-up * upCorrect + (walking * walkCorrect))) * 2) / rotationy;
		double yCornerBR = (((-botr.y) - (-up * upCorrect + (walking * walkCorrect))) * 2) / rotationy;
		double rotRightSideZ = zcRight * cosine + xcRight * sine;

		double tex30 = 0;
		double tex40 = 8;
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
					pixels[x + y * (width)] = t.r.pixels[((xTexture) & 127)+t.texVar+((yTexture) & 127) * 128];
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
					Log.Log(e.getStackTrace()+"\n"+e.getMessage(), false);
					continue;
				}
				zBuffer[x + y * width] = 1 / (tex1 + (tex2 - tex1) * pixelRotation) * 10/(renderDistance/5000);
			}
		}
	}
	
	public void renderDistanceLimiter() {
		for (int i = 0; i < width * height; i++) {
			int colour = pixels[i];
			int brightness = (int) (renderDistance / (zBuffer[i]));

			if (brightness < Display.brightness) {
				brightness = Display.brightness;
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


