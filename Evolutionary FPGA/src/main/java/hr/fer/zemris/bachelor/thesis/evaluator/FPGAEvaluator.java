package hr.fer.zemris.bachelor.thesis.evaluator;

import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.mapping.FPGAMapTask;

public class FPGAEvaluator implements Evaluator{
	
	public boolean valid = true;
	
	public Evaluator aliasesEvaluator;
	
	
	
	static class EvaluatorArranger {
		
		void prepareModelForEvaluation(FPGAModel model, AIFPGAConfiguration conf) {
			model.loadFromConfiguration(conf.configuration);
			model.clearWires();
			model.fillLabels();
		}
		
	}

	@Override
	public double evaluate(FPGAModel model, FPGAMapTask mapTask) {
		double sol = 0;
		sol += aliasesEvaluator.evaluate(model, mapTask);
		return 0;
	}
}
