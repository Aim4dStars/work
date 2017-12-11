package com.bt.nextgen.service.btesb.supermatch.model;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.btesb.supermatch.SuperMatchDateTimeConverter;
import com.bt.nextgen.service.integration.supermatch.Member;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Super fund's member details
 */
@ServiceBean(xpath = "Member", type = ServiceBeanType.CONCRETE)
public class MemberImpl implements Member {

    @ServiceElement(xpath = "MemberDetails/CustomerIdentifiers/CustomerIdentifier/CustomerNumberIdentifier/CustomerNumber")
    private String customerId;

    @ServiceElement(xpath = "MemberDetails/CustomerIdentifiers/CustomerIdentifier/CustomerNumberIdentifier/CustomerNumberIssuer")
    private String issuer;

    @ServiceElement(xpath = "MemberDetails/PartyDetails/Individual/GivenName")
    private String firstName;

    @ServiceElement(xpath = "MemberDetails/PartyDetails/Individual/LastName")
    private String lastName;

    @ServiceElement(xpath = "MemberDetails/PartyDetails/Individual/DateOfBirth", converter = SuperMatchDateTimeConverter.class)
    private DateTime dateOfBirth;

    @ServiceElement(xpath = "MemberDetails/EmailAddresses/EmailAddress/EmailAddressDetail/EmailAddress")
    private List<String> emailAddresses;

    @Override
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @Override
    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public DateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(DateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public List<String> getEmailAddresses() {
        return emailAddresses;
    }

    public void setEmailAddresses(List<String> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }
}