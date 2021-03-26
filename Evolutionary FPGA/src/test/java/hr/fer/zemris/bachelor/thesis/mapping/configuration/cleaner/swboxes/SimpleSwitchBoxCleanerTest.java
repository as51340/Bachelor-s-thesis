package hr.fer.zemris.bachelor.thesis.mapping.configuration.cleaner.swboxes;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationInitializer;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationRandomizer;
import hr.fer.zemris.fpga.FPGAModel;

/**
 * Tests simple switch box cleaner.
 * 
 * @author andi
 *
 */
class SimpleSwitchBoxCleanerTest {

	@Test
	void test1() {
		int rows = 2, columns = 2, pins = 1, variables = 2, wires = 3;
		FPGAModel model = new FPGAModel(rows, columns, variables, wires, pins);
		AIFPGAConfiguration[] population = new AIFPGAConfigurationInitializer(50, model).initialize();
		AIFPGAConfigurationRandomizer random = new AIFPGAConfigurationRandomizer(model);
		SwitchBoxCleaner swCleaner = new SimpleSwitchBoxCleaner(model);
		for (int i = 0; i < 50; i++) {
			AIFPGAConfiguration individual = population[i];
			random.randomize(individual);
			byte[][][] swConf = individual.configuration.switchBoxes;
			for (int j = 0; j < swConf.length; j++) { // iterate through all switch boxes
				byte[][] conf = swConf[j];
				byte[][] copy = copyArray(conf);
				swCleaner.clean(copy);
				//printTwoArrays(conf, copy);
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
//				System.out.println();
//				System.out.println();
			}
		}
	}
	
	private void printTwoArrays(byte[][] old, byte[][] newArr) {
		for(int i = 0; i < old.length; i++) {
			for(int j = 0; j < old[0].length; j++) {
				System.out.print(old[i][j] + " ");
			}
			System.out.print("     ");
			for(int j = 0; j < newArr[0].length; j++) {
				System.out.print(newArr[i][j] + " ");
			}
			System.out.println();
		}
	}

	private byte[][] copyArray(byte[][] old) {
		byte[][] newArr = new byte[old.length][old[0].length];
		for (int i = 0; i < old.length; i++) {
			for (int j = 0; j < old[0].length; j++) {
				newArr[i][j] = old[i][j];
			}
		}
		return newArr;
	}

	private void printSwitchBox(byte[][] sw) {
		for (int i = 0; i < sw.length; i++) {
			for (int j = 0; j < sw[0].length; j++) {
				System.out.print(sw[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

}
