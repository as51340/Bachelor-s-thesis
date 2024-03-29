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

	public static int labelIsNull = 0, wrongType = 0, wrongTitleName = 0;

	/*
	 * We are optimists at the start. How this even works?
	 */
	public boolean valid = true;

	@Override
	public double evaluate(AIFPGAConfiguration conf, FPGAModel model, FPGAMapTask mapTask) {
		double sol = 0;
		Pin[] pins = model.pins;
		Map<String, String> aliasMap = mapTask.aliasMap;

		for (String alias : aliasMap.keySet()) {
			boolean founded = false;
			for (int i = 0; i < pins.length; i++) { // try to find matching
				if (pins[i].input == true || pins[i].connectionIndex == -1 || pins[i].title == null
						|| !pins[i].title.equals(alias)) { // title is null
					continue;
				}
				Object v = pins[i].wires[pins[i].connectionIndex].label;
				if (v != null) { // label is not null
					if (v instanceof CLBBox) {
						CLBBox clb = (CLBBox) v;
						if (clb.title.equals(aliasMap.get(alias))) { // da li je taj pin dobro spojen
							founded = true;
						} else {
							sol += Coefficients.OUTPUT_PENALTY;
							wrongTitleName++;
						}
					} else { // wrong type
						sol += Coefficients.OUTPUT_WRONG_TYPE; // dosa je object
						wrongType++;
					}
				} else {
					sol += Coefficients.OUTPUT_BLACK_LABEL; // nema spoja
					labelIsNull++;
				}
			}
			if (founded == false) { // nothing was founded
				valid = false;
			}
		}
		return sol;
	}
}
