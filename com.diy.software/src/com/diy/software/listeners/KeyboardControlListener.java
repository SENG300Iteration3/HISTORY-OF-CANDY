package com.diy.software.listeners;

import com.diy.software.controllers.KeyboardControl;

public interface KeyboardControlListener {
	
	public void awaitingKeyboardInput(KeyboardControl kc);
	
	public void keyboardInputRecieved(KeyboardControl kc);
	
	public void keyboardInputCompleted(KeyboardControl kc, String query);
	
}