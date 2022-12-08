package com.diy.software.test.gui;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.PaneControl;
import com.diy.software.controllers.StationControl;

import ca.powerutility.PowerGrid;
import swing.frames.AttendantActionsGUI;
import swing.frames.AttendantStationGUI;

public class AttendantStationGUITest {
	
	AttendantStationGUI gui;
	PaneControl pc;
	ArrayList<StationControl> controls;
	int numTabs;

	@Before
	public void setUp() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();
		
		controls = new ArrayList<StationControl>();
		numTabs = 3;
		for (int i = 0; i < numTabs; i++) {
			controls.add(new StationControl());
		}
		pc = new PaneControl(controls);
		
		gui = new AttendantStationGUI(pc);
	}

	@After
	public void tearDown() throws Exception {
		gui = null;
		pc = null;
		controls = null;
	}

	@Test
	public void testClientSidePaneChanged() {
		int expected = 2;
		gui.clientSidePaneChanged(controls.get(0), expected);
		assertEquals(expected, gui.getStationPane().tabbedPane.getSelectedIndex());
	}

}
