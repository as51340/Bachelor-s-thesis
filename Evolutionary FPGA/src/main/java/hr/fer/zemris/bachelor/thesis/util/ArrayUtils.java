package hr.fer.zemris.bachelor.thesis.util;

import java.util.HashSet;
import java.util.Set;

public class ArrayUtils {

	public static void printTwoArrays(byte[][] old, byte[][] newArr) {
		for (int i = 0; i < old.length; i++) {
			for (int j = 0; j < old[0].length; j++) {
				System.out.print(old[i][j] + " ");
			}
			System.out.print("     ");
			if (newArr == null) {
				System.out.println();
				continue;
			}
			for (int j = 0; j < newArr[0].length; j++) {
				System.out.print(newArr[i][j] + " ");
			}
			System.out.println();
		}
	}

	public static byte[][] copyArray(byte[][] old) {
		byte[][] newArr = new byte[old.length][old[0].length];
		for (int i = 0; i < old.length; i++) {
			for (int j = 0; j < old[0].length; j++) {
				newArr[i][j] = old[i][j];
			}
		}
		return newArr;
	}

	public static void printSwitchBox(byte[][] sw) {
		for (int i = 0; i < sw.length; i++) {
			for (int j = 0; j < sw[0].length; j++) {
				System.out.print(sw[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

	public static <T> boolean validate(T[] clbIndexes) {
		Set<T> visited = new HashSet<>();
		for(int i = 0; i < clbIndexes.length; i++) {
			if(!visited.add(clbIndexes[i])) return false;
		}
		return true;
	}
	
	public static boolean validateByte(byte[] arr) {
		Set<Byte> visited = new HashSet<>();
		for(int i = 0; i < arr.length; i++) {
			if(!visited.add(arr[i])) return false;
		}
		return true;
	}
	
	public static <T> void printArr(T[] arr) {
		for(int i = 0; i < arr.length; i++) System.out.print(arr[i] + " ");
		System.out.println();
	}

}
