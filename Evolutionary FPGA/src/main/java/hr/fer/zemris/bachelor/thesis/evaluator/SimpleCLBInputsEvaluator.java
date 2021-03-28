package hr.fer.zemris.bachelor.thesis.evaluator;

import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.FPGAModel.CLBBox;
import hr.fer.zemris.fpga.FPGAModel.Pin;
import hr.fer.zemris.fpga.mapping.FPGAMapTask;
import hr.fer.zemris.fpga.mapping.FPGAMapTask.CLB;

/**
 * Only evaluates if clb inputs are correct or not. We'll add later some more.
 * -20 if completely wrong object is connected, -10 is correct object but wrong
 * value. +10 for correct value. TODO, almost working, not totally. information
 * about distances etc.
 * 
 * @author andi
 *
 */
public class SimpleCLBInputsEvaluator implements Evaluator {
	
	public boolean valid = true;


	@Override
	public double evaluate(AIFPGAConfiguration conf, FPGAModel model, FPGAMapTask mapTask) {
		double sol = 0;
		CLBBox[] clbs = model.clbs;
		CLB[] mapTaskClbs = mapTask.clbs;
		for (int i = 0; i < conf.clbIndexes.length; i++) {
			CLBBox clbModel = clbs[conf.clbIndexes[i]]; // consider model
			CLB clbTask = mapTaskClbs[i];
//			System.out.println("CLB model: " + clbModel.title + " clbTask: " + clbTask.name); // these should be the
																								// same
			String[] inputsTask = clbTask.inputs;
			for (int j = 0; j < clbTask.inputs.length; j++) {
				if (clbModel.wiresIn[clbModel.inConnectionIndexes[j]].label == null) {
//					System.out.println("Null + input " + j);
				} else {
//					System.out.println(clbModel.wiresIn[clbModel.inConnectionIndexes[j]].label.getClass());
				}
				if (inputsTask[j].startsWith("CLB")) {
					if (!(clbModel.wiresIn[clbModel.inConnectionIndexes[j]].label instanceof CLBBox)) {
						sol -= 20;
						valid = false;
					} else { // it really is clb
						CLBBox label = (CLBBox) clbModel.wiresIn[clbModel.inConnectionIndexes[j]].label;
						if (label.title != null && !label.title.equals(inputsTask[j])) { // it is is clb but it's not correct clb
							sol -= 10;
							valid = false;
						} else {
							sol += 10;
						}
					}
				} else { // pin must be on input
					if (!(clbModel.wiresIn[clbModel.inConnectionIndexes[j]].label instanceof Pin)) { // it's not pin
						sol -= 20;
						valid = false;
					} else {
						Pin label = (Pin) clbModel.wiresIn[clbModel.inConnectionIndexes[j]].label;
						if (!label.title.equals(inputsTask[j])) { // it is pin but wrong
							sol -= 10;
							valid = false;
						} else {
							sol += 10;
						}
					}
				}
			}
		}
		return sol;
	}

}
