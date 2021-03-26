package hr.fer.zemris.bachelor.thesis.mapping.configuration;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;

import hr.fer.zemris.bachelor.thesis.ai.initialization.AIFPGAConfigurationInitializer;
import hr.fer.zemris.bachelor.thesis.util.ArrayUtils;
import hr.fer.zemris.fpga.FPGAModel;

/**
 * Requirements are: bytes in clb indexes should be from 0 to clbIndexes.length,
 * same for pinIndexes. For modelConfiguration: switchBoxex, all values should
 * be 0,1,2. ClbInIndexes and clbOutIndexes from 0 to wiresCount, and for pin
 * indexes also.
 * 
 * @author andi
 *
 */
class AIFPGAConfigurationRandomizerTest {

	@Test
	void numberOfWiresIsThree() {
		// three wires and two input variables
		int rows = 2, columns = 2, pins = 1, variables = 2, wires = 3;
		FPGAModel model = new FPGAModel(rows, columns, variables, wires, pins);
		AIFPGAConfiguration[] population = new AIFPGAConfigurationInitializer(50, model).initialize();
		AIFPGAConfigurationRandomizer random = new AIFPGAConfigurationRandomizer(model);
		for (int i = 0; i < population.length; i++) {
			AIFPGAConfiguration individual = population[i];
			assertEquals(rows * columns, individual.clbIndexes.length);
			assertEquals(2 * pins * (rows + columns), individual.pinIndexes.length);
			random.randomize(individual);
			// assert clb indexes
			// assert different
			for (int j = 0; j < individual.clbIndexes.length; j++) {
				// System.out.println(individual.clbIndexes[j] + " " +
				// individual.clbIndexes.length);
				assertTrue((individual.clbIndexes[j] < individual.clbIndexes.length) && individual.clbIndexes[j] != -1);
			}
			assertTrue(ArrayUtils.validate(Arrays.stream(individual.clbIndexes).boxed().toArray(Integer[]::new)));
			// assert pin indexes
			// assert different
			for (int j = 0; j < individual.pinIndexes.length; j++) {
				assertTrue((individual.pinIndexes[j] < individual.pinIndexes.length) && individual.pinIndexes[j] != -1);
			}
			assertTrue(ArrayUtils.validate(Arrays.stream(individual.pinIndexes).boxed().toArray(Integer[]::new)));
			// assert pin connection indexe
			for (int j = 0; j < individual.configuration.pinIndexes.length; j++) {
				assertTrue(
						(individual.configuration.pinIndexes[j] < 3) && individual.configuration.pinIndexes[j] != -1);
			}
			// assert clboutput indexe
			for (int j = 0; j < individual.configuration.clbOutIndexes.length; j++) {
				assertTrue((individual.configuration.clbOutIndexes[j] < 3)
						&& individual.configuration.clbOutIndexes[j] != -1);
			}
			// assert inpute
			// assert that they are different
			for (int j = 0; j < individual.configuration.clbInIndexes.length; j++) {
				assertTrue(ArrayUtils.validateByte(individual.configuration.clbInIndexes[j]));
				for (int k = 0; k < variables; k++) {
					assertTrue((individual.configuration.clbInIndexes[j][k] < 3)
							&& individual.configuration.clbInIndexes[j][k] != -1);
				}
			}
			// assert switchboxes
			for (int j = 0; j < individual.configuration.switchBoxes.length; j++) {
				for (int k = 0; k < individual.configuration.switchBoxes[0].length; k++) {
					for (int y = 0; y < individual.configuration.switchBoxes[0][0].length; y++) {
						assertTrue((individual.configuration.switchBoxes[j][k][y] < 3)
								&& individual.configuration.switchBoxes[j][k][y] != -1);
					}
				}
			}
		}
	}

}
