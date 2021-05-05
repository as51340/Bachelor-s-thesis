package hr.fer.zemris.bachelor.thesis.evaluator;

import java.util.HashMap;
import java.util.Map;

import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.FPGAModel.CLBBox;
import hr.fer.zemris.fpga.FPGAModel.Pin;
import hr.fer.zemris.fpga.FPGAModel.SwitchBox;
import hr.fer.zemris.fpga.FPGAModel.WireSegment;
import hr.fer.zemris.fpga.mapping.FPGAMapTask;

/**
 * Takes care about quality of connections. For example if clb inputs is connected to something that has label and same for switch boxes and clb outputs.
 * So there aren't any black connections!
 * @author andi
 *
 */
public class ConnectionQualityEvaluator implements Evaluator{

	@Override
	public double evaluate(AIFPGAConfiguration conf, FPGAModel model, FPGAMapTask mapTask) {
		SwitchBox[] swBoxes = model.switchBoxes;
		
		
		double sol = 0.0;
		
		//remember to remove when returning to old model
		
		//black wires connections elimination and advanced cleaning options
		for(int i = 0; i < swBoxes.length; i++) {
			byte[][] sw_config = swBoxes[i].configuration;
			
			Map<Integer, Integer> wiresInGroup = new HashMap<>();
			
			for(int j = 0; j < sw_config.length; j++) {
				for(int k = 0; k < sw_config[0].length && k < j; k++) {
					if(sw_config[j][k] == 2 && sw_config[k][j] == 1) { //from j to k 
						wiresInGroup.merge(j, 1, Integer::sum);
//						if (swBoxes[i].segments[k] == null) {
//							System.out.println("Switch box " + i + " wire " + k);
//						}
						if(swBoxes[i].segments[k].label == null) { //if you are connecting it to null label
							sol += Coefficients.SW_WIRE_TO_NULL;
						}
					} else if(sw_config[k][j] == 2 && sw_config[j][k] == 1) { //from k to j 
						wiresInGroup.merge(k, 1, Integer::sum);
						if(swBoxes[i].segments[j].label == null) {
							sol += Coefficients.SW_WIRE_TO_NULL;
						}
					}
				}
			}
			
			for(Integer key: wiresInGroup.keySet()) {
				int value = wiresInGroup.get(key);
				--value;
				sol+= Coefficients.MULTIPLE_VALUES * value; //how many multiple connections are from same wire
				
			}
		}
		
		
		//input not connected to any switch box
		Pin[] pins = model.pins;
		
		double component_2 = 0.0;
		
		for(int i = 0; i < pins.length; i++) {
			if(pins[i].input) { //that should eliminate condition if connection index is -1
				
				boolean founded = false;
				
				WireSegment segment = pins[i].wires[pins[i].connectionIndex];
				
				
				for(int j = 0; j < 4*model.wiresCount; j++) {
					if(segment.firstBox.configuration[j][segment.firstIndex] == 1 && segment.firstBox.configuration[segment.firstIndex][j] == 2) {
						founded = true;
					}
				}
				
				if(founded == true) continue; //check for next input pin
				
				for(int j = 0; j < 4*model.wiresCount; j++) {
					if(segment.secondBox.configuration[j][segment.secondIndex] == 1 && segment.secondBox.configuration[segment.secondIndex][j] == 2) {
						founded = true;
					}
				}
				if(founded == false) component_2 += Coefficients.INPUT_TO_NOWHERE;
				
			}
		}
		
		return sol + component_2;
	}

}
