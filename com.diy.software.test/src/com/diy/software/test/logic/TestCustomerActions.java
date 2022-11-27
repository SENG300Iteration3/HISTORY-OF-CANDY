package com.diy.software.test.logic;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.util.Tuple;
import com.diy.simulation.Customer;
import com.diy.software.controllers.ItemsControl;
import com.diy.software.controllers.SystemControl;
import com.diy.software.fakedata.FakeDataInitializer;

import ca.powerutility.PowerGrid;
public class TestCustomerActions {
	Customer customer;
	SystemControl controller;
	FakeDataInitializer fakeData;
	
	@Before
	public void setup() {
		
		PowerGrid.engageUninterruptiblePowerSource();
		controller = new SystemControl();
		customer = controller.customer;
		fakeData = new FakeDataInitializer();
		fakeData.addProductAndBarcodeData();
		
		customer.shoppingCart.add(fakeData.getItems()[0]);
		customer.shoppingCart.add(fakeData.getItems()[1]);
		customer.shoppingCart.add(fakeData.getItems()[2]);
		
		customer.useStation(controller.station);
	}
	
	@After
	public void teardown() {
		PowerGrid.reconnectToMains();
	}
	
	
	/*
	 * Expected behavior:
	 * 
	 * Customer scans items -> system is locked -> customer puts item in bagging area ->
	 * station unlocks -> customer presses pay -> customer presses debit or credit ->
	 * card is inserted -> pin entered wrong so error gets displayed -> pin entered correctly ->
	 * transaction complete and reciept prints
	 */
	@Test
	public void testPayWithCard() {
		customer.selectNextItem();
		
		StubSystem controlStub = new StubSystem();
		controller.listeners.add(controlStub);
		
		int i = 0;
		while((!controlStub.locked) && i < 25) {
			customer.scanItem(true);
			i++;
		}
		customer.placeItemInBaggingArea();
		ItemsControl ic = controller.getItemsControl();
		ArrayList<Tuple<String, Double>> items = ic.getCheckoutList();
		
		assertEquals("Item list should only have size 1!", 1, items.size());
		
	}
}
