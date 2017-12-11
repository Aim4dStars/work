package com.bt.nextgen.api.regularinvestment.v2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.account.v2.model.BankAccountDto;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.movemoney.v2.model.DepositDto;
import com.bt.nextgen.api.movemoney.v2.util.DepositUtil;
import com.bt.nextgen.api.regularinvestment.v2.model.RegularInvestmentDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.PayAnyoneAccountDetailsImpl;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.bt.nextgen.service.integration.movemoney.RecurringDepositDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetailsIntegrationService;

@Service("AccountHelperV2")
public class AccountHelper {

    @Autowired
    private PayeeDetailsIntegrationService payeeService;

    public RecurringDepositDetails getRecurringDepositDetails(RegularInvestmentDto invDto, ServiceErrors serviceErrors) {
        if (invDto != null && invDto.getDepositDetails() != null) {
            DepositDto depositDto = invDto.getDepositDetails();
            com.bt.nextgen.api.account.v3.model.AccountKey key = new com.bt.nextgen.api.account.v3.model.AccountKey(
                    EncodedString.toPlainText(invDto.getAccountKey().getAccountId()));
            depositDto.setKey(key);
            if (depositDto.getRepeatEndDate() != null) {
                depositDto.setEndRepeat("setDate");
            }

            return DepositUtil.populateRecurDepositDetailsReq(depositDto, getPayAnyoneAccount(depositDto),
                    getMoneyAccountIdentifier(depositDto.getKey(), serviceErrors));
        }
        return null;
    }

    public BankAccountDto getBankAccountDetails(String accId, ServiceErrors serviceErrors) {
        WrapAccountIdentifierImpl wrapAccountIdentifierImpl = new WrapAccountIdentifierImpl();
        wrapAccountIdentifierImpl.setBpId(accId);

        return getBankAccountDto(wrapAccountIdentifierImpl, serviceErrors);
    }

    public BankAccountDto getBankAccountDto(WrapAccountIdentifierImpl wrapAccountIdentifierImpl, ServiceErrors serviceErrors) {
        PayeeDetails payeeDetails = payeeService.loadPayeeDetails(wrapAccountIdentifierImpl, serviceErrors);

        BankAccountDto accDto = new BankAccountDto();
        if (payeeDetails.getCashAccount() != null) {
            accDto.setAccountNumber(payeeDetails.getCashAccount().getAccountNumber());
            accDto.setName(payeeDetails.getCashAccount().getAccountName());

            String bsb = payeeDetails.getCashAccount().getBsb();
            if (bsb != null && bsb.length() == 6) {
                bsb = bsb.substring(0, 3) + "-" + bsb.substring(3, 6);
            }
            accDto.setBsb(bsb);
        }
        return accDto;
    }

    public PayeeDetails getPayeeDetails(String accId, ServiceErrors serviceErrors) {
        WrapAccountIdentifierImpl wrapAccountIdentifierImpl = new WrapAccountIdentifierImpl();
        wrapAccountIdentifierImpl.setBpId(accId);

        return payeeService.loadPayeeDetails(wrapAccountIdentifierImpl, serviceErrors);
    }

    protected MoneyAccountIdentifier getMoneyAccountIdentifier(AccountKey accKey, ServiceErrors serviceErrors) {
        PayeeDetails payeeDetails = getPayeeDetails(accKey.getAccountId(), serviceErrors);
        if (payeeDetails != null) {
            return payeeDetails.getMoneyAccountIdentifier();
        }
        return null;
    }

    private PayAnyoneAccountDetails getPayAnyoneAccount(DepositDto depositDto) {
        PayAnyoneAccountDetails payAnyOneAccounts = new PayAnyoneAccountDetailsImpl();
        payAnyOneAccounts.setAccount(depositDto.getFromPayDto().getAccountId());
        payAnyOneAccounts.setBsb(depositDto.getFromPayDto().getCode());

        return payAnyOneAccounts;
    }
}
