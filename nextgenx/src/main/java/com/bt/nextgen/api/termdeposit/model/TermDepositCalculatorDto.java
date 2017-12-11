package com.bt.nextgen.api.termdeposit.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

public class TermDepositCalculatorDto extends BaseDto implements KeyedDto <TermDepositCalculatorKey>
{
	private TermDepositCalculatorKey key;
	private Badge badge;
    private List<Badge> badges;
    private List<TermDepositBankRates> termDepositBankRates;
    private String selectedAccountType;
    private List<String> accountTypeList;
	
	@Override
	public TermDepositCalculatorKey getKey()
	{
		return key;
	}

	public List<TermDepositBankRates> getTermDepositBankRates() 
	{
		return termDepositBankRates;
	}

	public void setTermDepositBankRates(List<TermDepositBankRates> termDepositBankRates) 
	{
		this.termDepositBankRates = termDepositBankRates;
	}

	public Badge getBadge() 
	{
		return badge;
	}

	public void setBadge(Badge badge) 
	{
		this.badge = badge;
	}

    public List<Badge> getBadges() 
	{
		return badges;
	}

    public void setBadges(List<Badge> badges) 
	{
		this.badges = badges;
	}

    public String getSelectedAccountType() {
        return selectedAccountType;
    }

    public List<String> getAccountTypeList() {
        return accountTypeList;
    }

    public void setSelectedAccountType(String selectedAccountType) {
        this.selectedAccountType = selectedAccountType;
    }

    public void setAccountTypeList(List<String> accountTypeList) {
        this.accountTypeList = accountTypeList;
    }


}
