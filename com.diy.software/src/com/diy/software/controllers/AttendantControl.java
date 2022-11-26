package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import com.diy.software.listeners.AttendantControlListener;
import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.OverloadException;
import com.jimmyselectronics.abagnale.IReceiptPrinter;
import com.jimmyselectronics.abagnale.ReceiptPrinterListener;

public class AttendantControl implements ActionListener, ReceiptPrinterListener {
	private SystemControl sc;
	private ArrayList<AttendantControlListener> listeners;
	String	attendantNotifications;
	private boolean noBaggingApproved = false;

	public AttendantControl(SystemControl sc) {
		this.sc = sc;
		this.listeners = new ArrayList<>();
	}

	public void addListener(AttendantControlListener l) {
		listeners.add(l);
	}

	public void removeListener(AttendantControlListener l) {
		listeners.remove(l);
	}

	/*
	 * FIXME: Assumes the bags are outside of the customer's shopping cart To
	 * implement with weight the bags must be in the customer's shopping cart to be
	 * weighted on the scale
	 */
	public void approveBagsAdded() {
		sc.unblockStation();
		for (AttendantControlListener l : listeners) {
			l.attendantApprovedBags(this);
		}
	}
	
	/**
	 * allow attendant to add paper to receipt printer
	 * adds 500 units of paper
	 * 
	 * precondition: printer is low on paper or out of paper
	 * @throws OverloadException too much paper added, printer cant handle it
	 */
	public void addPaper() throws OverloadException {
		sc.station.printer.addPaper(500);
		for (AttendantControlListener l : listeners)
			l.printerNotLowState();
	}
	
	/**
	 * allow attendant to add ink to receipt printer
	 * adds 2000 characters worth of ink
	 * 
	 * precondition: printer is low on ink or out of ink
	 * @throws OverloadException if more ink than the printer can handle is added
	 */
	public void addink() throws OverloadException {
		sc.station.printer.addInk(208000);
		for (AttendantControlListener l : listeners)
			l.printerNotLowState();
	}
	
	/**
	 * updates weight discrepancy message for attendant station
	 * @param updateMessage
	 */
	public void updateWeightDescrepancyMessage(String updateMessage) {
		for (AttendantControlListener l : listeners)
			l.signalWeightDescrepancy(updateMessage);
	}
	
	/**
	 * removes the last bagged item through the approval of no bagging request
	 */
	public void removeLastBaggedItem() {
		sc.getItemsControl().removeLastBaggedItem();
		for (AttendantControlListener l : listeners)
			l.initialState();
	}
	
	/**
	 * based on the button clicked, the switch controls the GUI to react to user events
	 * 
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		try {
			switch (c) {
			case "approve added bags":
				System.out.println("Attendant approved added bags");
				approveBagsAdded();
				break;
			case "addInk":
				attendantNotifications = ("stations printer needs more ink!");
				addink();
				break;
			case "addPaper":
				attendantNotifications = ("stations printer needs more paper!");
				addPaper();
				break;
			case "no_bagging":
				attendantNotifications = ("approved no bagging request");
				removeLastBaggedItem();
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
		for (AttendantControlListener l : listeners)
			l.addPaperState();
		
	}

	@Override
	public void outOfInk(IReceiptPrinter printer) {
		for (AttendantControlListener l : listeners)
			l.addInkState();
		
	}

	@Override
	public void lowInk(IReceiptPrinter printer) {
		for (AttendantControlListener l : listeners)
			l.addInkState();
		
	}

	@Override
	public void lowPaper(IReceiptPrinter printer) {
		for (AttendantControlListener l : listeners)
			l.addPaperState();
		
	}

	@Override
	public void paperAdded(IReceiptPrinter printer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inkAdded(IReceiptPrinter printer) {
		// TODO Auto-generated method stub
		
	}

	public void approveNoBaggingRequest() {
		for (AttendantControlListener l : listeners)
			l.noBaggingRequestState();
		
	}
}
