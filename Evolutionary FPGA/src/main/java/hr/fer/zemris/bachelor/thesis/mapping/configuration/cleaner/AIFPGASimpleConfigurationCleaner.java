package hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner;

import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationRandomizer;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.swboxes.SwitchBoxCleaner;
import hr.fer.zemris.fpga.FPGAModel;

public class AIFPGASimpleConfigurationCleaner extends AIFPGAConfigurationCleaner {


	public AIFPGASimpleConfigurationCleaner(FPGAModel model, SwitchBoxCleaner swCleaner) {
		super(model, swCleaner);
	}

	@Override
	public void clean(AIFPGAConfiguration conf) {
		swCleaner.clean(conf.getConfiguration().switchBoxes);
	}

}
