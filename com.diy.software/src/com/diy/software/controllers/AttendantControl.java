package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import com.diy.software.listeners.AttendantControlListener;
import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.OverloadException;
import com.jimmyselectronics.abagnale.IReceiptPrinter;
import com.jimmyselectronics.abagnale.ReceiptPrinterListener;
import com.unitedbankingservices.TooMuchCashException;
import com.unitedbankingservices.banknote.Banknote;
import com.unitedbankingservices.banknote.BanknoteStorageUnit;

import ca.ucalgary.seng300.simulation.SimulationException;

public class AttendantControl implements ActionListener, ReceiptPrinterListener {

	private StationControl sc;
	private ArrayList<AttendantControlListener> listeners;
	private Currency currency;
	String attendantNotifications;

	public AttendantControl(StationControl sc) {
		this.sc = sc;
		this.listeners = new ArrayList<>();
	}

	public void addListener(AttendantControlListener l) {
		listeners.add(l);
	}

	public void removeListener(AttendantControlListener l) {
		listeners.remove(l);
	}

	public void approveBagsAdded() {
		sc.unblockStation();
		for (AttendantControlListener l : listeners) {
			l.attendantApprovedBags(this);
		}
	}

	/**
	 * Allow attendant to shut down a station in order to do maintenance
	 *
	 * Precondition: The system is otherwise ready for customer interaction.
	 * The station to suspend is not in the midst of a customer session.
	 *
	 */
	public void preventStationUse(){
		sc.blockStation();
		for(AttendantControlListener l : listeners){
			l.attendantPreventUse(this);
		}
	}

	/**
	 * allow attendant to add paper to receipt printer
	 * adds 500 units of paper
	 * 
	 * precondition: printer is low on paper or out of paper
	 * 
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
	 * 
	 * @throws OverloadException if more ink than the printer can handle is added
	 */
	public void addInk() throws OverloadException {
		sc.station.printer.addInk(208000);
		for (AttendantControlListener l : listeners)
			l.printerNotLowState();
	}

	/**
	 * updates weight discrepancy message for attendant station
	 * 
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
	
	/*
	 * Updates banknotes in station to be used for change and notifies cash controller
	 * 
	 * @throws SimulationException
	 * 
	 * @throws TooMuchCashException
	 * 			Too much cash is loaded onto the storage
	 */
	public void adjustBanknotesForChange() throws SimulationException, TooMuchCashException {
		//Maybe change amount to add??
		int totalOnes = 20;
		int totalFives = 20;
		int totalTens = 20;
		int totalTwenties = 20;
		int totalFifties = 20;
		int totalHundreds = 20;

		Banknote one = new Banknote(currency, 1);
		Banknote five = new Banknote(currency, 5);
		Banknote ten = new Banknote(currency, 10);
		Banknote twenty = new Banknote(currency, 20);
		Banknote fifty = new Banknote(currency, 50);
		Banknote oneHundred = new Banknote(currency, 100);

		BanknoteStorageUnit unit = sc.station.banknoteStorage;
		
		sc.getCashControl().disablePayments();
		List<Banknote> unloadedBanknotes = unit.unload();
		sc.getCashControl().banknotesUnloaded(unit);	
		
		for(Banknote banknote : unloadedBanknotes) {
			if (banknote.getValue() == one.getValue()) {
				totalOnes--;
			}
			if (banknote.getValue() == five.getValue()) {
				totalFives--;
			}
			if (banknote.getValue() == ten.getValue()) {
				totalTens--;
			}
			if (banknote.getValue() == twenty.getValue()) {
				totalTwenties--;
			}
			if (banknote.getValue() == fifty.getValue()) {
				totalFifties--;
			}
			if (banknote.getValue() == oneHundred.getValue()) {
				totalHundreds--;
			}
		}
		
		for (int i = 0; i < totalOnes; i++) {
			unit.load(one);
		}
		for (int i = 0; i < totalFives; i++) {
			unit.load(five);
		}
		for (int i = 0; i < totalTens; i++) {
			unit.load(ten);
		}
		for (int i = 0; i < totalTwenties; i++) {
			unit.load(twenty);
		}
		for (int i = 0; i < totalFifties; i++) {
			unit.load(fifty);
		}
		for(int i = 0; i < totalHundreds; i++) {
			unit.load(oneHundred);
		}
				
		sc.getCashControl().banknotesLoaded(unit);
		sc.getCashControl().enablePayments();
	}

	/**
	 * based on the button clicked, the switch controls the GUI to react to user
	 * events
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
					addInk();
					break;
				case "addPaper":
					attendantNotifications = ("stations printer needs more paper!");
					addPaper();
					break;
				case "no_bagging":
					attendantNotifications = ("approved no bagging request");
					removeLastBaggedItem();
					break;
				case "prevent_use":
					attendantNotifications = ("Preventing use on station for maintenance");
					preventStationUse();
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
