package com.bt.nextgen.core.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name = "CA_DRAFT_ACCOUNT")
public class CorporateActionDraftAccountImpl implements CorporateActionSavedAccount {
    @EmbeddedId
    private CorporateActionSavedAccountKey key;

    @Column(name = "MINIMUM_PRICE_ID")
    private Integer minimumPriceId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = CorporateActionDraftAccountElectionImpl.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumns({@JoinColumn(name = "OE_ID", referencedColumnName = "OE_ID", insertable = false, updatable = false),
                  @JoinColumn(name = "ORDER_NUMBER", referencedColumnName = "ORDER_NUMBER", insertable = false, updatable = false),
                  @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID", insertable = false, updatable = false)})
    private List<CorporateActionSavedAccountElection> accountElections;

    public CorporateActionDraftAccountImpl() {
        // Empty constructor
    }

    public CorporateActionDraftAccountImpl(CorporateActionSavedAccountKey key, Integer minimumPriceId) {
        this.key = key;
        this.minimumPriceId = minimumPriceId;
    }

    public CorporateActionDraftAccountImpl(String oeId, String orderNumber, String accountNumber, Integer minimumPriceId) {
        this.key = new CorporateActionSavedAccountKey(oeId, orderNumber, accountNumber);
        this.minimumPriceId = minimumPriceId;
    }

    @Override
    public CorporateActionSavedAccountKey getKey() {
        return key;
    }

    @Override
    public void setKey(CorporateActionSavedAccountKey key) {
        this.key = key;
    }

    @Override
    public Integer getMinimumPriceId() {
        return minimumPriceId;
    }

    @Override
    public void setMinimumPriceId(Integer minimumPriceId) {
        this.minimumPriceId = minimumPriceId;
    }

    @Override
    public List<CorporateActionSavedAccountElection> getAccountElections() {
        return accountElections;
    }

    @Override
    public void setAccountElections(List<CorporateActionSavedAccountElection> accountElections) {
        this.accountElections = accountElections;
    }

    @Override
    public CorporateActionSavedAccountElection addAccountElection(Integer optionId, BigDecimal units, BigDecimal percent,
                                                                  BigDecimal oversubscribe) {
        if (accountElections == null) {
            accountElections = new ArrayList<>();
        }

        CorporateActionDraftAccountElectionImpl accountElection =
                new CorporateActionDraftAccountElectionImpl(
                        new CorporateActionSavedAccountElectionKey(key.getOeId(), key.getOrderNumber(), key.getAccountNumber(), optionId),
                        null,
                        units, percent, oversubscribe);

        accountElections.add(accountElection);

        return accountElection;
    }

    @Override
    public String toString() {
        return "CorporateActionDraftAccountImpl{" +
                "key=" + key +
                ", minimumPriceId=" + minimumPriceId +
                ", accountElections=" + accountElections +
                '}';
    }
}
