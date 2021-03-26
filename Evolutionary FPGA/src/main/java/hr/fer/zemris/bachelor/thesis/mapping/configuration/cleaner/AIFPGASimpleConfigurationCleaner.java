package hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner;

import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationRandomizer;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.swboxes.SwitchBoxCleaner;
import hr.fer.zemris.fpga.FPGAModel;

/**
 * Cleans only switch box using given algorithm.
 * @author andi
 *
 */
public class AIFPGASimpleConfigurationCleaner extends AIFPGAConfigurationCleaner {


	public AIFPGASimpleConfigurationCleaner(FPGAModel model, SwitchBoxCleaner swCleaner) {
		super(model, swCleaner);
	}

	@Override
	public void clean(AIFPGAConfiguration conf) {
		for(int i = 0; i < conf.configuration.switchBoxes.length; i++) {
			swCleaner.clean(conf.configuration.switchBoxes[i]);
		}
		
	}

}
