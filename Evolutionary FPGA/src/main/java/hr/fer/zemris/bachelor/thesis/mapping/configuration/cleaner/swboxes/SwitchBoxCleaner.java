package hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.swboxes;

import hr.fer.zemris.fpga.FPGAModel;

public abstract class SwitchBoxCleaner {

	protected FPGAModel model;
	
	public SwitchBoxCleaner(FPGAModel model) {
		this.model = model;
	}
	
	public abstract void clean(byte[][] swConf);
}
