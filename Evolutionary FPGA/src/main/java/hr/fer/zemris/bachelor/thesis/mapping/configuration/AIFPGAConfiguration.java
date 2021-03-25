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
	protected FPGAModelConfiguration configuration;
	
	/**
	 * clb indexes for saving clb positions 
	 */
	protected int[] clbIndexes;
	
	/**
	 * Saves pin positions
	 */
	protected int[] pinIndexes;

	
	public AIFPGAConfiguration(FPGAModelConfiguration configuration, int[] clbIndexes, int[] pinIndexes) {
		super();
		this.configuration = configuration;
		this.clbIndexes = clbIndexes;
		this.pinIndexes = pinIndexes;
	}

	public FPGAModelConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(FPGAModelConfiguration configuration) {
		this.configuration = configuration;
	}

	public int[] getClbIndexes() {
		return clbIndexes;
	}

	public void setClbIndexes(int[] clbIndexes) {
		this.clbIndexes = clbIndexes;
	}

	public int[] getPinIndexes() {
		return pinIndexes;
	}

	public void setPinIndexes(int[] pinIndexes) {
		this.pinIndexes = pinIndexes;
	}
	
	
	
	

}
