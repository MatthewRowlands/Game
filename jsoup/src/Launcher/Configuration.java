package Launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import Main.Display;

public class Configuration {
	
	Properties properties = new Properties();
	
	public void saveConfiguration(String s1, String s2, int width, int height){
		String path = "res/settings";
		String filename = "/config";
		try{
			//File directory = new File(path);
				//boolean success = directory.mkdirs();
			File file = new File(path+filename+".xml");
				boolean exists = file.exists();
			if(!exists){
				file.createNewFile();
			}	
			OutputStream write = new FileOutputStream(path+filename+".xml");
			properties.setProperty(s1, Integer.toString(width));
			properties.setProperty(s2, Integer.toString(height));
			properties.storeToXML(write, "Resolution");
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void loadConfiguration(String path){
		try{
			InputStream read = new FileInputStream(path);
			properties.loadFromXML(read);
			String width = properties.getProperty("Width");
			String height = properties.getProperty("Height");
			setResolution(Integer.parseInt(width), Integer.parseInt(height));	
			System.out.println("[Resolution] "+width+", "+height);
			read.close();
		}catch (FileNotFoundException e){	
			saveConfiguration("Width","Height",800,600);
			loadConfiguration(path);
		}catch (IOException e){	
			e.printStackTrace();
		}
	}
	public void setResolution(int width, int height){
		Display.width = width;
		Display.height = height;
	}
}
