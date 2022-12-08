package com.diy.software.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import com.diy.hardware.BarcodedProduct;
import com.diy.hardware.DoItYourselfStation;
import com.diy.hardware.PLUCodedItem;
import com.diy.hardware.PLUCodedProduct;
import com.diy.hardware.PriceLookUpCode;
import com.diy.hardware.Product;
import com.diy.hardware.external.CardIssuer;
import com.diy.hardware.external.ProductDatabases;
import com.diy.simulation.Customer;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.PLUCodeControlListener;
import com.diy.software.fakedata.GiftcardDatabase;
import com.diy.software.listeners.StationControlListener;
import com.diy.software.util.Tuple;
import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.EmptyException;
import com.jimmyselectronics.Item;
import com.jimmyselectronics.OverloadException;
import com.jimmyselectronics.abagnale.IReceiptPrinter;
import com.jimmyselectronics.abagnale.ReceiptPrinterListener;
import com.jimmyselectronics.necchi.Barcode;
import com.jimmyselectronics.necchi.BarcodeScanner;
import com.jimmyselectronics.necchi.BarcodeScannerListener;
import com.jimmyselectronics.necchi.BarcodedItem;
import com.jimmyselectronics.opeechee.Card;
import com.jimmyselectronics.opeechee.Card.CardData;
import com.jimmyselectronics.opeechee.CardReader;
import com.jimmyselectronics.opeechee.CardReaderListener;
import com.jimmyselectronics.svenden.ReusableBag;
import com.jimmyselectronics.svenden.ReusableBagDispenser;
import com.jimmyselectronics.svenden.ReusableBagDispenserListener;
import com.jimmyselectronics.virgilio.ElectronicScale;
import com.jimmyselectronics.virgilio.ElectronicScaleListener;
import com.unitedbankingservices.TooMuchCashException;
import com.unitedbankingservices.banknote.Banknote;
import com.unitedbankingservices.coin.Coin;

import ca.powerutility.NoPowerException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import swing.styling.KioskAudio;

/**
 * SystemControl is a class that acts as an intermediary between listeners
 * (hardware) and the GUI (customer). An object of type SystemControl will
 * always be associated with a DoItYourselfStation
 * 
 * @author Calder Sloman
 *
 */
public class StationControl
		implements BarcodeScannerListener, ElectronicScaleListener, CardReaderListener, ReceiptPrinterListener, ReusableBagDispenserListener {
	public FakeDataInitializer fakeData;
	private double expectedCheckoutWeight = 0.0;
	private double bagWeight = 0.0;
	private double weightOfLastItemAddedToBaggingArea = 0.0;
	private double prevWeightOfScannerTray = 0.0;
		
	public Customer customer;
	public DoItYourselfStation station; // make private after being done testing
	// NEVER USED -- private Double amountOwed = 0.0;

	public String userMessage;

	public ArrayList<StationControlListener> listeners = new ArrayList<>();
	
	// Need to track item objects associated with this particular instance
	public Map<Barcode, Item> barcodedItems = new HashMap<>();
	public Map<PriceLookUpCode, Item> pluCodedItems = new HashMap<>();

	/******** Control Classes ********/
	private ItemsControl ic;
	private BagsControl bc;
	private AttendantControl ac;
	private WalletControl wc;
	private MembershipControl mc;
	private BagDispenserControl bdc;
	private CashControl cc;
	private PinPadControl ppc;
	private PaymentControl pc;
	private	ReceiptControl rc;

	private boolean isLocked = false;
	public String memberName;
	public double weightOfLastItem;
	public double weightOfItemCodeEntered;
	private boolean membershipInput = false;
	private int bagInStock;
	private PLUCodeControl pcc;


	// used for receipt listeners
	boolean isOutOfPaper = false;
	boolean isOutOfInk = false;

	//Set up kiosk Audio
	KioskAudio kioskAudio;
	/**
	 * Constructor for the SystemControl class. Instantiates an object of type
	 * DoItYourselfStation as well as a set of listeners which are registered to the
	 * hardware with the DIYStation.
	 */
	public StationControl() {
		customer = new Customer();
		station = new DoItYourselfStation();
		customer.useStation(station);
		
		ic = new ItemsControl(this);
		bc = new BagsControl(this);
		mc = new MembershipControl(this);
		cc = new CashControl(this);
		bdc = new BagDispenserControl(this);
		ac = new AttendantControl(this);

		station.printer.register(this);
		station.mainScanner.register(this);
		station.handheldScanner.register(this);
		station.baggingArea.register(this);
		station.scanningArea.register(this);
		station.cardReader.register(this);
		station.reusableBagDispenser.register(this);
		rc = new ReceiptControl(this);
		
		kioskAudio = new KioskAudio();

		startUp();
		
		fillStation();
		
		/*
		 * loads maximum number of bags to the reusable bag dispenser 
		 */
		station.reusableBagDispenser.plugIn();
		station.reusableBagDispenser.turnOn();
		loadBags();

		/*
		 * simulates what the printer has in it before the printing starts
		 * to simulate low paper and low ink
		 */
		this.ac.addInk(50);
		this.ac.addPaper(1);

		wc = new WalletControl(this);
		ppc = new PinPadControl(this);
		pc = new PaymentControl(this);
		pcc = new PLUCodeControl(this);
	}

	/**
	 * Constructor for injecting fake data
	 */
	public StationControl(FakeDataInitializer fakeData) {
		this();
		this.fakeData = fakeData;
		fakeData.addProductAndBarcodeData();
		fakeData.addPLUCodedProduct();

		// for (Card c: this.fakeData.getCards()) customer.wallet.cards.add(c);
		// for (Item i: this.fakeData.getItems()) customer.shoppingCart.add(i);

		for (Card c : this.fakeData.getCards())
			customer.wallet.cards.add(c);
		for (Item i : this.fakeData.getItems()) {
			if (i instanceof BarcodedItem) {
				BarcodedItem item = (BarcodedItem) i;
				this.barcodedItems.put(item.getBarcode(), i);
				customer.shoppingCart.add(i);	
			}
			else if (i instanceof PLUCodedItem) {
				PLUCodedItem item = (PLUCodedItem) i;
				this.pluCodedItems.put(item.getPLUCode(), i);
				customer.shoppingCart.add(i);
			}
		}		
	}

	/**
	 * Registers a Listener for SystemControlListener
	 */
	public void register(StationControlListener l) {
		listeners.add(l);
	}

	/**
	 * Removes a Listener for SystemControlListener
	 */
	public void unregister(StationControlListener l) {
		listeners.remove(l);
	}

	public void startUp() {
		station.plugIn();
		station.turnOn();
		
		ic.resetState();
		ac.resetState();
		rc.resetState();
		cc.resetState();
	}
	
	public void shutDown() {
		
		station.unplug();
		station.turnOff();
	}
	
	public ItemsControl getItemsControl() {
		return ic;
	}
	
	public BagsControl getBagsControl() {
		return bc;
	}

	public AttendantControl getAttendantControl() {
		return ac;
	}

	public WalletControl getWalletControl() {
		return wc;
	}
	
	public MembershipControl getMembershipControl() {
		return mc;
	}
	
	public BagDispenserControl getBagDispenserControl() {
		return bdc;
	}
	public ReceiptControl getReceiptControl() {
		return rc;
	}


	public PinPadControl getPinPadControl() {
		return ppc;
	}

	public PaymentControl getPaymentControl() {
		return pc;
	};

	public CashControl getCashControl() {
		return cc;
	}
	
	public double getWeightOfScannerTray() {
		return prevWeightOfScannerTray;
	}
	public void setWeightOfScannerTray(double weight) {
		prevWeightOfScannerTray = weight;
	}

	public DoItYourselfStation getStation() {
		return station;
	}
	
	public void loadBags() {
		try {
			// loads full capacity
			int limit = station.reusableBagDispenser.getCapacity() - bagInStock;
			if(limit > 0) {
				for(int i = 0; i < limit; i++) {
					ReusableBag aBag = new ReusableBag();
					station.reusableBagDispenser.load(aBag);
				}
				System.out.println("Loaded " + limit + " bags!");		// notify in console
			}else System.out.println("Bag Dispenser is full. No bag loaded!");	
		}catch(OverloadException e) {}
	}
	
	private void fillStation() {
		for(int i : station.banknoteDenominations) {
			int capacity = station.banknoteDispensers.get(i).getCapacity();
			Banknote[] bills = new Banknote[capacity];
			for(int j = 0; j < capacity; j++) {
				bills[j] = new Banknote(Currency.getInstance("CAD"), i);
			}
			try {
				station.banknoteDispensers.get(i).load(bills);
			} catch (TooMuchCashException e) {
				e.printStackTrace();
			}
		}
		
		for(BigDecimal i : station.coinDenominations) {
			int capacity = station.coinDispensers.get(i).getCapacity();
			Coin[] coins = new Coin[capacity];
			for(int j = 0; j < capacity; j++) {
				coins[j] = new Coin(Currency.getInstance("CAD"), i);
			}
			try {
				station.coinDispensers.get(i).load(coins);
			} catch (TooMuchCashException e) {
				e.printStackTrace();
			}
		}
	}

	public void updateWeightOfLastItemAddedToBaggingArea(double weight) {
		weightOfLastItemAddedToBaggingArea = weight;
	}

	public double getWeightOfLastItemAddedToBaggingArea() {
		return weightOfLastItemAddedToBaggingArea;
	}

	/**
	 * Disables pieces of hardware so the Customer cannot continue checking out
	 * until an issue is resolved.
	 */
	public void blockStation() {
		if (!isLocked) {
			boolean loop = true;
			while (loop) {
				try {
					this.station.handheldScanner.disable();
					this.station.mainScanner.disable();
					this.station.cardReader.disable();
					this.cc.disablePayments(); // Added this method for when adjusting banknotes/coins.
					for (StationControlListener l : listeners) {
						l.systemControlLocked(this, true);
					}
					isLocked = true;
				} catch (NoPowerException e) {
					System.out.println(e.getMessage());
				} finally {
					if (this.station.handheldScanner.isPoweredUp())
						loop = false;
				}
			}
		}
		// System.out.println("Station Locked"); // Remove before release}
	}
	
	public void blockStation(String reason) {
		if (!isLocked) {
			boolean loop = true;
			while (loop) {
				try {
					this.station.handheldScanner.disable();
					this.station.cardReader.disable();
					for (StationControlListener l : listeners) {
						l.systemControlLocked(this, true, reason);
					}
					isLocked = true;
				} catch (NoPowerException e) {
					System.out.println(e.getMessage());
				} finally {
					if (this.station.handheldScanner.isPoweredUp())
						loop = false;
				}
			}
		}
	}

	/**
	 * Enables pieces of hardware so the Customer can continue checking out now that
	 * an issue has been resolved.
	 */
	public void unblockStation() {
		if (isLocked) {
			boolean loop = true;
			while (loop) {
				try {
					this.station.handheldScanner.enable();
					this.station.mainScanner.enable();
					this.station.cardReader.enable();
					this.cc.enablePayments(); // Added this method for when adjusting banknotes/coins is finished.
					for (StationControlListener l : listeners)
						l.systemControlLocked(this, false);
					isLocked = false;
				} catch (NoPowerException e) {
					System.out.println(e.getMessage());
				} finally {
					if (this.station.handheldScanner.isPoweredUp())
						loop = false;
				}
				// System.out.println("Station Unlocked"); // Remove before release
			}
		}
	}

	public double getExpectedWeight() {
		return expectedCheckoutWeight;
	}

	public void updateExpectedCheckoutWeight(double expectedWeight) {
		this.expectedCheckoutWeight += expectedWeight;
	}

	public void updateExpectedCheckoutWeight(double expectedWeight, boolean isBag) {
		this.expectedCheckoutWeight += expectedWeight;
		if (isBag)
			this.bagWeight += expectedWeight;
	}

	public void goBackOnUI() {
		for (StationControlListener l : listeners)
			l.triggerPanelBack(this);
	}

	public void goToInitialScreenOnUI() {
		for (StationControlListener l : listeners)
			l.triggerInitialScreen(this);
	}

	public void askForPin(String kind) {
		kioskAudio.usePinPadSound();
		for (StationControlListener l : listeners)
			l.initiatePinInput(this, kind);
	}

	public void triggerUnknownReasonForPaymentFailScreen(String reason) {
		if (reason == null || reason.isEmpty()) {
			for (StationControlListener l : listeners)
				l.paymentHasBeenCanceled(this, null, "Card data could not be read for an unknown reason");
		} else {
			for (StationControlListener l : listeners)
				l.paymentHasBeenCanceled(this, null, reason);
		}
	}
	
	public void triggerMembershipCardInputFailScreen(String reason) {
		for (StationControlListener l : listeners)
			l.paymentHasBeenCanceled(this, null, reason);
		wc.membershipCardInputCanceled();
	}

	public void startPaymentWorkflow() {
		for (StationControlListener l : listeners)
			l.triggerPaymentWorkflow(this);
	}

	public void startMembershipWorkflow() {
		for (StationControlListener l : listeners)
			l.triggerMembershipWorkflow(this);
	}

	public void startPLUCodeWorkflow() {
		for (StationControlListener l : listeners)
			l.triggerPLUCodeWorkflow(this);;
	}
	
	public void startMembershipCardInput() {
		membershipInput = true;
		for (StationControlListener l : listeners)
			l.startMembershipCardInput(this);
		wc.membershipCardInputEnabled();
		
	}
	
	public void startPurchaseBagsWorkflow() {
		for (StationControlListener l : listeners)
			l.triggerPurchaseBagsWorkflow(this);
	}
	
	public void startCatalogWorkflow() {
		for (StationControlListener l : listeners)
			l.triggerBrowsingCatalog(this);
	}
	
	public void cancelMembershipCardInput() {
		membershipInput = false;
		wc.membershipCardInputCanceled();
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
	}

	@Override
	public void turnedOn(AbstractDevice<? extends AbstractDeviceListener> device) {
	}

	@Override
	public void turnedOff(AbstractDevice<? extends AbstractDeviceListener> device) {
	}

	@Override
	public void cardInserted(CardReader reader) {

	}

	@Override
	public void cardRemoved(CardReader reader) {

	}

	@Override
	public void cardTapped(CardReader reader) {
		// TODO: implement
	}

	@Override
	public void cardSwiped(CardReader reader) {
		// TODO: implement
	}

	@Override
	// <<<<<<< HEAD
	/*
	 * Reads the data from the card and pays for the transaction using the card. On
	 * success, the amount owed will be updated to 0.0 and the hold placed on the
	 * card will be resolved. paymentSuccess is set to true. On failure by the bank
	 * being unable to authorize a hold, the amount due will not change.
	 * paymentSuccess is set to false. On failure by the bank being unable to post
	 * the transaction, the card's credit limit will not change. paymentSuccess is
	 * set to false.
	 */
	public void cardDataRead(CardReader reader, CardData data) {
		if (data.getType() == "MEMBERSHIP") {
			mc.checkMembership(Integer.parseInt(data.getNumber()));
			for (StationControlListener l : listeners) {
				l.membershipCardInputFinished(this);
			}
			membershipInput = false;
			wc.membershipCardInputCanceled();
		} else {
			Double amountOwed = this.ic.getCheckoutTotal();
			String cardNumber = data.getNumber();
			CardIssuer bank = fakeData.getCardIssuer();
			
			if(data.getType().equals(GiftcardDatabase.CompanyGiftCard)) {
				if(amountOwed == 0) {
					cc.paymentFailed(true);
					return;
				}
				
				Double amountOnCard = GiftcardDatabase.giftcardMap.get(cardNumber);
				Double dif = amountOnCard - amountOwed;
				Double amountPlaced = Math.min(amountOwed, amountOnCard);
				long holdNum = bank.authorizeHold(cardNumber, amountPlaced);
				if(holdNum != -1L && bank.postTransaction(cardNumber, holdNum, amountPlaced)) {
					if(dif >= 0) {
						GiftcardDatabase.giftcardMap.put(cardNumber, dif);
						ic.updateCheckoutTotal(-amountOwed);
						for (StationControlListener l : listeners) {
							l.paymentHasBeenMade(this, data);
						}
					}else {
						GiftcardDatabase.giftcardMap.put(cardNumber, 0.0);
						ic.updateCheckoutTotal(-amountOnCard);
					}
					bank.releaseHold(cardNumber, holdNum);
				cc.cashInserted();
				}else {
					cc.paymentFailed(false);
				}
				return;
			}

			long holdNum = bank.authorizeHold(cardNumber, amountOwed);
			if (holdNum <= -1) {
				for (StationControlListener l : listeners) {
					l.paymentHasBeenCanceled(this, data, "Could not authorize bank hold.");
				}
			} else if (bank.postTransaction(cardNumber, holdNum, amountOwed)) {
				bank.releaseHold(cardNumber, holdNum);
				this.ic.updateCheckoutTotal(-this.ic.getCheckoutTotal());
				for (StationControlListener l : listeners) {
					l.paymentHasBeenMade(this, data);
				}
				ic.updateCheckoutTotal(0);
				return;
			}
			for (StationControlListener l : listeners) {
				l.paymentHasBeenCanceled(this, data, "Payment failed.");
			}
		}
	}

	/**
	 * Add item to electronic scale
	 * 
	 * @param itemToBag item to be added
	 */
	public void bagItem(Item itemToBag) {
		customer.placeItemInBaggingArea();
		this.unblockStation();
	}

	/**
	 * Removing item from electronic scale
	 * 
	 * @param itemRemoved
	 */
	public void removeItem(Item itemRemoved) {
		station.baggingArea.remove(itemRemoved);
	}

	/**
	 * populates final receipt for printing
	 * 
	 * @param CheckoutList the list of items the customer paid for
	 * @return the whole receipt as a string
	 */
	public String populateReceipt(ArrayList<Tuple<BarcodedProduct, Integer>> CheckoutList) {
		String fmt = "%-15s%10s\n";
		String fmtOutput = String.format(fmt, "ITEM", "PRICE");
		for (Tuple<BarcodedProduct, Integer> item : CheckoutList) {
			fmtOutput += String.format(fmt, item.x.getDescription(), item.y);
		}
		System.out.println(fmtOutput);
		return fmtOutput;
	}

	/**
	 * TODO
	 * Delete later, moved to receipt controller
	 * simulates printing the receipt to the customer based on what they purchased
	 * 
	 * @param receipt the customer receipt as a string
	 */
//	public void printReceipt(String receipt) {
//
//		for (char receiptChar : receipt.toCharArray()) {
//			try {
//				station.printer.print(receiptChar);
//				//System.out.println(receiptChar);
//			} catch (EmptyException e) {
//
//			} catch (OverloadException e) {
//
//			}
//		}
//	}

	
	
	/**
	 * sets user message to announce weight on the indicated scale has changed
	 * 
	 * @param scale
	 *                      The scale where the event occurred.
	 * @param weightInGrams
	 *                      The new weight.
	 */
	@Override
	public void weightChanged(ElectronicScale scale, double weightInGrams) {
		if(scale == station.baggingArea) {
			// Any time the system registers a weight changed event it checks to see if the
			// expected weight matches the actual weight
			// If the expected weight doesn't match the actual weight, it blocks the system.
			if (this.expectedWeightMatchesActualWeight(weightInGrams)) {
				this.unblockStation();
				userMessage = "Weight of scale has changed to: " + weightInGrams;
			} else {
				this.blockStation();
				System.err.println("System has been blocked!");
			}
		} else {
			
		}
		
	}
	
	

	
	@Override
	public void overload(ElectronicScale scale) {
		userMessage = "Weight on scale has been overloaded, weight limit is: " + station.baggingArea.getWeightLimit();

	}

	@Override
	public void outOfOverload(ElectronicScale scale) {
		userMessage = "Excessive weight removed, continue scanning";
		this.unblockStation();

	}

	@Override
	public void barcodeScanned(BarcodeScanner barcodeScanner, Barcode barcode) {
		if (membershipInput) {
			mc.checkMembership(Integer.parseInt(barcode.toString()));
			for (StationControlListener l : listeners) {
				l.membershipCardInputFinished(this);
			}
			membershipInput = false;
			wc.membershipCardInputCanceled();
		}
	}
				
	public void addReusableBag(ReusableBag lastDispensedReusableBag) {		
		// Set the expected weight in SystemControl
		weightOfLastItem = lastDispensedReusableBag.getWeight();
		this.updateExpectedCheckoutWeight(weightOfLastItem);
		this.updateWeightOfLastItemAddedToBaggingArea(weightOfLastItem);
	}
	
	public void ItemApprovedToNotBag() {
		this.updateExpectedCheckoutWeight(-weightOfLastItem);
		this.updateWeightOfLastItemAddedToBaggingArea(-weightOfLastItem);
		ic.placeBulkyItemInCart();
		this.unblockStation();
	}


	/**
	 * Compares the expected weight after adding an item to the actual weight being
	 * read on the scale.
	 * 
	 * @return Boolean True if weights match, false otherwise
	 * @throws OverloadException If the weight has overloaded the scale.
	 */
	public boolean expectedWeightMatchesActualWeight(double actualWeight) {
		return Math.abs(getExpectedWeight() - (actualWeight + bagWeight)) <= 1;
	}
	
	public int getBagInStock() {
		return bagInStock;
	}

	
	@Override
	public void outOfPaper(IReceiptPrinter printer) {
		isOutOfPaper = true;
		rc.outOfPaper(printer);
		blockStation("Printer is out of ink or paper please wait for attendant");
		rc.outOfPaper(printer);
	}

	@Override
	public void outOfInk(IReceiptPrinter printer) {
		isOutOfInk = true;
		 rc.outOfInk(printer);
		 blockStation("Printer is out of ink or paper please wait for attendant");
		// have the same functionality as low ink for now
		 rc.outOfInk(printer);
	}
	
	@Override
	public void lowInk(IReceiptPrinter printer) {
		rc.lowInk(printer);
	}

	@Override
	public void lowPaper(IReceiptPrinter printer) {
		rc.lowPaper(printer);
	}

	@Override
	public void paperAdded(IReceiptPrinter printer) {
		isOutOfPaper = false;
		// unblock station when enough paper is added, checks if theres enough ink
		if(!isOutOfInk) {
			unblockStation();
			rc.paperAdded(printer);
			// System.out.println("station unblocked paper added");
		}
	}

	@Override
	public void inkAdded(IReceiptPrinter printer) {
		isOutOfInk = false;
		// unblock station when enough ink is added, checks if theres enough paper
		if(!isOutOfPaper) {
			unblockStation();
			rc.inkAdded(printer);
			// System.out.println("station unblocked ink added");
		}
	}
	
	public boolean isMembershipInput() {
		return membershipInput;
	}
	
	
	public void notifyNoBagsInStock() {
		for (StationControlListener l: listeners) {
			l.noBagsInStock(this);
		}
	}
	
	public void notifyNotEnoughBagsInStock(int numBag) {
		for (StationControlListener l: listeners) {
			l.notEnoughBagsInStock(this, numBag);
		}
	}

	public PLUCodeControl getPLUCodeControl() {
		return pcc;
	}

//	@Override
//	public void pluHasBeenUpdated(PLUCodeControl pcc, String pluCode) {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void bagDispensed(ReusableBagDispenser dispenser) {
		bagInStock--;
	}

	@Override
	public void outOfBags(ReusableBagDispenser dispenser) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bagsLoaded(ReusableBagDispenser dispenser, int count) {
		bagInStock++;
	}
	
	public void printReceipt() {
		for (StationControlListener l: listeners) {
			l.triggerReceiptScreen(this);
		}
		rc.printItems();
		rc.printTotalCost();
		rc.printMembership();
		rc.printDateTime();
		rc.printThankyouMsg();
	}

}