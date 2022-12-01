package com.diy.software.controllers;

import java.util.ArrayList;
import java.util.Currency;

import com.diy.software.util.Tuple;
import com.diy.hardware.BarcodedProduct;
import com.diy.hardware.DoItYourselfStation;
import com.diy.hardware.Product;
import com.diy.hardware.external.CardIssuer;
import com.diy.hardware.external.ProductDatabases;
import com.diy.simulation.Customer;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.StationControlListener;
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
import com.jimmyselectronics.opeechee.Card;
import com.jimmyselectronics.opeechee.Card.CardData;
import com.jimmyselectronics.opeechee.CardReader;
import com.jimmyselectronics.opeechee.CardReaderListener;
import com.jimmyselectronics.virgilio.ElectronicScale;
import com.jimmyselectronics.virgilio.ElectronicScaleListener;
import com.unitedbankingservices.TooMuchCashException;
import com.unitedbankingservices.banknote.Banknote;
import com.unitedbankingservices.coin.Coin;

import ca.powerutility.NoPowerException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/**
 * SystemControl is a class that acts as an intermediary between listeners
 * (hardware) and the GUI (customer). An object of type SystemControl will
 * always be associated with a DoItYourselfStation
 * 
 * @author Calder Sloman
 *
 */
public class StationControl
		implements BarcodeScannerListener, ElectronicScaleListener, CardReaderListener, ReceiptPrinterListener {
	public FakeDataInitializer fakeData;
	private double expectedCheckoutWeight = 0.0;
	private double bagWeight = 0.0;
	private double weightOfLastItemAddedToBaggingArea = 0.0;

	public Customer customer;
	public DoItYourselfStation station; // make private after being done testing
	// NEVER USED -- private Double amountOwed = 0.0;

	public String userMessage;

	public ArrayList<StationControlListener> listeners = new ArrayList<>();

	/******** Control Classes ********/
	private ItemsControl ic;
	private BagsControl bc;
	private AttendantControl ac;
	private WalletControl wc;
	private MembershipControl mc;
	private CashControl cc;

	private boolean isLocked = false;
	public String memberName;
	public double weightOfItemScanned;
	private boolean membershipInput = false;

	private PinPadControl ppc;
	private PaymentControl pc;

	/**
	 * Constructor for the SystemControl class. Instantiates an object of type
	 * DoItYourselfStation as well as a set of listeners which are registered to the
	 * hardware with the DIYStation.
	 */
	public StationControl() {
		customer = new Customer();
		station = new DoItYourselfStation();
		customer.useStation(station);

		station.printer.register(this);
		station.mainScanner.register(this);
		station.handheldScanner.register(this);
		station.mainScanner.register(this);
		station.baggingArea.register(this);
		station.cardReader.register(this);

		station.plugIn();
		station.turnOn();
		
		fillStation();

		ic = new ItemsControl(this);
		bc = new BagsControl(this);
		mc = new MembershipControl(this);
		cc = new CashControl(this);
		ac = new AttendantControl(this);

		/*
		 * simulates what the printer has in it before the printing starts
		 * to simulate low paper and low ink
		 */
		try {
			station.printer.addInk(500);
			station.printer.addPaper(1);
		} catch (OverloadException e1) {

		}
		wc = new WalletControl(this);
		ppc = new PinPadControl(this);
		pc = new PaymentControl(this);
	}

	/**
	 * Constructor for injecting fake data
	 */
	public StationControl(FakeDataInitializer fakeData) {
		this();
		this.fakeData = fakeData;
		this.fakeData.addCardData();
		this.fakeData.addProductAndBarcodeData();
		this.fakeData.addFakeMembers();

		// for (Card c: this.fakeData.getCards()) customer.wallet.cards.add(c);
		// for (Item i: this.fakeData.getItems()) customer.shoppingCart.add(i);

		for (Card c : this.fakeData.getCards())
			customer.wallet.cards.add(c);
		for (Item i : this.fakeData.getItems())
			customer.shoppingCart.add(i);
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

	public PinPadControl getPinPadControl() {
		return ppc;
	}

	public PaymentControl getPaymentControl() {
		return pc;
	};

	public CashControl getCashControl() {
		return cc;
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
		
		for(long i : station.coinDenominations) {
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
	
	public void startMembershipCardInput() {
		for (StationControlListener l : listeners)
			l.startMembershipCardInput(this);
		wc.membershipCardInputEnabled();
		membershipInput = true;
	}
	
	public void cancelMembershipCardInput() {
		wc.membershipCardInputCanceled();
		membershipInput = false;
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
	 * simulates printing the receipt to the customer based on what they purchased
	 * 
	 * @param receipt the customer receipt as a string
	 */
	public void printReceipt(String receipt) {

		for (char receiptChar : receipt.toCharArray()) {
			try {
				station.printer.print(receiptChar);
			} catch (EmptyException e) {

			} catch (OverloadException e) {

			}
		}
	}

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

	// FIXME: only barcoded products have barcodes, product only have price/weight
	@Override
	public void barcodeScanned(BarcodeScanner barcodeScanner, Barcode barcode) {
		if (membershipInput) {
			mc.checkMembership(Integer.parseInt(barcode.toString()));
			for (StationControlListener l : listeners) {
				l.membershipCardInputFinished(this);
			}
			membershipInput = false;
			wc.membershipCardInputCanceled();
		} else {
      Product product = findProduct(barcode);
		  checkInventory(product);
			weightOfItemScanned = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode).getExpectedWeight();
			// Add the barcode to the ArrayList within itemControl
			this.ic.addScannedItemToCheckoutList(barcode);
			// Set the expected weight in SystemControl
			this.updateExpectedCheckoutWeight(weightOfItemScanned);
			this.updateWeightOfLastItemAddedToBaggingArea(weightOfItemScanned);
			// Call method within SystemControl that handles the rest of the item scanning
			// procedure
			this.blockStation();
			// Trigger the GUI to display "place the scanned item in the Bagging Area"
		}
	}
	
	private void checkInventory(Product product) {
		if(ProductDatabases.INVENTORY.containsKey(product) && ProductDatabases.INVENTORY.get(product) >= 1) {
			ProductDatabases.INVENTORY.put(product, ProductDatabases.INVENTORY.get(product)-1); //updates INVENTORY with new total
		}else {
			// TODO: inform customer and attendant
			System.out.print("Out of stock");
		}
	}
	
	private BarcodedProduct findProduct(Barcode Barcode) throws NullPointerSimulationException {
    	if(ProductDatabases.BARCODED_PRODUCT_DATABASE.containsKey(Barcode)) {
            return ProductDatabases.BARCODED_PRODUCT_DATABASE.get(Barcode);        
        }
    	else {
    		// TODO: Inform customer station
    		System.out.println("Cannot find the product. Please try again or ask for assistant!");
    		throw new NullPointerSimulationException();
    	}
    }

	/**
	 * Compares the expected weight after adding an item to the actual weight being
	 * read on the scale.
	 * 
	 * @return Boolean True if weights match, false otherwise
	 * @throws OverloadException If the weight has overloaded the scale.
	 */
	public boolean expectedWeightMatchesActualWeight(double actualWeight) {
		return (this.getExpectedWeight() == actualWeight + bagWeight);
	}

	@Override
	public void outOfPaper(IReceiptPrinter printer) {

	}

	@Override
	public void outOfInk(IReceiptPrinter printer) {
		// System.out.println("out of ink");
		// have the same functionality as low ink for now
		// ac.lowInk(printer);
	}

	@Override
	public void lowInk(IReceiptPrinter printer) {
		// System.out.println("low ink");
		ac.lowInk(printer);
	}

	@Override
	public void lowPaper(IReceiptPrinter printer) {
		ac.lowPaper(printer);
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