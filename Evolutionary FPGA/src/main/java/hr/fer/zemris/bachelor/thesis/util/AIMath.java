package hr.fer.zemris.bachelor.thesis.util;

/**
 * Useful functions for observing parameters in evolutionary computing
 * @author andi
 *
 */
public class AIMath {
		
	
	public static double mean(double[] fitnesses) {
		double mean = 0.0;
		for(int i = 0; i < fitnesses.length; i++) {
			mean += fitnesses[i];
		}
		mean = mean / (double) fitnesses.length;
		return mean;
	}
	
	public static double stddev(double[] fitnesses) {
		double mean = AIMath.mean(fitnesses);
		
		double sqSum = 0.0;
		
		for(int i = 0; i < fitnesses.length; i++) {
			sqSum += (fitnesses[i] - mean) * (fitnesses[i] - mean);
		}
		
		sqSum = sqSum / (double) fitnesses.length;
		
		return Math.sqrt(sqSum);
	}
	
	public static double selectionDiff(double[] survived, double[] population) {
		double meanSurv = AIMath.mean(survived);	
		double meanPop = AIMath.mean(population);
		return meanSurv - meanPop;
	}
	
	/**
	 * If we assume fitness has normal distribution then we can say that intensity = selectionDiff / stddev;
	 * @return
	 */
	public static double selectionIntensity(double[] survived, double[] population) {
		double selectionDiff = AIMath.selectionDiff(survived, population);
		double stddev = AIMath.stddev(population);
		return selectionDiff / stddev;
	}
	
	/**
	 * Selection intensity for roulette wheel selection
	 * @param fitnesses
	 * @return
	 */
	public static double selIntRoulette(double[] fitnesses) {
		double stddev = AIMath.stddev(fitnesses);
		double mean = AIMath.mean(fitnesses);
		return stddev / mean;
	}
	
	/**
	 * Boltzmann selection, probability of selecting individual with fitness {@linkplain goalFitness} within populaton of fitnesses. B is Boltzmann param.
	 * @param goalFitness
	 * @param B
	 * @param fitnesses
	 * @return
	 */
	public static double getProbabilityOfSelectingIndividualBoltzmann(double goalFitness, double B, double[] fitnesses) {
		double param1 = Math.exp(B*goalFitness);
		double sum_2 = 0.0;
		for (int i = 0; i < fitnesses.length; i++) {
			sum_2 += Math.exp(B* fitnesses[i]);
		}
		return param1 / sum_2;
	}
	
	
	public static double selIntKTour(double k) {
		return Math.sqrt(Math.sqrt(2) * Math.log(k));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
