package hr.fer.zemris.bachelor.thesis.mapping;

import hr.fer.zemris.bachelor.thesis.ai.FPGAGeneticAlgorithm;
import hr.fer.zemris.bachelor.thesis.ai.SimplestGeneticAlgorithm;
import hr.fer.zemris.bachelor.thesis.ai.crossover.Crossover;
import hr.fer.zemris.bachelor.thesis.ai.crossover.SimpleCrossover;
import hr.fer.zemris.bachelor.thesis.ai.initialization.AIFPGAConfigurationInitializer;
import hr.fer.zemris.bachelor.thesis.ai.mutation.Mutation;
import hr.fer.zemris.bachelor.thesis.ai.mutation.SimpleMutation;
import hr.fer.zemris.bachelor.thesis.evaluator.Evaluator;
import hr.fer.zemris.bachelor.thesis.evaluator.FPGAEvaluator;
import hr.fer.zemris.bachelor.thesis.evaluator.SimpleAliasesEvaluator;
import hr.fer.zemris.bachelor.thesis.evaluator.SimpleCLBInputsEvaluator;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationRandomizer;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.AIFPGAAdvancedConfigurationCleaner;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.AIFPGAConfigurationCleaner;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.swboxes.SimpleSwitchBoxCleaner;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.swboxes.SwitchBoxCleaner;
import hr.fer.zemris.bool.SimpleFPGA;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.LogWriter;
import hr.fer.zemris.fpga.mapping.FPGAMapTask;

/**
 * Maps logic model into real physical model. LogWriter is used for writing to
 * user defined output.
 * 
 * @author andi
 *
 */
public class AIFPGAMapper {

	// Not implemented yet
	// protected GUIEvolutionVisualizer visualizer;
	
	public FPGAModel model;

	public SimpleFPGA sfpga;

	public LogWriter logger;

	public FPGAGeneticAlgorithm alg;

	public AIFPGAMapper(FPGAModel model, SimpleFPGA sfpga, LogWriter logger) {
		super();
		this.model = model;
		this.sfpga = sfpga;
		this.logger = logger;
	}
	
	private String toTime(long t) {
		long minuta = t / (1000*60);
		t -= minuta * (1000*60);
		long sekundi = t / (1000);
		t -= sekundi * (1000);
		return minuta+" min, "+sekundi+" sek, "+t+" tis.";
	}


	public FPGAModel map() {
		int popSize = 500, generations = 1000;
		AIFPGAConfigurationInitializer initer = new AIFPGAConfigurationInitializer(popSize, model);
		AIFPGAConfigurationRandomizer random = new AIFPGAConfigurationRandomizer(model);
		
		SwitchBoxCleaner swCleaner = new SimpleSwitchBoxCleaner(model);
		AIFPGAConfigurationCleaner cleaner = new AIFPGAAdvancedConfigurationCleaner(model, swCleaner, random);
		
		Crossover crosser = new SimpleCrossover(random, model);
		double mutationRate = 0.05;
		Mutation mutater = new SimpleMutation(mutationRate, random, model);
		
		FPGAMapTask mapTask = FPGAMapTask.fromSimpleFPGA(sfpga);

		Evaluator aliasesEvaluator = new SimpleAliasesEvaluator();
		Evaluator clbInputsEvaluator = new SimpleCLBInputsEvaluator();
		Evaluator fpgaEvaluator = new FPGAEvaluator(aliasesEvaluator, clbInputsEvaluator);
		alg = new SimplestGeneticAlgorithm(popSize, generations, mutationRate, initer, random, cleaner, null, crosser, mutater,
				fpgaEvaluator, mapTask, model, sfpga, logger);
		logger.log("Generations: " + generations);
		logger.log("Population size: " + popSize);
		logger.log("Mutation rate: " + mutationRate);
		long t0 = System.currentTimeMillis();
		alg.reproduction();
		long t1 = System.currentTimeMillis();
		System.out.println(toTime(t1 -t0));
		return alg.bestOverall;
	}

}
