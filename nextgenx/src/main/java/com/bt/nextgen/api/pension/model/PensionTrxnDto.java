package com.bt.nextgen.api.pension.model;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.core.validation.ValidationError;

import java.util.List;

/**
 * Created by L067218 on 12/09/2016.
 */
public class PensionTrxnDto extends BaseDto implements KeyedDto<AccountKey> {

    private AccountKey key;

    private String transactionStatus;
    private List<ValidationError> errors;

    @Override
    public AccountKey getKey() {
        return key;
    }

    public void setKey(AccountKey key) {
        this.key = key;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationError> errors) {
        this.errors = errors;
    }
}
