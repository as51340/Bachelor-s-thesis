package hr.fer.zemris.bachelor.thesis.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import hr.fer.zemris.bachelor.thesis.models.Pair;

/**
 * Provides tests for parsing requirements.
 * @author Andi Škrgat
 *
 */
class RequirementsParserTest {


	@Test
	public void emptyString() {
		assertNull(InputUtil.readInputRequirements(""));
	}
	
	@Test
	public void oneObject() {
		String s = "A=>7";
		Pair<Map<String, Integer>,  Map<Integer, String>> result = InputUtil.readInputRequirements(s);
		Map<String, Integer> inputToPins = result.getObj1();
		Map<Integer, String> pinsToInput = result.getObj2();
		assertEquals(7, inputToPins.get("A"));
		assertEquals("A", pinsToInput.get(7));
	}
	
	@Test
	public void invalidInputTooManyLetters() {
		String s = "AVB=>7";
		assertThrows(IllegalArgumentException.class, () -> {
			InputUtil.readInputRequirements(s);
		});
	}
	
	@Test
	public void invalidInputNotALetter() {
		String s = "2=>7";
		assertThrows(IllegalArgumentException.class, () -> {
			InputUtil.readInputRequirements(s);
		});
	}
	
	@Test
	public void invalidPinNotConvertable() {
		String s = "A=>7A";
		assertThrows(IllegalArgumentException.class, () -> {
			InputUtil.readInputRequirements(s);
		});
	}
	
	@Test
	public void twoObjectsSpaceNotImportantAfterComa() {
		String s = "A=>7, B=>8";
		Pair<Map<String, Integer>,  Map<Integer, String>> result = InputUtil.readInputRequirements(s);
		Map<String, Integer> inputToPins = result.getObj1();
		Map<Integer, String> pinsToInput = result.getObj2();
		assertEquals(7, inputToPins.get("A"));
		assertEquals("A", pinsToInput.get(7));
		assertEquals(8, inputToPins.get("B"));
		assertEquals("B", pinsToInput.get(8));
	}
	
	@Test
	public void twoObjectsSpaceNotImportantAfterInput() {
		String s = "A =>7, B=>8";
		Pair<Map<String, Integer>,  Map<Integer, String>> result = InputUtil.readInputRequirements(s);
		Map<String, Integer> inputToPins = result.getObj1();
		Map<Integer, String> pinsToInput = result.getObj2();
		assertEquals(7, inputToPins.get("A"));
		assertEquals("A", pinsToInput.get(7));
		assertEquals(8, inputToPins.get("B"));
		assertEquals("B", pinsToInput.get(8));
	}
	
	
	@Test
	public void twoObjectsSpaceNotImportantAfterPin() {
		String s = "A=>7 , B=>8";
		Pair<Map<String, Integer>,  Map<Integer, String>> result = InputUtil.readInputRequirements(s);
		Map<String, Integer> inputToPins = result.getObj1();
		Map<Integer, String> pinsToInput = result.getObj2();
		assertEquals(7, inputToPins.get("A"));
		assertEquals("A", pinsToInput.get(7));
		assertEquals(8, inputToPins.get("B"));
		assertEquals("B", pinsToInput.get(8));
	}
	
	@Test
	public void invalidFormatOnlyComa() {
		String s = ",";
		assertThrows(IllegalArgumentException.class, () -> {
			InputUtil.readInputRequirements(s);
		});
	}
	
	@Test
	public void invalidFormatWithoutInput() {
		String s = "=>B";
		assertThrows(IllegalArgumentException.class, () -> {
			InputUtil.readInputRequirements(s);
		});
	}
	
	@Test
	public void invalidFormatWithoutPin() {
		String s = "=>B";
		assertThrows(IllegalArgumentException.class, () -> {
			InputUtil.readInputRequirements(s);
		});
	}
	
	@Test
	public void invalidFormatManyEqualities() {
		String s = "=>=>=>";
		assertThrows(IllegalArgumentException.class, () -> {
			InputUtil.readInputRequirements(s);
		});
	}
	
	
	@Test
	public void invalidFormatWithoutInputAndPin() {
		String s = "=>B";
		assertThrows(IllegalArgumentException.class, () -> {
			InputUtil.readInputRequirements(s);
		});
	}
	
	@Test
	public void invalidFormatRandomTextWithoutComas() {
		String s = "eufjsefjffnfff";
		assertThrows(IllegalArgumentException.class, () -> {
			InputUtil.readInputRequirements(s);
		});
	}
	
	@Test
	public void invalidFormatRandomTextWithComas() {
		String s = "eufjefjf,fnf,f,f";
		assertThrows(IllegalArgumentException.class, () -> {
			InputUtil.readInputRequirements(s);
		});
	}
	
	@Test
	public void correctTextWithEndlineAfterComa() {
		String s = "A=>7,\nB=>8";
		Pair<Map<String, Integer>,  Map<Integer, String>> result = InputUtil.readInputRequirements(s);
		Map<String, Integer> inputToPins = result.getObj1();
		Map<Integer, String> pinsToInput = result.getObj2();
		assertEquals(7, inputToPins.get("A"));
		assertEquals("A", pinsToInput.get(7));
		assertEquals(8, inputToPins.get("B"));
		assertEquals("B", pinsToInput.get(8));
	}
	
	@Test
	public void correctTextWithEndlineBeforeComa() {
		String s = "A=>7\n,B=>8";
		Pair<Map<String, Integer>,  Map<Integer, String>> result = InputUtil.readInputRequirements(s);
		Map<String, Integer> inputToPins = result.getObj1();
		Map<Integer, String> pinsToInput = result.getObj2();
		assertEquals(7, inputToPins.get("A"));
		assertEquals("A", pinsToInput.get(7));
		assertEquals(8, inputToPins.get("B"));
		assertEquals("B", pinsToInput.get(8));
	}
	
	@Test
	public void correctTextWithMoreEndlines() {
		String s = "A=>7\n\n\n,B=>8";
		Pair<Map<String, Integer>,  Map<Integer, String>> result = InputUtil.readInputRequirements(s);
		Map<String, Integer> inputToPins = result.getObj1();
		Map<Integer, String> pinsToInput = result.getObj2();
		assertEquals(7, inputToPins.get("A"));
		assertEquals("A", pinsToInput.get(7));
		assertEquals(8, inputToPins.get("B"));
		assertEquals("B", pinsToInput.get(8));
	}
	
	@Test
	public void correctTextWithEndlineAndCarriageReturn() {
		String s = "A=>7\n\r\n,B=>8";
		Pair<Map<String, Integer>,  Map<Integer, String>> result = InputUtil.readInputRequirements(s);
		Map<String, Integer> inputToPins = result.getObj1();
		Map<Integer, String> pinsToInput = result.getObj2();
		assertEquals(7, inputToPins.get("A"));
		assertEquals("A", pinsToInput.get(7));
		assertEquals(8, inputToPins.get("B"));
		assertEquals("B", pinsToInput.get(8));
	}
	
	@Test
	public void correctTextWithEndlineAndCarriageReturnAndTabs() {
		String s = "\tA=>7\t\n\r\n,B=>8\t";
		Pair<Map<String, Integer>,  Map<Integer, String>> result = InputUtil.readInputRequirements(s);
		Map<String, Integer> inputToPins = result.getObj1();
		Map<Integer, String> pinsToInput = result.getObj2();
		assertEquals(7, inputToPins.get("A"));
		assertEquals("A", pinsToInput.get(7));
		assertEquals(8, inputToPins.get("B"));
		assertEquals("B", pinsToInput.get(8));
	}
	
	

}
