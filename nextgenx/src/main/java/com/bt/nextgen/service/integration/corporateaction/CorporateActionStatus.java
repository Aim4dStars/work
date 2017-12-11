package com.bt.nextgen.service.integration.corporateaction;

/**
 * Corporate action status - to be finalised
 */
public enum CorporateActionStatus {
	CANCEL("cancel"),
	CASE_COMPLETED("case_compl"),
	CLAIM_RECEIVED("claim_rcvd"),
	CLOSED("closed"),
	COMPLETED("completed"),
	IN_PROGRESS("in_progress"),
	OPEN("open"),
	PENDING("pending"),
	QUEUED("queue"),
	REQUIREMENTS_OUTSTANDING("requ_outsta"),
	SUBMITTED("subm"),
	TRADED("traded"),
	UNDER_ASSESSMENT("undr_assmt"),
	UNDER_PAYMENT("undr_pay"),
	UNDER_REVIEW("undr_review");

	private String id;

	CorporateActionStatus(String id) {
		this.id = id;
	}

	/**
	 * Converts to corporate action status for a given Avaloq internal ID
	 *
	 * @param id Avaloq internal ID
	 * @return CorporateActionStatus enum, null if no matching ID
	 */
	public static CorporateActionStatus forId(String id) {
		for (CorporateActionStatus status : CorporateActionStatus.values()) {
			if (status.id.equals(id)) {
				return status;
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

