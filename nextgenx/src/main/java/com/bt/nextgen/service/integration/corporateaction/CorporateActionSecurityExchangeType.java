package com.bt.nextgen.service.integration.corporateaction;

/**
 * Corporate action status - to be finalised
 */
public enum CorporateActionSecurityExchangeType {
	DE_STAPLE("destpl", "De-Staple (Stapled security)", CorporateActionType.SECURITY_EXCHANGE_DESTAPLE),
	CONVERSION("cnv_noreg_reg", "Conversion", CorporateActionType.SECURITY_EXCHANGE_CONVERSION),
	SECURITY_EXCHANGE("secxchg", "Security exchange", CorporateActionType.SECURITY_EXCHANGE_EXCHANGE),
	STAPLE("stpl", "Staple (Stapled security)", CorporateActionType.SECURITY_EXCHANGE_STAPLE),
	REINVESTMENT("revst", "Reinvestment", CorporateActionType.SECURITY_EXCHANGE_REINVESTMENT);

	private String id;
	private String description;
	private CorporateActionType corporateActionType;

	CorporateActionSecurityExchangeType(String id, String description, CorporateActionType corporateActionType) {
		this.id = id;
		this.description = description;
		this.corporateActionType = corporateActionType;
	}

	/**
	 * Converts to corporate action status for a given Avaloq internal ID
	 *
	 * @param id Avaloq internal ID
	 * @return CorporateActionStatus enum, null if no matching ID
	 */
	public static CorporateActionSecurityExchangeType forId(String id) {
		for (CorporateActionSecurityExchangeType exchangeType : CorporateActionSecurityExchangeType.values()) {
			if (exchangeType.id.equals(id)) {
				return exchangeType;
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

	public String getDescription() {
		return description;
	}

	public CorporateActionType getCorporateActionType() {
		return corporateActionType;
	}
}

