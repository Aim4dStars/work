package com.bt.nextgen.api.client.model;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;

import java.util.List;

public class ClientTxnDto extends ClientIdentificationDto {
    private String updatedAttribute;
    private String preferredName;
    private String resiCountryCodeForTax;
    private String resiCountryforTax;
    private boolean registrationForGst;
    private String fullName;
    private String registrationState;
    private String registrationStateCode;
    private String exemptionReason;
    private String tfn;
    private String tfnExemptId;
    private String saTfnExemptId;
    private String modificationSeq;
    private boolean tfnProvided;
    private List<AddressDto> addresses;
    private List<EmailDto> emails;
    private List<PhoneDto> phones;
    private List<TaxResidenceCountriesDto> taxResidenceCountries;
    private String investorTypeUpdated;
    private String cisKey;
    private List<DomainApiErrorDto> warnings;
    private RegisteredStateDto registeredStateDto;

    public String getInvestorTypeUpdated() {
        return investorTypeUpdated;
    }

    public void setInvestorTypeUpdated(String investorTypeUpdated) {
        this.investorTypeUpdated = investorTypeUpdated;
    }

    public String getModificationSeq() {
        return modificationSeq;
    }

    public void setModificationSeq(String modificationSeq) {
        this.modificationSeq = modificationSeq;
    }

    public boolean isTfnProvided() {
        return tfnProvided;
    }

    public void setTfnProvided(boolean tfnProvided) {
        this.tfnProvided = tfnProvided;
    }

    public String getResiCountryforTax() {
        return resiCountryforTax;
    }

    public String getRegistrationStateCode() {
        return registrationStateCode;
    }

    public void setRegistrationStateCode(String registrationStateCode) {
        this.registrationStateCode = registrationStateCode;
    }

    public String getExemptionReason() {
        return exemptionReason;
    }

    public void setExemptionReason(String exemptionReason) {
        this.exemptionReason = exemptionReason;
    }

    public void setResiCountryforTax(String resiCountryforTax) {
        this.resiCountryforTax = resiCountryforTax;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRegistrationState() {
        return registrationState;
    }

    public void setRegistrationState(String registrationState) {
        this.registrationState = registrationState;
    }

    public String getTfn() {
        return tfn;
    }

    public void setTfn(String tfn) {
        this.tfn = tfn;
    }

    public String getTfnExemptId() {
        return tfnExemptId;
    }

    public void setTfnExemptId(String tfnExemptId) {
        this.tfnExemptId = tfnExemptId;
    }

    public String getResiCountryCodeForTax() {
        return resiCountryCodeForTax;
    }

    public void setResiCountryCodeForTax(String resiCountryCodeForTax) {
        this.resiCountryCodeForTax = resiCountryCodeForTax;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public String getUpdatedAttribute() {
        return updatedAttribute;
    }

    public void setUpdatedAttribute(String updatedAttribute) {
        this.updatedAttribute = updatedAttribute;
    }

    public boolean isRegistrationForGst() {
        return registrationForGst;
    }

    public void setRegistrationForGst(boolean registrationForGst) {
        this.registrationForGst = registrationForGst;
    }

    public List<AddressDto> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressDto> addresses) {
        this.addresses = addresses;
    }

    public List<EmailDto> getEmails() {
        return emails;
    }

    public void setEmails(List<EmailDto> emails) {
        this.emails = emails;
    }

    public List<PhoneDto> getPhones() {
        return phones;
    }

    public void setPhones(List<PhoneDto> phones) {
        this.phones = phones;
    }

    public List<DomainApiErrorDto> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<DomainApiErrorDto> warnings) {
        this.warnings = warnings;
    }

    public String getCisKey() {
        return cisKey;
    }

    public void setCisKey(String cisKey) {
        this.cisKey = cisKey;
    }

    public RegisteredStateDto getRegisteredStateDto() {
        return registeredStateDto;
    }

    public void setRegisteredStateDto(RegisteredStateDto registeredStateDto) {
        this.registeredStateDto = registeredStateDto;
    }

    public String getSaTfnExemptId() {
        return saTfnExemptId;
    }

    public void setSaTfnExemptId(String saTfnExemptId) {
        this.saTfnExemptId = saTfnExemptId;
    }

    public List<TaxResidenceCountriesDto> getTaxResidenceCountries() {
        return taxResidenceCountries;
    }

    public void setTaxResidenceCountries(List<TaxResidenceCountriesDto> taxResidenceCountries) {
        this.taxResidenceCountries = taxResidenceCountries;
    }
}
