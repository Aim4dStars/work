package com.bt.nextgen.service.avaloq.insurance.service;

import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyrequest.v4_2.LIRequestContextType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyrequest.v4_2.ObjectFactory;

import java.util.UUID;

public class InsuranceRequestBuilder {
    private static final String VERSION = "4_2";
    private static final String PANORAMA_SUBMITTER = "PANORAMA";

    protected static LIRequestContextType getPolicyRequestContext(String requesterGcmId)
    {
        ObjectFactory objectFactory = new ObjectFactory();
        LIRequestContextType context = objectFactory.createLIRequestContextType();
        context.setVersion(VERSION);
        context.setResponseVersion(VERSION);
        context.setSubmitter(PANORAMA_SUBMITTER);
        context.setRequester(requesterGcmId);
        //TrackingReference trackingRef = TrackingReferenceLocator.locate();
        context.setTrackingID(UUID.randomUUID().toString());
        return context;
    }
}