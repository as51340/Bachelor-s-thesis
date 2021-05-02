package hr.fer.zemris.bachelor.thesis.ai.crossover;

import java.util.HashSet;
import java.util.Set;

import hr.fer.zemris.bachelor.thesis.evaluator.FPGAEvaluator;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationRandomizer;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.FPGAModel.FPGAModelConfiguration;

/**
 * Valid crossover is such a crossover where after crossing we still have valid
 * byte configuration in model
 * 
 * @author andi
 *
 */
public class ValidCrossover implements Crossover {

	private AIFPGAConfigurationRandomizer random;

	private FPGAModel model;

	public ValidCrossover(AIFPGAConfigurationRandomizer random, FPGAModel model) {
		this.random = random;
		this.model = model;
	}

	@Override
	public AIFPGAConfiguration crossover(AIFPGAConfiguration conf1, AIFPGAConfiguration conf2) {
		int[] newClbIndexes = crossClbAndPin(conf1.clbIndexes, conf2.clbIndexes);
		int[] newPinIndexes = crossClbAndPin(conf1.pinIndexes, conf2.pinIndexes);
		FPGAModelConfiguration newConf = crossModelConfigurations(conf1.configuration, conf2.configuration);
		AIFPGAConfiguration result = new AIFPGAConfiguration(newConf, newClbIndexes, newPinIndexes);
		return result;
	}

	/**
	 * Crosses underlying configuration
	 * 
	 * @param conf1
	 * @param conf2
	 * @return
	 */
	private FPGAModelConfiguration crossModelConfigurations(FPGAModelConfiguration conf1,
			FPGAModelConfiguration conf2) {
		FPGAModelConfiguration newConf = model.newConfiguration();
		crossRandomlyBytes(newConf.pinIndexes, conf1.pinIndexes, conf2.pinIndexes);
		crossRandomlyBytes(newConf.clbOutIndexes, conf1.clbOutIndexes, conf2.clbOutIndexes);
//		for(int i = 0; i < newConf.clbInIndexes.length; i++) {
//			byte[] arr1 = conf1.clbInIndexes[i];
//			byte[] arr2 = conf2.clbInIndexes[i];
//			crossRandomlyBytes(newConf.clbInIndexes[i], arr1, arr2);
//		}
		for (int i = 0; i < conf1.clbInIndexes.length; i++) {
			crossClbInputs(newConf.clbInIndexes[i], conf1.clbInIndexes[i], conf2.clbInIndexes[i]);
		}

		crossSwitchBoxes(newConf.switchBoxes, conf1.switchBoxes, conf2.switchBoxes);
		return newConf;
	}

	private void crossSwitchBoxes(byte[][][] swBoxes, byte[][][] conf1, byte[][][] conf2) {
		for (int i = 0; i < swBoxes.length; i++) {
			for (int j = 0; j < swBoxes[i].length; j++) {
				for (int k = 0; k < swBoxes[i][j].length; k++) {
					double p = random.nextDouble();
					
					if (conf1[i][j][k] == 0) {
						if (p <= 0.5) { // choose with 70% the other one
							swBoxes[i][j][k] = conf2[i][j][k];
							swBoxes[i][k][j] = conf2[i][k][j];
						}
					} else if (conf2[i][j][k] == 0) {
						if (p <= 0.5) { // choose with 70% the other one
							swBoxes[i][j][k] = conf1[i][j][k];
							swBoxes[i][k][j] = conf1[i][k][j];
						}
					} else {
						if (p <= 0.5) {
							swBoxes[i][j][k] = conf1[i][j][k];
							swBoxes[i][k][j] = conf1[i][k][j];
						} else {
							swBoxes[i][j][k] = conf2[i][j][k];
							swBoxes[i][k][j] = conf2[i][k][j];
						}
					}
				}
			}
		}
	}

	private void crossClbInputs(byte[] newConf, byte[] arr1, byte[] arr2) {
		Set<Byte> used = new HashSet<>();
		for (int i = 0; i < arr1.length; i++) {
			int rnd = random.nextInt(2); // select which one you want
			if (rnd == 0) {
				if (!used.contains(arr1[i])) {
					newConf[i] = arr1[i];
				} else {
					if (!used.contains(arr2[i])) {
						newConf[i] = arr2[i];
					} else {
						throw new IllegalStateException("States should be valid!");
					}
				}
			} else if (rnd == 1) {
				if (!used.contains(arr2[i])) {
					newConf[i] = arr2[i];
				} else {
					if (!used.contains(arr1[i])) {
						newConf[i] = arr1[i];
					} else {
						throw new IllegalStateException("States should be valid!");
					}
				}
			}
			used.add(newConf[i]);
		}
	}

	private void crossRandomlyBytes(byte[] result, byte[] arr1, byte[] arr2) {
		for (int i = 0; i < result.length; i++) {
			int index = random.nextInt(2);
			if (index == 0) {
				result[i] = arr1[i];
			} else {
				result[i] = arr2[i];
			}
		}
	}

	private int[] crossClbAndPin(int[] arr1, int[] arr2) {
		Set<Integer> unused = new HashSet<>(); // preprocess
		for (int i = 0; i < arr1.length; i++) {
			unused.add(arr1[i]);
		}
		int[] solution = new int[arr1.length];

		for (int i = 0; i < arr1.length; i++) {
			int rnd = random.nextInt(2); // we need 0 or 1 for choose array
			if (rnd == 0) {
				if (unused.contains(arr1[i])) {
					solution[i] = arr1[i];
				} else if (unused.contains(arr2[i])) {
					solution[i] = arr2[i];
				} else {
					solution[i] = (Integer) unused.toArray()[0];
				}
			} else if (rnd == 1) {
				if (unused.contains(arr2[i])) {
					solution[i] = arr2[i];
				} else if (unused.contains(arr1[i])) {
					solution[i] = arr1[i];
				} else {
					solution[i] = (Integer) unused.toArray()[0];
				}
			}
			unused.remove(solution[i]);
		}

		if (unused.size() > 0) {
			throw new IllegalStateException("Invalid configuration in valid crossover!");
		}
		unused.clear();
		for (int i = 0; i < solution.length; i++) {
			if (!unused.add(solution[i]))
				throw new IllegalStateException("Duplicates in crossovering!");
		}

		return solution;

	}

}
