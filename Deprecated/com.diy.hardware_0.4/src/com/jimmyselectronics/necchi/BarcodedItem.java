package com.jimmyselectronics.necchi;


import com.jimmyselectronics.Item;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Represents items for sale, each with a particular barcode and weight.
 * 
 * @author Jimmy's Electronics LLP
 */
public class BarcodedItem extends Item {
	private Barcode barcode;

	/**
	 * Basic constructor.
	 * 
	 * @param barcode
	 *            The barcode representing the identifier of the product of which
	 *            this is an item.
	 * @param weightInGrams
	 *            The real weight of the item.
	 * @throws SimulationException
	 *             If the barcode is null.
	 * @throws SimulationException
	 *             If the weight is &le;0.
	 */
	public BarcodedItem(Barcode barcode, double weightInGrams) {
		super(weightInGrams);

		if(barcode == null)
			throw new NullPointerSimulationException("barcode");

		this.barcode = barcode;
	}

	/**
	 * Gets the barcode of this item.
	 * 
	 * @return The barcode.
	 */
	public Barcode getBarcode() {
		return barcode;
	}
}
