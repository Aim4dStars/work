package com.bt.nextgen.service.btesb.supermatch;

import com.bt.nextgen.service.integration.supermatch.Member;
import com.bt.nextgen.service.integration.supermatch.SuperFundAccount;
import ns.btfin_com.party.v3_0.CustomerIdentifier;
import ns.btfin_com.party.v3_0.CustomerIdentifiers;
import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;
import ns.btfin_com.party.v3_0.CustomerNoBaseIssuerType;
import ns.btfin_com.party.v3_0.CustomerNumberIdentifier;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.superannuationmatchservice.superannuationmatchrequest.v1_0.IndividualType;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.superannuationmatchservice.superannuationmatchrequest.v1_0.ObjectFactory;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.superannuationmatchservice.superannuationmatchrequest.v1_0.PartyDetailType;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.superannuationmatchservice.superannuationmatchrequest.v1_0.SMChannelDetailType;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.superannuationmatchservice.superannuationmatchrequest.v1_0.SMFundDetailsInvolvedPartyDetailsType;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.superannuationmatchservice.superannuationmatchrequest.v1_0.SMFundsType;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.superannuationmatchservice.superannuationmatchrequest.v1_0.SMRequestContextType;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.superannuationmatchservice.superannuationmatchrequest.v1_0.SMRequestType;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.superannuationmatchservice.superannuationmatchrequest.v1_0.SMRetrieveSearchFilterType;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.superannuationmatchservice.superannuationmatchrequest.v1_0.SMRolloverFundDetailsType;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.superannuationmatchservice.superannuationmatchrequest.v1_0.SMRolloverFundsType;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.superannuationmatchservice.superannuationmatchrequest.v1_0.SMRolloverRequestType;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.superannuationmatchservice.superannuationmatchrequest.v1_0.SMUpsertCustomerIdentifiers;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.superannuationmatchservice.superannuationmatchrequest.v1_0.SMUpsertCustomerNumberIdentifier;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.superannuationmatchservice.superannuationmatchrequest.v1_0.SMUpsertFundsType;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.superannuationmatchservice.superannuationmatchrequest.v1_0.SMUpsertInvolvedPartyDetailsType;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.superannuationmatchservice.superannuationmatchrequest.v1_0.SMUpsertType;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.v1_0.ECOAccountCustomerDetailType;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.v1_0.ECOAccountDetailType;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.v1_0.ECOAccountIdentifiersType;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.v1_0.ECOAccountMaintenanceType;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.v1_0.MemberType;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.v1_0.MembersType;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.v1_0.SuperannuationFundAccountType;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.v1_0.SuperannuationMatchStatusSummaryType;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

/**
 * Request builder for creating supermatch request elements
 */
// suppressed warning about too many dependencies as this is a helper class
@SuppressWarnings({"squid:S1200"})
@Component
public class SuperMatchRequestBuilder {

    private static final String TRACKING_VERSION = "1_0";
    private static final String CHANNEL = "Online";
    private static final String DEVICE = "Desktop";
    private static final String SUB_CHANNEL = "BT-Pano";

    private static final Logger logger = LoggerFactory.getLogger(SuperMatchRequestBuilder.class);

    /**
     * Request to retrieve super fund details
     *
     * @param objectFactory    - Instance of {@link ObjectFactory}
     * @param superFundAccount - Super fund account {@link SuperFundAccount}
     */
    SMRequestType createRequestForRetrieveDetails(ObjectFactory objectFactory, SuperFundAccount superFundAccount) {
        final SMRequestType superMatchRequest = objectFactory.createSMRequestType();
        superMatchRequest.setFundDetails(createFundAccountsRequest(objectFactory, superFundAccount));
        return superMatchRequest;
    }

    private SMFundsType createFundAccountsRequest(ObjectFactory objectFactory, SuperFundAccount superFundAccount) {
        final SMFundsType fundDetails = objectFactory.createSMFundsType();
        final SuperannuationFundAccountType superFundAccountType = new SuperannuationFundAccountType();
        if (superFundAccount != null) {
            superFundAccountType.setAccountNo(superFundAccount.getAccountNumber());
            superFundAccountType.setUSI(superFundAccount.getUsi());
            superFundAccountType.setMembers(createMembers(objectFactory, superFundAccount));
        }

        fundDetails.getSuperFundAccount().add(superFundAccountType);
        return fundDetails;
    }

    private SMUpsertFundsType createFundAccountsRequestForUpdate(ObjectFactory objectFactory, SuperFundAccount superFundAccount) {
        final SMUpsertFundsType fundDetails = objectFactory.createSMUpsertFundsType();

        final SuperannuationFundAccountType superFundAccountType = new SuperannuationFundAccountType();
        if (superFundAccount != null) {
            superFundAccountType.setAccountNo(superFundAccount.getAccountNumber());
            superFundAccountType.setUSI(superFundAccount.getUsi());
            superFundAccountType.setMembers(createMembersForUpdate(objectFactory, superFundAccount));
        }

        fundDetails.getSuperFundAccount().add(superFundAccountType);
        return fundDetails;
    }

    private MembersType createMembers(ObjectFactory objectFactory, SuperFundAccount superFundAccount) {
        final MembersType members = new MembersType();

        for (Member member : superFundAccount.getMembers()) {
            final SMFundDetailsInvolvedPartyDetailsType memberDetails = objectFactory.createSMFundDetailsInvolvedPartyDetailsType();
            memberDetails.setCustomerIdentifiers(createCustomerIdentifierRequest(objectFactory, member));

            final MemberType memberType = new MemberType();
            memberType.setMemberDetails(memberDetails);
            members.getMember().add(memberType);
        }
        return members;
    }

    private MembersType createMembersForUpdate(ObjectFactory objectFactory, SuperFundAccount superFundAccount) {
        final MembersType members = new MembersType();

        for (Member member : superFundAccount.getMembers()) {
            final SMUpsertInvolvedPartyDetailsType memberDetails = objectFactory.createSMUpsertInvolvedPartyDetailsType();
            memberDetails.setCustomerIdentifiers(createCustomerIdentifierRequestForUpdate(objectFactory, member));

            final MemberType memberType = new MemberType();
            memberType.setMemberDetails(memberDetails);
            members.getMember().add(memberType);
        }
        return members;
    }

    private SMUpsertCustomerIdentifiers createCustomerIdentifierRequestForUpdate(ObjectFactory objectFactory, Member member) {
        final SMUpsertCustomerNumberIdentifier customerNumberIdentifier = objectFactory.createSMUpsertCustomerNumberIdentifier();
        final CustomerNoBaseIssuerType baseIssuerType = CustomerNoBaseIssuerType.fromValue(member.getIssuer());
        customerNumberIdentifier.setCustomerNumber(member.getCustomerId());
        customerNumberIdentifier.setCustomerNumberIssuer(CustomerNoAllIssuerType.fromValue(baseIssuerType));

        SMUpsertCustomerIdentifiers customerId = objectFactory.createSMUpsertCustomerIdentifiers();
        final CustomerIdentifier customerIdentifier = new CustomerIdentifier();
        customerIdentifier.setCustomerNumberIdentifier(customerNumberIdentifier);
        customerId.getCustomerIdentifier().add(customerIdentifier);
        return customerId;
    }

    private CustomerIdentifiers createCustomerIdentifierRequest(ObjectFactory objectFactory, Member member) {
        final CustomerNumberIdentifier customerNumberIdentifier = objectFactory.createSMFundDetailsCustomerNumberIdentifier();
        final CustomerNoBaseIssuerType baseIssuerType = CustomerNoBaseIssuerType.fromValue(member.getIssuer());
        customerNumberIdentifier.setCustomerNumber(member.getCustomerId());
        customerNumberIdentifier.setCustomerNumberIssuer(CustomerNoAllIssuerType.fromValue(baseIssuerType));

        final CustomerIdentifiers customerId = objectFactory.createSMFundDetailsCustomerIdentifiers();
        final CustomerIdentifier customerIdentifier = new CustomerIdentifier();
        customerIdentifier.setCustomerNumberIdentifier(customerNumberIdentifier);
        customerId.getCustomerIdentifier().add(customerIdentifier);
        return customerId;
    }

    /**
     * Request to update the Rollover fund details in ECO
     *
     * @param objectFactory    - Instance of {@link ObjectFactory}
     * @param superFundAccount - Super fund account {@link SuperFundAccount}
     * @param rollOverFunds    - List of funds to trigger rollover for
     */
    SMRolloverRequestType createRequestForUpdateRollOver(ObjectFactory objectFactory, SuperFundAccount superFundAccount, List<SuperFundAccount> rollOverFunds) {
        SMRolloverRequestType superMatchRequest = objectFactory.createSMRolloverRequestType();
        superMatchRequest.setFundDetails(createFundAccountsRequest(objectFactory, superFundAccount));
        superMatchRequest.setRolloverFundDetails(createRollOverFundDetailsRequest(objectFactory, rollOverFunds));
        return superMatchRequest;
    }

    private SMRolloverFundsType createRollOverFundDetailsRequest(ObjectFactory objectFactory, List<SuperFundAccount> rollOverFunds) {
        final SMRolloverFundsType smRolloverFunds = objectFactory.createSMRolloverFundsType();
        SMRolloverFundDetailsType rolloverFundDetails;
        for (SuperFundAccount fund : rollOverFunds) {
            rolloverFundDetails = objectFactory.createSMRolloverFundDetailsType();
            rolloverFundDetails.setAccountNo(fund.getAccountNumber());
            rolloverFundDetails.setUSI(fund.getUsi());
            rolloverFundDetails.setRolloverStatus(fund.getRolloverStatus());
            rolloverFundDetails.setRolloverAmount(fund.getRolloverAmount());
            smRolloverFunds.getSuperFundAccount().add(rolloverFundDetails);
        }

        return smRolloverFunds;
    }

    /**
     * Request to update the consent status
     *
     * @param objectFactory    - Instance of {@link ObjectFactory}
     * @param superFundAccount - Super fund account {@link SuperFundAccount}
     */
    SMUpsertType createRequestForUpdateConsent(ObjectFactory objectFactory, SuperFundAccount superFundAccount, Boolean isConsentProvided, String cisId) {
        final SuperannuationMatchStatusSummaryType statusSummaryType = objectFactory.createSMUpsertStatusSummaryType();
        statusSummaryType.setConsentStatusProvidedFlag(isConsentProvided);
        statusSummaryType.setConsentProvidedDevice(DEVICE);
        statusSummaryType.setConsentStatusSubmitter(cisId);

        final SMUpsertType superMatchRequest = objectFactory.createSMUpsertType();
        superMatchRequest.setFundDetails(createFundAccountsRequestForUpdate(objectFactory, superFundAccount));
        superMatchRequest.setStatusSummary(statusSummaryType);
        return superMatchRequest;
    }

    /**
     * Request to update the acknowledgement status in ECO
     *
     * @param objectFactory    - Instance of {@link ObjectFactory}
     * @param superFundAccount - Super fund account {@link SuperFundAccount}
     */
    SMUpsertType createRequestForAcknowledgement(ObjectFactory objectFactory, SuperFundAccount superFundAccount) {
        final SuperannuationMatchStatusSummaryType statusSummaryType = objectFactory.createSMUpsertStatusSummaryType();
        statusSummaryType.setMatchResultAcknowledgedFlag(true);

        final SMUpsertType superMatchRequest = objectFactory.createSMUpsertType();
        superMatchRequest.setFundDetails(createFundAccountsRequestForUpdate(objectFactory, superFundAccount));
        superMatchRequest.setStatusSummary(statusSummaryType);

        return superMatchRequest;
    }

    /**
     * Creates the request context element
     *
     * @param objectFactory - Instance of {@link ObjectFactory}
     * @param cisId         - CIS identifier
     */
    SMRequestContextType createRequestContext(ObjectFactory objectFactory, String cisId) {
        final SMRequestContextType context = objectFactory.createSMRequestContextType();
        context.setVersion(TRACKING_VERSION);
        context.setResponseVersion(TRACKING_VERSION);
        context.setSubmitter(cisId);
        context.setRequester(cisId);
        context.setRequestingSystem(CHANNEL);
        context.setTrackingID(UUID.randomUUID().toString());
        return context;
    }

    /**
     * Creates the filter for retrieve calls
     *
     * @param objectFactory - Instance of {@link ObjectFactory}
     */
    SMRetrieveSearchFilterType createFilterDetails(ObjectFactory objectFactory) {
        final SMRetrieveSearchFilterType filter = objectFactory.createSMRetrieveSearchFilterType();
        filter.setChannelDetails(createChannelDetail(objectFactory));
        return filter;
    }

    /**
     * Creates the channel request
     *
     * @param objectFactory - Instance of {@link ObjectFactory}
     */
    SMChannelDetailType createChannelDetail(ObjectFactory objectFactory) {
        final SMChannelDetailType channel = objectFactory.createSMChannelDetailType();
        channel.setChannel(CHANNEL);
        channel.setSubChannel(SUB_CHANNEL);
        return channel;
    }

    /**
     * Creates the member and account details for creating a new user account in ECO system
     *
     * @param customerId       - customer identifier (CIS key)
     * @param superFundAccount - SuperFundAccount with account and member information
     */
    ECOAccountDetailType createCreateMemberRequest(String customerId, SuperFundAccount superFundAccount) {
        final ECOAccountIdentifiersType accountIdentifiersType = new ECOAccountIdentifiersType();
        accountIdentifiersType.setPanoramaAccountNo(superFundAccount.getAccountNumber());
        accountIdentifiersType.setCustomerUSI(superFundAccount.getUsi());
        accountIdentifiersType.setCustomerCISKey(customerId);

        final ECOAccountCustomerDetailType accountCustomerDetailType = new ECOAccountCustomerDetailType();
        accountCustomerDetailType.setPartyDetails(getPartyDetails(superFundAccount));

        final ECOAccountDetailType accountDetailType = new ECOAccountDetailType();
        accountDetailType.setECOAccountIdentifiers(accountIdentifiersType);
        accountDetailType.setECOAccountMaintenanceInstruction(ECOAccountMaintenanceType.OPEN_ACCOUNT);
        accountDetailType.setECOAccountCustomerDetail(accountCustomerDetailType);

        return accountDetailType;
    }

    private PartyDetailType getPartyDetails(SuperFundAccount superFundAccount) {
        // populate party details
        Member member = superFundAccount.getMembers().get(0);

        final PartyDetailType partyDetails = new PartyDetailType();
        IndividualType individual = new IndividualType();
        individual.setGivenName(member.getFirstName());
        individual.setLastName(member.getLastName());
        individual.setDateOfBirth(convertDate(member.getDateOfBirth()));
        partyDetails.setIndividual(individual);

        return partyDetails;
    }

    private XMLGregorianCalendar convertDate(DateTime inputDate) {
        XMLGregorianCalendar xmlGregorianCalendar = null;
        if (inputDate != null) {
            try {
                final GregorianCalendar gregorianCalendar = new GregorianCalendar();
                gregorianCalendar.setTime(inputDate.toDate());
                xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
            } catch (DatatypeConfigurationException ex) {
                logger.error("Error creating date of birth for the member", ex);
            }
        }
        return xmlGregorianCalendar;
    }
}
