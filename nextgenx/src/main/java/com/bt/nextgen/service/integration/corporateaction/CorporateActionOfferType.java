package com.bt.nextgen.service.integration.corporateaction;

/**
 * Corporate action offer types - to be finalised
 */
public enum CorporateActionOfferType {
	CAPITAL_CALL("captcall", "Capital Call"),
	MERGER("merger", "Merger"),
	PUBLIC_OFFER("po", "Public Offer"),
	REVENUE("revn", "Revenue"),
	REINVEST("revst", "Reinvestment Offer"),
	CONVERSION("conv", "Conversion Offer"),
	EXCHANGE("xchng", "Exchange Offer");

	private String id;
	private String description;

	CorporateActionOfferType(String id , String description) {
		this.id = id;
		this.description = description;
	}

	/**
	 * Returns the corporate action offer type object for a given string ID
	 *
	 * @param id string ID
	 * @return corporate action offer type enum.  Null if no matching ID
	 */
	public static CorporateActionOfferType forId(String id) {
		for (CorporateActionOfferType type : CorporateActionOfferType.values()) {
			if (type.id.equals(id)) {
				return type;
			}
		}

		return null;
	}

	/**
	 * The Avaloq internal ID
	 *
	 * @return avaloq internal ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * Equivalent to the enum name
	 *
	 * @return name of the enum key
	 */
	public String getCode() {
		return this.name();
	}
	 /**
         * @return the description
         */
        public String getDescription() {
            return description;
        }
}
