package com.jimmyselectronics.opeechee;

import java.io.IOException;

/**
 * Represents exceptions arising from failures of the magnetic stripe.
 * 
 * @author Jimmy's Electronics LLP
 */
public class MagneticStripeFailureException extends IOException {
	private static final long serialVersionUID = -4703845851722394414L;

	/**
	 * Create an exception.
	 */
	public MagneticStripeFailureException() {}
}
