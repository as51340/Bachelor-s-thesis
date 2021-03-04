package hr.fer.zemris.bachelor.thesis.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.fer.zemris.bachelor.thesis.models.InputData;
import hr.fer.zemris.bachelor.thesis.models.Pair;

/**
 * Class that performs input operations - reads decomposed function(s), possible input requirements 
 * on pins and input data for FPGA structure.
 * @author Andi Škrgat
 *
 */
public class InputUtil {

	/**
	 * Read decomposed bool function. Probably I will need to parse it here.
	 * @return
	 */
	public static String readInputDecomposition() {
		return null;
	}
	
	/**
	 * User can specify inputs on some or all pins.
	 * @param requirements
	 * @return pair of two maps. First map contains pin mapped to input and second in reversed.
	 */
	public static Pair<Map<String, Integer>, Map<Integer, String>> readInputRequirements(String requirements) {
		if(requirements.trim().isEmpty()) return null;
		String cleanedReq = requirements.replace("\n", "").replace("\r", "");
		List<String> by_comas = Arrays.asList(cleanedReq.split(","));
		Map<String, Integer> inputToPin = new HashMap<String, Integer>();
		Map<Integer, String> pinToInput = new HashMap<Integer, String>();
		Pair<Map<String, Integer>, Map<Integer, String>> result = new Pair<Map<String,Integer>, Map<Integer,String>>();
		for(String obj: by_comas) {
			String[] splitted = obj.split("=>");
			if(splitted.length != 2) throw new IllegalArgumentException("No input and pin specified");
			String input = splitted[0];
			String pin = splitted[1];
			input = input.trim();
			pin = pin.trim();
			if(input.length() != 1 || !Character.isLetter(input.charAt(0))) {
				throw new IllegalArgumentException("Cannot parse input from requirements.");
			}
			int pinNum;
			try {
				pinNum = Integer.parseInt(pin);
			} catch(NumberFormatException ex) {
				throw new IllegalArgumentException("Cannot parse pin from requirements.");
			}
			inputToPin.put(input.toUpperCase(), pinNum);
			pinToInput.put(pinNum, input.toUpperCase());
		}
		if(by_comas.size() == 0) throw new IllegalArgumentException("Invalid requirements entered");
		result.setObj1(inputToPin);
		result.setObj2(pinToInput);
		return result; 
	}
	
	
	/**
	 * You will read it from somewhere
	 * @return InputData
	 */
	public static InputData readInputData() {
		return null;
		
		
	}
	
	
}
