package com.bt.nextgen.service.integration.corporateaction;

public enum CorporateActionCascadeOrderType {
	LOT_DEBLOCK_POSITIONS("5"),
	PO_LOT_DEBLOCK_POSITIONS("19"),
	CLASSIC_DEBLOCK_POSITIONS("107"),
	PO_CLASSIC_DEBLOCK_POSITIONS("163");

	private String id;

	CorporateActionCascadeOrderType(String id) {
		this.id = id;
	}

	/**
	 * Converts Avaloq internal ID to CorporateActionType
	 *
	 * @param id Avaloq internal ID
	 * @return CorporateActionType
	 */
	public static CorporateActionCascadeOrderType forId(String id) {
		for (CorporateActionCascadeOrderType caType : CorporateActionCascadeOrderType.values()) {
			if (caType.id.equals(id)) {
				return caType;
			}
		}

		return null;
	}

	/**
	 * Avaloq internal ID
	 *
	 * @return avalog internal ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * Name of this enum.  Equivalent to this.name()
	 *
	 * @return name of this enum
	 */
	public String getCode() {
		return this.name();
	}
}
