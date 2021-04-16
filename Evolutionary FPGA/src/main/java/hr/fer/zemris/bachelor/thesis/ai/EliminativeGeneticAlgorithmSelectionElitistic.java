package hr.fer.zemris.bachelor.thesis.ai;

import hr.fer.zemris.bachelor.thesis.ai.crossover.Crossover;
import hr.fer.zemris.bachelor.thesis.ai.initialization.Initializer;
import hr.fer.zemris.bachelor.thesis.ai.mutation.Mutation;
import hr.fer.zemris.bachelor.thesis.ai.selection.Selector;
import hr.fer.zemris.bachelor.thesis.evaluator.Evaluator;
import hr.fer.zemris.bachelor.thesis.evaluator.FPGAEvaluator;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationRandomizer;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.ConfUtil;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.AIFPGAConfigurationCleaner;
import hr.fer.zemris.bool.SimpleFPGA;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.LogWriter;
import hr.fer.zemris.fpga.mapping.FPGAMapTask;

/**
 * Puts new configuration on the place of the worst in current population
 * @author andi
 *
 */
public class EliminativeGeneticAlgorithmSelectionElitistic extends FPGAGeneticAlgorithm{

	public EliminativeGeneticAlgorithmSelectionElitistic(boolean type, String shortName, String name, int populationSize, int generations,
			double mutationRate, Initializer<AIFPGAConfiguration> initializer, AIFPGAConfigurationRandomizer randomizer,
			AIFPGAConfigurationCleaner cleaner, Selector selector, Crossover crosser, Mutation mutator,
			Evaluator evaluator, FPGAMapTask mapTask, FPGAModel ex, SimpleFPGA sfpga, LogWriter logger) {
		super(type, shortName, name, populationSize, generations, mutationRate, initializer, randomizer, cleaner, selector, crosser,
				mutator, evaluator, mapTask, ex, sfpga, logger);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void reproduction() {
		population = initializer.initialize();
		
		int min_index = -1;
		double min_value = Double.MAX_VALUE;
		int max_index = -1;
		double max_value = Double.MIN_VALUE;
		
		for (int i = 0; i < populationSize; i++) {
			randomizer.randomize(population[i]);
			cleaner.clean(population[i]);
			FPGAModel model = FPGAEvaluator.EvaluatorArranger.prepareModelForEvaluation(ex, population[i], mapTask,
					sfpga);
			fitnesses[i] = evaluator.evaluate(population[i], model, mapTask);
			if (fitnesses[i] < min_value) {
				min_value = fitnesses[i];
				min_index = i;
			}
			if(fitnesses[i] > max_value) {
				max_value = fitnesses[i];
				max_index = i;
//				bestOverall = model;
			}
			
			
			if(checkEvaluatorEnding(model)) return;
		}
		int i1, i2;
		for (int i = 0; i < generations; i++) {
			AIFPGAConfiguration conf1, conf2, newConf;
			i1 = selector.select(fitnesses);
			i2 = selector.select(fitnesses);
			
			conf1 = population[i1];
			conf2 = population[i2];
			
			newConf = crosser.crossover(conf1, conf2);
			
			if(mutator != null) mutator.mutate(newConf);
			
			cleaner.clean(newConf);
			FPGAModel model = FPGAEvaluator.EvaluatorArranger.prepareModelForEvaluation(ex, newConf, mapTask,
					sfpga);
			
			
			double value = evaluator.evaluate(newConf, model, mapTask);
			if(checkEvaluatorEnding(model)) return;
			if(value > min_value) {
				population[min_index] = newConf;
				fitnesses[min_index] = value; 
			}
			if(value > max_value) {
				max_index = min_index; // this is just for now
				max_value = value;
//				bestOverall = model;
			}
			
			min_index = getWorstFromPopulation(); // recalculate
			
			
			putAverageFitnessForGen(i+1); // we don't want to start from zero
			putBestFitnessForGen(i+1);
		}
	}
	
	
	private int getWorstFromPopulation() {
		int min_index = -1;
		double min_value = Double.MAX_VALUE;
		for(int i = 0; i < population.length; i++) {
			if (fitnesses[i] < min_value) {
				min_index = i;
				min_value = fitnesses[i];
			}
		}
		return min_index;
	}
	

}
