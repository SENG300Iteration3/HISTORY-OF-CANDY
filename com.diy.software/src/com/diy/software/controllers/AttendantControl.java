package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.HashMap;
import java.util.Objects;

import com.diy.software.listeners.AttendantControlListener;
import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.OverloadException;
import com.jimmyselectronics.abagnale.IReceiptPrinter;
import com.jimmyselectronics.abagnale.ReceiptPrinterListener;
import com.unitedbankingservices.TooMuchCashException;
import com.unitedbankingservices.banknote.Banknote;
import com.unitedbankingservices.banknote.BanknoteStorageUnit;
import com.unitedbankingservices.coin.Coin;
import com.unitedbankingservices.coin.CoinStorageUnit;

import ca.ucalgary.seng300.simulation.SimulationException;

public class AttendantControl implements ActionListener, ReceiptPrinterListener {

	private StationControl sc;
	private ArrayList<AttendantControlListener> listeners;
	private CoinStorageUnit unit;
	private Currency currency;
	String attendantNotifications;
	
	
	public static final ArrayList<String> logins = new ArrayList<String>();
	
	public void login(String password) {
		for (AttendantControlListener l : listeners) {
			l.loggedIn(logins.contains(password));
		}
	}

	public AttendantControl(StationControl sc) {
		this.sc = sc;
		this.listeners = new ArrayList<>();

		this.currency = Currency.getInstance("CAD");
	}

	public void addListener(AttendantControlListener l) {
		listeners.add(l);
	}

	public void removeListener(AttendantControlListener l) {
		listeners.remove(l);
	}
	
	
	// allow attendant to enable customer station use after it has been suspended
	public void permitStationUse() {
		sc.unblockStation();
		for (AttendantControlListener l : listeners) {
			l.attendantPermitStationUse(this);
		}
	}

	public void startUpStation() {
		sc.startUp();
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
	public void addPaper() {
		
		try {
			sc.station.printer.addPaper(500);
		} catch (OverloadException e) {
			for (AttendantControlListener l : listeners)
				l.signalWeightDescrepancy("Added too much paper!");
		}
		for (AttendantControlListener l : listeners)
			l.printerNotLowState();
	}

	/**
	 * allow attendant to add ink to receipt printer
	 * adds 208000 characters worth of ink
	 * 
	 * precondition: printer is low on ink or out of ink
	 * 
	 * @throws OverloadException if more ink than the printer can handle is added
	 */
	public void addInk(){
		try {
			sc.station.printer.addInk(208000);
		} catch (OverloadException e) {
			for (AttendantControlListener l : listeners)
				l.signalWeightDescrepancy("Added too much ink!");
		}
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
	 * Approve the no bagging request from customer
	 * Customer may leave the item in cart
	 */
	public void approveNoBagRequest() {
		sc.ItemApprovedToNotBag();
		for (AttendantControlListener l : listeners)
			l.initialState();
	}
	
	public void itemBagged() {
		for (AttendantControlListener l : listeners)
			l.itemBagged();
	}
	
	/*
	 * Attendant adjusts the amount of banknotes used for change
	 * Cash controller notifies if storage is low in order to use
	 * 
	 * @throws SimulationException
	 * 			For loading or checking null banknotes
	 * @throws TooMuchCashException
	 * 			Too much cash is loaded onto the storage
	 */
	public void adjustBanknotesForChange() throws SimulationException, TooMuchCashException {
		boolean isLow = sc.getCashControl().banknotesInStorageLow(this.sc.station.banknoteStorage);
		if (isLow) {
			for (AttendantControlListener l : listeners)
				l.banknotesInStorageLowState();
			System.out.println("Banknote storage needs to be refilled.");
		} else {
			System.out.println("Banknote storage does not need to be loaded for now.");
		}
	}
	
	/*
	 * Refills banknote storage if it is low
	 * 
	 * @param unit
	 * 			The storage unit where the banknotes will be loaded
	 * @throws SimulationException
	 * 			For loading or checking null banknotes
	 * @throws TooMuchCashException
	 * 			Too much cash is loaded onto the storage
	 */
	public void loadBanknotesToStorage(BanknoteStorageUnit unit) throws SimulationException, TooMuchCashException {
		// amount of each kind of banknote to add
		int totalFives = unit.getCapacity()/5;
		int totalTens = unit.getCapacity()/5;
		int totalTwenties = unit.getCapacity()/5;
		int totalFifties = unit.getCapacity()/5;
		int totalHundreds = unit.getCapacity()/5;

		sc.getCashControl().disablePayments();
		List<Banknote> unloadedBanknotes = unit.unload();
		sc.getCashControl().banknotesUnloaded(unit);	
		
		// Verify value of existing banknotes, decrement it associated counter, reload the banknote
		for(Banknote banknote : unloadedBanknotes) {
			if (banknote == null) {
				break;
			}
			if (banknote.getValue() == 5) {
				totalFives--;
			}
			if (banknote.getValue() == 10) {
				totalTens--;
			}
			if (banknote.getValue() == 20) {
				totalTwenties--;
			}
			if (banknote.getValue() == 50) {
				totalFifties--;
			}
			if (banknote.getValue() == 100) {
				totalHundreds--;
			}
			unit.load(banknote);
		}
		
		// Load banknotes
		for (int i = 0; i < totalFives; i++) {
			unit.load(new Banknote(currency, 5));
		}
		for (int i = 0; i < totalTens; i++) {
			unit.load(new Banknote(currency, 10));
		}
		for (int i = 0; i < totalTwenties; i++) {
			unit.load(new Banknote(currency, 20));
		}
		for (int i = 0; i < totalFifties; i++) {
			unit.load(new Banknote(currency, 50));
		}
		for(int i = 0; i < totalHundreds; i++) {
			unit.load(new Banknote(currency, 100));
		}
				
		sc.getCashControl().banknotesLoaded(unit);
		sc.getCashControl().enablePayments();
	}
	
	/**
	 * Attendant adjusts the amount of coin used for change
	 * Cash controller notifies if storage is low in order to use
	 * 
	 * @throws SimulationException
	 * 			For loading or checking null coin
	 * @throws TooMuchCashException
	 * 			Too much cash is loaded onto the storage
	 */
	public void notifyListenerAdjustCoinForChange() throws SimulationException, TooMuchCashException {
		boolean isLow = sc.getCashControl().coinInStorageLow(this.sc.station.coinStorage);
		if (isLow) {
			for (AttendantControlListener l : listeners)
				l.coinIsLowState(this.sc.station.coinStorage.getCapacity());
			System.out.println("Banknote storage needs to be refilled.");
		} else {
			System.out.println("Banknote storage does not need to be loaded for now.");
		}
	}
	
	/**
	 * fills up the coin slot and then signal cash controller that everything is okay
	 *
	 *@param unit
	 *		the unit that needs to be filled up
	 *@param AMOUNT
	 *		the amount of coin capacity to fill up MUST BE EVENLY DIVISIBLE BY 5
	 *
	 *@return
	 *		the filled up unit
	 * @throws TooMuchCashException 
	 * @throws SimulationException 

	 */
	public void adjustCoinsForChange(int AMOUNT) throws SimulationException, TooMuchCashException  {
		
		
		CoinStorageUnit unit = sc.station.coinStorage;
	
		
		if(AMOUNT > unit.getCapacity()) {
			throw new TooMuchCashException();
		}
		//take system out of service//
		
		AMOUNT /= 5;
		
		//sc.getCashControl().disablePayments();
		
		Coin tCoin = new Coin(Currency.getInstance("CAD"),BigDecimal.valueOf(2.0));
		Coin lCoin = new Coin(Currency.getInstance("CAD"),BigDecimal.valueOf(1.0));
		Coin qCoin = new Coin(Currency.getInstance("CAD"),BigDecimal.valueOf(0.25));
		Coin dCoin = new Coin(Currency.getInstance("CAD"),BigDecimal.valueOf(0.1));
		Coin nCoin = new Coin(Currency.getInstance("CAD"),BigDecimal.valueOf(0.05));
		
		
		List<Coin> unloadedCoins = unit.unload();
		sc.getCashControl().coinsUnloaded(unit);
		
		int nCounter = countCoin(nCoin,unloadedCoins);
		int dCounter = countCoin(dCoin,unloadedCoins);
		int qCounter = countCoin(qCoin,unloadedCoins);
		int lCounter = countCoin(lCoin,unloadedCoins);
		int tCounter = countCoin(tCoin,unloadedCoins);
		
		int nAmount = AMOUNT - nCounter;
		int dAmount = AMOUNT - dCounter;
		int qAmount = AMOUNT - qCounter;
		int lAmount = AMOUNT - lCounter;
		int tAmount = AMOUNT - tCounter;
		
		for(int i = 0; i < nAmount; i++) {
			unit.load(nCoin);
		}
		for(int i = 0; i < dAmount; i++) {
			unit.load(dCoin);
		}
		for(int i = 0; i < qAmount; i++) {
			unit.load(qCoin);
		}
		for(int i = 0; i < lAmount; i++) {
			unit.load(lCoin);
		}
		for(int i = 0; i < tAmount; i++) {
			unit.load(tCoin);
		}
		
				
		//notify cash controller that the unit has been filled
		sc.getCashControl().coinsLoaded(unit);
		
		//re enable system
		//sc.getCashControl().enablePayments();
		
	}
	
	/**
	 * counts how many coins of this type is
	 * @param value
	 * 		the value of the coin to count
	 * 
	 * @param coins
	 * 		the list to count the coin from
	 * 
	 * @return
	 * 		returns the amount of coins counted
	 */
	public int countCoin(Coin coinToCount, List<Coin> coins) {
	
		int count = 0;
		for(Coin c : coins) {

			if(c != null) {
				if(c == coinToCount) {
					count++;
				}
			}
		}
		return count;
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
				case "addCoin": 
					//TODO:
					
					break;
				case "addBanknote": 
					//TODO:
					
					break;
				case "addBag": 
					sc.loadBags();
					break;
				case "request no bag":
					attendantNotifications = ("customer requests no bagging");
					System.out.println("request no bag");
					noBagRequest();
					break;

				case "adjustBanknotesForChange":
					attendantNotifications = ("Checking if banknotes in storage need to be adjusted");
					adjustBanknotesForChange();
					// TODO
					// temporary delete later when button is moved
				case "printReceipt":
					//attendantNotifications = ("approved no bagging request");
					System.out.println("AC print receipt");
					sc.getReceiptControl().printItems();
					sc.getReceiptControl().printTotalCost();
					sc.getReceiptControl().printMembership();
					sc.getReceiptControl().printDateTime();
					sc.getReceiptControl().printThankyouMsg();		
					break;
				case "approve no bag":
					approveNoBagRequest();
					break;
				case "permit_use":
					attendantNotifications = ("Permitting use on station");
					permitStationUse();
					break;
				case "prevent_use":
					attendantNotifications = ("Preventing use on station for maintenance");
					preventStationUse();
				case "startUp":
					System.out.println("Station has been started up");
					startUpStation();
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
		for (AttendantControlListener l : listeners) {
			l.addPaperState();
			l.outOfPaper(this, "Out of Paper!");
		}
		

	}

	@Override
	public void outOfInk(IReceiptPrinter printer) {
		for (AttendantControlListener l : listeners) {
			l.addInkState();
			l.outOfInk(this, "Out of ink!");
		}

	}

	@Override
	public void lowInk(IReceiptPrinter printer) {
		System.out.println("AC low ink");
		for (AttendantControlListener l : listeners) {
			l.addInkState();
			l.lowInk(this, "Low on ink!");
		}
	}

	@Override
	public void lowPaper(IReceiptPrinter printer) {
		System.out.println("AC low paper");
		for (AttendantControlListener l : listeners) {
			l.addPaperState();
			l.lowPaper(this, "Low on paper!");
		}

	}

	@Override
	public void paperAdded(IReceiptPrinter printer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void inkAdded(IReceiptPrinter printer) {
		// TODO Auto-generated method stub

	}

	public void noBagRequest() {
		for (AttendantControlListener l : listeners)
			l.noBagRequest();
	}
}
