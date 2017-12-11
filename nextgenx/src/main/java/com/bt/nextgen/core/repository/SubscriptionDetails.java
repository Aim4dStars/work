package com.bt.nextgen.core.repository;

import javax.persistence.*;

@Entity
@Table(name = "SUBSCRIPTIONS")
public class SubscriptionDetails {

    @Id()
    @Column(name = "DOC_ID")
    private String docId;

    @Column(name = "ACCOUNT_ID")
    private String accountId;

    @Column(name = "STATUS")
    //@Enumerated(EnumType.STRING)
    private String status;

    @Column(name = "SUBSCRIBE_TYPE")
    private String subscriptionType;

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }
}