package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.api.client.service.ClientListDtoService;
import com.bt.nextgen.api.draftaccount.builder.CreateOneTimePasswordSendEmailRequestMsgTypeBuilder;
import com.bt.nextgen.core.repository.OnboardingCommunication;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.user.UserKey;

import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.*;
import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ResendExistingRegistrationCodeResponseMsgType;
import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ResendRegistrationCodeResponseDetailsErrorResponseType;
import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ResendRegistrationCodeResponseDetailsSuccessResponseType;
import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.StatusTypeCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;

import java.util.Date;
import java.util.List;

/**
 * Email request builder that constructs version 3.0 messages.
 */
@Component(ResendExistingRegistrationCodeSendEmailRequestMsgTypeBuilderV3.V3_BEAN_NAME)
@Qualifier("resendRegistrationCodeSendEmailRequestMsgTypeBuilder")
@Lazy
public class ResendExistingRegistrationCodeSendEmailRequestMsgTypeBuilderV3 implements CreateOneTimePasswordSendEmailRequestMsgTypeBuilder<ResendExistingRegistrationCodeRequestMsgType, ResendExistingRegistrationCodeResponseMsgType, ResendRegistrationCodeResponseDetailsErrorResponseType> {

    public static final String V3_BEAN_NAME = "resendRegistrationCodeSendEmailRequestMsgTypeBuilderV3";

    @Autowired
    private ClientListDtoService clientListDtoService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private POAPartyDetailsTypeBuilder poaPartyDetailsTypeBuilder;

    @Override
    @SuppressWarnings("deprecation")
    public ResendExistingRegistrationCodeRequestMsgType build(String clientId, String adviserPositionId, String role, ServiceErrors serviceErrors) {
        final ClientKey clientKey = new ClientKey(EncodedString.fromPlainText(clientId).toString());
        final IndividualDto individualDto = (IndividualDto) clientListDtoService.find(clientKey, serviceErrors);
        final BrokerUser brokerUser = brokerIntegrationService.getAdviserBrokerUser(BrokerKey.valueOf(adviserPositionId), serviceErrors);

        final CreateOTPPartyDetailsType partyDetails = new CreateOTPPartyDetailsType();
        
        //----
        partyDetails.setRoleType(POARoleTypeType.CLIENT);
        
        CreateOTPExistingCustomerIdentifierTypeBuilder createOTPExistingCustomerIdentifierTypeBuilder = new CreateOTPExistingCustomerIdentifierTypeBuilder();
        createOTPExistingCustomerIdentifierTypeBuilder.setId(individualDto.getGcmId());
        createOTPExistingCustomerIdentifierTypeBuilder.setType(CustomerNoAllIssuerType.BT_PANORAMA);
		CreateOTPExistingCustomerIdentifierType createOTPExistingCustomerIdentifierType = createOTPExistingCustomerIdentifierTypeBuilder.build();
        partyDetails.setCustomerIdentifier(createOTPExistingCustomerIdentifierType);

        final CreateOTPCommunicationDetailsType communicationDetails = new CreateOTPCommunicationDetailsType();
        communicationDetails.setCommunicationMedium(CommunicationMediumType.EMAIL);
        communicationDetails.setInvestorDetails(poaPartyDetailsTypeBuilder.createInvestorDetails(individualDto));
        communicationDetails.setIntermediaryDetails(poaPartyDetailsTypeBuilder.createIntermediaryDetails(brokerUser));

        final ResendExistingRegistrationCodeRequestMsgType request = new ResendExistingRegistrationCodeRequestMsgType();
        request.setCommunicationDetails(communicationDetails);
        request.setPartyDetails(partyDetails);
        return request;
    }

    @SuppressWarnings("deprecation")
	@Override
    public ResendExistingRegistrationCodeRequestMsgType buildForAdviser(String gcmId, String role, ServiceErrors serviceErrors) {
        CreateOTPPartyDetailsType partyDetails = new CreateOTPPartyDetailsType();
        partyDetails.setRoleType(POARoleTypeType.ADVISER);

        IndividualDto details = new IndividualDto();
        details.setGcmId(gcmId);
        
        //----
        CreateOTPExistingCustomerIdentifierTypeBuilder createOTPExistingCustomerIdentifierTypeBuilder = new CreateOTPExistingCustomerIdentifierTypeBuilder();
        createOTPExistingCustomerIdentifierTypeBuilder.setId(gcmId);
        createOTPExistingCustomerIdentifierTypeBuilder.setType(CustomerNoAllIssuerType.BT_PANORAMA);
        CreateOTPExistingCustomerIdentifierType createOTPExistingCustomerIdentifierType = createOTPExistingCustomerIdentifierTypeBuilder.build();
        partyDetails.setCustomerIdentifier(createOTPExistingCustomerIdentifierType);

        ResendExistingRegistrationCodeRequestMsgType request = new ResendExistingRegistrationCodeRequestMsgType();
        request.setPartyDetails(partyDetails);

        CreateOTPCommunicationDetailsType communicationDetails = new CreateOTPCommunicationDetailsType();
        communicationDetails.setCommunicationMedium(CommunicationMediumType.EMAIL);
        BrokerUser brokerUser = brokerIntegrationService.getBrokerUser(UserKey.valueOf(gcmId), serviceErrors);
        communicationDetails.setIntermediaryDetails(poaPartyDetailsTypeBuilder.createIntermediaryDetails(brokerUser));
        request.setCommunicationDetails(communicationDetails);

        return request;
    }

    @Override
    public JAXBElement<ResendExistingRegistrationCodeRequestMsgType> wrap(ResendExistingRegistrationCodeRequestMsgType request) {
        final ObjectFactory factory = new ObjectFactory();
        return factory.createResendExistingRegistrationCodeRequestMsg(request);
    }

    @Override
    public String extractInvestorEmailAddress(ResendExistingRegistrationCodeRequestMsgType request) {
        return request.getCommunicationDetails().getInvestorDetails().getEmailAddresses().getEmailAddress().get(0).getEmailAddressDetail().getValue().getEmailAddress();
    }

    @Override
    public boolean isSuccessful(ResendExistingRegistrationCodeResponseMsgType response) {
        return response.getStatus() == StatusTypeCode.SUCCESS;
    }

    @Override
    public OnboardingCommunication extractCommunicationDetails(ResendExistingRegistrationCodeResponseMsgType response) {
        final ResendRegistrationCodeResponseDetailsSuccessResponseType success = response.getResponseDetails().getSuccessResponse();
        final OnboardingCommunication communication = new OnboardingCommunication();
        communication.setCommunicationId(success.getCommunicationId());
        communication.setGcmPan(success.getCustomerIdentifier().getCustomerNumberIdentifier().getCustomerNumber());
        communication.setStatus(response.getStatus().value());
        communication.setLastModifiedDate(new Date());
        return communication;
    }

	@Override
	public List<ResendRegistrationCodeResponseDetailsErrorResponseType> getErrorResponses(
			ResendExistingRegistrationCodeResponseMsgType response) {
		 return response.getResponseDetails().getErrorResponses().getErrorResponse();
	}

    public void setClientListDtoService(ClientListDtoService clientListDtoService) {
        this.clientListDtoService = clientListDtoService;
    }

    public void setBrokerIntegrationService(BrokerIntegrationService brokerIntegrationService) {
        this.brokerIntegrationService = brokerIntegrationService;
    }

    public void setPoaPartyDetailsTypeBuilder(POAPartyDetailsTypeBuilder poaPartyDetailsTypeBuilder) {
        this.poaPartyDetailsTypeBuilder = poaPartyDetailsTypeBuilder;
    }
}
