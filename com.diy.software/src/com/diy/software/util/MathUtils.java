package com.diy.software.util;

public class MathUtils {
	
	// Force a value within a given range
	public static int clamp(int val, int min, int max) {
		return val > max ? max : val < min ? min : val;
	}
}
