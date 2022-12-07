package com.jimmyselectronics.abagnale;

import com.jimmyselectronics.AbstractDeviceListener;

/**
 * Listens for events emanating from a receipt printer.
 * 
 * @author Jimmy's Electronics LLP
 */
public interface ReceiptPrinterListener extends AbstractDeviceListener {
	/**
	 * Announces that the indicated printer is out of paper.
	 * 
	 * @param printer
	 *            The device from which the event emanated.
	 */
	void outOfPaper(IReceiptPrinter printer);

	/**
	 * Announces that the indicated printer is out of ink.
	 * 
	 * @param printer
	 *            The device from which the event emanated.
	 */
	void outOfInk(IReceiptPrinter printer);

	/**
	 * Announces that the indicated printer is low on ink.
	 * 
	 * @param printer
	 *            The device from which the event emanated.
	 */
	void lowInk(IReceiptPrinter printer);

	/**
	 * Announces that the indicated printer is low on paper.
	 * 
	 * @param printer
	 *            The device from which the event emanated.
	 */
	void lowPaper(IReceiptPrinter printer);

	/**
	 * Announces that paper has been added to the indicated printer.
	 * 
	 * @param printer
	 *            The device from which the event emanated.
	 */
	void paperAdded(IReceiptPrinter printer);

	/**
	 * Announces that ink has been added to the indicated printer.
	 * 
	 * @param printer
	 *            The device from which the event emanated.
	 */
	void inkAdded(IReceiptPrinter printer);
}
