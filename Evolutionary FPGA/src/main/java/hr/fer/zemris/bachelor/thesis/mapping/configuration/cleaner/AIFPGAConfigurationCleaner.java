package hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner;

import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationRandomizer;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.swboxes.SwitchBoxCleaner;
import hr.fer.zemris.fpga.FPGAModel;

public abstract class AIFPGAConfigurationCleaner {
	
	protected FPGAModel model;
	
	protected SwitchBoxCleaner swCleaner;
	
	
	public AIFPGAConfigurationCleaner(FPGAModel model, SwitchBoxCleaner swCleaner) {
		super();
		this.model = model;
		this.swCleaner = swCleaner;
	}


	public abstract void clean(AIFPGAConfiguration conf);
	

}
