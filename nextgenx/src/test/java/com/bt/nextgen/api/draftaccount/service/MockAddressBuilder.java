package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.AddressType;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockAddressBuilder {

    public static Builder make() {
        return new Builder();
    }

    public static class Builder {

        Address mockAddress;

        Builder() {
            mockAddress = mock(Address.class);
        }

        public Address collect() {
            return mockAddress;
        }

        public Builder withStreetNumber(String streetNumber) {
            when(mockAddress.getStreetNumber()).thenReturn(streetNumber);
            return this;
        }

        public Builder withStreetName(String streetName) {
            when(mockAddress.getStreetName()).thenReturn(streetName);
            return this;
        }

        public Builder withStreetTypeId(String streetTypeId) {
            when(mockAddress.getStreetTypeId()).thenReturn(streetTypeId);
            return this;
        }

        public Builder withStreetType(String streetType) {
            when(mockAddress.getStreetType()).thenReturn(streetType);
            return this;
        }

        public Builder withSuburb(String suburb) {
            when(mockAddress.getSuburb()).thenReturn(suburb);
            return this;
        }

        public Builder withState(String state) {
            when(mockAddress.getState()).thenReturn(state);
            return this;
        }

        public Builder withStateAbbr(String stateAbbr) {
            when(mockAddress.getStateAbbr()).thenReturn(stateAbbr);
            return this;
        }

        public Builder withStateCode(String stateCode) {
            when(mockAddress.getStateCode()).thenReturn(stateCode);
            return this;
        }

        public Builder withPostCode(String postCode) {
            when(mockAddress.getPostCode()).thenReturn(postCode);
            return this;
        }

        public Builder withCountryCode(String countryCode) {
            when(mockAddress.getCountryCode()).thenReturn(countryCode);
            return this;
        }

        public Builder withCountry(String country) {
            when(mockAddress.getCountry()).thenReturn(country);
            return this;
        }

        public Builder withAddressType(AddressMedium addressMedium) {
            when(mockAddress.getAddressType()).thenReturn(addressMedium);
            return this;
        }

        public Builder withPostAddress(AddressType addressType) {
            when(mockAddress.getPostAddress()).thenReturn(addressType);
            return this;
        }

        public Builder withStateOther(String stateOther) {
            when(mockAddress.getStateOther()).thenReturn(stateOther);
            return this;
        }

        public Builder withIsDomicile(boolean isDomicile) {
            when(mockAddress.isDomicile()).thenReturn(isDomicile);
            return this;
        }

        public Builder withIsMailingAddress(boolean isMailingAddress) {
            when(mockAddress.isMailingAddress()).thenReturn(isMailingAddress);
            return this;
        }
    }
}
