package com.diy.hardware;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Abstract base class for products. Note that a "product" is the <b>kind</b> of
 * item (e.g., 2 litre container of Dairyland brand 2% milk) and not an
 * individual item, which would be the specific physical object (e.g.,
 * <b>that</b> bottle of milk and not <b>this</b> one).
 */
public abstract class Product {
	private final long price;
	private final boolean isPerUnit;

	/**
	 * Create a product instance.
	 * 
	 * @param price
	 *            The price per unit or per kilogram.
	 * @param isPerUnit
	 *            True if the price is per unit; false if it is per kilogram.
	 * @throws SimulationException
	 *             If the price is null or &le;0.
	 */
	protected Product(long price, boolean isPerUnit) {
		if(price <= 0L)
			throw new InvalidArgumentSimulationException("A product's price can only be positive.");

		this.price = price;
		this.isPerUnit = isPerUnit;
	}

	/**
	 * Gets the price of the product.
	 * 
	 * @return The price. Cannot be null. Must be &gt;0.
	 */
	public long getPrice() {
		return price;
	}

	/**
	 * Tests whether the price is per-unit, as opposed to per-kilogram.
	 * 
	 * @return true if the price is per-unit; otherwise, false.
	 */
	public boolean isPerUnit() {
		return isPerUnit;
	}
}
