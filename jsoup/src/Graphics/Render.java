package Graphics;


public class Render {

	public final int width, height;
	public final int[] pixels;

	public Render(int width, int height) {
		this.width = width;
		this.height = height;
		pixels = new int[width * height];
	}

	public void draw(Render render, int xOff, int yOff) {
		for (int y = 0; y < render.height; y++) {
			int yPixel = y + yOff;
			if (yPixel < 0 || yPixel >= height) {
				continue;
			}
			for (int x = 0; x < render.width; x++) {
				int xPixel = x + xOff;
				if (xOff < 0 || xPixel >= width) {
					continue;
				}

				int alpha = render.pixels[x + y * render.width];

				if (alpha > 0) {
					pixels[xPixel + yPixel * render.width] = alpha;
					//Display.pixelsload++;
				}else{
					
				}
			}
		}
	}
}
