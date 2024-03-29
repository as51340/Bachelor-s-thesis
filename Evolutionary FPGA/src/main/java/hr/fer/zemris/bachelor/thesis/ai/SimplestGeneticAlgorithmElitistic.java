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

public class SimplestGeneticAlgorithmElitistic extends FPGAGeneticAlgorithm {
	
	@Override
	public void reset() {
		super.reset();
		best = -Double.MAX_VALUE;
	}

	private double best = -Double.MAX_VALUE;
	
	
	public SimplestGeneticAlgorithmElitistic(boolean type, String shortName, String name, int populationSize, int generations, double mutationRate,
			Initializer<AIFPGAConfiguration> initializer, AIFPGAConfigurationRandomizer randomizer,
			AIFPGAConfigurationCleaner cleaner, Selector selector, Crossover crosser, Mutation mutator,
			Evaluator evaluator, FPGAMapTask mapTask, FPGAModel ex, SimpleFPGA sfpga, LogWriter logger) {
		super(type, shortName, name, populationSize, generations, mutationRate, initializer, randomizer, cleaner, selector, crosser, mutator,
				evaluator, mapTask, ex, sfpga, logger);
	}

	@Override
	public void reproduction() {
		population = initializer.initialize();
		
		for (int i = 0; i < populationSize; i++) {
			randomizer.randomize(population[i]);
			cleaner.clean(population[i]);
			FPGAModel model = FPGAEvaluator.EvaluatorArranger.prepareModelForEvaluation(ex, population[i], mapTask,
					sfpga);
			fitnesses[i] = evaluator.evaluate(population[i], model, mapTask);
			if(checkEvaluatorEnding(model)) return;
		}
		
		for (int i = 0; i < generations; i++) {
			int i1, i2, i3, index;
			
			i1 = randomizer.nextInt(populationSize);
			while ((i2 = randomizer.nextInt(populationSize)) == i1);
			do {
				i3 = randomizer.nextInt(populationSize);
			} while (i3 == i1 || i3 == i2);
			
			index = ConfUtil.getWorstFromThree(fitnesses, i1, i2, i3);
			AIFPGAConfiguration conf1, conf2, newConf;
			if (index == i1) {
				conf1 = population[i2];
				conf2 = population[i3];
			} else if (index == i2) {
				conf1 = population[i1];
				conf2 = population[i3];
			} else {
				conf1 = population[i1];
				conf2 = population[i2];
			}
			newConf = crosser.crossover(conf1, conf2);
			if(mutator != null) mutator.mutate(newConf);
			cleaner.clean(newConf);
			FPGAModel model = FPGAEvaluator.EvaluatorArranger.prepareModelForEvaluation(ex, newConf, mapTask,
					sfpga);
			
			double value = evaluator.evaluate(newConf, model, mapTask);
			if(checkEvaluatorEnding(model)) {
				finalGen = i+1;
				return;
			}
			if(value > fitnesses[index]) {
				population[index] = newConf;
				fitnesses[index] = value; //
			}
			if(value > best) {
				bestOverall = model;
				best = value;
			}
			putAverageFitnessForGen(i+1); // we don't want to start from zero
			putBestFitnessForGen(i+1);
		}
		solFounded = false;
		System.out.println("Best fitness is " + best);
	}
	
}
