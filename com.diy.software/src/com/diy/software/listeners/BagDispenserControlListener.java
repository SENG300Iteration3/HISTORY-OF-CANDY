package com.diy.software.listeners;

import com.diy.software.controllers.BagDispenserControl;

public interface BagDispenserControlListener {

	public void numberFieldHasBeenUpdated(BagDispenserControl bdp, String memberNumber);
}
