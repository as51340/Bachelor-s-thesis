package hr.fer.zemris.bachelor.thesis.ai.crossover;

import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationRandomizer;
import hr.fer.zemris.bool.SimpleFPGA.FPGAConfiguration;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.FPGAModel.FPGAModelConfiguration;

/**
 * Completeley random solution. Randomizer chooses between two configurations and prints result out.
 * @author andi
 *
 */
public class SimpleCrossover implements Crossover{

	public AIFPGAConfigurationRandomizer random;
	
	/**
	 * Ugly that we need model
	 */
	public FPGAModel model;
	
	public SimpleCrossover(AIFPGAConfigurationRandomizer random, FPGAModel model) {
		super();
		this.random = random;
		this.model = model;
	}

	@Override
	public AIFPGAConfiguration crossover(AIFPGAConfiguration conf1, AIFPGAConfiguration conf2) {
		int[] newClbIndexes = crossRandomlyClbAndPinIndexes(conf1.clbIndexes, conf2.clbIndexes);
		int[] newPinIndexes = crossRandomlyClbAndPinIndexes(conf1.pinIndexes, conf2.pinIndexes);
		FPGAModelConfiguration newConf = crossModelConfigurations(conf1.configuration, conf2.configuration);
		AIFPGAConfiguration result = new AIFPGAConfiguration(newConf, newClbIndexes, newPinIndexes);
		return result;
	}
	
	public FPGAModelConfiguration crossModelConfigurations(FPGAModelConfiguration conf1, FPGAModelConfiguration conf2) {
		FPGAModelConfiguration newConf = model.newConfiguration();
		crossRandomlyBytes(newConf.pinIndexes, conf1.pinIndexes, conf2.pinIndexes);
		crossRandomlyBytes(newConf.clbOutIndexes, conf1.clbOutIndexes, conf2.clbOutIndexes);
		for(int i = 0; i < newConf.clbInIndexes.length; i++) {
			byte[] arr1 = conf1.clbInIndexes[i];
			byte[] arr2 = conf2.clbInIndexes[i];
			crossRandomlyBytes(newConf.clbInIndexes[i], arr1, arr2);
		}
		for(int i = 0; i < newConf.switchBoxes.length; i++) { //TODO advanced version of switch boxes crossover
			for(int j = 0; j < newConf.switchBoxes[0].length; j++) {
				byte[] arr1 = conf1.switchBoxes[i][j];
				byte[] arr2 = conf2.switchBoxes[i][j];
				crossRandomlyBytes(newConf.switchBoxes[i][j], arr1, arr2);
			}
		}
		return newConf;
	}
	
	public void crossRandomlyBytes(byte[] result, byte[] arr1, byte[] arr2) {
		for(int i = 0; i < result.length; i++) {
			int index = random.nextInt(2);
			if(index == 0) {
				result[i] = arr1[i];
			} else {
				result[i] = arr2[i];
			}
		}
	}
	
	public int[] crossRandomlyClbAndPinIndexes(int[] arr1, int[] arr2) {
		int[] newArr = new int[arr1.length];
		for(int i = 0; i < newArr.length; i++) {
			int index = random.nextInt(2); //because we cross two elements!!!
			if(index == 0) {
				newArr[i] = arr1[i];
			} else {
				newArr[i] = arr2[i];
			}
		}
		return newArr;
	}

}
