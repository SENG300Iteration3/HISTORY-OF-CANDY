/*
 * Used to get what should be printed on the receipt
 * Information included:
 * -Individual Item descriptions (ordered alphabetically)
 * -Individual Item Prices
 * -Total Price
 * -Membership number (optional)
 * 
 * Note: items are not verified as valid since that is already taken care of when adding items to scanned list
 */

package com.diy.software.controllers;

import com.diy.hardware.BarcodedProduct;
import com.diy.software.listeners.AttendantControlListener;
import com.diy.software.listeners.MembershipControlListener;
import com.diy.software.listeners.ReceiptControlListener;
import com.diy.software.util.Tuple;
import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.EmptyException;
import com.jimmyselectronics.OverloadException;
import com.jimmyselectronics.abagnale.IReceiptPrinter;
import com.jimmyselectronics.abagnale.ReceiptPrinterListener;
import com.jimmyselectronics.abagnale.ReceiptPrinterND;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import swing.screens.AttendantStationScreen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class ReceiptControl implements ActionListener, ReceiptPrinterListener{
	private StationControl sc;
	private ArrayList<ReceiptControlListener> listeners;
	private ArrayList<Tuple<String, Double>> checkoutList = new ArrayList<>();
	private static final DecimalFormat formatPrice = new DecimalFormat("0.00");
	private int retreivedMemNum;
	public int currentPaperCount = 0;
	public int currentInkCount = 0;
	public int paperLowThreshold = 100;
	public int inkLowThreshold = 1000;
	public StringBuilder finalReceiptToShowOnScreen = new StringBuilder(); 
	
	public boolean outOfInk = false;
	public boolean outOfPaper = false;
	public boolean lowInk = false;
	public boolean lowPaper = false;
	
	public ReceiptControl(StationControl sc) {
		this.sc = sc;
		this.listeners = new ArrayList<>();
	}

	public void addListener(ReceiptControlListener l) {
		listeners.add(l);
	}
	
	// Resets the data to its initial state. 
	public void resetState() {
		retreivedMemNum = -1; //indicates did not recive member number
	}
	
	public void callTakeReceipt() {
		for (ReceiptControlListener l : listeners) {
			l.setTakeReceiptState(this);
		}
	}
	
	
	/**
	 * Finds what the contents of the receipt should be based on checked out items
	 */
	public void printItems() {
		String allItems = "";
		//for(Tuple<String, Double> item : sc.getItemsControl().getCheckoutList()) {
			// delete after merge conflict sort
		for(Tuple<String, Double> item : sc.getItemsControl().getItemDescriptionPriceList()) {
			printReceipt(item.x + " , $" + item.y + "\n");
			allItems = allItems.concat(item.x + " , $" + item.y + "\n");
		}
		for (ReceiptControlListener l : listeners) {
			l.setCheckedoutItems(this, allItems);
		}
	}
	
	/**
	 * Finds the total cost of all items checked out
	 */
	public void printTotalCost() {
		double total = 0.0;
		for(Tuple<String, Double> item : sc.getItemsControl().getItemDescriptionPriceList()) {
			total += item.y;
		}
		printReceipt("Total: $" + total + "\n");
		for (ReceiptControlListener l : listeners) {
			l.setTotalCost(this, "Total: $" + total + "\n");	
		}
	}
	
	/**
	 * If there was a valid membership number entered, find it and print it on the receipt
	 */
	public void printMembership() {
		retreivedMemNum = sc.getMembershipControl().getValidMembershipNumber();
		
		if(retreivedMemNum != -1) {
			printReceipt("Membership number: " + retreivedMemNum + "\n");
			for (ReceiptControlListener l : listeners) {
				l.setMembership(this, "Membership number: " + retreivedMemNum + "\n");	
			}
		}
		else {
			for (ReceiptControlListener l : listeners) {
				l.setMembership(this, "");	
			}
		}
		
	}
	
	/**
	 * prints date and time receipt was printed at
	 */
	public void printDateTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");  
	    Date receiptPrintDate = new Date();  
	    printReceipt(formatter.format(receiptPrintDate) + "\n");
		for (ReceiptControlListener l : listeners) {
			l.setDateandTime(this, formatter.format(receiptPrintDate) + "\n");	
		}
	}
	
	/**
	 * printing a thank you message
	 */
	public void printThankyouMsg() {
		if (retreivedMemNum == -1) {
			printReceipt("Thank you for shopping with us!\n");
			for (ReceiptControlListener l : listeners) {
				l.setThankyouMessage(this, "Thank you for shopping with us!\n");
			}
		}else {
			printReceipt("Thank you for shopping with us " + sc.getMembershipControl().memberName + " !\n");
			for (ReceiptControlListener l : listeners) {
				l.setThankyouMessage(this, "Thank you for shopping with us " + sc.getMembershipControl().memberName + " !\n");	
			}

		}
		
	}
	
	/**
	 * used to check if the printer is on low ink or low paper
	 */
	private void checkLowInkOrPaper() {
		if(currentInkCount <= inkLowThreshold) {
			this.lowInk(sc.station.printer);
		}
		if(currentPaperCount <= paperLowThreshold)
			this.lowPaper(sc.station.printer);
	}
	
	/**
	 * simulates printing the receipt to the customer based on what they purchased
	 * 
	 * @param receipt the customer receipt as a string
	 */
	public void printReceipt(String receipt) {
		try {
		checkLowInkOrPaper();
		for (char receiptChar : receipt.toCharArray()) {
				if(receiptChar == '\n') {
					--currentPaperCount;
				}
				if(!Character.isWhitespace(receiptChar)) {
					--currentInkCount;
				}
				
				try {
					sc.station.printer.print(receiptChar);
				} catch (OverloadException e) {
					try {
						sc.station.printer.print('\n');
						finalReceiptToShowOnScreen.append('\n');
						sc.station.printer.print(receiptChar);
					} catch (OverloadException e1) {}//it is not possible to enter this catch block
				}
				finalReceiptToShowOnScreen.append(receiptChar);
				for (ReceiptControlListener l : listeners) {
					l.setTakeReceiptState(this);
				}
			}
		
		} catch (EmptyException e) {
			sc.station.printer.cutPaper();
			for (ReceiptControlListener l : listeners) {
				l.setNoReceiptState(this);
				l.setIncompleteReceiptState(this);
			}
			finalReceiptToShowOnScreen.setLength(0);
		}
		
	}
	
	/**
	 * called after the customer completes removing an incomplete receipt
	 */
	public void removedIncompleteReceipt() {
		for (ReceiptControlListener l : listeners) {
			l.setNoIncompleteReceiptState(this);
		}
	}
	
	/**
	 * called after the customer completes removing a complete receipt
	 */
	public void removedCompleteReceipt() {
		for (ReceiptControlListener l : listeners) {
			l.setNoReceiptState(this);
			l.setNoIncompleteReceiptState(this);	
		}
		// need to refresh the station
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		try {
			switch (c) {
			case "printReceipt":
				printItems();
				printTotalCost();
				printMembership();
				printDateTime();
				printThankyouMsg();
				break;
			case "takeReceipt":
				sc.station.printer.cutPaper();
				sc.station.printer.removeReceipt();
				removedCompleteReceipt();
				break;
			case "takeIncompleteReceipt":
				sc.station.printer.cutPaper();
				sc.station.printer.removeReceipt();
				removedIncompleteReceipt();
				break;
			default:
				break;
			}
		} catch (Exception ex) {

		}
		
	}

	@Override
	public void outOfPaper(IReceiptPrinter printer) {
		outOfPaper = true;
		sc.getAttendantControl().outOfPaper(printer);
	}

	@Override
	public void outOfInk(IReceiptPrinter printer) {
		outOfInk = true;
		sc.getAttendantControl().outOfInk(printer);
	}

	@Override
	public void lowInk(IReceiptPrinter printer) {
		if(outOfInk) {
			this.outOfInk(printer);
		}else {
			lowInk = true;
			sc.getAttendantControl().lowInk(printer);
		}
	}

	@Override
	public void lowPaper(IReceiptPrinter printer) {
		if(outOfPaper) {
			this.outOfPaper(printer);
		}else {
			lowPaper = true;
			sc.getAttendantControl().lowPaper(printer);
		}
	}
	
	@Override
	public void inkAdded(IReceiptPrinter printer) {
		outOfInk = false;
		if(currentInkCount > inkLowThreshold) {
			lowInk = false;
		}
	}

	@Override
	public void paperAdded(IReceiptPrinter printer) {
		outOfPaper = false;
		if(currentPaperCount > paperLowThreshold) {
			lowPaper = false;
		}
	}
	
	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnedOn(AbstractDevice<? extends AbstractDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnedOff(AbstractDevice<? extends AbstractDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

}
