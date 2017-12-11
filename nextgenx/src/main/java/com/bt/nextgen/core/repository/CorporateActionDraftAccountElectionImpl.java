package com.bt.nextgen.core.repository;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "CA_DRAFT_ACCOUNT_ELECTION")
public class CorporateActionDraftAccountElectionImpl implements CorporateActionSavedAccountElection {
    @EmbeddedId
    private CorporateActionSavedAccountElectionKey key;

    @Column(name = "OPTION_HASH")
    private String optionHash;

    @Column(name = "UNITS")
    private BigDecimal units;

    @Column(name = "PERCENT")
    private BigDecimal percent;

    @Column(name = "OVERSUBSCRIBE")
    private BigDecimal oversubscribe;

    public CorporateActionDraftAccountElectionImpl() {
        // Empty constructor
    }

    public CorporateActionDraftAccountElectionImpl(CorporateActionSavedAccountElectionKey key, String optionHash, BigDecimal units,
                                                   BigDecimal percent, BigDecimal oversubscribe) {
        this.key = key;
        this.optionHash = optionHash;
        this.units = units;
        this.percent = percent;
        this.oversubscribe = oversubscribe;
    }

    @Override
    public CorporateActionSavedAccountElectionKey getKey() {
        return key;
    }

    @Override
    public void setKey(CorporateActionSavedAccountElectionKey key) {
        this.key = key;
    }

    public String getOptionHash() {
        return optionHash;
    }

    public void setOptionHash(String optionHash) {
        this.optionHash = optionHash;
    }

    @Override
    public BigDecimal getUnits() {
        return units;
    }

    @Override
    public void setUnits(BigDecimal units) {
        this.units = units;
    }

    @Override
    public BigDecimal getPercent() {
        return percent;
    }

    @Override
    public void setPercent(BigDecimal percent) {
        this.percent = percent;
    }

    @Override
    public BigDecimal getOversubscribe() {
        return oversubscribe;
    }

    @Override
    public void setOversubscribe(BigDecimal oversubscribe) {
        this.oversubscribe = oversubscribe;
    }

    @Override
    public String toString() {
        return "CorporateActionDraftAccountElectionImpl{" +
                "key=" + key +
                ", optionHash='" + optionHash + '\'' +
                ", units=" + units +
                ", percent=" + percent +
                ", oversubscribe=" + oversubscribe +
                '}';
    }
}
