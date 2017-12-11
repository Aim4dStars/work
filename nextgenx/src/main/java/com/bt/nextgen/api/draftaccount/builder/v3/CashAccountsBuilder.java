package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.model.form.ILinkedAccountForm;
import com.bt.nextgen.api.draftaccount.model.form.ILinkedAccountsForm;
import com.bt.nextgen.api.draftaccount.util.BsbFormatter;
import ns.btfin_com.product.common.cashaccount.v2_0.LinkedFinancialInstitutionType;
import ns.btfin_com.product.common.investmentaccount.v2_0.CashAccountsType;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.btfin.panorama.onboarding.helper.CashAccountHelper.cashAccounts;
import static com.btfin.panorama.onboarding.helper.CashAccountHelper.linkedAccount;

@Service
class CashAccountsBuilder {

    CashAccountsType getCashAccounts(ILinkedAccountsForm linkedAccounts) {
        final ILinkedAccountForm primary = linkedAccounts.getPrimaryLinkedAccount();
        final List<ILinkedAccountForm> others = linkedAccounts.getOtherLinkedAccounts();
        final LinkedFinancialInstitutionType[] linked = new LinkedFinancialInstitutionType[others.size() + 1];
        linked[0] = getLinkedFinancialInstitutionType(primary, true);
        //US23438: for Direct (BT Invest) we can't have more than a single primary linked account
        if (primary.isAccountManuallyEntered()) {
            linked[0].setIsAccountManuallyEntered(true);
        }
        int i = 1;
        for (ILinkedAccountForm other : others) {
            linked[i] = getLinkedFinancialInstitutionType(other, false);
            i++;
        }
        return cashAccounts(linked);
    }

    private LinkedFinancialInstitutionType getLinkedFinancialInstitutionType(ILinkedAccountForm linkedAccount, boolean isPrimaryLinkedAccount) {
        final String bsb = BsbFormatter.formatBsb(linkedAccount.getBsb());
        return linkedAccount(bsb, linkedAccount.getAccountNumber(), linkedAccount.getAccountName(), linkedAccount.getNickName(), isPrimaryLinkedAccount);
    }
}
