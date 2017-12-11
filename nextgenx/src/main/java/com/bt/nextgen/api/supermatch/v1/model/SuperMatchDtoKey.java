package com.bt.nextgen.api.supermatch.v1.model;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.BaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Key for Super match updates
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ApiModel(value = "SuperMatchKey")
public class SuperMatchDtoKey extends BaseDto {

    @ApiModelProperty(value = "Account identifier")
    @JsonView(JsonViews.Write.class)
    private String accountId;

    @ApiModelProperty(value = "Update identifier - consent,acknowledge,rollover,member")
    @JsonView(JsonViews.Write.class)
    private String updateTypeId;

    public SuperMatchDtoKey() {
        // for object mapper
    }

    public SuperMatchDtoKey(String accountId) {
        this.accountId = accountId;
    }

    public SuperMatchDtoKey(String accountId, String updateTypeId) {
        this.accountId = accountId;
        this.updateTypeId = updateTypeId;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getUpdateTypeId() {
        return updateTypeId;
    }

    @JsonIgnore
    public UpdateType getUpdateType() {
        return UpdateType.forValue(updateTypeId);
    }
}
