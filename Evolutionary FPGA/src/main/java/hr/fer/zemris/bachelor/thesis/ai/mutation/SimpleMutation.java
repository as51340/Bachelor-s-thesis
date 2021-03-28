package hr.fer.zemris.bachelor.thesis.ai.mutation;

import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationRandomizer;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.FPGAModel.FPGAModelConfiguration;

/**
 * Changes chromosomes by observing mutation rate.
 * @author andi
 *
 */
public class SimpleMutation implements Mutation{

	public double mutationRate;

	public AIFPGAConfigurationRandomizer random;
	
	public FPGAModel model;
	
	public SimpleMutation(double mutationRate, AIFPGAConfigurationRandomizer random, FPGAModel model) {
		this.mutationRate = mutationRate;
		this.random = random;
		this.model = model;
	}
	
	@Override
	public void mutate(AIFPGAConfiguration conf) {
		int[] clbIndexes = conf.clbIndexes;
		int[] pinIndexes = conf.pinIndexes;
		FPGAModelConfiguration modelConf = conf.configuration;
		mutateArray(clbIndexes);
		mutateArray(pinIndexes);
		mutateByteArray((byte)model.wiresCount, modelConf.pinIndexes);
		mutateByteArray((byte) model.wiresCount, modelConf.clbOutIndexes);
		for(int i = 0; i < modelConf.clbInIndexes.length; i++) {
			byte[] arr = modelConf.clbInIndexes[i];
			mutateByteArray((byte)model.wiresCount, arr);
		}
		for(int i = 0; i < modelConf.switchBoxes.length; i++) { 
			for(int j = 0; j < modelConf.switchBoxes[0].length; j++) {
				byte[] arr = modelConf.switchBoxes[i][j];
				mutateByteArray((byte)3, arr); //because we can have connections 
			}
		}
		
		
	}
	
	public void mutateByteArray(byte bound, byte[] arr) {
		for(int i = 0; i < arr.length; i++) {
			double p = random.nextDouble(); //between 0 and 1
			if(p <= mutationRate) {
				arr[i] = (byte) random.nextInt(bound);
			}
		}
	}
	
	public void mutateArray(int[] arr) {
		for(int i = 0; i < arr.length; i++) {
			double p = random.nextDouble(); //between 0 and 1
			if(p <= mutationRate) {
				arr[i] = random.nextInt(arr.length);
			}
		}
	}

}
