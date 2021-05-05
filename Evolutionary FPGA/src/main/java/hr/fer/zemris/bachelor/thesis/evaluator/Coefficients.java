package hr.fer.zemris.bachelor.thesis.evaluator;

/**
 * Coefficients used in genetic algorithm
 * @author andi
 *
 */
public class Coefficients {
	
	/**
	 * CLB not connected correctly
	 */
	public static double OUTPUT_PENALTY = -1.25;
	
	/**
	 * What is connected on output pin
	 */
	public static double OUTPUT_WRONG_TYPE = -1.5;
	
	/**
	 * Input not okay
	 */
	public static double INPUT_PENALTY = -1.25;
	
	/**
	 * If we expect clb and get pin or viceversa
	 */	
	public static double INPUT_WRONG_TYPE = -1.5;
	
	/**
	 * More than one label on some segment
	 */
//	public static double COLLISION_PENALTY = -20.0;	
	public static double COLLISION_PENALTY = -0.0;	
	
	/**
	 * Nothing is connected
	 */
	public static double BLACK_LABEL = -7.75;
//	public static double BLACK_LABEL = -0;
		
	/**
	 * If more than SW_BOX_CONNECTIONS_MAX in the box
	 */
//	public static double SW_BOX_MAX_OVERFLOW_PENALTY = -0.75;
	public static double SW_BOX_MAX_OVERFLOW_PENALTY = -0;

	/**
	 * Maybe for optimizing some solutions
	 */
	public static double INPUT_DISTANCE_FACTOR = 1.0;
	
	/**
	 * Needs values tweaking
	 */
	public static double OUTPUT_DISTANCE_FACTOR = 1.0;
	
	/**
	 * If there are more connections from one wire to one group
	 */
	public static double MULTIPLE_VALUES = -0;
	
	/**
	 * If wire from switch box is connected to wire that has label null
	 */
//	public static double SW_WIRE_TO_NULL = -2.25;
	
	public static double SW_WIRE_TO_NULL = -0;
	
	/**
	 * No connection in switch box
	 */
	public static double SW_EMPTY = -0;
	
	/**
	 * Input pin isn't propagated 
	 */
//	public static double INPUT_TO_NOWHERE = -2.75;
	
	public static double INPUT_TO_NOWHERE = -0;
	
	/**
	 * Max connections in one switch box: model based estimation
	 * @param wiresCount
	 * @return
	 */
	public static int getMaxConnections(int params) {
		return 2*params -1;
	}
	
	

}
