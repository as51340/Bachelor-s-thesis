package hr.fer.zemris.bachelor.thesis.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import hr.fer.zemris.bachelor.thesis.models.InputData;

/**
 * Class for testing mathematical operations from {@linkplain ChipMathCalculator}. Input below 1 is not allowed so we are not going to test it.
 * @author Andi Škrgat
 *
 */
class ChipMathCalculatorTest {

	@Test
	void numberOfExternPins() {
		InputData data = createInputData();
		ChipMathCalculator calc = new ChipMathCalculator(data);
		assertEquals(20 , calc.getNumberOfExternPins());	
	}
	
	@Test
	void numberOfJunctionBoxes() {
		InputData data = createInputData();
		ChipMathCalculator calc = new ChipMathCalculator(data);
		assertEquals(12 , calc.getNumberOfJunctionBoxes());	
	}
	
	@Test
	void numberOfSegments() {
		InputData data = createInputData();
		ChipMathCalculator calc = new ChipMathCalculator(data);
		assertEquals(17 , calc.getNumberOfSegments());	
	}
	
	@Test
	void numberOfTwoWayJunctionConnections() {
		InputData data = createInputData();
		ChipMathCalculator calc = new ChipMathCalculator(data);
		assertEquals(4, calc.getNumberOfTwoWayConnectedJunctionBoxes());
	}
	
	@Test
	void numberOfThreeWayJunctionConnections() {
		InputData data = createInputData();
		ChipMathCalculator calc = new ChipMathCalculator(data);
		assertEquals(6, calc.getNumberOfThreeWayConnectedJunctionBoxes());
	}
	
	@Test
	void numberOfFourWayJunctionConnections() {
		InputData data = createInputData();
		ChipMathCalculator calc = new ChipMathCalculator(data);
		assertEquals(2, calc.getNumberOfFourWayConnectedJunctionBoxes());
	}
	
	/**
	 * @return {@linkplain InputData} for testing
	 */
	private InputData createInputData() {
		InputData data = new InputData();
		data.setClbRows(3);
		data.setClbCols(2);
		data.setClbEntries(2);
		data.setWiresPerSegment(4);
		data.setIoPinsPerSegment(2);
		return data;
	}

}
