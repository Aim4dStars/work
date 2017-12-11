package com.bt.nextgen.api.draftaccount.service;

import java.util.Date;
import java.util.List;

import com.bt.nextgen.api.draftaccount.model.AccountSettingsDto;
import org.apache.commons.lang3.text.WordUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.api.account.v2.model.LinkedAccountDto;
import com.bt.nextgen.api.account.v2.model.PersonRelationDto;
import com.bt.nextgen.api.broker.model.BrokerDto;
import com.bt.nextgen.api.client.model.CompanyDto;
import com.bt.nextgen.api.client.model.InvestorDto;
import com.bt.nextgen.api.client.model.RegisteredEntityDto;
import com.bt.nextgen.api.client.model.SmsfDto;
import com.bt.nextgen.api.client.model.TrustDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.CompanyDetailsDto;
import com.bt.nextgen.api.draftaccount.model.CorporateSmsfApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.CorporateTrustApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.IndividualDirectApplicationsDetailsDto;
import com.bt.nextgen.api.draftaccount.model.IndividualOrJointApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.IndividualSmsfApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.IndividualTrustApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.InvestmentChoiceDto;
import com.bt.nextgen.api.draftaccount.model.PensionEligibility;
import com.bt.nextgen.api.draftaccount.model.SuperPensionApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;

public class ClientApplicationDetailsDtoFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientApplicationDetailsDtoFactory.class);

    public static ClientApplicationDetailsDtoFactory.Builder make() {
        return new Builder();
    }

    public static class Builder {
        private String accountType;
        private String applicationOriginType;
        private String approvalType;
        private boolean isOfflineApprovalAccess;
        private String accountName;
        private AccountSettingsDto accountSettings;
        private List<LinkedAccountDto> linkedAccounts;
        private Object fees;
        private BrokerDto adviser;
        private String status;
        private String referenceNumber;
        private String productName;
        private String parentProductName;
        private String onboardingApplicationKey;
        private String accountKey;
        private String pdsUrl;
        private List<InvestorDto> additionalPersons;
        private RegisteredEntityDto organisationDetails;
        private List<InvestorDto> investorsDirectorsTrustees;
        private boolean nominatedFlag;
        private String majorShareholder;
        private InvestmentChoiceDto investmentChoiceDto;
        private boolean asimProfile;
        private DateTime lastModified;
        private PensionEligibility pensionEligibility;
        private Date applicationOpenDate;

        public Builder withAccountType(String accountType) {
            this.accountType = accountType;
            return this;
        }

        public Builder withApprovalType(String approvalType) {
            this.approvalType = WordUtils.capitalizeFully(approvalType);
            return this;
        }

        public Builder withOfflineApprovalAccess(boolean isOfflineApprovalAccess) {
            this.isOfflineApprovalAccess = isOfflineApprovalAccess;
            return this;
        }

        public Builder withApplicationOriginType(String applicationOriginType) {
            this.applicationOriginType = applicationOriginType;
            return this;
        }

        public Builder withAccountName(String accountName) {
            this.accountName = accountName;
            return this;
        }


        public Builder withAccountSettings(AccountSettingsDto accountSettings) {
            this.accountSettings = accountSettings;
            return this;
        }

        public Builder withLinkedAccounts(List<LinkedAccountDto> linkedAccounts) {
            this.linkedAccounts = linkedAccounts;
            return this;
        }

        public Builder withDraftFees(Object draftFees) {
            this.fees = draftFees;
            return this;
        }

        public Builder withAdviser(BrokerDto brokerDto) {
            this.adviser = brokerDto;
            return this;
        }

        public Builder withAccountAvaloqStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder withReferenceNumber(String referenceNumber) {
            this.referenceNumber = referenceNumber;
            return this;
        }

        public Builder withProductName(String productName) {
            this.productName = productName;
            return this;
        }

        public Builder withParentProductName(String parentProductName) {
            this.parentProductName = parentProductName;
            return this;
        }

        public Builder withMajorShareholderFlag(String majorShareholder) {
            this.majorShareholder = majorShareholder;
            return this;
        }

        public Builder withInvestmentChoice(InvestmentChoiceDto investmentChoiceDto) {
            this.investmentChoiceDto = investmentChoiceDto;
            return this;
        }

        public Builder withLastModifiedAt(DateTime lastModified) {
            this.lastModified = lastModified;
            return  this;
        }

        public Builder withPensionEligibility(PensionEligibility pensionEligibility) {
            this.pensionEligibility = pensionEligibility;
            return  this;
        }

        public Builder withApplicationOpenDate(Date applicationOpenDate){
            this.applicationOpenDate = applicationOpenDate;
            return this;
        }

        @SuppressWarnings({"squid:MethodCyclomaticComplexity", "squid:S1142"})
        public ClientApplicationDetailsDto collect() {
            ClientApplicationDetailsDto clientApplicationDetails;
            switch (IClientApplicationForm.AccountType.fromString(accountType)) {
                case INDIVIDUAL:
                case JOINT:
                case SUPER_ACCUMULATION:
                    clientApplicationDetails = buildSingleOwnerAccount();
                    break;
                case INDIVIDUAL_SMSF:
                case NEW_INDIVIDUAL_SMSF:
                    clientApplicationDetails = buildIndividualSmsf();
                    break;
                case CORPORATE_SMSF:
                case NEW_CORPORATE_SMSF:
                    clientApplicationDetails = buildCorporateSmsf();
                    break;
                case INDIVIDUAL_TRUST:
                    clientApplicationDetails = buildIndividualTrust();
                    break;
                case CORPORATE_TRUST:
                    clientApplicationDetails = buildCorporateTrust();
                    break;
                case COMPANY:
                    clientApplicationDetails = buildCompany();
                    break;
                case SUPER_PENSION:
                    clientApplicationDetails = buildSuperPension();
                    break;
                default:
                    throw new RuntimeException("Unknown account type returned: " + accountType);
            }
            LOGGER.info("ClientApplicationDetails object creation successfully completed");
            return clientApplicationDetails;
        }

        private ClientApplicationDetailsDto buildCommon(ClientApplicationDetailsDto builder) {
            return builder.withOnboardingApplicationKey(onboardingApplicationKey)
                    .withAccountKey(accountKey)
                    .withAccountType(accountType)
                    .withApprovalType(approvalType)
                    .withOfflineApprovalAccess(isOfflineApprovalAccess)
                    .withApplicationOriginType(applicationOriginType)
                    .withAccountName(accountName)
                    .withAccountSettings(accountSettings)
                    .withLinkedAccounts(linkedAccounts)
                    .withFees(fees)
                    .withAdviser(adviser)
                    .withAccountAvaloqStatus(status)
                    .withProductName(productName)
                    .withParentProductName(parentProductName)
                    .withReferenceNumber(referenceNumber)
                    .withPdsUrl(pdsUrl)
                    .withNominatedFlag(nominatedFlag)
                    .withMajorShareHolderFlag(majorShareholder)
                    .withASIMFlag(asimProfile)
                    .withLastModifiedAt(lastModified).withApplicationOpenDate(applicationOpenDate);
        }

        private ClientApplicationDetailsDto buildCompany() {
            return buildCommon(
                    new CompanyDetailsDto()
                            .withDirectorsSecretariesSignatories(investorsDirectorsTrustees)
                            .withCompany((CompanyDto) organisationDetails)
                            .withShareholders(additionalPersons)
            );
        }

        private ClientApplicationDetailsDto buildCorporateTrust() {
            return buildCommon(
                    new CorporateTrustApplicationDetailsDto()
                            .withDirectors(investorsDirectorsTrustees)
                            .withTrust((TrustDto) organisationDetails)
                            .withShareHoldersAndMembers(additionalPersons)
            );
        }

        private ClientApplicationDetailsDto buildIndividualTrust() {
            return buildCommon(
                    new IndividualTrustApplicationDetailsDto()
                            .withTrustees(investorsDirectorsTrustees)
                            .withTrust((TrustDto) organisationDetails)
                            .withShareHoldersAndMembers(additionalPersons)
            );
        }

        private ClientApplicationDetailsDto buildCorporateSmsf() {
            return buildCommon(
                    new CorporateSmsfApplicationDetailsDto()
                            .withDirectors(investorsDirectorsTrustees)
                            .withSmsf((SmsfDto) organisationDetails)
                            .withShareholdersAndMembers(additionalPersons)
            );
        }

        private ClientApplicationDetailsDto buildIndividualSmsf() {
            return buildCommon(
                    new IndividualSmsfApplicationDetailsDto()
                            .withTrustees(investorsDirectorsTrustees)
                            .withSmsf((SmsfDto) organisationDetails)
                            .withMembers(additionalPersons)
            );
        }

        private ClientApplicationDetailsDto buildSingleOwnerAccount() {
            if (applicationOriginType.equals(IClientApplicationForm.ApplicationOriginType.BT_PANORAMA.value())) {
                return buildCommon(
                        new IndividualOrJointApplicationDetailsDto()
                                .withInvestors(investorsDirectorsTrustees)
                );
            } else {
                return buildCommon(
                        new IndividualDirectApplicationsDetailsDto()
                                .withInvestors(investorsDirectorsTrustees)
                                .withInvestmentChoice(investmentChoiceDto)
                );
            }
        }

        private ClientApplicationDetailsDto buildSuperPension() {
            return buildCommon(
                new SuperPensionApplicationDetailsDto()
                    .withPensionEligibility(pensionEligibility)
                    .withInvestors(investorsDirectorsTrustees)
            );
        }

        public Builder withOnboardingApplicationKey(String onboardingApplicationKey) {
            this.onboardingApplicationKey = onboardingApplicationKey;
            return this;
        }

        public Builder withAccountKey(String accountKey) {
            this.accountKey = accountKey;
            return this;
        }

        public Builder withPdsUrl(String pdsUrl) {
            this.pdsUrl = pdsUrl;
            return this;
        }

        public Builder withAdditionalPersons(List<InvestorDto> additionalPersons) {
            this.additionalPersons = additionalPersons;
            return this;
        }

        public Builder withOrganisationDetails(RegisteredEntityDto organisationDetails) {
            this.organisationDetails = organisationDetails;
            return this;
        }

        public Builder withInvestorsDirectorsTrustees(List<InvestorDto> investorsDirectorsTrustees) {
            this.investorsDirectorsTrustees = investorsDirectorsTrustees;
            return this;
        }

        public Builder withNominatedFlag(boolean nominatedFlag) {
            this.nominatedFlag = nominatedFlag;
            return this;
        }

        public Builder withASIMFlag(boolean asimProfile) {
            this.asimProfile = asimProfile;
            return this;
        }


    }
}

