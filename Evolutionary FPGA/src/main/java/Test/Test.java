package Test;

import hr.fer.zemris.fpga.gui.FPGAComponent;

public class Test {

	public static void main(String[] args) {
		System.out.println("Hello World!");
		byte a = 65;
		System.out.println(a);
		String aa = "A" ;//every letter in string has one byte so store letters in byte array;
		System.out.println(aa.getBytes().length);
	}

}
