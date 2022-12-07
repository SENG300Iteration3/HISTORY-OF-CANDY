package com.jimmyselectronics.opeechee;

import java.io.IOException;

/**
 * Represents exceptions arising from entry of an invalid PIN.
 * 
 * @author Jimmy's Electronics LLP
 */
public class InvalidPINException extends IOException {
	private static final long serialVersionUID = 5461848339919309513L;

	/**
	 * Create an exception.
	 */
	public InvalidPINException() {}
}
