package com.jimmyselectronics.necchi;


import com.jimmyselectronics.AbstractDeviceListener;

/**
 * Listens for events emanating from a barcode scanner.
 * 
 * @author Jimmy's Electronics LLP
 */
public interface BarcodeScannerListener extends AbstractDeviceListener {
	/**
	 * An event announcing that the indicated barcode has been successfully scanned.
	 * 
	 * @param barcodeScanner
	 *            The device on which the event occurred.
	 * @param barcode
	 *            The barcode that was read by the scanner.
	 */
	void barcodeScanned(BarcodeScanner barcodeScanner, Barcode barcode);

}
