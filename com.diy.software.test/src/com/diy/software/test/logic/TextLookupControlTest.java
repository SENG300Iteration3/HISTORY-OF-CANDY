package com.diy.software.test.logic;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.controllers.TextLookupControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.AttendantControlListener;
import com.diy.software.listeners.TextLookupControlListener;
import com.jimmyselectronics.necchi.BarcodedItem;

import ca.powerutility.PowerGrid;

public class TextLookupControlTest {
	TextLookupControl tlc;
	StationControl sc;
	AttendantControl ac;
	TextLookupStub tls;
	FakeDataInitializer fdi;
	BarcodedItem item1;
	BarcodedItem item2;
	BarcodedItem[] items;
	
	@Before
	public void setup() {
		PowerGrid.engageUninterruptiblePowerSource();
		
		fdi = new FakeDataInitializer();
		fdi.addProductAndBarcodeData();
		items = fdi.getItems();
		item1 = items[0];
		item2 = items[1];
		
		// needs to add PLU coded items to fake data to test
		
		sc = new StationControl(fdi);
		ac = new AttendantControl(sc);
		tlc = ac.getTextLookupControl();
		tls = new TextLookupStub();
	}
	
	@Test
	public void testAddListener() {
		
	}
	
	@Test
	public void testRemoveListener() {

	}
	
	@Test
	public void testFindProduct() {

	}
	
	@Test
	public void testAddProduct() {

	}
	
	@Test
	public void testPlaceProductInBaggingArea() {

	}
	
	@Test
	public void testClearSearch() {

	}
	
	@Test
	public void testUpdateGUI() {

	}

	
	public class TextLookupStub implements TextLookupControlListener {
		boolean readyToSearch = false;
		boolean resultChosen = false;
		boolean itemBagged = false;
		boolean updatedCheckout = false;
		boolean searchCleared = false;

		@Override
		public void searchQueryWasEntered(TextLookupControl tlc) {
			readyToSearch = true;
			
		}

		@Override
		public void resultWasChosen(TextLookupControl tlc) {
			resultChosen = true;
			
		}

		@Override
		public void itemHasBeenBagged(TextLookupControl tlc) {
			itemBagged = true;
			
		}

		@Override
		public void checkoutHasBeenUpdated(TextLookupControl tlc) {
			updatedCheckout = true;
			
		}
		
		@Override
		public void searchHasBeenCleared(TextLookupControl tlc) {
			searchCleared = true;
			
		}
		
	}
	
	public class AttendantListenerStub implements AttendantControlListener {
    	boolean attendantBlocked = false;
    	boolean noBaggingRequested = false;
    	
    	@Override
    	public void attendantApprovedBags(AttendantControl ac) {
    		// TODO Auto-generated method stub
    	}

		@Override
		public void attendantPreventUse(AttendantControl ac) {
			attendantBlocked = true;
		}

		@Override
		public void addPaperState() {
			// TODO Auto-generated method stub
		}

		@Override
		public void addInkState() {
			// TODO Auto-generated method stub
		}

		@Override
		public void printerNotLowState() {
			// TODO Auto-generated method stub
		}

		@Override
		public void signalWeightDescrepancy(String updateMessage) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void noBaggingRequestState() {
			noBaggingRequested = true;
			
		}

		@Override
		public void initialState() {
			// TODO Auto-generated method stub
			
		}
    }
	
}