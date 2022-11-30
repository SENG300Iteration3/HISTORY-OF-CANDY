package com.diy.software.app;

import java.util.ArrayList;

import com.diy.hardware.DoItYourselfStation;
import com.diy.software.controllers.PaneControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;

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
}
