package com.bt.nextgen.service.integration.accountingsoftware.model;

import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.integration.account.AccountKey;

import java.util.List;

/**
 * Created by L062329 on 12/06/2015.
 */
public interface AccountingSoftware {

    AccountKey getKey();

    SoftwareFeedStatus getSoftwareFeedStatus();

    AccountingSoftwareType getSoftwareName();

    List<ValidationError> getValidationErrors();
}
