package com.bt.nextgen.api.draftaccount.model.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;
import org.apache.commons.lang.StringUtils;

import com.bt.nextgen.api.draftaccount.FormDataConstants;
import com.bt.nextgen.core.util.LambdaMatcher;

/**
 * @deprecated Should be using the v1 version of this class instead.
 */
@Deprecated
@SuppressWarnings({ "squid:S1200", "squid:MethodCyclomaticComplexity" })
class ClientApplicationForm implements IClientApplicationForm {



    private final Map<String, Object> mappedValues;
    private final AccountType accountType;

    public ClientApplicationForm(Map<String, Object> formData) {
        this.mappedValues = formData;
        accountType = AccountType.fromString((String) mappedValues.get("accountType"));
    }

    public List<IExtendedPersonDetailsForm> getInvestors() {
        List<IExtendedPersonDetailsForm> investorDetailsList = new ArrayList<>();
        List<Map<String, Object>> investors = (List<Map<String, Object>>) mappedValues.get("investors");
        IAccountSettingsForm accountSettingsForm = getAccountSettings();
        if (investors != null) {
            for (int i = 0; i < investors.size(); i++) {
                Map<String, Object> investor = investors.get(i);
                PaymentAuthorityEnum paymentSetting = accountSettingsForm.getPaymentSettingForInvestor(i);
                investorDetailsList.add(new ExtendedPersonDetailsForm(investor, accountSettingsForm.isPrimaryContact(i), paymentSetting, true));
            }
        }
        return investorDetailsList;
    }

    public List<IExtendedPersonDetailsForm> getDirectors() {
        List<IExtendedPersonDetailsForm> personDetailsList = new ArrayList<>();
        List<Map<String, Object>> persons = (List<Map<String, Object>>) mappedValues.get("directors");
        IAccountSettingsForm accountSettingsForm = getAccountSettings();
        ShareholderAndMembersForm shareholderAndMembersForm = (ShareholderAndMembersForm) getShareholderAndMembers();
        Integer directorWithSecretaryRole = StringUtils.isNotBlank(shareholderAndMembersForm.getCompanySecretaryValue()) ? Integer.parseInt(shareholderAndMembersForm.getCompanySecretaryValue()) : null;

        if(persons != null) {
            for (int i = 0; i < persons.size(); i++) {
                Map<String, Object> investorWithRole = (Map<String, Object>) shareholderAndMembersForm.getInvestorsWithRoles().get(i);
                personDetailsList.add(new DirectorDetailsForm(persons.get(i), accountSettingsForm.isPrimaryContact(i), accountSettingsForm.getPaymentSettingForInvestor(i),
                        accountSettingsForm.getApproverSettingForInvestor(i), "yes".equals(investorWithRole.get("isShareholder")), "yes".equals(investorWithRole.get("isMember")),
                        	"yes".equals(investorWithRole.get("isBeneficiary")), "yes".equals(investorWithRole.get("isBeneficialOwner")),
                        directorWithSecretaryRole != null && directorWithSecretaryRole == i, IOrganisationForm.OrganisationRole.DIRECTOR));
            }
        }
        return personDetailsList;
    }

    public List<IExtendedPersonDetailsForm> getDirectorsSecretariesSignatories() {
        List<IExtendedPersonDetailsForm> personDetailsList = new ArrayList<>();
        List<Map<String, Object>> persons = (List<Map<String, Object>>) mappedValues.get("directors");
        IAccountSettingsForm accountSettingsForm = getAccountSettings();
        ShareholderAndMembersForm shareholderAndMembersForm = (ShareholderAndMembersForm)getShareholderAndMembers();
        Integer directorWithSecretaryRole = StringUtils.isNotBlank(shareholderAndMembersForm.getCompanySecretaryValue()) ? Integer.parseInt(shareholderAndMembersForm.getCompanySecretaryValue()) : null;

        if(persons != null) {
            for (int i = 0; i < persons.size(); i++) {
                Map<String, Object> investorWithRole = (Map<String, Object>) shareholderAndMembersForm.getInvestorsWithRoles().get(i);
                personDetailsList.add(new DirectorDetailsForm(persons.get(i), accountSettingsForm.isPrimaryContact(i), accountSettingsForm.getPaymentSettingForInvestor(i),
                        accountSettingsForm.getApproverSettingForInvestor(i), "yes".equals(investorWithRole.get("isShareholder")), "yes".equals(investorWithRole.get("isMember")),
                        "yes".equals(investorWithRole.get("isBeneficiary")), "yes".equals(investorWithRole.get("isBeneficialOwner")),
                        directorWithSecretaryRole != null && directorWithSecretaryRole == i, accountSettingsForm.getRoleSettingForInvestor(i)));
            }
        }
        return personDetailsList;
    }

    public List<IPersonDetailsForm> getGenericPersonDetails() {
        final List<IPersonDetailsForm> personList = new ArrayList<>();
        final List<Map<String, Object>> person = (List<Map<String, Object>>) mappedValues.get(accountType.getPersonsSectionName());
        if (person != null) {
            for (int i = 0; i < person.size(); i++) {
                Map<String, Object> investor = person.get(i);
                personList.add(new PersonDetailsForm(investor));
            }
        }
        return personList;
    }

    public List<IPersonDetailsForm> getExistingPersonDetails() {
        return Lambda.filter(new LambdaMatcher<PersonDetailsForm>() {
            @Override
            protected boolean matchesSafely(PersonDetailsForm personDetailsForm) {
                return personDetailsForm.isExistingPerson();
            }
        }, getGenericPersonDetails());
    }

    public List<IExtendedPersonDetailsForm> getTrustees() {
        List<IExtendedPersonDetailsForm> personDetailsList = new ArrayList<>();
        List<Map<String, Object>> persons = (List<Map<String, Object>>) mappedValues.get("trustees");
        IAccountSettingsForm accountSettingsForm = getAccountSettings();
        ShareholderAndMembersForm membersForm = (ShareholderAndMembersForm)getShareholderAndMembers();

        if(persons != null) {
            for (int i = 0; i < persons.size(); i++) {
                Map<String, Object> investorWithRole = (Map<String, Object>) membersForm.getInvestorsWithRoles().get(i);
                TrusteeDetailsForm trustee = new TrusteeDetailsForm(
                        persons.get(i),
                        accountSettingsForm.isPrimaryContact(i),
                        accountSettingsForm.getPaymentSettingForInvestor(i),
                        accountSettingsForm.getApproverSettingForInvestor(i),
                        "yes".equals(investorWithRole.get("isMember")),
                        "yes".equals(investorWithRole.get("isBeneficiary"))
                );
                personDetailsList.add(trustee);
            }
        }
        return personDetailsList;
    }

    public List<IExtendedPersonDetailsForm> getAdditionalShareholdersAndMembers() {
        ShareholderAndMembersForm shareholderAndMembersForm = (ShareholderAndMembersForm) getShareholderAndMembers();
        return shareholderAndMembersForm.getAdditionalShareholdersAndMembers();
    }

    public ICompanyForm getCompanyTrustee() {
        return new CompanyForm((Map<String, Object>) this.mappedValues.get("companytrustee"));
    }

    public IAccountSettingsForm getAccountSettings() {
        return new AccountSettingsForm((Map<String, Object>) mappedValues.get("accountsettings"));
    }

    public IShareholderAndMembersForm getShareholderAndMembers() {
        return new ShareholderAndMembersForm((Map<String, Object>) mappedValues.get("shareholderandmembers"));
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public String getApplicationOrigin() {
        String applicationOrigin = (String) mappedValues.get(FormDataConstants.FIELD_APPLICATION_ORIGIN);
        if(!StringUtils.isEmpty(applicationOrigin)) {
            return ApplicationOriginType.fromString(applicationOrigin).value();
        }
        return ApplicationOriginType.BT_PANORAMA.value();
    }

    public String getAdviceType() {
        String adviceType = (String) mappedValues.get(FormDataConstants.FIELD_ADVICE_TYPE);
        if(!StringUtils.isEmpty(adviceType)) {
            return AdviceType.fromString(adviceType).value();
        }
        return AdviceType.PERSONAL_ADVICE.value();
    }

    public String getTrustType() {
        return (String) mappedValues.get("trustType");
    }

    public IFeesForm getFees() {
        return new FeesForm((Map<String, Object>) mappedValues.get("fees"));
    }

    public ILinkedAccountsForm getLinkedAccounts() {
        return new LinkedAccountsForm((Map<String, Object>) mappedValues.get("linkedaccounts"));
    }

    private boolean hasSmsf() {
        return mappedValues.get("smsfdetails") != null;
    }

    public ISmsfForm getSmsf() {
        return new SmsfForm((Map<String, Object>) mappedValues.get("smsfdetails"));
    }

    private boolean hasCompanyDetails() {
        return mappedValues.get("companydetails") != null;
    }

    public ICompanyForm getCompanyDetails() {
        return new CompanyForm((Map<String, Object>) this.mappedValues.get("companydetails"));
    }

    public boolean hasTrust() {
        return mappedValues.get("trustdetails") != null;
    }

    public ITrustForm getTrust() {
        return new TrustForm((Map<String, Object>) mappedValues.get("trustdetails"));
    }

    @Override
    public ApprovalType getApplicationApprovalType() {
        if (mappedValues.get("approvalType") == null){
            return ApprovalType.ONLINE;
        }
        return ApprovalType.fromString((String) mappedValues.get(FormDataConstants.FIELD_APPROVAL_TYPE));
    }

    public boolean hasInvestmentChoice(){
        return mappedValues.get(FormDataConstants.FIELD_INVESTMENTOPTIONS) != null;
    }

    public IInvestmentChoiceForm getInvestmentChoice() {
        return new InvestmentChoiceForm((Map<String, Object>) mappedValues.get(FormDataConstants.FIELD_INVESTMENTOPTIONS));
    }

    public String getAccountName() {
        switch (accountType) {
            case JOINT:
            case SUPER_ACCUMULATION:
            case SUPER_PENSION:
            case INDIVIDUAL:
                return getDisplayNameForJointOrIndividual();
            case CORPORATE_SMSF:
            case NEW_CORPORATE_SMSF:
            case INDIVIDUAL_SMSF:
            case NEW_INDIVIDUAL_SMSF:
                return hasSmsf()? getSmsf().getName() : "";
            case CORPORATE_TRUST:
            case INDIVIDUAL_TRUST:
                return hasTrust() ? getTrust().getName() : "";
            case COMPANY:
                return  hasCompanyDetails() ? getCompanyDetails().getName() : "";
            default:
                return "";
        }
    }

    private String getDisplayNameForJointOrIndividual() {
        List<String> investorNames = Lambda.convert(getGenericPersonDetails(), new Converter<PersonDetailsForm, String>() {
            @Override
            public String convert(PersonDetailsForm investor) {
                return StringUtils.join(new String[]{investor.getFirstName(), investor.getLastName()}, ' ').trim();
            }
        });

        return StringUtils.join(investorNames, ", ");
    }

    public boolean isDirectAccount() {
        return getApplicationOrigin().equals(ApplicationOriginType.WESTPAC_LIVE.value()) && getAdviceType().equals(AdviceType.NO_ADVICE.value());
    }

    //Pension eligibility doesn't exist for old json format
    @Override
    public IPensionEligibilityForm getPensionEligibility() {
        return null;
    }

    //ParentProductName doesn't exist for old json format
    @Override
    public String getParentProductName() {
        return null;
    }
}
