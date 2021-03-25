package hr.fer.zemris.bachelor.thesis.mapping.configuration;

import hr.fer.zemris.bachelor.thesis.ai.initialization.Initializer;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.FPGAModel.FPGAModelConfiguration;

/**
 * Initializer for {@linkplain AIFPGAConfiguration}. Everything is set to -1;
 * @author andi
 *
 */
public class AIFPGAConfigurationInitializer implements Initializer<AIFPGAConfiguration>{

	/**
	 * Population size
	 */
	protected int population_size;
	
	/**
	 * FPGAmodel
	 */
	protected FPGAModel model;
	
	
	public AIFPGAConfigurationInitializer(int population_size, FPGAModel model) {
		super();
		this.population_size = population_size;
		this.model = model;
	}


	@Override
	public AIFPGAConfiguration[] initialize() {
		AIFPGAConfiguration[] population = new AIFPGAConfiguration[population_size];
		int clbs_size = model.columns * model.rows;
		int pins_size = 2*model.pinsPerSegment*(model.rows + model.columns);
		for(int i = 0; i < population_size; i++) {
			FPGAModelConfiguration conf = model.newConfiguration();
			int[] clbIndexes = new int[clbs_size];
			int[] pinIndexes = new int[pins_size];
			for(int j = 0; j < clbs_size; j++) {
				clbIndexes[j] = -1;
			}
			for(int j = 0; j < pins_size; j++) {
				pinIndexes[j] = -1;
			}
			AIFPGAConfiguration individual = new AIFPGAConfiguration(conf, clbIndexes, pinIndexes);
			population[i] = individual;
		}
		return population;
	}
	
}
