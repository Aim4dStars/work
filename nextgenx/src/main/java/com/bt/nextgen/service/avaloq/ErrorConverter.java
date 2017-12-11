package com.bt.nextgen.service.avaloq;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.avaloq.abs.bb.fld_def.TextFld;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.validation.ValidationError.ErrorType;
import com.btfin.abs.err.v1_0.Err;
import com.btfin.abs.err.v1_0.ErrList;
import com.btfin.abs.err.v1_0.ErrType;

@Service("errorConverter")
public class ErrorConverter {

    @Autowired
    private CmsService cmsService;

    @SuppressWarnings("squid:MethodCyclomaticComplexity")
    public List<ValidationError> processErrorList(ErrList errList) {
        List<ValidationError> validations = new ArrayList<>();
        if (errList != null) {
            for (Err err : errList.getErr()) {
                ErrType errType = err.getType();
                ErrorType errorType;
                switch (errType) {
                    case FA:
                        errorType = ErrorType.FATAL;
                        break;
                    case OVR:
                        errorType = ErrorType.WARNING;
                        break;
                    default:
                        errorType = ErrorType.ERROR;
                        break;
                }
                String[] paramArray = new String[0];
                if (err.getErrParList() != null && err.getErrParList().getParList() != null
                        && err.getErrParList().getParList().getPar() != null
                        && !err.getErrParList().getParList().getPar().isEmpty()) {
                    List<TextFld> parList = err.getErrParList().getParList().getPar();
                    paramArray = new String[parList.size()];
                    int i = 0;
                    for (TextFld par : parList) {
                        paramArray[i] = par.getVal();
                        ++i;
                    }
                }

                String errorMsg = getErrorMsg(err, paramArray);
                validations.add(new ValidationError(err.getExtlKey() != null ? err.getExtlKey() : err.getId(), null,
                        errorMsg == null ? err.getErrMsg() : errorMsg, errorType));
            }
        }

        return validations;
    }

    private String getErrorMsg(Err err, String[] paramArray) {
        String errorMsg;
        String errorKey = Properties.get("errorcode." + err.getExtlKey());

        if (errorKey == null) {
            errorKey = Properties.get("errorcode." + err.getId());
        }

        if (errorKey == null) {
            errorMsg = err.getErrMsg();
        } else if (paramArray.length == 0) {
            errorMsg = cmsService.getContent(errorKey);
        } else {
            errorMsg = cmsService.getDynamicContent(errorKey, paramArray);
        }

        return errorMsg;
    }
}
