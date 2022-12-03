package com.diy.software.listeners;

import com.diy.software.controllers.ItemsControl;

public interface ItemsControlListener {
	
	public void awaitingItemToBeSelected(ItemsControl ic);
	
	public void barcodedItemWasSelected(ItemsControl ic);
	
	public void plucodedItemWasSelected(ItemsControl ic);
	
	public void awaitingItemToBePlacedInBaggingArea(ItemsControl ic);
	
	public void awaitingItemToBePlacedInScanningArea(ItemsControl itemsControl);
	
	public void noMoreItemsAvailableInCart(ItemsControl ic);
	
	public void itemsAreAvailableInCart(ItemsControl ic);

	public void awaitingItemToBeRemoved(ItemsControl itemsControl, String updateMessage);
	
	public void itemsHaveBeenUpdated(ItemsControl ic);
	
	public void productSubtotalUpdated(ItemsControl ic);
}
