package com.diy.software.test.logic;

import static org.junit.Assert.*;

import java.awt.event.ActionEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diy.software.controllers.KeyboardControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.controllers.VirtualKeyboardControl;
import com.diy.software.listeners.KeyboardControlListener;
import com.jimmyselectronics.nightingale.Keyboard;

public class VirtualKeyboardControlTest {
	VirtualKeyboardControl vkc;
	Keyboard keyboard;
	KeyboardControlStub kcStub;
	private StationControl sc;

	@Before
	public void setUp() throws Exception {
		sc = new StationControl();
		keyboard = new Keyboard(Keyboard.WINDOWS_QWERTY);
		vkc = new VirtualKeyboardControl(sc);
		kcStub = new KeyboardControlStub();
		vkc.addListener(kcStub);
	}

	@Test
	public void testActionPerformedOneLetter() {
		assertTrue(kcStub.label.equals(""));
		assertTrue(kcStub.inputText.equals(""));
		vkc.actionPerformed(new ActionEvent(this, 0, "KEY_PRESS: H"));
		assertTrue(kcStub.label.equals("H"));
		assertTrue(kcStub.inputText.equals("h"));
		
	}
	
	@Test
	public void testActionPerformedMultipleLetters() {
		assertTrue(kcStub.label.equals(""));
		assertTrue(kcStub.inputText.equals(""));
		vkc.actionPerformed(new ActionEvent(this, 0, "KEY_PRESS: H"));
		assertTrue(kcStub.label.equals("H"));
		assertTrue(kcStub.inputText.equals("h"));
		
		vkc.actionPerformed(new ActionEvent(this, 0, "KEY_PRESS: I"));
		assertTrue(kcStub.label.equals("I"));
		assertTrue(kcStub.inputText.equals("hi"));
		
		vkc.actionPerformed(new ActionEvent(this, 0, "KEY_PRESS: CapsLock"));
		
		vkc.actionPerformed(new ActionEvent(this, 0, "KEY_PRESS: 1 !"));
		assertTrue(kcStub.label.equals("1 !"));
		assertTrue(kcStub.inputText.equals("hi!"));
		
	}
	
	@Test
	public void testActionPerformedUnknownInput() {
		assertTrue(kcStub.label.equals(""));
		assertTrue(kcStub.inputText.equals(""));
		vkc.actionPerformed(new ActionEvent(this, 0, "H"));
		assertTrue(kcStub.label.equals(""));
		assertTrue(kcStub.inputText.equals(""));
		
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
