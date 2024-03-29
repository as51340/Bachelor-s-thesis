package hr.fer.zemris.bachelor.thesis.evaluator;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hr.fer.zemris.bachelor.thesis.mapping.configuration.AIFPGAConfiguration;
import hr.fer.zemris.fpga.FPGAModel;
import hr.fer.zemris.fpga.FPGAModel.CLBBox;
import hr.fer.zemris.fpga.FPGAModel.Pin;
import hr.fer.zemris.fpga.FPGAModel.SwitchBox;
import hr.fer.zemris.fpga.FPGAModel.WireSegment;
import hr.fer.zemris.fpga.mapping.FPGAMapTask;

/**
 * Evaluates distances between clb output and pin. It is neccessary that we first call this evaluator and then later others for program to run efficiently.
 * 
 * @author andi
 *
 */
public class TracingEvaluator implements Evaluator {

	@Override
	public double evaluate(AIFPGAConfiguration conf, FPGAModel model, FPGAMapTask mapTask) {
		FillLabelsResult tracingResult = fillLabels(model, mapTask);
		
		double distinct_result = tracingResult.distinctMultiples * Coefficients.COLLISION_PENALTY; //linear function of collisions
		double tracers = (tracingResult.aliasesTracingResult + tracingResult.inputsTracingResult) / 135.0;
		double cumulative_result = 1.25 * tracingResult.cumulativeMultiples / Coefficients.COLLISION_PENALTY * 0.75; // cumulative result
//		System.out.println("Distinct result: "  + distinct_result);
//		System.out.println("Aliases tracers: " + tracingResult.aliasesTracingResult);
//		System.out.println("Input tracers: " + tracingResult.inputsTracingResult);
//		System.out.println("Tracers: " + tracers);
//		System.out.println("Cumulative result: " + cumulative_result);
//		System.out.println();
		
		return distinct_result + cumulative_result;
	}

	/**
	 * Tracing algorithm result
	 * @author andi
	 *
	 */
	public class FillLabelsResult {
		public int distinctMultiples;
		public int cumulativeMultiples;
		public double aliasesTracingResult;
		public double inputsTracingResult;

	}

	public FillLabelsResult fillLabels(FPGAModel model, FPGAMapTask mapTask) {
		FillLabelsResult result = new FillLabelsResult();
		for (Pin pin : model.pins) {
			if (pin.connectionIndex != -1 && pin.input) {
				fillLabel(pin, pin.wires[pin.connectionIndex], result, model, mapTask);
			}
		}
		for (CLBBox box : model.clbs) {
			if (box.outConnectionIndex != -1) {
				fillLabel(box, box.wiresOut[box.outConnectionIndex], result, model, mapTask);
			}
		}
		return result;
	}

	private int checkPinsTrace(FPGAModel model, WireSegment segment, int dist, FPGAMapTask mapTask) {
		int res = 0;
		Map<String, String> aliases = mapTask.aliasMap;

		for (int i = 0; i < model.pins.length; i++) {
			Pin pin = model.pins[i];
			if (pin.connectionIndex == -1 || pin.input)
				continue;
			if (pin.wires[pin.connectionIndex] == segment) {
//				Object pinLabel = pin.wires[pin.connectionIndex].label;
				Object wireLabel = segment.label;
				String pinTitle = pin.title;
				if (wireLabel == null) {
//					System.out.println("Labela segmenta je null!");
				} else if (wireLabel instanceof Pin) {
					Pin pinLabel = (Pin) wireLabel;
//					System.out.println("Na izlazni pin " + pinTitle +  " je spojen uzlazni pin " + pinLabel.title + " ERROR 1");
				} else if (wireLabel instanceof CLBBox) {
					CLBBox box = (CLBBox) wireLabel;
					if (pinTitle == null) {
//						System.out.println("Pin title je null!");
					} else {
						if (box.title != null && box.title.equals(aliases.get(pinTitle))) {
//							System.out.println("Founded " + pinTitle + " with distance " + dist);
							res += dist;
						} else {
//							System.out.println("Naslov CLB-a nije kao iz alias mapa: expexted " + aliases.get(pinTitle) + " but got " + box.title);
						}
					}
				}
			} else {
//				System.out.println("Nije uopće taj segment!");
			}

		}
//		System.out.println();
//		System.out.println();
		return res;
	}

	private int checkClbInputsTrace(FPGAModel model, WireSegment segment, int dist, FPGAMapTask mapTask) {
//		System.out.println("Starting clb input tracing algorithm");
		int res = 0;
		for (int i = 0; i < model.clbs.length; i++) {
			CLBBox box = model.clbs[i];
			for (int j = 0; j < box.inConnectionIndexes.length; j++) {
				if (box.inConnectionIndexes[j] == -1) continue;
				if (box.wiresIn[box.inConnectionIndexes[j]] == segment) {
					Object label = segment.label;
					if (label instanceof Pin) {
//						Pin pinLabel = (Pin) label;
//						System.out.println("Founded " + pinLabel.title + " on index " + j + " on box " + box.title + " with distance " + dist);
					} else if (label instanceof CLBBox) {
//						CLBBox boxLabel = (CLBBox) label;
//						System.out.println("Founded " + boxLabel.title + " on index " + j + " on box " + box.title + " with distance " + dist);
					}
					res = dist;
				} else {
//					System.out.println("Nije uopće taj segment");
				}
			}

		}
		return res;
	}

	public void fillLabel(Object label, WireSegment segment, FillLabelsResult result, FPGAModel model,
			FPGAMapTask mapTask) {
		Deque<Integer> distances = new LinkedList<>();
		distances.addLast(0);

		Set<WireSegment> visitedSet = new HashSet<>();
		List<WireSegment> newSet = new LinkedList<>();
		newSet.add(segment);
		boolean multipleFound = false;
		while (!newSet.isEmpty()) {
			WireSegment currentSegment = newSet.remove(0);
			int dist = distances.removeFirst();
			visitedSet.add(currentSegment);
			if (currentSegment.label == null) {
				if (label instanceof CLBBox) {
//					CLBBox box = (CLBBox) label;
//					System.out.println("Postavio sam " + box.title + " na segment s indexom " + segment.index
//							+ " first index " + segment.firstIndex + " second index " + segment.secondIndex
//							+ " jer je bila null dosada");
				}
				currentSegment.label = label;
			} else if (currentSegment.label.equals(label)) {
				if (label instanceof CLBBox) {
//					CLBBox box = (CLBBox) label;
//					System.out.println("Postavio sam " + box.title + " na segment s indexom " + segment.index
//							+ " first index " + segment.firstIndex + " second index " + segment.secondIndex
//							+ " a vec je dosada bila ista.");
				}
				currentSegment.label = label;
			} else {
				if (label instanceof CLBBox) {
//					CLBBox box = (CLBBox) label;
//					System.out.println("Nisam postavio " + box.title + " na segment s indexom " + segment.index
//							+ " first index " + segment.firstIndex + " second index " + segment.secondIndex
//							+ " jer vec postoji labela");
					if (currentSegment.label instanceof Pin) {
//						Pin pin = (Pin) currentSegment.label;
//						System.out.println("Tu vec postoji " + pin.title);
					} else if (currentSegment.label instanceof CLBBox) {
//						CLBBox clbBoxBefore = (CLBBox) currentSegment.label;
//						System.out.println("Tu vec postoji " + clbBoxBefore.title);
					}

				}
				currentSegment.label = AIFPGAConfiguration.MULTIPLE_LABELS;
				result.cumulativeMultiples++;
				multipleFound = true; // ???
			}

			double aliasesTracingResult = ((model.wires.length / model.wiresCount)
					- checkPinsTrace(model, currentSegment, dist, mapTask));

			double inputsTracingResult = ((model.wires.length / model.wiresCount)
					- checkClbInputsTrace(model, currentSegment, dist, mapTask));

			result.aliasesTracingResult += aliasesTracingResult;

			result.inputsTracingResult += inputsTracingResult;

			addSwitchedWires(newSet, visitedSet, currentSegment.firstBox, currentSegment.firstIndex, model, dist,
					distances);
			addSwitchedWires(newSet, visitedSet, currentSegment.secondBox, currentSegment.secondIndex, model, dist,
					distances);
		}
		if (multipleFound) {
			result.distinctMultiples++;
		}
	}

	private void addSwitchedWires(List<WireSegment> newSet, Set<WireSegment> visitedSet, SwitchBox box,
			int index, FPGAModel model, int distance, Deque<Integer> distances) { // u visited setu su svi već pogledani
																					// wire segmenti, u newSet dodajemo
		byte[] rowToScan = null;
		for (int i = 0; i < 4 * model.wiresCount; i++) {
			if (i == index)
				continue;
			if (box.configuration[index][i] == 1) {
				WireSegment w = box.segments[i];
				if (w != null && !visitedSet.contains(w)) {
					newSet.add(w);
					distances.addLast(distance + 1);
				}
				rowToScan = box.configuration[i];
				break;
			}
			if (box.configuration[index][i] == 2) {
				WireSegment w = box.segments[i];
				if (w != null && !visitedSet.contains(w)) {
					newSet.add(w);
					distances.addLast(distance + 1);
				}
			}
		}
		if (rowToScan != null) {
			for (int i = 0; i < 4 * model.wiresCount; i++) {
				if (rowToScan[i] == 2) {
					WireSegment w = box.segments[i];
					if (w != null && !visitedSet.contains(w)) {
						newSet.add(w);
						distances.addLast(distance + 1);
					}
				} else if (rowToScan[i] == 1) {
					System.out.println("ERROR 1.");
				}
			}
		}
	}

}
