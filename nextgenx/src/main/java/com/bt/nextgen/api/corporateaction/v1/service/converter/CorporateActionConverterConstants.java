package com.bt.nextgen.api.corporateaction.v1.service.converter;

import java.math.BigDecimal;


public final class CorporateActionConverterConstants {
	public static final int MAX_OPTIONS = 7;
	public static final int MAX_SHARE_PURCHASE_PLAN_OPTIONS = 15;
	public static final int MAX_BUY_BACK_OPTIONS = 15;

	public static final int OPTION_TAKE_NO_ACTION_ID = 1000;
	public static final int OPTION_FINAL_TENDER_PRICE_ID = 1001;

	public static final String OPTION_VALUE_YES = "Y";
	public static final String OPTION_VALUE_NO = "N";

	public static final String OPTION_TAKE_NO_ACTION_VALUE = "NOAC";
	public static final String OPTION_PREFIX ="OPT";

	public static final int NONE_MINIMUM_PRICE_ID = 1;

	public static final BigDecimal DECIMAL_ONE_HUNDRED = new BigDecimal(100);
	public static final BigDecimal DECIMAL_NEGATIVE_ONE = new BigDecimal(-1);

	private CorporateActionConverterConstants() {
	}
}
