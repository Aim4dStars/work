package com.bt.nextgen.service.integration.corporateaction;

public enum CorporateActionGroup {
	VOLUNTARY("1", "voluntary"),
	MANDATORY("2", "mandatory");

	private String id;
	private String code;

	CorporateActionGroup(String id, String code) {
		this.id = id;
		this.code = code;
	}

	public static CorporateActionGroup forCode(String code) {
		for (CorporateActionGroup group : CorporateActionGroup.values()) {
			if (group.code.equals(code)) {
				return group;
			}
		}

		return null;
	}

	public static CorporateActionGroup forId(String id) {
		for (CorporateActionGroup group : CorporateActionGroup.values()) {
			if (group.id.equals(id)) {
				return group;
			}
		}

		return null;
	}

	public String getId() {
		return id;
	}

	public String getCode() {
		return code;
	}
}
