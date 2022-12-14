package com.diy.software.test.logic;

import static org.junit.Assert.*;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.PaneControl;
import com.diy.software.controllers.StationControl;

import ca.powerutility.PowerGrid;
import swing.panes.AttendantStationPane;
import swing.styling.GUI_JPanel;

public class AttendantPaneTest {
	StationControl sc;
	ArrayList<StationControl> stationControls;
	AttendantControlStub acStub;
	AttendantStationPane ap;
	JFrame frame;
	PaneControl pc;

	@Before
	public void setUp() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();
		stationControls = new ArrayList<StationControl>();
		sc = new StationControl();
		acStub = new AttendantControlStub();
		sc.getAttendantControl().addListener(acStub);
		stationControls.add(sc);
		
		pc = new PaneControl(stationControls);
		
		frame = new JFrame();
		ap = new AttendantStationPane(pc, frame);
	}

	@After
	public void tearDown() throws Exception {
		PowerGrid.reconnectToMains();
	}

	@Test
	public void testLoggedInPass() {
		assertTrue(ap.currentComponent.getClass().equals(GUI_JPanel.class));
		ap.loggedIn(true);
		assertTrue(ap.currentComponent.getClass().equals(JTabbedPane.class));
	}
	
	@Test
	public void testLoggedInFail() {
		assertTrue(ap.currentComponent.getClass().equals(GUI_JPanel.class));
		ap.loggedIn(false);
		assertTrue(ap.currentComponent.getClass().equals(GUI_JPanel.class));
	}

}
