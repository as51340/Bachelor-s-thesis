package test.hr.fer.zemris.fpga;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import hr.fer.zemris.bool.SimpleFPGA;

public class TestLoading {

	public static void main(String[] args) throws IOException {
		String text = Files.readString(Paths.get("./decomp-example-02.txt"));
		
		SimpleFPGA sfpga = SimpleFPGA.parseFromText(text);
		
		System.out.println(sfpga.toString());
	}
}
