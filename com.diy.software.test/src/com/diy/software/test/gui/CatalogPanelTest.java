package com.diy.software.test.gui;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.ItemsControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.ItemsControlListener;
import com.diy.software.test.logic.StubSystem;

import swing.panels.CatalogPanel;

public class CatalogPanelTest {
	
	CatalogPanel panel;
	StationControl sc;
	ItemsControl ic;
	FakeDataInitializer fdi;
	StubSystem sStub;
	ItemsControlListenerStub iStub;

	@Before
	public void setUp() throws Exception {
		fdi = new FakeDataInitializer();
		fdi.addPLUCodedProduct();
		
		sc = new StationControl(fdi);
		ic = sc.getItemsControl();
		ic.setInCatalog(true);
	
		panel = new CatalogPanel(sc);
		
		sStub = new StubSystem();
		sc.register(sStub);
		
		iStub = new ItemsControlListenerStub();
		ic.addListener(iStub);
		
		ic.pickupNextItem();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testItemButton() {
		ic.setIsPLU(true);
		ic.pluCodeEntered(sc.getPLUCodeControl(), "23456");
		panel.getItemButtons().get(4).doClick();
		assertTrue(sStub.triggerCatalogWorkflow);
		assertTrue(iStub.awaitingItemToBePlacedInBaggingArea);
	}
	
	@Test
	public void testCancelButton() {
		panel.getCancelBtn().doClick();
		assertTrue(sStub.triggerPanelBack);
		
	}
	
	public class ItemsControlListenerStub implements ItemsControlListener{
		
		public boolean awaitingItemToBePlacedInBaggingArea = false;

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
			awaitingItemToBePlacedInBaggingArea = true;
			
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
			// TODO Auto-generated method stub
			
		}
		
	}
}
