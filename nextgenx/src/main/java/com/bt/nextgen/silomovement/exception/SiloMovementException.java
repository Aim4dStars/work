package com.bt.nextgen.silomovement.exception;

public class SiloMovementException extends Exception {

	private final String errorState;

	public SiloMovementException(String errorState, String errorMsg) {
		super(errorMsg);
		this.errorState = errorState;
	}

	public String getErrorState() {
		return errorState;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
