package com.jimmyselectronics.necchi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ca.powerutility.NoPowerException;

/**
 * This is a sample class that demonstrates how to interact with a
 * BarcodeScanner, registering instances of {@link SampleBarcodeScannerListener}
 * on it and checking that they have recorded the correct information.
 */
public class SampleBarcodeScannerDemo {
	private BarcodeScanner scanner;
	private BarcodedItem item;
	private Barcode barcode;
	private SampleBarcodeScannerListener listener1, listener2, listener3;

	/**
	 * Sets up the test suite. This is run before every test method.
	 */
	@Before
	public void setup() {
		scanner = new BarcodeScanner();
		barcode = new Barcode(new Numeral[] { Numeral.one, Numeral.two, Numeral.three, Numeral.four }); // 1234
		item = new BarcodedItem(barcode, 10); // The weight doesn't matter for this demo.

		// Create 3 listeners ... so you can see which ones receive events and which
		// don't.
		listener1 = new SampleBarcodeScannerListener("listener1");
		listener2 = new SampleBarcodeScannerListener("listener2");
		listener3 = new SampleBarcodeScannerListener("listener3");

		// Initialize the fields inside the listeners. Having these fields public would
		// be a bad idea in real code, but this is just a demo.
		listener1.device = null;
		listener1.barcode = null;
		listener2.device = null;
		listener2.barcode = null;
		listener3.device = null;
		listener3.barcode = null;

		// We'll register the first and second listeners, but not the third for now.
		scanner.register(listener1);
		scanner.register(listener2);

		// Plug-in the scanner, otherwise it wouldn't work.
		scanner.plugIn();
	}

	/**
	 * Turns on the scanner and scans an item. It is expected that the two
	 * registered listeners will record the information from each event, but the
	 * unregistered one will not. Tries a bunch of other stuff too, which a proper
	 * test case really should not.
	 */
	@Test
	public void demoScan() {
		// The scanner is plugged in, but it has to be turned on.
		scanner.turnOn();

		// The registered listeners should have recorded the information from the
		// "turnOn" event.
		assertEquals(scanner, listener1.device);
		assertEquals(scanner, listener2.device);
		assertEquals(null, listener3.device); // Not registered, so it shouldn't receive the event.

		// Nothing scanned yet.
		assertEquals(null, listener1.barcode);
		assertEquals(null, listener2.barcode);
		assertEquals(null, listener3.barcode);

		// Clear the recorded information.
		listener1.device = null;
		listener2.device = null;
		listener3.device = null;
		listener1.barcode = null;
		listener2.barcode = null;
		listener3.barcode = null;

		// Deregister the second listener; register the third listener.
		scanner.deregister(listener2);
		scanner.register(listener3);

		// Now we scan an item.
		scanner.scan(item);

		// The registered listeners should have recorded the information from the
		// "barcodeScanned" event.
		assertEquals(scanner, listener1.device);
		assertEquals(null, listener2.device); // Not registered, so it shouldn't receive the event.
		assertEquals(scanner, listener3.device);

		assertEquals(barcode, listener1.barcode);
		assertEquals(null, listener2.barcode); // Not registered, so it shouldn't receive the event.
		assertEquals(barcode, listener3.barcode);

		// Clear the recorded information.
		listener1.device = null;
		listener2.device = null;
		listener3.device = null;
		listener1.barcode = null;
		listener2.barcode = null;
		listener3.barcode = null;

		// Disable scanner THEN turn it off.
		scanner.disable();
		scanner.turnOff();

		boolean found = false;

		// Check for disabled cannot work when it is off.
		try {
			scanner.isDisabled();
		}
		catch(NoPowerException e) {
			found = true;
		}

		assertTrue(found);

		// Turn on scanner THEN enable it.
		scanner.turnOn();
		scanner.enable();

		assertFalse(scanner.isDisabled());
	}
}
