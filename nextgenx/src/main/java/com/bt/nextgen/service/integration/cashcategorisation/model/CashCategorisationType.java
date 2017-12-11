package com.bt.nextgen.service.integration.cashcategorisation.model;


import com.bt.nextgen.service.integration.transactionhistory.BTOrderType;

import java.util.HashMap;
import java.util.Map;

public enum CashCategorisationType
{
	CONTRIBUTION("contri", "Contribution", "contribution", BTOrderType.DEPOSIT),
	ROLLOVER("rlov", "Rollover", "rlov", BTOrderType.DEPOSIT),
	ADMINISTRATION("admin", "Admin expense", "admin", BTOrderType.PAYMENT),
	ASSET_PURCHASE("purch", "Asset purchase", "purch", BTOrderType.PAYMENT),
	BENEFIT_PAYMENT("benf_payment", "Benefit Payment", "benf_payment", BTOrderType.PAYMENT),
	INSURANCE("insur", "Insurance", "insur", BTOrderType.PAYMENT),
	PENSION("pension", "Pension", "pension", BTOrderType.PAYMENT),
	PROPERTY("prty", "Property expense", "prty", BTOrderType.PAYMENT),
	REGULATORY("regltry", "Tax/Regulatory", "regltry", BTOrderType.PAYMENT),
	LUMP_SUM("lump_sum", "Lump sum", "lump_sum", BTOrderType.PAYMENT),
	SALE("sale", "Asset sale", "sale", BTOrderType.DEPOSIT),
	INSUR_INCOME("insur_income","Insurance","insur_income",BTOrderType.DEPOSIT),
	OTH_INCOME("oth_income","Other Income","oth_income",BTOrderType.DEPOSIT),
	PRTY_INCOME("prty_income","Property Income","prty_income",BTOrderType.DEPOSIT),
	REGULTRY_INCOME("regltry_income", "Tax/Regulatory", "regltry_income", BTOrderType.DEPOSIT);

	CashCategorisationType(String internalId, String name, String displayCode, BTOrderType orderType)
	{
		this.value = internalId;
		this.name = name;
		this.displayCode = displayCode;
		this.orderType = orderType;
	}

	private String value;
	private String name;
	private String displayCode;
	private BTOrderType orderType;
	private static Map<String, CashCategorisationType> lookup = new HashMap<>();


	static
	{
		for (CashCategorisationType cashCatType : CashCategorisationType.values())
		{
			lookup.put(cashCatType.getValue(), cashCatType);
		}
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString()
	{
		return value;
	}

	public String getDisplayCode() {
		return displayCode;
	}

	public void setDisplayCode(String displayCode) {
		this.displayCode = displayCode;
	}

	public static CashCategorisationType getByAvaloqInternalId(String internalId)
	{
		return lookup.get(internalId);
	}

	public BTOrderType getOrderType()
	{
		return orderType;
	}


}
