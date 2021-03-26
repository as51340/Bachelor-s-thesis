package hr.fer.zemris.bachelor.thesis.evaluator;

import java.util.Map;

import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.FPGAModel.CLBBox;
import hr.fer.zemris.fpga.FPGAModel.Pin;
import hr.fer.zemris.fpga.mapping.FPGAMapTask;

/**
 * This evaluator only sees if function is obtained from correct clb output. Valid: if we have only one correctly defined
 * pin connector to clb output. 
 * @author andi
 *
 */
public class SimpleAliasesEvaluator implements Evaluator{

	/*
	 * We are optimists at the start
	 */
	public boolean valid = true;
	
	@Override
	public double evaluate(FPGAModel model, FPGAMapTask mapTask) {
		double sol = 0;
		Pin[] pins = model.pins;
		Map<String, String> aliasMap = mapTask.aliasMap;
		for(String alias: aliasMap.keySet()) { //iterate through all aliases
			int founded = 0;
//			System.out.println("Alias: " + alias + " clb: " + aliasMap.get(alias));
			for(int i = 0; i < pins.length; i++) { //try to find matching
				if(pins[i].connectionIndex == -1) continue;
				Object v = pins[i].wires[pins[i].connectionIndex].label; 
				if(v instanceof CLBBox) {
					CLBBox clb = (CLBBox) v;
					if(clb.title.equals(aliasMap.get(alias))) { // da li je taj pin dobro spojen
//						System.out.println("Alias: " + alias + " CLB title: " + clb.title);
						founded++;
					} else {
//						System.out.println(clb.title + " " + aliasMap.get(alias));
					}
				} else {
//					if(v != null) System.out.println(v.getClass());
				}
			}
			if(founded == 1) {
				sol += 10; //reward if they found correctly more than in situation when configuration is invalid
			} else {
				sol -= 5;
				valid = false;
			}
		}
		
		return sol;
	}

}
