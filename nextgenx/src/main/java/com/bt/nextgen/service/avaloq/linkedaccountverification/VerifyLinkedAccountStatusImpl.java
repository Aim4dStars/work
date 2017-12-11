package com.bt.nextgen.service.avaloq.linkedaccountverification;

import com.bt.nextgen.service.integration.verifylinkedaccount.VerifyLinkedAccountStatus;

/**
 * Created by l078480 on 18/08/2017.
 */
public class VerifyLinkedAccountStatusImpl implements VerifyLinkedAccountStatus {

    private String linkedAccountStatus;

    @Override
    public String getLinkedAccountStatus() {
        return linkedAccountStatus;
    }
    @Override
    public void setLinkedAccountStatus(String linkedAccountStatus) {
        this.linkedAccountStatus = linkedAccountStatus;
    }



}
