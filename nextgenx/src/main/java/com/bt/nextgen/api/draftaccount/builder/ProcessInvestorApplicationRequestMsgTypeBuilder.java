package com.bt.nextgen.api.draftaccount.builder;

import ns.btfin_com.sharedservices.integration.common.response.errorresponsetype.v3.ErrorResponseType;

import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerUser;

/**
 * Builder interface to create on-boarding JAXB message instances from application form data.
 */
public interface ProcessInvestorApplicationRequestMsgTypeBuilder<R, P, E extends ErrorResponseType> extends ResponseMessageParser<P, E> {

    /**
     * Build the service request class out of the data provided from the form.
     * @param dealer the dealergroup for the product
     * @param clientApplicationForm the form data.
     * @param brokerUser the currently logged in Broker.
     * @param key Onboarding application key.
     * @param productId ID of the product into which the client should be onboarded.
     * @param dealer the dealer group under which the currently logged in Broker is operating.
     * @return the constructed request instance, that can be marshalled into a SOAP body for invoking the BT ESB
     *   onboarding service.
     */
    R buildFromForm(IClientApplicationForm clientApplicationForm, BrokerUser brokerUser, OnboardingApplicationKey key,
                    String productId, Broker dealer, ServiceErrors serviceErrors);
}
