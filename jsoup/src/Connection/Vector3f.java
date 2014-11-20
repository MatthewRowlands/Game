package Connection;

import java.io.Serializable;

import javax.swing.JTextArea;

public class Vector3f implements Serializable {

	private static final long serialVersionUID = 1L;
	public double x;
	public double y;
	public double z;

	public Vector3f(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3f(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3f() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public void OutputToConsole(String additionaltext) {
		System.out.println(additionaltext + " x:" + x + " y:" + y + " z:" + z);
	}

	public void appendToTextArea(JTextArea ta, String additionaltext) {
		ta.append(additionaltext + " x:" + x + " y:" + y + " z:" + z);
	}

}
