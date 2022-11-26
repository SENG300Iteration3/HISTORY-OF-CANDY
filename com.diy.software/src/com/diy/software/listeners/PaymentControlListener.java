package com.diy.software.listeners;

import com.diy.software.controllers.PaymentControl;
import com.diy.software.enums.PaymentType;

public interface PaymentControlListener {

	public void paymentMethodSelected(PaymentControl pc, PaymentType type);
}