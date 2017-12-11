package com.bt.nextgen.api.regularinvestment.v2.service;

import com.bt.nextgen.api.account.v2.model.BankAccountDto;
import com.bt.nextgen.api.movemoney.v2.model.PayeeDto;
import com.btfin.panorama.service.integration.account.LinkedAccount;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetails;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestment;

public final class RIPPayeeUtil {

    private RIPPayeeUtil() {
    }

    public static PayeeDto getFromPayDto(RegularInvestment regularInvestment, PayeeDetails payeeDetails) {
        PayeeDto fromPayDto = new PayeeDto();
        if (regularInvestment.getDirectDebitDetails().getPayAnyoneAccountDetails() != null) {

            for (LinkedAccount la : payeeDetails.getLinkedAccountList()) {
                if (la.getAccountNumber().equals(
                        regularInvestment.getDirectDebitDetails().getPayAnyoneAccountDetails().getAccount())) {
                    fromPayDto.setAccountId(la.getAccountNumber());
                    fromPayDto.setAccountName(la.getName());
                    fromPayDto.setCode(la.getBsb());
                    break;
                }
            }
        }
        return fromPayDto;
    }

    public static PayeeDto getBankPayeeDto(BankAccountDto bankDto) {
        PayeeDto toPayeeDto = new PayeeDto();
        toPayeeDto.setAccountId(bankDto.getAccountNumber());
        toPayeeDto.setAccountName(bankDto.getName());
        toPayeeDto.setCode(bankDto.getBsb());

        return toPayeeDto;
    }
}
