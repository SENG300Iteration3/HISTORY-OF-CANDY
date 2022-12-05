package com.diy.software.controllers;

import java.util.ArrayList;

import com.diy.software.listeners.KeyboardControlListener;
import com.diy.software.util.MathUtils;

public class KeyboardControl {
	protected ArrayList<KeyboardControlListener> listeners = new ArrayList<>();
	protected String text = "";
	protected boolean capsLockOn = false; // assumes caps lock is initially off
	protected boolean shiftPressed = false;
	protected int pointer = 0;

	public void addListener(KeyboardControlListener l) {
		listeners.add(l);
	}

	public void removeListener(KeyboardControlListener l) {
		listeners.remove(l);
	}
	
	public void pressKey(String key) {
		keyAction(key);
		for (KeyboardControlListener l : listeners)
			l.keyboardInputRecieved(this, this.text, key, this.pointer);
	}

	public void releaseKey(String key) {
		for (KeyboardControlListener l : listeners)
			l.awaitingKeyboardInput(this);
	}
	
	public void inputComplete() {
		for (KeyboardControlListener l : listeners)
			l.keyboardInputCompleted(this, this.text);
	}
	
	// Supports functionality for letters, numbers, symbols, backspace, delete, L/R
	// arrows, L/R shift and enter
	protected void keyAction(String key) {
		if (key.length() == 1) {
			addTextAtPointer(getLetterCase(key)); // Alphanumeric key
		} else if (key.equals("Enter")) {
			inputComplete();
		} else if (key.equals("CapsLock")) {
			capsLockOn = !capsLockOn;
		} else if (key.startsWith("Shift")) {
			shiftPressed = !shiftPressed;
		} else if (key.equals("Spacebar")) {
			addTextAtPointer(" ");
		} else if (key.equals("Tab")) {
			addTextAtPointer("	");
		} else if (isNumberOrSymbol(key)) {
			addTextAtPointer(getNumberOrSymbol(key));
		} else if (key.startsWith("Left")) {
			movePointer(-1);
		} else if (key.startsWith("Right")) {
			movePointer(1);
		} else if (key.startsWith("Backspace")) {
			if (pointer > 0)
				removeCharAtPointer();
		} else if (key.startsWith("Delete")) {
			removeCharInPlace();
		}
	}
	
	private void addTextAtPointer(String newText) {
		StringBuilder sb = new StringBuilder(this.text);
		this.text = sb.insert(pointer, newText).toString();
		movePointer(newText.length()); // Move pointer the appropriate number of spaces forward
	}
	
	private void removeCharInPlace() {
		if (pointer < text.length()) {
			if (pointer > 0) {
				// Move the pointer forwards again
				movePointer(1);
			}
			// Deleting the char moves the pointer back by 1
			removeCharAtPointer();
		}
	}
	
	private void removeCharAtPointer() {
		// Move the pointer back by 1, delete the char, then update the text
		this.text = new StringBuilder(this.text).deleteCharAt(movePointer(-1)).toString();
	}
	
	//@return new value of pointer
	private int movePointer(int delta) {
		// Make sure pointer doesn't fall out of range
		this.pointer = MathUtils.clamp(pointer + delta, 0, text.length());
		
		return this.pointer;
	}

	private String getLetterCase(String key) {
		return capsLockOn || shiftPressed ? key.toUpperCase() : key.toLowerCase();
	}
	
	// Identifies whether or not a key is 0-9 or one of !@#$%^&*()
	private boolean isNumberOrSymbol(String key) {
		int ascii = (int) key.toCharArray()[0];
		return ascii >= 48 && ascii <= 57; // identifies number/symbol key labels
	}

	// Gets symbol or number from label (there is a space in between, so the char index is 0 or 2)
	private String getNumberOrSymbol(String key) {
		return String.valueOf(key.charAt(!shiftPressed ? 0 : 2));
	}
}