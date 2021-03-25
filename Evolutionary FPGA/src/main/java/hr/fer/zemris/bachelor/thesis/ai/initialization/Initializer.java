package hr.fer.zemris.bachelor.thesis.ai.initialization;

/**
 * Used in some kind of initialization process. Pins, switch boxes..
 * @author andi
 *
 */
public interface Initializer<T> {
	
	/**
	 * @return array of created objects
	 */
	T[] initialize();

}
