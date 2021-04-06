package hr.fer.zemris.bachelor.thesis.ai.crossover;

import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationRandomizer;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.FPGAModel.FPGAModelConfiguration;


/**
 * This version of crossovering creates new configuration using mathematical operator of arithmetic middle or mean.
 * @author andi
 *
 */
public class CrossoverAS implements Crossover{

	
	public AIFPGAConfigurationRandomizer random;
	
	/**
	 * Ugly that we need model
	 */
	public FPGAModel model;
	
	public CrossoverAS(AIFPGAConfigurationRandomizer random, FPGAModel model) {
		super();
		this.random = random;
		this.model = model;
	}
	
	
	@Override
	public AIFPGAConfiguration crossover(AIFPGAConfiguration conf1, AIFPGAConfiguration conf2) {
		int[] newClbIndexes = calculateMeanInt(conf1.clbIndexes, conf2.clbIndexes);
		int[] newPinIndexes = calculateMeanInt(conf1.pinIndexes,conf2.pinIndexes);
		FPGAModelConfiguration newConf = crossModelConfigurations(conf1.configuration, conf2.configuration);
		AIFPGAConfiguration result = new AIFPGAConfiguration(newConf, newClbIndexes, newPinIndexes);
		return result;
	}
	
	public FPGAModelConfiguration crossModelConfigurations(FPGAModelConfiguration conf1, FPGAModelConfiguration conf2) {
		FPGAModelConfiguration newConf = model.newConfiguration();
		calculateMeanByte(newConf.pinIndexes, conf1.pinIndexes, conf2.pinIndexes);
		calculateMeanByte(newConf.clbOutIndexes, conf1.clbOutIndexes, conf2.clbOutIndexes);
		for(int i = 0; i < newConf.clbInIndexes.length; i++) {
			byte[] arr1 = conf1.clbInIndexes[i];
			byte[] arr2 = conf2.clbInIndexes[i];
			calculateMeanByte(newConf.clbInIndexes[i], arr1, arr2);
		}
		for(int i = 0; i < newConf.switchBoxes.length; i++) { //TODO advanced version of switch boxes crossover
			for(int j = 0; j < newConf.switchBoxes[0].length; j++) {
				byte[] arr1 = conf1.switchBoxes[i][j];
				byte[] arr2 = conf2.switchBoxes[i][j];
				calculateMeanByte(newConf.switchBoxes[i][j], arr1, arr2);
			}
		}
		return newConf;
	}
	
	private int[] calculateMeanInt(int[] arr1, int[] arr2) {
		int[] sol = new int[arr1.length];
		for(int i = 0; i < arr1.length; i++) {
			sol[i] = (arr1[i] + arr2[i]) / 2;
		}
		return sol;
	}
	
	private void calculateMeanByte(byte[] sol, byte[] arr1, byte[] arr2) {
		for(int i = 0; i < arr1.length; i++) {
			sol[i] = (byte) ((arr1[i] + arr2[i]) / 2);
		}
	}

}
