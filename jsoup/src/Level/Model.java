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

public class Model {

	public ArrayList<Objects> LoadModel(String file) {
		String filepath = "res/maps/" + file + ".txt";
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		ArrayList<String> text = new ArrayList<String>();
		ArrayList<double[]> coords = new ArrayList<double[]>();
		try {
			
			File directory = new File("res/maps");
			boolean success = directory.mkdirs();
			File f = new File(filepath);
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
			
			for(int i = 0; i < text.size(); i++){
				if(!text.get(i).equals("") && !text.get(i).trim().startsWith("#")){
					objects[i] = text.get(i);
					
					if(text.get(i).trim().startsWith("v")){
						System.out.print("v ");
						String[] line = objects[i].replace("v ", "").split(" ");
						double[] vertex = new double[line.length];
						for(int ii = 0; ii < line.length; ii++){
							vertex[ii] = Double.parseDouble(line[ii]);
						}
						System.out.println(vertex[0]+" "+vertex[1]+" "+vertex[2]);
						coords.add(vertex);
					}

					if(text.get(i).trim().startsWith("f")){
						System.out.print("f ");
					//double[] num = new double[line.length];
					//for(int ii = 0; ii < line.length; ii++){
					//	num[ii] = Double.parseDouble(line[ii]);
					//	System.out.print(num[ii]+", ");
					//}
					//System.out.println();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
