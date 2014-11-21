package Entity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import Main.Display;

public class Weapon {
	public double accuracy = 0.05;
	public double firerate = 15;
	public double WeaponDamage = 35;
	public boolean SemiAuto = false;
	public boolean FullAuto = !SemiAuto;
	public int WeaponAmmo = 30;
	public String name = "";
	public int ID = 0;
	public double reloadspeed = 3.0;
	
	public Weapon(int id){
		this.ID = id;
		String file = "/Weapons.txt";
	    FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    DataInputStream dis = null;
	    ArrayList<String> text = new ArrayList<String>();
	    try {
	      fis = new FileInputStream(Weapon.class.getResource(file).getFile());
	      bis = new BufferedInputStream(fis);
	      dis = new DataInputStream(bis);
	      
	      
	      while (dis.available() != 0) {
	    	  text.add(dis.readLine());
	      }

	      fis.close();
	      bis.close();
	      dis.close();
	      
	      String gun = text.get(id);

	      String name, firerate, damage, accuracy, ammo, firemode, reloadtime;
	      String[] split = gun.split(" ");
	      name = split[1];
	      firerate = split[2];
	      damage = split[3];
	      accuracy = split[4];
	      ammo = split[5];
	      firemode = split[6];
	      reloadtime = split[7];
	      
	      this.name = name;
	      this.firerate = Double.parseDouble(firerate);
	      this.WeaponDamage = Double.parseDouble(damage);
	      this.accuracy = Double.parseDouble(accuracy);
	      this.WeaponAmmo = Integer.parseInt(ammo);
	      this.reloadspeed = Double.parseDouble(reloadtime);
	      SemiAuto = firemode.equals("false");
	      FullAuto = !SemiAuto;      

	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	}
}
