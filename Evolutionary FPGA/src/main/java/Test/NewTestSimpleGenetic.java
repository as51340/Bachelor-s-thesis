package Test;

import java.awt.BorderLayout;
import java.io.File;
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

import hr.fer.zemris.bachelor.thesis.ai.EliminativeGeneticAlgorithmSelectionElitistic;
import hr.fer.zemris.bachelor.thesis.ai.FPGAGeneticAlgorithm;
import hr.fer.zemris.bachelor.thesis.ai.GenerationalGeneticAlgorithm;
import hr.fer.zemris.bachelor.thesis.ai.GenerationalGeneticAlgorithmElitistic;
import hr.fer.zemris.bachelor.thesis.ai.SimplestGeneticAlgorithm;
import hr.fer.zemris.bachelor.thesis.ai.SimplestGeneticAlgorithmElitistic;
import hr.fer.zemris.bachelor.thesis.ai.crossover.BreakpointCrossover;
import hr.fer.zemris.bachelor.thesis.ai.crossover.Crossover;
import hr.fer.zemris.bachelor.thesis.ai.crossover.CrossoverAS;
import hr.fer.zemris.bachelor.thesis.ai.crossover.ExponentialCrossover;
import hr.fer.zemris.bachelor.thesis.ai.crossover.SimpleCrossover;
import hr.fer.zemris.bachelor.thesis.ai.crossover.ValidCrossover;
import hr.fer.zemris.bachelor.thesis.ai.gui.EvolutionaryFPGAGUIMaker;
import hr.fer.zemris.bachelor.thesis.ai.initialization.AIFPGAConfigurationInitializer;
import hr.fer.zemris.bachelor.thesis.ai.mutation.Mutation;
import hr.fer.zemris.bachelor.thesis.ai.mutation.MutationSwap;
import hr.fer.zemris.bachelor.thesis.ai.mutation.SimpleMutation;
import hr.fer.zemris.bachelor.thesis.ai.selection.KTournamentSelection;
import hr.fer.zemris.bachelor.thesis.ai.selection.Selector;
import hr.fer.zemris.bachelor.thesis.ai.selection.RouletteWheelSelection;
import hr.fer.zemris.bachelor.thesis.evaluator.Evaluator;
import hr.fer.zemris.bachelor.thesis.evaluator.FPGAEvaluator;
import hr.fer.zemris.bachelor.thesis.evaluator.SimpleAliasesEvaluator;
import hr.fer.zemris.bachelor.thesis.evaluator.SimpleCLBInputsEvaluator;
import hr.fer.zemris.bachelor.thesis.evaluator.TracingEvaluator;
import hr.fer.zemris.bachelor.thesis.mapping.AIFPGAMapper;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationRandomizer;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.AIFPGAAdvancedConfigurationCleaner;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.AIFPGAConfigurationCleaner;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.AIFPGASimpleConfigurationCleaner;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.swboxes.AdvancedSwitchBoxCleaner;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.swboxes.SimpleSwitchBoxCleaner;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.swboxes.SwitchBoxCleaner;
import hr.fer.zemris.bachelor.thesis.util.AILibrary;
import hr.fer.zemris.bool.SimpleFPGA;
import hr.fer.zemris.fpga.FPGAModel;
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
		int rows = 2, columns = 2, pins = 1, variables = 2, wires = 3;
		FPGAModel model = new FPGAModel(rows, columns, variables, wires, pins);
		String fileName = "./src/main/resources/decomp-example-01.txt"; // wont load with resource as stream
		String text = Files.readString(Paths.get(fileName));
		SimpleFPGA sfpga = SimpleFPGA.parseFromText(text);

		LogWriter logger = new StandardLogWriter();
		AIFPGAMapper mapper = new AIFPGAMapper(logger);
		FPGAMapTask mapTask = FPGAMapTask.fromSimpleFPGA(sfpga);
		
		AILibrary library = new AILibrary();
		library.constructLibrary(model, mapTask, sfpga, logger);
		
		

		FileWriter fw = new FileWriter("exception.txt", true);
		PrintWriter pw = new PrintWriter(fw);

		for (int i = 60; i < 66; i++) {
			FPGAGeneticAlgorithm alg = library.instances[i];
			try {
				for (int j = 0; j < 1; j++) {
					alg.selector.intensities.clear();
					FPGAModel resultModel = mapper.map(alg);

					SwingUtilities.invokeLater(() -> {
						new EvolutionaryFPGAGUIMaker(
								alg.shortName, alg.name + " pop: " + alg.populationSize + " generations: "
										+ alg.generations + " mutation rate: " + alg.mutationRate,
								alg.genToBest, alg.genToAvg);
					});
					if(alg.selector != null) {
						SwingUtilities.invokeLater(() -> {
							new EvolutionaryFPGAGUIMaker("Selection intensity process", alg.selector.intensities);
						});
					}

					if (resultModel == null) {
						logger.log("Result model is null and cannot be seen!\n");
						continue;
					}

					SwingUtilities.invokeLater(() -> {
						JFrame f = new JFrame("Preglednik rezultata mapiranja");

						f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
						f.setSize(500, 500);
						f.getContentPane().setLayout(new BorderLayout());

						FPGATabX tab = new FPGATabX();
						tab.installFPGAModel(resultModel, sfpga);

						f.getContentPane().add(tab, BorderLayout.CENTER);

						f.setVisible(true);
					});
				}
			} catch (Exception ex) {
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy:MM:dd_HH:mm:ss");
				LocalDateTime now = LocalDateTime.now();
				pw.write(dtf.format(now) + "\n");
				pw.write("Index " + i + " Shortcut: " + library.instances[i].shortName + " Full name: " + library.instances[i].name
						+ "\n");
				ex.printStackTrace(pw);
				pw.write("\n\n");
			}

		}
		pw.flush();

		pw.close();
		fw.close();

		// .setVisible(true)

	}

}
