package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

import com.diy.software.util.Tuple;
import com.diy.hardware.BarcodedProduct;
import com.diy.hardware.PriceLookUpCode;
import com.diy.hardware.Product;
import com.diy.hardware.PLUCodedItem;
import com.diy.hardware.PLUCodedProduct;
import com.diy.hardware.PriceLookUpCode;
import com.diy.hardware.external.ProductDatabases;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.ItemsControlListener;
import com.diy.software.listeners.PLUCodeControlListener;
import com.diy.software.listeners.StationControlListener;
import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.Item;
import com.jimmyselectronics.OverloadException;
import com.jimmyselectronics.necchi.Barcode;
import com.jimmyselectronics.necchi.BarcodeScanner;
import com.jimmyselectronics.necchi.BarcodeScannerListener;
import com.jimmyselectronics.necchi.BarcodedItem;
import com.jimmyselectronics.svenden.ReusableBag;
import com.jimmyselectronics.virgilio.ElectronicScale;
import com.jimmyselectronics.virgilio.ElectronicScaleListener;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

public class ItemsControl implements ActionListener, BarcodeScannerListener, ElectronicScaleListener, PLUCodeControlListener {
	private StationControl sc;
	private ArrayList<ItemsControlListener> listeners;
	public ArrayList<Tuple<BarcodedProduct, Integer>> tempList = new ArrayList<>();
	private ArrayList<Tuple<String, Double>> checkoutList = new ArrayList<>();
	private ArrayList<ReusableBag> bags = new ArrayList<ReusableBag>(); // stores reusable bag item with no barcode
	private double checkoutListTotal = 0.0;
	private Item currentItem;
	private double weightofScannerTray = 0.0;

	private boolean scanSuccess = true, weighSuccess = true, inCatalog = false;

	public String userMessage = "";
	private long baggingAreaTimerStart;
	private long baggingAreaTimerEnd;
	private final static double PROBABILITY_OF_BAGGING_WRONG_ITEM = 0.20;
	private final static ThreadLocalRandom random = ThreadLocalRandom.current();
	private Item wrongBaggedItem = new Item(235) {};
	private boolean isPLU = false;
	private PriceLookUpCode expectedPLU = null;
	private boolean removedWrongBaggedItem;
	private double scaleExpectedWeight;
	private double scaleReceivedWeight;
	private PriceLookUpCode currentProductCode;

	public ItemsControl(StationControl sc) {
		this.sc = sc;
		sc.station.handheldScanner.register(this);
		sc.station.mainScanner.register(this);
		sc.station.baggingArea.register(this);
		sc.station.scanningArea.register(this);
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

	public void addItemToCheckoutList(Tuple<String, Double> item) {
		checkoutList.add(item);
		refreshGui();
	}

	public void addScannedItemToCheckoutList(Barcode barcode) {
		BarcodedProduct barcodedProduct = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
		double price;
		if (barcodedProduct != null) {
			price = (double) barcodedProduct.getPrice();
			this.addItemToCheckoutList(new Tuple<String, Double>(barcodedProduct.getDescription(), price));
			this.updateCheckoutTotal(price);
		} else {
			System.err.println("Scanned item is not in product database!");
		}
	}

	public void updateCheckoutTotal(double amount) {
		if (checkoutListTotal + amount >= 0)
			checkoutListTotal += amount;
		refreshGui();
	}

	public void addReusableBags(ReusableBag aBag) {
		bags.add(aBag); // add to reusable bags doesnt really need it for now

		double reusableBagPrice = sc.fakeData.getReusableBagPrice();
		this.updateCheckoutTotal(reusableBagPrice); // update total balance
		this.addItemToCheckoutList(new Tuple<String, Double>("Reusable bag", reusableBagPrice));
	}

	public double getCheckoutTotal() {
		return checkoutListTotal;
	}

	public ArrayList<Tuple<String, Double>> getCheckoutList() {
		return checkoutList;
	}

	private void refreshGui() {
		for (ItemsControlListener l : listeners) {
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
			// TODO: Find another way to do this
			this.currentItem = sc.customer.shoppingCart.get(sc.customer.shoppingCart.size() - 1);
			isPLU = currentItem.getClass() == PLUCodedItem.class;
			if(isPLU) {
				expectedPLU = ((PLUCodedItem)currentItem).getPLUCode();
			}

			sc.customer.selectNextItem();
			if (currentItem instanceof PLUCodedItem) {
				for (ItemsControlListener l : listeners)
					l.itemWasSelected(this);
			} else {
				for (ItemsControlListener l : listeners)
					l.itemWasSelected(this);
			}
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

			// if there was a plu item on the scanner tray then remove it
			if(weightofScannerTray != 0) {
				sc.station.scanningArea.remove(currentItem);
			}

			for (ItemsControlListener l : listeners) {
				l.itemsAreAvailableInCart(this);
				l.awaitingItemToBeSelected(this);
			}
		} catch (Exception e) {
			// exception should never occur since this code path is only ever called when
			// currentItem is not null
		}
	}

	public boolean addItemByPLU() {
		try {

			PLUCodedProduct product = ProductDatabases.PLU_PRODUCT_DATABASE.get(currentProductCode);
			double price;

			baggingAreaTimerStart = System.currentTimeMillis();

			System.out.println(product.getDescription());

//			pluItemSelected();

			double weight = weightofScannerTray - sc.getWeightOfScannerTray();
			System.out.println(weight + " Scale weight");

			sc.setWeightOfScannerTray(weightofScannerTray);
			sc.updateExpectedCheckoutWeight(weight);
			sc.updateWeightOfLastItemAddedToBaggingArea(weight);
			
			// Maybe add this to the right of the item in the checkout list
			System.out.println("Weight of item: " + weight);

			if(weight == 0.0) {
				System.err.println("Please place the item on the scale before entering the code!!");
				return false;
			}

			// price per kg
			price = (double) product.getPrice() * weight / 1000;
			this.addItemToCheckoutList(new Tuple<String, Double>(product.getDescription(), price));
			this.updateCheckoutTotal(price);

			System.out.println("Added item to checkout list!");

//			sc.goBackOnUI();
			sc.blockStation();
			for (ItemsControlListener l : listeners)
				l.awaitingItemToBePlacedInBaggingArea(this);

			return true;
		} catch(InvalidArgumentSimulationException | NullPointerSimulationException e) {
			System.err.println(e.toString());
			return false;
		}
	}

	public boolean getIsPLU() {
		return isPLU;
	}

	// TODO: scanItem now differtiates between using handheldScanner and mainScanner
	// ALSO: note that a new weight area called scanningArea exists now to grab
	// weight of items during general scanning phase
	public void scanCurrentItem(boolean useHandheld) {
		if (currentItem instanceof BarcodedItem) {
			baggingAreaTimerStart = System.currentTimeMillis();
			scanSuccess = false;
			sc.customer.scanItem(useHandheld);
			if (!scanSuccess) {
				// if scanSuccess is still false after listeners have been called, we can show
				// an alert showing a failed scan if time permits.
			}
		} else {
			System.err.println("Item does not have a barcode, please enter the PLU code!");
		}
	}

	public void placeItemOnBaggingArea() {
		if (currentItem instanceof BarcodedItem) {
			scaleExpectedWeight = sc.weightOfLastItem;
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
				System.out.println(wrongBaggedItem);
			}

			if (baggingAreaTimerEnd - baggingAreaTimerStart > 10000) {
				userMessage = "Please place item on scale!";
				// not blocking station for now
				// sc.blockStation();
			}

			if (!weighSuccess) {
				// if weighSuccess is still false after listeners have been called, we can show
				// and alert showing a failed weigh-in if time permits.
			}
		} else {
			sc.customer.placeItemInBaggingArea();
			System.out.println("expected: " + sc.getExpectedWeight());
			try {
				System.out.println("actual: " + sc.station.baggingArea.getCurrentWeight());
			} catch (OverloadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * Weighs the item before entering the plu code.
	 */
	public void weighItem() {
		if(isPLU) {
			System.out.println("Weighing item!!");
			sc.station.scanningArea.add(currentItem);

		} else {
			System.err.println("No need to weigh a barcoded item!!");
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

	private Barcode searchBarcodedProductDatabase(String strProductName) {
		Barcode result = null;
		for (Entry<Barcode, BarcodedProduct> entry : ProductDatabases.BARCODED_PRODUCT_DATABASE.entrySet()) {
			if (entry.getValue().getDescription().compareTo(strProductName) == 0) {
				result = entry.getKey();
				break;
			}
		}
		return result;
	}

	private PriceLookUpCode searchPLUCodedProductDatabase(String strProductName) {
		PriceLookUpCode result = null;

		for (Entry<PriceLookUpCode, PLUCodedProduct> entry : ProductDatabases.PLU_PRODUCT_DATABASE.entrySet()) {
			if (entry.getValue().getDescription().compareTo(strProductName) == 0) {

				result = entry.getKey();
			}
		}
		return result;
	}

	private void addItemByBrowsing(String strProductName) {
		Barcode barcodeIdentifier = null;
		PriceLookUpCode PLUCodeIdentifier = null;

		boolean isItemSelected = false;

		barcodeIdentifier = searchBarcodedProductDatabase(strProductName);
		if (barcodeIdentifier != null) {
			addScannedItemToCheckoutList(barcodeIdentifier);
			isItemSelected = true;
		}

		PLUCodeIdentifier = searchPLUCodedProductDatabase(strProductName);
		if (PLUCodeIdentifier != null) {
			currentProductCode = PLUCodeIdentifier;
			pluItemSelected();
//			isItemSelected = true;
		}

		// FIXME: prompt place on scanning area instead
		if (isItemSelected) {
			sc.goBackOnUI();

			inCatalog = false;
		}
	}

	public void pluItemSelected() {
		try {
			// Check with product database and update inventory
			Product product = findProduct(currentProductCode);
			checkInventory(product);
			
			if(!isPLU) {
				System.err.println("The currently selected item has no PLU code! Or there is no item selected!");
				currentProductCode = null;
				sc.goBackOnUI();
			} else if(expectedPLU.hashCode() != currentProductCode.hashCode()) {
				System.err.println("You entered the wrong PLU code for the item!");
				System.err.printf("The expected PLU code is %s\n", expectedPLU);
			} else {
				inCatalog = false;
				
				// Signal scanning area to wait for item to be placed on
				for (ItemsControlListener l : listeners)
					l.awaitingItemToBePlacedInScanningArea(sc);
//				sc.goBackOnUI();
			}
			
		}catch(NullPointerSimulationException e) {
			System.err.println("PLU code does not exist in the database");
		} 
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();

		if (inCatalog) {
			addItemByBrowsing(c);
			if (c.compareTo("cancel catalog") == 0) {
				sc.goBackOnUI();
				inCatalog = false;
			}
		} else {
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
				case "enter plu":
					System.out.println("Customer entered a PLU Code");
					sc.startPLUCodeWorkflow();
					break;
				case "put back":
					System.out.println("Customer put back current item");
					putUnscannedItemBack();
					break;
				case "bag":
					System.out.println("Customer put item in bagging area");
					placeItemOnBaggingArea();
					break;
				case "pay":
					System.out.println("Starting payment workflow");
					sc.startPaymentWorkflow();
					break;
				case "member":
					sc.startMembershipWorkflow();
					break;
				case "catalog":
					inCatalog = true;
					sc.startCatalogWorkflow();
					break;
				case "weigh":
					weighItem();
					break;
				default:
					break;
				}
			} catch (Exception ex) {

			}
		}
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
	public void barcodeScanned(BarcodeScanner barcodeScanner, Barcode barcode) {
		if (!sc.isMembershipInput()) {
			scanSuccess = true;
			sc.blockStation();

			// Verify product and inventory in the product database
			Product product = findProduct(barcode);
			checkInventory(product);

			System.out.println("scanned item");

			sc.weightOfLastItem = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode).getExpectedWeight();

			// Add the barcode to the ArrayList within itemControl
			this.addScannedItemToCheckoutList(barcode);
			// Set the expected weight in SystemControl
			sc.updateExpectedCheckoutWeight(sc.weightOfLastItem);
			sc.updateWeightOfLastItemAddedToBaggingArea(sc.weightOfLastItem);

			for (ItemsControlListener l : listeners)
				l.awaitingItemToBePlacedInBaggingArea(this);
		}
	}

	private void checkInventory(Product product) {
		if (ProductDatabases.INVENTORY.containsKey(product) && ProductDatabases.INVENTORY.get(product) >= 1) {
			ProductDatabases.INVENTORY.put(product, ProductDatabases.INVENTORY.get(product) - 1); // updates INVENTORY
																									// with new total
		} else {
			// TODO: inform customer and attendant
			System.out.print("Out of stock");
		}
	}

	private BarcodedProduct findProduct(Barcode Barcode) throws NullPointerSimulationException {
		if (ProductDatabases.BARCODED_PRODUCT_DATABASE.containsKey(Barcode)) {
			return ProductDatabases.BARCODED_PRODUCT_DATABASE.get(Barcode);
		} else {
			// TODO: Inform customer station
			System.out.println("Cannot find the product. Please try again or ask for assistant!");
			throw new NullPointerSimulationException();
		}
	}
	
	private PLUCodedProduct findProduct(PriceLookUpCode code) throws NullPointerSimulationException {
		if(ProductDatabases.PLU_PRODUCT_DATABASE.containsKey(code)) {
					return ProductDatabases.PLU_PRODUCT_DATABASE.get(code);        
			}
		else {
			// TODO: Inform customer station
			System.out.println("Cannot find the product. Please try again or ask for assistant!");
			throw new NullPointerSimulationException();
		}
	}

	/**
	 * sets user message to announce weight on the indicated scale has changed
	 * 
	 * @param scale         The scale where the event occurred.
	 * @param weightInGrams The new weight.
	 */
	@Override
	public void weightChanged(ElectronicScale scale, double weightInGrams) {
		if (scale == sc.station.baggingArea) {
			weighSuccess = true;
			for (ItemsControlListener l : listeners)
				l.awaitingItemToBeSelected(this);
			if (sc.expectedWeightMatchesActualWeight(weightInGrams)) {
				sc.unblockStation();
				userMessage = "Weight of scale has changed to: " + weightInGrams;
			} else {
				// System.out.println("Expected: " + sc.getExpectedWeight() + "Added: " +
				// weightInGrams);
				String weightDescrepancyMessage = "Expected item weight of: " + scaleExpectedWeight + ", "
						+ "Weight bagged: " + scaleReceivedWeight
						+ ". Weight Descrepancy detected please bag the right item";
				String weightDescrepancyMessageAttendant = "Expected item weight of: " + scaleExpectedWeight + ", "
						+ "Weight bagged: " + scaleReceivedWeight + ". Customer bagged the wrong item";
				for (ItemsControlListener l : listeners)
					l.awaitingItemToBeRemoved(this, weightDescrepancyMessage);

				sc.getAttendantControl().updateWeightDescrepancyMessage(weightDescrepancyMessageAttendant);
				;

			}
			if (removedWrongBaggedItem) {
				sc.unblockStation();
				for (ItemsControlListener l : listeners)
					l.awaitingItemToBeSelected(this);
			}
		} else {
			weightofScannerTray = weightInGrams;	
			addItemByPLU();
		}
	}

	@Override
	public void overload(ElectronicScale scale) {
		userMessage = "Weight on scale has been overloaded, weight limit is: "
				+ sc.station.baggingArea.getWeightLimit();
	}

	@Override
	public void outOfOverload(ElectronicScale scale) {
		userMessage = "Excessive weight removed, continue scanning";
		sc.unblockStation();
	}

	public Item getCurrentItem() {
		return currentItem;
	}
	
	public void setCurrentProduct(PriceLookUpCode code) {
		currentProductCode = code;
	}

	@Override
	public void pluCodeEntered(PLUCodeControl pcc, String pluCode) {
		// pass in plu input
		PriceLookUpCode code = new PriceLookUpCode(pluCode);
		setCurrentProduct(code);
		
		// next step: scanning area
		pluItemSelected();
	}

	@Override
	public void pluErrorMessageUpdated(PLUCodeControl pcc, String errorMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pluHasBeenUpdated(PLUCodeControl pcc, String pluCode) {
		// TODO Auto-generated method stub
		
	}
}
