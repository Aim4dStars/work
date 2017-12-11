package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.form.IInvestmentChoiceForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.investmentChoiceForm.InvestmentChoice;

/**
 * Created by F058391 on 14/04/2016.
 */
class InvestmentChoiceForm implements IInvestmentChoiceForm {

    private final InvestmentChoice investmentChoice;

    public InvestmentChoiceForm(InvestmentChoice investmentChoice) {
        this.investmentChoice = investmentChoice;
    }

    @Override
    public String getPortfolioName() {
        return investmentChoice.getPortfolioName();
    }

    @Override
    public String getInitialDeposit() {
        //TODO funny hack to remove once toggle not required
        final Double initialDeposit = investmentChoice.getInitialDeposit();
        return initialDeposit != null ? initialDeposit.toString() : null;
    }

    @Override
    public String getPortfolioType() {
        return investmentChoice.getPortfolioType();
    }
}
