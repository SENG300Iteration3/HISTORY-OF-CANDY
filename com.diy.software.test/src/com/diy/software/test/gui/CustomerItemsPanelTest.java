package com.diy.software.test.gui;

import static org.junit.Assert.*;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.BagsControl;
import com.diy.software.controllers.ItemsControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.AttendantControlListener;
import com.diy.software.listeners.BagsControlListener;
import com.diy.software.listeners.ItemsControlListener;
import com.diy.software.test.logic.StubSystem;
import com.unitedbankingservices.coin.CoinStorageUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import swing.panels.CustomerItemsPanel;
import swing.screens.AddItemsScreen;
import swing.screens.AddOwnBagsPromptScreen;
import swing.screens.BlockedPromptScreen;

public class CustomerItemsPanelTest {

	CustomerItemsPanel panel;
	StationControl sc;
	FakeDataInitializer fdi;
	StubSystem sStub;
	ItemControllerStub icStub;
	AttendantControllerStub acStub;

	AddItemsScreen addItemsScreen;
	AddOwnBagsPromptScreen addOwnBagsPromptScreen;
	AttendantControl ac;

	BagControllerStub bcStub;



	@Before
	public void setUp() throws Exception {

	fdi = new FakeDataInitializer();
	fdi.addProductAndBarcodeData();
	sc = new StationControl(fdi);
	panel = new CustomerItemsPanel(sc);

	sStub = new StubSystem();
	sc.register(sStub);

	icStub = new ItemControllerStub();
	sc.getItemsControl().addListener(icStub);

	bcStub = new BagControllerStub();
	sc.getBagsControl().addListener(bcStub);

	ac = new AttendantControl(sc);
	sc.getAttendantControl().addListener(acStub);

	}

	@After
	public void tearDown() throws Exception {

		fdi = null;
		sc = null;
		panel = null;
		sStub = null;
		icStub = null;

	}

	@Test
	public void testSelectNextItemButton() {
		panel.getSelectNextItemButton().doClick();
		assertTrue(icStub.itemWasSelected);
	}


	@Test
	public void testSelectNextItemButtonDisabled() {
		panel.getSelectNextItemButton().setEnabled(false);
		assertFalse(icStub.itemWasSelected);
	}


	@Test
	public void testMainScannerButton() {

		panel.getSelectNextItemButton().doClick();
		panel.getMainScannerButton().doClick();
		assertTrue(icStub.awaitingItemToBePlacedInBaggingArea);

	}

	@Test
	public void testHandHeldScannerButton() {

		panel.getSelectNextItemButton().doClick();
		panel.getHandheldScannerButton().doClick();
		assertTrue(icStub.awaitingItemToBePlacedInBaggingArea);

	}

	@Test
	public void testDeselectItemButton() {

		panel.getSelectNextItemButton().doClick();
		panel.getDeselectCurrentItemButton().doClick();
		assertTrue(icStub.awaitingItemToBeSelected);

	}

	@Test
	public void testItemWeightButton() {

		panel.getSelectNextItemButton().doClick();
		panel.getMainScannerButton().doClick();
		panel.getItemWeight().doClick();
		assertTrue(icStub.awaitingItemToBePlacedInBaggingArea);

	}

	@Test
	public void testPlaceItemInBaggingAreaButton() {

		panel.getSelectNextItemButton().doClick();
		panel.getMainScannerButton().doClick();
		panel.getItemWeight().doClick();


		panel.getPlaceItemInBaggingAreaButton().doClick();
		assertTrue(icStub.awaitingItemToBeSelected);

	}

	@Test
	public void testAddOwnBags() {

		addItemsScreen = new AddItemsScreen(sc);
		addItemsScreen.addOwnBagsBtn.doClick();
		//assertTrue(icStub.awaitingAttendant);


		addOwnBagsPromptScreen = new AddOwnBagsPromptScreen(sc, "Done");
		addOwnBagsPromptScreen.getDoneAddingBagsButton().doClick();
		assertTrue(bcStub.awaitingAttendant);

	}

	@Test
	public void testRemoveItem() {

		panel.getSelectNextItemButton().doClick();
		panel.getMainScannerButton().doClick();
		panel.getItemWeight().doClick();
		panel.getPlaceItemInBaggingAreaButton().doClick();

		addItemsScreen = new AddItemsScreen(sc);
		addItemsScreen.removeItemBtn.doClick();
		assertTrue(icStub.awaitingAttendant);


	}



	public class ItemControllerStub implements ItemsControlListener {

		public boolean awaitingItemToBeSelected = false;
		public boolean itemWasSelected = false;
		public boolean awaitingItemToBePlacedInBaggingArea = false;
		public boolean awaitingAttendant = false;

		@Override
		public void awaitingItemToBeSelected(ItemsControl ic) {
			awaitingItemToBeSelected = true;
		}

		@Override
		public void itemWasSelected(ItemsControl ic) {
			itemWasSelected = true;
		}

		@Override
		public void awaitingItemToBePlacedInBaggingArea(ItemsControl ic) {
			awaitingItemToBePlacedInBaggingArea = true;
		}

		@Override
		public void awaitingItemToBePlacedInScanningArea(StationControl sc) {

		}

		@Override
		public void noMoreItemsAvailableInCart(ItemsControl ic) {

		}

		@Override
		public void itemsAreAvailableInCart(ItemsControl ic) {

		}

		@Override
		public void awaitingItemToBeRemoved(ItemsControl itemsControl, String updateMessage) {

		}

		@Override
		public void itemRemoved(ItemsControl itemsControl) {

		}

		@Override
		public void itemsHaveBeenUpdated(ItemsControl ic) {

		}

		@Override
		public void productSubtotalUpdated(ItemsControl ic) {

		}

		@Override
		public void awaitingAttendantToApproveItemRemoval(ItemsControl ic) {
			awaitingAttendant = true;
		}
	}

	public class BagControllerStub implements BagsControlListener {

		public boolean awaitingAttendant = false;
		public boolean awaitingCustomer = false;

		@Override
		public void awaitingCustomerToFinishPlacingBagsInBaggingArea(BagsControl bc) {
			awaitingCustomer = true;
		}

		@Override
		public void awaitingAttendantToVerifyBagsPlacedInBaggingArea(BagsControl bc) {
			awaitingAttendant = true;
		}

		@Override
		public void readyToAcceptNewBagsInBaggingArea(BagsControl bc) {

		}
	}

	public class AttendantControllerStub implements AttendantControlListener {

		@Override
		public void attendantApprovedBags(AttendantControl ac) {

		}

		@Override
		public void attendantPreventUse(AttendantControl ac) {

		}

		@Override
		public void attendantApprovedItemRemoval(AttendantControl bc) {

		}

		@Override
		public void lowInk(AttendantControl ac, String message) {

		}

		@Override
		public void lowPaper(AttendantControl ac, String message) {

		}

		@Override
		public void printerNotLowState() {

		}

		@Override
		public void outOfInk(AttendantControl ac, String message) {

		}

		@Override
		public void outOfPaper(AttendantControl ac, String message) {

		}

		@Override
		public void signalWeightDescrepancy(String updateMessage) {

		}

		@Override
		public void noBagRequest() {

		}

		@Override
		public void itemBagged() {

		}

		@Override
		public void initialState() {

		}

		@Override
		public void banknotesInStorageLowState() {

		}

		@Override
		public void attendantPermitStationUse(AttendantControl ac) {

		}

		@Override
		public void loggedIn(boolean isLoggedIn) {

		}

		@Override
		public void printerNotLowInkState() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void printerNotLowPaperState() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addTooMuchInkState() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addTooMuchPaperState() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void coinIsLowState(int amount) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void banknotesNotLowState() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void coinsNotLowState() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void triggerItemSearchScreen(AttendantControl ac) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void exitTextSearchScreen(AttendantControl ac) {
			// TODO Auto-generated method stub
			
		}
	}


}
