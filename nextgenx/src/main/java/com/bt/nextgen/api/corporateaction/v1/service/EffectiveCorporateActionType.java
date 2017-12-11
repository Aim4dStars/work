package com.bt.nextgen.api.corporateaction.v1.service;

import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;

public class EffectiveCorporateActionType {
	private final CorporateActionType type;
	private final String code;
	private final String description;

	public EffectiveCorporateActionType(CorporateActionType type, String code, String description) {
		this.type = type;
		this.code = code;
		this.description = description;
	}

	public CorporateActionType getType() {
		return type;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}
}
