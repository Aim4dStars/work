package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.service.avaloq.account.AlternateNameImpl;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.bt.nextgen.service.avaloq.account.AccountAuthoriser;
import com.btfin.panorama.core.security.avaloq.userinformation.PersonRelationship;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.ExemptionReason;
import com.bt.nextgen.service.integration.domain.Gender;
import com.bt.nextgen.service.integration.domain.IdentityVerificationStatus;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.domain.PersonDetail;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry;
import org.joda.time.DateTime;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.powermock.api.mockito.PowerMockito;

public class MockPersonDetailBuilder {

    public static Builder make() {

        return new Builder();
    }

    public static class Builder {
        PersonDetail mockPersonDetail;
        public Builder() {
            mockPersonDetail = mock(PersonDetail.class);
        }

        public PersonDetail collect() {
            return mockPersonDetail;
        }

        public Builder withPrimaryRole(PersonRelationship role) {
            when(mockPersonDetail.getPrimaryRole()).thenReturn(role);
            return this;
        }

        public Builder withTitle(String title) {
            when(mockPersonDetail.getTitle()).thenReturn(title);
            return this;
        }

        public Builder withFirstName(String firstName) {
            when(mockPersonDetail.getFirstName()).thenReturn(firstName);
            return this;
        }

        public Builder withLastName(String lastName) {
            when(mockPersonDetail.getLastName()).thenReturn(lastName);
            return this;
        }

        public Builder withDateOfBirth(DateTime dateTime) {
            when(mockPersonDetail.getDateOfBirth()).thenReturn(dateTime);
            return this;
        }

        public Builder withGender(Gender gender) {
            when(mockPersonDetail.getGender()).thenReturn(gender);
            return this;
        }

        public Builder withResiCountryForTax(String country) {
            when(mockPersonDetail.getResiCountryForTax()).thenReturn(country);
            return this;
        }

        public Builder withResiCountryCodeForTax(String code) {
            when(mockPersonDetail.getResiCountryCodeForTax()).thenReturn(code);
            return this;
        }

        public Builder withExemptionReason(ExemptionReason exemption) {
            when(mockPersonDetail.getExemptionReason()).thenReturn(exemption);
            return this;
        }

        public Builder withIdentityVerificationStatus(IdentityVerificationStatus status) {
            when(mockPersonDetail.getIdentityVerificationStatus()).thenReturn(status);
            return this;
        }

        public Builder withGcmId(String gcmId) {
            when(mockPersonDetail.getGcmId()).thenReturn(gcmId);
            return this;
        }

        public Builder withInvestorType(InvestorType investorType) {
            when(mockPersonDetail.getInvestorType()).thenReturn(investorType);
            return this;
        }

        public Builder withClientKey(ClientKey clientKey) {
            when(mockPersonDetail.getClientKey()).thenReturn(clientKey);
            return this;
        }

        public Builder withClientType(ClientType clientType) {
            when(mockPersonDetail.getClientType()).thenReturn(clientType);
            return this;
        }

        public Builder withPersonAssociation(InvestorRole investorRole) {
            when(mockPersonDetail.getPersonAssociation()).thenReturn(investorRole);
            return this;
        }

        public Builder withIsPrimaryContact(boolean isPrimaryContact) {
            when(mockPersonDetail.isPrimaryContact()).thenReturn(isPrimaryContact);
            return this;
        }

        public Builder withIsApprover(boolean isApprover) {
            when(mockPersonDetail.isApprover()).thenReturn(isApprover);
            return this;
        }

        public Builder withIsMember(boolean isMember) {
            when(mockPersonDetail.isMember()).thenReturn(isMember);
            return this;
        }

        public Builder withIsBeneficiary(boolean isBeneficiary) {
            when(mockPersonDetail.isBeneficiary()).thenReturn(isBeneficiary);
            return this;
        }

        public Builder withIsShareHolder(boolean isShareHolder) {
            when(mockPersonDetail.isShareholder()).thenReturn(isShareHolder);
            return this;
        }

        public Builder withAnzsicId(String anzsicId) {
            when(mockPersonDetail.getAnzsicId()).thenReturn(anzsicId);
            return this;
        }

        public Builder withIndustry(String industry) {
            when(mockPersonDetail.getIndustry()).thenReturn(industry);
            return this;
        }

        public Builder withExemptId(String exemptId) {
            when(mockPersonDetail.getTfnExemptId()).thenReturn(exemptId);
            return this;
        }

        public Builder withAddresses(List<Address> address) {
            when(mockPersonDetail.getAddresses()).thenReturn(address);
            return this;
        }

        public Builder withPhones(List<Phone> phones) {
            when(mockPersonDetail.getPhones()).thenReturn(phones);
            return this;
        }

        public Builder withEmails(List<Email> emails) {
            when(mockPersonDetail.getEmails()).thenReturn(emails);
            return this;
        }

        public Builder withAccountAuthorisationList(List<AccountAuthoriser> accountAuthorisers) {
            when(mockPersonDetail.getAccountAuthorisationList()).thenReturn(accountAuthorisers);
            return this;
        }

        public Builder withAccountAlternateNameList(List<AlternateNameImpl> alternateNames) {
            when(mockPersonDetail.getAlternateNameList()).thenReturn(alternateNames);
            return this;
        }

        public Builder withCountryOfResidence(String countryOfResidence){
            PowerMockito.when(mockPersonDetail.getResiCountryForTax()).thenReturn(countryOfResidence);
            return this;
        }

        public Builder withTaxResidenceCountries(List<TaxResidenceCountry> taxResidenceCountryList){
            when(mockPersonDetail.getTaxResidenceCountries()).thenReturn(taxResidenceCountryList);
            return this;
        }
    }
}
