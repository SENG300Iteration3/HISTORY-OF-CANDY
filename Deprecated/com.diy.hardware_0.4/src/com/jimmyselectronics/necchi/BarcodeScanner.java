package com.jimmyselectronics.necchi;

import java.util.Random;

import com.jimmyselectronics.AbstractDevice;

import ca.powerutility.NoPowerException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/**
 * A complex device hidden behind a simple simulation. They can scan and that is
 * about all.
 * 
 * @author Jimmy's Electronics LLP
 */
public class BarcodeScanner extends AbstractDevice<BarcodeScannerListener> {
	/**
	 * Create a barcode scanner.
	 */
	public BarcodeScanner() {}

	private Random random = new Random();
	private static final int PROBABILITY_OF_FAILED_SCAN = 10; /* out of 100 */

	/**
	 * Simulates the customer's action of scanning an item. The result of the scan
	 * is only announced to any registered observers. Requires power.
	 * 
	 * @param item
	 *            The item to scan. Of course, it will only work if the item has a
	 *            barcode, and maybe not even then.
	 * @return true if the scan worked; otherwise, false.
	 */
	public synchronized boolean scan(BarcodedItem item) {
		if(!isPoweredUp())
			throw new NoPowerException();

		if(item == null)
			throw new NullPointerSimulationException("item");

		if(isDisabled())
			return false;

		if(random.nextInt(100) >= PROBABILITY_OF_FAILED_SCAN)
			notifyBarcodeScanned(item.getBarcode());
		else
			return false;
		
		return true;
	}

	private void notifyBarcodeScanned(Barcode barcode) {
		for(BarcodeScannerListener l : listeners())
			l.barcodeScanned(this, barcode);
	}
}
