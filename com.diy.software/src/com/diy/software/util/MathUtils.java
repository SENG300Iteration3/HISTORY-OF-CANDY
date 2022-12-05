package com.diy.software.util;

public class MathUtils {
	
	public static int clamp(int val, int min, int max) {
		return val > max ? max : val < min ? min : val;
	}
}
