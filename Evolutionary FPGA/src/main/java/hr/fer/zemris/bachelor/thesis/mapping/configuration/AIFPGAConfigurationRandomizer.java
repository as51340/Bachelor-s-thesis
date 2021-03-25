package hr.fer.zemris.bachelor.thesis.mapping.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.FPGAModel.FPGAModelConfiguration;

/**
 * Creates random configuration after individual is created. Nothing is cleared, switch is not as it should be => no requests are fulfilled
 * @author andi
 *
 */
public class AIFPGAConfigurationRandomizer {
	
	/**
	 * Reference to random
	 */
	private Random random;
	
	/**
	 * FPGA model
	 */
	private FPGAModel model;
	
	public AIFPGAConfigurationRandomizer(FPGAModel model) {
		this.model = model;
		random = new Random(System.currentTimeMillis()); //init seed
	}
	
	/**
	 * Creates random configuration
	 * @param conf
	 */
	public void randomize(AIFPGAConfiguration conf) {
		FPGAModelConfiguration modelConf = conf.configuration;
		conf.clbIndexes= randomizeArray(conf.clbIndexes); //clbs
		conf.pinIndexes = randomizeArray(conf.pinIndexes); //pins
		modelConf.clbOutIndexes = randomizeByteArray((byte) model.wiresCount, modelConf.clbOutIndexes); //clbs outputs, first argument should be wires count I think
		modelConf.pinIndexes = randomizeByteArray((byte) model.wiresCount, modelConf.clbOutIndexes);
		for(int i = 0; i < model.clbs.length; i++) {
			modelConf.clbInIndexes[i] = randomizeByteArray((byte) model.wiresCount, modelConf.clbInIndexes[i]);
		}
		for(int i = 0; i < model.switchBoxes.length; i++) {
			for(int j = 0; j < 4* model.wiresCount; j++) {
				modelConf.switchBoxes[i][j] = randomizeByteArray((byte)3, modelConf.switchBoxes[i][j]);
			}
		}
				
	}
	
	/**
	 * Not very intelligent algorithm, could be better.
	 * @param arr
	 */
	public int[] randomizeArray(int[] arr) {
		for(int i = 0; i < arr.length; i++) arr[i] = i;
		List<Integer> ints = Arrays.stream(arr).boxed().collect(Collectors.toList());
		Collections.shuffle(ints);
		for(int i = 0; i < arr.length; i++) {
			arr[i] = ints.get(i);
		}
		return arr;
	}
	
	/**
	 * Creates random byte array
	 * @param boundary
	 * @param arr
	 */
	public byte[] randomizeByteArray(byte boundary, byte[] arr) {
		for(int i = 0; i < arr.length; i++) {
			int rnd = random.nextInt(boundary);
			arr[i] = (byte) rnd;
		}
		return arr;
	}
	
	
}
