package com.diy.software.listeners;

import com.diy.software.controllers.TextLookupControl;

public interface TextLookupControlListener {
	
	public void searchQueryWasEntered(TextLookupControl tlc);
	
	public void resultsWereFound(TextLookupControl tlc);
	
	public void noResultsWereFound(TextLookupControl tlc);
	
	public void resultWasChosen(TextLookupControl tlc);
	
	public void itemHasBeenAddedToCheckout(TextLookupControl tlc);
	
	public void itemHasBeenBagged(TextLookupControl tlc);
	
	public void searchHasBeenCleared(TextLookupControl tlc);
}
