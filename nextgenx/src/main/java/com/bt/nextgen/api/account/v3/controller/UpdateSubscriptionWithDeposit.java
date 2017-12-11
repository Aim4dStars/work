package com.bt.nextgen.api.account.v3.controller;

import com.bt.nextgen.api.account.v3.model.AccountSubscriptionDto;
import com.bt.nextgen.api.movemoney.v2.model.DepositDto;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.operation.ChainedControllerOperation;
import com.bt.nextgen.core.api.operation.ControllerOperation;

/**
 * This operation combines the results of account subscription switch & deposit and generates a combined response.
 */

public class UpdateSubscriptionWithDeposit<T extends BaseDto> extends ChainedControllerOperation {

    private ControllerOperation depositResponse;

    public UpdateSubscriptionWithDeposit(ControllerOperation subscriptionResponse, ControllerOperation depositResponse) {
        super(subscriptionResponse);
        this.depositResponse = depositResponse;
    }

    @Override
    protected ApiResponse performChainedOperation(ApiResponse subscriptionResponse) {
        final DepositDto depositDto = (DepositDto) depositResponse.performOperation().getData();
        final AccountSubscriptionDto subscriptionDto = (AccountSubscriptionDto) subscriptionResponse.getData();
        return new ApiResponse(subscriptionResponse.getApiVersion(), subscriptionResponse.getStatus(),
                new AccountSubscriptionDto(subscriptionDto, depositDto), subscriptionResponse.getError(), subscriptionResponse.getPaging());
    }
}