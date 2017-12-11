package com.bt.nextgen.service.integration.accountactivation;

import com.bt.nextgen.service.avaloq.accountactivation.AccountActivationRequest;
import com.bt.nextgen.service.avaloq.accountactivation.AccountActivationRequestImpl;

public class AccountActivationRequestBuilder {
    String orderId = "122345";
    String gcmId = "75685";

    public static AccountActivationRequestBuilder anAccountActivationRequest() {
        return new AccountActivationRequestBuilder();
    }

    public AccountActivationRequestImpl build() {
        return new AccountActivationRequestImpl(gcmId, orderId, null);
    }

}
