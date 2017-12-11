package com.bt.nextgen.service.integration.corporateaction;

/**
 * Corporate action notification status for ROA
 */

public enum CorporateActionNotificationStatus {
	GENERATING("generating"),
	READY("ready"),
	SENT("sent");


	private String id;


	CorporateActionNotificationStatus(String id) {
		this.id = id;
	}

	/**
	 * Converts to corporate action election status for a given Avaloq internal ID
	 *
	 * @param id Avaloq internal ID
	 * @return CorporateActionNotificationStatus enum, null if no matching ID
	 */

	public static CorporateActionNotificationStatus forId(String id) {
		for (CorporateActionNotificationStatus type : CorporateActionNotificationStatus.values()) {
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