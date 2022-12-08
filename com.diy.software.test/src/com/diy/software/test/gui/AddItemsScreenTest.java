package com.diy.software.test.gui;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.hardware.PriceLookUpCode;
import com.diy.software.controllers.BagsControl;
import com.diy.software.controllers.ItemsControl;
import com.diy.software.controllers.PaymentControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.BagsControlListener;
import com.diy.software.listeners.ItemsControlListener;
import com.diy.software.test.logic.StubSystem;
import com.diy.software.util.Tuple;

import ca.powerutility.PowerGrid;
import swing.screens.AddItemsScreen;
import swing.styling.GUI_JButton;
import swing.styling.GUI_JLabel;
import swing.styling.GUI_JPanel;

public class AddItemsScreenTest {
	FakeDataInitializer fdi;
	StationControl sc;
	PaymentControl pc;
	AddItemsScreen screen;
	StubSystem sStub;
	BagsControlListenerStub bStub;
	ItemsControlListenerStub iStub;
	
	GUI_JButton payBtn;
	GUI_JButton memberBtn;

	GUI_JButton addOwnBagsBtn;
	GUI_JButton removeItemBtn;

	GUI_JButton purchaseBagsBtn;
	GUI_JButton addItemByPLUBtn;
	GUI_JButton catalogBtn;
	
	GUI_JPanel scannedPanel;
	GUI_JLabel subtotalLabel;

	@Before
	public void setUp() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();
		
		fdi = new FakeDataInitializer();
		fdi.addPLUCodedProduct();
		sc = new StationControl(fdi);
		screen = new AddItemsScreen(sc);
		
		bStub = new BagsControlListenerStub();
		sc.getBagsControl().addListener(bStub);
		
		iStub = new ItemsControlListenerStub();
		sc.getItemsControl().addListener(iStub);
		
		sStub = new StubSystem();
		sc.register(sStub);
		
		payBtn = screen.payBtn;
		memberBtn = screen.memberBtn;
		
		addOwnBagsBtn = screen.addOwnBagsBtn;
		removeItemBtn = screen.removeItemBtn;

		purchaseBagsBtn = screen.purchaseBagsBtn;
		addItemByPLUBtn = screen.addItemByPLUBtn;
		catalogBtn = screen.catalogBtn;
		scannedPanel = screen.scannedPanel;
		subtotalLabel = screen.subtotalLabel;
	}

	@After
	public void tearDown() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();
	}

	@Test
	public void testPayBtn() {
		payBtn.doClick();
		assertTrue(sStub.triggerPaymentWorkflow);
	}
	
	@Test
	public void testMemberBtn() {
		memberBtn.doClick();
		assertTrue(sStub.triggerMembershipWorkflow);
	}
	
	@Test
	public void testAddOwnBagsBtn() {
		addOwnBagsBtn.doClick();
		assertTrue(bStub.waitForPlaceBags);
	}
	
	@Test
	public void testRemoveItemBtn() {
		removeItemBtn.setEnabled(true);
		removeItemBtn.doClick();
		assertTrue(iStub.waitForApproveRemove);
	}
	
	@Test
	public void testPurchaseBagsBtn() {
		purchaseBagsBtn.doClick();
		assertTrue(sStub.triggerPurchaseBagsWorkflow);
	}
	
	@Test
	public void testAddItemByPLUBtn() {
		addItemByPLUBtn.setEnabled(true);
		addItemByPLUBtn.doClick();
		assertTrue(sStub.startPLUCodeWorkflow);
	}
	
	@Test
	public void testCatalogBtn() {
		catalogBtn.setEnabled(true);
		catalogBtn.doClick();
		assertTrue(sStub.triggerCatalogWorkflow);
	}

	@Test
	public void testInvalidateAllScannedItems() {
		testAddScannedItem();
		screen.invalidateAllScannedItems();
		assertTrue(scannedPanel.getComponentCount() == 0);
		
	}

	@Test
	public void testAddScannedItem() {
		screen.addScannedItem("KEtcHup", 1000);
		screen.addScannedItem("muStuRD", 500);
		screen.addScannedItem("rEaliSh", 250);
		assertTrue(scannedPanel.getComponentCount() == 3);
	}

	@Test
	public void testUpdateSubtotal() {
		screen.updateSubtotal(1750);
		assertTrue(subtotalLabel.getText().equals("$1750.00"));
	}

	@Test
	public void testAwaitingItemToBeSelected() {
		screen.awaitingItemToBeSelected(sc.getItemsControl());
		assertFalse(addItemByPLUBtn.isEnabled());
		assertFalse(catalogBtn.isEnabled());
	}

	@Test
	public void testItemWasSelected() {
		screen.itemWasSelected(sc.getItemsControl());
		assertTrue(addItemByPLUBtn.isEnabled());
		assertTrue(catalogBtn.isEnabled());
	}

	
	@Test
	public void testItemsHaveBeenUpdatedNoItems() {
		removeItemBtn.setEnabled(true);
		screen.itemsHaveBeenUpdated(sc.getItemsControl());
		assertFalse(removeItemBtn.isEnabled());
	}
	
	@Test
	public void testItemsHaveBeenUpdated() {
		removeItemBtn.setEnabled(false);
		sc.getItemsControl().addItemToCheckoutList(new PriceLookUpCode("11111"));
		sc.getItemsControl().addItemToCheckoutList(new PriceLookUpCode("23456"));
		screen.itemsHaveBeenUpdated(sc.getItemsControl());
		assertTrue(scannedPanel.getComponentCount() == 2);
		assertTrue(removeItemBtn.isEnabled());
	}
	@Test
	public void testProductSubtotalUpdated() {
		sc.getItemsControl().updateCheckoutTotal(11.00);
		screen.itemsHaveBeenUpdated(sc.getItemsControl());
		screen.productSubtotalUpdated(sc.getItemsControl());
		assertTrue(subtotalLabel.getText().equals("Subtotal: $11.00"));
	}


	public class BagsControlListenerStub implements BagsControlListener{

		public boolean readyForNewBags = false;
		public boolean waitForVerifyBags = false;
		public boolean waitForPlaceBags = false;

		@Override
		public void awaitingCustomerToFinishPlacingBagsInBaggingArea(BagsControl bc) {
			waitForPlaceBags = true;
			
		}

		@Override
		public void awaitingAttendantToVerifyBagsPlacedInBaggingArea(BagsControl bc) {
			waitForVerifyBags = true;
			
		}

		@Override
		public void readyToAcceptNewBagsInBaggingArea(BagsControl bc) {
			readyForNewBags = true;
			
		}
		
	}
	
	public class ItemsControlListenerStub implements ItemsControlListener{

		public boolean waitForApproveRemove = false;

		@Override
		public void awaitingItemToBeSelected(ItemsControl ic) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void itemWasSelected(ItemsControl ic) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void awaitingItemToBePlacedInBaggingArea(ItemsControl ic) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void awaitingItemToBePlacedInScanningArea(StationControl sc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void noMoreItemsAvailableInCart(ItemsControl ic) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void itemsAreAvailableInCart(ItemsControl ic) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void awaitingItemToBeRemoved(ItemsControl itemsControl, String updateMessage) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void itemRemoved(ItemsControl itemsControl) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void itemsHaveBeenUpdated(ItemsControl ic) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void productSubtotalUpdated(ItemsControl ic) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void awaitingAttendantToApproveItemRemoval(ItemsControl ic) {
			waitForApproveRemove = true;
			
		}
		
	}
}
