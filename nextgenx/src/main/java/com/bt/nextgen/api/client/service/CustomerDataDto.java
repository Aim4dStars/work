package com.bt.nextgen.api.client.service;

import com.bt.nextgen.api.account.v2.model.BankAccountDto;
import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.ClientUpdateKey;
import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.api.client.model.RegisteredStateDto;
import com.bt.nextgen.api.client.model.TaxResidenceCountriesDto;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.group.customer.groupesb.IndividualDetails;

import java.util.List;

/**
 * Created by L070815 on 30/07/2015.
 */
public class CustomerDataDto extends BaseDto implements KeyedDto<ClientUpdateKey> {



    //GCM data below (using GESB services call)
    private String cisKey;
    private String updatedAttribute;
    private String preferredName;
    private AddressDto address;
    private ClientUpdateKey key;
    private List<EmailDto> emails;
    private List<PhoneDto> phones;
    private List<BankAccountDto> bankAccounts;
    private RegisteredStateDto registeredStateDto;
    private Boolean hasGcmEnabled;
    private IndividualDetails individualDetails;
    private List<TaxResidenceCountriesDto> taxResidenceCountries;
    private String tfn;
    private boolean hasSuperCheck; // Indicates whether the TFN came through SuperCheck

    //Panorama details below (if existing Panorama -> setup using ABS services call)
    private PanoramaCustomerDto panoramaDetails;

    public List<EmailDto> getEmails() {
        return emails;
    }

    public void setEmails(List<EmailDto> emails) {
        this.emails = emails;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    @Override
    public ClientUpdateKey getKey() {
        return key;
    }

    public void setKey(ClientUpdateKey key) {
        this.key = key;
    }

    public RegisteredStateDto getRegisteredStateDto() {
        return registeredStateDto;
    }

    public void setRegisteredStateDto(RegisteredStateDto registeredStateDto) {
        this.registeredStateDto = registeredStateDto;
    }

    public Boolean getHasGcmEnabled() {
        return hasGcmEnabled;
    }

    public void setHasGcmEnabled(Boolean hasGcmEnabled) {
        this.hasGcmEnabled = hasGcmEnabled;
    }

    public List<BankAccountDto> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<BankAccountDto> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    public List<PhoneDto> getPhones() {
        return phones;
    }

    public void setPhones(List<PhoneDto> phones) {
        this.phones = phones;
    }

    public IndividualDetails getIndividualDetails() {
        return individualDetails;
    }

    public void setIndividualDetails(IndividualDetails individualDetails) {
        this.individualDetails = individualDetails;
    }

    public List<TaxResidenceCountriesDto> getTaxResidenceCountries() {
        return taxResidenceCountries;
    }

    public void setTaxResidenceCountries(List<TaxResidenceCountriesDto> taxResidenceCountries) {
        this.taxResidenceCountries = taxResidenceCountries;
    }

    public String getTfn() {
        return tfn;
    }

    public void setTfn(String tfn) {
        this.tfn = tfn;
    }

    public PanoramaCustomerDto getPanoramaDetails() {
        return panoramaDetails;
    }

    public void setPanoramaDetails(PanoramaCustomerDto panoramaDetails) {
        this.panoramaDetails = panoramaDetails;
    }

    public boolean isHasSuperCheck() {
        return hasSuperCheck;
    }

    public void setHasSuperCheck(boolean hasSuperCheck) {
        this.hasSuperCheck = hasSuperCheck;
    }
    public String getUpdatedAttribute() {
        return updatedAttribute;
    }

    public void setUpdatedAttribute(String updatedAttribute) {
        this.updatedAttribute = updatedAttribute;
    }
    public String getCisKey() {
        return cisKey;
    }

    public void setCisKey(String cisKey) {
        this.cisKey = cisKey;
    }


}
