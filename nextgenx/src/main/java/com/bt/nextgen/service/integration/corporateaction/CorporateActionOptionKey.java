package com.bt.nextgen.service.integration.corporateaction;

public enum CorporateActionOptionKey {
	TITLE("title"),
	OFFERED_PRICE("pay"),
	OLD_STOCK_HELD("old"),
	NEW_STOCK_ALLOCATED("new"),
	ASSET_ID("asset"),
	SUBSCRIPTION_AMOUNT("subscr_pay"),
	SUBSCRIPTION_QUANTITY("subscr_qty"),
	NEW_ASSET_ID("new_asset_code"),
	PRICE("price"),
	SECURITY_EXCHANGE("secxchg_type"),
	REVENUE_PER_PRICE("rpp"),
	QUANTITY("qty"),
	PRICE_FACTOR("pf"),
	OVERSUBSCRIBE("over_subscr"),
	STRIKE_PRICE("strike"),
	PRICE_AS_PERCENT("dcsnt_as_perc"),
	FINAL_TENDER_PRICE("final_tender_price"),
	OPTION("opt"),
	OPTION_MINIMUM_PRICE("opt", "_min"),
	REDEMPTION_PRICE("rdmpt"),
	IS_NON_PRO_RATA("is_non_pro_rata"),
	IS_QUANTITY_ALLOWED("is_qty_alw"),
	MINIMUM_QUANTITY("min_qty"),
	MAXIMUM_QUANTITY("max_qty"),
	STEP("step"),
	MAX_OVERSUBSCRIBE_QUANTITY("max_oversubs_qty"),
	MAX_OVERSUBSCRIBE_PERCENT("max_oversubs_perc"),
	FINAL_BUY_BACK_PRICE("final_buyback_price");

	private String code;
	private String codePostfix;

	CorporateActionOptionKey(String code) {
		this.code = code;
	}

	CorporateActionOptionKey(String code, String codePostfix) {
		this.code = code;
		this.codePostfix = codePostfix;
	}

	public static CorporateActionOptionKey forCode(String code) {
		for (CorporateActionOptionKey key : CorporateActionOptionKey.values()) {
			if (key.getCode().equals(code)) {
				return key;
			}
		}

		return null;
	}

	public String getCode() {
		return code;
	}

	public String getCode(int idx) {
		return codePostfix == null ? code + idx : code + idx + codePostfix;
	}
}
