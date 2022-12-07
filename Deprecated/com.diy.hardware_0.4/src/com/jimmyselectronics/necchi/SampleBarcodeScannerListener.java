package com.jimmyselectronics.necchi;

import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;

/**
 * This is a sample of how one could create a class to listen for events
 * emanating from a barcode scanner. This sample only reacts to "turnedOn",
 * "turnedOff", and "barcodeScanned" events. You can see this class in action by
 * running the test suite in {@link SampleBarcodeScannerDemo}.
 */
public class SampleBarcodeScannerListener implements BarcodeScannerListener {
	/**
	 * Here, we will record the device on which an event occurs.
	 */
	public AbstractDevice<? extends AbstractDeviceListener> device = null;
	/**
	 * Here, we will record the barcode that has been scanned.
	 */
	public Barcode barcode = null;
	/**
	 * This is the name of this listener.
	 */
	public String name;

	/**
	 * Basic constructor.
	 * 
	 * @param name
	 *            The name to use for this listener.
	 */
	public SampleBarcodeScannerListener(String name) {
		this.name = name;
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		// We will ignore this kind of event
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		// We will ignore this kind of event
	}

	@Override
	public void turnedOn(AbstractDevice<? extends AbstractDeviceListener> device) {
		this.device = device;
		System.out.println(name + ": The barcode scanner has been turned on.");
	}

	@Override
	public void turnedOff(AbstractDevice<? extends AbstractDeviceListener> device) {
		this.device = device;
		System.out.println(name + ": The barcode scanner has been turned off.");
	}

	@Override
	public void barcodeScanned(BarcodeScanner barcodeScanner, Barcode barcode) {
		this.device = barcodeScanner;
		this.barcode = barcode;
		System.out.println(name + ": A barcode has been scanned: " + barcode.toString());
	}
}
