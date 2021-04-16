package hr.fer.zemris.bachelor.thesis.ai.mutation;

import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationRandomizer;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.FPGAModel.FPGAModelConfiguration;

/**
 * Clbs and pins simply swap two values in their array. Connection indexes are changes wihtin each pin, clb with mutation rate od 5%.
 * @author andi
 *
 */
public class MutationSwap implements Mutation{

	public double mutationRate;

	public AIFPGAConfigurationRandomizer random;
	
	public FPGAModel model;
	
	
	public MutationSwap(double mutationRate, AIFPGAConfigurationRandomizer random, FPGAModel model) {
		this.mutationRate = mutationRate;
		this.random = random;
		this.model = model;
	}
	
	
	@Override
	public void mutate(AIFPGAConfiguration conf) {
		int[] clbIndexes = conf.clbIndexes;
		int[] pinIndexes = conf.pinIndexes;
		swapMutate(clbIndexes);
		swapMutate(pinIndexes);
		
		FPGAModelConfiguration modelConf = conf.configuration;
		
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
	
	/**
	 * Simply swaps two elements
	 * @param arr
	 */
	private void swapMutate(int[] arr) {
		int index1 = random.nextInt(arr.length);
		int index2;
		while((index2 = random.nextInt(arr.length)) == index1); //don't allow same indices
		
		int temp = arr[index1];
		arr[index1] = arr[index2];
		arr[index2] = temp;
	}
	
	/**
	 * Mutation within mutation rate
	 * @param rnd
	 * @param arr
	 */
	private void mutateByteArray(byte rnd, byte[] arr) {
		for(int i = 0; i < arr.length; i++) {
			double rate = random.nextDouble();
			if(rate <= mutationRate) {
				arr[i] = (byte)random.nextInt(rnd);
			}
		}
	}
	
	
	

}
