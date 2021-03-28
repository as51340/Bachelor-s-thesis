package hr.fer.zemris.bachelor.thesis.ai.crossover;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import hr.fer.zemris.bachelor.thesis.ai.initialization.AIFPGAConfigurationInitializer;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationRandomizer;
import hr.fer.zemris.fpga.FPGAModel;

/**
 * Tests some simple properties of crossovering
 * @author andi
 *
 */
class SimpleCrossoverTest {

	@Test
	void test1() {
		int rows = 2, columns = 2, pins = 1, variables = 2, wires = 3, popSize = 50;
		FPGAModel model = new FPGAModel(rows, columns, variables, wires, pins);
		
		AIFPGAConfigurationInitializer initer = new AIFPGAConfigurationInitializer(popSize, model);
		AIFPGAConfigurationRandomizer random = new AIFPGAConfigurationRandomizer(model);
		Crossover crosser = new SimpleCrossover(random, model);
		AIFPGAConfiguration[] population = initer.initialize();
		for(int i = 0; i < popSize; i++) {
			random.randomize(population[i]);
		}
		//for example just test two of them
		for(int i = 0; i < 50; i++) {
			int rnd = random.nextInt(popSize);
			AIFPGAConfiguration child = crosser.crossover(population[rnd], population[rnd/2]);
			for(int j = 0; j < child.clbIndexes.length; j++) {
//				System.out.println(population[rnd].clbIndexes[j] + " " + population[rnd/2].clbIndexes[j] + " " + 
//			child.clbIndexes[j]);
				assertTrue(child.clbIndexes[j] != -1 && child.clbIndexes[j] < child.clbIndexes.length);
				assertTrue(child.clbIndexes[j] == population[rnd].clbIndexes[j] || 
						child.clbIndexes[j] == population[rnd/2].clbIndexes[j]);
			}
			for(int j = 0; j < child.pinIndexes.length; j++) {
				assertTrue(child.pinIndexes[j] != -1 && child.pinIndexes[j] < child.pinIndexes.length);
				assertTrue(child.pinIndexes[j] == population[rnd].pinIndexes[j] || 
						child.pinIndexes[j] == population[rnd/2].pinIndexes[j]);
			}
			for(int j = 0; j < child.configuration.pinIndexes.length; j++) {
				assertTrue(child.configuration.pinIndexes[j] != -1 && child.configuration.pinIndexes[j] < 
						model.wiresCount); //because of connection indexes
				assertTrue(child.configuration.pinIndexes[j] == population[rnd].configuration.pinIndexes[j] || 
						child.configuration.pinIndexes[j] == population[rnd/2].configuration.pinIndexes[j]);
			}
			for(int j = 0; j < child.configuration.clbOutIndexes.length; j++) {
				assertTrue(child.configuration.clbOutIndexes[j] != -1 && child.configuration.clbOutIndexes[j] < 
						model.wiresCount); //because of connection indexes
				assertTrue(child.configuration.clbOutIndexes[j] == population[rnd].configuration.clbOutIndexes[j] || 
						child.configuration.clbOutIndexes[j] == population[rnd/2].configuration.clbOutIndexes[j]);
			}
			for(int k = 0; k < child.configuration.clbInIndexes.length; k++) {
				for(int j = 0; j < child.configuration.clbInIndexes[0].length; j++) {
					assertTrue(child.configuration.clbInIndexes[k][j] != -1 && 
							child.configuration.clbInIndexes[k][j] <  model.wiresCount); //because of connection indexes
					assertTrue(child.configuration.clbInIndexes[k][j] == population[rnd].configuration.clbInIndexes[k][j] || 
							child.configuration.clbInIndexes[k][j] == population[rnd/2].configuration.clbInIndexes[k][j]);
				}
			}
			for(int k = 0; k < child.configuration.switchBoxes.length; k++) {
				for(int j = 0; j < child.configuration.switchBoxes[0].length; j++) {
					for(int y = 0; y < child.configuration.switchBoxes[0][0].length; y++) {
						assertTrue(child.configuration.switchBoxes[k][j][y] != -1 && 
								child.configuration.switchBoxes[k][j][y] < 3);
						assertTrue(child.configuration.switchBoxes[k][j][y] == population[rnd].configuration.switchBoxes[k][j][y] || 
								child.configuration.switchBoxes[k][j][y] == population[rnd/2].configuration.switchBoxes[k][j][y]);
					}
				}
			}
		}
		
	}

}
