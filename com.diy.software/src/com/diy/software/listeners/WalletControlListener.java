package com.diy.software.listeners;

import com.diy.software.controllers.WalletControl;

public interface WalletControlListener {
	
	public void cardHasBeenSelected(WalletControl wc);
	
	public void cardPaymentsEnabled(WalletControl wc);
	
	public void cardPaymentsDisabled(WalletControl wc);
	
	public void cardHasBeenInserted(WalletControl wc);
	
	public void cardWithPinInserted(WalletControl wc);
	
	public void cardWithPinRemoved(WalletControl wc);

}
