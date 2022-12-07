package com.diy.hardware;

import com.jimmyselectronics.Item;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/**
 * Represents items for sale, each with a particular price-lookup code and weight.
 */
public class PLUCodedItem extends Item {
	private PriceLookUpCode pluCode;

	/**
	 * Basic constructor.
	 * 
	 * @param pluCode
	 *            The PLU code representing the identifier of the product of which
	 *            this is an item.
	 * @param weightInGrams
	 *            The actual weight of the item.
	 */
	public PLUCodedItem(PriceLookUpCode pluCode, double weightInGrams) {
		super(weightInGrams);
		
		if(pluCode == null)
			throw new NullPointerSimulationException("pluCode");

		this.pluCode = pluCode;
	}

	/**
	 * Gets the PLU code of this item.
	 * 
	 * @return The PLU code.
	 */
	public PriceLookUpCode getPLUCode() {
		return pluCode;
	}
}
