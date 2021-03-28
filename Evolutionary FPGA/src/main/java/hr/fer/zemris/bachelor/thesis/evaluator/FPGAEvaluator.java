package hr.fer.zemris.bachelor.thesis.evaluator;

import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.bool.SimpleFPGA;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.mapping.FPGAMapTask;

/**
 * Complete {@linkplain FPGAModel} evaluator.
 * 
 * @author andi
 *
 */
public class FPGAEvaluator implements Evaluator {

	public boolean valid = true;

	public Evaluator aliasesEvaluator;

	public Evaluator clbInputsEvaluator;

	public FPGAEvaluator(Evaluator aliasesEvaluator, Evaluator clbInputsEvaluator) {
		super();
		this.aliasesEvaluator = aliasesEvaluator;
		this.clbInputsEvaluator = clbInputsEvaluator;
	}

	public static class EvaluatorArranger {

		/**
		 * I don't think this method needs all that stuff. Maybe just on the end
		 * @param model
		 * @param conf
		 */
		public static FPGAModel prepareModelForEvaluation(FPGAModel model1, AIFPGAConfiguration conf, FPGAMapTask mapTask,
				SimpleFPGA sfpga) {
			FPGAModel model = new FPGAModel(model1.rows, model1.columns, model1.clbVariables, model1.wiresCount,
					model1.pinsPerSegment); //not sure if all would be deleted
			model.simplify(conf.configuration); // check all this stuff
			model.loadFromConfiguration(conf.configuration);
			model.clearWires();
			model.fillLabels();
			for (int i = 0; i < mapTask.clbs.length; i++) {
//				System.out.println("Result clb index[" + i + "]: " + conf.clbIndexes[i]);
				model.clbs[conf.clbIndexes[i]].setTitle(mapTask.clbs[i].name);
				model.clbs[conf.clbIndexes[i]].lut = sfpga.getLuts()[mapTask.clbs[i].decomposedIndex];
			}
			for (int i = 0; i < mapTask.variables.length; i++) {
				model.pins[conf.pinIndexes[i]].setTitle(mapTask.variables[i]);
			}
			for (int i = 0; i < mapTask.aliases.length; i++) {
				model.pins[conf.pinIndexes[mapTask.variables.length + i]].setTitle(mapTask.aliases[i]);
			}
			return model;
		}
	}

	@Override
	public double evaluate(AIFPGAConfiguration conf, FPGAModel model, FPGAMapTask mapTask) {
		double sol = 0;
		sol += aliasesEvaluator.evaluate(null, model, mapTask);
		if(((SimpleAliasesEvaluator) aliasesEvaluator).valid) {
//			System.out.println("Aliases je true");
		}
		valid = valid && ((SimpleAliasesEvaluator) aliasesEvaluator).valid;
		((SimpleAliasesEvaluator) aliasesEvaluator).valid = true;
		sol += clbInputsEvaluator.evaluate(conf, model, mapTask);
		if(((SimpleCLBInputsEvaluator) clbInputsEvaluator).valid) {
//			System.out.println("Inputs je true");
		}
		valid = valid && ((SimpleCLBInputsEvaluator) clbInputsEvaluator).valid;
		((SimpleCLBInputsEvaluator) clbInputsEvaluator).valid = true;
		return sol;
	}
}
