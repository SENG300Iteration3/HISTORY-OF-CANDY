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
import swing.frames.AttendantActionsGUI;
import swing.frames.AttendantStationGUI;
import swing.frames.CustomerActionsGUI;
import swing.frames.CustomerStationGUI;

public class SENG300DoItYourselfStation {
	public static FakeDataInitializer fakeData;
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
      stationControls.add(new StationControl(fakeData));
    }
    PaneControl Attendantpc = new PaneControl(stationControls);
    PaneControl Customerpc = new PaneControl(stationControls);
    new AttendantStationGUI(Attendantpc);
    new CustomerStationGUI(Customerpc);
    new CustomerActionsGUI(Customerpc);
    new AttendantActionsGUI(Attendantpc);
  }

  public static void configureDoItYourselfStation() {
    DoItYourselfStation.configureBanknoteDenominations(new int[] { 100, 50, 20, 10, 5, 1 });
    DoItYourselfStation.configureCoinDenominations(new long[] { 200, 100, 25, 10, 5, 1 });
  }
  
	public static void initializeInventory() {
		fakeData = new FakeDataInitializer();
		fakeData.addCardData();
		fakeData.addProductAndBarcodeData();
		fakeData.addPLUCodedProduct();
		fakeData.addFakeMembers();
		fakeData.addFakeAttendantLogin();
	}
	
}
