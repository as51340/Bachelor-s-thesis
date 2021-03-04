package hr.fer.zemris.bachelor.thesis.models;

/**
 * General pair that can be parametrized. Can be used in whole model
 * @author Andi Škrgat
 *
 * @param <T>
 * @param <U>
 */
public class Pair<T, U> {
	
	/**
	 * Object 1
	 */
	private T obj1;
	
	/**
	 * Object 2
	 */
	private U obj2;
	
	/**
	 * Empty constructor
	 */
	public Pair() {}
	
	/**
	 * Simple constructor
	 * @param obj1 object1
	 * @param obj2 object2
	 */
	public Pair(T obj1, U obj2) {
		this.obj1 = obj1;
		this.obj2 = obj2;
	}

	/**
	 * @return first objects
	 */
	public T getObj1() {
		return obj1;
	}

	/**
	 * Set first object
	 * @param obj1 new first object
	 */
	public void setObj1(T obj1) {
		this.obj1 = obj1;
	}

	/**
	 * @return second object
	 */
	public U getObj2() {
		return obj2;
	}

	/**
	 * Set second object
	 * @param obj2 new object
	 */
	public void setObj2(U obj2) {
		this.obj2 = obj2;
	}
	
	
	

}
