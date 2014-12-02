package Model;

import java.util.ArrayList;

import Graphics.Texture;

public class Face {

	public Texture t;
	String name = "Default";
	public ArrayList<Vertex> vertices;
	
	public Face(ArrayList<Vertex> a) {
		this.vertices  = a;
	}

	public Face(ArrayList<Vertex> a, String name) {
		this.vertices = a;
		this.name = name;
	}
	
	public void setTexture(int index){
		t.texVar = index;
	}

	public void setMaterial(String cfile) {
		t = new Texture("/textures/"+cfile+".png");	
	}
}
