package Test;

import java.awt.BorderLayout;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import hr.fer.zemris.bachelor.thesis.ai.FPGAGeneticAlgorithm;
import hr.fer.zemris.bachelor.thesis.ai.gui.EvolutionaryFPGAGUIMaker;
import hr.fer.zemris.bachelor.thesis.evaluator.FPGAEvaluator;
import hr.fer.zemris.bachelor.thesis.evaluator.SimpleAliasesEvaluator;
import hr.fer.zemris.bachelor.thesis.evaluator.SimpleCLBInputsEvaluator;
import hr.fer.zemris.bachelor.thesis.mapping.AIFPGAMapper;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.bachelor.thesis.util.AILibrary;
import hr.fer.zemris.bool.SimpleFPGA;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.FPGAModel.SwitchBox;
import hr.fer.zemris.fpga.FPGAModel.WireSegment;
import hr.fer.zemris.fpga.LogWriter;
import hr.fer.zemris.fpga.StandardLogWriter;
import hr.fer.zemris.fpga.gui.FPGATabX;
import hr.fer.zemris.fpga.mapping.FPGAMapTask;

/**
 * Hope for the best
 * 
 * @author andi
 *
 */
public class NewTestSimpleGenetic {

	public static void main(String[] args) throws IOException {

		String fileName = "./src/main/resources/2vs1-three-vars.txt"; // wont load with resource as stream
		int rows = 2, columns = 1, pins = 1, variables = 2, wires = 3, nums = 1;
		int popSize = 50, iterations = 30000, librarySize = 70;
		boolean fitnessImg = true, intensityImg = true;
		String algName = "SV66";
		
		if (args.length == 0) {
			System.out.println("Running program with default settings!");
		}
		
		for (int i = 0; i < args.length - 1; i++) {
			if (args[i].equals("--rows")) {
				rows = Integer.parseInt(args[i + 1]);
			} else if (args[i].equals("--cols")) {
				columns = Integer.parseInt(args[i + 1]);
			} else if (args[i].equals("--pins")) {
				pins = Integer.parseInt(args[i + 1]);
			} else if (args[i].equals("--variables")) {
				variables = Integer.parseInt(args[i + 1]);
			} else if (args[i].equals("--wires")) {
				wires = Integer.parseInt(args[i + 1]);
			} else if (args[i].equals("--file")) {
				fileName = args[i + 1];
			} else if (args[i].equals("--nums")) {
				nums = Integer.parseInt(args[i + 1]);
			} else if (args[i].equals("--iterations")) {
				iterations = Integer.parseInt(args[i + 1]);
			} else if (args[i].equals("--population")) {
				popSize = Integer.parseInt(args[i + 1]);
			} else if (args[i].equals("--fitness")) {
				fitnessImg = true;
			} else if (args[i].equals("--intensity")) {
				intensityImg = true;
			} else if(args[i].equals("--alg")) {
				algName = args[i+1];
			}
		}

		FPGAModel model = new FPGAModel(rows, columns, variables, wires, pins);
		String text = Files.readString(Paths.get(fileName));
		SimpleFPGA sfpga = SimpleFPGA.parseFromText(text);

		LogWriter logger = new StandardLogWriter();
		AIFPGAMapper mapper = new AIFPGAMapper(logger);
		FPGAMapTask mapTask = FPGAMapTask.fromSimpleFPGA(sfpga);
		
		if (mapTask.clbs.length > rows * columns) {
			System.out.println("I don't have enough CLB chips, exiting...");
			return;
		}
		
		AILibrary library = new AILibrary(popSize, iterations, librarySize);
		library.constructLibrary(model, mapTask, sfpga, logger);

		FileWriter fw = new FileWriter("exception.txt", true);
		PrintWriter pw = new PrintWriter(fw);

		boolean learningMode = false;

		if (rows * columns == 4 && mapTask.clbs.length == 4) {
			learningMode = true;
		}
		
		int founded = 0, architectureCounter = 0;
		long generations = 0;
		FPGAGeneticAlgorithm alg = library.getByName(algName);
		long t1 = System.currentTimeMillis(), t2;

		try {
			for (int j = 0; j < nums; j++) {

				FPGAModel resultModel = mapper.map(alg);

//					AIFPGAConfiguration bestConf = alg.bestConf;
//
//					boolean[] pinsInput = bestConf.configuration.pinInput;
//					byte[] pins_1 = bestConf.configuration.pinIndexes;
//					for(int k = 0; k < pinsInput.length; k++) {
//						System.out.println("Input: " + pinsInput[k]);
//					}
//					
//
//					FPGAEvaluator.DEBUG = true;
//					FPGAEvaluator.EvaluatorArranger.prepareModelForEvaluation(resultModel, bestConf, mapTask, sfpga);
////
//					
//
//					if (resultModel == null) {
//						logger.log("Result model is null and cannot be seen!\n");
//						continue;
//					}
//
//					resultModel.clearWires();
//					resultModel.fillLabels();
//					
//					
//					
//					for (int k = 0; k < mapTask.clbs.length; k++) {
//						System.out.println(mapTask.clbs[k].inputs[0]);
//						System.out.println(mapTask.clbs[k].inputs[1]);
//						System.out.println();
//					}
//
//					for (int k = 0; k < resultModel.pins.length; k++) {
//						System.out.println("Pin(" + k + ").connectionIndex=" + resultModel.pins[k].connectionIndex);
//						System.out.println("Pin(" + k + ").input=" + resultModel.pins[k].input);
//						System.out.println("Pin(" + k + ").title=" + resultModel.pins[k].title);
//						if (resultModel.pins[k].connectionIndex != -1) {
//							if(resultModel.pins[k].wires[resultModel.pins[k].connectionIndex].label instanceof CLBBox) {
//								CLBBox box = (CLBBox) resultModel.pins[k].wires[resultModel.pins[k].connectionIndex].label;
//								System.out.println("Pin(" + k + ").label="
//										+ box.title);
//							} else {
//								System.out.println("Pin(" + k + ").label="
//										+ resultModel.pins[k].wires[resultModel.pins[k].connectionIndex].label);
//							}
//						}
//					}
//					System.out.println();
//					System.out.println();
//					System.out.println();
//					for (int k = 0; k < resultModel.clbs.length; k++) {
//						System.out.println(resultModel.clbs[k].title + ").index1: " + resultModel.clbs[k].inConnectionIndexes[0]);
//						System.out.println(resultModel.clbs[k].title + ").index2_ " + resultModel.clbs[k].inConnectionIndexes[1]);
//						System.out.println(resultModel.clbs[k].title + ").out_index" + resultModel.clbs[k].outConnectionIndex);
//						
//						if(resultModel.clbs[k].wiresIn[resultModel.clbs[k].inConnectionIndexes[0]].label instanceof CLBBox) {
//							CLBBox box = (CLBBox) resultModel.clbs[k].wiresIn[resultModel.clbs[k].inConnectionIndexes[0]].label;
//							System.out.println(resultModel.clbs[k].title + ").label_1: " + box.title);
//						} else {
//							System.out.println(resultModel.clbs[k].title + ").label_1: "
//									+ resultModel.clbs[k].wiresIn[resultModel.clbs[k].inConnectionIndexes[0]].label);
//						}
//						
//						if(resultModel.clbs[k].wiresIn[resultModel.clbs[k].inConnectionIndexes[1]].label instanceof CLBBox) {
//							CLBBox box = (CLBBox) resultModel.clbs[k].wiresIn[resultModel.clbs[k].inConnectionIndexes[1]].label;
//							System.out.println(resultModel.clbs[k].title + ").label_2: " + box.title);
//						} else {
//							System.out.println(resultModel.clbs[k].title + ").label_2: "
//									+ resultModel.clbs[k].wiresIn[resultModel.clbs[k].inConnectionIndexes[1]].label);
//						}
//						
//						System.out.println();
//					}

				// Cleaning process
				for (int k = 0; k < resultModel.switchBoxes.length; k++) {
					SwitchBox currModelBox = resultModel.switchBoxes[k];
					byte[][] swConfig = currModelBox.configuration;
					for (int l = 0; l < swConfig.length; l++) {
						for (int m = 0; m < swConfig[0].length && m < l; m++) {
							if ((swConfig[l][m] == 1 && swConfig[m][l] == 2)
									|| (swConfig[l][m] == 2 && swConfig[m][l] == 1)) {
								WireSegment firstSegment = currModelBox.segments[l],
										secondSegment = currModelBox.segments[m];
								if (firstSegment.label == null && secondSegment.label == null) {
									swConfig[l][m] = 0;
									swConfig[m][l] = 0;
								}
							}
						}
					}
				}

				// learning mode
				if (learningMode) {
					if ((resultModel.clbs[0].title.equals("CLB(0)") && resultModel.clbs[1].title.equals("CLB(2)")
							&& resultModel.clbs[2].title.equals("CLB(1)") && resultModel.clbs[3].title.equals("CLB(3)"))
							|| (resultModel.clbs[0].title.equals("CLB(1)") && resultModel.clbs[1].title.equals("CLB(3)")
									&& resultModel.clbs[2].title.equals("CLB(0)")
									&& resultModel.clbs[3].title.equals("CLB(2)"))) {
						architectureCounter++;
					}
				}
				if (alg.solFounded == true) {
					founded++; // one more founded
					generations += alg.finalGen; // more generations
				}
				
				//fitness maker
				if(fitnessImg && nums == 1) {
					new Thread(() -> {
						SwingUtilities.invokeLater(() -> {
							new EvolutionaryFPGAGUIMaker(
									alg.shortName, alg.name + " pop: " + alg.populationSize + " generations: "
											+ alg.generations + " mutation rate: " + alg.mutationRate,
									alg.genToBest, alg.genToAvg);
						});					
					}).start();
				
				}
				
				// intensity maker
				if (intensityImg && !alg.type &&  nums == 1) {
					new Thread(() -> {
						SwingUtilities.invokeLater(() -> {
							new EvolutionaryFPGAGUIMaker("Selection intensity process", alg.selector.intensities);
						});
					}).start();
				}
				//main GUI maker
				
				if (nums == 1) { // run mode, othewise "train" mode
					SwingUtilities.invokeLater(() -> {
						JFrame f = new JFrame("Preglednik rezultata mapiranja");

						f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
						f.setSize(1200, 960);
						f.getContentPane().setLayout(new BorderLayout());

						FPGATabX tab = new FPGATabX();
						tab.installFPGAModel(resultModel, sfpga);

						f.getContentPane().add(tab, BorderLayout.CENTER);
						tab.updateZoom(1.5);
						f.setVisible(true);
					});
				}

				if (nums > 1) {
					alg.reset();
				}
			}
		} catch (Exception ex) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy:MM:dd_HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			pw.write(dtf.format(now) + "\n");
			pw.write("Shortcut: " + alg.shortName + " Full name: "
					+ alg.name + "\n");
			ex.printStackTrace(pw);
			pw.write("\n\n");
		}
		t2 = System.currentTimeMillis();
		System.out.println("Running time: " + (t2 - t1) / 1000.0 + "s");
		System.out.println("Percentage: " + founded / (double) nums);
		System.out.println("Average generations: " + (double) generations / (double) founded);
		if (learningMode) {
			System.out.println("Architecture IQ percentage: " + architectureCounter / (double) nums);
		}
		System.out.println("***Printing output properties***");
		System.out.println("Aliases failures: " + FPGAEvaluator.aliasesFailures);
		System.out.println("Label is null: " + SimpleAliasesEvaluator.labelIsNull);
		System.out.println("Wrong type: " + SimpleAliasesEvaluator.wrongType);
		System.out.println("Wrong title name: " + SimpleAliasesEvaluator.wrongTitleName);
		System.out.println();
		System.out.println("***Printing input properites***");
		System.out.println("Input failures: " + FPGAEvaluator.inputsFailures);
		System.out.println("Input black label: " + SimpleCLBInputsEvaluator.inputBlackLabel);
		System.out.println("Input wrong type: " + SimpleCLBInputsEvaluator.inputWrongType);
		System.out.println("Wrong name: " + SimpleCLBInputsEvaluator.inputWrongName);
		pw.flush();
		pw.close();
		fw.close();
	}

}
