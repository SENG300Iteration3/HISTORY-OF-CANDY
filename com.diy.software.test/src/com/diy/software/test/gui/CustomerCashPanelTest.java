package com.diy.software.test.gui;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.CashControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.CashControlListener;
import com.unitedbankingservices.banknote.BanknoteDispensationSlotObserver;
import com.unitedbankingservices.banknote.BanknoteInsertionSlot;
import com.unitedbankingservices.banknote.BanknoteInsertionSlotObserver;
import com.unitedbankingservices.coin.CoinSlot;
import com.unitedbankingservices.coin.CoinSlotObserver;

import ca.powerutility.PowerGrid;
import swing.panels.CustomerCashPanel;

public class CustomerCashPanelTest {
	
	CustomerCashPanel panel;
	StationControl sc;
	CashControl cc;
	CoinSlotObserverStub cStub;
	BanknoteInsertionSlotObserverStub bStub;

	@Before
	public void setUp() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();
		
		sc = new StationControl();
		
		panel = new CustomerCashPanel(sc);
		
		cStub = new CoinSlotObserverStub();
		sc.station.coinSlot.attach(cStub);
		
		bStub = new BanknoteInsertionSlotObserverStub();
		sc.station.banknoteInput.attach(bStub);
		
		cc = sc.getCashControl();
		
		panel.coinInsertionEnabled(cc);
		panel.noteInsertionEnabled(cc);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPennyButton() {
		panel.getPenny().doClick();
		assertTrue(cStub.coinInserted);
	}
	
	@Test
	public void testNickelButton() {
		panel.getNickel().doClick();
		assertTrue(cStub.coinInserted);
	}
	
	@Test
	public void testDimeButton() {
		panel.getDime().doClick();
		assertTrue(cStub.coinInserted);
	}
	
	@Test
	public void testQuarterButton() {
		panel.getQuarter().doClick();
		assertTrue(cStub.coinInserted);
	}
	
	@Test
	public void testLoonieButton() {
		panel.getLoonie().doClick();
		assertTrue(cStub.coinInserted);
	}
	
	@Test
	public void testToonieButton() {
		panel.getToonie().doClick();
		assertTrue(cStub.coinInserted);
	}
	
	@Test
	public void testPennyButtonCoinInputDisabled() {
		panel.coinInsertionDisabled(cc);
		panel.getPenny().doClick();
		assertFalse(cStub.coinInserted);
	}
	
	@Test
	public void testNickelButtonCoinInputDisabled() {
		panel.coinInsertionDisabled(cc);
		panel.getNickel().doClick();
		assertFalse(cStub.coinInserted);
	}
	
	@Test
	public void testDimeButtonCoinInputDisabled() {
		panel.coinInsertionDisabled(cc);
		panel.getDime().doClick();
		assertFalse(cStub.coinInserted);
	}
	
	@Test
	public void testQuarterButtonCoinInputDisabled() {
		panel.coinInsertionDisabled(cc);
		panel.getQuarter().doClick();
		assertFalse(cStub.coinInserted);
	}
	
	@Test
	public void testLoonieButtonCoinInputDisabled() {
		panel.coinInsertionDisabled(cc);
		panel.getLoonie().doClick();
		assertFalse(cStub.coinInserted);
	}
	
	@Test
	public void testToonieButtonCoinInputDisabled() {
		panel.coinInsertionDisabled(cc);
		panel.getToonie().doClick();
		assertFalse(cStub.coinInserted);
	}
	
	@Test
	public void testDollar1Button() {
		panel.getDollar1().doClick();
		assertTrue(bStub.banknoteInserted);
	}
	
	@Test
	public void testDollar5Button() {
		panel.getDollar5().doClick();
		assertTrue(bStub.banknoteInserted);
	}
	
	@Test
	public void testDollar10Button() {
		panel.getDollar10().doClick();
		assertTrue(bStub.banknoteInserted);
	}
	
	@Test
	public void testDollar20Button() {
		panel.getDollar20().doClick();
		assertTrue(bStub.banknoteInserted);
	}
	
	@Test
	public void testDollar50Button() {
		panel.getDollar50().doClick();
		assertTrue(bStub.banknoteInserted);
	}
	
	@Test
	public void testDollar100Button() {
		panel.getDollar100().doClick();
		assertTrue(bStub.banknoteInserted);
	}
	
	@Test
	public void testDollar1ButtonNoteInputDisabled() {
		panel.noteInsertionDisabled(cc);
		panel.getDollar1().doClick();
		assertFalse(bStub.banknoteInserted);
	}
	
	@Test
	public void testDollar5ButtonNoteInputDisabled() {
		panel.noteInsertionDisabled(cc);
		panel.getDollar5().doClick();
		assertFalse(bStub.banknoteInserted);
	}
	
	@Test
	public void testDollar10ButtonNoteInputDisabled() {
		panel.noteInsertionDisabled(cc);
		panel.getDollar10().doClick();
		assertFalse(bStub.banknoteInserted);
	}
	
	@Test
	public void testDollar20ButtonNoteInputDisabled() {
		panel.noteInsertionDisabled(cc);
		panel.getDollar20().doClick();
		assertFalse(bStub.banknoteInserted);
	}
	
	@Test
	public void testDollar50ButtonNoteInputDisabled() {
		panel.noteInsertionDisabled(cc);
		panel.getDollar50().doClick();
		assertFalse(bStub.banknoteInserted);
	}
	
	@Test
	public void testDollar100ButtonNoteInputDisabled() {
		panel.noteInsertionDisabled(cc);
		panel.getDollar100().doClick();
		assertFalse(bStub.banknoteInserted);
	}
	
	public class CoinSlotObserverStub implements CoinSlotObserver{
		
		public boolean coinInserted = false;

		@Override
		public void coinInserted(CoinSlot slot) {
			coinInserted = true;
			
		}
	}
	
	public class BanknoteInsertionSlotObserverStub implements BanknoteInsertionSlotObserver {
		
		public boolean banknoteInserted = false;
		
		public void banknoteInserted(BanknoteInsertionSlot slot) {
			banknoteInserted = true;
		}

		
		public void banknoteEjected(BanknoteInsertionSlot slot) {}

	
		public void banknoteRemoved(BanknoteInsertionSlot slot) {}
	}

}
