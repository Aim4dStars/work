package com.bt.nextgen.api.notification.model;

import com.btfin.panorama.core.security.integration.messages.NotificationSubCategory;
import org.apache.commons.lang3.StringUtils;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum NotificationSubCategoryConverter
{
	CLIENT_CHANGE_REQUESTED(NotificationSubCategory.CLIENT_CHANGE_REQUESTED, "Client change requested"),
	CLIENT_NON_RESPONSE(NotificationSubCategory.CLIENT_NON_RESPONSE, "Client non response"),
	DEPOSIT_CLEARED(NotificationSubCategory.DEPOSIT_CLEARED, "Deposit cleared"),
	INVESTMENT_PROCEEDS_DEPOSITED(NotificationSubCategory.INVESTMENT_PROCEEDS_DEPOSITED, "Investment proceeds deposited"),
	TD_PROCEEDS_DEPOSITED(NotificationSubCategory.TD_PROCEEDS_DEPOSITED, "TD proceeds deposited"),
	FAILED_DEPOSIT(NotificationSubCategory.FAILED_DEPOSIT, "Failed deposit"),
	FAILED_PAYMENTS_AND_WITHDRAWALS(NotificationSubCategory.FAILED_PAYMENTS_AND_WITHDRAWALS, "Failed payments and withdrawals"),
	FAILED_SCHEDULED_PAYMENT(NotificationSubCategory.FAILED_SCHEDULED_PAYMENT, "Failed scheduled payment"),
	INSUFFICIENT_CASH(NotificationSubCategory.INSUFFICIENT_CASH, "Insufficient cash"),
	MINIMUM_CASH_BREACH(NotificationSubCategory.MINIMUM_CASH_BREACH, "Minimum cash breach"),
	TRADE_STALLED(NotificationSubCategory.TRADE_STALLED, "Trade alert"),
	INVALID_ASSET_CODE(NotificationSubCategory.INVALID_ASSET_CODE, "Invalid asset code"),
	//Newly added in Version2 changes
	ACCOUNT_OPEN_NEW(NotificationSubCategory.ACCOUNT_OPEN_NEW, "Account open"),
	APPLICATION_CHANGE_REQUESTED(NotificationSubCategory.APPLICATION_CHANGE_REQUESTED, "Application change requested"),
	STATEMENTS(NotificationSubCategory.STATEMENTS, "Statements"),
	CHEQUE_DEPOSIT(NotificationSubCategory.CHEQUE_DEPOSIT, "Cheque deposit"),
	INTEREST_ADJUSTMENT(NotificationSubCategory.INTEREST_ADJUSTMENT, "Interest adjustment"),
	TERM_DEPOSIT_OPEN(NotificationSubCategory.TERM_DEPOSIT_OPEN, "Term deposit opened"),
	IN_SPECIE_TRANSFERS_TRX(NotificationSubCategory.IN_SPECIE_TRANSFERS_TRX, "In specie transfers"),
	TD_MATURING(NotificationSubCategory.TD_MATURING, "TD maturing"),
	FAILED_PAYMENT(NotificationSubCategory.FAILED_PAYMENT, "Failed payment"),
	SYS_FAILURE(NotificationSubCategory.SYS_FAILURE, "System failure"),
	FEES(NotificationSubCategory.FEES, "Fees"),
	IN_SPECIE_TRANSFERS(NotificationSubCategory.IN_SPECIE_TRANSFERS, "In specie transfers"),
	PAYMENT_LIMIT_CHANGE(NotificationSubCategory.PAYMENT_LIMIT_CHANGE, "Payment limit change"),
	CHANGE_PERMISSION(NotificationSubCategory.CHANGE_PERMISSION, "Change permission"),
	ADVISER_CHANGE(NotificationSubCategory.ADVISER_CHANGE, "Adviser change"),
	ACCOUNT_CLOSURE(NotificationSubCategory.ACCOUNT_CLOSURE, "Account closure"),
	CHANGE_OWNERSHIP(NotificationSubCategory.CHANGE_OWNERSHIP, "Change ownership"),
	ACCOUNT_PREFERENCE(NotificationSubCategory.ACCOUNT_PREFERENCE, "Account preferences"),
	ACCOUNT_OPEN(NotificationSubCategory.ACCOUNT_OPEN, "Account open"),
	EMAIL_DELIVERY_FAILURE(NotificationSubCategory.EMAIL_DELIVERY_FAILURE, "Email delivery failure"),
	BILLER_AND_PAYEES(NotificationSubCategory.BILLER_AND_PAYEES, "Billers and payees"),
	CHANGE_DETAILS(NotificationSubCategory.CHANGE_DETAILS, "Change details"),
	CHANGE_ADVISER_DETAILS(NotificationSubCategory.CHANGE_ADVISER_DETAILS, "Change adviser details"),
	INTEREST_RATE_CHANGE(NotificationSubCategory.INTEREST_RATE_CHANGE, "Interest rate change"),
	EXTERNAL_HOLDINGS(NotificationSubCategory.EXTERNAL_HOLDINGS, "External Asset update"),
	CORPORATE_ACTION_REMINDER(NotificationSubCategory.CORPORATE_ACTION_REMINDER, "Corporate action reminder"),
	CORPORATE_ACTION_ANNOUNCEMENTS(NotificationSubCategory.CORPORATE_ACTION_ANNOUNCEMENTS, "Corporate action announcement"),
	ACCOUNTANT_LINK(NotificationSubCategory.ACCOUNTANT_LINK,"Accountant linked"),
	ACCOUNTANT_DELINK(NotificationSubCategory.ACCOUNTANT_DELINK,"Accountant delinked"),
	FUND_ADMINISTRATION_APPL(NotificationSubCategory.FUND_ADMINISTRATION_APPL,"Fund administration application"),
	FUND_ADMINISTRATION_FEES(NotificationSubCategory.FUND_ADMINISTRATION_FEES,"Fund administration fees"),
	REPLACEMENT_TRUST_DEED(NotificationSubCategory.REPLACEMENT_TRUST_DEED,"Replacement trust deed"),
	APPLICATION_COMPLETED(NotificationSubCategory.APPLICATION_COMPLETED,"Application completed");

	private final NotificationSubCategory categoryRaw;
	private final String categoryValue;
	private static final Map <NotificationSubCategory, NotificationSubCategoryConverter> notificationSubCategoryMap = new HashMap <NotificationSubCategory, NotificationSubCategoryConverter>();

	NotificationSubCategoryConverter(NotificationSubCategory categoryRaw, String categoryValue)
	{
		this.categoryRaw = categoryRaw;
		this.categoryValue = categoryValue;
	}

	static
	{
		for (NotificationSubCategoryConverter category : EnumSet.allOf(NotificationSubCategoryConverter.class))
			notificationSubCategoryMap.put(category.getCatgeoryRaw(), category);
	}

	public static String convert(NotificationSubCategory value)
	{
		String category = "";
		if (value != null)
		{
			category = StringUtils.isNotBlank(notificationSubCategoryMap.get(value).getCatgeory())
				? notificationSubCategoryMap.get(value).getCatgeory()
				: "";
		}
		return category;
	}

	public String getCatgeory()
	{
		return categoryValue;
	}

	private NotificationSubCategory getCatgeoryRaw()
	{
		return categoryRaw;
	}
}