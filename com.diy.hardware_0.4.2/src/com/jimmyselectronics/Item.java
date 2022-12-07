package com.jimmyselectronics;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;

/**
 * Abstract base class of items for sale, each with a particular weight.
 * 
 * @author Jimmy's Electronics LLP
 */
public abstract class Item {
	private double weightInGrams;

	/**
	 * Constructs an item with the indicated weight.
	 * 
	 * @param weightInGrams
	 *            The true weight of the item, in grams.
	 */
	protected Item(double weightInGrams) {
		if(weightInGrams <= 0.0)
			throw new InvalidArgumentSimulationException("The weight has to be positive.");

		this.weightInGrams = weightInGrams;
	}

	/**
	 * Reads the weight of the item, in grams.
	 * 
	 * @return The weight in grams.
	 */
	public double getWeight() {
		return weightInGrams;
	}
}
