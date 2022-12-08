package com.diy.software.test.logic;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.diy.hardware.BarcodedProduct;
import com.diy.hardware.PLUCodedProduct;
import com.diy.hardware.PriceLookUpCode;
import com.diy.hardware.external.ProductDatabases;
import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.controllers.TextLookupControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.AttendantControlListener;
import com.diy.software.listeners.TextLookupControlListener;
import com.diy.software.util.CodedProduct;
import com.jimmyselectronics.necchi.Barcode;
import com.unitedbankingservices.coin.CoinStorageUnit;

import ca.powerutility.PowerGrid;

public class TextLookupControlTest {
	TextLookupControl tlc;
	StationControl sc;
	AttendantControl ac;
	TextLookupStub tlStub;
	AttendantStub attStub;
	FakeDataInitializer fdi;
	Barcode [] barcodes;
	BarcodedProduct bp1, bp2;
	PriceLookUpCode [] plucodes;
	PLUCodedProduct plu1, plu2;
	
	@Before
	public void setup() {
		PowerGrid.engageUninterruptiblePowerSource();
		
		fdi = new FakeDataInitializer();
		fdi.addProductAndBarcodeData();
		fdi.addPLUCodedProduct();
		barcodes = fdi.getBarcodes();
		plucodes = fdi.getPLUCode();
		bp1 = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcodes[0]);	//"Can of Beans"
		bp2 = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcodes[1]);	//"Bag of Doritos"
		plu1 = ProductDatabases.PLU_PRODUCT_DATABASE.get(plucodes[0]);		//"Banana"
		plu2 = ProductDatabases.PLU_PRODUCT_DATABASE.get(plucodes[1]);		//"Romania Tomato"
		
		sc = new StationControl(fdi);
		ac = new AttendantControl(sc);
		tlc = ac.getTextLookupControl();
		tlStub = new TextLookupStub();
		attStub = new AttendantStub();
	}
	
	@Test
	public void testAddListener() {
		tlStub.queryEntered = false;
		tlc.readyToSearch();
		assertFalse(tlStub.queryEntered);
		
		tlc.addListener(tlStub);
		tlStub.queryEntered = false;
		tlc.readyToSearch();
		assertTrue(tlStub.queryEntered);
	}
	
	@Test
	public void testRemoveListener() {
		tlc.addListener(tlStub);
		
		tlStub.queryEntered = false;
		tlc.readyToSearch();
		assertTrue(tlStub.queryEntered);
		
		tlc.removeListener(tlStub);
		
		tlStub.queryEntered = false;
		tlc.readyToSearch();
		assertFalse(tlStub.queryEntered);
	}
	
	@Test
	public void testFindProductUsingValidKeyword() {
		tlc.addListener(tlStub);
		
		String keyword = "Doritos";
		tlc.findProduct(keyword);
		assertTrue(tlStub.resultsFound);
		assertEquals(bp2.getDescription(), tlc.getResult(0).getBarcodedProduct().getDescription());
		assertEquals(bp2.getPrice(), tlc.getResult(0).getBarcodedProduct().getPrice());
		assertEquals(bp2.getExpectedWeight(), tlc.getResult(0).getBarcodedProduct().getExpectedWeight(), 0);
		
		tlc.clearSearch();
		
		keyword = "banana";
		tlc.findProduct(keyword);
		assertTrue(tlStub.resultsFound);
		assertEquals(plu1.getDescription(), tlc.getResult(0).getPLUCodedProduct().getDescription());
		assertEquals(plu1.getPrice(), tlc.getResult(0).getPLUCodedProduct().getPrice());

	}
	
	@Test
	public void testFindProductUsingInvalidKeyword() {
		tlc.addListener(tlStub);
		
		String keyword = "Apple";
		tlc.findProduct(keyword);
		assertFalse(tlStub.resultsFound);
	}
	
	@Test
	public void testFindProductWithKeywordInUnexpectedCaseFormat() {
		tlc.addListener(tlStub);
		
		String keyword = "doRiTOS";
		tlc.findProduct(keyword);
		assertTrue(tlStub.resultsFound);
		assertEquals(bp2.getDescription(), tlc.getResult(0).getBarcodedProduct().getDescription());
		assertEquals(bp2.getPrice(), tlc.getResult(0).getBarcodedProduct().getPrice());
		assertEquals(bp2.getExpectedWeight(), tlc.getResult(0).getBarcodedProduct().getExpectedWeight(), 0);
		
		tlc.clearSearch();
		
		keyword = "BANANA";
		tlc.findProduct(keyword);
		assertTrue(tlStub.resultsFound);
		assertEquals(plu1.getDescription(), tlc.getResult(0).getPLUCodedProduct().getDescription());
		assertEquals(plu1.getPrice(), tlc.getResult(0).getPLUCodedProduct().getPrice());
	}
	
	@Test
	public void testFindProductWithPartialKeyword() {
		tlc.addListener(tlStub);
		
		String keyword = "Dor";
		tlc.findProduct(keyword);
		assertTrue(tlStub.resultsFound);
		assertEquals(bp2.getDescription(), tlc.getResult(0).getBarcodedProduct().getDescription());
		assertEquals(bp2.getPrice(), tlc.getResult(0).getBarcodedProduct().getPrice());
		assertEquals(bp2.getExpectedWeight(), tlc.getResult(0).getBarcodedProduct().getExpectedWeight(), 0);
		
		tlc.clearSearch();
		
		keyword = "nana";
		tlc.findProduct(keyword);
		assertTrue(tlStub.resultsFound);
		assertEquals(plu1.getDescription(), tlc.getResult(0).getPLUCodedProduct().getDescription());
		assertEquals(plu1.getPrice(), tlc.getResult(0).getPLUCodedProduct().getPrice());
	}
	
	@Test
	public void testGetResultWithInvalidSelectionIndex() {
		tlc.addListener(tlStub);
		
		CodedProduct result;		
		tlc.findProduct("tomato");
		result = tlc.getResult(-1);
		assertEquals(null, result);
		result = tlc.getResult(1);
		assertEquals(null, result);
	}
	
	@Test
	public void testAddProducts() {
		tlc.addListener(tlStub);
		ac.addListener(attStub);
		
		assertFalse(attStub.attendantBlocked);
		
		tlc.findProduct("beans");
		tlc.addProduct(0);
		assertTrue(tlStub.itemAdded);
		assertEquals(sc.getItemsControl().getCheckoutList().get(0).x, tlc.getProductDescription());
		assertEquals(sc.getItemsControl().getCheckoutList().get(0).y, tlc.getProductCost(), 0);
		assertEquals(sc.getItemsControl().getCheckoutTotal(), tlc.getProductCost(), 0);
		double updatedTotal = sc.getItemsControl().getCheckoutTotal();

		tlc.clearSearch();
		
		tlc.findProduct("tomato");
		tlc.addProduct(0);
		assertTrue(tlStub.itemAdded);
		assertEquals(sc.getItemsControl().getCheckoutList().get(1).x, tlc.getProductDescription());
		assertEquals(sc.getItemsControl().getCheckoutList().get(1).y, tlc.getProductCost(), 0);
		assertEquals((sc.getItemsControl().getCheckoutTotal() - updatedTotal), tlc.getProductCost(), 0.0001);
		
		assertTrue(attStub.attendantBlocked);
	}
	
	@Test
	public void testPlaceProductInBaggingArea() {
		tlc.addListener(tlStub);
		ac.addListener(attStub);
		
		double lastItemWeight = sc.getWeightOfLastItemAddedToBaggingArea();
		double lastExpectedWeight = sc.getExpectedWeight();
		tlc.findProduct("tomato");
		tlc.addProduct(0);
		
		assertTrue(attStub.attendantBlocked);
		
		tlc.placeProductInBaggingArea();
		
		assertTrue(tlStub.itemBagged);
		assertNotEquals(lastItemWeight, sc.getWeightOfLastItemAddedToBaggingArea());
		assertNotEquals(lastExpectedWeight, sc.getExpectedWeight());
		assertEquals(sc.getWeightOfLastItemAddedToBaggingArea(), tlc.getProductWeight(), 0);
		assertFalse(attStub.attendantBlocked);
	}
	
	@Test
	public void testClearSearch() {
		tlc.addListener(tlStub);
		tlc.findProduct("beans");
		assertTrue(tlStub.resultsFound);
		assertFalse(tlStub.searchCleared);
		
		tlc.clearSearch();
		assertFalse(tlStub.resultsFound);
		assertTrue(tlStub.searchCleared);
	}
	
	@Test
	public void testKeyboardInputComplete() {
		tlc.addListener(tlStub);
		tlc.keyboardInputCompleted(ac.getKeyboardControl(), "beans");
		assertTrue(tlStub.queryEntered);
		assertTrue(tlStub.resultsFound);
	}
	
	public class TextLookupStub implements TextLookupControlListener {
		boolean queryEntered = false;
		boolean resultsFound = false;
		boolean resultChosen = false;
		boolean itemAdded = false;
		boolean itemBagged = false;
		boolean searchCleared = false;

		@Override
		public void searchQueryWasEntered(TextLookupControl tlc) {
			queryEntered = true;
			searchCleared = false;
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
			resultChosen = true;
			
		}

		@Override
		public void itemHasBeenAddedToCheckout(TextLookupControl tlc) {
			itemAdded = true;
		}

		@Override
		public void itemHasBeenBagged(TextLookupControl tlc) {
			itemBagged = true;
			
		}
		
		@Override
		public void searchHasBeenCleared(TextLookupControl tlc) {
			searchCleared = true;
			queryEntered = false;
			resultsFound = false;
			resultChosen = false;
			itemBagged = false;
			itemAdded = false;
		}
		
	}
	
	public class AttendantStub implements AttendantControlListener {
    	boolean attendantBlocked = false;
    	
    	@Override
    	public void attendantApprovedBags(AttendantControl ac) {
    		// TODO Auto-generated method stub
    	}

		@Override
		public void attendantPreventUse(AttendantControl ac) {
			attendantBlocked = true;
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
		public void initialState() {
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
		public void outOfInk(AttendantControl ac, String message) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void outOfPaper(AttendantControl ac, String message) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void noBagRequest() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void attendantPermitStationUse(AttendantControl ac) {
			attendantBlocked = false;
			
		}

		@Override
		public void loggedIn(boolean isLoggedIn) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void attendantApprovedItemRemoval(AttendantControl bc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void itemBagged() {
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
		public void banknotesInStorageLowState() {
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
		public void stationShutDown(AttendantControl ac) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void stationStartedUp(AttendantControl ac) {
			// TODO Auto-generated method stub
			
		}
    }
	
}