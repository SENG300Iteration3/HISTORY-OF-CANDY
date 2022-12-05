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

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import swing.panes.AttendantStationPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class ReceiptControl implements ActionListener, ReceiptPrinterListener{
	private StationControl sc;
	private ArrayList<ReceiptControlListener> listenersReceipt;
	private ArrayList<AttendantControlListener> listenersAttendant;
	private ArrayList<Tuple<String, Double>> checkoutList = new ArrayList<>();
	private static final DecimalFormat formatPrice = new DecimalFormat("0.00");
	private int retreivedMemNum;
	public int currentPaperCount = 0;
	public int currentInkCount = 0;
	
	public boolean outOfInk = false;
	public boolean outOfPaper = false;
	
	public ReceiptControl (StationControl sc) {
		this.sc = sc;
		this.listenersReceipt = new ArrayList<>();
		this.listenersAttendant = new ArrayList<>();
	}

	public void addListenerReceipt(ReceiptControlListener lr) {
		listenersReceipt.add(lr);
	}
	
	public void addListenerAttendant(AttendantControlListener la) {
		listenersAttendant.add(la);
	}
	// Resets the data to its initial state. 
	//TODO delete for later if not needed for GUI
	public void resetState() {
		retreivedMemNum = -1; // can't use null, gonnause this.
	}
	
	
	/**
	 * Finds what the contents of the receipt should be based on checked out items
	 */
	public void printItems() {
		for(Tuple<String, Double> item : sc.getItemsControl().getCheckoutList()) {
			System.out.println(item.x + " , $" + item.y);
			printReceipt(item.x + " , $" + item.y + "\n");
		}
		//System.out.println("test print receipt");
	}
	
	/**
	 * Finds the total cost of all items checked out
	 */
	public void printTotalCost() {
		double total = 0.0;
		for(Tuple<String, Double> item : sc.getItemsControl().getCheckoutList()) {
			total += item.y;
		}
		System.out.println("Total: $" + total);
		printReceipt("Total: $" + total + "\n");
	}
	
	/**
	 * If there was a valid membership number entered, find it and print it on the receipt
	 */
	public void printMembership() {
		retreivedMemNum = sc.getMembershipControl().getValidMembershipNumber();
		//String retreivedMemName = sc.getMembershipControl().memberName;
		
		if(retreivedMemNum == -1) {
			// Do nothing
			//System.out.println("No member found");
			//printReceipt("No member found");
		}else {
			System.out.println("Membership number: " + retreivedMemNum);
			printReceipt("Membership number: " + retreivedMemNum + "\n");
		}
		
	}
	
	/**
	 * prints date and time receipt was printed at
	 */
	public void printDateTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");  
	    Date receiptPrintDate = new Date();  
	    System.out.println(formatter.format(receiptPrintDate));
	    printReceipt(formatter.format(receiptPrintDate) + "\n");
	}
	
	/**
	 * printing a thank you message
	 */
	public void printThankyouMsg() {
		if (retreivedMemNum == -1) {
			//System.out.println("Thank you for shopping with us!");
			printReceipt("Thank you for shopping with us!\n");
		}else {
			//System.out.println("Thank you for shopping with us " + sc.getMembershipControl().memberName + " !");
			printReceipt("Thank you for shopping with us " + sc.getMembershipControl().memberName + " !\n");

		}
		
	}
	
	/**
	 * used to check if the printer is on low ink or low paper
	 */
	private void checkLowInkNPaper() {
		if(currentInkCount <= 1000)
			this.lowInk(sc.station.printer);
		else if(currentPaperCount <= 100)
			this.lowPaper(sc.station.printer);
	}
	
	/**
	 * simulates printing the receipt to the customer based on what they purchased
	 * 
	 * @param receipt the customer receipt as a string
	 */
	private void printReceipt(String receipt) {
		try {
		checkLowInkNPaper();
		for (char receiptChar : receipt.toCharArray()) {	
				sc.station.printer.print(receiptChar);
				for (ReceiptControlListener l : listenersReceipt) {
					l.setTakeReceiptState(this);
				}
			}
		
		} catch (EmptyException e) {
			sc.station.printer.cutPaper();
			for (ReceiptControlListener l : listenersReceipt) {
				l.setNoReceiptState(this);
				l.setIncompleteReceiptState(this);
			}
		} catch (OverloadException e) {
			
		}
		
	}
	
	/**
	 * called after the customer completes removing an incomplete receipt
	 */
	public void removedIncompleteReceipt() {
		for (ReceiptControlListener l : listenersReceipt) {
			l.setNoIncompleteReceiptState(this);
		}
	}
	
	/**
	 * called after the customer completes removing a complete receipt
	 */
	public void removedCompleteReceipt() {
		for (ReceiptControlListener l : listenersReceipt) {
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
				System.out.println("Customer prints receipt");
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

	@Override
	public void outOfPaper(IReceiptPrinter printer) {
		outOfPaper = true;
		System.out.println("out of paper");
		sc.getAttendantControl().outOfPaper(printer);
	}

	@Override
	public void outOfInk(IReceiptPrinter printer) {
		outOfInk = true;
		System.out.println("out of ink");
		sc.getAttendantControl().outOfInk(printer);
	}

	@Override
	public void lowInk(IReceiptPrinter printer) {
		System.out.println("RC low ink");
		if(outOfInk) {
			this.outOfInk(printer);
		}else {
			sc.getAttendantControl().lowInk(printer);
		}
	}

	@Override
	public void lowPaper(IReceiptPrinter printer) {
		System.out.println("RC low paper");
		if(outOfPaper) {
			this.outOfPaper(printer);
		}else {
			sc.getAttendantControl().lowPaper(printer);
		}
	}

	@Override
	public void paperAdded(IReceiptPrinter printer) {
		outOfPaper = false;
	}

	@Override
	public void inkAdded(IReceiptPrinter printer) {
		outOfInk = false;
	}

}
