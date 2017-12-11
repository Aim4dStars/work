package com.bt.nextgen.api.draftaccount.model.form;


import java.util.Map;

/**
 * @deprecated Use the v1 version of this class instead.
 */
@Deprecated
class InvestmentChoiceForm implements IInvestmentChoiceForm{

    private final  Map<String, Object> investmentChoice;

    public InvestmentChoiceForm(Map<String, Object> investmentChoice) {
        this.investmentChoice = investmentChoice;
    }

    @Override
    public String getPortfolioName() {
        return (String)investmentChoice.get("portfolioName");
    }

    @Override
    public String getInitialDeposit() {
        return String.valueOf(investmentChoice.get("initialDeposit"));
    }

    @Override
    public String getPortfolioType() {
        return (String)investmentChoice.get("portfolioType");
    }
}
