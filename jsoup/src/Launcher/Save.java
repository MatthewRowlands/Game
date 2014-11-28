package Launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import Entity.Weapon;
import Main.Display;

public class Save {
	
	Properties loadout = new Properties();
	
	public void saveConfiguration(Weapon wep1, Weapon wep2){
		String path = "res/settings";
		String filename = "/loadout";
		try{
			File directory = new File(path);
				boolean success = directory.mkdirs();
			File file = new File(path+filename+".xml");
				boolean exists = file.exists();
			if(!exists){
				file.createNewFile();
			}	
			System.out.println(directory.getAbsolutePath());
			System.out.println(success);
			System.out.println(file.getAbsolutePath());
			System.out.println(exists);
			
			OutputStream write = new FileOutputStream(path+filename+".xml");
			loadout.setProperty("Weapon 1", Integer.toString(wep1.ID));
			loadout.setProperty("Weapon 2", Integer.toString(wep2.ID));
			loadout.storeToXML(write, "Loadout");
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void loadConfiguration(String path){
		try{
			InputStream read = new FileInputStream(path);
			loadout.loadFromXML(read);
			String wep1 = loadout.getProperty("Weapon 1");
			String wep2 = loadout.getProperty("Weapon 2");
			setWeapons(Integer.parseInt(wep1), Integer.parseInt(wep2));	
			read.close();
		}catch (FileNotFoundException e){	
			saveConfiguration(new Weapon(1),new Weapon(3));
			loadConfiguration(path);
		}catch (IOException e){	
			e.printStackTrace();
		}
	}
	public void setWeapons(int wep1ID, int wep2ID){
		Weapon wep1 = new Weapon(wep1ID);
		Weapon wep2 = new Weapon(wep2ID);
		System.out.println("[Loadout] "+wep1.name+", "+wep2.name);
		Display.w1 = wep1;
		Display.w2 = wep2;
		Display.initialaccuracy = wep1.accuracy;
		Display.startaccuracy = Display.initialaccuracy;
		Display.accuracy = Display.startaccuracy;
		Display.firerate = wep1.firerate;
		Display.firemode = wep1.firemode;
		Display.sWeaponAmmo = wep1.WeaponAmmo;
		Display.WeaponAmmo = Display.sWeaponAmmo;
		Display.reloadspeed = wep1.reloadspeed * 1000;
		Display.WeaponDamage = wep1.WeaponDamage;
	}
}
