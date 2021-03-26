package hr.fer.zemris.bachelor.thesis.mapping.configuration;


import hr.fer.zemris.fpga.FPGAModel.FPGAModelConfiguration;

/**
 * Class that will serve as individual in genetic algorithm application.
 * @author andi
 *
 */
public class AIFPGAConfiguration {
	
	/**
	 * Saves configuration
	 */
	public FPGAModelConfiguration configuration;
	
	/**
	 * clb indexes for saving clb positions 
	 */
	public int[] clbIndexes;
	
	/**
	 * Saves pin positions
	 */
	public int[] pinIndexes;

	
	public AIFPGAConfiguration(FPGAModelConfiguration configuration, int[] clbIndexes, int[] pinIndexes) {
		super();
		this.configuration = configuration;
		this.clbIndexes = clbIndexes;
		this.pinIndexes = pinIndexes;
	}

}
