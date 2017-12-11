package com.bt.nextgen.service.integration.corporateaction;

/**
 * Corporate action election status - to be finalised
 */

public enum CorporateActionAccountParticipationStatus {
	SUBMITTED ("clt_conf"),
	NOT_SUBMITTED ("unconf");

	private String id;
	

	CorporateActionAccountParticipationStatus(String id) {
		this.id = id;
	}

	/**
	 * Converts to corporate action election status for a given Avaloq internal ID
	 *
	 * @param id Avaloq internal ID
	 * @return CorporateActionAccountParticipationStatus enum, null if no matching ID
	 */
	
	public static CorporateActionAccountParticipationStatus forId(String id) {
		for (CorporateActionAccountParticipationStatus type : CorporateActionAccountParticipationStatus.values()) {
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
	public String getCode()	{
		return this.name();
	}	

}