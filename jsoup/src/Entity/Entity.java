package Entity;

public class Entity <E>{

	public Entity(double x, double y, double z) {

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
