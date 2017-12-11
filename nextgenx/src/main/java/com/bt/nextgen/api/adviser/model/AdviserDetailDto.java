package com.bt.nextgen.api.adviser.model;


import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;
import org.joda.time.DateTime;

public class AdviserDetailDto implements KeyedDto <ClientKey>
{

    private String fullName;
    private Address primaryAddress;
    private Phone primaryMobilePhone;
    private Phone primaryBusinessPhone;
    private Phone homePhone;
    private Email primaryEmail;
    private String userId;
    private String userName;
    private String dealerGroupName;
    private String title;
    private DateTime openDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DateTime getOpenDate() {
        return openDate;
    }

    public void setOpenDate(DateTime openDate) {
        this.openDate = openDate;
    }

    public Phone getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(Phone homePhone) {
        this.homePhone = homePhone;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Address getPrimaryAddress() {
         return primaryAddress;
    }

    public void setPrimaryAddress(Address primaryAddress) {
        this.primaryAddress = primaryAddress;
    }

    public Phone getPrimaryMobilePhone() {
        return primaryMobilePhone;
    }

    public void setPrimaryMobilePhone(Phone primaryMobilePhone) {
        this.primaryMobilePhone = primaryMobilePhone;
    }

    public Phone getPrimaryBusinessPhone() {
        return primaryBusinessPhone;
    }

    public void setPrimaryBusinessPhone(Phone primaryBusinessPhone) {
        this.primaryBusinessPhone = primaryBusinessPhone;
    }

    public Email getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(Email primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDealerGroupName() {
        return dealerGroupName;
    }

    public void setDealerGroupName(String dealerGroupName) {
        this.dealerGroupName = dealerGroupName;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public ClientKey getKey()
    {
        return null;
    }
}
