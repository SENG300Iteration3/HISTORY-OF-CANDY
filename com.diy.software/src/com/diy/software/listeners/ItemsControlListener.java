package com.diy.software.listeners;

import com.diy.software.controllers.ItemsControl;
import com.diy.software.controllers.StationControl;

public interface ItemsControlListener {
	
	public void awaitingItemToBeSelected(ItemsControl ic);
	
	public void itemWasSelected(ItemsControl ic);
	
	public void awaitingItemToBePlacedInBaggingArea(ItemsControl ic);
	
	public void awaitingItemToBePlacedInScanningArea(StationControl sc);
	
	public void noMoreItemsAvailableInCart(ItemsControl ic);
	
	public void itemsAreAvailableInCart(ItemsControl ic);

	public void awaitingItemToBeRemoved(ItemsControl itemsControl, String updateMessage);
	
	public void itemsHaveBeenUpdated(ItemsControl ic);
	
	public void productSubtotalUpdated(ItemsControl ic);
	
	public void awaitingAttendantToApproveItemRemoval(ItemsControl ic);
	

}
