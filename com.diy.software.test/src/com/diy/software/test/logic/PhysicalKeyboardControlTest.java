package com.diy.software.test.logic;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.KeyboardControl;
import com.diy.software.controllers.PhysicalKeyboardControl;
import com.diy.software.listeners.KeyboardControlListener;
import com.jimmyselectronics.nightingale.Keyboard;

public class PhysicalKeyboardControlTest {
	PhysicalKeyboardControl pkc;
	Keyboard keyboard;
	KeyboardControlStub kcStub;

	@Before
	public void setUp() throws Exception {
		keyboard = new Keyboard(Keyboard.WINDOWS_QWERTY);
		pkc = new PhysicalKeyboardControl(keyboard);
		kcStub = new KeyboardControlStub();
		pkc.addListener(kcStub);
		
	}

	@Test
	public void testKeyPressedOneKey() {
		assertTrue(kcStub.label.equals(""));
		assertTrue(kcStub.inputText.equals(""));
		pkc.keyPressed(keyboard, "H");
		assertTrue(kcStub.label.equals("H"));
		assertTrue(kcStub.inputText.equals("h"));
		
	}
	
	@Test
	public void testKeyPressedMultipleKeys() {
		assertTrue(kcStub.label.equals(""));
		assertTrue(kcStub.inputText.equals(""));
		
		pkc.keyPressed(keyboard, "H");
		assertTrue(kcStub.label.equals("H"));
		assertTrue(kcStub.inputText.equals("h"));
		
		pkc.keyPressed(keyboard, "E");
		assertTrue(kcStub.label.equals("E"));
		assertTrue(kcStub.inputText.equals("he"));
		
		pkc.keyPressed(keyboard, "L");
		assertTrue(kcStub.label.equals("L"));
		assertTrue(kcStub.inputText.equals("hel"));
		
		pkc.keyPressed(keyboard, "L");
		assertTrue(kcStub.label.equals("L"));
		assertTrue(kcStub.inputText.equals("hell"));
		
		pkc.keyPressed(keyboard, "O");
		assertTrue(kcStub.label.equals("O"));
		assertTrue(kcStub.inputText.equals("hello"));
		pkc.inputComplete();
		assertTrue(kcStub.inputText.equals(kcStub.completeText));
		
	}
	
	@Test
	public void testKeyReleasedOneKey() {
		assertTrue(kcStub.label.equals(""));
		assertTrue(kcStub.inputText.equals(""));
		pkc.keyReleased(keyboard, "H");
		assertTrue(kcStub.label.equals(""));
		assertTrue(kcStub.inputText.equals(""));
		
	}

	@Test
	public void testKeyReleasedLShift() {
		assertTrue(kcStub.label.equals(""));
		assertTrue(kcStub.inputText.equals(""));
		pkc.keyPressed(keyboard, "Shift (Left)");
		assertTrue(kcStub.label.equals("Shift (Left)"));
		assertTrue(kcStub.inputText.equals(""));
		pkc.keyPressed(keyboard, "W");
		assertTrue(kcStub.label.equals("W"));
		assertTrue(kcStub.inputText.equals("W"));
		
		pkc.keyReleased(keyboard, "Shift (Left)");
		
		pkc.keyPressed(keyboard, "W");
		assertTrue(kcStub.label.equals("W"));
		assertTrue(kcStub.inputText.equals("Ww"));
	}
	
	@Test
	public void testKeyReleasedRShift() {
		assertTrue(kcStub.label.equals(""));
		assertTrue(kcStub.inputText.equals(""));
		pkc.keyPressed(keyboard, "Shift (Right)");
		assertTrue(kcStub.label.equals("Shift (Right)"));
		assertTrue(kcStub.inputText.equals(""));
		pkc.keyPressed(keyboard, "R");
		assertTrue(kcStub.label.equals("R"));
		assertTrue(kcStub.inputText.equals("R"));
		
		pkc.keyReleased(keyboard, "Shift (Right)");
		
		pkc.keyPressed(keyboard, "R");
		assertTrue(kcStub.label.equals("R"));
		assertTrue(kcStub.inputText.equals("Rr"));
	}

	public class KeyboardControlStub implements KeyboardControlListener{

		String inputText = "";
		String label = "";
		String completeText = "";

		@Override
		public void keyboardInputRecieved(KeyboardControl kc, String text, String key, int pointerPosition) {
			this.label = key;
			this.inputText = text;
			
		}

		@Override
		public void keyboardInputCompleted(KeyboardControl kc, String text) {
			this.completeText = text;
		}
		
	}
}
