package com.bt.nextgen.api.draftaccount.model.form.v1;

import java.util.ArrayList;
import java.util.List;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.AnswerTypeEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.ApprovalTypeEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.company.AdditionalShareholderAndMember;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.bt.nextgen.api.draftaccount.model.form.IAccountSettingsForm;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.ICompanyForm;
import com.bt.nextgen.api.draftaccount.model.form.IExtendedPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IFeesForm;
import com.bt.nextgen.api.draftaccount.model.form.IInvestmentChoiceForm;
import com.bt.nextgen.api.draftaccount.model.form.ILinkedAccountsForm;
import com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm;
import com.bt.nextgen.api.draftaccount.model.form.IPensionEligibilityForm;
import com.bt.nextgen.api.draftaccount.model.form.IPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IShareholderAndMembersForm;
import com.bt.nextgen.api.draftaccount.model.form.ISmsfForm;
import com.bt.nextgen.api.draftaccount.model.form.ITrustForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.OnboardingApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.company.InvestorsWithRole;
import com.bt.nextgen.api.draftaccount.schemas.v1.customer.Customer;
import com.bt.nextgen.api.draftaccount.schemas.v1.trust.TrustDetails;
import com.bt.nextgen.core.util.LambdaMatcher;

import static java.util.Collections.emptyList;

/**
 * Created by m040398 on 18/03/2016.
 */
@SuppressWarnings({"squid:S1200", "squid:MethodCyclomaticComplexity"})
final class ClientApplicationForm implements IClientApplicationForm {

    private final OnboardingApplicationFormData formData;
    private Integer correlationIndex;

    ClientApplicationForm(OnboardingApplicationFormData formData) {
        this.formData = formData;
        this.correlationIndex = 0;
    }

    /**
     * This method is only used for individual and joint accounts, where all investors are approvers
     *
     * @return
     */
    @Override
    public List<IExtendedPersonDetailsForm> getInvestors() {
        List<IExtendedPersonDetailsForm> investorDetailsList = new ArrayList<>();
        IAccountSettingsForm accountSettingsForm = getAccountSettings();
        for (int i = 0; i < this.formData.getInvestors().size(); i++) {
            investorDetailsList.add(
                new ExtendedPersonDetailsForm(++correlationIndex, formData.getInvestors().get(i), accountSettingsForm.isPrimaryContact(i),
                    accountSettingsForm.getPaymentSettingForInvestor(i), true));
        }
        return investorDetailsList;
    }

    @Override
    public List<IExtendedPersonDetailsForm> getDirectors() {
        return getDirectorsByOrganisationRole(IOrganisationForm.OrganisationRole.DIRECTOR);
    }

    private List<IExtendedPersonDetailsForm> getDirectorsByOrganisationRole(IOrganisationForm.OrganisationRole role) {
        List<IExtendedPersonDetailsForm> personDetailsList = new ArrayList<>();
        IAccountSettingsForm accountSettingsForm = getAccountSettings();
        ShareholderAndMembersForm shareholderAndMembersForm = (ShareholderAndMembersForm) getShareholderAndMembers();
        Integer directorWithSecretaryRole = StringUtils.isNotBlank(shareholderAndMembersForm.getCompanySecretaryValue()) ? Integer.parseInt(shareholderAndMembersForm.getCompanySecretaryValue()) : null;
        List < Customer > directors = this.formData.getDirectors();
        for (int i = 0; i < directors.size(); i++) {
            InvestorsWithRole investorWithRole = shareholderAndMembersForm.getInvestorsWithRoles().get(i);
            personDetailsList.add(new DirectorDetailsForm(++correlationIndex, directors.get(i), accountSettingsForm.isPrimaryContact(i),
                accountSettingsForm.getPaymentSettingForInvestor(i), accountSettingsForm.getApproverSettingForInvestor(i),
                AnswerTypeEnum.YES.equals(investorWithRole.getIsShareholder()),
                    AnswerTypeEnum.YES.equals(investorWithRole.getIsMember()),
                    AnswerTypeEnum.YES.equals(investorWithRole.getIsBeneficiary()),
                    AnswerTypeEnum.YES.equals(investorWithRole.getIsBeneficialOwner()),
                    directorWithSecretaryRole !=null && directorWithSecretaryRole == i,
                    AnswerTypeEnum.YES.equals(investorWithRole.getIsControllerOfTrust()),
                    role != null ? role : accountSettingsForm.getRoleSettingForInvestor(i)));
        }
        return personDetailsList;
    }

    @Override
    public List<IExtendedPersonDetailsForm> getDirectorsSecretariesSignatories() {
        return getDirectorsByOrganisationRole(null);
    }

    @Override
    public List<IPersonDetailsForm> getGenericPersonDetails() {
        final List<Customer> persons;
        switch (this.formData.getAccountType()) {
            case COMPANY:
            case NEW_CORPORATE_SMSF:
            case CORPORATE_SMSF:
            case CORPORATE_TRUST:
                persons = this.formData.getDirectors();
                break;
            case INDIVIDUAL:
            case JOINT:
            case SUPER_ACCUMULATION:
            case SUPER_PENSION:
                persons = this.formData.getInvestors();
                break;
            case INDIVIDUAL_TRUST:
            case INDIVIDUAL_SMSF:
            case NEW_INDIVIDUAL_SMSF:
                persons = this.formData.getTrustees();
                break;
            default:
                persons = emptyList();
        }
        final List<IPersonDetailsForm> personList = new ArrayList<>(persons.size());
        for (Customer person : persons) {
            personList.add(new PersonDetailsForm(++correlationIndex, person));
        }
        return personList;
    }

    @Override
    public List<IPersonDetailsForm> getExistingPersonDetails() {
        return Lambda.filter(new LambdaMatcher<IPersonDetailsForm>() {
            @Override
            protected boolean matchesSafely(IPersonDetailsForm personDetailsForm) {
                return personDetailsForm.isExistingPerson();
            }
        }, getGenericPersonDetails());
    }

    @Override
    public List<IExtendedPersonDetailsForm> getTrustees() {
        List<IExtendedPersonDetailsForm> personDetailsList = new ArrayList<>();
        IAccountSettingsForm accountSettingsForm = getAccountSettings();
        ShareholderAndMembersForm shareholderAndMembersForm = (ShareholderAndMembersForm) getShareholderAndMembers();
        List<Customer> trustees = this.formData.getTrustees();
        for (int i = 0; i < trustees.size(); i++) {
            InvestorsWithRole investorWithRole = shareholderAndMembersForm.getInvestorsWithRoles().get(i);
            personDetailsList.add(new TrusteeDetailsForm(++correlationIndex, trustees.get(i), accountSettingsForm.isPrimaryContact(i),
                accountSettingsForm.getPaymentSettingForInvestor(i), accountSettingsForm.getApproverSettingForInvestor(i),
                    AnswerTypeEnum.YES.equals(investorWithRole.getIsMember()),
                    AnswerTypeEnum.YES.equals(investorWithRole.getIsBeneficiary())));
        }
        return personDetailsList;
    }

    @Override
    public List<IExtendedPersonDetailsForm> getAdditionalShareholdersAndMembers() {
        List<IExtendedPersonDetailsForm> result = null;
        if(formData.getShareholderandmembers() != null) {
            result = new ArrayList<>();
            List<AdditionalShareholderAndMember> additionalShareholdersAndMembers = ((ShareholderAndMembersForm) getShareholderAndMembers()).getAdditionalShareholdersAndMembers();
            if (CollectionUtils.isNotEmpty(additionalShareholdersAndMembers)) {
                for (AdditionalShareholderAndMember member : additionalShareholdersAndMembers) {
                    result.add(new ShareholderMemberDetailsForm(++correlationIndex, member));
                }
            }
        }
        return result;
    }

    @Override
    public ICompanyForm getCompanyTrustee() {
        return new CompanyForm(++correlationIndex, formData.getCompanytrustee());
    }

    @Override
    public IAccountSettingsForm getAccountSettings() {
        return new AccountSettingsForm(formData.getAccountsettings());
    }

    @Override
    public IShareholderAndMembersForm getShareholderAndMembers() {
        return new ShareholderAndMembersForm(formData.getShareholderandmembers());
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.fromString(formData.getAccountType().toString());
    }

    @Override
    public String getApplicationOrigin() {
        return ApplicationOriginType.BT_PANORAMA.value(); //considering Direct Onboarding will not use this implementation
    }

    @Override
    public String getAdviceType() {
        return AdviceType.PERSONAL_ADVICE.value(); //considering Direct Onboarding will not use this implementation
    }

    @Override
    public String getTrustType() {
        return hasTrust() ? this.formData.getTrustType().toString() : null;
    }

    @Override
    public String getParentProductName() {
        return this.formData.getParentProductName();
    }


    @Override
    public IFeesForm getFees() {
        return new FeesForm(formData.getFees());
    }

    @Override
    public ILinkedAccountsForm getLinkedAccounts() {
        return new LinkedAccountsForm(formData.getLinkedaccounts());
    }

    @Override
    public ISmsfForm getSmsf() {
        return new SmsfForm(++correlationIndex, formData.getSmsfdetails());
    }

    @Override
    public ICompanyForm getCompanyDetails() {
        return new CompanyForm(++correlationIndex, formData.getCompanydetails());
    }

    @Override
    public boolean hasTrust() {
        return null != formData.getTrustdetails();
    }

    @Override
    public ITrustForm getTrust() {
        final TrustDetails details = formData.getTrustdetails();
        return details == null ? null : new TrustForm(++correlationIndex, details);
    }

    /**
     * Returns false because only Direct Onboarding has this feature.
     *
     * @return
     */
    @Override
    public boolean hasInvestmentChoice() {
        return false;
    }

    @Override
    public IInvestmentChoiceForm getInvestmentChoice() {
        throw new IllegalStateException(
            "this method should not be called for advised applications. Only Direct Onboarding has implemented this method");
    }

    @Override
    public String getAccountName() {
        String acctName;
        switch (this.formData.getAccountType()) {
            case JOINT:
            case SUPER_ACCUMULATION:
            case SUPER_PENSION:
            case INDIVIDUAL:
                acctName = getDisplayNameForJointOrIndividual();
                break;
            case CORPORATE_SMSF:
            case NEW_CORPORATE_SMSF:
            case INDIVIDUAL_SMSF:
            case NEW_INDIVIDUAL_SMSF:
                acctName = formData.getSmsfdetails() != null ? getSmsf().getName() : "";
                break;
            case CORPORATE_TRUST:
            case INDIVIDUAL_TRUST:
                acctName = hasTrust() ? getTrust().getName() : "";
                break;
            case COMPANY:
                acctName = formData.getCompanydetails() != null ? getCompanyDetails().getName() : "";
                break;
            default:
                acctName = "";
        }
        return acctName;
    }

    private String getDisplayNameForJointOrIndividual() {
        List<String> investorNames = Lambda.convert(getGenericPersonDetails(), new Converter<IPersonDetailsForm, String>() {
            @Override
            public String convert(IPersonDetailsForm investor) {
                return StringUtils.join(new String[]{investor.getFirstName(), investor.getLastName()}, ' ').trim();
            }
        });

        return StringUtils.join(investorNames, ", ");
    }

    @Override
    public boolean isDirectAccount() {
        return false; //this will always be FALSE as it will be used for advised accounts only
    }

    @Override
    public ApprovalType getApplicationApprovalType() {
        return ApprovalTypeEnum.OFFLINE.equals(formData.getApprovalType()) ? ApprovalType.OFFLINE
            : ApprovalType.ONLINE;
    }

    @Override
    public IPensionEligibilityForm getPensionEligibility() {
        return formData.getPensioneligibility()!= null ? new PensionEligibilityForm(formData.getPensioneligibility()) : null;
    }
}
