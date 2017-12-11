package com.bt.nextgen.service.group.customer.groupesb.state;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.ExemptionReason;
import com.bt.nextgen.service.integration.domain.Gender;
import com.bt.nextgen.service.integration.domain.IdentityVerificationStatus;
import com.btfin.panorama.core.security.integration.domain.InvestorDetail;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.domain.PensionExemptionReason;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.domain.RegisteredEntity;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@SuppressWarnings("squid:S1200")
public class CustomerRegisteredState implements RegisteredEntity {

    private String abn;

    private boolean registrationForGst;
    private ClientKey clientKey;
    private String registrationType;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    private DateTime registrationDate;

    private String country;

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    private String registrationState;


    private String registrationStateCode;


    private boolean registrationOnline;

    private String registrationNumber;

    public void setRegistrationState(String registrationState) {
        this.registrationState = registrationState;
    }

    public void setRegistrationStateCode(String registrationStateCode) {
        this.registrationStateCode = registrationStateCode;
    }

    public void setRegistrationDate(DateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public void setAbn(String abn) {
        this.abn = abn;
    }

    private List<InvestorDetail> linkedClients = new ArrayList<>();

    private String asicName;


    /**
     * Returns the ABN of the Person
     *
     * @return String
     */
    @Override
    public String getAbn() {
        return this.abn;
    }

    /**
     * Returns the Registration Date of the Person
     *
     * @return Date
     */
    @Override
    public Date getRegistrationDate() {
        return this.registrationDate.toDate();
    }

    /**
     * Returns the Registration State of the Person
     *
     * @return String
     */
    @Override
    public String getRegistrationState() {
        return this.registrationState;
    }

    /**
     * Returns the Registration state code of the Person
     *
     * @return String
     */
    @Override
    public String getRegistrationStateCode() {
        return this.registrationStateCode;
    }

    /**
     * Returns boolean based on whether the Person is registered for GST
     *
     * @return boolean
     */
    @Override
    public boolean isRegistrationForGst() {
        return registrationForGst;
    }

    /**
     * Retruns the linked clients of the Person
     *
     * @return List <Investor>
     */
    @Override
    public List<InvestorDetail> getLinkedClients() {
        return this.linkedClients;
    }

    /**
     * Setter method for linked clients of the Person
     *
     * @param linkedClient
     */
    @Override
    public void addLinkedClients(InvestorDetail linkedClient) {
        this.linkedClients.add(linkedClient);
    }


    /**
     * Returns boolean for Registration Online
     *
     * @return boolean
     */
    @Override
    public boolean isRegistrationOnline() {
        return this.registrationOnline;
    }

    @Override
    public String getTitle() {
        return null;
    }

    /**
     * Returns the ASIC name of the trust/company
     *
     * @return String
     */
    @Override
    public String getAsicName() {
        return this.asicName;
    }


    /**
     * Returns boolean based on whether TFN exists for the assocaited Person
     *
     * @return boolean
     */
    @Override
    public boolean getTfnProvided() {
        return false;
    }

    /**
     * Returns the exemption reason associated with the Person
     *
     * @return ExemptionReason
     */
    @Override
    public ExemptionReason getExemptionReason() {
        return null;
    }

    /**
     * Returns the identify verification status of the Person
     *
     * @return IdentityVerificationStatus
     */
    @Override
    public IdentityVerificationStatus getIdVerificationStatus() {
        return null;
    }

    /**
     * Returns the ANZSIC id of the Person
     *
     * @return String
     */
    @Override
    public String getAnzsicId() {
        return null;
    }

    /**
     * Returns the person association (Role) of the Person
     *
     * @return PersonAssociation
     */
    @Override
    public InvestorRole getPersonAssociation() {
        return null;
    }

    /**
     * Returns the person association (Role) of the Person
     *
     * @return PersonAssociation
     */
    @Override
    public List<InvestorRole> getPersonRoles() {
        return Collections.emptyList();
    }

    /**
     * Returns the open date of the person account associated with the Person
     *
     * @return DateTime
     */
    @Override
    public DateTime getOpenDate() {
        return null;
    }



    /**
     * Returns the modification sequence number of the address
     *
     * @return String
     */
    @Override
    public String getModificationSeq() {
        return null;
    }

    /**
     * Returns the type of Investor
     *
     * @return InvestorType
     */
    @Override
    public InvestorType getInvestorType() {
        return null;
    }

    /**
     * Returns TFN number of Investor
     *
     * @return InvestorType
     */
    @Override
    public String getTfn() {
        return null;
    }

    /**
     * Returns TFN exempt id of Investor
     *
     * @return InvestorType
     */
    @Override
    public String getTfnExemptId() {
        return null;
    }

    /**
     * Returns Industry value for Anzsic code returned from Avaloq
     *
     * @return String industry
     */
    @Override
    public String getIndustry() {
        return null;
    }

    @Override
    public String getWestpacCustomerNumber() {
        return null;
    }

    @Override
    public String getSaTfnExemptId() {
        return null;
    }

    @Override
    public PensionExemptionReason getPensionExemptionReason() {
        return null;
    }

    /**
     * @return String
     * @deprecated Need to use BankingCustomerIdentifier which provides GCM ID , CIS Key.
     * Returns the Gcm id of the Person
     */
    @Override
    @Deprecated
    public String getGcmId() {
        return null;
    }

    @Override
    public List<ClientKey> getAssociatedPersonKeys() {
        return Collections.emptyList();
    }

    @Override
    public IdentityVerificationStatus getIdentityVerificationStatus() {
        return null;
    }

    /**
     * @return The GCM Id in the bank
     */
    @Override
    public String getBankReferenceId() {
        return null;
    }

    /**
     * @Return the UserKey which will have the underlying value of the bankId
     */
    @Override
    public UserKey getBankReferenceKey() {
        return null;
    }

    /**
     * @Return the CISKey
     */
    @Override
    public CISKey getCISKey() {
        return null;
    }

    @Override
    public Collection<AccountKey> getWrapAccounts() {
        return new ArrayList<AccountKey>();
    }

    @Override
    public Collection<ClientDetail> getRelatedPersons() {
        return new ArrayList<ClientDetail>();
    }

    /**
     * Returns the closed date if the person is closed
     *
     * @return DateTime
     */
    @Override
    public DateTime getCloseDate() {
        return null;
    }

    @Override
    public ClientType getClientType() {
        return null;
    }

    @Override
    public List<Address> getAddresses() {
        return new ArrayList<Address>();
    }

    @Override
    public InvestorType getLegalForm() {
        return null;
    }

    @Override
    public List<Email> getEmails() {
        return new ArrayList<Email>();
    }

    @Override
    public List<Phone> getPhones() {
        return new ArrayList<Phone>();
    }

    @Override
    public int getAge() {
        return 0;
    }

    @Override
    public Gender getGender() {
        return null;
    }

    @Override
    public DateTime getDateOfBirth() {
        return null;
    }

    @Override
    public String getFullName() {
        return null;
    }

    @Override
    public String getFirstName() {
        return null;
    }

    @Override
    public String getLastName() {
        return null;
    }

    @Override
    public ClientKey getClientKey() {
        return clientKey;
    }

    @Override
    public void setClientKey(ClientKey personId) {
        this.clientKey = personId;
    }

    @Override
    public String getSafiDeviceId() {
        return null;
    }


    public String getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(String registrationType) {
        this.registrationType = registrationType;
    }

    @Override
    public String getResiCountryForTax() {
        return null;
    }

    @Override
    public String getResiCountryCodeForTax() {
        return null;
    }

    @Override
    public List<TaxResidenceCountry> getTaxResidenceCountries() {
        return null;
    }

    @Override
    public String getBrandSiloId() {
        return null;
    }
}
