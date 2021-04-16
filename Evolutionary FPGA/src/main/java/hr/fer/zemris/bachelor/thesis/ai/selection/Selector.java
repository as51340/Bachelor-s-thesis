package hr.fer.zemris.bachelor.thesis.ai.selection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;

/**
 * Interface that specifies method for obtaining individual from population of {@linkplain AIFPGAConfiguration}. 
 * Different algorithms will differently implement this method. For example: k-tournament selection, Boltzmann
 * selection etc.
 * @author andi
 *
 */
public abstract class Selector {
	
	
	public List<Double> intensities;
	
	public Selector() {
		intensities = new ArrayList<>();
	}
	
	
	/**
	 * @param population
	 * @return individual index from population using some standard
	 */
	public abstract int select(double[] fitnesses);

}
