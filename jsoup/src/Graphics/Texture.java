package Graphics;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Texture {
	//public static String floorf = "/textures/Ground1.png";
	//public static Render floor = loadBitmap(floorf);
	//public static String rooff = "/textures/Sky.png";
	//public static Render roof = loadBitmap(rooff);
	//public static String enemyf = "/textures/Fire2.png";
	//public static Render enemy = loadBitmap(enemyf);
	
	public Render r;
	public int texVar = 0;
	public String file;
	
	public Texture(String file){
		this.file = file;
		r = loadBitmap(file);
	}
	
	public static Render loadBitmap(String fileName){
		try{
			File file = new File("/textures");
			boolean success = file.mkdirs();
			if(success){
				System.err.println("Textures Do Not Exist!");
			}
			BufferedImage image = ImageIO.read(Texture.class.getResource(fileName));
			int width = image.getWidth();
			int height = image.getHeight();
			Render result = new Render(width, height);
			image.getRGB(0, 0, width, height, result.pixels, 0, width);
			return result;
		} catch (Exception e){
			System.out.println("KAPUT");
			throw new RuntimeException(e);
		}
	}
}
