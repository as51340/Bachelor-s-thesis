package hr.fer.zemris.bachelor.thesis.ai;

import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;

/**
 * Performs mutation on one individual({@linkplain AIFPGAConfiguration}). This way we can later implement different
 * kinds of mutation operations
 * @author andi
 *
 */
public interface Mutation {
	
	/**
	 * Changes some chromosomes on individual
	 * @param conf
	 * @return
	 */
	void mutate(AIFPGAConfiguration conf);
	

}
