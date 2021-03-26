package Test;

import java.util.LinkedHashMap;
import java.util.Map;

import hr.fer.zemris.fpga.gui.FPGAComponent;

public class Test {

	public static void main(String[] args) {
		Map<Integer, Integer> maps = new LinkedHashMap();
		for(int i = 0; i < 10; i++) {
			maps.put(i, null);
		}
		for(var v: maps.entrySet()) {
			v.setValue(v.getKey() + 1);
		}
		for(var v: maps.entrySet()) {
			System.out.println(v.getKey() + " " + v.getValue());
		}
	}

}
