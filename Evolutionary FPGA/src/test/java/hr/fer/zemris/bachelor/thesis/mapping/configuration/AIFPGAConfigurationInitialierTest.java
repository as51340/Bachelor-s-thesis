package hr.fer.zemris.bachelor.thesis.mapping.configuration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import hr.fer.zemris.bachelor.thesis.ai.initialization.AIFPGAConfigurationInitializer;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.fpga.FPGAModel;

/**
 * Just to check initialization
 * @author andi
 *
 */
class AIFPGAConfigurationInitialierTest {

	@Test
	void test1() {
		int rows =2, columns = 2, pins = 1, variables =2, wires =3;
		AIFPGAConfigurationInitializer init = new AIFPGAConfigurationInitializer(50,
				new FPGAModel(rows, columns, variables, wires, pins));
		AIFPGAConfiguration[] population = init.initialize();
		assertEquals(population.length, 50);
		for(int i = 0; i < population.length; i++) {
			assertEquals(rows*columns, population[i].clbIndexes.length);
			for(int j = 0; j < population[i].clbIndexes.length; j++) {
				assertEquals(population[i].clbIndexes[j], -1);
			}
			assertEquals(2*pins*(rows + columns), population[i].pinIndexes.length);
			for(int j = 0; j < population[i].pinIndexes.length; j++) {
				assertEquals(population[i].pinIndexes[j], -1);
			}
		}
	}

}