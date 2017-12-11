package com.bt.nextgen.api.supermatch.v1.model;

import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.rollover.v1.model.SuperfundDto;
import com.bt.nextgen.config.JsonViews;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * Super match fund details
 */
@ApiModel("SuperMatchFund")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SuperMatchFundDto extends SuperfundDto {

    @ApiModelProperty("Fund's account number")
    @JsonView(JsonViews.Write.class)
    private String accountNumber;

    @ApiModelProperty("Is insurance cover available for the fund")
    private Boolean insuranceCovered;

    @ApiModelProperty("Fund address detail")
    private AddressDto address;

    @ApiModelProperty("Fund's contact bumber")
    private String contactNumber;

    @ApiModelProperty("Fund's contact person name")
    private String contactName;

    @ApiModelProperty("Fund balance")
    private BigDecimal balance;

    @ApiModelProperty(value = "Activity status for the fund",
            notes = "Values to be expected: Active, Closed, Lost Inactive, Lost Uncontactable, Open And Lost, Open And Not Lost")
    private String activityStatus;

    @ApiModelProperty("Is fund rolloverable")
    private Boolean rolloverable;

    @ApiModelProperty("Rollover details")
    @JsonView(JsonViews.Write.class)
    private List<RolloverDetailsDto> rolloverDetails;

    @ApiModelProperty("List of members for the fund")
    private List<MemberDto> members;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Boolean getInsuranceCovered() {
        return insuranceCovered;
    }

    public void setInsuranceCovered(Boolean insuranceCovered) {
        this.insuranceCovered = insuranceCovered;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(String activityStatus) {
        this.activityStatus = activityStatus;
    }

    public Boolean isRolloverable() {
        return rolloverable;
    }

    public void setRolloverable(Boolean rolloverable) {
        this.rolloverable = rolloverable;
    }

    public List<RolloverDetailsDto> getRolloverDetails() {
        return rolloverDetails;
    }

    public void setRolloverDetails(List<RolloverDetailsDto> rolloverDetails) {
        this.rolloverDetails = rolloverDetails;
    }

    public List<MemberDto> getMembers() {
        return members;
    }

    public void setMembers(List<MemberDto> members) {
        this.members = members;
    }
}
