package com.diy.software.fakedata;



import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.diy.hardware.BarcodedProduct;
import com.diy.hardware.PLUCodedItem;
import com.diy.hardware.PLUCodedProduct;
import com.diy.hardware.PriceLookUpCode;
import com.diy.hardware.external.CardIssuer;
import com.diy.hardware.external.ProductDatabases;
import com.jimmyselectronics.Item;
import com.diy.software.controllers.AttendantControl;
import com.jimmyselectronics.necchi.Barcode;
import com.jimmyselectronics.necchi.BarcodedItem;
import com.jimmyselectronics.necchi.Numeral;
import com.jimmyselectronics.opeechee.Card;
import com.unitedbankingservices.coin.CoinStorageUnit;

public class FakeDataInitializer {
	
	/**
	 * The known Barcoded Items mapped to Barcodes
	 */
	public Map<Barcode, BarcodedItem> BARCODED_ITEM_DATABASE = new HashMap<>();
	/**
	 * The known PLUCoded Item mapped to PriceLookUpCodes
	 */
	public Map<PriceLookUpCode, PLUCodedItem> PLUCODED_ITEM_DATABASE = new HashMap<>();
	
	private Barcode barcode1, barcode2, barcode3, barcode4;
	private BarcodedItem item1, item2, item3, item4;
	private BarcodedProduct bp1, bp2, bp3, bp4;
	
	private PriceLookUpCode code1, code2, code3, code4, code5, code6, code7, code8;
	private PLUCodedProduct pp1, pp2, pp3, pp4, pp5, pp6, pp7, pp8;
	private PLUCodedItem pitem1, pitem2, pitem3, pitem4, pitem5, pitem6, pitem7, pitem8;
	
	private long reusableBagPrice = 2;
	
	private Card card1, card2, card3, card4, card5;

	private CardIssuer fakebank;
	private CoinStorageUnit fakeCoinStorageUnit;
	private final Double AMOUNT_AVAILABLE = 1000.0;
	private final int COIN_STORAGE_CAPACITY = 50;
	private Calendar calendar = Calendar.getInstance();
	private Date today = new Date();
	private Date expire_date = new Date(today.getTime() + (1000*60*60*24));
	
	
	public void addProductAndBarcodeData() {

		// Only item 1 and item 2 are currently in the "customers shopping cart"
		
		barcode1 = new Barcode(new Numeral[] { Numeral.one, Numeral.two, Numeral.three, Numeral.four }); 
		barcode2 = new Barcode(new Numeral[] { Numeral.zero, Numeral.four, Numeral.two, Numeral.zero });
		barcode3 = new Barcode(new Numeral[] { Numeral.four, Numeral.three, Numeral.two, Numeral.one }); 
		barcode4 = new Barcode(new Numeral[] { Numeral.one, Numeral.two, Numeral.one, Numeral.two }); 
		
		item1 = new BarcodedItem(barcode1, 450); 
		item2 = new BarcodedItem(barcode2, 420); 
		item3 = new BarcodedItem(barcode3, 350); 
		item4 = new BarcodedItem(barcode4, 550); 	
		
		bp1 = new BarcodedProduct(barcode1, "Can of Beans", 2, 450);
		bp2 = new BarcodedProduct(barcode2, "Bag of Doritos", 5, 420);
		bp3 = new BarcodedProduct(barcode3, "Rib Eye Steak", 17, 350);
		bp4 = new BarcodedProduct(barcode4, "Cauliflower", 6, 550);
		
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode1, bp1);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode2, bp2);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode3, bp3);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode4, bp4);
		
		BARCODED_ITEM_DATABASE.put(barcode1, item1);
		BARCODED_ITEM_DATABASE.put(barcode2, item2);
		BARCODED_ITEM_DATABASE.put(barcode3, item3);
		BARCODED_ITEM_DATABASE.put(barcode4, item4);
		
		ProductDatabases.INVENTORY.put(bp1, 10);
		ProductDatabases.INVENTORY.put(bp2, 10);
		ProductDatabases.INVENTORY.put(bp3, 10);
		ProductDatabases.INVENTORY.put(bp4, 10);


	}
	
	public void addPLUCodedProduct() {

		// Only pitem3 and pitem4 are currently in the "customers shopping cart"
		
		code1 = new PriceLookUpCode("1234");
		code2 = new PriceLookUpCode("9876");
		code3 = new PriceLookUpCode("11111");
		code4 = new PriceLookUpCode("23456");
		code5 = new PriceLookUpCode("2718");
		code6 = new PriceLookUpCode("31415");
		code7 = new PriceLookUpCode("9806");
		code8 = new PriceLookUpCode("6022");
		
		pp1 = new PLUCodedProduct(code1, "Green Apples", 8);
		pp2 = new PLUCodedProduct(code2, "Broccoli", 5);
		pp3 = new PLUCodedProduct(code3, "Tomatoes", 4);
		pp4 = new PLUCodedProduct(code4, "Oranges", 7);
		pp5 = new PLUCodedProduct(code5, "Gomu Gomu Devil Fruit", 260);
		pp6 = new PLUCodedProduct(code6, "Hana Hana Devil Fruit", 250);
		pp7 = new PLUCodedProduct(code7, "Mera Mera Devil Fruit", 290);
		pp8 = new PLUCodedProduct(code8, "Hito Hito Devil Fruit", 350);
		
		pitem1 = new PLUCodedItem(code1, 200);
		pitem2 = new PLUCodedItem(code2, 300);
		pitem3 = new PLUCodedItem(code3, 717);
		pitem4 = new PLUCodedItem(code4, 622);
		pitem5 = new PLUCodedItem(code5, 100);
		pitem6 = new PLUCodedItem(code6, 150);
		pitem7 = new PLUCodedItem(code7, 175);	
		pitem8 = new PLUCodedItem(code8, 50);
		
		ProductDatabases.PLU_PRODUCT_DATABASE.put(code1, pp1);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(code2, pp2);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(code3, pp3);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(code4, pp4);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(code5, pp5);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(code6, pp6);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(code7, pp7);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(code8, pp8);
		
		ProductDatabases.INVENTORY.put(pp1, 10);
		ProductDatabases.INVENTORY.put(pp2, 10);
		ProductDatabases.INVENTORY.put(pp3, 10);
		ProductDatabases.INVENTORY.put(pp4, 10);
		ProductDatabases.INVENTORY.put(pp5, 10);
		ProductDatabases.INVENTORY.put(pp6, 10);
		ProductDatabases.INVENTORY.put(pp7, 10);
		ProductDatabases.INVENTORY.put(pp8, 10);
		
		PLUCODED_ITEM_DATABASE.put(code1, pitem1);
		PLUCODED_ITEM_DATABASE.put(code2, pitem2);
		PLUCODED_ITEM_DATABASE.put(code3, pitem3);
		PLUCODED_ITEM_DATABASE.put(code4, pitem4);
		PLUCODED_ITEM_DATABASE.put(code5, pitem5);
		PLUCODED_ITEM_DATABASE.put(code6, pitem6);
		PLUCODED_ITEM_DATABASE.put(code7, pitem7);
		PLUCODED_ITEM_DATABASE.put(code8, pitem8);
		
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
		AttendantControl.logins.add("password");
		AttendantControl.logins.add("wordpass");
		AttendantControl.logins.add("Password1");
		AttendantControl.logins.add("12345");
	}
	
	public void addCardData() {
		
		fakebank = new CardIssuer("RBC", 14);
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

	/**
	 * Get certain items for shopping cart
	 * This method does not return all items in the database
	 * @return a list of items that will be in customer shopping cart
	 */
	public Item[] getItems() {
		return new Item[] {item1, item2, pitem3, pitem4};
	}
	
	public PriceLookUpCode[] getPLUCode() {
		return new PriceLookUpCode[] {code1, code2, code3, code4};
	}

	/**
	 * Get all BarcodedProducts
	 * 
	 * @return
	 * 		An array of all BarcodedProducts
	 */
	public BarcodedProduct[] getBarcodedProducts() {
		return new BarcodedProduct[] {bp1, bp2, bp3, bp4};
	}
	
	/**
	 * Get all BarcodedItems
	 * 
	 * @return
	 * 		An array of all BarcodedItems
	 */
	public BarcodedItem[] getBarcodedItems() {
		return new BarcodedItem[] {item1, item2, item3, item4};
	}
	
	/**
	 *  Get all PriceLookupCodes
	 * @return
	 * 		An array of all PriceLookupCodes
	 */
	public PriceLookUpCode[] getPLUCodes() {
		return new PriceLookUpCode[] {code1, code2, code3, code4, code5, code6, code7, code8};
	}
	
	/**
	 *  Get all PLUCodedProducts
	 * @return
	 * 		An array of all PLUCodedProducts
	 */
	public PLUCodedProduct[] getPLUProducts() {
		return new PLUCodedProduct[] {pp1, pp2, pp3, pp4, pp5, pp6, pp7, pp8};
	}
			
	
	/**
	 * Get all PLUCodedItems including PLUCodedItems in customer shopping cart
	 * @return
	 */
	public PLUCodedItem[] getPLUItem() {
		return new PLUCodedItem[] {pitem1, pitem2, pitem3, pitem4, pitem5, pitem6, pitem7, pitem8};
	}
	
	public double getReusableBagPrice() {
		return reusableBagPrice;
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
