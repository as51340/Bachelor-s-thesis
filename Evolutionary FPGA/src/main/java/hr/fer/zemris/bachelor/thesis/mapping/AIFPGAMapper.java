package hr.fer.zemris.bachelor.thesis.mapping;

import hr.fer.zemris.bachelor.thesis.ai.FPGAGeneticAlgorithm;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.LogWriter;
import hr.fer.zemris.fpga.mapping.FPGAMapTask;

/**
 * Maps logic model into real physical model. LogWriter is used for writing to user defined output. 
 * @author andi
 *
 */
public class AIFPGAMapper {

	//Not implemented yet
	//protected GUIEvolutionVisualizer visualizer;
	
	public FPGAModel model;

	public FPGAMapTask task;
	
	public LogWriter logger;
	
	public FPGAGeneticAlgorithm alg;

	public AIFPGAMapper(FPGAModel model, FPGAMapTask task, LogWriter logger, FPGAGeneticAlgorithm alg) {
		super();
		this.model = model;
		this.task = task;
		this.logger = logger;
		this.alg = alg;
	}
	
}

