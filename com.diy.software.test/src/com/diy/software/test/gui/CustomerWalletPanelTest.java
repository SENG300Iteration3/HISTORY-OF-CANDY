package com.diy.software.test.gui;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.StationControl;
import com.diy.software.controllers.WalletControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.diy.software.listeners.WalletControlListener;
import com.diy.software.test.logic.StubSystem;
import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.necchi.Barcode;
import com.jimmyselectronics.necchi.BarcodeScanner;
import com.jimmyselectronics.necchi.BarcodeScannerListener;
import com.jimmyselectronics.opeechee.Card;
import com.jimmyselectronics.opeechee.Card.CardData;
import com.jimmyselectronics.opeechee.CardReader;
import com.jimmyselectronics.opeechee.CardReaderListener;

import ca.powerutility.PowerGrid;
import swing.panels.CustomerWalletPanel;

public class CustomerWalletPanelTest {
	
	FakeDataInitializer fdi;
	StationControl sc;
	CustomerWalletPanel panel;
	WalletControlListenerStub wStub;
	StubSystem sStub;
	CardReaderListenerStub cStub;
	BarcodeScannerListenerStub bStub;

	@Before
	public void setUp() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();
		
		fdi = new FakeDataInitializer();
		fdi.addCardData();
		fdi.addFakeMembers();
		
		sc = new StationControl(fdi);
		panel = new CustomerWalletPanel(sc);
		
		sStub = new StubSystem();
		sc.register(sStub);
		
		wStub = new WalletControlListenerStub();
		sc.getWalletControl().addListener(wStub);
		
		cStub = new CardReaderListenerStub();
		sc.station.cardReader.register(cStub);
		
		bStub = new BarcodeScannerListenerStub();
		sc.station.mainScanner.register(bStub);
	}

	@After
	public void tearDown() throws Exception {
		fdi = null;
		sc = null;
		panel = null;
		wStub = null;
		sStub = null;
	}

	@Test
	public void testCc1Button() {
		panel.getCc1().doClick();
		assertTrue(wStub.cardSelected);
	}
	
	@Test
	public void testCc2Button() {
		panel.getCc2().doClick();
		assertTrue(wStub.cardSelected);
	}
	
	@Test
	public void testCc3Button() {
		panel.getCc3().doClick();
		assertTrue(wStub.cardSelected);
	}
	
	@Test
	public void testGcButton() {
		panel.getGc().doClick();
		assertTrue(wStub.cardSelected);
	}
	
	@Test
	public void testMButton() {
		panel.getM().doClick();
		assertTrue(wStub.membershipCardSelected);
	}
	
	@Test
	public void testTapButton() {
		panel.getCc2().doClick();
		panel.getTapButton().setEnabled(true);
		panel.getTapButton().doClick();
		assertTrue(cStub.cardTapped);
	}
	
	@Test
	public void testSwipeButton() {
		panel.getCc2().doClick();
		panel.getSwipeButton().setEnabled(true);
		panel.getSwipeButton().doClick();
		assertTrue(cStub.cardSwiped);
	}
	
	@Test
	public void testInsertOrEjectButton() {
		panel.getCc2().doClick();
		panel.getInsertOrEjectButton().setEnabled(true);
		panel.getInsertOrEjectButton().doClick();
		assertEquals("insert", sStub.paymentType);
		
	}
	
	@Test
	public void testScanMemCardButton() {
		Card[] cards = fdi.getCards();
		Card membershipCard = cards[3];
		String expected = membershipCard.number;
		
		panel.getM().doClick();
		panel.getScanMemButton().setEnabled(true);
		panel.getScanMemButton().doClick();
		
		assertEquals(expected, bStub.scannedBarcode);
		
	}
	
	public class CardReaderListenerStub implements CardReaderListener{
		
		public boolean cardInserted = false;
		public boolean cardTapped = false;
		public boolean cardSwiped = false;
		public boolean cardDataRead = false;

		@Override
		public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void turnedOn(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void turnedOff(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void cardInserted(CardReader reader) {
			cardInserted = true;
			
		}

		@Override
		public void cardRemoved(CardReader reader) {
			cardInserted = false;
			
		}

		@Override
		public void cardTapped(CardReader reader) {
			cardTapped = true;
			
		}

		@Override
		public void cardSwiped(CardReader reader) {
			cardSwiped = true;
			
		}

		@Override
		public void cardDataRead(CardReader reader, CardData data) {
			cardDataRead = true;
			
		}
	}
	
	
	public class WalletControlListenerStub implements WalletControlListener{
		
		public boolean cardSelected = false;
		public boolean membershipCardSelected = false;

		@Override
		public void cardHasBeenSelected(WalletControl wc) {
			cardSelected = true;
			
		}

		@Override
		public void membershipCardHasBeenSelected(WalletControl wc) {
			membershipCardSelected= true;
			
		}

		@Override
		public void membershipCardInputEnabled(WalletControl wc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void cardPaymentsEnabled(WalletControl wc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void cardPaymentsDisabled(WalletControl wc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void cardHasBeenInserted(WalletControl wc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void cardWithPinInserted(WalletControl wc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void cardWithPinRemoved(WalletControl wc) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void membershipCardInputCanceled(WalletControl walletControl) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public class BarcodeScannerListenerStub implements BarcodeScannerListener{
		
		public String scannedBarcode;

		@Override
		public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void turnedOn(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void turnedOff(AbstractDevice<? extends AbstractDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void barcodeScanned(BarcodeScanner barcodeScanner, Barcode barcode) {
			scannedBarcode = barcode.toString();
			
		}
		
	}

}
