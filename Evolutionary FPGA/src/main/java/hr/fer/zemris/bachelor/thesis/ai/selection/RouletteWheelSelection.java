package hr.fer.zemris.bachelor.thesis.ai.selection;

import java.util.Arrays;

import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfigurationRandomizer;
import hr.fer.zemris.bachelor.thesis.util.AIMath;

/**
 * Selector that choose individuals by taking into account their fitness functions. flag isn't important in this example. Transforms to nonnegative values.
 * @author andi
 *
 */
public class RouletteWheelSelection extends Selector{

	private AIFPGAConfigurationRandomizer random;
	
	public RouletteWheelSelection(AIFPGAConfigurationRandomizer random) {
		super();
		this.random = random;
	}


	//return -1 if nothing was found but this cannot be possible?
	@Override
	public int select(double[] fitnesses) {
		double[] wrapper = wrap(fitnesses);
		double sum = Arrays.stream(wrapper).sum();
		
		//intensity work
		this.intensities.add(AIMath.selIntRoulette(fitnesses));
		

		double area = 0;
		
		double select = random.nextDouble() * sum;  //get random value between zero and sum 
		
		for(int i = 0; i < wrapper.length; i++) {
			area += wrapper[i];
			if(area > select  - 1e-5) return i;
		}
		throw new IllegalArgumentException("Could not find solution in SimpleRouletteWheelSelection class");
	}
	
	
	private double[] wrap(double[] fitnesses) {
		double negative = Double.MAX_VALUE; //always use proportional scale
		double[] wrapper = new double[fitnesses.length];
		
		for(int i = 0; i < fitnesses.length; i++) {
			if(fitnesses[i] < negative) {
				negative = fitnesses[i];
			}
		}
		
		for(int i = 0; i < fitnesses.length; i++) {
			wrapper[i] = fitnesses[i] - negative; //transform comparing to smallest.
		}
		return wrapper;
	}

}
