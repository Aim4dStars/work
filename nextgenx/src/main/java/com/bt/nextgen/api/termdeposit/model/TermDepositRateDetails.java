package com.bt.nextgen.api.termdeposit.model;

import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;

import java.util.List;

/**
 * Created by L069552 on 31/08/17.
 */
public class TermDepositRateDetails {

    private List<TermDepositInterestRate> termDepositInterestRates;
    private Badge selectedBadge;

    public List<TermDepositInterestRate> getTermDepositInterestRates() {
        return termDepositInterestRates;
    }

    public void setTermDepositInterestRates(List<TermDepositInterestRate> termDepositInterestRates) {
        this.termDepositInterestRates = termDepositInterestRates;
    }

    public Badge getSelectedBadge() {
        return selectedBadge;
    }

    public void setSelectedBadge(Badge selectedBadge) {
        this.selectedBadge = selectedBadge;
    }
}
