package com.bt.nextgen.api.draftaccount.builder;

import javax.xml.bind.JAXBElement;

import ns.btfin_com.sharedservices.integration.common.response.errorresponsetype.v3.ErrorResponseType;

import com.bt.nextgen.core.repository.OnboardingCommunication;
import com.bt.nextgen.service.ServiceErrors;

/**
 * Builder interface for generating JAXB request instances for request messages.
 */
public interface CreateOneTimePasswordSendEmailRequestMsgTypeBuilder<R, P, E extends ErrorResponseType> extends ResponseMessageParser<P, E> {

    R build(String clientId, String adviserPositionId, String role, ServiceErrors serviceErrors);

    R buildForAdviser(String gcmId, String role, ServiceErrors serviceErrors);

    JAXBElement<R> wrap(R instance);

    String extractInvestorEmailAddress(R request);

    OnboardingCommunication extractCommunicationDetails(P response);
}
