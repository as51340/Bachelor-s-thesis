package hr.fer.zemris.bachelor.thesis.ai;

import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.ConfUtil;

/**
 * Simplest version of genetic algorithm. Three tournament eliminative genetic algorithm. It isn't elitistic. We always take different individuals, 
 * so we could allow randomness in evolution. TODO somehow test it.
 * @author andi
 *
 */
public class SimplestGeneticAlgorithm extends FPGAGeneticAlgorithm{


	@Override
	public void reproduction() {
		population = initializer.initialize();
		for(int i = 0; i < populationSize; i++) {
			randomizer.randomize(population[i]);
			cleaner.clean(population[i]);
//			fitnesses[i] = evaluator.evaluate(population[i]);
		}
		for(int i = 0; i < generations; i++) {
			int i1, i2, i3, index;
			i1 = randomizer.nextInt(populationSize);
			while((i2 = randomizer.nextInt(populationSize)) == i1);
			do {
				i3 = randomizer.nextInt(populationSize);
			} while(i3 == i1 || i3 == i2);
			index = ConfUtil.getWorstFromThree(fitnesses, i1, i2, i3);
			AIFPGAConfiguration conf1, conf2, newConf;	
			if(index == i1) {
				conf1 = population[i2];
				conf2 = population[i3];
			} else if(index == i2) {
				conf1 = population[i1];
				conf2 = population[i3];
			} else {
				conf1 = population[i1];
				conf2 = population[i2];
			}
			newConf = crosser.crossover(conf1, conf2);
			mutator.mutate(newConf);
			population[index] = newConf; //simplest version, we always put on the 3.place
//			fitnesses[index] = evaluator.evaluate(model);
		}
	}

}
