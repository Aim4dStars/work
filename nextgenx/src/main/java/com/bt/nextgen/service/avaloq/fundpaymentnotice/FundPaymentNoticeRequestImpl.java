package com.bt.nextgen.service.avaloq.fundpaymentnotice;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.fundpaymentnotice.FundPaymentNoticeRequest;

/**
 * @author L070354
 *
 * Implementation class of the FundPaymentNoticeRequest
 */

public class FundPaymentNoticeRequestImpl implements FundPaymentNoticeRequest
{

	DateTime startDate;

	DateTime endDate;

	Collection <String> assetIdList;

	@Override
	public DateTime getStartDate()
	{
		// TODO Auto-generated method stub
		return startDate;
	}

	@Override
	public DateTime getEndDate()
	{
		// TODO Auto-generated method stub
		return endDate;
	}

	@Override
	public Collection <String> getAssetIds()
	{
		// TODO Auto-generated method stub
		return assetIdList;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(DateTime startDate)
	{
		this.startDate = startDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(DateTime endDate)
	{
		this.endDate = endDate;
	}

	/**
	 * @return the assetIdList
	 */
	public Collection <String> getAssetIdList()
	{
		return assetIdList;
	}

	/**
	 * @param assetIdList the assetIdList to set
	 */
	public void setAssetIdList(Collection <String> assetIdList)
	{
		this.assetIdList = assetIdList;
	}

}
