package com.diy.software.listeners;

import com.diy.software.controllers.TextLookupControl;

public interface TextLookupControlListener {
	
	public void searchItemWasSelected(TextLookupControl tlc);
	
	public void searchQueryWasEntered(TextLookupControl tlc);
	
	public void resultWasChosen(TextLookupControl tlc);
	
	public void awaitsBaggingOfItem(TextLookupControl tlc);
	
	public void checkoutHasBeenUpdated(TextLookupControl tlc);
}
