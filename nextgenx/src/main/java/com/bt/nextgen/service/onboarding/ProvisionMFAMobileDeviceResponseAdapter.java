package com.bt.nextgen.service.onboarding;

import com.bt.nextgen.service.onboarding.btesb.ResponseAdapter;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ProvisionMFAMobileDeviceResponseMsgType;
import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.StatusTypeCode;

/**
 * Created by L069552 on 6/11/17.
 */
public class ProvisionMFAMobileDeviceResponseAdapter extends ResponseAdapter implements ProvisionMFAMobileDeviceResponse {

    private String mfaDeviceArrangementId;

    private ProvisionMFAMobileDeviceResponseMsgType provisionMFAMobileDeviceResponseMsgType;


    public ProvisionMFAMobileDeviceResponseAdapter(){
        setServiceErrors(new ServiceErrorsImpl());
    }

    public ProvisionMFAMobileDeviceResponseAdapter(ProvisionMFAMobileDeviceResponseMsgType provisionMFAMobileDeviceResponseMsgType){
        this();

        if(null != provisionMFAMobileDeviceResponseMsgType){
            if (provisionMFAMobileDeviceResponseMsgType.getStatus() == StatusTypeCode.SUCCESS) {
                this.mfaDeviceArrangementId = provisionMFAMobileDeviceResponseMsgType.getResponseDetails().getSuccessResponse().getMFAArrangementID();
            }else{
                setServiceErrors(provisionMFAMobileDeviceResponseMsgType.getResponseDetails().getErrorResponses());
            }
        }
    }
    @Override
    public String getMFAArrangementId() {
        return mfaDeviceArrangementId;
    }
}