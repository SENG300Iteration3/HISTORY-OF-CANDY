package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Objects;
import com.diy.hardware.AttendantStation;
import com.diy.software.listeners.AttendantControlListener;
import com.diy.software.listeners.MembershipControlListener;
import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.OverloadException;
import com.jimmyselectronics.abagnale.IReceiptPrinter;
import com.jimmyselectronics.abagnale.ReceiptPrinterListener;
import com.jimmyselectronics.nightingale.Key;
import com.jimmyselectronics.nightingale.KeyListener;
import com.jimmyselectronics.nightingale.Keyboard;
import com.jimmyselectronics.nightingale.KeyboardListener;
import com.unitedbankingservices.TooMuchCashException;
import com.unitedbankingservices.coin.Coin;
import com.unitedbankingservices.coin.CoinStorageUnit;
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
	private CoinStorageUnit unit;
	private Currency currency;
	private KeyboardControl kc;
	private TextLookupControl tlc;
	private String attendantNotifications;
	
	public static final ArrayList<String> logins = new ArrayList<String>();

	public AttendantControl(StationControl sc) {
		this.sc = sc;
		this.station = new AttendantStation();
		this.listeners = new ArrayList<>();
		
		station.keyboard.register(this);
		
		station.plugIn();
		station.turnOn();
		
		kc = new PhysicalKeyboardControl(this);
		tlc = new TextLookupControl(this, this.sc);
		
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
	
	public KeyboardControl getKeyboardControl() {
		return kc;
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
	 * fills up the coin slot and then signal cash controller that everything is okay
	 *
	 *@param unit
	 *		the unit that needs to be filled up
	 *@param AMOUNT
	 *		the amount of coins to fill up
	 * @throws TooMuchCashException 
	 * @throws SimulationException 

	 */
	public void adjustCoinsForChange(CoinStorageUnit unit, int AMOUNT) throws SimulationException, TooMuchCashException  {
		
		this.unit = unit;
		//take system out of service
		
		sc.getCashControl().disablePayments();
		
		Coin nickelToAdd = new Coin(5);
		Coin dimeToAdd = new Coin(10);
		Coin quaterToAdd = new Coin(25);
		Coin loonieToAdd = new Coin(100);
		Coin toonieToAdd = new Coin(200);
		
		
		List<Coin> unloadedCoins = unit.unload();
		
		int nCounter = countCoin(5,unloadedCoins);
		int dCounter = countCoin(10,unloadedCoins);
		int qCounter = countCoin(25,unloadedCoins);
		int lCounter = countCoin(100,unloadedCoins);
		int tCounter = countCoin(200,unloadedCoins);
		
		int nAmount = AMOUNT - nCounter;
		int dAmount = AMOUNT - dCounter;
		int qAmount = AMOUNT - qCounter;
		int lAmount = AMOUNT - lCounter;
		int tAmount = AMOUNT - tCounter;
		
		addCoin(nAmount,nickelToAdd);
		addCoin(dAmount,dimeToAdd);
		addCoin(qAmount,quaterToAdd);
		addCoin(lAmount,loonieToAdd);
		addCoin(tAmount,toonieToAdd);
		
		
		//notify cash controller that the unit has been filled
		sc.getCashControl().coinsLoaded(unit);
		
		//re enable system
		sc.getCashControl().enablePayments();
		
		for (AttendantControlListener l : listeners)
			l.initialState();
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
	public int countCoin(long value, List<Coin> coins) {
		int count = 0;
		for(Coin c : coins) {
			if(c.getValue() == value) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * add the specified amount of coins to a list to add to dispenser
	 * 
	 * @param amount
	 * 		amount of coin to add
	 * 
	 * @param coin 
	 * 		the coin type to add
	 * @throws TooMuchCashException 
	 * @throws SimulationException 
	 */
	public void addCoin(int amount,Coin coin) throws SimulationException, TooMuchCashException{
		for(int i = 0; i < amount; i++) {
			unit.load(coin);
		}
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
				case "shutDown":
					//TODO:
					break;
				case "add":
					//TODO:
					break;
				case "remove":
					//TODO:
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
