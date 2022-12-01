package com.diy.software.test.logic;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.diy.hardware.PLUCodedProduct;
import com.diy.hardware.external.ProductDatabases;
import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.jimmyselectronics.necchi.Barcode;
import com.jimmyselectronics.necchi.Numeral;

import ca.powerutility.PowerGrid;

public class TestPLU {
  FakeDataInitializer fakeData;
  StationControl controller;

  @Before
  public void setup() {
    PowerGrid.engageUninterruptiblePowerSource();

    fakeData = new FakeDataInitializer();
    controller = new StationControl();

    fakeData.addProductAndBarcodeData();
    fakeData.initializePLUProducts();
  }

  @Test
  public void testPLUDatabasePopulationWithInventory() {
    ArrayList<Boolean> check = new ArrayList<>();
    int counter = 0;
    for(PLUCodedProduct product : ProductDatabases.PLU_PRODUCT_DATABASE.values()) {
      check.add(false);
      Set<Barcode> barcodes = ProductDatabases.BARCODED_PRODUCT_DATABASE.keySet();
      for(Barcode barcode : barcodes) {
        if(barcode.hashCode() == product.getPLUCode().hashCode()) {
          check.remove(counter);
          check.add(counter, true);
          System.out.println("True");
        }
        System.out.println("False");
      }
      counter++;
    }
    ArrayList<Boolean> temp = new ArrayList<>();
    for(Barcode barcode : ProductDatabases.BARCODED_PRODUCT_DATABASE.keySet()) {
      temp.add(true);
    }
    assertTrue(check.equals(temp));
  }
}