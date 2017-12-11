package com.bt.nextgen.api.supermatch.v1.model;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Super match data transfer object
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ApiModel(value = "SuperMatch")
public class SuperMatchDto extends BaseDto implements KeyedDto<SuperMatchDtoKey> {

    @ApiModelProperty(value = "Super match detail identifier")
    @JsonView(JsonViews.Write.class)
    private SuperMatchDtoKey key;

    @ApiModelProperty(value = "Is consent provided by the user")
    @JsonView(JsonViews.Write.class)
    private Boolean consentProvided;

    @ApiModelProperty(value = "If user has seen the super match results")
    @JsonView(JsonViews.Write.class)
    private Boolean hasSeenResults;

    @ApiModelProperty(value = "Is match result available")
    private Boolean matchResultAvailable;

    @ApiModelProperty(value = "List of money items held by ATO (these are not allowed to be rolled over)")
    private List<MoneyItemDto> atoHeldMonies;

    @ApiModelProperty(value = "BT super fund details")
    private SuperMatchFundDto btSuperFund;

    @ApiModelProperty(value = "List of funds found in the search")
    @JsonView(JsonViews.Write.class)
    private List<SuperMatchFundDto> superMatchFundList;

    @ApiModelProperty(value = "List of domain errors that may come from ICC/ECO")
    private List<DomainApiErrorDto> errors;

    /**
     * Constructs an instance with the key
     *
     * @param key - Super match Dto key
     */
    public SuperMatchDto(SuperMatchDtoKey key) {
        this.key = key;
    }

    public SuperMatchDto() {
        // Default constructor
    }

    @Override
    public SuperMatchDtoKey getKey() {
        return key;
    }

    public void setKey(SuperMatchDtoKey key) {
        this.key = key;
    }

    public Boolean isConsentProvided() {
        return consentProvided;
    }

    public void setConsentProvided(Boolean consentProvided) {
        this.consentProvided = consentProvided;
    }

    public Boolean getHasSeenResults() {
        return hasSeenResults;
    }

    public void setHasSeenResults(Boolean hasSeenResults) {
        this.hasSeenResults = hasSeenResults;
    }

    public Boolean getMatchResultAvailable() {
        return matchResultAvailable;
    }

    public void setMatchResultAvailable(Boolean matchResultAvailable) {
        this.matchResultAvailable = matchResultAvailable;
    }

    public List<MoneyItemDto> getAtoHeldMonies() {
        return atoHeldMonies;
    }

    public void setAtoHeldMonies(List<MoneyItemDto> atoHeldMonies) {
        this.atoHeldMonies = atoHeldMonies;
    }

    public SuperMatchFundDto getBtSuperFund() {
        return btSuperFund;
    }

    public void setBtSuperFund(SuperMatchFundDto btSuperFund) {
        this.btSuperFund = btSuperFund;
    }

    public List<SuperMatchFundDto> getSuperMatchFundList() {
        return superMatchFundList;
    }

    public void setSuperMatchFundList(List<SuperMatchFundDto> superMatchFundList) {
        this.superMatchFundList = superMatchFundList;
    }

    public List<DomainApiErrorDto> getErrors() {
        return errors;
    }

    public void setErrors(List<DomainApiErrorDto> errors) {
        this.errors = errors;
    }
}
