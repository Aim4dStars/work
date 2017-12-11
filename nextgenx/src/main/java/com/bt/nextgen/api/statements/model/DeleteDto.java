package com.bt.nextgen.api.statements.model;

import com.bt.nextgen.core.api.model.BaseDto;

/**
 * Created by L075208 on 2/11/2015.
 */
public class DeleteDto extends BaseDto {

    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String getType() {
        return "DeleteDocumentDto";
    }
}
