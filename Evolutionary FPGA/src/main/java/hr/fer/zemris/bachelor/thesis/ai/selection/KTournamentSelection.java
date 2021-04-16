package hr.fer.zemris.bachelor.thesis.ai.selection;

import java.util.HashSet;
import java.util.Set;

import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationRandomizer;
import hr.fer.zemris.bachelor.thesis.util.AIMath;

/**
 * Regular k tournament selection. Selects best from k individuals and returns. If best is set to false then we are selection worst from k in popoulation
 * selected individual.
 * 
 * @author andi
 *
 */
public class KTournamentSelection extends Selector {

	private int k;

	private AIFPGAConfigurationRandomizer random;
	
	private boolean best = true;

	public KTournamentSelection(int k, AIFPGAConfigurationRandomizer random) {
		super();
		this.k = k;
		this.random = random;
	}

	@Override
	public int select(double[] fitnesses) {//if flag == false we search for the worst, if fla == best we search for the best from k {
		Set<Integer> selected = new HashSet<>();
		
		this.intensities.add(AIMath.selIntKTour(this.k));
		
		double[] helper = new double[k];
		int rnd = 0;
		for (int j = 0; j < k; j++) {
			while (true) {
				rnd = random.nextInt(fitnesses.length);
				if(!selected.contains(rnd)) break;
			}
			helper[j] = fitnesses[rnd];
			selected.add(rnd);
		}
		
		if(!best) return getMin(helper);
		return getMax(helper);

	}
	
	private int getMin(double[] helper) {
		int min = 0;
		double currMin = helper[0];
		for (int i = 1; i < k; i++) {
			if (helper[i] < currMin) {
				currMin = helper[i];
				min = i;
			}
		}
		return min;
	}
	
	public void setWorst(boolean best) {
		this.best = best;
	}
	
	
	private int getMax(double[] helper) {
		int max = 0;
		double currMax = helper[0];
		for (int i = 1; i < k; i++) {
			if (helper[i] > currMax) {
				currMax = helper[i];
				max = i;
			}
		}
		return max;
	}
	
	

}
