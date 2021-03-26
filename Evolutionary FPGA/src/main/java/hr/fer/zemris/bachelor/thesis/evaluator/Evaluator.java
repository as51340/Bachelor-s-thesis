package hr.fer.zemris.bachelor.thesis.evaluator;

import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.mapping.FPGAMapTask;

/**
 * All objects that will implement this interface must create evaluate method. We need physical model and
 * mapping task.
 * @author andi
 *
 */
public interface Evaluator {

	double evaluate(FPGAModel model, FPGAMapTask mapTask);
	
}
