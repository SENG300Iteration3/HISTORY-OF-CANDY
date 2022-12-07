package com.diy.hardware;

import com.jimmyselectronics.necchi.Barcode;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Represents products with barcodes. Such products always have prices per-unit.
 */
public class BarcodedProduct extends Product {
	private final Barcode barcode;
	private final String description;
	private final double expectedWeightInGrams;

	/**
	 * Create a product.
	 * 
	 * @param barcode
	 *            The barcode of the product.
	 * @param description
	 *            The description of the product.
	 * @param price
	 *            The price per-unit of the product.
	 * @param expectedWeightInGrams
	 *            The expected weight of each item of this product.
	 * @throws SimulationException
	 *             If any argument is null.
	 * @throws SimulationException
	 *             If the price is &le;0.
	 * @throws IllegalArgumentException
	 *             If the expected weight is &le;0.
	 */
	public BarcodedProduct(Barcode barcode, String description, long price, double expectedWeightInGrams) {
		super(price, true);

		if(barcode == null)
			throw new NullPointerSimulationException("barcode");

		if(description == null)
			throw new NullPointerSimulationException("description");

		if(expectedWeightInGrams <= 0.0)
			throw new IllegalArgumentException("Products have to have a positive expected weight.");
		
		this.barcode = barcode;
		this.description = description;
		this.expectedWeightInGrams = expectedWeightInGrams;
	}

	/**
	 * Get the barcode.
	 * 
	 * @return The barcode. Cannot be null.
	 */
	public Barcode getBarcode() {
		return barcode;
	}

	/**
	 * Get the description.
	 * 
	 * @return The description. Cannot be null.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Get the expected weight.
	 * 
	 * @return The expected weight in grams.
	 */
	public double getExpectedWeight() {
		return expectedWeightInGrams;
	}
}
