package com.bt.nextgen.service.integration.corporateaction;

/**
 * Corporate action Transaction status
 */

public enum CorporateActionTransactionStatus {
	POST_EX_DATE("post ex-date"),
	PRE_EX_DATE("pre ex-date");


	private String id;


	CorporateActionTransactionStatus(String id) {
		this.id = id;
	}

	/**
	 * Converts to corporate action election status for a given Avaloq internal ID
	 *
	 * @param id Avaloq internal ID
	 * @return CorporateActionTransactionStatus enum, null if no matching ID
	 */

	public static CorporateActionTransactionStatus forId(String id) {
		for (CorporateActionTransactionStatus type : CorporateActionTransactionStatus.values()) {
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