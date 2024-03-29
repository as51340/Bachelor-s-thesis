package hr.fer.zemris.bachelor.thesis.evaluator;

import java.util.HashSet;
import java.util.Set;

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
	
	public static int inputBlackLabel = 0, inputWrongType = 0, inputWrongName = 0;
	
	public boolean valid = true;
	
	@Override
	public double evaluate(AIFPGAConfiguration conf, FPGAModel model, FPGAMapTask mapTask) {
		double sol = 0;
		
		CLBBox[] clbs = model.clbs;
		CLB[] mapTaskClbs = mapTask.clbs;		
		
		for (int i = 0; i < conf.clbIndexes.length && i < mapTaskClbs.length; i++) {
			CLBBox clbModel = clbs[conf.clbIndexes[i]]; // consider model
			CLB clbTask = mapTaskClbs[i];
			String[] inputsTask = clbTask.inputs;
			
			Set<String> used = new HashSet<>();
			
			
			for (int j = 0; j < clbTask.inputs.length; j++) {
				if(clbModel.inConnectionIndexes[j] == -1) { //ako je connection index =-1
					throw new IllegalStateException("CLB: " + clbModel.title + " input connection index: " + j + " is -1");
				}
				if(clbModel.outConnectionIndex  == -1) {
					throw new IllegalStateException("CLB: " + clbModel.title + " output connection index is -1");
				}
				if (clbModel.wiresIn[clbModel.inConnectionIndexes[j]].label == null) { //completely blackness :-/
					inputBlackLabel++;
					sol += Coefficients.BLACK_LABEL; 
					valid = false;
					continue;
				}
				if (inputsTask[j].startsWith("CLB(")) { //ako treba naći clb
					if (!(clbModel.wiresIn[clbModel.inConnectionIndexes[j]].label instanceof CLBBox)) {
						inputWrongType++;
						valid = false;
						sol += Coefficients.INPUT_WRONG_TYPE;
					} else { // it really is clb
						CLBBox label = (CLBBox) clbModel.wiresIn[clbModel.inConnectionIndexes[j]].label;
						if(label.title != null) {
							if(!used.add(label.title)) {
								inputWrongName++;
								valid = false;
								sol += Coefficients.INPUT_MULTIPLES;
							} else if(!label.title.equals(inputsTask[j])) {
								inputWrongName++;
								sol += Coefficients.INPUT_PENALTY;
								valid = false;
							}
						}
					}
				} else { // pin must be on input
					if (!(clbModel.wiresIn[clbModel.inConnectionIndexes[j]].label instanceof Pin)) { // it's not pin
						inputWrongType++;
						sol += Coefficients.INPUT_WRONG_TYPE;
						valid = false;
					} else {
						Pin label = (Pin) clbModel.wiresIn[clbModel.inConnectionIndexes[j]].label;
						if(!label.input) { // ako nije ulazni pin
							sol += Coefficients.INPUT_WRONG_PIN_TYPE;
						} else {
							if(!used.add(label.title)) {
								inputWrongName++;
								valid = false;
								sol += Coefficients.INPUT_MULTIPLES;
							} else if(label.title != null) {
								if (!label.title.equals(inputsTask[j])) { // it is pin but wrong
									inputWrongName++;
									sol += Coefficients.INPUT_PENALTY;
									valid = false;
								}
							} else { //na pinove se pazi na drugim mjestima
								throw new IllegalStateException("Label is pin and its title is null");
							}	
						}
						
					}
				}
			}
		}
		return sol;
	}

}
