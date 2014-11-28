package Level;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import Entity.Objects;
import Entity.Weapon;

public class Map {

	public ArrayList<Objects> LoadMap(String file) {
		String filepath = "res/maps/" + file + ".txt";
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		ArrayList<String> text = new ArrayList<String>();
		ArrayList<Objects> coords = new ArrayList<Objects>();
		try {
			
			File directory = new File("res/maps");
			System.out.println(directory.getAbsolutePath());
			boolean success = directory.mkdirs();
			System.out.println(success);
			File f = new File(filepath);
			System.out.println(f.getAbsolutePath());
			boolean exists = f.exists();
			System.out.println(exists);
			if (!exists) {
				f.createNewFile();
				return null;
			}
			
			fis = new FileInputStream(f);
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);

			while (dis.available() != 0) {
				text.add(dis.readLine());
			}

			fis.close();
			bis.close();
			dis.close();
			
			String[] objects = new String[text.size()];
			for(int i = 1; i < text.size(); i++){
				if(!text.get(i).equals("")){
					objects[i] = text.get(i);
					String[] line = objects[i].split(" ");
					double[] num = new double[line.length];
					for(int ii = 0; ii < line.length; ii++){
						num[ii] = Double.parseDouble(line[ii].replace(",", ""));
						System.out.print(num[ii]+", ");
					}
					coords.add(new Objects(num[0], num[1], num[2]));
					System.out.println();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return coords;
	}
}
