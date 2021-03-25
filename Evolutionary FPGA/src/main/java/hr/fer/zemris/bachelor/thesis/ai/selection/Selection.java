package hr.fer.zemris.bachelor.thesis.ai.selection;

import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;

/**
 * Interface that specifies method for obtaining individual from population of {@linkplain AIFPGAConfiguration}. 
 * Different algorithms will differently implement this method. For example: k-tournament selection, Boltzmann
 * selection etc.
 * @author andi
 *
 */
public interface Selection {
	
	/**
	 * @param population
	 * @return individual from population using some standard
	 */
	AIFPGAConfiguration select(AIFPGAConfiguration[] population);

}
