package Test;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

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
import hr.fer.zemris.bachelor.thesis.ai.crossover.SimpleCrossover;
import hr.fer.zemris.bachelor.thesis.ai.gui.EvolutionaryFPGAGUIMaker;
import hr.fer.zemris.bachelor.thesis.ai.initialization.AIFPGAConfigurationInitializer;
import hr.fer.zemris.bachelor.thesis.ai.mutation.Mutation;
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

		int popSize = 50, generations = 5000;
		double mutationRate = 0.05;

		FPGAMapTask mapTask = FPGAMapTask.fromSimpleFPGA(sfpga);

		AIFPGAConfigurationInitializer initer = new AIFPGAConfigurationInitializer(popSize, model);
		AIFPGAConfigurationRandomizer random = new AIFPGAConfigurationRandomizer(model);

		SwitchBoxCleaner simpleSwCleaner = new SimpleSwitchBoxCleaner(model);
		SwitchBoxCleaner advancedSwCleaner = new AdvancedSwitchBoxCleaner(model);
		AIFPGAConfigurationCleaner simpleCleanerSimpleSwitchBox = new AIFPGASimpleConfigurationCleaner(model,
				simpleSwCleaner);
		AIFPGAConfigurationCleaner simpleCleanerAdvancedSwitchBox = new AIFPGASimpleConfigurationCleaner(model,
				advancedSwCleaner);

		Crossover crosser = new SimpleCrossover(random, model);
		Crossover crosserAS = new CrossoverAS(random, model);
		Crossover crosserBreaker = new BreakpointCrossover(random, model);
		Mutation mutater = new SimpleMutation(mutationRate, random, model);
		
		Selector kTourSelect = new KTournamentSelection(2, random);
		Selector rouletteWheel = new RouletteWheelSelection(random);

		Evaluator aliasesEvaluator = new SimpleAliasesEvaluator();
		Evaluator clbInputsEvaluator = new SimpleCLBInputsEvaluator();
		Evaluator tracingEvaluator = new TracingEvaluator();
		Evaluator fpgaEvaluator = new FPGAEvaluator(aliasesEvaluator, clbInputsEvaluator, tracingEvaluator);

		FPGAGeneticAlgorithm alg1 = new SimplestGeneticAlgorithm("SV1", "Simplest version", popSize, generations,
				mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosser, mutater, fpgaEvaluator,
				mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg2 = new SimplestGeneticAlgorithm("SV2", "Simplest version", popSize, generations,
				mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosser, mutater, fpgaEvaluator,
				mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg3 = new SimplestGeneticAlgorithm("SV3", "Simplest version", popSize, generations,
				mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserAS, mutater, fpgaEvaluator,
				mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg4 = new SimplestGeneticAlgorithm("SV4", "Simplest version", popSize, generations,
				mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserBreaker, mutater,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg5 = new SimplestGeneticAlgorithm("SV5", "Simplest version", popSize, generations,
				mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserAS, mutater, fpgaEvaluator,
				mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg6 = new SimplestGeneticAlgorithm("SV6", "Simplest version", popSize, generations,
				mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserBreaker, mutater,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg7 = new SimplestGeneticAlgorithm("SV7", "Simplest version", popSize, generations,
				mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosser, null, fpgaEvaluator, mapTask,
				model, sfpga, logger);

		FPGAGeneticAlgorithm alg8 = new SimplestGeneticAlgorithm("SV8", "Simplest version", popSize, generations,
				mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosser, null, fpgaEvaluator,
				mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg9 = new SimplestGeneticAlgorithm("SV9", "Simplest version", popSize, generations,
				mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserAS, null, fpgaEvaluator,
				mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg10 = new SimplestGeneticAlgorithm("SV10", "Simplest version", popSize, generations,
				mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserBreaker, null, fpgaEvaluator,
				mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg11 = new SimplestGeneticAlgorithm("SV11", "Simplest version", popSize, generations,
				mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserAS, null, fpgaEvaluator,
				mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg12 = new SimplestGeneticAlgorithm("SV12", "Simplest version", popSize, generations,
				mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserBreaker, null, fpgaEvaluator,
				mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg13 = new SimplestGeneticAlgorithmElitistic("SV13", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosser,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg14 = new SimplestGeneticAlgorithmElitistic("SV14", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosser,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg15 = new SimplestGeneticAlgorithmElitistic("SV15", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserAS,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg16 = new SimplestGeneticAlgorithmElitistic("SV16", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserBreaker,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg17 = new SimplestGeneticAlgorithmElitistic("SV17", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserAS,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg18 = new SimplestGeneticAlgorithmElitistic("SV18", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null,
				crosserBreaker, mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg19 = new SimplestGeneticAlgorithmElitistic("SV19", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosser, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg20 = new SimplestGeneticAlgorithmElitistic("SV20", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosser, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg21 = new SimplestGeneticAlgorithmElitistic("SV21", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserAS, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg22 = new SimplestGeneticAlgorithmElitistic("SV22", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserBreaker,
				null, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg23 = new SimplestGeneticAlgorithmElitistic("SV23", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserAS,
				null, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg24 = new SimplestGeneticAlgorithmElitistic("S24", "Simplest version elitistic", popSize,
				generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserBreaker, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg25 = new GenerationalGeneticAlgorithm("SV25", "Generational version", popSize,
				generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosser, mutater,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg26 = new GenerationalGeneticAlgorithm("SV26", "Generational version", popSize,
				generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosser, mutater,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg27 = new GenerationalGeneticAlgorithm("SV27", "Generational version", popSize,
				generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserAS, mutater,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg28 = new GenerationalGeneticAlgorithm("SV28", "Generational version", popSize,
				generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserBreaker, mutater,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg29 = new GenerationalGeneticAlgorithm("SV29", "Generational version", popSize,
				generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserAS, mutater,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg30 = new GenerationalGeneticAlgorithm("SV30", "Generational version", popSize,
				generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserBreaker,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg31 = new GenerationalGeneticAlgorithm("SV31", "Generational version", popSize,
				generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosser, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg32 = new GenerationalGeneticAlgorithm("SV32", "Generational version", popSize,
				generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosser, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg33 = new GenerationalGeneticAlgorithm("SV33", "Generational version", popSize,
				generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserAS, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg34 = new GenerationalGeneticAlgorithm("SV34", "Generational version", popSize,
				generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserBreaker, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg35 = new GenerationalGeneticAlgorithm("SV35", "Generational version", popSize,
				generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserAS, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg36 = new GenerationalGeneticAlgorithm("SV36", "Generational version", popSize,
				generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserBreaker, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg37 = new GenerationalGeneticAlgorithmElitistic("SV37", "Generational version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosser,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg38 = new GenerationalGeneticAlgorithmElitistic("SV38", "Generational version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosser,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg39 = new GenerationalGeneticAlgorithmElitistic("SV39", "Generational version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserAS,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg40 = new GenerationalGeneticAlgorithmElitistic("SV40", "Generational version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserBreaker,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg41 = new GenerationalGeneticAlgorithmElitistic("SV41", "Generational version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserAS,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg42 = new GenerationalGeneticAlgorithmElitistic("SV42", "Generational version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null,
				crosserBreaker, mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg43 = new GenerationalGeneticAlgorithmElitistic("SV43", "Generational version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosser, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg44 = new GenerationalGeneticAlgorithmElitistic("SV44", "Generational version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosser, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg45 = new GenerationalGeneticAlgorithmElitistic("S45", "Generational version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserAS, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg46 = new GenerationalGeneticAlgorithmElitistic("SV46", "Generational version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserBreaker,
				null, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg47 = new GenerationalGeneticAlgorithmElitistic("SV47", "Generational version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserAS,
				null, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg48 = new GenerationalGeneticAlgorithmElitistic("SV48", "Generational version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null,
				crosserBreaker, null, fpgaEvaluator, mapTask, model, sfpga, logger);

		
		FPGAGeneticAlgorithm alg49 = new EliminativeGeneticAlgorithmSelectionElitistic("SV49", "EliminativeGeneticAlgorithmKSelectionElitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, kTourSelect, crosser,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg50 = new EliminativeGeneticAlgorithmSelectionElitistic("SV50", "EliminativeGeneticAlgorithmKSelectionElitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, kTourSelect, crosser,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg51 = new EliminativeGeneticAlgorithmSelectionElitistic("SV51", "EliminativeGeneticAlgorithmKSelectionElitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, kTourSelect, crosserAS,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg52 = new EliminativeGeneticAlgorithmSelectionElitistic("SV52", "EliminativeGeneticAlgorithmKSelectionElitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, kTourSelect, crosserBreaker,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg53 = new EliminativeGeneticAlgorithmSelectionElitistic("SV53", "EliminativeGeneticAlgorithmKSelectionElitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, kTourSelect, crosserAS,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg54 = new EliminativeGeneticAlgorithmSelectionElitistic("SV54", "EliminativeGeneticAlgorithmKSelectionElitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, kTourSelect,
				crosserBreaker, mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg55 = new EliminativeGeneticAlgorithmSelectionElitistic("SV55", "EliminativeGeneticAlgorithmKSelectionElitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, kTourSelect, crosser, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg56 = new EliminativeGeneticAlgorithmSelectionElitistic("SV56", "EliminativeGeneticAlgorithmKSelectionElitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, kTourSelect, crosser, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg57 = new EliminativeGeneticAlgorithmSelectionElitistic("SV57", "EliminativeGeneticAlgorithmKSelectionElitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, kTourSelect, crosserAS, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg58 = new EliminativeGeneticAlgorithmSelectionElitistic("SV58", "EliminativeGeneticAlgorithmKSelectionElitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, kTourSelect, crosserBreaker,
				null, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg59 = new EliminativeGeneticAlgorithmSelectionElitistic("SV59", "EliminativeGeneticAlgorithmKSelectionElitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, kTourSelect, crosserAS,
				null, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg60 = new EliminativeGeneticAlgorithmSelectionElitistic("SV60", "EliminativeGeneticAlgorithmKSelectionElitistic", popSize,
				generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, kTourSelect, crosserBreaker, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		
		
		FPGAGeneticAlgorithm alg61 = new SimplestGeneticAlgorithmElitistic("SV61", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, rouletteWheel, crosserBreaker,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg62 = new SimplestGeneticAlgorithmElitistic("SV62", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, rouletteWheel, crosserAS,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg63 = new SimplestGeneticAlgorithmElitistic("SV63", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, rouletteWheel,
				crosserBreaker, mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg64 = new SimplestGeneticAlgorithmElitistic("SV64", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, rouletteWheel, crosser, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg65 = new SimplestGeneticAlgorithmElitistic("SV65", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, rouletteWheel, crosser, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);
		
		
		FPGAGeneticAlgorithm[] instances = new FPGAGeneticAlgorithm[66];
		instances[0] = alg1;
		instances[1] = alg2;
		instances[2] = alg3;
		instances[3] = alg4;
		instances[4] = alg5;
		instances[5] = alg6;
		instances[6] = alg7;
		instances[7] = alg8;
		instances[8] = alg10;
		instances[9] = alg9;
		instances[10] = alg11;
		instances[11] = alg12;
		instances[12] = alg13;
		instances[13] = alg14;
		instances[14] = alg15;
		instances[15] = alg16;
		instances[16] = alg17;
		instances[17] = alg18;
		instances[18] = alg19;
		instances[19] = alg20;
		instances[20] = alg21;
		instances[21] = alg22;
		instances[22] = alg23;
		instances[23] = alg24;
		instances[24] = alg25;
		instances[25] = alg26;
		instances[26] = alg27;
		instances[27] = alg28;
		instances[28] = alg29;
		instances[29] = alg30;
		instances[30] = alg31;
		instances[31] = alg32;
		instances[32] = alg33;
		instances[33] = alg34;
		instances[34] = alg35;
		instances[35] = alg36;
		instances[36] = alg37;
		instances[37] = alg38;
		instances[38] = alg39;
		instances[39] = alg40;
		instances[40] = alg41;
		instances[41] = alg42;
		instances[42] = alg43;
		instances[43] = alg44;
		instances[44] = alg45;
		instances[45] = alg46;
		instances[46] = alg47;
		instances[47] = alg48;
		instances[48] = alg49;
		instances[49] = alg50;
		instances[50] = alg51;
		instances[51] = alg52;
		instances[52] = alg53;
		instances[53] = alg54;
		instances[54] = alg55;
		instances[55] = alg56;
		instances[56] = alg57;
		instances[57] = alg58;
		instances[58] = alg59;
		instances[59] = alg60;
		instances[60] = alg61;
		instances[61] = alg62;
		instances[62] = alg63;
		instances[63] = alg64;
		instances[64] = alg65;
		
		FileWriter fw  = new FileWriter ("exception.txt", true);
		PrintWriter pw = new PrintWriter (fw);
		
		for (int i = 15; i < 16; i++) {

			try {
				for (int j = 0; j < 5; j++) {
					FPGAGeneticAlgorithm alg = instances[i];
					FPGAModel resultModel = mapper.map(alg);
					
					SwingUtilities.invokeLater(() -> {
						new EvolutionaryFPGAGUIMaker(alg.shortName,
								alg.name + " pop: " + alg.populationSize + " generations: " + alg.generations + " mutation rate: " + mutationRate,
								alg.genToBest, alg.genToAvg);
					});

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
			} catch(Exception ex) {
				pw.write("Index " + i + " Shortcut: " + instances[i].shortName + " Full name: " + instances[i].name + "\n");
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
