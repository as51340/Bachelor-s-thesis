package hr.fer.zemris.bachelor.thesis.ai;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
 * 
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

	public Map<Integer, Double> genToBest;
	
	public Map<Integer, Double> genToAvg;
	
	
	public String name;
	
	public String shortName;
	
	//false eliminative, true generational
	public boolean type;
	
	public AIFPGAConfiguration bestConf = null;
	
	
	public FPGAGeneticAlgorithm(boolean type, String shortName, String name, int populationSize, int generations, double mutationRate,
			Initializer<AIFPGAConfiguration> initializer, AIFPGAConfigurationRandomizer randomizer,
			AIFPGAConfigurationCleaner cleaner, Selector selector, Crossover crosser, Mutation mutator,
			Evaluator evaluator, FPGAMapTask mapTask, FPGAModel ex, SimpleFPGA sfpga, LogWriter logger) {
		super();
		this.type = type;
		this.shortName = shortName;
		this.name = name;
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
		genToBest = new HashMap<>();
		genToAvg = new HashMap<>();
	}
	
	void putAverageFitnessForGen(int gen) {
		genToAvg.put(gen, Arrays.stream(fitnesses).average().getAsDouble());
	}
	
	void putBestFitnessForGen(int gen) {
		genToBest.put(gen,  Arrays.stream(fitnesses).max().getAsDouble());
	}
	public boolean checkEvaluatorEnding(FPGAModel model) {
		if (((FPGAEvaluator) evaluator).valid) { // you founded valid model so set bestOverall and exit
			bestOverall = model;
			logger.log("Founded\n");
			return true;
		}
		((FPGAEvaluator) evaluator).valid = true; 
		return false;
	}

	/**
	 * Main "loop" algorithm. Life iterator :)
	 */
	public abstract void reproduction();

}
