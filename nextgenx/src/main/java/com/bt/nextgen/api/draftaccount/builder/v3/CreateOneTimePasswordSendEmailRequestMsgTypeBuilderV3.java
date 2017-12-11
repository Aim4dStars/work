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
import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.CreateOneTimePasswordSendEmailResponseDetailsErrorResponseType;
import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.CreateOneTimePasswordSendEmailResponseDetailsSuccessResponseType;
import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.CreateOneTimePasswordSendEmailResponseMsgType;
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
@Component(CreateOneTimePasswordSendEmailRequestMsgTypeBuilderV3.V3_BEAN_NAME)
@Qualifier("createOneTimePasswordSendEmailRequestMsgTypeBuilder")
@Lazy
public class CreateOneTimePasswordSendEmailRequestMsgTypeBuilderV3 implements CreateOneTimePasswordSendEmailRequestMsgTypeBuilder<CreateOneTimePasswordSendEmailRequestMsgType, CreateOneTimePasswordSendEmailResponseMsgType, CreateOneTimePasswordSendEmailResponseDetailsErrorResponseType> {

    public static final String V3_BEAN_NAME = "createOneTimePasswordSendEmailRequestMsgTypeBuilderV3";

    @Autowired
    private ClientListDtoService clientListDtoService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private POAPartyDetailsTypeBuilder poaPartyDetailsTypeBuilder;

    @SuppressWarnings("deprecation")
	@Override
    public CreateOneTimePasswordSendEmailRequestMsgType build(String clientId, String adviserPositionId, String role, ServiceErrors serviceErrors) {
        final ClientKey clientKey = new ClientKey(EncodedString.fromPlainText(clientId).toString());
        final IndividualDto individualDto = (IndividualDto) clientListDtoService.findWithoutRelatedAccounts(clientKey, serviceErrors);
        final BrokerUser brokerUser = brokerIntegrationService.getAdviserBrokerUser(BrokerKey.valueOf(adviserPositionId), serviceErrors);

        final CreateOTPPartyDetailsType partyDetails = new CreateOTPPartyDetailsType();
        partyDetails.setRoleType(POARoleTypeType.CLIENT);
       
        //-----
        CreateOTPExistingCustomerIdentifierTypeBuilder createOTPExistingCustomerIdentifierTypeBuilder = new CreateOTPExistingCustomerIdentifierTypeBuilder();
        createOTPExistingCustomerIdentifierTypeBuilder.setId(individualDto.getGcmId());
        createOTPExistingCustomerIdentifierTypeBuilder.setType(CustomerNoAllIssuerType.BT_PANORAMA);
        CreateOTPExistingCustomerIdentifierType createOTPExistingCustomerIdentifierType = createOTPExistingCustomerIdentifierTypeBuilder.build();
        partyDetails.setCustomerIdentifier(createOTPExistingCustomerIdentifierType);

        final CreateOTPCommunicationDetailsType communicationDetails = new CreateOTPCommunicationDetailsType();
        communicationDetails.setCommunicationMedium(CommunicationMediumType.EMAIL);
        communicationDetails.setInvestorDetails(poaPartyDetailsTypeBuilder.createInvestorDetails(individualDto));
        communicationDetails.setIntermediaryDetails(poaPartyDetailsTypeBuilder.createIntermediaryDetails(brokerUser));

        final CreateOneTimePasswordSendEmailRequestMsgType request = new CreateOneTimePasswordSendEmailRequestMsgType();
        request.setCommunicationDetails(communicationDetails);
        request.setPartyDetails(partyDetails);
        return request;
    }

    @SuppressWarnings("deprecation")
	@Override
    public CreateOneTimePasswordSendEmailRequestMsgType buildForAdviser(String gcmId, String role, ServiceErrors serviceErrors) {
        CreateOTPPartyDetailsType partyDetails = new CreateOTPPartyDetailsType();
        partyDetails.setRoleType(POARoleTypeType.ADVISER);

        IndividualDto details = new IndividualDto();
        details.setGcmId(gcmId);
        
        CreateOTPExistingCustomerIdentifierTypeBuilder createOTPExistingCustomerIdentifierTypeBuilder = new CreateOTPExistingCustomerIdentifierTypeBuilder();
        createOTPExistingCustomerIdentifierTypeBuilder.setId(gcmId);
        createOTPExistingCustomerIdentifierTypeBuilder.setType(CustomerNoAllIssuerType.BT_PANORAMA);
        CreateOTPExistingCustomerIdentifierType createOTPExistingCustomerIdentifierType = createOTPExistingCustomerIdentifierTypeBuilder.build();
        partyDetails.setCustomerIdentifier(createOTPExistingCustomerIdentifierType);

        CreateOneTimePasswordSendEmailRequestMsgType request = new CreateOneTimePasswordSendEmailRequestMsgType();
        request.setPartyDetails(partyDetails);

        CreateOTPCommunicationDetailsType communicationDetails = new CreateOTPCommunicationDetailsType();
        communicationDetails.setCommunicationMedium(CommunicationMediumType.EMAIL);
        BrokerUser brokerUser = brokerIntegrationService.getBrokerUser(UserKey.valueOf(gcmId), serviceErrors);
        communicationDetails.setIntermediaryDetails(poaPartyDetailsTypeBuilder.createIntermediaryDetails(brokerUser));
        request.setCommunicationDetails(communicationDetails);

        return request;
    }

    @Override
    public JAXBElement<CreateOneTimePasswordSendEmailRequestMsgType> wrap(CreateOneTimePasswordSendEmailRequestMsgType request) {
        final ObjectFactory factory = new ObjectFactory();
        return factory.createCreateOneTimePasswordSendEmailRequestMsg(request);
    }

    @Override
    public String extractInvestorEmailAddress(CreateOneTimePasswordSendEmailRequestMsgType request) {
        return request.getCommunicationDetails().getInvestorDetails().getEmailAddresses().getEmailAddress().get(0).getEmailAddressDetail().getValue().getEmailAddress();
    }

    @Override
    public boolean isSuccessful(CreateOneTimePasswordSendEmailResponseMsgType response) {
        return response.getStatus() == StatusTypeCode.SUCCESS;
    }

    @Override
    public List<CreateOneTimePasswordSendEmailResponseDetailsErrorResponseType> getErrorResponses(CreateOneTimePasswordSendEmailResponseMsgType response) {
        return response.getResponseDetails().getErrorResponses().getErrorResponse();
    }

    @Override
    public OnboardingCommunication extractCommunicationDetails(CreateOneTimePasswordSendEmailResponseMsgType response) {
        final CreateOneTimePasswordSendEmailResponseDetailsSuccessResponseType success = response.getResponseDetails().getSuccessResponse();
        final OnboardingCommunication communication = new OnboardingCommunication();
        communication.setCommunicationId(success.getCommunicationId());
        communication.setGcmPan(success.getCustomerIdentifier().getCustomerNumberIdentifier().getCustomerNumber());
        communication.setStatus(response.getStatus().value());
        communication.setLastModifiedDate(new Date());
        return communication;
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
