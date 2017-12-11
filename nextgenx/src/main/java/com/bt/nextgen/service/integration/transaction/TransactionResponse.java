package com.bt.nextgen.service.integration.transaction;

import java.math.BigInteger;
import java.util.List;

import com.bt.nextgen.core.validation.ValidationError;

public interface TransactionResponse {

    public String getLocListItem(Integer index);

    public BigInteger getLocItemIndex(String itemId);

    public List<ValidationError> getValidationErrors();
}
