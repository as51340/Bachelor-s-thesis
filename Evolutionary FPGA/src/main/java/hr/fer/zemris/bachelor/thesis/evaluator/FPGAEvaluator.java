package hr.fer.zemris.bachelor.thesis.evaluator;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.bool.SimpleFPGA;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.FPGAModel.CLBBox;
import hr.fer.zemris.fpga.FPGAModel.Pin;
import hr.fer.zemris.fpga.FPGAModel.SwitchBox;
import hr.fer.zemris.fpga.FPGAModel.WireSegment;
import hr.fer.zemris.fpga.FPGAModel.LabelsFillResult;
import hr.fer.zemris.fpga.mapping.FPGAMapTask;
import hr.fer.zemris.fpga.mapping.FPGAMapTask.CLB;

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

	public Evaluator tracingEvaluator;

	public FPGAEvaluator(Evaluator aliasesEvaluator, Evaluator clbInputsEvaluator, Evaluator tracingEvaluator) {
		super();
		this.aliasesEvaluator = aliasesEvaluator;
		this.clbInputsEvaluator = clbInputsEvaluator;
		this.tracingEvaluator = tracingEvaluator;
	}

	public static class EvaluatorArranger {

		/**
		 * I don't think this method needs all that stuff. Maybe just on the end
		 * 
		 * @param model
		 * @param conf
		 */
		public static FPGAModel prepareModelForEvaluation(FPGAModel model1, AIFPGAConfiguration conf,
				FPGAMapTask mapTask, SimpleFPGA sfpga) {
			FPGAModel model = new FPGAModel(model1.rows, model1.columns, model1.clbVariables, model1.wiresCount,
					model1.pinsPerSegment); // not sure if all would be deleted

			for (int i = 0; i < mapTask.clbs.length; i++) {
				model.clbs[conf.clbIndexes[i]].setTitle(mapTask.clbs[i].name);
				model.clbs[conf.clbIndexes[i]].lut = sfpga.getLuts()[mapTask.clbs[i].decomposedIndex];
			}
			for (int i = 0; i < mapTask.variables.length; i++) {
				model.pins[conf.pinIndexes[i]].setTitle(mapTask.variables[i]);
			}
			for (int i = 0; i < mapTask.aliases.length; i++) {
				model.pins[conf.pinIndexes[mapTask.variables.length + i]].setTitle(mapTask.aliases[i]);
			}

			model.simplify(conf.configuration); // check all this stuff

			for (int i = 0; i < mapTask.variables.length; i++) {
				conf.configuration.pinInput[conf.pinIndexes[i]] = true;
			}
			model.loadFromConfiguration(conf.configuration);

//			model.fillLabels();
			// result.distinctMultiples; //koliko je zica u problemima
			// result.cumulativeMultiples //za svako novo gazenje broj koliko gazenja ima
			// isto uvest kaznjavanje za to

			return model;
		}

	}

	@Override
	public double evaluate(AIFPGAConfiguration conf, FPGAModel model, FPGAMapTask mapTask) {
		double sol = 0;

		double trace = tracingEvaluator.evaluate(conf, model, mapTask); // must be called first for filling labels, soft
																		// req
//		sol += trace;

		sol += aliasesEvaluator.evaluate(null, model, mapTask); // hard req
		if (((SimpleAliasesEvaluator) aliasesEvaluator).valid) {
			System.out.println("Aliases je true");
		}
		valid = valid && ((SimpleAliasesEvaluator) aliasesEvaluator).valid;
		((SimpleAliasesEvaluator) aliasesEvaluator).valid = true;

		sol += clbInputsEvaluator.evaluate(conf, model, mapTask); // hard req
		if (((SimpleCLBInputsEvaluator) clbInputsEvaluator).valid) {
			System.out.println("Inputs je true");
		}
		valid = valid && ((SimpleCLBInputsEvaluator) clbInputsEvaluator).valid;
		((SimpleCLBInputsEvaluator) clbInputsEvaluator).valid = true;

		return sol;
	}
}
