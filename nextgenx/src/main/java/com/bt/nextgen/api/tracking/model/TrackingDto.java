package com.bt.nextgen.api.tracking.model;

import com.bt.nextgen.api.draftaccount.model.ApplicationClientStatus;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.ApprovalTypeEnum;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.core.repository.OnboardingApplicationStatus;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import org.joda.time.DateTime;

import java.util.List;

public class TrackingDto extends BaseDto  implements KeyedDto<ClientApplicationKey> {

    private DateTime lastModified;
    private PersonInfo adviser;
    private PersonInfo lastModifiedBy;
    private List<Investor> investors;
    private String accountType;
    private String accountId;
    private String encodedAccountId;
    private ApprovalTypeEnum approvalType;
    private String trustType;
    private ClientApplicationKey clientApplicationId;
    private OnboardingApplicationKey onboardingApplicationKey;
    private String referenceNumber;
    private String productName;
    private String parentProductName;
    private String displayName;
    private Contact primaryContact;
    private String encryptedBpId;

    private OnboardingApplicationStatus status;

    private ClientApplicationKey key;
    private String orderId;

    public TrackingDto(DateTime lastModified, String accountType, ClientApplicationKey clientApplicationId) {
        this.lastModified = lastModified;
        this.accountType = accountType;
        this.clientApplicationId = clientApplicationId;
    }

    public DateTime getLastModified() {
        return lastModified;
    }

    public PersonInfo getAdviser() {
        return adviser;
    }

    public PersonInfo getLastModifiedBy() {
        return lastModifiedBy;
    }

    public List<Investor> getInvestors() {
        return investors;
    }

    public OnboardingApplicationStatus getStatus() {
        return status;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getTrustType() {
        return trustType;
    }

    public void setTrustType(String trustType) {
        this.trustType = trustType;
    }

    public ClientApplicationKey getClientApplicationId() {
        return clientApplicationId;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Contact getPrimaryContact() {
        return this.primaryContact;
    }

    public void setStatus(OnboardingApplicationStatus status) {
        this.status = status;
    }

    public void setLastModifiedBy(PersonInfo lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public void setAdviser(String firstName, String lastName) {
        setAdviser(new PersonInfo(firstName, lastName));
    }

    public void setAdviser(PersonInfo adviser) {
        this.adviser = adviser;
    }

    public void setInvestors(List<Investor> investors) {
        this.investors = investors;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getParentProductName() {
        return parentProductName;
    }

    public void setParentProductName(String parentProductName) {
        this.parentProductName = parentProductName;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setPrimaryContact(Contact contact) {
        this.primaryContact = contact;
    }

    public ApprovalTypeEnum getApprovalType() {
        return approvalType;
    }

    public void setApprovalType(ApprovalTypeEnum approvalType) {
        this.approvalType = approvalType;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getEncryptedBpId() {
        return encryptedBpId;
    }

    public void setEncryptedBpId(String encryptedBpId) {
        this.encryptedBpId = encryptedBpId;
    }

    public String getEncodedAccountId() {
        return encodedAccountId;
    }

    public void setEncodedAccountId(String encodedAccountId) {
        this.encodedAccountId = encodedAccountId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public static class Investor {
        private ClientKey clientKey;
        private ApplicationClientStatus status;
        private String lastName;
        private String firstName;
        private String email;
        private boolean approver;
        private boolean tfnEntered;

        public Investor(ClientKey clientKey, ApplicationClientStatus status, String lastName, String firstName, String email, boolean approver, boolean tfnEntered) {
            this.clientKey = clientKey;
            this.status = status;
            this.lastName = lastName;
            this.firstName = firstName;
            this.email = email;
            this.approver = approver;
            this.tfnEntered = tfnEntered;
        }

        public ClientKey getClientKey() {
            return clientKey;
        }

        public ApplicationClientStatus getStatus() {
            return status;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public String getFirstName() {
            return firstName;
        }

        public boolean isApprover() {
            return approver;
        }
        
        public boolean hasTFNEntered() {
        	return tfnEntered;
        }
    }

    public static class Contact {
        private String firstName;
        private String lastName;
        private List<ContactMethod> contacts;

        public Contact(String firstName, String lastName, List<ContactMethod> contacts) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.contacts = contacts;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public List<ContactMethod> getContacts() {
            return contacts;
        }
    }

    public static class ContactMethod {
        private ContactMethodType method;
        private String value;

        public ContactMethod(ContactMethodType method, String value) {
            this.method = method;
            this.value = value;
        }

        public ContactMethodType getMethod() {
            return method;
        }

        public String getValue() {
            return value;
        }
    }

    public static enum ContactMethodType {
        EMAIL, MOBILE
    }

    @Override
    public ClientApplicationKey getKey()
    {
        return key;
    }

    public void setKey(ClientApplicationKey key)
    {
        this.key = key;
    }

    public OnboardingApplicationKey getOnboardingApplicationKey() {
        return onboardingApplicationKey;
    }

    public void setOnboardingApplicationKey(OnboardingApplicationKey onboardingApplicationKey) {
        this.onboardingApplicationKey = onboardingApplicationKey;
    }
}
