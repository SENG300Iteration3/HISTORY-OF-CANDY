package com.diy.software.test.gui;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.PaneControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;

import ca.powerutility.PowerGrid;
import swing.frames.CustomerActionsGUI;
import swing.frames.CustomerStationGUI;

public class CustomerStationGUITest {

	PaneControl pc;
	ArrayList<StationControl> controls;
	int numTabs;
	CustomerStationGUI gui;
	FakeDataInitializer fdi;

	@Before
	public void setUp() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();
		fdi = new FakeDataInitializer();
		fdi.addCardData();
		fdi.addFakeAttendantLogin();
		fdi.addFakeMembers();
		fdi.addPLUCodedProduct();
		fdi.addProductAndBarcodeData();
		
		controls = new ArrayList<StationControl>();
		numTabs = 3;
		for (int i = 0; i < numTabs; i++) {
			controls.add(new StationControl(fdi));
		}
		pc = new PaneControl(controls);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCustomerStationGUI() {
		gui = new CustomerStationGUI(pc);
		assertEquals(numTabs, gui.getTabbedPane().getTabCount());
	}
	
	@Test
	public void testClientSidePaneChanged() {
		gui = new CustomerStationGUI(pc);
		int expected = 2;
		gui.clientSidePaneChanged(controls.get(0), expected);
		assertEquals(expected, gui.getTabbedPane().getSelectedIndex());
	}

}
