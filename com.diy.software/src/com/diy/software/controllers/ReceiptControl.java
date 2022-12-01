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
import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.abagnale.IReceiptPrinter;
import com.jimmyselectronics.abagnale.ReceiptPrinterListener;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import swing.panes.AttendantStationPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

public class ReceiptControl implements ActionListener, ReceiptPrinterListener{
	private StationControl sc;
	private ArrayList<ReceiptControlListener> listenersReceipt;
	private ArrayList<AttendantControlListener> listenersAttendant;
	private static final DecimalFormat formatPrice = new DecimalFormat("0.00");
	
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
	
	
	/**
	 * Finds what the contents of the receipt should be when there is a member number inputed
	 */
	private void printItems() {
		String testPrint = "asfdfhfg";
		sc.printReceipt(testPrint);
		System.out.println("test print receipt");
	}

	/*
	 * Finds what the contents of the receipt should be when there is a member number inputed
	 * 
	 * @param items 
	 * 		The ScannedList of items to put on the receipt
	 * @param memberNumber 
	 * 		The memberNumer to put on the receipt
	 * @return
	 * 		The full receipt as a string
	 */
//    public static String fullReceipt(ScannedList items, int memberNumber){
//        String receipt = "";
//        double totalCost = 0;   
//        BarcodedProduct AlphabeticalItems[]=new BarcodedProduct[ScannedList.CART.size()];
//        
//        //checking if membership number is valid
//        if(!MembershipDatabaseDemo.MEMBERSHIP_DATABASE.contains(memberNumber)) {
//        	throw new InvalidArgumentSimulationException("Invalid membership number");
//        }
//        
//        //sorting the items so they show up alphabetically on the receipt (needed for testing)
//        int len = ScannedList.CART.size();
//        int i = 0;
//        for(BarcodedProduct key : ScannedList.CART.keySet()) {
//        	AlphabeticalItems[i] = key;
//        	i++;
//        }
//        for(int j = 0; j < len; j++){
//        	for (int k = j + 1; k < len; k++) {
//        		/*
//        		 * NOTE: There is a small chance that 100% branch coverage will not be gotten on the below statement
//        		 * since it is sorting the elements stored in a hashmap in alphabetical order. Since a hashmap is ordered
//        		 * randomly there is a chance that all the products in the hashmap will already be in alphabetical order
//        		 * (or reverse alphabetical order), meaning the if statement will always pass or fail.
//        		 * 
//        		 * Please rerun the test if occurs
//        		 */
//                if(AlphabeticalItems[j].getDescription().compareTo(AlphabeticalItems[k].getDescription()) > 0) { 
//                	BarcodedProduct temp = AlphabeticalItems[j];  
//                    AlphabeticalItems[j] = AlphabeticalItems[k];  
//                    AlphabeticalItems[k] = temp; 
//                }
//        	}
//        }
//        
//        //finding what the receipt should print
//        for (int j = 0; j < len; j++){
//            String decription = AlphabeticalItems[j].getDescription();
//            Double price = ScannedList.CART.get(AlphabeticalItems[j]);
//            //price = price/100;
//
//            receipt += decription + "\n";
//            receipt += "$" + formatPrice.format(price) + "\n";
//            totalCost += price;
//        }
//
//        receipt += "\nTotal Cost: $" + formatPrice.format(totalCost) + "\n";
//
//        receipt += "Membership Number: " + memberNumber + "\n";
//
//        receipt += "Thank you for shopping with us!\n";
//
//        return receipt;
//    }

	/*
	 * Finds what the contents of the receipt should be when there is not a member number inputed
	 * 
	 * @param items 
	 * 		The ScannedList of items to put on the receipt
	 * @return
	 * 		The full receipt as a string
	 */
//    public static String fullReceipt(ScannedList items){
//        String receipt = "";
//        double totalCost = 0;
//        BarcodedProduct AlphabeticalItems[]=new BarcodedProduct[ScannedList.CART.size()];
//        
//        //sorting the items so they show up alphabetically on the receipt (needed for testing)
//        int len = ScannedList.CART.size();
//        int i = 0;
//        for(BarcodedProduct key : ScannedList.CART.keySet()) {
//        	AlphabeticalItems[i] = key;
//        	i++;
//        }
//        for(int j = 0; j < len; j++){
//        	for (int k = j + 1; k < len; k++) {
//        		/*
//        		 * NOTE: There is a small chance that 100% branch coverage will not be gotten on the below statement
//        		 * since it is sorting the elements stored in a hashmap in alphabetical order. Since a hashmap is ordered
//        		 * randomly there is a chance that all the products in the hashmap will already be in alphabetical order
//        		 * (or reverse alphabetical order), meaning the if statement will always pass or fail.
//        		 * 
//        		 * Please rerun the test if occurs
//        		 */
//                if(AlphabeticalItems[j].getDescription().compareTo(AlphabeticalItems[k].getDescription()) > 0) { 
//                	BarcodedProduct temp = AlphabeticalItems[j];  
//                    AlphabeticalItems[j] = AlphabeticalItems[k];  
//                    AlphabeticalItems[k] = temp; 
//                }
//        	}
//        }
//        
//        //finding what the receipt should print
//        for (int j = 0; j < len; j++){
//            String decription = AlphabeticalItems[j].getDescription();
//            Double price = ScannedList.CART.get(AlphabeticalItems[j]);
//            //price = price/100;
//
//            receipt += decription + "\n";
//            receipt += "$" + formatPrice.format(price) + "\n";
//            totalCost += price;
//        }
//
//        receipt += "\nTotal Cost: $" + formatPrice.format(totalCost) + "\n";
//
//        receipt += "Thank you for shopping with us!\n";
//
//        return receipt;
//    }

	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		try {
			switch (c) {
			case "printReceipt":
				System.out.println("Customer prints receipt");
				printItems();
//				printTotalCost();
//				printMembership();
//				printDateTime();
//				printThankyouMsg();
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void outOfInk(IReceiptPrinter printer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lowInk(IReceiptPrinter printer) {
		for (AttendantControlListener la : listenersAttendant)
			la.addInkState();	
	}

	@Override
	public void lowPaper(IReceiptPrinter printer) {
		for (AttendantControlListener la : listenersAttendant)
			la.addPaperState();
	}

	@Override
	public void paperAdded(IReceiptPrinter printer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inkAdded(IReceiptPrinter printer) {
		// TODO Auto-generated method stub
		
	}


}
