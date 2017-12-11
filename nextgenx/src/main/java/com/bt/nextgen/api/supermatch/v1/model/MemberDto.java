package com.bt.nextgen.api.supermatch.v1.model;

import com.bt.nextgen.service.integration.supermatch.Member;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;

@ApiModel(value = "Member details")
public class MemberDto {

    @ApiModelProperty("Customer identifier")
    private String customerId;

    @ApiModelProperty("Client's first name")
    private String firstName;

    @ApiModelProperty("Client's last name")
    private String lastName;

    @ApiModelProperty("Client's date of birth")
    private DateTime dateOfBirth;

    public MemberDto(Member member) {
        customerId = member.getCustomerId();
        firstName = member.getFirstName();
        lastName = member.getLastName();
        dateOfBirth = member.getDateOfBirth();
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public DateTime getDateOfBirth() {
        return dateOfBirth;
    }

}
