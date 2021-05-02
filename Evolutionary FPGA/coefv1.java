package hr.fer.zemris.bachelor.thesis.evaluator;

/**
 * Coefficients used in genetic algorithm
 * @author andi
 *
 */
public class Coefficients {
	
	/**
	 * CLB connected to pin
	 */
	public static double OUTPUT_PRIZE = 10;
	
	/**
	 * CLB not connected correctly
	 */
	public static double OUTPUT_PENALTY = 0; //currently not used
	
	/**
	 * Input okay
	 */
	public static double INPUT_PRIZE = 0; //currently not used
	
	/**
	 * Input not okay
	 */
	public static double INPUT_PENALTY = -100;
	
	/**
	 * If we expect clb and get pin or viceversa
	 */
	public static double INPUT_WRONG_TYPE = -250;
	
	/**
	 * What is connected on output pin
	 */
	public static double OUTPUT_WRONG_TYPE = -250;

	
	/**
	 * If more than SW_BOX_CONNECTIONS_MAX in the box
	 */
	public static double SW_BOX_MAX_OVERFLOW_PENALTY = -50.0;
	
	/**
	 * Penalty if wire goes where it shouldn't
	 */
	public static double SW_BOX_WIRE_TO_NULL_PENALTY = -1.0;
	
	/**
	 * Prize for okay wire
	 */
	public static double SW_BOX_WIRE_OKAY = 1;
	
	/**
	 * Maybe for optimizing some solutions
	 */
	public static double INPUT_DISTANCE_FACTOR = 1;
	
	/**
	 * Needs values tweaking
	 */
	public static double OUTPUT_DISTANCE_FACTOR = 1;
	
	/**
	 * More than one label on some segment
	 */
	public static double COLLISION_PENALTY = -1800;	
	
	/**
	 * Nothing is connected
	 */
	public static double BLACK_LABEL = -500;
	
	/**
	 * Max connections in one switch box: model based estimation
	 * @param wiresCount
	 * @return
	 */
	public static int getMaxConnections(int wiresCount) {
		return 2*wiresCount -1;
	}
	
	

}

