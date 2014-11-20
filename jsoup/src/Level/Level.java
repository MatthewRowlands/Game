package Level;

import java.util.Random;

import Main.Display;

public class Level {
	public Block[] blocks;
	public final int width;
	public final int height;

	
	public Level (int width, int height){
		this.width = width;
		this.height = height;
		blocks = new Block[width * height];
		Random random = new Random();
		
		for (int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				Block block = null;
				
				if(random.nextInt(10) == 0){
					block = new SolidBlock();
					Display.blockcount++;
				}else{
					block = new Block();
				}
				blocks[x + y * width] = block;
			}
		}
	}
	
	public Block create(int x, int y){
		if(x < 0 || y < 0 || x >= width || y >= height){
			return Block.solidWall;
		}
		return blocks[x + y * width];		
	}
}
