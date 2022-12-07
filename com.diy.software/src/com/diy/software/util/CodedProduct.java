package com.diy.software.util;

import com.diy.hardware.BarcodedProduct;
import com.diy.hardware.PLUCodedProduct;

public class CodedProduct {
	BarcodedProduct bar;
	PLUCodedProduct plu;

	public CodedProduct(BarcodedProduct bar, PLUCodedProduct plu) {
		this.bar = bar;
		this.plu = plu;
	}
	
	public BarcodedProduct getBarcodedProduct() {
		return bar;
	}
	
	public PLUCodedProduct getPLUCodedProduct() {
		return plu;
	}
	
	public String toString() {
		if (bar != null) {
			return bar.getDescription();
		} else {
			return plu.getDescription();
		}
	}
	
	public long getPrice() {
		if (bar != null) {
			return bar.getPrice();
		} else {
			return plu.getPrice();
		}
	}
}
