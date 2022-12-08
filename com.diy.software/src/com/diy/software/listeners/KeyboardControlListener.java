package com.diy.software.listeners;

import com.diy.software.controllers.KeyboardControl;

public interface KeyboardControlListener {
	
	// Gives the text entered so far along with the the most recent key pressed
	public void keyboardInputRecieved(KeyboardControl kc, String text, String key, int pointerPosition);
	
	// Special event for when the enter key is pressed
	public void keyboardInputCompleted(KeyboardControl kc, String text);
}