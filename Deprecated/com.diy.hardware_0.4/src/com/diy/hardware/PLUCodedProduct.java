package com.diy.hardware;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/**
 * Represents products with price-lookup (PLU) codes. Such products always have
 * prices per-kilogram.
 */
public class PLUCodedProduct extends Product {
	private final PriceLookUpCode pluCode;
	private final String description;

	/**
	 * Create a product.
	 * 
	 * @param pluCode
	 *            The PLU code of the product.
	 * @param description
	 *            The description of the product.
	 * @param price
	 *            The price per-kilogram of the product.
	 */
	public PLUCodedProduct(PriceLookUpCode pluCode, String description, long price) {
		super(price, false);

		if(pluCode == null)
			throw new NullPointerSimulationException("barcode");

		if(description == null)
			throw new NullPointerSimulationException("description");

		this.pluCode = pluCode;
		this.description = description;
	}

	/**
	 * Get the PLU code.
	 * 
	 * @return The PLU code. Cannot be null.
	 */
	public PriceLookUpCode getPLUCode() {
		return pluCode;
	}

	/**
	 * Get the description.
	 * 
	 * @return The description. Cannot be null.
	 */
	public String getDescription() {
		return description;
	}
}
