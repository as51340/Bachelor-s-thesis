package hr.fer.zemris.bachelor.thesis.ai.crossover;

import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationRandomizer;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.FPGAModel.FPGAModelConfiguration;

/**
 * First selects m genes from one configuration and then other n -m from second
 * @author andi
 *
 */
public class ExponentialCrossover implements Crossover{

	private AIFPGAConfigurationRandomizer random;
	
	private FPGAModel model;
	
	public ExponentialCrossover(AIFPGAConfigurationRandomizer random, FPGAModel model) {
		this.random = random;
		this.model = model;
	}
	
	
	@Override
	public AIFPGAConfiguration crossover(AIFPGAConfiguration conf1, AIFPGAConfiguration conf2) {
		int[] newClbIndexes = exponentialCross(conf1.clbIndexes, conf2.clbIndexes);
		int[] newPinIndexes = exponentialCross(conf1.pinIndexes, conf2.pinIndexes);
		FPGAModelConfiguration newConf = crossModelConfigurations(conf1.configuration, conf2.configuration);
		AIFPGAConfiguration result = new AIFPGAConfiguration(newConf, newClbIndexes, newPinIndexes);
		return result;
	}
	
	
	private int[] exponentialCross(int[] arr1, int[] arr2) {
		int i = 0;
		int[] solution = new int[arr1.length];
		while(random.nextInt(2) == 0) {
			solution[i] = arr1[i];
			i++;
		}

		for(; i < arr2.length; i++) {
			solution[i] = arr2[i];
		}
		return solution;	
	}
	
	private FPGAModelConfiguration crossModelConfigurations(FPGAModelConfiguration conf1, FPGAModelConfiguration conf2) {
		FPGAModelConfiguration newConf = model.newConfiguration();
		exponentialOnBytes(newConf.pinIndexes, conf1.pinIndexes, conf2.pinIndexes);
		exponentialOnBytes(newConf.clbOutIndexes, conf1.clbOutIndexes, conf2.clbOutIndexes);
		for(int i = 0; i < newConf.clbInIndexes.length; i++) {
			byte[] arr1 = conf1.clbInIndexes[i];
			byte[] arr2 = conf2.clbInIndexes[i];
			exponentialOnBytes(newConf.clbInIndexes[i], arr1, arr2);
		}
		for(int i = 0; i < newConf.switchBoxes.length; i++) { //TODO advanced version of switch boxes crossover
			byte[][] sw1 = conf1.switchBoxes[i];
			byte[][] sw2 = conf2.switchBoxes[i];
			
		}
		return newConf;
	}
	
	private void exponentialOnBytes(byte[] result, byte[] arr1, byte[] arr2) {
		int i = 0;
		
		while(random.nextInt(2) == 0) {
			result[i] = arr1[i];
			i++;
		}
		for(; i < arr2.length; i++) {
			result[i] = arr2[i];
		}
	}
	
}
