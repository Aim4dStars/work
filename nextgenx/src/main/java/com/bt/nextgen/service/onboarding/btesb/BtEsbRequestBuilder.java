/**
 *
 */
package com.bt.nextgen.service.onboarding.btesb;

import com.bt.nextgen.service.onboarding.ProvisionMFADeviceRequest;
import com.btfin.panorama.core.security.Roles;
import com.bt.nextgen.service.onboarding.CreateAccountRequest;
import com.bt.nextgen.service.onboarding.FirstTimeRegistrationRequest;
import com.bt.nextgen.service.onboarding.ResendRegistrationEmailRequest;
import com.bt.nextgen.service.onboarding.ValidatePartyAndSmsAction;
import com.bt.nextgen.service.onboarding.ValidatePartyRequest;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.bt.nextgen.serviceops.util.AustralianMobileNumberFormatter;
import com.bt.nextgen.serviceops.util.MobileNumberFormatter;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import ns.btfin_com.party.v3_0.CustomerIdentifier;
import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;
import ns.btfin_com.party.v3_0.CustomerNumberIdentifier;
import ns.btfin_com.party.v3_0.IndividualType;
import ns.btfin_com.party.v3_0.PartyDetailType;
import ns.btfin_com.product.panorama.application.v1_0.ApplicationOriginType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.AliasCredentialDetailsType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.CREDAddressDetailType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.CREDAddressType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.CREDDeviceDetailsType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.CREDPostCodeType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.CREDValidatePartyRegistrationDetailType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.CREDValidatePartyRegistrationInvolvedPartyType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.CREDValidatePartySMSDetailType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.CREDValidatePartySMSInvolvedPartyType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.CREDValidateRegistrationIndividualType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.CREDValidateSMSIndividualType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.CreateOTPCommunicationDetailsType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.CreateOTPExistingCustomerIdentifierType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.CreateOTPPartyDetailsType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.CreateOneTimePasswordSendEmailRequestMsgType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.ExistingCustomerIdentifiersType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.NonStandardContactNumberType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.OTPCredentialDetailsType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.OneExistingCustomerIdentifiers;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.PMFAPartyDetails;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.POACommunicationDetailsType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.POAContactDetailType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.POAContactType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.POAContactsType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.POAEmailAddressDetailType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.POAEmailAddressType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.POAEmailAddressesType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.POAIntContactDetailType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.POAIntContactType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.POAIntContactsType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.POAIntInvolvedPartyDetailsType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.POAInvolvedPartyDetailsType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.POARoleTypeType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.PartyDetailsType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.ProvisionMFAMobileDeviceRequestMsgType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.ProvisionOnlineAccessRequestMsgType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.ValidatePartyRegistrationRequestMsgType;
import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ProvisionMFAMobileDeviceResponseMsgType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.CredentialDetailsType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.OBAddressDetailType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.OBAddressType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.OBDeviceDetailsType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.OBPostCodeType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.OBValidateIndividualType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.OBValidatePartyAndSMSOneTimePasswordChallengeInvolvedPartyType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.OBValidatePartyDetailType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.ValidatePartySMSOneTimePasswordChallengeRequestMsgType;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactDetailType;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactNumberType;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactNumberTypeCode;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactType;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactsType;
import ns.btfin_com.sharedservices.common.contact.v1_1.EmailAddressDetailType;
import ns.btfin_com.sharedservices.common.contact.v1_1.EmailAddressType;
import ns.btfin_com.sharedservices.common.contact.v1_1.EmailAddressesType;
import ns.btfin_com.sharedservices.common.contact.v1_1.StandardContactNumberType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.CommunicationMediumType.EMAIL;
import static ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.POARoleTypeType.ADVISER;
import static ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.POARoleTypeType.CLIENT;
import static ns.btfin_com.sharedservices.common.contact.v1_1.ContactNumberTypeCode.MOBILE;
import static ns.btfin_com.sharedservices.common.contact.v1_1.ContactNumberTypeCode.PHONE;

/**
 * @author L055011
 */
public class BtEsbRequestBuilder {
    public static final String AU_COUNTRY_DIAL_CODE = AustralianMobileNumberFormatter.COUNTRY_CODE_PREFIX;

    private static final Logger LOGGER = LoggerFactory.getLogger(BtEsbRequestBuilder.class);

    private static ns.btfin_com.sharedservices.common.contact.v1_1.ObjectFactory contactObjectFactory = OnboardingObjectFactory.getContactobjectfactory();

    private final MobileNumberFormatter mobileNumberFormatter = new AustralianMobileNumberFormatter();

    /**
     * method to build a ProvisionOnlineAccessRequestMsgType (adviser version) from POJO request.
     *
     * @return ProvisionOnlineAccessRequestMsgType formatted for advisers.
     */
    public ProvisionOnlineAccessRequestMsgType build(CreateAccountRequest request) {
        ProvisionOnlineAccessRequestMsgType message = new ProvisionOnlineAccessRequestMsgType();
        message.setPartyDetails(getPartyDetails(request.getCustomerIdentifiers(), POARoleTypeType.ADVISER));
        message.setCommunicationDetails(communicationDetails(request));
        return message;
    }

    /**
     * method to build a CreateOneTimePasswordSendEmailRequestMsgType request from a pojo request
     *
     * @param request the request to be sent.
     * @return CreateOneTimePasswordAndSendEmailRequestMsgType
     */
    public CreateOneTimePasswordSendEmailRequestMsgType build(ResendRegistrationEmailRequest request) {
        CreateOneTimePasswordSendEmailRequestMsgType message = new CreateOneTimePasswordSendEmailRequestMsgType();
        message.setPartyDetails(getOtpPartyDetails(request));
        message.setCommunicationDetails(communicationDetails(request));
        return message;
    }

    /**
     * @param registrationRequest
     * @return
     */
    public ValidatePartySMSOneTimePasswordChallengeRequestMsgType build(FirstTimeRegistrationRequest registrationRequest) {
        ValidatePartySMSOneTimePasswordChallengeRequestMsgType request = new ValidatePartySMSOneTimePasswordChallengeRequestMsgType();
        request.setInvolvedParty(involvedParty(registrationRequest));
        request.setDeviceDetails(deviceDetails(registrationRequest));
        return request;
    }

    public ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.ValidatePartySMSOneTimePasswordChallengeRequestMsgType buildRegistrationDetails(FirstTimeRegistrationRequest request) {

        ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.ValidatePartySMSOneTimePasswordChallengeRequestMsgType registationRequest =
                new ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.ValidatePartySMSOneTimePasswordChallengeRequestMsgType();
        registationRequest.setInvolvedParty(involvedPartyType(request));
        registationRequest.setDeviceDetails(deviceDetailsType(request));
        return registationRequest;
    }

    private CREDValidatePartySMSInvolvedPartyType involvedPartyType(FirstTimeRegistrationRequest registrationRequest) {

        CREDValidatePartySMSInvolvedPartyType partyType = new CREDValidatePartySMSInvolvedPartyType();
        final CREDValidatePartySMSDetailType partyDetails = new CREDValidatePartySMSDetailType();
        final CREDValidateSMSIndividualType individual = new CREDValidateSMSIndividualType();
        individual.setLastName(registrationRequest.getLastName());
        partyDetails.setIndividual(individual);
        partyType.setPartyDetails(partyDetails);
        final CREDAddressType address = new CREDAddressType();
        final CREDAddressDetailType detail = new CREDAddressDetailType();
        final CREDPostCodeType structured = new CREDPostCodeType();
        structured.setPostcode(registrationRequest.getPostalCode());
        detail.setStructuredAddressDetail(structured);
        address.setAddressDetail(detail);
        partyType.setPostalAddress(address);

        final AliasCredentialDetailsType credentials = new AliasCredentialDetailsType();
        credentials.setUserAlias(registrationRequest.getUserName());
        partyType.setCredentialDetails(credentials);

        return partyType;
    }

    private CREDDeviceDetailsType deviceDetailsType(FirstTimeRegistrationRequest request) {
        final CREDDeviceDetailsType device = new CREDDeviceDetailsType();
        device.setDevicePrint(request.getDeviceToken());
        final HttpRequestParams params = request.getHttpRequestParams();
        if (params != null) {
            device.setDeviceTokenCookie("");
            device.setHTTPAccept(params.getHttpAccept());
            device.setHTTPAcceptChars(params.getHttpAcceptChars());
            device.setHTTPAcceptEncoding(params.getHttpAcceptEncoding());
            device.setHTTPAcceptLanguage(params.getHttpAcceptLanguage());
            device.setHTTPReferrer(params.getHttpReferrer());
            device.setIPAddress(params.getHttpOriginatingIpAddress());
            device.setUserAgent(params.getHttpUserAgent());
        }
        return device;
    }


    public ValidatePartyRegistrationRequestMsgType buildValidatePartyRequest(ValidatePartyRequest credentialsRequest) {
        ValidatePartyRegistrationRequestMsgType request = new ValidatePartyRegistrationRequestMsgType();

        CREDValidatePartyRegistrationInvolvedPartyType involvedPartyType = new CREDValidatePartyRegistrationInvolvedPartyType();

        CREDAddressType address = new CREDAddressType();
        CREDAddressDetailType addressType = new CREDAddressDetailType();
        addressType.setStructuredAddressDetail(new CREDPostCodeType());
        addressType.getStructuredAddressDetail().setPostcode(credentialsRequest.getPostalCode());
        address.setAddressDetail(addressType);

        involvedPartyType.setPostalAddress(address);
        involvedPartyType.setCredentialDetails(new OTPCredentialDetailsType());
        involvedPartyType.getCredentialDetails().setOneTimePassword(credentialsRequest.getRegistrationCode());

        involvedPartyType.setPartyDetails(new CREDValidatePartyRegistrationDetailType());
        CREDValidateRegistrationIndividualType individual = new CREDValidateRegistrationIndividualType();
        individual.setLastName(credentialsRequest.getLastName());
        involvedPartyType.getPartyDetails().setIndividual(individual);

        request.setInvolvedParty(involvedPartyType);

        return request;
    }

    private OBValidatePartyAndSMSOneTimePasswordChallengeInvolvedPartyType involvedParty(FirstTimeRegistrationRequest request) {
        final OBValidatePartyAndSMSOneTimePasswordChallengeInvolvedPartyType party = new OBValidatePartyAndSMSOneTimePasswordChallengeInvolvedPartyType();
        final OBValidatePartyDetailType partyDetails = new OBValidatePartyDetailType();
        final OBValidateIndividualType individual = new OBValidateIndividualType();
        individual.setLastName(request.getLastName());
        partyDetails.setIndividual(individual);
        party.setPartyDetails(partyDetails);

        final OBAddressType address = new OBAddressType();
        final OBAddressDetailType detail = new OBAddressDetailType();
        final OBPostCodeType structured = new OBPostCodeType();
        structured.setPostcode(request.getPostalCode());
        detail.setStructuredAddressDetail(structured);
        address.setAddressDetail(detail);
        party.setPostalAddress(address);

        final CredentialDetailsType credentials = new CredentialDetailsType();
        final ValidatePartyAndSmsAction action = request.getAction();
        if (action != null) {
            switch (action) {
                case REGISTRATION:
                    credentials.setOneTimePassword(request.getRegistrationCode());
                    break;
                case FORGOT_PASSWORD:
                    credentials.setUserAlias(request.getUserName());
                    break;
            }
        }
        party.setCredentialDetails(credentials);
        return party;
    }

    private OBDeviceDetailsType deviceDetails(FirstTimeRegistrationRequest request) {
        final OBDeviceDetailsType device = new OBDeviceDetailsType();
        device.setDevicePrint(request.getDeviceToken());
        final HttpRequestParams params = request.getHttpRequestParams();
        if (params != null) {
            device.setDeviceTokenCookie("");
            device.setHTTPAccept(params.getHttpAccept());
            device.setHTTPAcceptChars(params.getHttpAcceptChars());
            device.setHTTPAcceptEncoding(params.getHttpAcceptEncoding());
            device.setHTTPAcceptLanguage(params.getHttpAcceptLanguage());
            device.setHTTPReferrer(params.getHttpReferrer());
            device.setIPAddress(params.getHttpOriginatingIpAddress());
            device.setUserAgent(params.getHttpUserAgent());
        }
        return device;
    }

    private PartyDetailsType getPartyDetails(Map<CustomerNoAllIssuerType, String> customerIdentifiers, POARoleTypeType roleType) {
        final PartyDetailsType details = new PartyDetailsType();
        ExistingCustomerIdentifiersType existingCustomerIdentifier = new ExistingCustomerIdentifiersType();
        existingCustomerIdentifier.getCustomerIdentifier().addAll(fetchCustomerIdentifierList(customerIdentifiers,roleType));
        details.setCustomerIdentifiers(existingCustomerIdentifier);
        details.setRoleType(roleType);
        return details;
    }

    private CreateOTPPartyDetailsType getOtpPartyDetails(Map<CustomerNoAllIssuerType, String> customerIdentifiers, POARoleTypeType roleType) {
        final CreateOTPPartyDetailsType details = new CreateOTPPartyDetailsType();

        CreateOTPExistingCustomerIdentifierType existingCustomerIdentifier = new CreateOTPExistingCustomerIdentifierType();
        CustomerNumberIdentifier customerNumberIdentifier = new CustomerNumberIdentifier();
        customerNumberIdentifier.setCustomerNumber(customerIdentifiers.get(CustomerNoAllIssuerType.BT_PANORAMA));
        customerNumberIdentifier.setCustomerNumberIssuer(CustomerNoAllIssuerType.BT_PANORAMA);
        existingCustomerIdentifier.setCustomerNumberIdentifier(customerNumberIdentifier);
        details.setCustomerIdentifier(existingCustomerIdentifier);
        details.setRoleType(roleType);
        return details;
    }

    private PartyDetailsType getPartyDetails(ResendRegistrationEmailRequest request) {
        final boolean client = request.getPersonRole() == Roles.ROLE_INVESTOR;
        final POARoleTypeType role = client ? CLIENT : ADVISER;
        return getPartyDetails(request.getCustomerIdentifiers(), role);
    }

    private CreateOTPPartyDetailsType getOtpPartyDetails(ResendRegistrationEmailRequest request) {
        final boolean client = request.getPersonRole() == Roles.ROLE_INVESTOR;
        final POARoleTypeType role = client ? CLIENT : ADVISER;
        return getOtpPartyDetails(request.getCustomerIdentifiers(), role);
    }

    private CustomerIdentifier getCustomerIdentifier(String id, CustomerNoAllIssuerType type) {
        CustomerIdentifier customerIdentifier = new CustomerIdentifier();
        CustomerNumberIdentifier customerNumberIdentifier = new CustomerNumberIdentifier();
        customerNumberIdentifier.setCustomerNumber(id);
        customerNumberIdentifier.setCustomerNumberIssuer(type);
        customerIdentifier.setCustomerNumberIdentifier(customerNumberIdentifier);
        return customerIdentifier;
    }

    private PartyDetailType getNameDetails(String firstName, String lastName) {
        final PartyDetailType party = new PartyDetailType();
        party.setIndividual(individual(firstName, lastName));
        return party;
    }

    private POACommunicationDetailsType communicationDetails(CreateAccountRequest request) {
        final POACommunicationDetailsType communication = new POACommunicationDetailsType();
        communication.setCommunicationMedium(EMAIL);

        // Set adviser details
        final POAIntInvolvedPartyDetailsType adviserDetails = new POAIntInvolvedPartyDetailsType();
        adviserDetails.setPartyDetails(getNameDetails(request.getFirstName(), request.getLastName()));
        adviserDetails.setEmailAddresses(emailAddresses(getEmailAddressType(request.getPrimaryEmailAddress())));
        adviserDetails.setContacts(contacts(nonStandardContactNumber(request.getPrimaryMobileNumber(), MOBILE)));
        communication.setIntermediaryDetails(adviserDetails);
        return communication;
    }

    private CreateOTPCommunicationDetailsType communicationDetails(ResendRegistrationEmailRequest request) {
        final CreateOTPCommunicationDetailsType communicationDetails = new CreateOTPCommunicationDetailsType();
        communicationDetails.setCommunicationMedium(EMAIL);
        if (request.getPersonRole() == Roles.ROLE_INVESTOR) {
            communicationDetails.setInvestorDetails(investorDetails(request));
        }
        communicationDetails.setIntermediaryDetails(intermediaryDetails(request));
        return communicationDetails;
    }

    private POAInvolvedPartyDetailsType investorDetails(ResendRegistrationEmailRequest request) {
        final POAInvolvedPartyDetailsType investor = new POAInvolvedPartyDetailsType();
        investor.setPartyDetails(getNameDetails(request.getInvestorFirstName(), request.getInvestorLastName()));
        investor.setEmailAddresses(emailAddresses(request.getInvestorPrimaryEmailAddress()));
        investor.setContacts(contacts(investorContact(request)));
        return investor;
    }

    private POAIntInvolvedPartyDetailsType intermediaryDetails(ResendRegistrationEmailRequest request) {
        final POAIntInvolvedPartyDetailsType intermediary = new POAIntInvolvedPartyDetailsType();
        intermediary.setPartyDetails(getNameDetails(request.getAdviserFirstName(), request.getAdviserLastName()));
        intermediary.setEmailAddresses(emailAddresses(request.getAdviserPrimaryEmailAddress()));
        intermediary.setContacts(contacts(adviserContact(request)));
        return intermediary;
    }

    private EmailAddressesType emailAddresses(EmailAddressType emails) {
        final EmailAddressesType emailAddresses = new EmailAddressesType();
        emailAddresses.getEmailAddress().addAll(asList(emails));
        return emailAddresses;
    }

    protected POAEmailAddressesType emailAddresses(String... emails) {
        POAEmailAddressesType emailAddresses = new POAEmailAddressesType();
        for (String email : emails) {
            emailAddresses.getEmailAddress().add(emailAddress(email));
        }
        return emailAddresses;
    }

    private POAEmailAddressType emailAddress(String email) {
        final POAEmailAddressType emailAddress = new POAEmailAddressType();
        final POAEmailAddressDetailType emailAddressDetail = new POAEmailAddressDetailType();
        emailAddressDetail.setEmailAddress(email);
        emailAddress.setEmailAddressDetail(contactObjectFactory.createEmailAddressTypeEmailAddressDetail(emailAddressDetail));
        return emailAddress;
    }

    private POAContactsType contacts(POAContactType... contacts) {
        final POAContactsType result = new POAContactsType();
        result.getContact().addAll(asList(contacts));
        return result;
    }

    private POAIntContactsType contacts(POAIntContactType... contacts) {
        final POAIntContactsType result = new POAIntContactsType();
        result.getContact().addAll(asList(contacts));
        return result;
    }

    private ContactsType contacts(ContactType... contacts) {
        final ContactsType result = new ContactsType();
        result.getContact().addAll(asList(contacts));
        return result;
    }

    private POAContactType investorContact(ResendRegistrationEmailRequest request) {
        return contact(request.getInvestorPrimaryContactNumber(), request.getInvestorPrimaryContactNumberType());
    }

    private POAIntContactType adviserContact(ResendRegistrationEmailRequest request) {
        final POAIntContactType contact = new POAIntContactType();
        final POAIntContactDetailType detail = new POAIntContactDetailType();
        final NonStandardContactNumberType number = new NonStandardContactNumberType();
        number.setNonStandardContactNumber(request.getAdviserPrimaryContactNumber());
        detail.setContactNumber(number);
        detail.setContactNumberType(contactNumberType(request.getAdviserPrimaryContactNumberType()));
        contact.setContactDetail(detail);
        return contact;
    }

    private ContactNumberTypeCode contactNumberType(String type) {
        return Attribute.MOBILE.equalsIgnoreCase(type) ? MOBILE : PHONE;
    }

    private ContactNumberType standardContactNumber(String fullNumber) {
        String countryCode;
        String areaCode;
        String subscriber;
        try {
            final String formattedNumber = mobileNumberFormatter.formatMobileNumber(fullNumber);
            countryCode = formattedNumber.substring(0, 2);
            areaCode = formattedNumber.substring(2, 3);
            subscriber = formattedNumber.substring(3);
        } catch (IllegalArgumentException iae) {
            LOGGER.warn("Unable to format number to country/area/subscriber components: {}", fullNumber, iae);
            countryCode = AU_COUNTRY_DIAL_CODE;
            areaCode = "2";
            subscriber = fullNumber;
        }
        return standardContactNumber(countryCode, areaCode, subscriber);
    }

    private ContactNumberType standardContactNumber(String countryCode, String areaCode, String subscriber) {
        final StandardContactNumberType standard = new StandardContactNumberType();
        standard.setCountryCode(countryCode);
        standard.setAreaCode(areaCode);
        standard.setSubscriberNumber(subscriber);

        final ContactNumberType number = new ContactNumberType();
        number.setStandardContactNumber(standard);
        return number;
    }

    /**
     * Private method to form jaxb contact type, with a non-standard contact number.
     *
     * @param number
     * @param contactNumberType
     * @return ContactType
     */
    private ContactType nonStandardContactNumber(String number, ContactNumberTypeCode contactNumberType) {
        final ContactNumberType contactNumber = new ContactNumberType();
        contactNumber.setNonStandardContactNumber(number);
        return contact(contactNumber, contactNumberType);
    }

    private POAContactType contact(String contactNumber, String contactNumberType) {
        final POAContactType contact = new POAContactType();
        final POAContactDetailType detail = new POAContactDetailType();
        detail.setContactNumber(standardContactNumber(contactNumber));
        detail.setContactNumberType(contactNumberType(contactNumberType));
        contact.setContactDetail(detail);
        return contact;
    }

    private ContactType contact(ContactNumberType contactNumber, ContactNumberTypeCode contactNumberType) {
        final ContactType contact = new ContactType();
        final ContactDetailType detail = new ContactDetailType();
        detail.setContactNumber(contactNumber);
        detail.setContactNumberType(contactNumberType);
        contact.setContactDetail(detail);
        return contact;
    }

    /**
     * private method to form jaxb EmailAddressType
     *
     * @param primaryEmailAddress
     * @return EmailAddressType
     */
    private EmailAddressType getEmailAddressType(String primaryEmailAddress) {
        EmailAddressType emailAddressType = new EmailAddressType();
        if (StringUtils.isNotEmpty(primaryEmailAddress)) {
            EmailAddressDetailType emailAddressDetailType = new EmailAddressDetailType();
            emailAddressDetailType.setEmailAddress(primaryEmailAddress);
            emailAddressType.setEmailAddressDetail(contactObjectFactory.createEmailAddressTypeEmailAddressDetail(emailAddressDetailType));
        }
        return emailAddressType;
    }

    /**
     * private method to form jaxb IndividualType
     *
     * @param firstName
     * @param lastName
     * @return IndividualType
     */
    private IndividualType individual(String firstName, String lastName) {
        IndividualType individualType = new IndividualType();
        individualType.setGivenName(firstName);
        individualType.setLastName(lastName);
        return individualType;
    }

    /**
     * method to build a ProceesAdviserRequestMesgType from pojo request
     *
     * @param request
     * @return ProcessAdvisersRequestMsgType
     */
    public ProvisionOnlineAccessRequestMsgType buildInvestorProcessRequest(ResendRegistrationEmailRequest request) {
        final ProvisionOnlineAccessRequestMsgType message = new ProvisionOnlineAccessRequestMsgType();
        message.setPartyDetails(getPartyDetails(request.getCustomerIdentifiers(), CLIENT));

        POACommunicationDetailsType communicationDetails = new POACommunicationDetailsType();
        communicationDetails.setCommunicationMedium(EMAIL);

        //Set Investor Details
        POAInvolvedPartyDetailsType investorDetails = new POAInvolvedPartyDetailsType();
        investorDetails.setPartyDetails(getNameDetails(request.getInvestorFirstName(), request.getInvestorLastName()));
        investorDetails.setEmailAddresses(emailAddresses(getEmailAddressType(request.getInvestorPrimaryEmailAddress())));
        investorDetails.setContacts(contacts(contact(standardContactNumber(request.getInvestorPrimaryContactNumber()), MOBILE)));
        communicationDetails.setInvestorDetails(investorDetails);

        //set adviser details
        POAIntInvolvedPartyDetailsType adviserDetails = new POAIntInvolvedPartyDetailsType();
        adviserDetails.setPartyDetails(getNameDetails(request.getAdviserFirstName(), request.getAdviserLastName()));
        adviserDetails.setEmailAddresses(emailAddresses(getEmailAddressType(request.getAdviserPrimaryEmailAddress())));
        adviserDetails.setContacts(contacts(nonStandardContactNumber(request.getAdviserPrimaryContactNumber(), MOBILE)));
        communicationDetails.setIntermediaryDetails(adviserDetails);
        message.setCommunicationDetails(communicationDetails);
        return message;
    }


    public ProvisionMFAMobileDeviceRequestMsgType buildProvisionMFADeviceRequest(ProvisionMFADeviceRequest provisionMFADeviceRequest){

        final ProvisionMFAMobileDeviceRequestMsgType provisionMFAMobileDeviceRequestMsgType = new ProvisionMFAMobileDeviceRequestMsgType();
        provisionMFAMobileDeviceRequestMsgType.setPartyDetails(getPmfaPartyDetails(provisionMFADeviceRequest.getCustomerIdentifiers(), POARoleTypeType.CLIENT));
        provisionMFAMobileDeviceRequestMsgType.setMobileNumber(provisionMFADeviceRequest.getPrimaryMobileNumber());
        provisionMFAMobileDeviceRequestMsgType.setMFACanonicalProductCode(provisionMFADeviceRequest.getCanonicalProductCode());
        provisionMFAMobileDeviceRequestMsgType.setApplicationOrigin(ApplicationOriginType.BT_PANORAMA);
        return provisionMFAMobileDeviceRequestMsgType;
    }


    private PMFAPartyDetails getPmfaPartyDetails(Map<CustomerNoAllIssuerType, String> customerIdentifiers, POARoleTypeType roleType) {
        final PMFAPartyDetails details = new PMFAPartyDetails();
        OneExistingCustomerIdentifiers oneExistingCustomerIdentifier = new OneExistingCustomerIdentifiers();
        oneExistingCustomerIdentifier.getCustomerIdentifier().addAll(fetchCustomerIdentifierList(customerIdentifiers,roleType));
        details.setCustomerIdentifiers(oneExistingCustomerIdentifier);
        details.setRoleType(roleType);
        return details;
    }

    public List<CustomerIdentifier> fetchCustomerIdentifierList(Map<CustomerNoAllIssuerType, String> customerIdentifiers,POARoleTypeType roleType){
        List<CustomerIdentifier> customerIdentifiersList = new ArrayList<>();
        customerIdentifiersList.add(getCustomerIdentifier(customerIdentifiers.get(CustomerNoAllIssuerType.BT_PANORAMA), CustomerNoAllIssuerType.BT_PANORAMA));
        String cisKey = customerIdentifiers.get(CustomerNoAllIssuerType.WESTPAC_LEGACY);
        String Znumber= customerIdentifiers.get(CustomerNoAllIssuerType.WESTPAC);
        if (CLIENT == roleType && !(null==cisKey || cisKey.isEmpty())) {
            customerIdentifiersList.add(getCustomerIdentifier(customerIdentifiers.get(CustomerNoAllIssuerType.WESTPAC_LEGACY), CustomerNoAllIssuerType.WESTPAC_LEGACY));
        }
        if(CLIENT == roleType && !(null==Znumber || Znumber.isEmpty())){
            customerIdentifiersList.add(getCustomerIdentifier(customerIdentifiers.get(CustomerNoAllIssuerType.WESTPAC), CustomerNoAllIssuerType.WESTPAC));
        }
          return customerIdentifiersList;
    }


}