package hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.swboxes;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import hr.fer.zemris.bachelor.thesis.ai.initialization.AIFPGAConfigurationInitializer;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationRandomizer;
import hr.fer.zemris.bachelor.thesis.util.ArrayUtils;
import hr.fer.zemris.fpga.FPGAModel;

/**
 * Test for advanced switch box cleaner
 * @author andi
 *
 */
class AdvancedSwitchBoxCleanerTest {

	@Test
	void test1() {
		int rows = 2, columns = 2, pins = 1, variables = 2, wires = 3, pop_size = 5;
		FPGAModel model = new FPGAModel(rows, columns, variables, wires, pins);
		AIFPGAConfiguration[] population = new AIFPGAConfigurationInitializer(pop_size, model).initialize();
		AIFPGAConfigurationRandomizer random = new AIFPGAConfigurationRandomizer(model);
		SwitchBoxCleaner swCleaner = new AdvancedSwitchBoxCleaner(model);
		for (int i = 0; i < pop_size; i++) {
			AIFPGAConfiguration individual = population[i];
			random.randomize(individual);
			byte[][][] swConf = individual.configuration.switchBoxes;
			for (int j = 0; j < swConf.length; j++) { // iterate through all switch boxes
				byte[][] conf = swConf[j];
				byte[][] copy = ArrayUtils.copyArray(conf);
				swCleaner.clean(copy);
				ArrayUtils.printTwoArrays(conf, copy);
				for(int k = 0; k < copy.length; k++) {
					boolean isStarting = false, isEnding = false;
					for(int y = 0; y < copy[0].length; y++) {
						if(copy[k][y] == 2) { //ako smo našli dvojku, nismo smjeli nać jedinicu
							assertFalse(isEnding);
							assertEquals(copy[y][k], 1);
						} else if(copy[k][y] == 1) {
							assertFalse(isEnding || isStarting);
							assertEquals(copy[y][k], 2);
						}
					}
				}
				System.out.println();
				System.out.println();
			}
		}
	}

}
