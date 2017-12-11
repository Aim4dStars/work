package com.bt.nextgen.api.income.v1.model;

import com.bt.nextgen.service.integration.income.DistributionIncome;
import org.joda.time.DateTime;

import java.math.BigDecimal;

@Deprecated
public class DistributionIncomeDto extends IncomeDto
{
	private DateTime executionDate;
	private BigDecimal quantity;
	private BigDecimal incomeRate;
    private boolean isFeeRebate;

	public DistributionIncomeDto(String name, String code, DateTime executionDate, DateTime paymentDate, BigDecimal quantity,
		BigDecimal incomeRate, BigDecimal amount)
	{
		super(name, code, paymentDate, amount);
		this.executionDate = executionDate;
		this.quantity = quantity;
		this.incomeRate = incomeRate;
		
        isFeeRebate = paymentDate != null && amount != null && quantity == null;
	}

	public DistributionIncomeDto(String name, String code, DistributionIncome income) {
	    super(name, code, income.getPaymentDate(), income.getAmount());
        executionDate = income.getExecutionDate();
        quantity = income.getQuantity();
        incomeRate = income.getIncomeRate();
        isFeeRebate = income.isFeeRebate();
    }

    public DateTime getExecutionDate()
	{
		return executionDate;
	}

	public BigDecimal getQuantity()
	{
		return quantity;
	}

	public BigDecimal getIncomeRate()
	{
		return incomeRate;
	}

    public boolean getIsFeeRebate() {
        return isFeeRebate;
    }
}
