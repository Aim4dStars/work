package com.bt.nextgen.service.integration.fundpaymentnotice;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;

import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.order.Order;

/**
 * 
 * This interface defines the reponse recieved from avaloq for the Fund Payment Notice
 *
 */

public interface FundPaymentNotice
{
	/**
	 * @return asset - The Asset for the Managed Fund 
	 */
	Asset getAsset();

	/**
	 * @return order - return the order
	 */
	Order getOrder();

	/**
	 *  @return date - the distribution date
	 */
	DateTime getDistributionDate();

	/**
	 *  @return date - the tax relevance date
	 */
	DateTime getTaxRelevanceDate();

	/**
	 *  @return date - the tax year
	 */
	String getTaxYear();

	/**
	 *  @return DistributionDetails - the distribution Details
	 */
	List <DistributionDetails> getDistributions();

	/**
	 * @return amount - The Distribution Amount for the Managed Fund
	 */
	BigDecimal getDistributionAmount();

	/**
	 * 
	 * @return amount - The Withholding Tax Fund payment amount for the Managed Fund
	 */
	BigDecimal getWithHoldFundPaymentAmount();

	/**
	 * Returns if the fund payment notice is on a AMIT asset
	 */
	Boolean isAmitNotice();
}
