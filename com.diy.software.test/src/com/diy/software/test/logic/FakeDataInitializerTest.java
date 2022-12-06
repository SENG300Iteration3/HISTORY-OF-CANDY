package com.diy.software.test.logic;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.diy.hardware.external.ProductDatabases;
import com.diy.software.controllers.AttendantControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.fakedata.MembershipDatabase;
import com.jimmyselectronics.necchi.Barcode;
import com.jimmyselectronics.necchi.BarcodedItem;
import com.jimmyselectronics.necchi.Numeral;
import com.jimmyselectronics.opeechee.Card;

public class FakeDataInitializerTest {
	
	private Barcode b1;
	private Barcode b2;
	private Barcode b3;
	private Barcode b4;
	private Card c1;
	private Card c2;
	private Card c3;
	
	@Before
	public void setUp() throws Exception {
		ProductDatabases.BARCODED_PRODUCT_DATABASE.clear();
		b1 = new Barcode(new Numeral[] { Numeral.one, Numeral.two, Numeral.three, Numeral.four });
		b2 = new Barcode(new Numeral[] { Numeral.zero, Numeral.four, Numeral.two, Numeral.zero });
		b3 = new Barcode(new Numeral[] { Numeral.four, Numeral.three, Numeral.two, Numeral.one }); 
		b4 = new Barcode(new Numeral[] { Numeral.one, Numeral.two, Numeral.one, Numeral.two }); 
		
		c1 = new Card("AMEX", "0000000000001234", "Stephen Strange", "000", "1234", true, true);
		c2 = new Card("VISA", "0000000000004321", "Tony Stark", "111", "0987", true, true);
		c3 = new Card("MAST", "0000000000009999", "Natasha Romanoff", "222", "1111", true, false);
	}

	@Test
	public void testAddProductAndBarcodeData() {
		FakeDataInitializer fdi = new FakeDataInitializer();
		assertTrue(ProductDatabases.BARCODED_PRODUCT_DATABASE.isEmpty());
		for (Barcode barcode : fdi.getBarcodes()) {
			assertNull(barcode);
		}
		fdi.addProductAndBarcodeData();
		assertFalse(ProductDatabases.BARCODED_PRODUCT_DATABASE.isEmpty());
		assertTrue(ProductDatabases.BARCODED_PRODUCT_DATABASE.get(b1).getDescription().equals("Can of Beans"));
		assertTrue(ProductDatabases.BARCODED_PRODUCT_DATABASE.get(b2).getPrice() == 5 );
		assertTrue(ProductDatabases.BARCODED_PRODUCT_DATABASE.get(b3).getExpectedWeight() == 350 );
		assertTrue(ProductDatabases.BARCODED_PRODUCT_DATABASE.get(b4).getDescription().equals("Cauliflower"));
		for (Barcode barcode : fdi.getBarcodes()) {
			assertNotNull(barcode);
		}
		
		ProductDatabases.BARCODED_PRODUCT_DATABASE.clear();
	}

	@Test
	public void testAddCardData() {
		FakeDataInitializer fdi = new FakeDataInitializer();
		for (Card card : fdi.getCards()) {
			assertNull(card);
		}
		fdi.addCardData();
		for (Card card : fdi.getCards()) {
			assertNotNull(card);
		}
	}
	
	@Test
	public void testAddFakeMembers() {
		MembershipDatabase.membershipMap.clear();
		assertTrue(MembershipDatabase.membershipMap.isEmpty());
		FakeDataInitializer fdi = new FakeDataInitializer();
		fdi.addFakeMembers();
		assertTrue(MembershipDatabase.membershipMap.get(1234).equals("Itadori"));
		assertTrue(MembershipDatabase.membershipMap.get(1235).equals("Tanjiro"));
		assertTrue(MembershipDatabase.membershipMap.get(1236).equals("Nezuko"));
		assertTrue(MembershipDatabase.membershipMap.get(1237).equals("Zenitsu"));
	}
	
	@Test
	public void testAddFakeAttendantLogin() {
		AttendantControl.logins.clear();
		assertTrue(AttendantControl.logins.isEmpty());
		FakeDataInitializer fdi = new FakeDataInitializer();
		fdi.addFakeAttendantLogin();
		assertTrue(AttendantControl.logins.contains("password"));
		assertTrue(AttendantControl.logins.contains("wordpass"));
		assertTrue(AttendantControl.logins.contains("Password1"));
		assertTrue(AttendantControl.logins.contains("12345"));
	}

	@Test
	public void testGetBarcodes() {
		FakeDataInitializer fdi = new FakeDataInitializer();
		fdi.addProductAndBarcodeData();
		Barcode[] barcodes = fdi.getBarcodes();
		
		assertTrue(barcodes[0].equals(b1));
		assertTrue(barcodes[1].equals(b2));
		assertTrue(barcodes[2].equals(b3));
		assertTrue(barcodes[3].equals(b4));
		
		ProductDatabases.BARCODED_PRODUCT_DATABASE.clear();
	}

	@Test
	public void testGetItems() {
		FakeDataInitializer fdi = new FakeDataInitializer();
		for (BarcodedItem item : fdi.getItems()) {
			assertNull(item);
		}
		fdi.addProductAndBarcodeData();
		for (BarcodedItem item : fdi.getItems()) {
			assertNotNull(item);
		}
		
		BarcodedItem[] items = fdi.getItems();
		
		assertTrue(items[0].getBarcode().equals(b1));
		assertTrue(items[1].getBarcode().equals(b2));
		assertTrue(items[2].getBarcode().equals(b3));
		assertTrue(items[3].getBarcode().equals(b4));
		assertTrue(items[0].getWeight() == 450);
		assertTrue(items[3].getWeight() == 550);
		
		ProductDatabases.BARCODED_PRODUCT_DATABASE.clear();
	}

	@Test
	public void testGetCardIssuer() {
		FakeDataInitializer fdi = new FakeDataInitializer();
		assertNull(fdi.getCardIssuer());
		fdi.addCardData();
		assertNotNull(fdi.getCardIssuer());
		assertTrue(fdi.getCardIssuer().authorizeHold(c1.number, 25) != -1);
	}

	@Test
	public void testGetCards() {
		FakeDataInitializer fdi = new FakeDataInitializer();
		for (Card card : fdi.getCards()) {
			assertNull(card);
		}
		fdi.addCardData();
		for (Card card : fdi.getCards()) {
			assertNotNull(card);
		}
		
		Card[] cards = fdi.getCards();
		assertTrue(cards[0].number.equals(c1.number));
		assertTrue(cards[1].cardholder.equals(c2.cardholder));
		assertTrue(cards[2].cvv.equals(c3.cvv));
		assertTrue(cards[0].kind.equals(c1.kind));
	}

}
