package com.bt.nextgen.api.safi.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.service.safi.model.SafiAuthenticateResponse;

/**
 * Created by L072457 on 24/11/2015.
 */
public class SafiResponseDto extends BaseDto {

    private boolean successFlag = false;
    private String errorMessage;
    private String errorId;

    public boolean isSuccessFlag() {
        return successFlag;
    }

    public void setSuccessFlag(boolean successFlag) {
        this.successFlag = successFlag;
    }

    public String getErrorId() {
        return errorId;
    }

    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
