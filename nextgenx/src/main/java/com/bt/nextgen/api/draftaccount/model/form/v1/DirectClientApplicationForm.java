package com.bt.nextgen.api.draftaccount.model.form.v1;

import java.util.ArrayList;
import java.util.List;

import ch.lambdaj.Lambda;

import com.bt.nextgen.api.draftaccount.model.form.IAccountSettingsForm;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.ICompanyForm;
import com.bt.nextgen.api.draftaccount.model.form.IExtendedPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IFeesForm;
import com.bt.nextgen.api.draftaccount.model.form.IInvestmentChoiceForm;
import com.bt.nextgen.api.draftaccount.model.form.ILinkedAccountsForm;
import com.bt.nextgen.api.draftaccount.model.form.IPensionEligibilityForm;
import com.bt.nextgen.api.draftaccount.model.form.IPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IShareholderAndMembersForm;
import com.bt.nextgen.api.draftaccount.model.form.ISmsfForm;
import com.bt.nextgen.api.draftaccount.model.form.ITrustForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.DirectClientApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.customer.Customer;
import com.bt.nextgen.api.draftaccount.schemas.v1.fees.Fees;
import com.bt.nextgen.core.util.LambdaMatcher;

/**
 * Created by F058391 on 14/04/2016.
 */

final class DirectClientApplicationForm implements IClientApplicationForm {

    private final DirectClientApplicationFormData formData;

    DirectClientApplicationForm(DirectClientApplicationFormData formData) {
        this.formData = formData;
    }

    @Override
    public List<IExtendedPersonDetailsForm> getInvestors() {
        List<IExtendedPersonDetailsForm> investorDetailsList = new ArrayList<>();
        IAccountSettingsForm accountSettingsForm = getAccountSettings();
        for (int i = 0; i < this.formData.getInvestors().size(); i++) {
            int correlationIndex = i+1; // Since we can't start correlation sequence from zero
            investorDetailsList.add(
                    new ExtendedPersonDetailsForm(correlationIndex,formData.getInvestors().get(i),
                            accountSettingsForm.isPrimaryContact(i),
                            accountSettingsForm.getPaymentSettingForInvestor(i),
                            true)
            );
        }
        return investorDetailsList;
    }

    @Override
    public List<IExtendedPersonDetailsForm> getDirectors() {
        throw new IllegalStateException("getDirector shouldn't be invoked for direct");
    }

    @Override
    public List<IExtendedPersonDetailsForm> getDirectorsSecretariesSignatories() {
        throw new IllegalStateException("getDirectorsSecretariesSignatories shouldn't be invoked for direct");
    }

    @Override
    public List<IPersonDetailsForm> getGenericPersonDetails() {
        final List<IPersonDetailsForm> personList = new ArrayList<>();
        final List<Customer> investors = this.formData.getInvestors();
        for (int i = 0; i < investors.size(); i++) {
            int correlationIndex = i+1; // Since we cant start correlation sequence from zero
            Customer customer = investors.get(i);
            personList.add(new PersonDetailsForm(correlationIndex, customer));
        }
        return personList;
    }

    @Override
    public List<IPersonDetailsForm> getExistingPersonDetails() {
        return Lambda.filter(new LambdaMatcher<PersonDetailsForm>() {
            @Override
            protected boolean matchesSafely(PersonDetailsForm personDetailsForm) {
                return personDetailsForm.isExistingPerson();
            }
        }, getGenericPersonDetails());
    }


    @Override
    public List<IExtendedPersonDetailsForm> getTrustees() {
        throw new IllegalStateException("getTrustees shouldn't be invoked for direct");
    }

    @Override
    public List<IExtendedPersonDetailsForm> getAdditionalShareholdersAndMembers() {
        throw new IllegalStateException("getAdditionalShareholdersAndMembers shouldn't be invoked for direct");
    }

    @Override
    public ICompanyForm getCompanyTrustee() {
        throw new IllegalStateException("getCompanyTrustee shouldn't be invoked for direct");
    }

    @Override
    public IAccountSettingsForm getAccountSettings() {
        return new DirectAccountSettingsForm();
    }

    @Override
    public IShareholderAndMembersForm getShareholderAndMembers() {
        throw new IllegalStateException("getShareholderAndMembers shouldn't be invoked for direct");
    }

    @Override
    public AccountType getAccountType() {
        if (formData != null && formData.getAccountType() != null) {
            switch (formData.getAccountType()) {
                case SUPER_ACCUMULATION://DIRECT_SUPER
                    return AccountType.SUPER_ACCUMULATION;
                case SUPER_PENSION://DIRECT_PENSION
                    return AccountType.SUPER_PENSION;
                default:
                    return AccountType.INDIVIDUAL;
            }
        }
        return AccountType.INDIVIDUAL;
    }

    @Override
    public String getApplicationOrigin() {
        return ApplicationOriginType.WESTPAC_LIVE.value();
    }

    @Override
    public String getAdviceType() {
        return AdviceType.NO_ADVICE.value();
    }

    @Override
    public String getTrustType() {
        throw new IllegalStateException("getTrustType shouldn't be invoked for direct");
    }

    @Override
    public IFeesForm getFees() {
        return new FeesForm(new Fees());
    }

    @Override
    public ILinkedAccountsForm getLinkedAccounts() {
        return new LinkedAccountsForm(formData.getLinkedaccounts());
    }

    @Override
    public ISmsfForm getSmsf() {
        throw new IllegalStateException("getSmsf shouldn't be invoked for direct");
    }

    @Override
    public ICompanyForm getCompanyDetails() {
        throw new IllegalStateException("getCompanyDetails shouldn't be invoked for direct");
    }

    @Override
    public boolean hasTrust() {
        return false;
    }

    @Override
    public ITrustForm getTrust() {
        return null;
    }

    @Override
    public boolean hasInvestmentChoice() {
        return null!=formData.getInvestmentoptions();
    }

    @Override
    public IInvestmentChoiceForm getInvestmentChoice() {
        if(formData.getInvestmentoptions() != null) {
            return new InvestmentChoiceForm(formData.getInvestmentoptions());
        }
        return null;
    }

    @Override
    public String getAccountName() {
        return null;
    }

    @Override
    public boolean isDirectAccount() {
        return true;
    }

    @Override
    public ApprovalType getApplicationApprovalType() {
        return ApprovalType.ONLINE;
    }

    @Override
    public IPensionEligibilityForm getPensionEligibility() {
        return formData.getPensioneligibility() != null ? new PensionEligibilityForm(formData.getPensioneligibility()) : null;
    }

    @Override
    public String getParentProductName() {
        throw new IllegalStateException("getParentProductName shouldn't be invoked for direct");
    }
}
