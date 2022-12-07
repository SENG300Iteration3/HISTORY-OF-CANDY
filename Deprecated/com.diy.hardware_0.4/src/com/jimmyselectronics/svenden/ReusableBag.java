package com.jimmyselectronics.svenden;

import com.jimmyselectronics.Item;

/**
 * Represents a single, reusable bag for carrying groceries.
 * 
 * @author Jimmy's Electronics LLP
 */
public class ReusableBag extends Item {
	private static final double idealWeightInGrams = 5.0;

	/**
	 * Default constructor.
	 */
	public ReusableBag() {
		super(idealWeightInGrams);
	}
}
