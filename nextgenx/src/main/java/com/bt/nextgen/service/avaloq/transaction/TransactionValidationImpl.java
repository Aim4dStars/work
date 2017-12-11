package com.bt.nextgen.service.avaloq.transaction;

import com.bt.nextgen.core.validation.ValidationError.ErrorType;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.integration.transaction.ParList;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;
import com.btfin.abs.err.v1_0.ErrType;

import java.util.ArrayList;
import java.util.List;

@ServiceBean(xpath = "err")
public class TransactionValidationImpl implements TransactionValidation {

    @ServiceElement(xpath = "type")
    private String errorType;

    @ServiceElement(xpath = "id")
    private String errorId;

    @ServiceElement(xpath = "err_msg")
    private String errorMessage;

    @ServiceElement(xpath = "log_id")
    private String logId;

    @ServiceElement(xpath = "extl_key")
    private String externalKey;

    @ServiceElement(xpath = "loc_list", type = ValidationLocation.class)
    private List<ValidationLocation> validLocList;

    // private List<String> locList;

    @ServiceElementList(xpath = "err_par_list/par_list", type = ParListImpl.class)
    private List<ParList> paramList;

    private String field;

    public TransactionValidationImpl() {
        super();
    }

    public String getErrorId() {
        return errorId;
    }

    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public List<String> getLocList() {
        List<String> locList = new ArrayList<>();
        if (this.getValidLocList() != null) {
            for (ValidationLocation loc : this.getValidLocList()) {
                locList.add(loc.getLoc());
            }
        }
        return locList;
    }

    @Deprecated
    public void setLocList(List<String> locList) {
        List<ValidationLocation> vlList = new ArrayList<>();
        for (String loc : locList) {
            ValidationLocation vl = new ValidationLocation();
            vl.setLoc(loc);
            vlList.add(vl);
        }
        setValidLocList(vlList);
    }

    public String getExternalKey() {
        return externalKey;
    }

    public void setExternalKey(String externalKey) {
        this.externalKey = externalKey;
    }

    public List<ParList> getParamList() {
        return paramList;
    }

    public void setParamList(List<ParList> paramList) {
        this.paramList = paramList;
    }

    @Override
    public ErrorType getType() {
        ErrType errType = ErrType.fromValue(errorType);
        switch (errType) {
            case FA:
                return ErrorType.FATAL;
            case OVR:
                return ErrorType.WARNING;
            default:
                return ErrorType.ERROR;
        }
    }

    @Override
    public String getField() {
        return field;
    }

    public void setfield(String field) {
        this.field = field;
    }

    public List<ValidationLocation> getValidLocList() {
        return validLocList;
    }

    public void setValidLocList(List<ValidationLocation> validLocList) {
        this.validLocList = validLocList;
    }

}
