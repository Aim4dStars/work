package com.bt.nextgen.service.avaloq.insurance.model;

import com.bt.nextgen.core.conversion.BooleanConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.integration.externalasset.builder.DateTimeConverter;
import org.joda.time.DateTime;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@ServiceBean(xpath = "PolicyLifeDetails")
public class PolicyLifeImpl {

    @ServiceElement(xpath = "LifeInsuredDetails/PartyDetails/GivenName")
    private String givenName;

    @ServiceElement(xpath = "LifeInsuredDetails/PartyDetails/LastName")
    private String lastName;

    @ServiceElement(xpath = "DateOfBirth", converter = DateTimeConverter.class)
    private DateTime dateOfBirth;

    @ServiceElement(xpath = "Smoker", converter = BooleanConverter.class)
    private Boolean smokingStatus;

    @ServiceElement(xpath = "LifeInsuredDetails/PostalAddresses/Address[1]/AddressDetail/StructuredAddressDetail/City")
    private String city;

    @ServiceElement(xpath = "LifeInsuredDetails/PostalAddresses/Address[1]/AddressDetail/StructuredAddressDetail/State")
    private String state;

    @ServiceElement(xpath = "LifeInsuredDetails/PostalAddresses/Address[1]/AddressDetail/StructuredAddressDetail/Postcode")
    private String postCode;

    @ServiceElement(xpath = "LifeInsuredDetails/PostalAddresses/Address[1]/AddressDetail/StructuredAddressDetail/CountryCode")
    private String countryCode;

    @ServiceElementList(xpath = "LifeInsuredDetails/PostalAddresses/Address[1]/AddressDetail/StructuredAddressDetail/" +
            "AddressTypeDetail/NonStandardAddress/AddressLine", type = String.class)
    private List<String> addresses = new ArrayList<>();

    @ServiceElementList(xpath = "LifeInsuredDetails/Contacts/Contact/ContactDetail/ContactNumber/" +
            "NonStandardContactNumber", type = String.class)
    private List<String> contactNumbers = new ArrayList<>();

    @Valid
    @ServiceElementList(xpath = "Benefit", type = BenefitsImpl.class)
    private List<BenefitsImpl> benefits;

    @ServiceElementList(xpath = "BenefitOption", type = BenefitOptionsImpl.class)
    private List<BenefitOptionsImpl> benefitOptions;

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public DateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(DateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Boolean isSmokingStatus() {
        return smokingStatus;
    }

    public void setSmokingStatus(Boolean smokingStatus) {
        this.smokingStatus = smokingStatus;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    public List<String> getContactNumbers() {
        return contactNumbers;
    }

    public void setContactNumbers(List<String> contactNumbers) {
        this.contactNumbers = contactNumbers;
    }

    public List<BenefitsImpl> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<BenefitsImpl> benefits) {
        this.benefits = benefits;
    }

    public List<BenefitOptionsImpl> getBenefitOptions() {
        return benefitOptions;
    }

    public void setBenefitOptions(List<BenefitOptionsImpl> benefitOptions) {
        this.benefitOptions = benefitOptions;
    }
}
