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
			addText(getLetterCase(key)); // Alphanumeric key
		} else if (key.equals("CapsLock")) {
			capsLockOn = !capsLockOn;
		} else if (key.startsWith("Shift")) {
			shiftPressed = !shiftPressed;
		} else if (key.equals("Spacebar")) {
			addText(" ");
		} else if (key.equals("Tab")) {
			addText("	");
		} else if (isNumberOrSymbol(key)) {
			addText(getNumberOrSymbol(key));
		} else if (key.equals("Enter")) {
			inputComplete();
		} else if (key.startsWith("Left")) {
			movePointer(-1);
		} else if (key.startsWith("Right")) {
			movePointer(1);
		}
	}
	
	private void addText(String newText) {
		this.text += newText;
		movePointer(newText.length()); // Move pointer the appropriate number of spaces forward
	}
	
	private void movePointer(int delta) {
		this.pointer = MathUtils.clamp(pointer + delta, 0, text.length());
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