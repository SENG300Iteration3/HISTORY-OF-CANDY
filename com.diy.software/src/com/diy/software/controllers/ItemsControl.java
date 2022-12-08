package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
	private ArrayList<Object> checkoutList = new ArrayList<>();
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

	public void setIsPLU(boolean proposition) {
		isPLU = proposition;
	}
	
	public void setExpectedPLU(PriceLookUpCode pcode) {
		expectedPLU = pcode;
	}

	public void setCurrentProductCode(PriceLookUpCode pcode) {
		currentProductCode = pcode;
	}
	public void setCurrentItem(Item item) {
		currentItem = item;
	}

	public boolean getInCatalog() {
		return inCatalog;
	}

	public void addListener(ItemsControlListener l) {
		listeners.add(l);
	}

	public void removeListener(ItemsControlListener l) {
		listeners.remove(l);
	}


	public void addItemToCheckoutList(Object item) {
		checkoutList.add(item);
		refreshGui();
	}

	public void addItemToCheckoutList(Barcode barcode, PriceLookUpCode pluCode) {
		if (barcode == null) {
			checkoutList.add(pluCode);
		}
		else if (pluCode == null) {
			checkoutList.add(barcode);
		}
		refreshGui();
	}

	public void addScannedItemToCheckoutList(Barcode barcode) {
		BarcodedProduct barcodedProduct = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
		double price;
		if (barcodedProduct != null) {
			price = (double) barcodedProduct.getPrice();
			this.addItemToCheckoutList(barcode, null);
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
	//	Resets the data in ItemControl. 
	public void resetState() {
		checkoutList = new ArrayList<>();
		tempList = new ArrayList<>();
		checkoutListTotal = 0.0;
		scanSuccess = true;
		weighSuccess = true;
		userMessage = "";
		baggingAreaTimerStart = -1; // Setting to -1 b/c I can't set this to null
		baggingAreaTimerEnd = -1; // Not sure if gonna be problematic. 
		refreshGui();
	}
	
	public void notifyItemRemoved() {
		for (ItemsControlListener l : listeners){
			l.itemRemoved(this);
		}
	}
	

	/**
	 * Method that checks to see if an integer passed in as argument is the same as
	 * one of the reusable bags item numbers on screen.
	 * 
	 * @param index
	 * 		integer that matches an item number displayed on the customer GUI
	 * @return
	 * 		true if the index refers to a bag item number, false otherwise
	 */
	public boolean isIndexBag(int index) {
		if (this.bags.size() > 0 && this.checkoutList.size() < index) {
			if (index <= (this.checkoutList.size() + this.bags.size())){
				return true;
			}
			return false;
		}		
		else return false;
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
		double weight;
		double price;
		if (index <= 0) return false;
		else if (isIndexBag(index)) {
			weight = 5.0;
			price = sc.fakeData.getReusableBagPrice();
			this.sc.updateExpectedCheckoutWeight(-weight);
			this.updateCheckoutTotal(-price);	
			this.sc.station.baggingArea.remove(bags.get(0));
			this.bags.remove(0);
			refreshGui();
			return true;
		}
		else if (index <= this.checkoutList.size()) {
			Item item;
			index--; // decrement index so it matches actual array index!
			if (this.checkoutList.get(index) instanceof Barcode) {
				Barcode barcode = (Barcode) this.checkoutList.get(index);
				// getting the actual object that the customer had in his shopping cart and was subsequently added to the baggingArea
				item = (BarcodedItem) this.sc.barcodedItems.get(barcode);
				price = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode).getPrice();
				weight = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode).getExpectedWeight();
			}
			else {
				PriceLookUpCode pluCode = (PriceLookUpCode) this.checkoutList.get(index);
				// getting the actual object that the customer had in his shopping cart and was subsequently added to the baggingArea
				item = (PLUCodedItem) this.sc.pluCodedItems.get(pluCode);
				weight = item.getWeight();
				price = ProductDatabases.PLU_PRODUCT_DATABASE.get(pluCode).getPrice() * weight / 1000;	
			}
			this.updateCheckoutTotal(-price);				// decrement price
			this.sc.updateExpectedCheckoutWeight(-weight);  // decrement weight
			this.sc.station.baggingArea.remove(item);
			checkoutList.remove(index); // remove the barcode or PLUCode from checkoutList so GUI updates accordingly
			refreshGui();
			//sc.goToInitialScreenOnUI();
			return true;
		}
		else {
			return false;
		}

	}
	
	public ArrayList<Tuple<String, Double>> getItemDescriptionPriceList() {
		 ArrayList<Tuple<String, Double>> list = new ArrayList<>();
		 double price;
		 String description;
		 for (int i = 0; i < this.checkoutList.size(); i ++) {
				if (this.checkoutList.get(i) instanceof Barcode) {
					Barcode barcode = (Barcode) this.checkoutList.get(i);
					price = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode).getPrice();
					description = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode).getDescription();
				}
				else {
					PriceLookUpCode pluCode = (PriceLookUpCode) this.checkoutList.get(i);
					PLUCodedItem item = (PLUCodedItem) this.sc.pluCodedItems.get(pluCode);
					double weight = item.getWeight(); // When PLU coded items are made they will have to be added to pluCodedItems along with the PLU code
					price = ProductDatabases.PLU_PRODUCT_DATABASE.get(pluCode).getPrice() * weight / 1000;	
					description = ProductDatabases.PLU_PRODUCT_DATABASE.get(pluCode).getDescription();
				}
			list.add(new Tuple<String, Double>(description, price));
		 }
		 double reusableBagPrice = this.sc.fakeData.getReusableBagPrice();
		 for (int i = 0; i < bags.size(); i++) {
			 list.add(new Tuple<String, Double>("Reusable Bag", reusableBagPrice));
		 }
		 return list;
	}
	
	public ArrayList<Object> getCheckoutList() {
		return checkoutList;
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
		refreshGui();
	}

	public double getCheckoutTotal() {
		return checkoutListTotal;
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
			this.addItemToCheckoutList(currentProductCode);
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
			scaleExpectedWeight = sc.weightOfLastItem;
			weighSuccess = false;
			baggingAreaTimerEnd = System.currentTimeMillis();
			// placing an item could potentially fail so allow for retries
			// simulating a 40% chance of putting wrong item on the scale
			if (random.nextDouble(0.0, 1.0) > PROBABILITY_OF_BAGGING_WRONG_ITEM) {
				weighSuccess = true;
				sc.customer.placeItemInBaggingArea();
				sc.getAttendantControl().itemBagged();			// cancel no bag request if there is one
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

		if (!isPLU) {
			System.err.println("The currently selected item has no PLU code! Or there is no item selected!");
			currentProductCode = null;
			inCatalog = false;
			sc.goBackOnUI();
		} else {
			PriceLookUpCode PLUCodeIdentifier = searchPLUCodedProductDatabase(strProductName);
			if (PLUCodeIdentifier != null) {
				currentProductCode = PLUCodeIdentifier;
				pluItemSelected(); // FIXME: can probably remove this line and replace with below commented
			}
		}
	}

	public void pluItemSelected() {
		try {
			// Check with product database and update inventory
			Product product = findProduct(currentProductCode);
			checkInventory(product);
		}catch (NullPointerSimulationException e) {

			System.err.println("PLU code does not exist in the database");
			throw new NullPointerException("PLU code does not exist in the database");
		}
			if(!isPLU) {

				System.err.println("The currently selected item has no PLU code! Or there is no item selected!");
				currentProductCode = null;
				sc.goBackOnUI();;
			} else if(expectedPLU.hashCode() != currentProductCode.hashCode()) {

				System.err.println("You entered the wrong PLU code for the item!");
				System.err.printf("The expected PLU code is %s\n", expectedPLU);
				throw new NullPointerException("You entered the wrong PLU code for the item!");
			} else {
				inCatalog = false;
				// Signal scanning area to wait for item to be placed on
				for (ItemsControlListener l : listeners)
					l.awaitingItemToBePlacedInScanningArea(sc);
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
				case "remove item":
					System.out.println("Customer requests item removal");
					requestRemoveItem();
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

	public ArrayList<ReusableBag> getBagsList() {
		return this.bags;
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
