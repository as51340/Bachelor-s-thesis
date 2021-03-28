package hr.fer.zemris.bachelor.thesis.mapping.configuration;

/**
 * Class that will have implementations of different useful methods used by {@linkplain AIFPGAConfiguration}
 * @author andi
 *
 */
public class ConfUtil {
	
	/**
	 * For now we consider situation in which we have constructed fitness as award not as penalty.
	 * @param fitness
	 * @param i1
	 * @param i2
	 * @param i3
	 */
	public static int getBestFromThree(double[] fitness, int i1, int i2, int i3) {
		if(fitness[i1] > fitness[i2] && fitness[i1] > fitness[i3]) {
			return i1;
		} else if(fitness[i2] > fitness[i1] && fitness[i2] > fitness[i3]) {
			return i2;
		} else {
			return i3;
		}
	}
	
	/**
	 * For now we consider situation in which we have constructed fitness as award not as penalty.
	 * @param fitness
	 * @param i1
	 * @param i2
	 * @param i3
	 */
	public static int getWorstFromThree(double[] fitness, int i1, int i2, int i3) {
//		for(int i = 0; i < fitness.length; i++) {
//			System.out.print(fitness[i] + " ");
//		}
		if(fitness[i1] < fitness[i2] && fitness[i1] < fitness[i3]) {
			return i1;
		} else if(fitness[i2] < fitness[i1] && fitness[i2] < fitness[i3]) {
			return i2;
		} else {
			return i3;
		}
	}

}
