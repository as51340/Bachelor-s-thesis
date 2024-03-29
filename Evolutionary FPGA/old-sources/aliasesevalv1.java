	package hr.fer.zemris.bachelor.thesis.evaluator;

import java.util.Map;

import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.FPGAModel.CLBBox;
import hr.fer.zemris.fpga.FPGAModel.Pin;
import hr.fer.zemris.fpga.mapping.FPGAMapTask;

/**
 * This evaluator only sees if function is obtained from correct clb output.
 * Valid: if we have only one correctly defined pin connector to clb output.
 * 
 * @author andi
 *
 */
public class SimpleAliasesEvaluator implements Evaluator {

	/*
	 * We are optimists at the start. How this even works?
	 */
	public boolean valid = true;

	@Override
	public double evaluate(AIFPGAConfiguration conf, FPGAModel model, FPGAMapTask mapTask) {
		double sol = 0;
		int correct = 0;
		Pin[] pins = model.pins;
		Map<String, String> aliasMap = mapTask.aliasMap;
		for (String alias : aliasMap.keySet()) {
			int founded = 0;
			for (int i = 0; i < pins.length; i++) { // try to find matching
				if (pins[i].connectionIndex == -1 || pins[i].title == null || !pins[i].title.equals(alias)) { //title is null
					continue;
				}
				Object v = pins[i].wires[pins[i].connectionIndex].label;
				if (v != null) { //label is not null
					if (v instanceof CLBBox) {
						CLBBox clb = (CLBBox) v;
						if (clb.title != null && clb.title.equals(aliasMap.get(alias))) { // da li je taj pin dobro
																							// spojen
							founded++;
						}
					} else {
						valid = false;
						sol += Coefficients.INPUT_WRONG_TYPE;
					}
				} else { //punish it with black label
					valid = false;
					sol += Coefficients.BLACK_LABEL;
				}
			}
//			System.out.println("Founded " + founded);
			if (founded == 1) {
				correct++;
				sol = Math.pow(Coefficients.OUTPUT_PRIZE, correct); //linear grow of good values
			} 
			else {
				valid = false;
			}
		}
		return sol;
	}

}

