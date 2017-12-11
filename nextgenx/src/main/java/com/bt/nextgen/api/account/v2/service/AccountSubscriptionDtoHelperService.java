package com.bt.nextgen.api.account.v2.service;

import com.bt.nextgen.api.account.v1.model.DepositDto;
import com.bt.nextgen.api.account.v2.model.AccountSubscriptionDepositDto;
import com.bt.nextgen.api.account.v2.model.AccountSubscriptionDto;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.operation.ChainedControllerOperation;
import com.bt.nextgen.core.api.operation.ControllerOperation;

/**
 * This service combines the results of account subscription and deposit services and generates a combined response.
 */
@Deprecated
public class AccountSubscriptionDtoHelperService<T extends BaseDto> extends ChainedControllerOperation {

    private ControllerOperation depositResponse;

    public AccountSubscriptionDtoHelperService(ControllerOperation subscriptionResponse, ControllerOperation depositResponse) {
        super(subscriptionResponse);
        this.depositResponse = depositResponse;
    }

    @Override
    protected ApiResponse performChainedOperation(ApiResponse subscriptionResponse) {
        final DepositDto depositDto = (DepositDto) depositResponse.performOperation().getData();
        final AccountSubscriptionDto subscriptionDto = (AccountSubscriptionDto) subscriptionResponse.getData();
        return new ApiResponse(subscriptionResponse.getApiVersion(), subscriptionResponse.getStatus(),
                new AccountSubscriptionDepositDto(subscriptionDto, depositDto), subscriptionResponse.getError(), subscriptionResponse.getPaging());
    }
}