package com.bt.nextgen.api.fees.model;

import com.bt.nextgen.api.account.v1.model.AccountKey;

import java.util.List;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class FeeScheduleDto extends BaseDto implements KeyedDto <AccountKey>
{

	private AccountKey key;

	private List <FeesTypeDto> fees;

    private int maxFeesCount;

	private List <InvestmentMgmtFeesDto> investmentMgmtFees;

	private FeesScheduleTrxnDto transactionDto;

	private List <DomainApiErrorDto> warnings;

	private String submitDate;

	public List <FeesTypeDto> getFees()
	{
		return fees;
	}

	public void setFees(List <FeesTypeDto> fees)
	{
		this.fees = fees;
	}

	public AccountKey getKey()
	{
		return key;
	}

	public void setKey(AccountKey key)
	{
		this.key = key;
	}

	public FeesScheduleTrxnDto getTransactionDto()
	{
		return transactionDto;
	}

	public void setTransactionDto(FeesScheduleTrxnDto transactionDto)
	{
		this.transactionDto = transactionDto;
	}

	public List <DomainApiErrorDto> getWarnings()
	{
		return warnings;
	}

	public void setWarnings(List <DomainApiErrorDto> warnings)
	{
		this.warnings = warnings;
	}

	public String getSubmitDate()
	{
		return submitDate;
	}

	public void setSubmitDate(String submitDate)
	{
		this.submitDate = submitDate;
	}

	public List <InvestmentMgmtFeesDto> getInvestmentMgmtFees()
	{
		return investmentMgmtFees;
	}

	public void setInvestmentMgmtFees(List <InvestmentMgmtFeesDto> investmentMgmtFees)
	{
		this.investmentMgmtFees = investmentMgmtFees;
	}

    public int getMaxFeesCount() {
        return maxFeesCount;
    }

    public void setMaxFeesCount(int maxFeesCount) {
        this.maxFeesCount = maxFeesCount;
    }
}
