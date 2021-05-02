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
 * Complete {@linkplain FPGAModel} evaluator. Design pattern: some weird
 * composit I think
 * 
 * @author andi
 *
 */
public class FPGAEvaluator implements Evaluator {

	public boolean valid = true;

	public Evaluator aliasesEvaluator;

	public Evaluator clbInputsEvaluator;

	public Evaluator tracingEvaluator;

	public Evaluator switchBoxEvaluator;

	public Evaluator connectionQualEvaluator;

	public boolean aliasesValid = false;

	public boolean inputsValid = false;

	public static boolean DEBUG = false;
	
//	private List<EvaluatorRunnable> runnables;
	
//	private List<Thread> evalThreads = null;
	
	public FPGAEvaluator() {
		super();
		this.aliasesEvaluator = new SimpleAliasesEvaluator();
		this.clbInputsEvaluator = new SimpleCLBInputsEvaluator();
		this.tracingEvaluator = new TracingEvaluator();
		this.switchBoxEvaluator = new SwitchBoxEvalutor();
		this.connectionQualEvaluator = new ConnectionQualityEvaluator();
		
		

//		runnables = new ArrayList<>();
//		runnables.add(new EvaluatorRunnable(switchBoxEvaluator));
//		runnables.add(new EvaluatorRunnable(aliasesEvaluator));
//		runnables.add(new EvaluatorRunnable(clbInputsEvaluator));
//		runnables.add(new EvaluatorRunnable(connectionQualEvaluator));
//				
//		evalThreads = new ArrayList<>();
//		
//		for(int i = 0; i < runnables.size(); i++) {
//			evalThreads.add(new Thread(runnables.get(i)));
//		}
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

			// not very efficient but should work for keeping free pin out from use
			for (int i = 0; i < model.pins.length; i++) {
				if (model.pins[i].title == null) {
					model.pins[i].connectionIndex = -1;
					model.pins[i].input = false;
				}
			}

			if (DEBUG) {
				for (int i = 0; i < mapTask.variables.length; i++) {
//					model.pins[conf.pinIndexes[i]].setTitle(mapTask.variables[i]);
					System.out.println("Stavljam varijablu " + mapTask.variables[i] + " na pin " + conf.pinIndexes[i]);
				}
				for (int i = 0; i < mapTask.aliases.length; i++) {
//					model.pins[conf.pinIndexes[mapTask.variables.length + i]].setTitle(mapTask.aliases[i]);
					System.out.println("Stavljam funkciju " + mapTask.aliases[i] + " na pin "
							+ conf.pinIndexes[mapTask.variables.length + i]);
				}

				for (int i = 0; i < conf.configuration.pinInput.length; i++) {
					System.out.println("Pin " + i + " je ulazni: " + conf.configuration.pinInput[i]);
				}
			}

			return model;
		}
	}

	/**
	 *
	 * @author andi
	 *
	 */
	public static class EvaluatorRunnable implements Runnable {

		private Evaluator evaluator;

		private AIFPGAConfiguration conf;

		private FPGAModel model;

		private FPGAMapTask mapTask;

		/**
		 * Local thread solution
		 */
		private double localSol = 0.0;

		public EvaluatorRunnable(Evaluator evaluator) {
			this.evaluator = evaluator;
		}
		
		public void setConf(AIFPGAConfiguration conf) {
			this.conf = conf;
		}
		
		public void setModel(FPGAModel model) {
			this.model = model;
		}
		
		public void setMapTask(FPGAMapTask mapTask) {
			this.mapTask = mapTask;
		}

		@Override
		public void run() {
			this.localSol = this.evaluator.evaluate(conf, model, mapTask);
		}

	}

	@Override
	public double evaluate(AIFPGAConfiguration conf, FPGAModel model, FPGAMapTask mapTask) {
		double sol = 0;
//
		sol += tracingEvaluator.evaluate(conf, model, mapTask); // must be called first for filling labels, soft
		sol += switchBoxEvaluator.evaluate(conf, model, mapTask); // soft req
		sol += aliasesEvaluator.evaluate(conf, model, mapTask); // hard req
		sol += clbInputsEvaluator.evaluate(conf, model, mapTask); // hard req
		sol += connectionQualEvaluator.evaluate(conf, model, mapTask); // soft req
	
		
//		for(int i = 0; i < evalThreads.size(); i++) {
//			runnables.get(i).setConf(conf);
//			runnables.get(i).setMapTask(mapTask);
//			runnables.get(i).setModel(model);
//			evalThreads.get(i).start();
//		}
//		
//		for(int i = 0; i < evalThreads.size(); i++) {
//			try {
//				evalThreads.get(i).join();
//				sol+= runnables.get(i).localSol;
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		

		if (((SimpleAliasesEvaluator) aliasesEvaluator).valid) {
//			System.out.println("Aliases je true");
			if (((SimpleCLBInputsEvaluator) clbInputsEvaluator).valid) {
//				System.out.println("Inputs je true");
			}
		}

		valid = valid && ((SimpleAliasesEvaluator) aliasesEvaluator).valid;
		((SimpleAliasesEvaluator) aliasesEvaluator).valid = true;

		valid = valid && ((SimpleCLBInputsEvaluator) clbInputsEvaluator).valid;
		((SimpleCLBInputsEvaluator) clbInputsEvaluator).valid = true;

		return sol;
	}
}
