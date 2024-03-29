package hr.fer.zemris.bachelor.thesis.evaluator;

/**
 * Coefficients used in genetic algorithm
 * @author andi
 *
 */
public class Coefficients {
	
	/**
	 * Nothing on output
	 */
	public static double OUTPUT_BLACK_LABEL = -22.25;
	
	/**
	 * CLB not connected correctly
	 */
	public static double OUTPUT_PENALTY = -2.75;
	
	/**
	 * What is connected on output pin
	 */
	public static double OUTPUT_WRONG_TYPE = -20.5;
	
	/**
	 * Input not okay
	 */
	public static double INPUT_PENALTY = -2.75;
	
	/**
	 * Output pin on input
	 */
	public static double INPUT_WRONG_PIN_TYPE = -6.75;
	
	/**
	 * If we expect clb and get pin or viceversa
	 */	
	public static double INPUT_WRONG_TYPE = -22.25;
	
	/**
	 * More than one same input occurrs
	 */
	public static double INPUT_MULTIPLES = -5.25; // izbaci iz lokalnog optimuma
	
	/**
	 * More than one label on some segment
	 */
	public static double COLLISION_PENALTY = -35.0;	
	
	/**
	 * Nothing is connected
	 */
	public static double BLACK_LABEL = -27.25;
		
	/**
	 * If more than SW_BOX_CONNECTIONS_MAX in the box
	 */
	public static double SW_BOX_MAX_OVERFLOW_PENALTY = -2.5;
	
	/**
	 * If there are more connections from one wire to one group
	 */
	public static double MULTIPLE_VALUES = -3.5;
	
	/**
	 * If wire from switch box is connected to wire that has label null
	 */
	public static double SW_WIRE_TO_NULL = -0.75;
	
	/**
	 * Switch box empty
	 */
	public static double SW_EMPTY = -3.25;
	
	/**
	 * Input pin isn't propagated 
	 */
	public static double INPUT_TO_NOWHERE = -8.75;
	
	/**
	 * Output is not propagated - punish only if needs to be punished
	 */
	public static double OUTPUT_TO_NOWHERE = -8.25;
	
	/**
	 * Max connections in one switch box: model based estimation
	 * @param wiresCount
	 * @return
	 */
	public static int getMaxConnections(int wiresCount) {
		return 3;
	}
	
	

}
