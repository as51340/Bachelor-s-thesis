package hr.fer.zemris.bachelor.thesis.util;

import hr.fer.zemris.bachelor.thesis.models.InputData;

/**
 * Class that provides useful functions for obtaining number of segments,
 * junction boxes etc. Uses data from {@linkplain InputData} and
 * {@linkplain InputData} is created when user starts decomposition of function.
 * 
 * @author Andi Škrgat
 *
 */
public class ChipMathCalculator {

	/**
	 * Input data
	 */
	private InputData data;

	/**
	 * Simple constructor
	 * 
	 * @param data
	 */
	public ChipMathCalculator(InputData data) {
		this.data = data;
	}

	/**
	 * Returns number of extern pins 2(m + n)*io_per_segment
	 * 
	 * @return number of extern pins
	 */
	public int getNumberOfExternPins() {
		return 2 * (data.getClbCols() + data.getClbRows()) * data.getIoPinsPerSegment();
	}

	/**
	 * Returns number of junction boxes on some scheme (m+1)(n+1)
	 * 
	 * @return number of junction boxes
	 */
	public int getNumberOfJunctionBoxes() {
		return (data.getClbRows() + 1) * (data.getClbCols() + 1);
	}

	/**
	 * Returns number of segments for specified input 2mn + m + n
	 * 
	 * @return number of segments for specified input
	 */
	public int getNumberOfSegments() {
		return 2 * data.getClbRows() * data.getClbCols() + data.getClbRows() + data.getClbCols();
	}

	/**
	 * @return 4 for each one of four angles.
	 */
	public int getNumberOfTwoWayConnectedJunctionBoxes() {
		return 4;
	}

	/**
	 * @return number of three way connected junction boxes.
	 */
	public int getNumberOfThreeWayConnectedJunctionBoxes() {
		return 2*(data.getClbRows() + data.getClbCols() - 2);
	}

	/**
	 * @return number of four way connected junction boxes.
	 */
	public int getNumberOfFourWayConnectedJunctionBoxes() {
		return (data.getClbRows() - 1) * (data.getClbCols() - 1);
	}
}
