package com.bt.nextgen.service.integration.corporateaction;

public class CorporateActionValidationError {
	private final String positionId;
	private final String errorCode;
	private final String errorMessage;

	public CorporateActionValidationError(String positionId, String errorCode, String errorMessage) {
		this.positionId = positionId;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getPositionId() {
		return positionId;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
