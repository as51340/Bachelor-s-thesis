package hr.fer.zemris.bachelor.thesis.mapping;

import hr.fer.zemris.bachelor.thesis.ai.FPGAGeneticAlgorithm;
import hr.fer.zemris.bachelor.thesis.ai.GenerationalGeneticAlgorithm;
import hr.fer.zemris.bachelor.thesis.ai.GenerationalGeneticAlgorithmElitistic;
import hr.fer.zemris.bachelor.thesis.ai.SimplestGeneticAlgorithm;
import hr.fer.zemris.bachelor.thesis.ai.SimplestGeneticAlgorithmElitistic;
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
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.AIFPGASimpleConfigurationCleaner;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.swboxes.AdvancedSwitchBoxCleaner;
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


	public LogWriter logger;

	public AIFPGAMapper(LogWriter logger) {
		super();
		this.logger = logger;
	}
	
	private String toTime(long t) {
		long minuta = t / (1000*60);
		t -= minuta * (1000*60);
		long sekundi = t / (1000);
		t -= sekundi * (1000);
		return minuta+" min, "+sekundi+" sek, "+t+" tis.";
	}


	public FPGAModel map(FPGAGeneticAlgorithm alg) {
//		logger.log("Generations: " + alg.generations);
//		logger.log("Population size: " + alg.populationSize);
//		logger.log("Mutation rate: " + alg.mutationRate);		
		long t0 = System.currentTimeMillis();
		alg.reproduction();
		long t1 = System.currentTimeMillis();
		//logger.log(toTime(t1 -t0));
		return alg.bestOverall;
	}

}
