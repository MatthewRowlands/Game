package Entity;

public class Entity <E>{

	double initialx;
	double initialy;
	double initialz;
	public double x;
	public double y;
	public double z;
	public double heightstep = 0.15;
	
	public Entity(double x, double y, double z) {
		this.x = x;
		this.y = y+heightstep;
		this.z = z;
		this.initialx = x;
		this.initialy = y+heightstep;
		this.initialz = z;
	}

    E createContents(Class<E> clazz)
    {
        try {
			return clazz.newInstance();
		} catch (Exception e) {
			return null;
		}
    }
}
