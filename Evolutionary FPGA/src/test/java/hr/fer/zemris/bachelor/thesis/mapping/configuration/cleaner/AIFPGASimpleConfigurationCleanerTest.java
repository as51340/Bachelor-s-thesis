package hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationInitializer;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationRandomizer;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.swboxes.SimpleSwitchBoxCleaner;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.swboxes.SwitchBoxCleaner;
import hr.fer.zemris.bachelor.thesis.util.ArrayUtils;
import hr.fer.zemris.fpga.FPGAModel;

class AIFPGASimpleConfigurationCleanerTest {

	@Test
	void test1() {
		int rows = 2, columns = 2, pins = 1, variables = 2, wires = 3;
		FPGAModel model = new FPGAModel(rows, columns, variables, wires, pins);
		AIFPGAConfiguration[] population = new AIFPGAConfigurationInitializer(50, model).initialize();
		AIFPGAConfigurationRandomizer random = new AIFPGAConfigurationRandomizer(model);
		SwitchBoxCleaner swCleaner = new SimpleSwitchBoxCleaner(model);
		AIFPGAConfigurationCleaner cleaner = new AIFPGASimpleConfigurationCleaner(model, swCleaner);
		for (int i = 0; i < 50; i++) {
			AIFPGAConfiguration individual = population[i];
			random.randomize(individual);
			cleaner.clean(individual);
			byte[][][] swConf = individual.configuration.switchBoxes;
			for (int j = 0; j < swConf.length; j++) { // iterate through all switch boxes
				byte[][] conf = swConf[j];
				swCleaner.clean(conf);
				// printTwoArrays(conf, copy);
				for (int k = 0; k < conf.length; k++) {
					boolean isStarting = false, isEnding = false;
					for (int y = 0; y < conf[0].length; y++) {
						if (conf[k][y] == 2) { // ako smo našli dvojku, nismo smjeli nać jedinicu
							assertFalse(isEnding);
							assertEquals(conf[y][k], 1);
						} else if (conf[k][y] == 1) {
							assertFalse(isEnding || isStarting);
							assertEquals(conf[y][k], 2);
						}
					}
				}
				ArrayUtils.printSwitchBox(individual.configuration.switchBoxes[j]);
				System.out.println();
				System.out.println();
			}
		}
	}

}
