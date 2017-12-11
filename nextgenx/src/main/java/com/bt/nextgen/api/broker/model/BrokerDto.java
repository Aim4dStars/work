package com.bt.nextgen.api.broker.model;

import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class BrokerDto {
    private BrokerKey key;
    private String brokerType;
    private BrokerKey brokerParentKey;
    private String firstName;
    private String middleName;
    private String lastName;
    private String corporateName;
    private String displayName;
    private List<AddressDto> addresses;
    private List<EmailDto> email;
    private List<PhoneDto> phone;
    private boolean isOfflineApproval = false;
    private String dealerGroupName;

    public BrokerKey getKey() {
        return key;
    }

    public void setKey(BrokerKey key) {
        this.key = key;
    }

    public String getBrokerType() {
        return brokerType;
    }

    public void setBrokerType(String brokerType) {
        this.brokerType = brokerType;
    }

    public BrokerKey getBrokerParentKey() {
        return brokerParentKey;
    }

    public void setBrokerParentKey(BrokerKey brokerParentKey) {
        this.brokerParentKey = brokerParentKey;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCorporateName() {
        if (StringUtils.isNotBlank(corporateName)) {
            return corporateName;
        }
        return getFullName();
    }

    public void setCorporateName(String corporateName) {
        this.corporateName = corporateName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFullName() {
        if (getFirstName() != null && getLastName() != null) {
            return getFirstName() + " " + getLastName();
        }
        return null;
    }

    public List<AddressDto> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressDto> addresses) {
        this.addresses = addresses;
    }

    public List<EmailDto> getEmail() {
        return email;
    }

    public void setEmail(List<EmailDto> email) {
        this.email = email;
    }

    public List<PhoneDto> getPhone() {
        return phone;
    }

    public void setPhone(List<PhoneDto> phone) {
        this.phone = phone;
    }

    public boolean isOfflineApproval() {
        return isOfflineApproval;
    }

    public void setOfflineApproval(boolean isOfflineApproval) {
        this.isOfflineApproval = isOfflineApproval;
    }

    public String getDealerGroupName() {
        return dealerGroupName;
    }

    public void setDealerGroupName(String dealerGroupName) {
        this.dealerGroupName = dealerGroupName;
    }
}
