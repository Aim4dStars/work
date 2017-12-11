package com.bt.nextgen.service.onboarding.btesb;

import com.bt.nextgen.service.onboarding.CreateAccountResponse;
import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ProvisionOnlineAccessResponseMsgType;

import static ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.StatusTypeCode.SUCCESS;

/**
 * Base class for handling provision online access response objects.
 */
public class ProvisionOnlineAccessResponseAdapter extends ResponseAdapter implements CreateAccountResponse {

    public ProvisionOnlineAccessResponseAdapter() {
    }

    public ProvisionOnlineAccessResponseAdapter(ProvisionOnlineAccessResponseMsgType response) {
        if (null != response && response.getStatus() != SUCCESS) {
            setServiceErrors(response.getResponseDetails().getErrorResponses());
        }
    }
}
