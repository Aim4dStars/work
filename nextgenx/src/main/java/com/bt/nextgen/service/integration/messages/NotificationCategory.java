package com.bt.nextgen.service.integration.messages;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author L070354
 * 
 * Enumeration of Notification categories
 */
public enum NotificationCategory
{

	CHANGES_TO_ACCOUNTS("chg_act"),
	CLIENT_ACTIONS("clt_act"),
	CONFIRMED_TRANSACTIONS("conf_trx"),
	FAILED_TRANSACTIONS_AND_WARNINGS("fail_trx"),
	MATURING_TERM_DEPOSITS("mat_term"),
	NEW_CLIENTS("new_clt"),
	NEW_STATEMENTS("new_stmt"),
	PRODUCT_NEWS("prod_news"),
	MARKET_NEWS("mkt_news"),
	ASX_ANNOUNCEMENTS("asx_announcements"),
	CLIENT_ACTIVATION("clt_activ"),
	FAILED_PAYMENTS("fail_pay"),
	WELCOME_PACK("welc_pck"),

	//Newly added in Version2 changes
	FAIL_MARKETDATAHUB("fail_mdh"),
	TRANSFER_VETTING("xfer_vetting"),
    NEW_ACCOUNTANT("new_acctnt"),
	CORPORATE_ACTIONS("secevt2"),
	FUND_ADMINISTRATION("smsf_fa");

	private final String category;

	private static final Map <String, com.btfin.panorama.core.security.integration.messages.NotificationCategory> statusMap = new HashMap();

	static
	{
		//Create reverse lookup hash map 
		for (com.btfin.panorama.core.security.integration.messages.NotificationCategory cat : com.btfin.panorama.core.security.integration.messages.NotificationCategory.values())
			statusMap.put(cat.getCategoryValue(), cat);
	}

	public String getCategoryValue()
	{
		return category;
	}

	NotificationCategory(String category)
	{
		this.category = category;
	}

	public static com.btfin.panorama.core.security.integration.messages.NotificationCategory getCategory(String category)
	{

		return (com.btfin.panorama.core.security.integration.messages.NotificationCategory)statusMap.get(category);
	}
}
