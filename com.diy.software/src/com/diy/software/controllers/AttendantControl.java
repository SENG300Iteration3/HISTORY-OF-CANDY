package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import com.diy.hardware.AttendantStation;
import com.diy.software.listeners.AttendantControlListener;
import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.OverloadException;
import com.jimmyselectronics.abagnale.IReceiptPrinter;
import com.jimmyselectronics.abagnale.ReceiptPrinterListener;
import com.jimmyselectronics.nightingale.Key;
import com.jimmyselectronics.nightingale.KeyListener;
import com.jimmyselectronics.nightingale.Keyboard;
import com.jimmyselectronics.nightingale.KeyboardListener;
import com.jimmyselectronics.abagnale.ReceiptPrinterND;
import com.unitedbankingservices.TooMuchCashException;
import com.unitedbankingservices.banknote.Banknote;
import com.unitedbankingservices.banknote.BanknoteStorageUnit;
import com.unitedbankingservices.coin.Coin;
import com.unitedbankingservices.coin.CoinStorageUnit;

import ca.ucalgary.seng300.simulation.SimulationException;

public class AttendantControl implements ActionListener, ReceiptPrinterListener, KeyboardListener, KeyListener {

	public AttendantStation station;
	private StationControl sc;
	private ItemsControl ic;
	private ArrayList<AttendantControlListener> listeners;
	private Currency currency;
	private TextLookupControl tlc;
	private KeyboardControl kc;
	private String attendantNotifications;
	private int MAXIMUM_INK = 0;
	
	public static final ArrayList<String> logins = new ArrayList<String>();

	public AttendantControl(StationControl sc) {
		this.sc = sc;
		this.station = new AttendantStation();
		this.listeners = new ArrayList<>();
		
		this.currency = Currency.getInstance("CAD");
		
		station.keyboard.register(this);
		
		station.plugIn();
		station.turnOn();
		
		tlc = new TextLookupControl(this, this.sc);
		kc = new KeyboardControl(station.keyboard);
		ic = sc.getItemsControl();
	}

	public void login(String password) {
		for (AttendantControlListener l : listeners) {
			l.loggedIn(logins.contains(password));
		}
	}
	
	public void logout() {
		for (AttendantControlListener l : listeners) {
			l.loggedIn(false);
		}
	}

	public void addListener(AttendantControlListener l) {
		listeners.add(l);
	}

	public void removeListener(AttendantControlListener l) {
		listeners.remove(l);
	}
	
	public TextLookupControl getTextLookupControl() {
		return tlc;
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
		//sc.unblockStation();
		for (AttendantControlListener l : listeners) {
			l.stationStartedUp(this);
		}
	}
	
	public void shutDownStation() {
		sc.blockStation("shutdown");
		for (AttendantControlListener l : listeners) {
			l.stationShutDown(this);
		}
		sc.shutDown();
	}	
	
	public void resetState() {
		sc.unblockStation(); // initial state of AttendantControl is an unblocked station.
		for (AttendantControlListener l : listeners) {
			l.initialState();
		}
	}
	
	public void approveBagsAdded() {
		sc.unblockStation();
		for (AttendantControlListener l : listeners) {
			l.attendantApprovedBags(this); 
		}
	}
	
	/**
	 * Unblocks the customer DIYStation and announces that an item
	 * has been removed to any AttendantControl listeners.
	 */
	public void removeItemSuccesful() {
		sc.unblockStation();
		for (AttendantControlListener l : listeners) {
			l.attendantApprovedItemRemoval(this);
		}
	}
	
	/**
	 * This method should be triggered when the attendant selects the "Remove Item"
	 * option from their console. It calls removeItem in ItemsControl and passes
	 * an integer as an argument. This integer should correspond to the item number which
	 * is to be removed.
	 * 
	 * Precondition: The customer must have requested "remove item" from their console.
	 * 
	 * @param i	The item number to remove
	 * @return	Whether the removal was successful
	 */
	public boolean removeItem(int i) {
		
		boolean success = ic.removeItem(i);
		
		if (success) {
			removeItemSuccesful();
			ic.notifyItemRemoved();
		}
		
		return success;
		
		// TODO Switch this so that the GUI uses a pinpad/numpad to enter the number
//		System.out.println("Enter the number corrseponding to the item to be removed: ");
//		int itemNumber = scanner.nextInt();
//		while (!ic.removeItem(itemNumber)) {
//			System.out.println("Enter the number corrseponding to the item to be removed: ");
//			itemNumber = scanner.nextInt();
//		}
		
	}
	
	/**
	 * Allow attendant to shut down a station in order to do maintenance
	 *
	 * Precondition: The system is otherwise ready for customer interaction.
	 * The station to suspend is not in the midst of a customer session.
	 *
	 */
	public void preventStationUse(){
		sc.blockStation("prevent");
		for(AttendantControlListener l : listeners){
			l.attendantPreventUse(this);
		}
	}
	
	/**
	 * allow attendant to add ink to receipt printer
	 * If too much ink is added, simulate fixing by adding the max amount of ink allowed - 100
	 * @param inkUnit amount of ink to add
	 * 
	 * precondition: printer is low on ink or out of ink
	 * 
	 * @throws OverloadException if more ink than the printer can handle is added
	 */
	public void addInk(int inkUnit){
		try {
			sc.getReceiptControl().currentInkCount += inkUnit;
			sc.station.printer.addInk(inkUnit);
		} catch (OverloadException e) {
			sc.getReceiptControl().currentInkCount -= inkUnit;
			for (AttendantControlListener l : listeners) {
				l.signalWeightDescrepancy("Added too much ink!");
				l.addTooMuchInkState();
			}
			addInk(ReceiptPrinterND.MAXIMUM_INK - sc.getReceiptControl().currentInkCount - 1); //add maximum amount of ink possible that doesn't cause overload
			
		}
		if(sc.getReceiptControl().currentInkCount >= sc.getReceiptControl().inkLowThreshold) {
			for (AttendantControlListener l : listeners)
				l.printerNotLowInkState();
		}
	}

	/**
	 * allow attendant to add paper to receipt printer
	 * If too much paper is added, simulate fixing by adding the max amount of paper allowed - 100
	 * @param paperUnit amount of paper to add
	 * 
	 * precondition: printer is low on paper or out of paper
	 * 
	 * @throws OverloadException too much paper added, printer cant handle it
	 */
	public void addPaper(int paperUnit) {
		
		try {
			sc.getReceiptControl().currentPaperCount += paperUnit;
			sc.station.printer.addPaper(paperUnit);
		} catch (OverloadException e) {
			sc.getReceiptControl().currentPaperCount -= paperUnit;
			for (AttendantControlListener l : listeners) {
				l.signalWeightDescrepancy("Added too much paper!");
				l.addTooMuchPaperState();
			}
			addPaper(ReceiptPrinterND.MAXIMUM_PAPER - sc.getReceiptControl().currentPaperCount - 1); //add maximum amount of paper possible that doesn't cause overload
		}
		if(sc.getReceiptControl().currentPaperCount >= sc.getReceiptControl().paperLowThreshold) {
			for (AttendantControlListener l : listeners) {
				l.printerNotLowPaperState();
			}
		}
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

//		sc.getCashControl().disablePayments();
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
		
		for (AttendantControlListener l : listeners)
			l.banknotesNotLowState();
		
//		sc.getCashControl().enablePayments();
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
		
		sc.getCashControl().disablePayments();
		
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
		sc.getCashControl().enablePayments();
		
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
					// Listener wont react if we type 208000 as a parameter 
					int inkUnit = 208000;
					addInk(inkUnit);
					System.out.print("added ink");
					break;
				case "addPaper":
					attendantNotifications = ("stations printer needs more paper!");
					// Listener wont react if we type 500 as a parameter 
					int paperUnit = 500;
					addPaper(paperUnit);
					break;
				case "addCoin": 
					//TODO:
					adjustCoinsForChange(sc.getStation().coinStorage.getCapacity());
					break;
				case "addBanknote":
					loadBanknotesToStorage(sc.getStation().banknoteStorage);
					break;
				case "addBag": 
					sc.loadBags();
					break;
				case "request no bag":
					attendantNotifications = ("customer requests no bagging");
					System.out.println("request no bag");
					noBagRequest();
					break;
				case "printReceipt":
					//attendantNotifications = ("approved no bagging request");
					System.out.println("AC print receipt");
					sc.getReceiptControl().printItems();
					sc.getReceiptControl().printTotalCost();
					sc.getReceiptControl().printMembership();
					sc.getReceiptControl().printDateTime();
					sc.getReceiptControl().printThankyouMsg();		
					break;
				case "adjustBanknotesForChange":
					attendantNotifications = ("Checking if banknotes in storage need to be adjusted");
					adjustBanknotesForChange();
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
				case "shutDown":
					shutDownStation();
					 System.out.println("Station has been shut down");
					break;
				case "add":
					textSearch();
					break;
				case "remove":
					textSearch();
					break;
				case "logout":
					logout();
					break;
				default:
					break;
			}
		} catch (Exception ex) {
			
		}
		
	}
	
	public void textSearch() {
		for (AttendantControlListener l : listeners) {
			l.triggerItemSearchScreen(this);
		}
	}
	
	public void exitTextSearch() {
		for (AttendantControlListener l : listeners) {
			l.exitTextSearchScreen(this);
		}
	}
	
	public KeyboardControl getKeyboardControl() {
		return kc;
	}

	@Override
	public void outOfPaper(IReceiptPrinter printer) {
		for (AttendantControlListener l : listeners) {
			//l.addPaperState();
			l.outOfPaper(this, "Out of Paper!");
		}
	}

	@Override
	public void outOfInk(IReceiptPrinter printer) {
		for (AttendantControlListener l : listeners) {
			//l.addInkState();
			l.outOfInk(this, "Out of ink!");
		}

	}

	@Override
	public void lowInk(IReceiptPrinter printer) {
		for (AttendantControlListener l : listeners) {
			//l.addInkState();
			l.lowInk(this, "Low on ink!");
		}
	}

	@Override
	public void lowPaper(IReceiptPrinter printer) {
		for (AttendantControlListener l : listeners) {
			//l.addPaperState();
			l.lowPaper(this, "Low on paper!");
		}

	}

	@Override
	public void paperAdded(IReceiptPrinter printer) {
		
	}

	@Override
	public void inkAdded(IReceiptPrinter printer) {
		
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

	public void noBagRequest() {
		for (AttendantControlListener l : listeners)
			l.noBagRequest();
	}

	@Override
	public void pressed(Key k) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void released(Key k) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(Keyboard keyboard, String label) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(Keyboard keyboard, String label) {
		// TODO Auto-generated method stub
		
	}
}
