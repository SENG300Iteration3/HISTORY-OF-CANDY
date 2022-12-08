package com.diy.software.test.logic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.event.ActionEvent;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.diy.hardware.PLUCodedProduct;
import com.diy.hardware.Product;
import com.diy.hardware.external.ProductDatabases;
import com.diy.software.controllers.PLUCodeControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.PLUCodeControlListener;

import ca.powerutility.PowerGrid;

public class TestPLU {
  FakeDataInitializer fakeData;
  StationControl sc;
  PLUCodeControl pc;
  PLUCodeListenerStub pcls;

  @Before
  public void setup() {
    PowerGrid.engageUninterruptiblePowerSource();

    fakeData = new FakeDataInitializer();
    sc = new StationControl();
    pc = new PLUCodeControl(sc);
    pcls = new PLUCodeListenerStub();

    fakeData.addProductAndBarcodeData();
    fakeData.addPLUCodedProduct();
  }

  @Test
  public void testPLUDatabasePopulationWithInventory() {
	boolean check = false;
    int counter = 0;
    Set<Product> products = ProductDatabases.INVENTORY.keySet();
    
    for(PLUCodedProduct product : ProductDatabases.PLU_PRODUCT_DATABASE.values()) {
      for(Product item : products) {
    	  // check if PLU item is in the inventory
        if(item.hashCode() == product.hashCode()) {
          check = true;
        }
      }
      counter++;
    }
    assertTrue(check);
  }
  
	  @Test
	  public void testAddListener() {
		assertFalse(pcls.updated);
		
		pc.addListener(pcls);
		
		assertFalse(pcls.updated);
	  }
  
	  @Test
	  public void testRemoveListener() {
		assertFalse(pcls.updated);
		
		pc.removeListener(pcls);
		
		assertFalse(pcls.updated);
	  }
	  
		@Test 
		public void testActionPerformedPLUInput() {
			pc.addListener(pcls);
			ActionEvent e = new ActionEvent(this, 0, "PLU_INPUT_BUTTON: 1234");
			assertFalse(pcls.updated);
			assertFalse(pcls.stubplu.equals("1234"));
			
			pc.actionPerformed(e);
			
			assertTrue(pcls.updated);
			assertTrue(pcls.stubplu.equals("1234"));
		}
		
		  @Test
		  public void testActionPerformedBadPLUInput() {
			  pc.addListener(pcls);
			  ActionEvent e = new ActionEvent(this, 0, "1234");
			  assertFalse(pcls.updated);
			  
			  pc.actionPerformed(e);
			  
			  assertFalse(pcls.updated);
			assertFalse(pcls.stubplu.equals("1234"));
		  }
	  
	  @Test
	  public void testActionPerformedCancelButton() {
		  pc.addListener(pcls);
		  ActionEvent e = new ActionEvent(this, 0, "cancel");
		  assertFalse(pcls.updated);
		  
		  pc.actionPerformed(e);
		  
		  assertTrue(pcls.stubplu.equals(""));
	  }
	  
	  @Test
	  public void testActionPerformedPLUCorrect() {
		  pc.addListener(pcls);
		  ActionEvent e = new ActionEvent(this, 0, "correct");
		  assertFalse(pcls.updated);
		  
		  pc.actionPerformed(e);
		  
		  assertTrue(pcls.stubplu.equals(""));
	  }
	  
	  @Test
	  public void testActionPerformedPLUSubmitInvalid() {
		  pc.addListener(pcls);
		  ActionEvent e = new ActionEvent(this, 0, "submit");
		  assertFalse(pcls.updated);
		  
		  pc.actionPerformed(e);
		  
		  assertTrue(pcls.stubplu.equals(""));
	  }
	  
	  @Test
	  public void testActionPerformedPLUSubmit() {
		  pc.addListener(pcls);
		  ActionEvent e = new ActionEvent(this, 0, "submit");
		  assertFalse(pcls.updated);
		  
		  pc.actionPerformed(e);
		  
		  assertTrue(pcls.stubplu.equals(""));
	  }
	  
	public class PLUCodeListenerStub implements PLUCodeControlListener {
		String stubplu = "";
		boolean updated = false;
		String pluerr = "";

		@Override
		public void pluHasBeenUpdated(PLUCodeControl pcc, String pluCode) {
			// TODO Auto-generated method stub
			updated = true;
			stubplu = pluCode;
		}

		@Override
		public void pluCodeEntered(PLUCodeControl pcc, String pluCode) {
			// TODO Auto-generated method stub
			updated = true;
			stubplu = pluCode;
		}

		@Override
		public void pluErrorMessageUpdated(PLUCodeControl pcc, String errorMessage) {
			// TODO Auto-generated method stub
			updated = true;
			pluerr = errorMessage;
		}
	}
}