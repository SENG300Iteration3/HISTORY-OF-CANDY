package com.diy.software.listeners;

import com.diy.software.controllers.WalletControl;

public interface WalletControlListener {
	
	public void cardHasBeenSelected(WalletControl wc);
	
	public void membershipCardHasBeenSelected(WalletControl wc);
	
	public void membershipCardInputEnabled(WalletControl wc);
	
	public void cardPaymentsEnabled(WalletControl wc);
	
	public void cardPaymentsDisabled(WalletControl wc);
	
	public void cardHasBeenInserted(WalletControl wc);
	
	public void cardWithPinInserted(WalletControl wc);
	
	public void cardWithPinRemoved(WalletControl wc);

	public void membershipCardInputCanceled(WalletControl walletControl);

}
