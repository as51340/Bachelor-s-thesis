package hr.fer.zemris.bachelor.thesis.ai.mutation;

import hr.fer.zemris.bachelor.thesis.evaluator.Coefficients;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationRandomizer;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.FPGAModel.FPGAModelConfiguration;
import hr.fer.zemris.fpga.FPGAModel.SwitchBox;
import hr.fer.zemris.fpga.FPGAModel.WireSegment;

/**
 * Clbs and pins simply swap two values in their array. Connection indexes are
 * changes wihtin each pin, clb with mutation rate od 5%.
 * 
 * @author andi
 *
 */
public class MutationSwap implements Mutation {

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

		swapMutate(pinIndexes);
		swapMutate(clbIndexes);

		FPGAModelConfiguration modelConf = conf.configuration;

		mutateByteArray((byte) model.wiresCount, modelConf.pinIndexes);
		mutateByteArray((byte) model.wiresCount, modelConf.clbOutIndexes);

		for (int i = 0; i < modelConf.clbInIndexes.length; i++) {
			byte[] arr = modelConf.clbInIndexes[i];
//			mutateByteArray((byte)model.wiresCount, arr);
			mutateClbInputs(arr);
		}
//		for(int i = 0; i < modelConf.switchBoxes.length; i++) { 
//			for(int j = 0; j < modelConf.switchBoxes[0].length; j++) {
//				byte[] arr = modelConf.switchBoxes[i][j];
//				mutateByteArray((byte)3, arr); //because we can have connections 
//			}
//		}

//		for (int i = 0; i < modelConf.switchBoxes.length; i++) {
//			mutateSwitchBox(modelConf.switchBoxes[i]);
//		}

		mutateSwitchBoxes(modelConf.switchBoxes);
	}

	/**
	 * Circular shift of all positions
	 * 
	 * @param arr
	 */
	private void mutateClbInputs(byte[] arr) {
		byte tmp = arr[0];
		for (int i = 0; i < arr.length - 1; i++) {
			arr[i] = arr[i + 1];
		}
		arr[arr.length - 1] = tmp;
	}

	private boolean validIndex(int row, int col, int j) {
		if (row == 0 && j < model.wiresCount)
			return false;
		if (row == model.rows && (j >= 2 * model.wiresCount && j < 3 * model.wiresCount))
			return false;
		if (col == 0 && (j >= 3 * model.wiresCount && j < 4 * model.wiresCount))
			return false;
		if (col == model.columns && (j >= model.wiresCount && j < 2 * model.wiresCount))
			return false;
		return true;

	}

	private void mutateSwitchBoxes(byte[][][] swBoxes) {
		int maxConnections = Coefficients.getMaxConnections(model.clbs[0].inConnectionIndexes.length);
		int connChange = random.nextInt(maxConnections);

//		for (int i = 0; i < swBoxes.length; i++) {
//			for (int j = 0; j < swBoxes[0].length; j++) {
//				for (int k = 0; k < swBoxes[0][0].length; k++) {
//					System.out.print(swBoxes[i][j][k] + " ");
//				}
//				System.out.println();
//			}
//			System.out.println();
//		}

		for (int i = 0; i < swBoxes.length; i++) {

			int row = i / (model.columns + 1);
			int col = i % (model.columns + 1);
			int connections = 0;
			
//			SwitchBox currModelBox = model.switchBoxes[i];
			
			

			for (int j = 0; j < swBoxes[0].length && connections < connChange; j++) { //while you can change more connections
				for (int k = 0; k < swBoxes[0][0].length && k < j && connections < connChange; k++) { // dont iterate over upper half

					if ((swBoxes[i][j][k] == 1 && swBoxes[i][k][j] == 2)
							|| (swBoxes[i][j][k] == 2 && swBoxes[i][k][j] == 1)) { // if actualy is there conn
						
						boolean valid = validIndex(row, col, j) && validIndex(row, col, k); // just to be sure if wire is okay
						if (valid) { // if everything is valid

							
//							WireSegment firstSegment = currModelBox.segments[j], secondSegment = currModelBox.segments[k];
//							if(firstSegment.label == null && secondSegment.label == null) {
//								System.err.println("Both segments are null: Switch box=" + i + "j=" + j + "k=" + k);
//							}
							
							
							double y = random.nextDouble(); // for deciding if we want to create completely new
															// connection or
							// change only one edge
							if (y <= 0.75) {

								int parentGroup1 = -1; // first index can be in any group
								int index1 = nextIndex(parentGroup1, row, col); // get first index
								parentGroup1 = index1 / model.wiresCount; //get first parent group
								int index2 = nextIndex(parentGroup1, row, col); //get second index

								swBoxes[i][j][k] = 0; //remove old connection
								swBoxes[i][k][j] = 0; //remove old connection

								swBoxes[i][index1][index2] = 1; //create one edge
								swBoxes[i][index2][index1] = 2; //create second edge

							} else {
								int group_j = j / model.wiresCount; //get j group of wires
								int group_k = k / model.wiresCount; //get k group of wires
								int newSecondIndex; //get second connection edge

								double x = random.nextDouble(); //for deciding which index we want to change
								int parentGroup = group_j; // get parent group of j
								if (x <= 0.5) { // add more diversity, sometimes change k sometimes j
									parentGroup = group_k; //get parent group of k
								}

								newSecondIndex = nextIndex(parentGroup, row, col); //next index

								if (x <= 0.5) { // then change it as k
									if (swBoxes[i][j][k] == 1) { // change wire from j k to j newSecondIndex
										swBoxes[i][newSecondIndex][k] = 1; //attach as it was
										swBoxes[i][k][newSecondIndex] = 2; //attach as it was
									} else if (swBoxes[i][j][k] == 2) {
										swBoxes[i][newSecondIndex][k] = 2; //attach as it was
										swBoxes[i][k][newSecondIndex] = 1; //attach as it was
									}
								} else { // j rocks
									if (swBoxes[i][j][k] == 1) { // change wire from j k to j newSecondIndex
										swBoxes[i][j][newSecondIndex] = 1; //attach as it was 
										swBoxes[i][newSecondIndex][j] = 2; //attach as it was
									} else if (swBoxes[i][j][k] == 2) {
										swBoxes[i][j][newSecondIndex] = 2; //attach as it was
										swBoxes[i][newSecondIndex][j] = 1; //attach as it was
									}
								}

								// delete always last connection

								if (x <= 0.35) { // with 25% chance delete last wire, maybe we will need it later
									swBoxes[i][j][k] = 0;
									swBoxes[i][k][j] = 0;
								}
							}
							connections++;
						} else {
							throw new IllegalStateException("Invalid wire somewhere in randomizer?");
						}
					}
				}
			}
		}
	}

	/**
	 * Get next for creating new connection or connection edge
	 * 
	 * @param parentGroup
	 * @return
	 */
	private int nextIndex(int parentGroup, int row, int col) {
		int newSecondIndex; //solution
		while (true) { //as long as its false solution
			newSecondIndex = random.nextInt(model.wiresCount * 4); // connect it to second index
			int newGroup = newSecondIndex / model.wiresCount; // get other group
			if (newGroup == parentGroup) // if same group continue
				continue;
			if (validIndex(row, col, newSecondIndex)) // check for index validicity
				break;
		}
		return newSecondIndex;
	}

	/**
	 * Simply swaps two elements
	 * 
	 * @param arr
	 */
	private void swapMutate(int[] arr) {
		if (arr.length == 1)
			return;
		int index1 = random.nextInt(arr.length);
		int index2;
		while ((index2 = random.nextInt(arr.length)) == index1)
			; // don't allow same indices

		int temp = arr[index1];
		arr[index1] = arr[index2];
		arr[index2] = temp;
	}

	/**
	 * Mutation within mutation rate
	 * 
	 * @param rnd
	 * @param arr
	 */
	private void mutateByteArray(byte rnd, byte[] arr) {
		for (int i = 0; i < arr.length; i++) {
			double rate = random.nextDouble();
			if (rate <= mutationRate) {
				arr[i] = (byte) random.nextInt(rnd);
			}
		}
	}

}
