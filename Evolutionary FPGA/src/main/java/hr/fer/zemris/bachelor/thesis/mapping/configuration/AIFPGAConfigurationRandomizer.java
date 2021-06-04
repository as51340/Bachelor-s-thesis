package hr.fer.zemris.bachelor.thesis.mapping.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import hr.fer.zemris.bachelor.thesis.evaluator.Coefficients;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.FPGAModel.FPGAModelConfiguration;

/**
 * Creates random configuration after individual is created. Nothing is cleared,
 * switch is not as it should be => no requests are fulfilled
 * 
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
		random = new Random(System.currentTimeMillis()); // init seed
	}

	/**
	 * Creates random configuration
	 * 
	 * @param conf
	 */
	public void randomize(AIFPGAConfiguration conf) {
		FPGAModelConfiguration modelConf = conf.configuration;
		conf.clbIndexes = randomizeArray(conf.clbIndexes); // clbs
		conf.pinIndexes = randomizeArray(conf.pinIndexes); // pins
		modelConf.clbOutIndexes = randomizeByteArray((byte) model.wiresCount, modelConf.clbOutIndexes); 
		
		modelConf.pinIndexes = randomizeByteArray((byte) model.wiresCount, modelConf.pinIndexes);
		for (int i = 0; i < model.clbs.length; i++) {
			modelConf.clbInIndexes[i] = randomizeDistinctByteArray((byte) model.wiresCount, modelConf.clbInIndexes[i]);
		}
		
		randomSwitchBoxConfiguration(modelConf.switchBoxes);
		
//		for (int i = 0; i < model.switchBoxes.length; i++) {
//			for (int j = 0; j < 4 * model.wiresCount; j++) {
//				modelConf.switchBoxes[i][j] = randomizeByteArray((byte) 3, modelConf.switchBoxes[i][j]);
//			}
//		}
	}

	private void randomSwitchBoxConfiguration(byte[][][] swBoxes) {
		int maxConnections = Coefficients.getMaxConnections(model.clbs[0].inConnectionIndexes.length);

		int conns = nextIntBetween(1, maxConnections+1); // 1 to 5 for example

		for (int i = 0; i < swBoxes.length; i++) {

			int row = i / (model.columns + 1);
			int col = i % (model.columns + 1);
			int connections = 0;

			while (connections < conns) {
				int j, k, group_j, group_k;
				
				j = random.nextInt(model.wiresCount * 4); // get first index
				
				if(row == 0 && j < model.wiresCount) continue;
				if(row == model.rows && (j >= 2 * model.wiresCount && j < 3 * model.wiresCount)) continue;
				if(col == 0 && (j >= 3 * model.wiresCount && j < 4 * model.wiresCount)) continue;
				if(col == model.columns && (j >= model.wiresCount && j < 2 * model.wiresCount)) continue;
				
				group_j = j / model.wiresCount; //get first group
				
				while (true) {
					k = random.nextInt(model.wiresCount * 4); // get second index
					group_k = k / model.wiresCount;
					if (group_k == group_j) continue;
					//dont even think about creating stupid connections
					if(row == 0 && k < model.wiresCount) continue;
					if(row == model.rows && (k >= 2 * model.wiresCount && k < 3 * model.wiresCount)) continue;
					if(col == 0 && (k >= 3 * model.wiresCount && k < 4 * model.wiresCount)) continue;
					if(col == model.columns && (k >= model.wiresCount && k < 2 * model.wiresCount)) continue;
					break;
				}
				
				int rnd = random.nextInt(3); // 0 1 or 2 for switch box
				if (rnd == 1) {
					swBoxes[i][j][k] = 1;
					swBoxes[i][k][j] = 2;
				} else { //rnd == 2
					swBoxes[i][j][k] = 2;
					swBoxes[i][k][j] = 1;
				}
				connections++;
			}

		}

//		for (int i = 0; i < swBoxes.length; i++) {
//			for (int j = 0; j < swBoxes[0].length; j++) {
//				for (int k = 0; k < swBoxes[0][0].length; k++) {
//					System.out.print(swBoxes[i][j][k] + " ");
//				}
//				System.out.println();
//			}
//			System.out.println();
//		}

	}

	/**
	 * Not very intelligent algorithm, could be better.
	 * 
	 * @param arr
	 */
	public int[] randomizeArray(int[] arr) {
		for (int i = 0; i < arr.length; i++)
			arr[i] = i;
		List<Integer> ints = Arrays.stream(arr).boxed().collect(Collectors.toList());
		Collections.shuffle(ints);
		for (int i = 0; i < arr.length; i++) {
			arr[i] = ints.get(i);
		}
		// System.out.println();
		return arr;
	}

	/**
	 * Creates random byte array
	 * 
	 * @param boundary
	 * @param arr
	 */
	public byte[] randomizeByteArray(byte boundary, byte[] arr) {
		for (int i = 0; i < arr.length; i++) {
			int rnd = random.nextInt(boundary);
			arr[i] = (byte) rnd;
		}
		return arr;
	}

	/**
	 * Creates random byte array
	 * 
	 * @param boundary
	 * @param arr
	 */
	public byte[] randomizeDistinctByteArray(byte boundary, byte[] arr) {
		Set<Byte> visited = new HashSet<>();
		for (int i = 0; i < arr.length; i++) {
			while (true) {
				int rnd = random.nextInt(boundary);
				if (visited.add((byte) rnd)) {
					arr[i] = (byte) rnd;
					break;
				}
			}
		}
		return arr;
	}

	public int nextIntBetween(int bnd1, int bnd2) {
		return random.nextInt(bnd2) + bnd1;
	}

	/**
	 * Wrapper
	 * 
	 * @return
	 */
	public int nextInt(int bound) {
		return random.nextInt(bound);
	}

	/**
	 * Wrapper
	 * 
	 * @return
	 */
	public double nextDouble() {
		return random.nextDouble();
	}

}
