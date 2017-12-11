package com.bt.nextgen.service.avaloq.modelportfolio.rebalance;

import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationImpl;
import com.bt.nextgen.service.integration.transaction.TransactionResponse;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;

import java.math.BigInteger;
import java.util.List;

@ServiceBean(xpath = "/", type = ServiceBeanType.CONCRETE)
public class ModelPortfolioSubmitResponseImpl implements TransactionResponse {
    @ServiceElement(xpath = "//rsp/valid/err_list/err | //rsp/exec/err_list/err", type = TransactionValidationImpl.class)
    private List<TransactionValidation> errors;

    @ServiceElement(xpath = "//err_list/err", type = TransactionValidationImpl.class)
    private List<TransactionValidation> fatalErrors;

    private List<ValidationError> validationErrors;

    public List<TransactionValidation> getErrors() {
        if (fatalErrors != null)
            return fatalErrors;
        else
            return errors;
    }

    @Override
    public String getLocListItem(Integer index) {
        return null;
    }

    @Override
    public BigInteger getLocItemIndex(String itemId) {
        return null;
    }

    @Override
    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }
}