package com.diy.software.app;

import java.util.ArrayList;

import com.diy.hardware.BarcodedProduct;
import com.diy.hardware.DoItYourselfStation;
import com.diy.hardware.PLUCodedItem;
import com.diy.hardware.PLUCodedProduct;
import com.diy.hardware.PriceLookUpCode;
import com.diy.hardware.external.ProductDatabases;
import com.diy.software.controllers.PaneControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.jimmyselectronics.necchi.Barcode;
import com.jimmyselectronics.necchi.Numeral;

import ca.powerutility.PowerGrid;
import swing.frames.AttendantStationGUI;
import swing.frames.CustomerActionsGUI;
import swing.frames.CustomerStationGUI;

public class SENG300DoItYourselfStation {
  public static void main(String[] args) {
	PowerGrid.engageUninterruptiblePowerSource();
    int totalNumberOfStations;
    try {
      totalNumberOfStations = Integer.parseInt(args[0]);
    } catch (Exception e) {
      totalNumberOfStations = 3;
    }
    
    configureDoItYourselfStation();
	initializeInventory();
    ArrayList<StationControl> stationControls = new ArrayList<>();
    for (int i = 0; i < totalNumberOfStations; i++) {
      stationControls.add(new StationControl(new FakeDataInitializer()));
    }
    PaneControl pc = new PaneControl(stationControls);
    new AttendantStationGUI(pc);
    new CustomerStationGUI(pc);
    new CustomerActionsGUI(pc);
  }

  public static void configureDoItYourselfStation() {
    DoItYourselfStation.configureBanknoteDenominations(new int[] { 100, 50, 20, 10, 5, 1 });
    DoItYourselfStation.configureCoinDenominations(new long[] { 200, 100, 25, 10, 5, 1 });
  }
  
	public static void initializeInventory() {
		Barcode barcode1 = new Barcode(new Numeral[] { Numeral.one, Numeral.two, Numeral.three, Numeral.four });
		Barcode barcode2 = new Barcode(new Numeral[] { Numeral.zero, Numeral.four, Numeral.two, Numeral.zero });
		Barcode barcode3 = new Barcode(new Numeral[] { Numeral.four, Numeral.three, Numeral.two, Numeral.one }); 
		Barcode barcode4 = new Barcode(new Numeral[] { Numeral.one, Numeral.two, Numeral.one, Numeral.two }); 
		
		PriceLookUpCode plu1 = new PriceLookUpCode("2718");
		PriceLookUpCode plu2 = new PriceLookUpCode("31415");
		PriceLookUpCode plu3 = new PriceLookUpCode("9806");
		PriceLookUpCode plu4 = new PriceLookUpCode("6022");

		BarcodedProduct bp1 = new BarcodedProduct(barcode1, "Can of Beans", 2, 450);
		ProductDatabases.INVENTORY.put(bp1, 10);
		BarcodedProduct bp2 = new BarcodedProduct(barcode2, "Bag of Doritos", 5, 420);
		ProductDatabases.INVENTORY.put(bp2, 10);
		BarcodedProduct bp3 = new BarcodedProduct(barcode3, "Rib Eye Steak", 17,350);
		ProductDatabases.INVENTORY.put(bp3, 10);
		BarcodedProduct bp4 = new BarcodedProduct(barcode4, "Cauliflower", 6,550);
		ProductDatabases.INVENTORY.put(bp4, 10);
		
		PLUCodedProduct pcp1 = new PLUCodedProduct(plu1, "Gomu Gomu Devil Fruit", 260);
		ProductDatabases.INVENTORY.put(pcp1, 10);
		PLUCodedProduct pcp2 = new PLUCodedProduct(plu2, "Hana Hana Devil Fruit", 250);
		ProductDatabases.INVENTORY.put(pcp2, 10);
		PLUCodedProduct pcp3 = new PLUCodedProduct(plu3, "Mera Mera Devil Fruit", 290);
		ProductDatabases.INVENTORY.put(pcp3, 10);
		PLUCodedProduct pcp4 = new PLUCodedProduct(plu4, "Hito Hito Devil Fruit", 350);
		ProductDatabases.INVENTORY.put(pcp4, 10);
		
		
		
		PriceLookUpCode code1 = new PriceLookUpCode("1234");
		PLUCodedProduct pp1 = new PLUCodedProduct(code1, "Green Apples", 8);
		
		PriceLookUpCode code2 = new PriceLookUpCode("9876");
		PLUCodedProduct pp2 = new PLUCodedProduct(code2, "Broccoli", 5);

		PriceLookUpCode code3 = new PriceLookUpCode("11111");
		PLUCodedProduct pp3 = new PLUCodedProduct(code3, "Tomatoes", 4);

		PriceLookUpCode code4 = new PriceLookUpCode("23456");
		PLUCodedProduct pp4 = new PLUCodedProduct(code4, "Oranges", 7);
		
		ProductDatabases.INVENTORY.put(pp1, 100);
		ProductDatabases.INVENTORY.put(pp2, 100);
		ProductDatabases.INVENTORY.put(pp3, 100);
		ProductDatabases.INVENTORY.put(pp4, 100);
	}
	
}
