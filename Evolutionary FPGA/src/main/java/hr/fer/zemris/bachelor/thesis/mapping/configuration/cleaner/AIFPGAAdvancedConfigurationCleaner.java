package hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner;

import java.util.HashSet;
import java.util.Set;

import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationRandomizer;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.swboxes.SwitchBoxCleaner;
import hr.fer.zemris.fpga.FPGAModel;

/**
 * Cleans duplicates from all configuration arrray => clb inputs, pin indexes, clb indexes
 * @author andi
 *
 */
public class AIFPGAAdvancedConfigurationCleaner extends AIFPGAConfigurationCleaner{

	protected AIFPGAConfigurationRandomizer random;
	
	
	public AIFPGAAdvancedConfigurationCleaner(FPGAModel model, SwitchBoxCleaner swCleaner, AIFPGAConfigurationRandomizer random) {
		super(model, swCleaner);
		this.random = random;
	}

	@Override
	public void clean(AIFPGAConfiguration conf) {
		swCleaner.clean(conf.getConfiguration().switchBoxes);
		cleanIntArray(conf.getPinIndexes());
		cleanIntArray(conf.getClbIndexes());
		for(int i = 0; i < conf.getConfiguration().clbInIndexes.length; i++) {
			cleanByteArray(conf.getConfiguration().clbInIndexes[i]); //inputs must not be duplicated
		}
	}
	
	
	protected void cleanIntArray(int[] array) {
		Set<Integer> visited = new HashSet();
		boolean valid = true;
		for(int i = 0; i < array.length; i++) {
			if(!visited.add(i)) { //if adding fails
				valid = false;
				break;
			}
		}
		if(!valid) {
			array = random.randomizeArray(array); //no need for array returning I think
		}
	}
	
	protected void cleanByteArray(byte[] array) {
		Set<Byte> visited = new HashSet();
		boolean valid = true;
		for(byte i = 0; i < array.length; i++) {
			if(!visited.add(i)) { //if adding fails
				valid = false;
				break;
			}
		}
		if(!valid) {
			array = random.randomizeByteArray((byte)model.wiresCount, array);
		}
	}

}
