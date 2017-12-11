package com.bt.nextgen.api.subscriptions.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.subscriptions.model.SubscriptionDto;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;
import com.bt.nextgen.core.api.dto.SearchByKeyDtoService;
import com.bt.nextgen.core.api.dto.SubmitDtoService;

public interface SubscriptionDtoService extends FindAllDtoService<SubscriptionDto>,
        SubmitDtoService<AccountKey, SubscriptionDto>,
        SearchByKeyDtoService<AccountKey, SubscriptionDto>,
        SearchByCriteriaDtoService<SubscriptionDto>{
}