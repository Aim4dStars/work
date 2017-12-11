package com.bt.nextgen.service.group.customer.groupesb.email.v10;

import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.AddressType;
import com.bt.nextgen.service.integration.domain.Email;


public class CustomerEmailV10 implements Email {

    public enum EmailAction {
        ADD("A"),MODIFY("M"),DELETE("D");

        private String code;
        EmailAction(String code) {
            this.code = code;
        }
        public static EmailAction fromString(String text) {
            for (EmailAction t : EmailAction.values()) {
                if (t.code.equalsIgnoreCase(text)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Invalid action type: " + text);
        }
    }

    private AddressKey emailKey;
    private String modificationSequence;
    private String email;
    private AddressMedium type;
    private boolean preferred;
    private AddressType category;
    //TODO tobe removed
    private String versionNumber;
    private String contractMethodId;
    private String rowSetSequenceNo;
    private String validityStatus;
    private String priority;
    private String priorityLevelName;
    private String priorityLevelValue;
    private String usageId;


    private EmailAction action;
    private String oldAddress;


    @Override
    public AddressKey getEmailKey() {
        return emailKey;
    }

    @Override
    public AddressMedium getType() {
        return type;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getModificationSeq() {
        return modificationSequence;
    }

    @Override
    public boolean isPreferred() {
        return preferred;
    }

    @Override
    public AddressType getCategory() {
        return category;
    }

    public void setEmailKey(AddressKey emailKey) {
        this.emailKey = emailKey;
    }

    public void setModificationSeq(String modificationSequence) {
        this.modificationSequence = modificationSequence;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setType(AddressMedium type) {
        this.type = type;
    }

    public void setPreferred(boolean isPreferred) {
        this.preferred = isPreferred;
    }

    public void setCategory(AddressType category) {
        this.category = category;
    }

    public String getModificationSequence() {
        return modificationSequence;
    }

    public void setModificationSequence(String modificationSequence) {
        this.modificationSequence = modificationSequence;
    }

    public String getContractMethodId() {
        return contractMethodId;
    }

    public void setContractMethodId(String contractMethodId) {
        this.contractMethodId = contractMethodId;
    }

    public String getPriorityLevelName() {
        return priorityLevelName;
    }

    public void setPriorityLevelName(String priorityLevelName) {
        this.priorityLevelName = priorityLevelName;
    }

    public String getPriorityLevelValue() {
        return priorityLevelValue;
    }

    public void setPriorityLevelValue(String priorityLevelValue) {
        this.priorityLevelValue = priorityLevelValue;
    }

    public String getUsageId() {
        return usageId;
    }

    public void setUsageId(String usageId) {
        this.usageId = usageId;
    }

    public EmailAction getAction() {
        return action;
    }

    public void setAction(EmailAction action) {
        this.action = action;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getRowSetSequenceNo() {
        return rowSetSequenceNo;
    }

    public void setRowSetSequenceNo(String rowSetSequenceNo) {
        this.rowSetSequenceNo = rowSetSequenceNo;
    }

    public String getValidityStatus() {
        return validityStatus;
    }

    public void setValidityStatus(String validityStatus) {
        this.validityStatus = validityStatus;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getOldAddress() {
        return oldAddress;
    }

    public void setOldAddress(String oldAddress) {
        this.oldAddress = oldAddress;
    }
}
