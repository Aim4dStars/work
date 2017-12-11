package com.bt.nextgen.service.integration.corporateaction;

/**
 * Corporate action response status for ROA
 */

public enum CorporateActionResponseStatus {
	APPROVED("approved"),
	PENDING("pending"),
	DECLINED("declined");


	private String id;


	CorporateActionResponseStatus(String id) {
		this.id = id;
	}

	/**
	 * Converts to corporate action election status for a given Avaloq internal ID
	 *
	 * @param id Avaloq internal ID
	 * @return CorporateActionResponseStatus enum, null if no matching ID
	 */

	public static CorporateActionResponseStatus forId(String id) {
		for (CorporateActionResponseStatus type : CorporateActionResponseStatus.values()) {
			if (type.id.equals(id)) {
				return type;
			}
		}
		return null;
	}

	/**
	 * The avaloq internal ID
	 *
	 * @return Avaloq internal ID code
	 */

	public String getId() {
		return id;
	}

	/**
	 * The name of the enum
	 *
	 * @return name of this enum.  Equiv to this.name()
	 */
	public String getCode() {
		return this.name();
	}

}