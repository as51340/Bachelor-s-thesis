package hr.fer.zemris.bachelor.thesis.evaluator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.mapping.FPGAMapTask;

/**
 * We need very precise method for managing {@linkplain SwitchBox}. Remove
 * penalty linear model.
 * 
 * @author andi
 *
 */
public class SwitchBoxEvalutor implements Evaluator {

	@Override
	public double evaluate(AIFPGAConfiguration conf, FPGAModel model, FPGAMapTask mapTask) {
		byte[][][] swBoxes = conf.configuration.switchBoxes;
		int maxConnections = Coefficients.getMaxConnections(model.clbs[0].inConnectionIndexes.length); //maximum where clb is in the middle or something similar

		double sol = 0.0;
		for (int i = 0; i < swBoxes.length; i++) {

			int row = i / (model.columns + 1);
			int col = i % (model.columns + 1);
			int connections = 0;
			
			List<List<Integer>> groups = new ArrayList<>();
			for(int z = 0; z < 4; z++) groups.add(new ArrayList<>());
			

			for (int j = 0; j < swBoxes[0].length; j++) {
				for (int k = 0; k < swBoxes[0][0].length; k++) {
					if (j < k) // eliminate upper half
						continue;
					
					if (swBoxes[i][j][k] == 1 && swBoxes[i][k][j] == 2) {
						connections++;
						int group = j / model.wiresCount;
						groups.get(group).add(k);
					} else if(swBoxes[i][j][k] == 2 && swBoxes[i][k][j] == 1) {
						connections++;
						int group = k / model.wiresCount;
						groups.get(group).add(j);
					}
				}
			}
			
			for(int z = 0; z < groups.size(); z++) {
				List<Integer> group = groups.get(z);
				Set<Integer> seen = new HashSet<>();
				for(int l = 0; l < group.size(); l++) {
					if(!seen.add(group.get(l))) sol += Coefficients.MULTIPLE_VALUES; //same punisghment as it is for connection quality evaluator
				}
			}
				
			int localMaxConnections = maxConnections;
			if(row == 0 || row == model.rows || col == 0 || col == model.columns) localMaxConnections = (localMaxConnections + 1) / 2;; //we are somewhere in the corner
			
			if(connections > localMaxConnections) {
				sol += (connections - localMaxConnections) * Coefficients.SW_BOX_MAX_OVERFLOW_PENALTY; //add overflow error
			} else if(connections == 0) {
				sol += Coefficients.SW_EMPTY; 
			}
		}

		return sol;
	}

}
