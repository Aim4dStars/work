package com.bt.nextgen.service.integration.corporateaction;

public enum CorporateActionDecisionKey {
	QUANTITY("qty"),
	PERCENT("perc"),
	BLOCK_ON_DECISION("do_block_on_appl_decsn"),
	SUBSCRIBED_OPTION("subscr_opt"),
	EXERCISE_RIGHTS_PERCENT("exe"),
	EXERCISE_RIGHTS_ALL("exe_all"),
	EXERCISE_RIGHTS_QUANTITY("exe_qty"),
	EXERCISE_RIGHTS_OVERSUBSCRIBE_QUANTITY("oversubs_qty"),
	EXERCISE_NO_ACTION("no_action"),
	OPTION("opt"),
	OPTION_QUANTITY("opt", "_qty"),
	OPTION_PERCENT("opt", "_perc"),
	MINIMUM_PRICE("min_price"),
	MINIMUM_PRICE_DECISION("min_price_decsn"),
	FINAL_TENDER_PERCENT("perc_final_tender"),
	FINAL_TENDER_QUANTITY("qty_final_tender"),
	SUBSCRIBED_QUANTITY("subscr_qty");

	private String code;
	private String codePostfix;

	CorporateActionDecisionKey(String code) {
		this.code = code;
	}

	CorporateActionDecisionKey(String code, String codePostfix) {
		this.code = code;
		this.codePostfix = codePostfix;
	}

	public static CorporateActionDecisionKey forCode(String code) {
		for (CorporateActionDecisionKey key : CorporateActionDecisionKey.values()) {
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
