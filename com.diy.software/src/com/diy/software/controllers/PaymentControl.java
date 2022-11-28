package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import com.diy.software.enums.PaymentType;
import com.diy.software.listeners.PaymentControlListener;

public class PaymentControl implements ActionListener {
	private StationControl sc;
	private ArrayList<PaymentControlListener> listeners;
	public PaymentControl (StationControl sc) {
		this.sc = sc;
		this.listeners = new ArrayList<>();
	}
	
	public void addListener(PaymentControlListener l) {
		listeners.add(l);
	}
	
	public void removeListener(PaymentControlListener l) {
		listeners.remove(l);
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		switch (c) {
			case "giftCard":
				startPaymentProcess(PaymentType.GiftCard);
				break;
			case "cash":
				startPaymentProcess(PaymentType.Cash);
				break;
			case "credit":
				startPaymentProcess(PaymentType.Credit);
				break;
			case "debit":
				startPaymentProcess(PaymentType.Debit);
				break;
			default:
				sc.goBackOnUI();
				break;

		}

	}

	/**
	 * Author: yes
	 * 
	 * STREAMLINED LEZZZ GOOOOOOOO!
	 * @param type
	 */
	public void startPaymentProcess(PaymentType type) {
		for (PaymentControlListener l : listeners)
			l.paymentMethodSelected(this, type);
	}

}
