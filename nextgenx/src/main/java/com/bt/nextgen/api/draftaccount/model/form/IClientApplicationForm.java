package com.bt.nextgen.api.draftaccount.model.form;

import java.util.List;

/**
 * Created by m040398 on 15/03/2016.
 */
public interface IClientApplicationForm {
    List<IExtendedPersonDetailsForm> getInvestors();
    List<IExtendedPersonDetailsForm> getDirectors();
    List<IExtendedPersonDetailsForm> getDirectorsSecretariesSignatories();
    List<IPersonDetailsForm> getGenericPersonDetails();
    List<IPersonDetailsForm> getExistingPersonDetails();
    List<IExtendedPersonDetailsForm> getTrustees();
    List<IExtendedPersonDetailsForm> getAdditionalShareholdersAndMembers();
    ICompanyForm getCompanyTrustee();
    IAccountSettingsForm getAccountSettings();
    IShareholderAndMembersForm getShareholderAndMembers();
    AccountType getAccountType();
    String getApplicationOrigin();
    String getAdviceType();
    String getTrustType();
    IFeesForm getFees();
    ILinkedAccountsForm getLinkedAccounts();
    ISmsfForm getSmsf();
    ICompanyForm getCompanyDetails();
    boolean hasTrust();
    ITrustForm getTrust();
    ApprovalType getApplicationApprovalType();
    IPensionEligibilityForm getPensionEligibility();
    String getParentProductName();

    /**
     * @deprecated
     * To be removed after Direct Onboarding is implementing their own JSON schemas
     *
     * @return
     */
    @Deprecated
    boolean hasInvestmentChoice();

    /**
     * @deprecated
     * This method is being used only for Direct Onboarding and should be removed in the future from this interface
     *
     * @return
     */
    @Deprecated
    IInvestmentChoiceForm getInvestmentChoice();

    String getAccountName();

    boolean isDirectAccount();

    enum AccountType {
        INDIVIDUAL("individual", "investors"),
        JOINT("joint", "investors"),
        SUPER_ACCUMULATION("superAccumulation","investors"),
        SUPER_PENSION("superPension","investors"),
        CORPORATE_SMSF("corporateSMSF", "directors"),
        INDIVIDUAL_SMSF("individualSMSF", "trustees"),
        NEW_INDIVIDUAL_SMSF("newIndividualSMSF", "trustees"),
        NEW_CORPORATE_SMSF("newCorporateSMSF", "directors"),
        CORPORATE_TRUST("corporateTrust", "directors"),
        INDIVIDUAL_TRUST("individualTrust", "trustees"),
        COMPANY("company", "directors"),
        UNKNOWN("unknown", "unknown");

        private final String val;

        private final String personsSectionName;

        AccountType(String val, String personsSectionName) {
            this.val = val;
            this.personsSectionName = personsSectionName;
        }

        public static AccountType fromString(String text) {
            for (AccountType t : AccountType.values()) {
                if (t.val.equalsIgnoreCase(text)) {
                    return t;
                }
            }

            throw new IllegalArgumentException("Invalid account type: " + text);
        }

        public String value() {
            return val;
        }

        public String getPersonsSectionName() {
            return personsSectionName;
        }
    }

    enum AdviceType {
        FACTUAL_INFORMATION("FactualInformation"),
        GENERAL_ADVICE("GeneralAdvice"),
        PERSONAL_ADVICE("PersonalAdvice"),
        NO_ADVICE("NoAdvice");

        private final String adviceTypeValue;

        AdviceType(String value) {
            this.adviceTypeValue = value;
        }

        public static AdviceType fromString(String text) {
            for (AdviceType t : AdviceType.values()) {
                if (t.adviceTypeValue.equalsIgnoreCase(text)) {
                    return t;
                }
            }

            throw new IllegalArgumentException("Invalid advice type: " + text);
        }

        public String value() {
            return adviceTypeValue;
        }
    }

    enum ApplicationOriginType {
        WESTPAC_LIVE("WestpacLive"),
        BT_PANORAMA("BTPanorama");


        private final String applicationOriginTypeValue;

        ApplicationOriginType(String value) {
            this.applicationOriginTypeValue = value;
        }

        public static ApplicationOriginType fromString(String text) {
            for (ApplicationOriginType t : ApplicationOriginType.values()) {
                if (t.applicationOriginTypeValue.equalsIgnoreCase(text)) {
                    return t;
                }
            }

            throw new IllegalArgumentException("Invalid ApplicationOrigin type: " + text);
        }

        public String value() {
            return applicationOriginTypeValue;
        }
    }

    enum ApprovalType {
        OFFLINE("offline"),
        ONLINE("online");

        private final String approvalType;

        ApprovalType(String value) {
            this.approvalType = value;
        }

        public static ApprovalType fromString(String text) {
            for (ApprovalType t : ApprovalType.values()) {
                if (t.approvalType.equalsIgnoreCase(text)) {
                    return t;
                }
            }

            throw new IllegalArgumentException("Invalid ApprovalType type: " + text);
        }

        public String value() {
            return approvalType;
        }
    }

}
