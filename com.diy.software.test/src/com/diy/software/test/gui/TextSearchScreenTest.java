package com.diy.software.test.gui;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.controllers.TextLookupControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.AttendantControlListener;
import com.diy.software.listeners.TextLookupControlListener;

import ca.powerutility.PowerGrid;
import swing.screens.TextSearchScreen;
import swing.styling.GUI_JButton;

public class TextSearchScreenTest {
	
	FakeDataInitializer fdi;
	TextSearchScreen screen;
	StationControl sc;
	AttendantControl ac;
	TextLookupControlListenerStub tStub;
	AttendantControlListenerStub aStub;

	@Before
	public void setUp() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();
		
		fdi = new FakeDataInitializer();
		fdi.addPLUCodedProduct();
		fdi.addProductAndBarcodeData();
		
		sc = new StationControl(fdi);
		ac = sc.getAttendantControl();
		
		screen = new TextSearchScreen(sc, ac);
		
		tStub = new TextLookupControlListenerStub();
		ac.getTextLookupControl().addListener(tStub);
		
		aStub = new AttendantControlListenerStub();
		ac.addListener(aStub);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBackButton() {
		screen.getBackButton().doClick();
		assertTrue(aStub.exitTextSearchScreen);
	}
	
	@Test
	public void testSearchButtonResultsFound() {
		screen.getSearchbar().setText("e");
		screen.getSearchButton().doClick();
		assertTrue(tStub.resultsFound);
	}
	
	@Test
	public void testSearchButtonNoResultsFound() {
		screen.getSearchbar().setText("qqqqqq");
		screen.getSearchButton().doClick();
		assertFalse(tStub.resultsFound);
	}
	
	@Test
	public void testItemButton() {
		screen.getSearchbar().setText("e");
		screen.getSearchButton().doClick();
		GUI_JButton itemButton = screen.getItemButtonList().get(1);
		itemButton.doClick();
		
		assertTrue(tStub.itemAddedToCheckout);
	}
	
	public class TextLookupControlListenerStub implements TextLookupControlListener{
		
		public boolean resultsFound;
		public boolean itemAddedToCheckout;

		@Override
		public void searchQueryWasEntered(TextLookupControl tlc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void resultsWereFound(TextLookupControl tlc) {
			resultsFound = true;
			
		}

		@Override
		public void noResultsWereFound(TextLookupControl tlc) {
			resultsFound = false;
			
		}

		@Override
		public void resultWasChosen(TextLookupControl tlc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void itemHasBeenAddedToCheckout(TextLookupControl tlc) {
			itemAddedToCheckout = true;
			
		}

		@Override
		public void itemHasBeenBagged(TextLookupControl tlc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void searchHasBeenCleared(TextLookupControl tlc) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public class AttendantControlListenerStub implements AttendantControlListener{
		
		public boolean exitTextSearchScreen = false;

		@Override
		public void attendantApprovedBags(AttendantControl ac) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void attendantPreventUse(AttendantControl ac) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void attendantApprovedItemRemoval(AttendantControl bc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void lowInk(AttendantControl ac, String message) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void lowPaper(AttendantControl ac, String message) {
			// TODO Auto-generated method stub
			
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
		public void outOfInk(AttendantControl ac, String message) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void outOfPaper(AttendantControl ac, String message) {
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
		public void signalWeightDescrepancy(String updateMessage) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void noBagRequest() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void itemBagged() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void initialState() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void banknotesInStorageLowState() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void attendantPermitStationUse(AttendantControl ac) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void coinIsLowState(int amount) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void loggedIn(boolean isLoggedIn) {
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
			exitTextSearchScreen = true;
			
		}

		@Override
		public void printerNotLowState() {
			// TODO Auto-generated method stub
			
		}
	}

}
