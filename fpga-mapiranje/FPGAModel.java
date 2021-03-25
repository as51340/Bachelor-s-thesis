package hr.fer.zemris.fpga;

import hr.fer.zemris.bool.BooleanLUT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class FPGAModel {

	public static class SwitchBox {
		public int dimension;
		public byte[][] configuration;
		public WireSegment[] segments;
		public int row;
		public int column;
		
		public SwitchBox(int dimension, int row, int column) {
			super();
			this.dimension = dimension;
			this.segments = new WireSegment[4*dimension];
			this.configuration = new byte[4*dimension][4*dimension];
			this.row = row;
			this.column = column;
		}
		@Override
		public String toString() {
			return "SwitchBox(row="+row+",col="+column+")";
		}
	}
	
	public static class WireSegment {
		static final Object MULTIPLE_LABELS = new Object();
		public int row;
		public int column;
		public int firstIndex;
		public int secondIndex;
		public SwitchBox firstBox;
		public SwitchBox secondBox;
		public Object label;
		public boolean horizontal;
		public int index;
		
		public WireSegment(SwitchBox firstBox, int firstIndex,
				SwitchBox secondBox, int secondIndex, int row, int column, boolean horizontal, int index) {
			super();
			this.firstBox = firstBox;
			this.firstIndex = firstIndex;
			this.secondBox = secondBox;
			this.secondIndex = secondIndex;
			firstBox.segments[firstIndex] = this;
			secondBox.segments[secondIndex] = this;
			this.row = row;
			this.column = column;
			this.horizontal = horizontal;
			this.index = index;
		}
		@Override
		public String toString() {
			return "WireSegment(row="+row+",col="+column+","+(horizontal?"H":"A")+","+index+") between "+firstBox+"@"+firstIndex+" and " + secondBox+"@"+secondIndex;
		}
	}

	public static class Pin {
		public WireSegment[] wires;
		public byte connectionIndex;
		public boolean input;
		public String title;
		public Boolean value;
		
		public Pin(int wiresCount) {
			wires = new WireSegment[wiresCount];
			connectionIndex = -1;
		}
		@Override
		public String toString() {
			return "Pin ("+(input?"IN":"OUT")+(title==null?"":","+title)+") on wires "+Arrays.toString(wires);
		}
		public void setTitle(String title) {
			this.title = title;
		}
	}

	public static class CLBBox {
		public String alias;
		public String title;
		public Boolean outputValue;
		public WireSegment[] wiresIn;
		public WireSegment[] wiresOut;
		public byte[] inConnectionIndexes;
		public byte outConnectionIndex;
		public BooleanLUT lut;
		
		public CLBBox(int wiresCount, int numberOfVariables) {
			wiresIn = new WireSegment[wiresCount];
			wiresOut = new WireSegment[wiresCount];
			inConnectionIndexes = new byte[numberOfVariables];
			for(int i = 0; i < numberOfVariables; i++) {
				inConnectionIndexes[i] = -1;
			}
			outConnectionIndex = -1;
		}
		
		@Override
		public String toString() {
			return "CLB inWires "+Arrays.toString(wiresIn)+", outWires "+Arrays.toString(wiresOut);
		}

		public void setTitle(String name) {
			this.title = name;
		}
		public void setAlias(String alias) {
			this.alias = alias;
		}
	}
	
	public int rows;
	public int columns;
	public int clbVariables;
	public int wiresCount;
	public int pinsPerSegment;
	public CLBBox[] clbs;
	public SwitchBox[] switchBoxes;
	public Pin[] pins;
	public WireSegment[] wires;
	
	public FPGAModel(int rows, int columns, int clbVariables, int wiresCount, int pinsPerSegment) {
		super();
		this.rows = rows;
		this.columns = columns;
		this.clbVariables = clbVariables;
		this.wiresCount = wiresCount;
		this.pinsPerSegment = pinsPerSegment;
		
		clbs = new CLBBox[rows*columns];
		switchBoxes = new SwitchBox[(rows+1)*(columns+1)];
		pins = new Pin[pinsPerSegment * (2*columns + 2*rows)];
		
		// Stvaranje CLBova:
		for(int i = 0; i < clbs.length; i++) {
			clbs[i] = new CLBBox(wiresCount, clbVariables);
		}

		// Stvaranje SwitchBoxova:
		for(int i = 0; i < switchBoxes.length; i++) {
			switchBoxes[i] = new SwitchBox(wiresCount, i/(columns+1), i%(columns+1));
		}
		
		// Stvaranje pinova:
		for(int i = 0; i < pins.length; i++) {
			pins[i] = new Pin(wiresCount);
		}
		
		List<WireSegment> wiresList = new ArrayList<>();
		// Generiranje vodoravnih snopova žica:
		int swIndex = -1;
		for(int y = 0; y <= rows; y++) {
			for(int c = 0; c < columns; c++) {
				swIndex++;
				for(int i = 0; i < wiresCount; i++) {
					WireSegment w = new WireSegment(switchBoxes[swIndex], wiresCount+i, switchBoxes[swIndex+1], 3*wiresCount+i, y, c, true, i);
					wiresList.add(w);
				}
			}
			swIndex++;
		}
		
		// Generiranje okomitih snopova žica:
		for(int x = 0; x <= columns; x++) {
			for(int r = 0; r < rows; r++) {
				swIndex = r*(columns+1)+x;
				for(int i = 0; i < wiresCount; i++) {
					WireSegment w = new WireSegment(switchBoxes[swIndex], 2*wiresCount+i, switchBoxes[swIndex+columns+1], i, r, x, false, i);
					wiresList.add(w);
				}
			}
		}

		// Generiranje kompletnog popisa segmenata:
		wires = wiresList.toArray(new WireSegment[0]);
		
		// Spajanje ulaza i izlaza CLBova na snopove žica:
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < columns; c++) {
				int clbIndex = r*columns + c;
				swIndex = r*(columns+1)+c;
				for(int i = 0; i < wiresCount; i++) {
					clbs[clbIndex].wiresIn[i] = switchBoxes[swIndex].segments[2*wiresCount+i];
					clbs[clbIndex].wiresOut[i] = switchBoxes[swIndex+1].segments[2*wiresCount+i];
				}
			}
		}

		// Generiranje gornjih pinova:
		for(int c = 0; c < columns; c++) {
			int pinIndex = c*pinsPerSegment-1;
			for(int p = 0; p < pinsPerSegment; p++) {
				pinIndex++;
				for(int i = 0; i < wiresCount; i++) {
					pins[pinIndex].wires[i] = switchBoxes[c].segments[wiresCount+i];
				}
			}
		}
			
		// Generiranje donjih pinova:
		for(int c = 0; c < columns; c++) {
			int pinIndex = columns*pinsPerSegment + rows*pinsPerSegment+c*pinsPerSegment-1;
			for(int p = 0; p < pinsPerSegment; p++) {
				pinIndex++;
				for(int i = 0; i < wiresCount; i++) {
					pins[pinIndex].wires[i] = switchBoxes[rows*(columns+1)+c].segments[wiresCount+i];
				}
			}
		}

		// Generiranje lijevih pinova:
		for(int r = 0; r < rows; r++) {
			int pinIndex = pinsPerSegment*2*columns+(r+rows)*pinsPerSegment-1;
			for(int p = 0; p < pinsPerSegment; p++) {
				pinIndex++;
				for(int i = 0; i < wiresCount; i++) {
					pins[pinIndex].wires[i] = switchBoxes[r*(columns+1)].segments[2*wiresCount+i];
				}
			}
		}

		// Generiranje desnih pinova:
		for(int r = 0; r < rows; r++) {
			int pinIndex = pinsPerSegment*columns+r*pinsPerSegment-1;
			for(int p = 0; p < pinsPerSegment; p++) {
				pinIndex++;
				for(int i = 0; i < wiresCount; i++) {
					pins[pinIndex].wires[i] = switchBoxes[r*(columns+1)+columns].segments[2*wiresCount+i];
				}
			}
		}
	}

	public class FPGAModelConfiguration {
		public byte[][][] switchBoxes;
		public byte[][] clbInIndexes;
		public byte[] clbOutIndexes;
		public byte[] pinIndexes;
		public boolean[] pinInput;
		
		public void copyFrom(FPGAModelConfiguration c) {
			for(int i = 0; i < pinInput.length; i++) {
				pinInput[i] = c.pinInput[i];
			}
			for(int i = 0; i < pinIndexes.length; i++) {
				pinIndexes[i] = c.pinIndexes[i];
			}
			for(int i = 0; i < clbOutIndexes.length; i++) {
				clbOutIndexes[i] = c.clbOutIndexes[i];
			}
			for(int i = 0; i < clbInIndexes.length; i++) {
				for(int j = 0; j < clbInIndexes[i].length; j++) {
					clbInIndexes[i][j] = c.clbInIndexes[i][j];
				}
			}
			for(int i = 0; i < switchBoxes.length; i++) {
				for(int j = 0; j < switchBoxes[i].length; j++) {
					for(int k = 0; k < switchBoxes[i][j].length; k++) {
						switchBoxes[i][j][k] = c.switchBoxes[i][j][k];
					}
				}
			}
		}
	}

	public FPGAModelConfiguration newConfiguration() {
		FPGAModelConfiguration conf = new FPGAModelConfiguration();
		
		conf.switchBoxes = new byte[switchBoxes.length][4*wiresCount][4*wiresCount];
		conf.clbInIndexes = new byte[clbs.length][clbVariables];
		conf.clbOutIndexes = new byte[clbs.length];
		conf.pinIndexes = new byte[rows*2*pinsPerSegment + columns*2*pinsPerSegment];
		conf.pinInput = new boolean[rows*2*pinsPerSegment + columns*2*pinsPerSegment];
		
		for(int i = 0; i < conf.clbInIndexes.length; i++) {
			for(int j = 0; j < conf.clbInIndexes[i].length; j++) {
				conf.clbInIndexes[i][j] = -1;
			}
			conf.clbOutIndexes[i] = -1;
		}

		for(int i = 0; i < conf.pinIndexes.length; i++) {
			conf.pinIndexes[i] = -1;
		}
		
		return conf;
	}

	public FPGAModelConfiguration saveToConfiguration(FPGAModelConfiguration conf) {
		for(int i = 0; i < conf.switchBoxes.length; i++) {
			byte[][] src = switchBoxes[i].configuration;
			byte[][] dst = conf.switchBoxes[i];
			for(int j = 0; j < 4*wiresCount; j++) {
				for(int k = 0; k < 4*wiresCount; k++) {
					dst[j][k] = src[j][k];
				}
			}
		}
		for(int i = 0; i < conf.clbInIndexes.length; i++) {
			CLBBox box = clbs[i];
			byte[] dst = conf.clbInIndexes[i];
			for(int j = 0; j < clbVariables; j++) {
				dst[j] = box.inConnectionIndexes[j];
			}
			conf.clbOutIndexes[i] = box.outConnectionIndex;
		}
		for(int i = 0; i < conf.pinIndexes.length; i++) {
			conf.pinIndexes[i] = pins[i].connectionIndex;
			conf.pinInput[i] = pins[i].input;
		}
		return conf;
	}

	public void loadFromConfiguration(FPGAModelConfiguration conf) {
		for(int i = 0; i < conf.switchBoxes.length; i++) {
			byte[][] dst = switchBoxes[i].configuration;
			byte[][] src = conf.switchBoxes[i];
			for(int j = 0; j < 4*wiresCount; j++) {
				for(int k = 0; k < 4*wiresCount; k++) {
					dst[j][k] = src[j][k];
				}
			}
		}
		for(int i = 0; i < conf.clbInIndexes.length; i++) {
			CLBBox box = clbs[i];
			byte[] src = conf.clbInIndexes[i];
			for(int j = 0; j < clbVariables; j++) {
				box.inConnectionIndexes[j] = src[j];
			}
			box.outConnectionIndex = conf.clbOutIndexes[i];
		}
		for(int i = 0; i < conf.pinIndexes.length; i++) {
			pins[i].connectionIndex = conf.pinIndexes[i];
			pins[i].input = conf.pinInput[i];
		}
	}

	public void clearWires() {
		for(WireSegment w : wires) {
			w.label = null;
		}
	}

	public static class LabelsFillResult {
		public int distinctMultiples;
		public int cumulativeMultiples;
	}
	
	public LabelsFillResult fillLabels() {
		LabelsFillResult result = new LabelsFillResult();
		for(Pin pin : pins) {
			if(pin.connectionIndex != -1 && pin.input) {
				fillLabel(pin, pin.wires[pin.connectionIndex], result);
			}
		}
		for(CLBBox box : clbs) {
			if(box.outConnectionIndex!=-1) {
				fillLabel(box, box.wiresOut[box.outConnectionIndex], result);
			}
		}
		return result;
	}

	public void fillLabel(Object label, WireSegment segment, LabelsFillResult result) {
		Set<WireSegment> visitedSet = new HashSet<>();
		List<WireSegment> newSet = new LinkedList<>();
		newSet.add(segment);
		boolean multipleFound = false;
		while(!newSet.isEmpty()) {
			WireSegment currentSegment = newSet.remove(0);
			visitedSet.add(currentSegment);
			if(currentSegment.label==null || currentSegment.label.equals(label)) {
				currentSegment.label=label;
			} else {
				currentSegment.label = WireSegment.MULTIPLE_LABELS;
				result.cumulativeMultiples++;
				multipleFound = true;
			}
			addSwitchedWires(newSet, visitedSet, currentSegment.firstBox, currentSegment.firstIndex);
			addSwitchedWires(newSet, visitedSet, currentSegment.secondBox, currentSegment.secondIndex);
		}
		if(multipleFound) {
			result.distinctMultiples++;
		}
	}
		
	private void addSwitchedWires(List<WireSegment> newSet, Set<WireSegment> visitedSet, SwitchBox box, int index) {
		byte[] rowToScan = null;
		for(int i = 0; i < 4*wiresCount; i++) {
			if(i==index) continue;
			if(box.configuration[index][i]==1) {
				WireSegment w = box.segments[i];
				if(w != null && !visitedSet.contains(w)) {
					newSet.add(w);
				}
				rowToScan = box.configuration[i];
				break;
			}
			if(box.configuration[index][i]==2) {
				WireSegment w = box.segments[i];
				if(w != null && !visitedSet.contains(w)) {
					newSet.add(w);
				}
			}
		}
		if(rowToScan!=null) {
			for(int i = 0; i < 4*wiresCount; i++) {
				if(rowToScan[i]==2) {
					WireSegment w = box.segments[i];
					if(w != null && !visitedSet.contains(w)) {
						newSet.add(w);
					}
				} else if(rowToScan[i]==1) {
					System.out.println("ERROR 1.");					
				}
			}
		}
	}

	public void simplify(FPGAModelConfiguration conf) {
		for(int bi = 0; bi < conf.switchBoxes.length; bi++) {
			byte[][] swconf = conf.switchBoxes[bi];
			for(int i = 0; i < swconf.length; i++) {
				for(int j = 0; j < swconf[i].length; j++) {
					if(swconf[i][j]==0) continue;
					if(switchBoxes[bi].segments[i]==null && switchBoxes[bi].segments[j]==null) {
						swconf[i][j] = 0;
						swconf[j][i] = 0;
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		FPGAModel model = new FPGAModel(2, 2, 2, 4, 2);
		FPGAModelConfiguration config = model.newConfiguration();
		config.pinIndexes[12] = 1;
		config.pinInput[12] = true;
		config.switchBoxes[3][1][4] = 2;
		config.switchBoxes[3][4][1] = 1;
		config.switchBoxes[4][12][11] = 2;
		config.switchBoxes[4][11][12] = 1;
		model.loadFromConfiguration(config);
		model.clearWires();
		model.fillLabels();
		Object label = model.clbs[3].wiresIn[3].label;
		System.out.println(label);
	}

}
