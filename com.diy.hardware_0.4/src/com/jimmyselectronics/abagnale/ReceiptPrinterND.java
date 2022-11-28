package com.jimmyselectronics.abagnale;

import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.EmptyException;
import com.jimmyselectronics.OverloadException;

import ca.powerutility.NoPowerException;
import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Represents printers used for printing receipts. A printer has a finite amount
 * of paper (measured in lines that can be printed) and ink (measured in
 * characters that can be printed).
 * <p>
 * Since this is a simulation, each character is assumed to require the same
 * amount of ink (except blanks and newlines) and the font size is fixed.
 * </p>
 * 
 * @author Jimmy's Electronics LLP
 */
public class ReceiptPrinterND extends AbstractDevice<ReceiptPrinterListener> implements IReceiptPrinter {
	/**
	 * Represents the maximum amount of ink that the printer can hold, measured in
	 * printable-character units.
	 */
	public static final int MAXIMUM_INK = 1 << 20;
	/**
	 * Represents the maximum amount of paper that the printer can hold, measured in
	 * lines.
	 */
	public static final int MAXIMUM_PAPER = 1 << 10;
	private int charactersOfInkRemaining = 0;
	private int linesOfPaperRemaining = 0;
	private StringBuilder sb = new StringBuilder();
	private int charactersOnCurrentLine = 0;

	/**
	 * Represents the maximum number of characters that can fit on one line of the
	 * receipt. This is a simulation, so the font is assumed monospaced and of fixed
	 * size.
	 */
	public final static int CHARACTERS_PER_LINE = 60;

	/**
	 * Creates a receipt printer.
	 */
	public ReceiptPrinterND() {}

	/**
	 * Prints a single character to the receipt. Whitespace characters are ignored,
	 * with the exception of ' ' (blank) and '\n', which signals to move to the
	 * start of the next line. Requires power.
	 * 
	 * @param c
	 *            The character to print.
	 * @throws EmptyException
	 *             If there is no ink or no paper in the printer.
	 * @throws OverloadException
	 *             If the extra character would spill off the end of the line.
	 */
	@Override
	public synchronized void print(char c) throws EmptyException, OverloadException {
		if(!isPoweredUp())
			throw new NoPowerException();

		if(c == '\n') {
			--linesOfPaperRemaining;
			charactersOnCurrentLine = 0;
		}
		else if(c != ' ' && Character.isWhitespace(c))
			return;
		else if(charactersOnCurrentLine == CHARACTERS_PER_LINE)
			throw new OverloadException("The line is too long. Add a newline");
		else if(linesOfPaperRemaining == 0)
			throw new EmptyException("There is no paper in the printer.");
		else
			charactersOnCurrentLine++;

		if(!Character.isWhitespace(c)) {
			if(charactersOfInkRemaining == 0)
				throw new EmptyException("There is no ink in the printer");

			charactersOfInkRemaining--;
		}

		sb.append(c);

		if(charactersOfInkRemaining == 0)
			notifyOutOfInk();

		if(linesOfPaperRemaining == 0)
			notifyOutOfPaper();
	}

	/**
	 * The receipt is finished printing, so cut it so that the customer can easily
	 * remove it. Failure to cut the paper means that the receipt will not be
	 * retrievable by the customer. Requires power.
	 */
	@Override
	public synchronized void cutPaper() {
		if(!isPoweredUp())
			throw new NoPowerException();

		lastReceipt = sb.toString();
	}

	private String lastReceipt = null;

	/**
	 * Simulates the customer removing the receipt. Failure to cut the receipt
	 * first, or to always remove the receipt means that the customer will end up
	 * with other customers' receipts too! Does not require power.
	 * 
	 * @return The receipt if it has been cut; otherwise, null.
	 */
	@Override
	public synchronized String removeReceipt() {
		String receipt = lastReceipt;

		if(lastReceipt != null) {
			lastReceipt = null;
			sb = new StringBuilder();
		}
		else
			throw new InvalidArgumentSimulationException("A non-existent receipt cannot be removed.");

		return receipt;
	}

	/**
	 * Adds ink to the printer. Simulates a human doing the adding. On success,
	 * announces "inkAdded" event. Requires power.
	 * 
	 * @param quantity
	 *            The quantity of characters-worth of ink to add.
	 * @throws SimulationException
	 *             If the quantity is negative.
	 * @throws OverloadException
	 *             If the total of the existing ink plus the added quantity is
	 *             greater than the printer's capacity.
	 */
	@Override
	public synchronized void addInk(int quantity) throws OverloadException {
		if(!isPoweredUp())
			throw new NoPowerException();

		if(quantity < 0)
			throw new InvalidArgumentSimulationException("Are you trying to remove ink?");

		if(charactersOfInkRemaining + quantity > MAXIMUM_INK)
			throw new OverloadException("You spilled a bunch of ink!");

		if(quantity > 0) {
			charactersOfInkRemaining += quantity;
			notifyInkAdded();
		}
	}

	/**
	 * Adds paper to the printer. Simulates a human doing the adding. On success,
	 * announces "paperAdded" event. Requires power.
	 * 
	 * @param units
	 *            The quantity of lines-worth of paper to add.
	 * @throws SimulationException
	 *             If the quantity is negative.
	 * @throws OverloadException
	 *             If the total of the existing paper plus the added quantity is
	 *             greater than the printer's capacity.
	 */
	@Override
	public synchronized void addPaper(int units) throws OverloadException {
		if(!isPoweredUp())
			throw new NoPowerException();

		if(units < 0)
			throw new InvalidArgumentSimulationException("Are you trying to remove paper?");

		if(linesOfPaperRemaining + units > MAXIMUM_PAPER)
			throw new OverloadException("You may have broken the printer, jamming so much in there!");

		if(units > 0) {
			linesOfPaperRemaining += units;
			notifyPaperAdded();
		}
	}

	private void notifyOutOfInk() {
		for(ReceiptPrinterListener l : listeners())
			l.outOfInk(this);
	}

	private void notifyInkAdded() {
		for(ReceiptPrinterListener l : listeners())
			l.inkAdded(this);
	}

	private void notifyOutOfPaper() {
		for(ReceiptPrinterListener l : listeners())
			l.outOfPaper(this);
	}

	private void notifyPaperAdded() {
		for(ReceiptPrinterListener l : listeners())
			l.paperAdded(this);
	}
}
