package hr.fer.zemris.bachelor.thesis.ai;

import hr.fer.zemris.bachelor.thesis.ai.crossover.Crossover;
import hr.fer.zemris.bachelor.thesis.ai.initialization.Initializer;
import hr.fer.zemris.bachelor.thesis.ai.mutation.Mutation;
import hr.fer.zemris.bachelor.thesis.ai.selection.Selector;
import hr.fer.zemris.bachelor.thesis.evaluator.Evaluator;
import hr.fer.zemris.bachelor.thesis.evaluator.FPGAEvaluator;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationRandomizer;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.AIFPGAConfigurationCleaner;
import hr.fer.zemris.bool.SimpleFPGA;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.LogWriter;
import hr.fer.zemris.fpga.mapping.FPGAMapTask;

/**
 * Genetic algorithm and its properties.
 * @author andi
 *
 */
public abstract class FPGAGeneticAlgorithm {
	
	public int populationSize;
	
	public int generations;
	
	public double mutationRate;
	
	public AIFPGAConfiguration[] population;
	
	public double[] fitnesses;
	
	public FPGAModel bestOverall = null;
	
	public FPGAModel bestInGen = null;
	
	public Initializer<AIFPGAConfiguration> initializer;
	
	public AIFPGAConfigurationRandomizer randomizer;
	
	public AIFPGAConfigurationCleaner cleaner;
	
	public Selector selector;
	
	public Crossover crosser;
	
	public Mutation mutator;
	
	public Evaluator evaluator;
	
	public FPGAMapTask mapTask;
	
	public FPGAModel ex;
	
	public SimpleFPGA sfpga;
	
	public LogWriter logger;


	public FPGAGeneticAlgorithm(int populationSize, int generations, double mutationRate,
			Initializer<AIFPGAConfiguration> initializer, AIFPGAConfigurationRandomizer randomizer,
			AIFPGAConfigurationCleaner cleaner, Selector selector, Crossover crosser, Mutation mutator,
			Evaluator evaluator, FPGAMapTask mapTask, FPGAModel ex, SimpleFPGA sfpga, LogWriter logger) {
		super();
		this.populationSize = populationSize;
		this.generations = generations;
		this.mutationRate = mutationRate;
		this.initializer = initializer;
		this.randomizer = randomizer;
		this.cleaner = cleaner;
		this.selector = selector;
		this.crosser = crosser;
		this.mutator = mutator;
		this.evaluator = evaluator;
		this.mapTask = mapTask;
		this.ex = ex;
		this.sfpga = sfpga;
		this.logger = logger;
		this.fitnesses = new double[populationSize];
	}






	/**
	 * Main "loop" algorithm. Life iterator :)
	 */
	public abstract void reproduction();

}
