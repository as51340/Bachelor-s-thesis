package hr.fer.zemris.bachelor.thesis.util;

import hr.fer.zemris.bachelor.thesis.ai.EliminativeGeneticAlgorithmSelectionElitistic;
import hr.fer.zemris.bachelor.thesis.ai.FPGAGeneticAlgorithm;
import hr.fer.zemris.bachelor.thesis.ai.GenerationalGeneticAlgorithm;
import hr.fer.zemris.bachelor.thesis.ai.GenerationalGeneticAlgorithmElitistic;
import hr.fer.zemris.bachelor.thesis.ai.GenerationalGeneticAlgorithmSelection;
import hr.fer.zemris.bachelor.thesis.ai.SimplestGeneticAlgorithm;
import hr.fer.zemris.bachelor.thesis.ai.SimplestGeneticAlgorithmElitistic;
import hr.fer.zemris.bachelor.thesis.ai.crossover.BreakpointCrossover;
import hr.fer.zemris.bachelor.thesis.ai.crossover.Crossover;
import hr.fer.zemris.bachelor.thesis.ai.crossover.CrossoverAS;
import hr.fer.zemris.bachelor.thesis.ai.crossover.ExponentialCrossover;
import hr.fer.zemris.bachelor.thesis.ai.crossover.SimpleCrossover;
import hr.fer.zemris.bachelor.thesis.ai.crossover.ValidCrossover;
import hr.fer.zemris.bachelor.thesis.ai.initialization.AIFPGAConfigurationInitializer;
import hr.fer.zemris.bachelor.thesis.ai.mutation.Mutation;
import hr.fer.zemris.bachelor.thesis.ai.mutation.MutationSwap;
import hr.fer.zemris.bachelor.thesis.ai.mutation.SimpleMutation;
import hr.fer.zemris.bachelor.thesis.ai.selection.KTournamentSelection;
import hr.fer.zemris.bachelor.thesis.ai.selection.RouletteWheelSelection;
import hr.fer.zemris.bachelor.thesis.ai.selection.Selector;
import hr.fer.zemris.bachelor.thesis.evaluator.Evaluator;
import hr.fer.zemris.bachelor.thesis.evaluator.FPGAEvaluator;
import hr.fer.zemris.bachelor.thesis.evaluator.SimpleAliasesEvaluator;
import hr.fer.zemris.bachelor.thesis.evaluator.SimpleCLBInputsEvaluator;
import hr.fer.zemris.bachelor.thesis.evaluator.TracingEvaluator;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationRandomizer;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.AIFPGAConfigurationCleaner;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.AIFPGASimpleConfigurationCleaner;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.swboxes.AdvancedSwitchBoxCleaner;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.swboxes.SimpleSwitchBoxCleaner;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.swboxes.SwitchBoxCleaner;
import hr.fer.zemris.bool.SimpleFPGA;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.LogWriter;
import hr.fer.zemris.fpga.mapping.FPGAMapTask;

/**
 * Library of all genetic algorithms used for problem of FPGA mapping.
 * @author andi
 *
 */
public class AILibrary {
	
	
	private int popSize, generations, librarySize;
	
	private double mutationRate = 0.05;

	public FPGAGeneticAlgorithm[] instances;
	
	public AILibrary(int popSize, int generations, int librarySize) {
		this.popSize = popSize;
		this.generations = generations;
		this.librarySize = librarySize;
	}
	
	public int getPopSize() {
		return popSize;
	}

	public void setPopSize(int popSize) {
		this.popSize = popSize;
	}

	public int getGenerations() {
		return generations;
	}

	public void setGenerations(int generations) {
		this.generations = generations;
	}

	public int getSize() {
		return librarySize;
	}


	public double getMutationRate() {
		return mutationRate;
	}

	public void setMutationRate(double mutationRate) {
		this.mutationRate = mutationRate;
	}
	
	public FPGAGeneticAlgorithm getByName(String algName) {
		for(int i = 0; i < instances.length; i++) {
			if(instances[i].shortName.equals(algName)) {
				return instances[i];
			}
		}
		return null;
	}

	public void constructLibrary(FPGAModel model, FPGAMapTask mapTask, SimpleFPGA sfpga, LogWriter logger) {
		instances = new FPGAGeneticAlgorithm[librarySize];
		
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
		Crossover validCrosser = new ValidCrossover(random, model);
		Crossover expCrosser = new ExponentialCrossover(random, model);

		Mutation mutater = new SimpleMutation(mutationRate, random, model);
		Mutation swapMutator = new MutationSwap(mutationRate, random, model);

		Selector kTourSelect = new KTournamentSelection(5, random);
		Selector rouletteWheel = new RouletteWheelSelection(random);

		Evaluator fpgaEvaluator = new FPGAEvaluator();

		FPGAGeneticAlgorithm alg1 = new SimplestGeneticAlgorithm(false, "SV1", "Simplest version", popSize, generations,
				mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosser, mutater, fpgaEvaluator,
				mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg2 = new SimplestGeneticAlgorithm(false, "SV2", "Simplest version", popSize, generations,
				mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosser, mutater, fpgaEvaluator,
				mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg3 = new SimplestGeneticAlgorithm(false, "SV3", "Simplest version", popSize, generations,
				mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserAS, mutater, fpgaEvaluator,
				mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg4 = new SimplestGeneticAlgorithm(false, "SV4", "Simplest version", popSize, generations,
				mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserBreaker, mutater,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg5 = new SimplestGeneticAlgorithm(false, "SV5", "Simplest version", popSize, generations,
				mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserAS, mutater, fpgaEvaluator,
				mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg6 = new SimplestGeneticAlgorithm(false, "SV6", "Simplest version", popSize, generations,
				mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserBreaker, mutater,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg7 = new SimplestGeneticAlgorithm(false, "SV7", "Simplest version", popSize, generations,
				mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosser, null, fpgaEvaluator, mapTask,
				model, sfpga, logger);

		FPGAGeneticAlgorithm alg8 = new SimplestGeneticAlgorithm(false, "SV8", "Simplest version", popSize, generations,
				mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosser, null, fpgaEvaluator,
				mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg9 = new SimplestGeneticAlgorithm(false, "SV9", "Simplest version", popSize, generations,
				mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserAS, null, fpgaEvaluator,
				mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg10 = new SimplestGeneticAlgorithm(false, "SV10", "Simplest version", popSize, generations,
				mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserBreaker, null, fpgaEvaluator,
				mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg11 = new SimplestGeneticAlgorithm(false, "SV11", "Simplest version", popSize, generations,
				mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserAS, null, fpgaEvaluator,
				mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg12 = new SimplestGeneticAlgorithm(false, "SV12", "Simplest version", popSize, generations,
				mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserBreaker, null, fpgaEvaluator,
				mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg13 = new SimplestGeneticAlgorithmElitistic(false, "SV13", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosser,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg14 = new SimplestGeneticAlgorithmElitistic(false, "SV14", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosser,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg15 = new SimplestGeneticAlgorithmElitistic(false, "SV15", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserAS,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg16 = new SimplestGeneticAlgorithmElitistic(false, "SV16", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, validCrosser,
				swapMutator, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg17 = new SimplestGeneticAlgorithmElitistic(false, "SV17", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserAS,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg18 = new SimplestGeneticAlgorithmElitistic(false, "SV18", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null,
				crosserBreaker, mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg19 = new SimplestGeneticAlgorithmElitistic(false, "SV19", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosser, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg20 = new SimplestGeneticAlgorithmElitistic(false, "SV20", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosser, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg21 = new SimplestGeneticAlgorithmElitistic(false, "SV21", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserAS, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg22 = new SimplestGeneticAlgorithmElitistic(false, "SV22", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserBreaker,
				null, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg23 = new SimplestGeneticAlgorithmElitistic(false, "SV23", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserAS,
				null, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg24 = new SimplestGeneticAlgorithmElitistic(false, "S24", "Simplest version elitistic", popSize,
				generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserBreaker, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg25 = new GenerationalGeneticAlgorithm(true, "SV25", "Generational version", popSize,
				generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosser, mutater,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg26 = new GenerationalGeneticAlgorithm(true, "SV26", "Generational version", popSize,
				generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosser, mutater,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg27 = new GenerationalGeneticAlgorithm(true, "SV27", "Generational version", popSize,
				generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserAS, mutater,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg28 = new GenerationalGeneticAlgorithm(true, "SV28", "Generational version", popSize,
				generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserBreaker, mutater,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg29 = new GenerationalGeneticAlgorithm(true, "SV29", "Generational version", popSize,
				generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserAS, mutater,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg30 = new GenerationalGeneticAlgorithm(true, "SV30", "Generational version", popSize,
				generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserBreaker,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg31 = new GenerationalGeneticAlgorithm(true, "SV31", "Generational version", popSize,
				generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosser, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg32 = new GenerationalGeneticAlgorithm(true, "SV32", "Generational version", popSize,
				generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosser, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg33 = new GenerationalGeneticAlgorithm(true, "SV33", "Generational version", popSize,
				generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserAS, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg34 = new GenerationalGeneticAlgorithm(true, "SV34", "Generational version", popSize,
				generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserBreaker, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg35 = new GenerationalGeneticAlgorithm(true, "SV35", "Generational version", popSize,
				generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserAS, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg36 = new GenerationalGeneticAlgorithm(true, "SV36", "Generational version", popSize,
				generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserBreaker, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg37 = new GenerationalGeneticAlgorithmElitistic(true, "SV37", "Generational version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosser,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg38 = new GenerationalGeneticAlgorithmElitistic(true, "SV38", "Generational version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosser,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg39 = new GenerationalGeneticAlgorithmElitistic(true, "SV39", "Generational version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserAS,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg40 = new GenerationalGeneticAlgorithmElitistic(true, "SV40", "Generational version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserBreaker,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg41 = new GenerationalGeneticAlgorithmElitistic(true, "SV41", "Generational version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserAS,
				mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg42 = new GenerationalGeneticAlgorithmElitistic(true, "SV42", "Generational version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null,
				crosserBreaker, mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg43 = new GenerationalGeneticAlgorithmElitistic(true, "SV43", "Generational version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosser, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg44 = new GenerationalGeneticAlgorithmElitistic(true, "SV44", "Generational version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosser, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg45 = new GenerationalGeneticAlgorithmElitistic(true, "S45", "Generational version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserAS, null,
				fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg46 = new GenerationalGeneticAlgorithmElitistic(true, "SV46", "Generational version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, null, crosserBreaker,
				null, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg47 = new GenerationalGeneticAlgorithmElitistic(true, "SV47", "Generational version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, crosserAS,
				null, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg48 = new GenerationalGeneticAlgorithmElitistic(true, "SV48", "Generational version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null,
				crosserBreaker, null, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg49 = new EliminativeGeneticAlgorithmSelectionElitistic(false, "SV49",
				"EliminativeGeneticAlgorithmKSelectionElitistic", popSize, generations, mutationRate, initer, random,
				simpleCleanerSimpleSwitchBox, kTourSelect, crosser, mutater, fpgaEvaluator, mapTask, model, sfpga,
				logger);

		FPGAGeneticAlgorithm alg50 = new EliminativeGeneticAlgorithmSelectionElitistic(false, "SV50",
				"EliminativeGeneticAlgorithmKSelectionElitistic", popSize, generations, mutationRate, initer, random,
				simpleCleanerAdvancedSwitchBox, kTourSelect, crosser, mutater, fpgaEvaluator, mapTask, model, sfpga,
				logger);

		FPGAGeneticAlgorithm alg51 = new EliminativeGeneticAlgorithmSelectionElitistic(false, "SV51",
				"EliminativeGeneticAlgorithmKSelectionElitistic", popSize, generations, mutationRate, initer, random,
				simpleCleanerSimpleSwitchBox, kTourSelect, crosserAS, mutater, fpgaEvaluator, mapTask, model, sfpga,
				logger);

		FPGAGeneticAlgorithm alg52 = new EliminativeGeneticAlgorithmSelectionElitistic(false, "SV52",
				"EliminativeGeneticAlgorithmKSelectionElitistic", popSize, generations, mutationRate, initer, random,
				simpleCleanerSimpleSwitchBox, kTourSelect, crosserBreaker, mutater, fpgaEvaluator, mapTask, model,
				sfpga, logger);

		FPGAGeneticAlgorithm alg53 = new EliminativeGeneticAlgorithmSelectionElitistic(false, "SV53",
				"EliminativeGeneticAlgorithmKSelectionElitistic", popSize, generations, mutationRate, initer, random,
				simpleCleanerAdvancedSwitchBox, kTourSelect, crosserAS, mutater, fpgaEvaluator, mapTask, model, sfpga,
				logger);

		FPGAGeneticAlgorithm alg54 = new EliminativeGeneticAlgorithmSelectionElitistic(false, "SV54",
				"EliminativeGeneticAlgorithmKSelectionElitistic", popSize, generations, mutationRate, initer, random,
				simpleCleanerAdvancedSwitchBox, kTourSelect, crosserBreaker, mutater, fpgaEvaluator, mapTask, model,
				sfpga, logger);

		FPGAGeneticAlgorithm alg55 = new EliminativeGeneticAlgorithmSelectionElitistic(false, "SV55",
				"EliminativeGeneticAlgorithmKSelectionElitistic", popSize, generations, mutationRate, initer, random,
				simpleCleanerSimpleSwitchBox, kTourSelect, crosser, null, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg56 = new EliminativeGeneticAlgorithmSelectionElitistic(false, "SV56",
				"EliminativeGeneticAlgorithmKSelectionElitistic", popSize, generations, mutationRate, initer, random,
				simpleCleanerAdvancedSwitchBox, kTourSelect, crosser, null, fpgaEvaluator, mapTask, model, sfpga,
				logger);

		FPGAGeneticAlgorithm alg57 = new EliminativeGeneticAlgorithmSelectionElitistic(false, "SV57",
				"EliminativeGeneticAlgorithmKSelectionElitistic", popSize, generations, mutationRate, initer, random,
				simpleCleanerSimpleSwitchBox, kTourSelect, crosserAS, null, fpgaEvaluator, mapTask, model, sfpga,
				logger);

		FPGAGeneticAlgorithm alg58 = new EliminativeGeneticAlgorithmSelectionElitistic(false, "SV58",
				"EliminativeGeneticAlgorithmKSelectionElitistic", popSize, generations, mutationRate, initer, random,
				simpleCleanerSimpleSwitchBox, kTourSelect, crosserBreaker, null, fpgaEvaluator, mapTask, model, sfpga,
				logger);

		FPGAGeneticAlgorithm alg59 = new EliminativeGeneticAlgorithmSelectionElitistic(false, "SV59",
				"EliminativeGeneticAlgorithmKSelectionElitistic", popSize, generations, mutationRate, initer, random,
				simpleCleanerAdvancedSwitchBox, kTourSelect, crosserAS, null, fpgaEvaluator, mapTask, model, sfpga,
				logger);

		FPGAGeneticAlgorithm alg60 = new EliminativeGeneticAlgorithmSelectionElitistic(false, "SV60",
				"EliminativeGeneticAlgorithmKSelectionElitistic", popSize, generations, mutationRate, initer, random,
				simpleCleanerAdvancedSwitchBox, kTourSelect, crosserBreaker, null, fpgaEvaluator, mapTask, model, sfpga,
				logger);
		

		FPGAGeneticAlgorithm alg61 = new EliminativeGeneticAlgorithmSelectionElitistic(false, "SV61", "EliminativeGeneticAlgorithmKSelectionElitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, rouletteWheel,
				crosserBreaker, mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg62 = new EliminativeGeneticAlgorithmSelectionElitistic(false, "SV62", "EliminativeGeneticAlgorithmKSelectionElitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, rouletteWheel,
				crosserAS, mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg63 = new EliminativeGeneticAlgorithmSelectionElitistic(false, "SV63", "EliminativeGeneticAlgorithmKSelectionElitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, rouletteWheel,
				crosserBreaker, mutater, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg64 = new EliminativeGeneticAlgorithmSelectionElitistic(false, "SV64", "EliminativeGeneticAlgorithmKSelectionElitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerSimpleSwitchBox, rouletteWheel,
				crosser, null, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg65 = new EliminativeGeneticAlgorithmSelectionElitistic(false, "SV65", "EliminativeGeneticAlgorithmKSelectionElitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, rouletteWheel,
				crosser, null, fpgaEvaluator, mapTask, model, sfpga, logger);

		FPGAGeneticAlgorithm alg66 = new EliminativeGeneticAlgorithmSelectionElitistic(false, "SV66", "EliminativeGeneticAlgorithmKSelectionElitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, rouletteWheel,
				validCrosser, swapMutator, fpgaEvaluator, mapTask, model, sfpga, logger);
		
		
		
		FPGAGeneticAlgorithm alg67 = new SimplestGeneticAlgorithmElitistic(false, "SV67", "Simplest version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, 
				validCrosser, swapMutator, fpgaEvaluator, mapTask, model, sfpga, logger);
		
		FPGAGeneticAlgorithm alg68 = new GenerationalGeneticAlgorithmElitistic(true, "S68", "Generational version elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, null, validCrosser, swapMutator,
				fpgaEvaluator, mapTask, model, sfpga, logger);
		
		FPGAGeneticAlgorithm alg69 = new EliminativeGeneticAlgorithmSelectionElitistic(false, "SV69", "Eliminative elitistic",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, kTourSelect,
				validCrosser, swapMutator, fpgaEvaluator, mapTask, model, sfpga, logger);
		
		
		FPGAGeneticAlgorithm alg70 = new GenerationalGeneticAlgorithmSelection(true, "SV70", "Generational version elitistic, selection",
				popSize, generations, mutationRate, initer, random, simpleCleanerAdvancedSwitchBox, rouletteWheel, validCrosser, swapMutator,
				fpgaEvaluator, mapTask, model, sfpga, logger);
		
		
		
		

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
		instances[65] = alg66;
		instances[66] = alg67;
		instances[67] = alg68;
		instances[68] = alg69;
		instances[69] = alg70;
		
		
	}

}
