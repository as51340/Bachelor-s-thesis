package hr.fer.zemris.bachelor.thesis.ai;

import hr.fer.zemris.bachelor.thesis.ai.crossover.Crossover;
import hr.fer.zemris.bachelor.thesis.ai.initialization.Initializer;
import hr.fer.zemris.bachelor.thesis.ai.mutation.Mutation;
import hr.fer.zemris.bachelor.thesis.ai.selection.Selector;
import hr.fer.zemris.bachelor.thesis.evaluator.Evaluator;
import hr.fer.zemris.bachelor.thesis.evaluator.FPGAEvaluator;
import hr.fer.zemris.bachelor.thesis.evaluator.SimpleCLBInputsEvaluator;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationRandomizer;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.ConfUtil;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.AIFPGAConfigurationCleaner;
import hr.fer.zemris.bool.SimpleFPGA;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.LogWriter;
import hr.fer.zemris.fpga.mapping.FPGAMapTask;

/**
 * Simplest version of genetic algorithm. Three tournament eliminative genetic
 * algorithm. It isn't elitistic. We always take different individuals, so we
 * could allow randomness in evolution. TODO somehow test it.
 * 
 * @author andi
 *
 */
public class SimplestGeneticAlgorithm extends FPGAGeneticAlgorithm {

	public SimplestGeneticAlgorithm(int populationSize, int generations, double mutationRate,
			Initializer<AIFPGAConfiguration> initializer, AIFPGAConfigurationRandomizer randomizer,
			AIFPGAConfigurationCleaner cleaner, Selector selector, Crossover crosser, Mutation mutator,
			Evaluator evaluator, FPGAMapTask mapTask, FPGAModel ex, SimpleFPGA sfpga, LogWriter logger) {
		super(populationSize, generations, mutationRate, initializer, randomizer, cleaner, selector, crosser, mutator,
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
		for (int i = 0; i < generations; i++) {
//			logger.log("Generation no " + i + "\n");
			int i1, i2, i3, index;
			i1 = randomizer.nextInt(populationSize);
			while ((i2 = randomizer.nextInt(populationSize)) == i1);
			do {
				i3 = randomizer.nextInt(populationSize);
			} while (i3 == i1 || i3 == i2);
//			logger.log(i1 + " " + i2 + " " + i3 + "\n");
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
			mutator.mutate(newConf);
			cleaner.clean(newConf);
			FPGAModel model = FPGAEvaluator.EvaluatorArranger.prepareModelForEvaluation(ex, newConf, mapTask,
					sfpga);
//			bestOverall = model;
			fitnesses[index] = evaluator.evaluate(newConf, model, mapTask);
			if(checkEvaluatorEnding(model)) return;			
			population[index] = newConf; // simplest version, we always put on the 3.place
			putAverageFitnessForGen(i+1); // we don't want to start from zero
			putBestFitnessForGen(i+1);
		}
	}

}
