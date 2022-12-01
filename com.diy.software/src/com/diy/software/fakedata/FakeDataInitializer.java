package com.diy.software.fakedata;



import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.diy.hardware.BarcodedProduct;
import com.diy.hardware.external.CardIssuer;
import com.diy.hardware.external.ProductDatabases;
import com.jimmyselectronics.necchi.Barcode;
import com.jimmyselectronics.necchi.BarcodedItem;
import com.jimmyselectronics.necchi.Numeral;
import com.jimmyselectronics.opeechee.Card;

public class FakeDataInitializer {
	
	/**
	 * The known Barcoded Items. Indexed by barcode.
	 */
	public static final Map<String, BarcodedItem> BARCODED_ITEM_DATABASE = new HashMap<>();
	private Barcode barcode1, barcode2, barcode3, barcode4;
	private BarcodedItem item1, item2, item3, item4;
	private BarcodedProduct bp1, bp2, bp3, bp4;
	private Card card1, card2, card3, card4;
	private CardIssuer fakebank;
	private final Double AMOUNT_AVAILABLE = 1000.0;
	Calendar expire_date = Calendar.getInstance();
	
	public void addProductAndBarcodeData () {
		
		barcode1 = new Barcode(new Numeral[] { Numeral.one, Numeral.two, Numeral.three, Numeral.four }); 
		item1 = new BarcodedItem(barcode1, 450); 
		bp1 = new BarcodedProduct(barcode1, "Can of Beans", 2, 450);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode1, bp1);
		FakeDataInitializer.BARCODED_ITEM_DATABASE.put("Can of Beans", item1);
		
		barcode2 = new Barcode(new Numeral[] { Numeral.zero, Numeral.four, Numeral.two, Numeral.zero }); 
		item2 = new BarcodedItem(barcode2, 420); 
		bp2 = new BarcodedProduct(barcode2, "Bag of Doritos", 5, 420);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode2, bp2);
		FakeDataInitializer.BARCODED_ITEM_DATABASE.put("Bag of Doritos", item2);
		
		barcode3 = new Barcode(new Numeral[] { Numeral.four, Numeral.three, Numeral.two, Numeral.one }); 
		item3 = new BarcodedItem(barcode3, 350); 
		bp3 = new BarcodedProduct(barcode3, "Rib Eye Steak", 17, 350);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode3, bp3);
		FakeDataInitializer.BARCODED_ITEM_DATABASE.put("Rib Eye Steak", item3);
		
		barcode4 = new Barcode(new Numeral[] { Numeral.one, Numeral.two, Numeral.one, Numeral.two }); 
		item4 = new BarcodedItem(barcode4, 550); 	
		bp4 = new BarcodedProduct(barcode4, "Cauliflower", 6, 550);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode4, bp4);
		FakeDataInitializer.BARCODED_ITEM_DATABASE.put("Cauliflower", item4);
		
		
		ProductDatabases.INVENTORY.put(bp1, 100);
		ProductDatabases.INVENTORY.put(bp2, 100);
		ProductDatabases.INVENTORY.put(bp3, 100);
		ProductDatabases.INVENTORY.put(bp4, 100);

	}
	
	/**
	 * add fake membership data
	 * @return void
	 */
	public void addFakeMembers() {
		MembershipDatabase.membershipMap.put(1234, "Itadori");
		MembershipDatabase.membershipMap.put(1235, "Tanjiro");
		MembershipDatabase.membershipMap.put(1236, "Nezuko");
		MembershipDatabase.membershipMap.put(1237, "Zenitsu");
	}
	
	public void addCardData() {
		
		fakebank = new CardIssuer("RBC", 14);
		expire_date.add(Calendar.YEAR, 5);
		card1 = new Card("AMEX", "0000000000001234", "Stephen Strange", "000", "1234", false, true);
		card2 = new Card("VISA", "0000000000004321", "Tony Stark", "111", "0987", true, true);
		card3 = new Card("MAST", "0000000000009999", "Natasha Romanoff", "222", "1111", true, false);
		card4 = new Card("MEMBERSHIP", "1234", "Itadori", "000", "0000", false, true);
		
		fakebank.addCardData("0000000000001234", "Stephen Strange", expire_date, "000", AMOUNT_AVAILABLE);
		fakebank.addCardData("0000000000004321", "Tony Stark", expire_date, "111", AMOUNT_AVAILABLE);
		fakebank.addCardData("0000000000009999", "Natasha Romanoff", expire_date, "222", AMOUNT_AVAILABLE);
		fakebank.addCardData("1234", "Itadori", expire_date, "000", AMOUNT_AVAILABLE);
	}
	
	public Barcode[] getBarcodes() {
		return new Barcode[] {barcode1, barcode2, barcode3, barcode4};
	}
	
	public BarcodedItem[] getItems() {
		return new BarcodedItem[] {item1, item2, item3, item4};
	}
	
	public Card[] getCards() {
		return new Card[] {card1, card2, card3, card4};
	}
	
	public CardIssuer getCardIssuer() {
		return fakebank;
	}

	public HashMap<Integer, String> getMembershipMap() {
		return MembershipDatabase.membershipMap;
	}
}
