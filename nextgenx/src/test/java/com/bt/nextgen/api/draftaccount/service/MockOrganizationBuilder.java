package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.ExemptionReason;
import com.bt.nextgen.service.integration.domain.IdentityVerificationStatus;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.domain.Organisation;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import org.joda.time.DateTime;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockOrganizationBuilder {
    public static Builder make() {
        return new Builder();
    }

    public static class Builder {
        private Organisation mockOrganisation;
        private Builder() {
            mockOrganisation = mock(Organisation.class);
        }


        public Organisation collect() {
            return mockOrganisation;
        }

        public Builder withResiCountryForTax(String country) {
            when(mockOrganisation.getResiCountryForTax()).thenReturn(country);
            return this;
        }

        public Builder withResiCountryCodeForTax(String countryCode) {
            when(mockOrganisation.getResiCountryCodeForTax()).thenReturn(countryCode);
            return this;
        }

        public Builder withABN(String abn) {
            when(mockOrganisation.getAbn()).thenReturn(abn);
            return this;
        }

        public Builder withIsRegistrationForGst(boolean isRegistrationForGst) {
            when(mockOrganisation.isRegistrationForGst()).thenReturn(isRegistrationForGst);
            return this;
        }

        public Builder withRegistrationDate(DateTime dateTime) {
            when(mockOrganisation.getRegistrationDate()).thenReturn(dateTime.toDate());
            return this;
        }

        public Builder withRegistrationState(String state) {
            when(mockOrganisation.getRegistrationState()).thenReturn(state);
            return this;
        }

        public Builder withRegistrationStateCode(String stateCode) {
            when(mockOrganisation.getRegistrationStateCode()).thenReturn(stateCode);
            return this;
        }

        public Builder withIsRegistrationOnline(boolean isRegistrationOnline) {
            when(mockOrganisation.isRegistrationOnline()).thenReturn(isRegistrationOnline);
            return this;
        }

        public Builder withIsTfnProvided(boolean isTfnProvided) {
            when(mockOrganisation.getTfnProvided()).thenReturn(isTfnProvided);
            return this;
        }

        public Builder withExceptionReason(ExemptionReason exemptionReason) {
            when(mockOrganisation.getExemptionReason()).thenReturn(exemptionReason);
            return this;
        }

        public Builder withIdVerificationStatus(IdentityVerificationStatus status) {
            when(mockOrganisation.getIdVerificationStatus()).thenReturn(status);
            return this;
        }

        public Builder withAnzsicId(String anzscId) {
            when(mockOrganisation.getAnzsicId()).thenReturn(anzscId);
            return this;
        }

        public Builder withIndustry(String industry) {
            when(mockOrganisation.getIndustry()).thenReturn(industry);
            return this;
        }

        public Builder withGcmId(String gcmId) {
            when(mockOrganisation.getGcmId()).thenReturn(gcmId);
            return this;
        }

        public Builder withInvestorType(InvestorType type) {
            when(mockOrganisation.getInvestorType()).thenReturn(type);
            return this;
        }

        public Builder withTfnExemptId(String exemptId) {
            when(mockOrganisation.getTfnExemptId()).thenReturn(exemptId);
            return this;
        }

        public Builder withClientKey(ClientKey key) {
            when(mockOrganisation.getClientKey()).thenReturn(key);
            return this;
        }

        public Builder withFullName(String fullName) {
            when(mockOrganisation.getFullName()).thenReturn(fullName);
            return this;
        }

        public Builder withAddresses(List<Address> addresses) {
            when(mockOrganisation.getAddresses()).thenReturn(addresses);
            return this;
        }

        public Builder withACN(String acn) {
            when(mockOrganisation.getAcn()).thenReturn(acn);
            return this;
        }

        public Builder withAsicName(String asicName) {
            when(mockOrganisation.getAsicName()).thenReturn(asicName);
            return this;
        }

        public Builder withClientType(ClientType clientType) {
            when(mockOrganisation.getClientType()).thenReturn(clientType);
            return this;
        }

        public Builder withLegalForm(InvestorType investorType) {
            when(mockOrganisation.getLegalForm()).thenReturn(investorType);
            return this;
        }

        public Builder withAssociatedRole(InvestorRole investorRole) {
            when(mockOrganisation.getPersonAssociation()).thenReturn(investorRole);
            return this;
        }
    }
}
