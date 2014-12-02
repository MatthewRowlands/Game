package Model;

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

	public ArrayList<Face> model = new ArrayList<Face>();
	String cfile = "Concrete";
	int texloc = 0;
	
	public ArrayList<Face> LoadModel(String file) {
		String filepath = "res/Models/" + file + ".txt";
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		ArrayList<String> text = new ArrayList<String>();
		ArrayList<Vertex> coords = new ArrayList<Vertex>();
		try {
			
			File directory = new File("res/Models");
			boolean success = directory.mkdirs();
			File f = new File(filepath);
			System.out.println(f.getAbsolutePath()+"\n");
			boolean exists = f.exists();
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
				if(!text.get(i).equals("")){
					objects[i] = text.get(i);
					
					if(text.get(i).trim().startsWith("#")){
						//System.out.println(text.get(i));
					}
					
					else if(text.get(i).trim().startsWith("v")){//check if starts with "v" after removing spaces
						System.out.print("v ");//output v
						String[] line = objects[i].replace("v", "").replaceAll("(^\\s+|\\s+$)", "").split("\\s+");//create array of strings to store 
						double[] vertex = new double[line.length];
						for(int ii = 0; ii < line.length; ii++){
							String tstore = line[ii].trim();
							System.out.print(tstore+" ");
							vertex[ii] = Double.parseDouble(tstore);
						}
						System.out.println();
						coords.add(new Vertex(vertex[0],vertex[1],vertex[2]));
					}

					else if(text.get(i).trim().startsWith("f")){
						System.out.print("f ");
						String[] line = objects[i].replace("f", "").replaceAll("(^\\s+|\\s+$)", "").split(" +");
						ArrayList<Vertex> facev = new ArrayList<Vertex>();
						for(int ii = 0; ii < line.length; ii++){
							line[ii].trim();
							int index = Math.abs(Integer.parseInt(line[ii]));
							System.out.print(index+" ");
							facev.add(coords.get(index-1));
						}
						model.add(new Face(facev));
						model.get(model.size()-1).setMaterial(cfile);
						model.get(model.size()-1).setTexture(texloc);
						System.out.println();
					}
					
					else if(text.get(i).trim().startsWith("mtllib")){
						String[] line = objects[i].replaceAll("(^\\s+|\\s+$)", "").split(" +");
						String filename = line[1].replace(".mtl", "");
						System.out.println("mtllib "+filename+".mtl");
						this.cfile = filename;
					}
					
					else if(text.get(i).trim().startsWith("usemtl")){
						texloc = 0;//TODO
						System.out.println("usemtl "+texloc);
					}
					else{
						//System.out.println(text.get(i).trim());
					}
				}else{
					System.out.println();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}
}
