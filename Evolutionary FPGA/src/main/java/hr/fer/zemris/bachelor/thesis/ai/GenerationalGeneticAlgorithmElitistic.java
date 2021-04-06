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
 * Ordinal generational gen. algorithm. Not elitistic. 
 * @param shortName
 * @param name
 * @param populationSize
 * @param generations
 * @param mutationRate
 * @param initializer
 * @param randomizer
 * @param cleaner
 * @param selector
 * @param crosser
 * @param mutator
 * @param evaluator
 * @param mapTask
 * @param ex
 * @param sfpga
 * @param logger
 */
public class GenerationalGeneticAlgorithmElitistic extends FPGAGeneticAlgorithm{
	
	public GenerationalGeneticAlgorithmElitistic(String shortName, String name, int populationSize, int generations, double mutationRate,
			Initializer<AIFPGAConfiguration> initializer, AIFPGAConfigurationRandomizer randomizer,
			AIFPGAConfigurationCleaner cleaner, Selector selector, Crossover crosser, Mutation mutator,
			Evaluator evaluator, FPGAMapTask mapTask, FPGAModel ex, SimpleFPGA sfpga, LogWriter logger) {
		super(shortName, name, populationSize, generations, mutationRate, initializer, randomizer, cleaner, selector, crosser, mutator,
				evaluator, mapTask, ex, sfpga, logger);
	}
	
	@Override
	public void reproduction() {
		population = initializer.initialize();
		for (int i = 0; i < populationSize; i++) {
//			logger.log("Initialized individual: " + i + "\n");
			randomizer.randomize(population[i]);
			cleaner.clean(population[i]);
			FPGAModel model = FPGAEvaluator.EvaluatorArranger.prepareModelForEvaluation(ex, population[i], mapTask,
					sfpga);
			fitnesses[i] = evaluator.evaluate(population[i], model, mapTask);
			if(checkEvaluatorEnding(model)) return;
		}
		
		for(int i = 0; i < generations; i++) {
			AIFPGAConfiguration[] helper = initializer.initialize();
			double[] helperFitnesses = new double[populationSize];
			int j = 1; //counter for helper generation, put best individual in the generation
			int bestIndex = getBest();
			helper[0] = population[bestIndex]; //create some elitistic material
			helperFitnesses[0] = fitnesses[bestIndex];
			
			while(j < populationSize) {
				int i1 = randomizer.nextInt(populationSize), i2;
				while((i2 = randomizer.nextInt(populationSize)) == i1); //ne zelimo iste
				
				AIFPGAConfiguration conf1 = population[i1], conf2 = population[i2];
				AIFPGAConfiguration child = crosser.crossover(conf1, conf2);
				if(mutator != null) mutator.mutate(child);
				cleaner.clean(child);
				FPGAModel model = FPGAEvaluator.EvaluatorArranger.prepareModelForEvaluation(ex, child, mapTask,
						sfpga);
				helperFitnesses[j] = evaluator.evaluate(child, model, mapTask);
				helper[j] = child; // simplest version, we always put on the 3.place
				j++;
				if(checkEvaluatorEnding(model)) return;
			}
			population = helper;
			fitnesses = helperFitnesses;
			putAverageFitnessForGen(i+1); // we don't want to start from zero
			putBestFitnessForGen(i+1);
		}
		
	}
	
	
	private int getBest() {
		double max = fitnesses[0];
		int index = 0;
		for(int i = 1; i < populationSize; i++) {
			if(fitnesses[i] > max)  {
				max = fitnesses[i];
				index = i;
			}
		}
		return index;
	}

}
