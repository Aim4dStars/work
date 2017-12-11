package com.bt.nextgen.service.integration.corporateaction;

public enum CorporateActionSummaryTemplate {
	PAYMENT("{asset_code} {ca_type} {price_amount}"),
	DIV_CASH_RIGHT("{asset_code} {ca_type} {rpp_amount}"),
	LIQUIDATION("{asset_code} {ca_type} {pay_amount}"),
	FINAL_LIQUIDATION("{asset_code} {ca_type} {price_amount}"),
	CAPITAL_CALL_SHARE("{asset_code} {ca_type} {pf_amount} {new_asset_code}"),
	NAME_CHANGE("{asset_code} {ca_type} {new_asset_code}"),
	EXPIRE("{asset_code} {ca_type} 'expired at book cost $0'"),
	DISTRIBUTION("{asset_code} {ca_type} {old} {new_asset_code} {new}"),
	SPLIT_WITH_FRACTION("{asset_code} {ca_type} {old} now {new}"),
	DENOMINATION("{asset_code} Return of Capital {qty}"),
	SECURITY_EXCHANGE("{asset_code} {sec_type} {old} {new_asset_code} {new}"),
	SECURITY_EXCHANGE_WITH_PAYMENT_AND_FRACTION("{asset_code} {old} {new_asset_code} {new}"),
	MERGER_CASH("{asset_code} {ca_type} {price_amount}"),
	MERGER_WITH_PAYMENT_AND_FRACTION("{asset_code} {old} is now {new_asset_code} {new} and {pay_amount} per unit"),
	REDEMPTION_AT_MATURITY("{asset_code} redeemed for {rdmt_amount}"),
	REDEMPTION("{asset_code} redeemed for {price_amount}"),
	BUY_BACK("{asset_code}, Off Market Buy Back, {final_buy_back_price}");

	private String template;

	CorporateActionSummaryTemplate(String template) {
		this.template = template;
	}

	public String getTemplate() {
		return template;
	}
}
