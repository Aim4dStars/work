package com.bt.nextgen.service.integration.cashcategorisation.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bt.nextgen.service.integration.transactionhistory.BTOrderType;

/**
 * SMSF Contribution sub  types.<p>
 * Enumeration of avaloq static code category <code>code_pay_sub_cat</code>.
 */
//TODO: Need to split this up between payment and deposit enums
public enum CashCategorisationSubtype
{
	EMPLOYER("empl", "Employer", ContributionClassification.CONCESSIONAL, BTOrderType.DEPOSIT, 1),
	PERSONAL_NON_CONCESSIONAL("prsnl_nconc", "Personal - non concessional", ContributionClassification.NON_CONCESSIONAL, BTOrderType.DEPOSIT, 3),
	PERSONAL_CONCESSIONAL("prsnl_conc", "Personal - concessional", ContributionClassification.CONCESSIONAL, BTOrderType.DEPOSIT, 2),
	SPOUSE_CHILD_CONTRIBUTION("spouse_chld_contri", "Spouse or child contribution", ContributionClassification.NON_CONCESSIONAL, BTOrderType.DEPOSIT, 4),
	OTHER_THIRD_PARTY("oth_thp", "Other 3rd Party", ContributionClassification.CONCESSIONAL, BTOrderType.DEPOSIT, 5),
	GOVT_CO_CONTRIBUTION("gov_low_income_contri", "Govt. co-contributions & low income", ContributionClassification.OTHER, BTOrderType.DEPOSIT, 6),
	GCT_SMALL_BUS_15YEAR_EXEMPTION("cgt_small_busi_15year_exmpt", "Small business CGT - 15 year exemption", ContributionClassification.OTHER, BTOrderType.DEPOSIT, 7),
	GCT_SMALL_BUS_RETIREMENT_EXEMPTION("cgt_small_busi_retir_exmpt", "Small business CGT – retirement", ContributionClassification.OTHER, BTOrderType.DEPOSIT, 8),
	PERSONAL_INJURY_ELECTION("prsnl_injury_elect", "Personal injury election", ContributionClassification.OTHER, BTOrderType.DEPOSIT, 9),
	FOREIGN_SUPER_ASSESSABLE("frn_super_assble", "Foreign super fund - assessable", ContributionClassification.CONCESSIONAL, BTOrderType.DEPOSIT, 10),
	FOREIGN_SUPER_NON_ASSESSABLE("frn_super_non_assble", "Foreign super fund - non assessable", ContributionClassification.NON_CONCESSIONAL, BTOrderType.DEPOSIT, 11),
	ASIC("asic", "Asic", ContributionClassification.OTHER , BTOrderType.PAYMENT, 39),
	ACCOUNTING_FEE("acc", "Accounting fee", ContributionClassification.OTHER, BTOrderType.PAYMENT, 12),
	ACTUARY_FEE("actuary", "Actuary fee", ContributionClassification.OTHER, BTOrderType.PAYMENT, 13),
	ADMIN_FEE("admin", "Admin fee", ContributionClassification.OTHER, BTOrderType.PAYMENT, 14),
	AGENTS_FEE("agent", "Agents fee", ContributionClassification.OTHER, BTOrderType.PAYMENT, 28),
	AUDIT_FEE("audit", "Audit fee", ContributionClassification.OTHER, BTOrderType.PAYMENT, 16),
	BAS("bas", "BAS / IAS", ContributionClassification.OTHER, BTOrderType.PAYMENT, 40),
	FIXED_INTEREST("fixed_intr", "Fixed interest", ContributionClassification.OTHER, BTOrderType.PAYMENT, 20),
	ESTABLISHMENT_FEE("estab", "Establishment fee", ContributionClassification.OTHER, BTOrderType.PAYMENT, 17),
	INCOME_TAX("income_tax", "Income tax", ContributionClassification.OTHER, BTOrderType.PAYMENT, 41),
	INSURANCE("insur", "Insurance", ContributionClassification.OTHER, BTOrderType.PAYMENT, 25),
	PRTY_INSURANCE("prty_insur", "Insurance", ContributionClassification.OTHER, BTOrderType.PAYMENT, 30),
	LEGAL_AND_DISBURSEMENT("disbrs", "Legal & Disbursement", ContributionClassification.OTHER, BTOrderType.PAYMENT, 31),
	LEGAL_FEE("legal", "Legal fee", ContributionClassification.OTHER, BTOrderType.PAYMENT, 18),
	LOAN_REPAYMENT("loan_pay", "Loan repayment", ContributionClassification.OTHER, BTOrderType.PAYMENT, 32),
	LUMP_SUM("lump_sum", "Lump Sum", ContributionClassification.OTHER, BTOrderType.PAYMENT, 26),
	MAINTENANCE("maint", "Maintenance", ContributionClassification.OTHER, BTOrderType.PAYMENT, 33),
	MANAGED_INVESTMENT("mngd_invst", "Managed investment", ContributionClassification.OTHER, BTOrderType.PAYMENT, 21),
	PURCH_OTHER("purch_other", "Other", ContributionClassification.OTHER, BTOrderType.PAYMENT, 24),
	OTHER_FEE("other_fee", "Other fee", ContributionClassification.OTHER, BTOrderType.PAYMENT, 19),
	PRTY_OTHER("prty_other", "Other", ContributionClassification.OTHER, BTOrderType.PAYMENT, 38),
	REGLTRY_OTHER("regltry_oth", "Other", ContributionClassification.OTHER, BTOrderType.PAYMENT, 42),
	PENSION("pension", "Pension", ContributionClassification.OTHER, BTOrderType.PAYMENT, 27),
	PROPERTY("prty", "Property", ContributionClassification.OTHER, BTOrderType.PAYMENT, 22),
	RATES("rates", "Rates", ContributionClassification.OTHER, BTOrderType.PAYMENT, 34),
	CAPITAL_WORKS("cap_work", "Capital works", ContributionClassification.OTHER, BTOrderType.PAYMENT, 29),
	SHARES("shares", "Shares", ContributionClassification.OTHER, BTOrderType.PAYMENT, 23),
	STAMP_DUTY("st_duty", "Stamp Duty", ContributionClassification.OTHER, BTOrderType.PAYMENT, 35),
	STRATA_BODY_CORP("strata", "Strata / Body Corporate", ContributionClassification.OTHER, BTOrderType.PAYMENT, 36),
	UTILITIES("util", "Utilities", ContributionClassification.OTHER, BTOrderType.PAYMENT, 37),
	ADVISER_FEE("avsr", "Adviser fee", ContributionClassification.OTHER, BTOrderType.PAYMENT, 15),

	ATO_RFD("ato_rfd","ATO refund",ContributionClassification.OTHER, BTOrderType.DEPOSIT, 58),
	DISTR("distr","Distributions",ContributionClassification.OTHER, BTOrderType.DEPOSIT, 50),
	DIV("div","Dividend",ContributionClassification.OTHER, BTOrderType.DEPOSIT,51),
	REFUND("refund","Fee refund / rebate",ContributionClassification.OTHER, BTOrderType.DEPOSIT,52),
	SALE_FIXED_INTR("sale_fixed_intr","Fixed interest",ContributionClassification.OTHER, BTOrderType.DEPOSIT,43),
	INTR("intr","Interest",ContributionClassification.OTHER, BTOrderType.DEPOSIT,54),
	SALE_MNGD_INVST("sale_mngd_invst","Managed fund / portfolio",ContributionClassification.OTHER, BTOrderType.DEPOSIT,44),
	REGLTRY_OTH_INCOME("regltry_oth_income","Other",ContributionClassification.OTHER, BTOrderType.DEPOSIT,59),
	SALE_OTHER("sale_other","Other",ContributionClassification.OTHER, BTOrderType.DEPOSIT,47),
	PROCEEDS("proceeds","Proceeds",ContributionClassification.OTHER, BTOrderType.DEPOSIT,48),
	SALE_PRTY("sale_prty","Property",ContributionClassification.OTHER, BTOrderType.DEPOSIT,45),
	REBATE("rebate","Rebate",ContributionClassification.OTHER, BTOrderType.DEPOSIT,49),
	RENT("rent","Rent",ContributionClassification.OTHER, BTOrderType.DEPOSIT,56),
	SALE_SHARES("sale_shares","Shares",ContributionClassification.OTHER, BTOrderType.DEPOSIT,46),
	FRN_INCOME("frn_income","Fee refund / rebate",ContributionClassification.OTHER, BTOrderType.DEPOSIT,53),
	PRTY_OTH("prty_oth","Other",ContributionClassification.OTHER, BTOrderType.DEPOSIT,57),
	OTH_OTH("oth_oth","Other",ContributionClassification.OTHER, BTOrderType.DEPOSIT,55),
	RLOV("rlov","Rollover / Transfer In",ContributionClassification.OTHER, BTOrderType.DEPOSIT,60);

	CashCategorisationSubtype(String avaloqInternalId, String name, ContributionClassification classification, BTOrderType orderType, int order)
	{
		this.avaloqInternalId = avaloqInternalId;
		this.name = name;
		this.order = order;
		this.classification = classification;
		this.orderType = orderType;
	}

	private String avaloqInternalId;

	private String name;

	private int order;

	private BTOrderType orderType;

	private ContributionClassification classification;

	private static Map<String, CashCategorisationSubtype> lookup = new HashMap<>();

	private static Map<String, Integer> sortOrderLookup = new HashMap<>();

	static
	{
		for (CashCategorisationSubtype subtype : CashCategorisationSubtype.values())
		{
			lookup.put(subtype.getAvaloqInternalId(), subtype);
			sortOrderLookup.put(subtype.getAvaloqInternalId(), subtype.getOrder());
		}
	}


	public String getAvaloqInternalId() {
		return avaloqInternalId;
	}

	public ContributionClassification getClassification()
	{
		return classification;
	}

	public static CashCategorisationSubtype getByAvaloqInternalId(String internalId)
	{
		return lookup.get(internalId);
	}

	public static Map<String, Integer> getSortOrderLookup() { return sortOrderLookup; }

	public int getOrder() {return order;}

	public String toString()
	{
		return getAvaloqInternalId();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BTOrderType getOrderType() {
		return orderType;
	}

	/**
	 * Return all sub-categories for a specific transaction order type: deposit, payment
	 *
	 * @param orderType order type to retrieve sub-categories for
	 * @return ordered list of sub-categories (ordering based on defined order)
	 */
	public static List<CashCategorisationSubtype> getSortedCashCategorisationSubtypeListForOrderType(BTOrderType orderType)
	{
		List<CashCategorisationSubtype> subtypeList = new ArrayList<>();

		for (CashCategorisationSubtype subtype : lookup.values())
		{
			if (subtype.orderType == orderType)
			{
				subtypeList.add(subtype);
			}
		}

		Collections.sort(subtypeList, new Comparator<CashCategorisationSubtype>()
		{
			public int compare(CashCategorisationSubtype subtype1, CashCategorisationSubtype subtype2)
			{
				return Integer.valueOf(subtype1.getOrder()).compareTo(Integer.valueOf(subtype2.getOrder()));
			}
		});

		return subtypeList;
	}
}
