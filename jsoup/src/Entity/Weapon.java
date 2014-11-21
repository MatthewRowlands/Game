package Entity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import Main.Display;

public class Weapon {
	
	public Weapon(int type, int id){
		String file = "/Weapons.txt";
	    FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    DataInputStream dis = null;
	    String text = "";
	    try {
	      fis = new FileInputStream(Weapon.class.getResource(file).getFile());
	      bis = new BufferedInputStream(fis);
	      dis = new DataInputStream(bis);
	      
	      while (dis.available() != 0) {
	        text+=dis.available()+" "+dis.readLine()+" ";
	      }

	      fis.close();
	      bis.close();
	      dis.close();
	      
	      System.out.println(text);

	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	}
	
	public static void main(String[] args){
		new Weapon(1,1);
	}
}
