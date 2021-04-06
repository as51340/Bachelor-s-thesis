package hr.fer.zemris.bachelor.thesis.ai.selection;

import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;

/**
 * Interface that specifies method for obtaining individual from population of {@linkplain AIFPGAConfiguration}. 
 * Different algorithms will differently implement this method. For example: k-tournament selection, Boltzmann
 * selection etc.
 * @author andi
 *
 */
public interface Selector {
	
	/**
	 * @param population
	 * @return individual index from population using some standard
	 */
	int select(double[] fitnesses, boolean flag);

}
