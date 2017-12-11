package com.bt.nextgen.api.fundpayment.model;

import java.util.List;

import com.bt.nextgen.core.api.model.BaseDto;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.bt.nextgen.api.util.ApiConstants;
import com.bt.nextgen.core.api.model.KeyedDto;

public class FundPaymentNoticeDto extends BaseDto implements KeyedDto <FundPaymentNoticeSearchDtoKey>
{
	private String code;
	private String fundName;
	private String fundManager;
	private String incomeTaxYear;
	private DateTime distributionDate;
	private String distributionAmount;
	private String mitWhtAmount;
	private List <Distribution> distributionList;
	private Boolean amitNotice;

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getFundName()
	{
		return fundName;
	}

	public void setFundName(String fundName)
	{
		this.fundName = fundName;
	}

	public String getFundManager()
	{
		return fundManager;
	}

	public void setFundManager(String fundManager)
	{
		this.fundManager = fundManager;
	}

	public String getIncomeTaxYear()
	{
		return incomeTaxYear;
	}

	public void setIncomeTaxYear(String incomeTaxYear)
	{
		this.incomeTaxYear = incomeTaxYear;
	}

	public DateTime getDistributionDate()
	{
		return distributionDate;
	}

	public void setDistributionDate(DateTime dateTime)
	{
		this.distributionDate = dateTime;
	}

	public String getDistributionAmount()
	{
		return distributionAmount;
	}

	public void setDistributionAmount(String bigDecimal)
	{
		this.distributionAmount = bigDecimal;
	}

	public List <Distribution> getDistributionList()
	{
		return distributionList;
	}

	public void setDistributionList(List <Distribution> distributionList)
	{
		this.distributionList = distributionList;
	}

	@Override
	public String getType()
	{
		return null;
	}

	@Override
	public FundPaymentNoticeSearchDtoKey getKey()
	{
		return null;
	}

	public String getMitWhtAmount()
	{
		return StringUtils.isNotBlank(mitWhtAmount) ? mitWhtAmount : ApiConstants.SIX_DECIMAL_PLACES;
	}

	public void setMitWhtAmount(String mitWhtAmount)
	{
		this.mitWhtAmount = mitWhtAmount;
	}

    public Boolean isAmitNotice() {
        return amitNotice;
    }

    public void setAmitNotice(Boolean amitNotice) {
        this.amitNotice = amitNotice;
    }
}
