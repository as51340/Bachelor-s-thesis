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
		Pin[] pins = model.pins;
		Map<String, String> aliasMap = mapTask.aliasMap;
		for (String alias : aliasMap.keySet()) {
			int founded = 0;
//			System.out.println("Alias: " + alias + " clb: " + aliasMap.get(alias));
			for (int i = 0; i < pins.length; i++) { // try to find matching
				if (pins[i].connectionIndex == -1 || pins[i].title == null || !pins[i].title.equals(alias)) {
					continue;
				}
				Object v = pins[i].wires[pins[i].connectionIndex].label;
				if (v != null) {
					if (v instanceof CLBBox) {
						CLBBox clb = (CLBBox) v;
//						System.out.println(aliasMap.get(alias) + " " + clb.title); // expected vs actual
						if (clb.title != null && clb.title.equals(aliasMap.get(alias))) { // da li je taj pin dobro
																							// spojen
							founded++;
						}
					}
				} else {
//					System.out.println(
//							"Null labela for Pin " + pins[i].title + ", connection index " + pins[i].connectionIndex);
				}
			}
//			System.out.println("Founded " + founded);
			if (founded == 1) {
				sol += 10; // reward if they found correctly more than in situation when configuration is
							// invalid
			}
//			else if (founded > 1) {
//				sol += 5;
//			}
			else {
				sol -= 5;
				valid = false;
			}
		}

		return sol;
	}

}
