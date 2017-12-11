package com.bt.nextgen.service.integration.termdeposit;

public enum TermDepositAction {
    VALIDATE_ADD_TERM_DEPOSIT("Validate Add Term Deposit"),
    BREAK_TERM_DEPOSIT("Break Term Deposit"),
    UPDATE_TERM_DEPOSIT("Update Term Deposit"),
    ADD_TERM_DEPOSIT("Add Term Deposit"),
    VALIDATE_BREAK_TERM_DEPOSIT("Validate Break Term Deposit");

    private final String termDepositActionValue;

    public String getTermDepositActionValue() {
        return termDepositActionValue;
    }

    TermDepositAction(String termDepositActionValue) {
        this.termDepositActionValue = termDepositActionValue;
    }

}
