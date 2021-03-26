package hr.fer.zemris.bachelor.thesis.ai;

import hr.fer.zemris.bachelor.thesis.ai.initialization.Initializer;
import hr.fer.zemris.bachelor.thesis.ai.selection.Selector;
import hr.fer.zemris.bachelor.thesis.evaluator.Evaluator;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationRandomizer;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.AIFPGAConfigurationCleaner;

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
	
	public AIFPGAConfiguration bestOverall;
	
	public AIFPGAConfiguration bestInGen;
	
	public Initializer<AIFPGAConfiguration> initializer;
	
	public AIFPGAConfigurationRandomizer randomizer;
	
	public AIFPGAConfigurationCleaner cleaner;
	
	public Selector selector;
	
	public Crossover crosser;
	
	public Mutation mutator;
	
	public Evaluator evaluator;

	/**
	 * Main "loop" algorithm. Life iterator :)
	 */
	public abstract void reproduction();

}
