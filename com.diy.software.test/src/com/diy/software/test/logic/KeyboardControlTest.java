package com.diy.software.test.logic;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.KeyboardControl;
import com.diy.software.listeners.KeyboardControlListener;
import com.jimmyselectronics.nightingale.Keyboard;

public class KeyboardControlTest extends KeyboardControl {
	public KeyboardControlTest() {
		super(new Keyboard(Keyboard.WINDOWS_QWERTY));
	}

	KeyboardControl kc;
	KeyboardControlStub kcStub;

	@Before
	public void setUp() throws Exception {
		
		kcStub = new KeyboardControlStub();
		
		this.addListener(kcStub);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testKeyActionChar() {
		
		assertTrue(this.text.equals(""));
		assertTrue(this.pointer == 0);
		keyAction("a");
		assertTrue(this.pointer == 1);
		assertTrue(this.text.equals("a"));
	}
	
	@Test
	public void testKeyActionCharCapital() {
		testKeyActionCapsLockOn();
		
		assertTrue(this.text.equals(""));
		assertTrue(this.pointer == 0);
		keyAction("a");
		assertTrue(this.pointer == 1);
		assertTrue(this.text.equals("A"));
	}
	
	@Test
	public void testKeyAction2Chars() {
		testKeyActionChar();
		
		assertTrue(this.text.equals("a"));
		assertTrue(this.pointer == 1);
		keyAction("b");
		assertTrue(this.pointer == 2);
		assertTrue(this.text.equals("ab"));
	}
	
	@Test
	public void testKeyAction2CharsCaptialThenLower() {
		testKeyActionCharCapital();
		testKeyActionCapsLockOff();
		
		assertTrue(this.text.equals("A"));
		assertTrue(this.pointer == 1);
		keyAction("b");
		assertTrue(this.pointer == 2);
		assertTrue(this.text.equals("Ab"));
	}
	
	@Test
	public void testKeyAction2CharsLowerThenCaptial() {
		testKeyActionChar();
		testKeyActionCapsLShiftOn();
		
		assertTrue(this.text.equals("a"));
		assertTrue(this.pointer == 1);
		keyAction("b");
		assertTrue(this.pointer == 2);
		assertTrue(this.text.equals("aB"));
	}
	
	@Test
	public void testKeyActionEnter() {
		String testKey = "Enter";
		
		kcStub.inputCompleted = false;
		keyAction(testKey);
		assertTrue(kcStub.inputCompleted);
		
	}
	
	@Test
	public void testKeyActionCapsLockOn() {
		String testKey = "CapsLock";
		
		this.capsLockOn = false;
		keyAction(testKey);
		assertTrue(this.capsLockOn);
	}
	
	@Test
	public void testKeyActionCapsLockOff() {
		String testKey = "CapsLock";
		
		this.capsLockOn = true;
		keyAction(testKey);
		assertFalse(this.capsLockOn);
	}
	
	@Test
	public void testKeyActionCapsLShiftOn() {
		String testKey = "Shift (Left)";
		
		this.leftShiftPressed = false;
		keyAction(testKey);
		assertTrue(this.leftShiftPressed);
	}
	
	@Test
	public void testKeyActionCapsLShiftOff() {
		String testKey = "Shift (Left)";
		
		this.leftShiftPressed = true;
		keyAction(testKey);
		assertFalse(this.leftShiftPressed);
	}
	
	@Test
	public void testKeyActionCapsRShiftOff() {
		String testKey = "Shift (Right)";
		
		this.rightShiftPressed = true;
		keyAction(testKey);
		assertFalse(this.rightShiftPressed);
	}
	
	@Test
	public void testKeyActionCapsRShiftOn() {
		String testKey = "Shift (Right)";
		
		this.rightShiftPressed = false;
		keyAction(testKey);
		assertTrue(this.rightShiftPressed);
	}
	
	@Test
	public void testKeyActionSpacebar() {
		String testKey = "Spacebar";
		
		assertTrue(this.text.equals(""));
		assertTrue(this.pointer == 0);
		keyAction(testKey);
		assertTrue(this.pointer == 1);
		assertTrue(this.text.equals(" "));
	}
	
	@Test
	public void testKeyActionTab() {
		String testKey = "Tab";
		
		assertTrue(this.text.equals(""));
		assertTrue(this.pointer == 0);
		keyAction(testKey);
		assertTrue(this.pointer == 1);
		assertTrue(this.text.equals("	"));
	}
	
	@Test
	public void testKeyActionLeft() {
		testKeyAction2Chars();
		
		String testKey = "Left";
		
		assertTrue(this.pointer == 2);
		keyAction(testKey);
		assertTrue(this.pointer == 1);
	}
	
	@Test
	public void testKeyActionRight() {
		testKeyActionLeft();
		
		String testKey = "Right";
		
		assertTrue(this.pointer == 1);
		keyAction(testKey);
		assertTrue(this.pointer == 2);
	}
	
	@Test
	public void testKeyActionBackSpace() {
		testKeyAction2Chars();
		
		String testKey = "Backspace";
		
		assertTrue(this.text.equals("ab"));
		assertTrue(this.pointer == 2);
		keyAction(testKey);
		assertTrue(this.pointer == 1);
		assertTrue(this.text.equals("a"));
	}
	
	@Test
	public void testKeyActionBackBackspaceOutOFbounds() {
		testKeyActionLeft();
		keyAction("Left");
		
		String testKey = "Backspace";
		
		assertTrue(this.text.equals("ab"));
		assertTrue(this.pointer == 0);
		keyAction(testKey);
		assertTrue(this.pointer == 0);
		assertTrue(this.text.equals("ab"));
	}
	
	@Test
	public void testKeyActionBackDelete() {
		testKeyActionLeft();
		keyAction("Left");
		
		String testKey = "Delete";
		
		assertTrue(this.text.equals("ab"));
		assertTrue(this.pointer == 0);
		keyAction(testKey);
		assertTrue(this.pointer == 0);
		assertTrue(this.text.equals("b"));
	}
	
	@Test
	public void testKeyActionBackDeleteMiddle() {
		testKeyActionLeft();
		
		String testKey = "Delete";
		
		assertTrue(this.text.equals("ab"));
		assertTrue(this.pointer == 1);
		keyAction(testKey);
		assertTrue(this.pointer == 1);
		assertTrue(this.text.equals("a"));
	}
	
	@Test
	public void testKeyActionBackDeleteOutOfBounds() {
		String testKey = "Delete";
		
		assertTrue(this.text.equals(""));
		assertTrue(this.pointer == 0);
		keyAction(testKey);
		assertTrue(this.pointer == 0);
		assertTrue(this.text.equals(""));
	}
	
	@Test
	public void testKeyActionNumber() {
		
		assertTrue(this.text.equals(""));
		assertTrue(this.pointer == 0);
		keyAction("1 !");
		assertTrue(this.pointer == 1);
		assertTrue(this.text.equals("1"));
	}
	
	@Test
	public void testKeyActionSymbol() {
		testKeyActionCapsLockOn();
		
		assertTrue(this.text.equals(""));
		assertTrue(this.pointer == 0);
		keyAction("6 ^");
		assertTrue(this.pointer == 1);
		assertTrue(this.text.equals("^"));
	}
	
	@Test
	public void testKeyActionCharAndSymbol() {
		testKeyActionChar();
		testKeyActionCapsRShiftOn();
		
		assertTrue(this.text.equals("a"));
		assertTrue(this.pointer == 1);
		keyAction("6 ^");
		assertTrue(this.pointer == 2);
		assertTrue(this.text.equals("a^"));
	}
	
	@Test
	public void testKeyActionUnrecognized() {
		
		assertTrue(this.text.equals(""));
		assertTrue(this.pointer == 0);
		keyAction("Home");
		assertTrue(this.pointer == 0);
		assertTrue(this.text.equals(""));
	}
	
	@Test
	public void testKeyActionUnrecognizedSymbols() {
		
		assertTrue(this.text.equals(""));
		assertTrue(this.pointer == 0);
		keyAction("θ π");
		assertTrue(this.pointer == 0);
		assertTrue(this.text.equals(""));
	}
	
	public class KeyboardControlStub implements KeyboardControlListener{

		boolean inputRecieved = false;
		boolean inputCompleted = false;

		@Override
		public void keyboardInputRecieved(KeyboardControl kc, String text, String key, int pointerPosition) {
			inputRecieved = true;
			
		}

		@Override
		public void keyboardInputCompleted(KeyboardControl kc, String text) {
			inputCompleted = true;
			
		}
		
	}

}
