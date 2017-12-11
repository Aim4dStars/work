package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.IInvestmentChoiceForm;
import com.bt.nextgen.api.draftaccount.model.form.ILinkedAccountForm;
import com.bt.nextgen.api.draftaccount.model.form.ILinkedAccountsForm;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.PaymentInstructionType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.PaymentInstructionsType;
import ns.btfin_com.sharedservices.common.payment.v2_1.PaymentAccountType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil.getXMLGregorianCalendarNow;
import static com.btfin.panorama.onboarding.helper.PaymentHelper.bankAccount;
import static com.btfin.panorama.onboarding.helper.PaymentHelper.debitInstruction;
import static com.btfin.panorama.onboarding.helper.PaymentHelper.paymentInstructions;
import static java.math.BigDecimal.ZERO;
import static org.apache.commons.lang.StringUtils.isBlank;

@Service
class PaymentInstructionsBuilder {

    PaymentInstructionsType getPaymentInstructions(ILinkedAccountsForm linkedAccounts) {
        final List<ILinkedAccountForm> allLinkedAccouts = new LinkedList<>(linkedAccounts.getOtherLinkedAccounts());
        allLinkedAccouts.add(0, linkedAccounts.getPrimaryLinkedAccount());
        final Collection<PaymentInstructionType> payments = new ArrayList<>(allLinkedAccouts.size());
        for (ILinkedAccountForm linkedAccount : allLinkedAccouts) {
            final BigDecimal amount = getPaymentAmount(linkedAccount.getDirectDebitAmount());
            if (amount.compareTo(ZERO) > 0) {
                payments.add(debit(linkedAccount, amount));
            }
        }
        return payments.isEmpty() ? null : paymentInstructions(payments);
    }

    PaymentInstructionsType getPaymentInstructionsForInvestmentAccount(IClientApplicationForm clientApplicationForm) {
        final IInvestmentChoiceForm investmentChoiceForm = clientApplicationForm.getInvestmentChoice();
        final BigDecimal amount = getPaymentAmount(investmentChoiceForm.getInitialDeposit());
        if (amount.compareTo(ZERO) > 0) {
            final ILinkedAccountForm primary = clientApplicationForm.getLinkedAccounts().getPrimaryLinkedAccount();
            return paymentInstructions(debit(primary, amount));
        }
        return null;
    }

    private PaymentInstructionType debit(ILinkedAccountForm linked, BigDecimal amount) {
        PaymentAccountType account = bankAccount(linked.getBsb(), linked.getAccountNumber(), linked.getAccountName());
        return debitInstruction(amount, account, getXMLGregorianCalendarNow());
    }

    private BigDecimal getPaymentAmount(String directDebitAmount) {
        return isBlank(directDebitAmount) ? ZERO : new BigDecimal(directDebitAmount);
    }
}
