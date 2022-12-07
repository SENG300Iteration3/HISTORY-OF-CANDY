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

public class AttendantActionsGUITest {
	
	PaneControl pc;
	ArrayList<StationControl> controls;
	int numTabs;
	AttendantActionsGUI gui;

	@Before
	public void setUp() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();
		
		controls = new ArrayList<StationControl>();
		numTabs = 3;
		for (int i = 0; i < numTabs; i++) {
			controls.add(new StationControl());
		}
		pc = new PaneControl(controls);
		
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAttendantStationGUI() {
		gui = new AttendantActionsGUI(pc);
		assertEquals(numTabs, gui.getTabbedPane().getTabCount());
	}
	
	@Test
	public void testClientSidePaneChanged() {
		gui = new AttendantActionsGUI(pc);
		int expected = 2;
		gui.clientSidePaneChanged(controls.get(0), expected);
		assertEquals(expected, gui.getTabbedPane().getSelectedIndex());
	}

}
