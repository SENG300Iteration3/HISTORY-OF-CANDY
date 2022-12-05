package com.diy.software.test.logic;

import static org.junit.Assert.*;

import java.awt.event.KeyEvent;

import javax.swing.JTextField;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.KeyboardInputControl;
import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.nightingale.Keyboard;
import com.jimmyselectronics.nightingale.KeyboardListener;

import ca.powerutility.PowerGrid;

public class KeyboardInputControlTest {

	Keyboard keyboard;
	KeyboardInputControl kic;
	KeyboardStub boardStub;

	@Before
	public void setUp() throws Exception {
		PowerGrid.engageUninterruptiblePowerSource();
		keyboard = new Keyboard(Keyboard.WINDOWS_QWERTY);
		keyboard.plugIn();
		keyboard.turnOn();
		keyboard.enable();
		kic = new KeyboardInputControl(keyboard);
		boardStub = new KeyboardStub();
		keyboard.register(boardStub);
		
	}

	@After
	public void tearDown() throws Exception {
		PowerGrid.reconnectToMains();
	}

	@Test
	public void testKeyPressedChar() {
		KeyEvent key = new KeyEvent(new JTextField(), KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_A, 'A');
		assertTrue(boardStub.keyPressed.equals(""));
		kic.keyPressed(key);
		assertTrue(boardStub.keyPressed.equals("A"));
	}
	
	@Test
	public void testKeyPressedDigit() {
		KeyEvent key = new KeyEvent(new JTextField(), KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_2, '2');
		assertTrue(boardStub.keyPressed.equals(""));
		kic.keyPressed(key);
		assertTrue(boardStub.keyPressed.equals("2 @"));
	}
	
	@Test
	public void testKeyPressedSpecial() {
		KeyEvent key = new KeyEvent(new JTextField(), KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_SPACE, ' ');
		assertTrue(boardStub.keyPressed.equals(""));
		kic.keyPressed(key);
		assertTrue(boardStub.keyPressed.equals("Spacebar"));
	}
	
	@Test
	public void testKeyReleasedChar() {
		testKeyPressedChar();
		KeyEvent key = new KeyEvent(new JTextField(), KeyEvent.KEY_RELEASED, 0, 0, KeyEvent.VK_A, 'A');
		assertTrue(boardStub.keyReleased.equals(""));
		kic.keyReleased(key);
		assertTrue(boardStub.keyReleased.equals("A"));
	}
	
	
	@Test
	public void testKeyPressedUnknown() {
		KeyEvent key = new KeyEvent(new JTextField(), KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ESCAPE, ' ');
		assertTrue(boardStub.keyPressed.equals(""));
		kic.keyPressed(key);
		assertTrue(boardStub.keyPressed.equals(""));
	}
	
	@Test
	public void testKeyReleasedUnknown() {
		KeyEvent key = new KeyEvent(new JTextField(), KeyEvent.KEY_RELEASED, 0, 0, KeyEvent.VK_JAPANESE_HIRAGANA, ' ');
		assertTrue(boardStub.keyReleased.equals(""));
		kic.keyReleased(key);
		assertTrue(boardStub.keyReleased.equals(""));
	}
	
	@Test
	public void testKeyPressedDisabled() {
		keyboard.disable();
		KeyEvent key = new KeyEvent(new JTextField(), KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_A, 'A');
		assertTrue(boardStub.keyPressed.equals(""));
		kic.keyPressed(key);
		assertTrue(boardStub.keyPressed.equals(""));
	}
	
	@Test
	public void testKeyReleasedDisabled() {
		testKeyPressedChar();
		keyboard.disable();
		KeyEvent key = new KeyEvent(new JTextField(), KeyEvent.KEY_RELEASED, 0, 0, KeyEvent.VK_A, 'A');
		assertTrue(boardStub.keyReleased.equals(""));
		kic.keyReleased(key);
		assertTrue(boardStub.keyReleased.equals(""));
	}
	
	
	
	public class KeyboardStub implements KeyboardListener{

		String keyPressed = "";
		String keyReleased = "";

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
		public void keyPressed(Keyboard keyboard, String label) {
			keyPressed = label;
			
		}

		@Override
		public void keyReleased(Keyboard keyboard, String label) {
			keyReleased = label;
			
		}
		
	}

}
