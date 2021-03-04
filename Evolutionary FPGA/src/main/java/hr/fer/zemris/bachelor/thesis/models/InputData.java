package hr.fer.zemris.bachelor.thesis.models;

public class InputData {

	private int clbEntries;
	
	private int clbRows;
	
	private int clbCols;
	
	private int wiresPerSegment;
	
	private int ioPinsPerSegment;
	
	public InputData() {}

	public InputData(int clbEntries, int clbRows, int clbCols, int wiresPerSegment, int ioPinsPerSegment) {
		super();
		this.clbEntries = clbEntries;
		this.clbRows = clbRows;
		this.clbCols = clbCols;
		this.wiresPerSegment = wiresPerSegment;
		this.ioPinsPerSegment = ioPinsPerSegment;
	}

	public int getClbEntries() {
		return clbEntries;
	}

	public void setClbEntries(int clbEntries) {
		this.clbEntries = clbEntries;
	}

	public int getClbRows() {
		return clbRows;
	}

	public void setClbRows(int clbRows) {
		this.clbRows = clbRows;
	}

	public int getClbCols() {
		return clbCols;
	}

	public void setClbCols(int clbCols) {
		this.clbCols = clbCols;
	}

	public int getWiresPerSegment() {
		return wiresPerSegment;
	}

	public void setWiresPerSegment(int wiresPerSegment) {
		this.wiresPerSegment = wiresPerSegment;
	}

	public int getIoPinsPerSegment() {
		return ioPinsPerSegment;
	}

	public void setIoPinsPerSegment(int ioPinsPerSegment) {
		this.ioPinsPerSegment = ioPinsPerSegment;
	}
	
	
}
