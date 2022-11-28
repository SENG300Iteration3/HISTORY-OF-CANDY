package com.diy.software.fakedata;



import java.util.Calendar;
import java.util.HashMap;

import com.diy.hardware.BarcodedProduct;
import com.diy.hardware.PLUCodedProduct;
import com.diy.hardware.PriceLookUpCode;
import com.diy.hardware.external.CardIssuer;
import com.diy.hardware.external.ProductDatabases;
import com.diy.software.controllers.AttendantLogin;
import com.jimmyselectronics.necchi.Barcode;
import com.jimmyselectronics.necchi.BarcodedItem;
import com.jimmyselectronics.necchi.Numeral;
import com.jimmyselectronics.opeechee.Card;
import com.unitedbankingservices.coin.CoinStorageUnit;

public class FakeDataInitializer {
	
	private Barcode barcode1, barcode2, barcode3, barcode4;
	private BarcodedItem item1, item2, item3, item4;
	private BarcodedProduct bp1, bp2, bp3, bp4;
	
	private PriceLookUpCode code1, code2, reusableBagCode;
	private PLUCodedProduct plu1, plu2, reusableBagProduct;
	
	private Card card1, card2, card3, card4, card5;
	private CardIssuer fakebank;
	private CoinStorageUnit fakeCoinStorageUnit;
	private final Double AMOUNT_AVAILABLE = 1000.0;
	private final int COIN_STORAGE_CAPACITY = 50;
	Calendar expire_date = Calendar.getInstance();
	
	public void addProductAndBarcodeData () {
		barcode1 = new Barcode(new Numeral[] { Numeral.one, Numeral.two, Numeral.three, Numeral.four }); 
		item1 = new BarcodedItem(barcode1, 450); 
		barcode2 = new Barcode(new Numeral[] { Numeral.zero, Numeral.four, Numeral.two, Numeral.zero }); 
		item2 = new BarcodedItem(barcode2, 420); 
		barcode3 = new Barcode(new Numeral[] { Numeral.four, Numeral.three, Numeral.two, Numeral.one }); 
		item3 = new BarcodedItem(barcode3, 350); 
		barcode4 = new Barcode(new Numeral[] { Numeral.one, Numeral.two, Numeral.one, Numeral.two }); 
		item4 = new BarcodedItem(barcode4, 550); 	
		
		bp1 = new BarcodedProduct(barcode1, "Can of Beans", 2, 450);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode1, bp1);
		bp2 = new BarcodedProduct(barcode2, "Bag of Doritos", 5, 420);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode2, bp2);
		bp3 = new BarcodedProduct(barcode3, "Rib Eye Steak", 17, 350);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode3, bp3);
		bp4 = new BarcodedProduct(barcode4, "Cauliflower", 6, 550);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode4, bp4);
		
		ProductDatabases.INVENTORY.put(bp1, 100);
		ProductDatabases.INVENTORY.put(bp2, 100);
		ProductDatabases.INVENTORY.put(bp3, 100);
		ProductDatabases.INVENTORY.put(bp4, 100);

	}
	
	public void addPLUCodedProduct() {
		code1 = new PriceLookUpCode("1234");
		code2 = new PriceLookUpCode("1235");
		reusableBagCode = new PriceLookUpCode("1236");
		
		plu1 = new PLUCodedProduct(code1, "banana", 1);
		plu2 = new PLUCodedProduct(code2, "Romania tomamto", 2);
		reusableBagProduct = new PLUCodedProduct(reusableBagCode, "reusable bag", 2);
		
		ProductDatabases.PLU_PRODUCT_DATABASE.put(code1,plu1);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(code2,plu2);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(reusableBagCode, reusableBagProduct);
		
		//FIXME: Add to inventory
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
	
	public void addFakeAttendantLogin() {
		AttendantLogin.loginMap.put("A1", "password");
		AttendantLogin.loginMap.put("A2", "wordpass");
		AttendantLogin.loginMap.put("A3", "Password1");
		AttendantLogin.loginMap.put("A4", "12345");
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
		
		card5 = new Card(GiftcardDatabase.CompanyGiftCard, "00001234", "Jimmy James", null, null, false, false);
		GiftcardDatabase.giftcardMap.put("00001234", 50.00);
	}
	
	public void setCoinStorageCapacity() {
		fakeCoinStorageUnit = new CoinStorageUnit(COIN_STORAGE_CAPACITY);
	}
	
	public Barcode[] getBarcodes() {
		return new Barcode[] {barcode1, barcode2, barcode3, barcode4};
	}
	
	public BarcodedItem[] getItems() {
		return new BarcodedItem[] {item1, item2, item3, item4};
	}
	
	public PriceLookUpCode[] getPLUCode() {
		return new PriceLookUpCode[] {code1, code2, reusableBagCode};
	}
	
	public double getReusableBagPrice() {
		return ProductDatabases.PLU_PRODUCT_DATABASE.get(reusableBagCode).getPrice();
	}
	
	public Card[] getCards() {
		return new Card[] {card1, card2, card3, card4, card5};
	}
	
	public CardIssuer getCardIssuer() {
		return fakebank;
	}

	public HashMap<Integer, String> getMembershipMap() {
		return MembershipDatabase.membershipMap;
	}
	
	public CoinStorageUnit getFakeUnit() {
		return fakeCoinStorageUnit;
	}
}
