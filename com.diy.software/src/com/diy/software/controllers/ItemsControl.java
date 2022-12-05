package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import com.diy.software.util.Tuple;
import com.diy.hardware.BarcodedProduct;
import com.diy.hardware.external.ProductDatabases;
import com.diy.software.listeners.ItemsControlListener;
import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.Item;
import com.jimmyselectronics.necchi.Barcode;
import com.jimmyselectronics.necchi.BarcodeScanner;
import com.jimmyselectronics.necchi.BarcodeScannerListener;
import com.jimmyselectronics.necchi.BarcodedItem;
import com.jimmyselectronics.virgilio.ElectronicScale;
import com.jimmyselectronics.virgilio.ElectronicScaleListener;

public class ItemsControl implements ActionListener, BarcodeScannerListener, ElectronicScaleListener {
	private StationControl sc;
	private ArrayList<ItemsControlListener> listeners;
	public ArrayList<Tuple<BarcodedProduct,Integer>> tempList = new ArrayList<>();
	public ArrayList<Barcode> checkoutList = new ArrayList<>();
	private double checkoutListTotal = 0.0;

	private boolean scanSuccess = true, weighSuccess = true;
	
	public String userMessage = "";
	private long baggingAreaTimerStart;
	private long baggingAreaTimerEnd;
	private final static double PROBABILITY_OF_BAGGING_WRONG_ITEM = 0.20;
	private final static ThreadLocalRandom random = ThreadLocalRandom.current();
	private Item wrongBaggedItem = new Item(235){};
	
	private boolean removedWrongBaggedItem;
	private double scaleExpectedWeight;
	private double scaleReceivedWeight;
	
	// TODO REMOVE BEFORE RELEASE
	Scanner scanner = new Scanner(System.in);

	public ItemsControl(StationControl sc) {
		this.sc = sc;
		sc.station.handheldScanner.register(this);
		sc.station.mainScanner.register(this);
		sc.station.baggingArea.register(this);
		this.listeners = new ArrayList<>();
		
		
	}
	
	public Item getWrongBaggedItem() {
		return wrongBaggedItem;
	}

	public void addListener(ItemsControlListener l) {
		listeners.add(l);
	}

	public void removeListener(ItemsControlListener l) {
		listeners.remove(l);
	}
	
	public void addItemToCheckoutList(Barcode barcode) {
		checkoutList.add(barcode);
		refreshGui();
	}
	
	public void addScannedItemToCheckoutList(Barcode barcode) {
		BarcodedProduct barcodedProduct = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
		double price;
		if (barcodedProduct != null) {
			price = (double) barcodedProduct.getPrice();
			this.addItemToCheckoutList(barcode);
			this.updateCheckoutTotal(price);
		} else {
			System.err.println("Scanned item is not in product database!");
		}
	}
	
	
	/**
	 * Blocks the customers DIYStation and announces that
	 * the customer wants to remove an item to any ItemsControl listeners.
	 */
	public void requestRemoveItem() {
		sc.blockStation(); // block station
		for (ItemsControlListener l : listeners){
			l.awaitingAttendantToApproveItemRemoval(this);
		}
	}
	
	/**
	 * The method responsible for removing an item from the system entirely.
	 * Its cost is subtracted from the bill total, it is removed from the bagging area,
	 * its weight is subtracted from expected weight, the "stores" inventory is updated
	 * and the GUI is refreshed.
	 * 
	 * @param index 
	 * 			The item number displayed on screen which you want to remove
	 * @return
	 * 		True if the index is within . False otherwise.
	 */
	public boolean removeItem(int index) {
		if (index > checkoutList.size()) {
			return false;
		}
		else {
			index--; // decrement index so it matches actual array index!
			Barcode barcode = this.checkoutList.get(index);
			// getting the actual object that the customer had in his shopping cart and was subsequently added to the baggingArea
			BarcodedItem item = (BarcodedItem) this.sc.items.get(barcode);
			double price = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode).getPrice();
			double weight = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode).getExpectedWeight();
			// TODO update inventory to increase "stock" since the item wasn't sold
			this.sc.updateExpectedCheckoutWeight(-weight);  // decrement weight
			this.sc.station.baggingArea.remove(item);
			this.updateCheckoutTotal(-price);	// decrement price
			checkoutList.remove(index); // remove the barcode from checkoutList so GUI updates accordingly
			refreshGui();
			return true;
		}

	}
	
	public void updateCheckoutTotal(double amount) {
		if (checkoutListTotal + amount >= 0) 
			checkoutListTotal += amount;
		refreshGui();
	}
	
	public double getCheckoutTotal() {
		return checkoutListTotal;
	}
	
	public ArrayList<Barcode> getCheckoutList () {
		return checkoutList;
	}
	
	private void refreshGui() {
		for (ItemsControlListener l: listeners) {
			l.itemsHaveBeenUpdated(this);
			l.productSubtotalUpdated(this);
		}
	}

	/**
	 * Picks up next item and if shopping cart is empty after, notifies
	 * noMoreItemsAvail
	 * 
	 * If the shoppingCart is empty at the start, ignores selecting next item and
	 * instead notified noMoreItemsAvail
	 */
	public void pickupNextItem() {
		try {
			sc.customer.selectNextItem();
			for (ItemsControlListener l : listeners)
				l.itemWasSelected(this);
			if (sc.customer.shoppingCart.size() == 0) {
				for (ItemsControlListener l : listeners)
					l.noMoreItemsAvailableInCart(this);
			}
		} catch (NoSuchElementException e) {
			// next item does not exist
			for (ItemsControlListener l : listeners)
				l.noMoreItemsAvailableInCart(this);
		}
	}

	public void putUnscannedItemBack() {
		try {
			sc.customer.deselectCurrentItem();
			for (ItemsControlListener l : listeners) {
				l.itemsAreAvailableInCart(this);
				l.awaitingItemToBeSelected(this);
			}
		} catch (Exception e) {
			// exception should never occur since this code path is only ever called when
			// currentItem is not null
		}
	}

	// TODO: scanItem now differtiates between using handheldScanner and mainScanner
	// ALSO: note that a new weight area called scanningArea exists now to grab weight of items during general scanning phase
	public void scanCurrentItem(boolean useHandheld) {
		baggingAreaTimerStart = System.currentTimeMillis();
		scanSuccess = false;
		sc.customer.scanItem(useHandheld);
		if (!scanSuccess) {
			// if scanSuccess is still false after listeners have been called, we can show
			// an alert showing a failed scan if time permits.
		}
	}

	public void placeItemOnScale() {
		scaleExpectedWeight = sc.weightOfItemScanned;
		weighSuccess = false;
		baggingAreaTimerEnd = System.currentTimeMillis();
		// placing an item could potentially fail so allow for retries
		// simulating a 40% chance of putting wrong item on the scale
		if (random.nextDouble(0.0, 1.0) > PROBABILITY_OF_BAGGING_WRONG_ITEM) {
			weighSuccess = true;
			sc.customer.placeItemInBaggingArea();
			
		} else {
			// simulation weight discrepancy
			scaleReceivedWeight = wrongBaggedItem.getWeight();
			removedWrongBaggedItem = false;
			sc.customer.placeItemInBaggingArea();
			sc.station.baggingArea.add(wrongBaggedItem);
			
		}
		
		if(baggingAreaTimerEnd - baggingAreaTimerStart > 10000) {
			userMessage = "Please place item on scale!";
			// not blocking station for now
			//sc.blockStation();
		}
		
		if (!weighSuccess) {
			// if weighSuccess is still false after listeners have been called, we can show
			// and alert showing a failed weigh-in if time permits.
		}
	}
	
	/**
	 * removes the last wrongly added item from the scale
	 */
	public void removeLastBaggedItem() {
		sc.unblockStation();
		removedWrongBaggedItem = true;
		sc.station.baggingArea.remove(wrongBaggedItem);
		for (ItemsControlListener l : listeners)
			l.awaitingItemToBeSelected(this);
	}

	/**
	 * After the attendant approved no bag request, customer leave the item in cart
	 */
	public void placeBulkyItemInCart() {
		try {
			// Customer leaves the current item in the cart. 
			sc.customer.leaveBulkyItemInCart();
			for (ItemsControlListener l : listeners)
				l.awaitingItemToBeSelected(this);
		} catch (Exception e) {
			// do nothing if failure
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		try {
			switch (c) {
			case "pick up":
				System.out.println("Customer picks up next item");
				pickupNextItem();	
				break;
			case "main scan":
				System.out.println("Customer uses main scanner to scan next item");
				scanCurrentItem(false);
				break;
			case "handheld scan":
				System.out.println("Customer uses handheld scanner to scan next item");
				scanCurrentItem(true);
				break;
			case "put back":
				System.out.println("Customer put back current item");
				putUnscannedItemBack();
				break;
			case "bag":
				System.out.println("Customer put item in bagging area");
				placeItemOnScale();
				break;
			case "pay":
				System.out.println("Starting payment workflow");
				sc.startPaymentWorkflow();
				break;
			case "member":
				sc.startMembershipWorkflow();
				break;
			case "remove item":
				System.out.println("Requesting item removal. Please wait for Assistance!");
				this.requestRemoveItem();
				break;
			}
		} catch (Exception ex) {

		}
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
	}

	@Override
	public void turnedOn(AbstractDevice<? extends AbstractDeviceListener> device) {	}

	@Override
	public void turnedOff(AbstractDevice<? extends AbstractDeviceListener> device) {}

	@Override
	public void barcodeScanned(BarcodeScanner barcodeScanner, Barcode barcode) {
		if (!sc.isMembershipInput()) {
			scanSuccess = true;
			for (ItemsControlListener l : listeners)
				l.awaitingItemToBePlacedInBaggingArea(this);
		}
	}
	
	/**
	 * sets user message to announce weight on the indicated scale has changed
	 * 
	 * @param scale
	 *            The scale where the event occurred.
	 * @param weightInGrams
	 *            The new weight.
	 */
	@Override
	public void weightChanged(ElectronicScale scale, double weightInGrams) {
		weighSuccess = true;
		for (ItemsControlListener l : listeners)
			l.awaitingItemToBeSelected(this);
		if(sc.expectedWeightMatchesActualWeight(weightInGrams)) {
			sc.unblockStation();
			userMessage = "Weight of scale has changed to: " + weightInGrams;
		}else {
			//System.out.println("Expected: " + sc.getExpectedWeight() + "Added: " + weightInGrams);
			String weightDescrepancyMessage = "Expected item weight of: " + scaleExpectedWeight + ", " +
												"Weight bagged: " + scaleReceivedWeight + ". Weight Descrepancy detected please bag the right item";
			String weightDescrepancyMessageAttendant = "Expected item weight of: " + scaleExpectedWeight + ", " +
					"Weight bagged: " + scaleReceivedWeight + ". Customer bagged the wrong item";
			for (ItemsControlListener l : listeners)
				l.awaitingItemToBeRemoved(this, weightDescrepancyMessage);
	
			sc.getAttendantControl().updateWeightDescrepancyMessage(weightDescrepancyMessageAttendant);;
				
			}
			if(removedWrongBaggedItem) {
				sc.unblockStation();
				for (ItemsControlListener l : listeners)
					l.awaitingItemToBeSelected(this);
			}
		}
		

	@Override
	public void overload(ElectronicScale scale) {
		userMessage = "Weight on scale has been overloaded, weight limit is: " + sc.station.baggingArea.getWeightLimit();
	}

	@Override
	public void outOfOverload(ElectronicScale scale) {
		userMessage = "Excessive weight removed, continue scanning";
		sc.unblockStation();
	}



}