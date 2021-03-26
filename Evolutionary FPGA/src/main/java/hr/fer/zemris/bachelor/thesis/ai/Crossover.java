package hr.fer.zemris.bachelor.thesis.ai;

import hr.fer.zemris.bachelor.thesis.ai.selection.Selector;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;

/**
 * Crossovers two individuals selected using {@linkplain Selector}. Returns new individual.
 * @author andi
 *
 */
public interface Crossover {
	
	/**
	 * Crossovers 2 individual using some strategy
	 * @param conf1
	 * @param conf
	 * @return
	 */
	AIFPGAConfiguration crossover(AIFPGAConfiguration conf1, AIFPGAConfiguration conf);

}
