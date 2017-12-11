package com.bt.nextgen.api.cgt.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;
import java.util.List;

public class CgtGroupDto extends BaseDto {
    private String groupId;
    private String groupCode;
    private String groupName;
    private String groupType;

    private BigDecimal amount;
    private Integer quantity;
    private BigDecimal taxAmount;
    private BigDecimal grossGain;

    private BigDecimal costBase;
    private BigDecimal indexedCostBase;
    private BigDecimal reducedCostBase;

    private BigDecimal costBaseGain;

    private List<CgtSecurity> cgtSecurities;

    public CgtGroupDto(String groupId, String groupCode, String groupName, String groupType, BigDecimal amount, Integer quantity,
            BigDecimal taxAmount, BigDecimal grossGain, BigDecimal costBase, BigDecimal indexedCostBase,
            BigDecimal reducedCostBase, List<CgtSecurity> cgtSecurities, BigDecimal costBaseGain) {
        super();
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupCode = groupCode;
        this.groupType = groupType;
        this.amount = amount;
        this.quantity = quantity;
        this.taxAmount = taxAmount;
        this.grossGain = grossGain;

        this.costBase = costBase;
        this.indexedCostBase = indexedCostBase;
        this.reducedCostBase = reducedCostBase;

        this.cgtSecurities = cgtSecurities;

        this.costBaseGain = costBaseGain;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupType() {
        return groupType;
    }

    public List<CgtSecurity> getCgtSecurities() {
        return cgtSecurities;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public BigDecimal getGrossGain() {
        return grossGain;
    }

    public BigDecimal getCostBase() {
        return costBase;
    }

    public BigDecimal getIndexedCostBase() {
        return indexedCostBase;
    }

    public BigDecimal getReducedCostBase() {
        return reducedCostBase;
    }

    public BigDecimal getCostBaseGain() {
        return costBaseGain;
    }

}
