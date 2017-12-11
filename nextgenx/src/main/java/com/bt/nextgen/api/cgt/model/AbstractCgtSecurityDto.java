package com.bt.nextgen.api.cgt.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public abstract class AbstractCgtSecurityDto extends BaseDto implements KeyedDto, CgtSecurity {
    protected String securityCode;
    private String securityName;
    private String securityType;
    private BigDecimal amount;
    private Integer quantity;
    private DateTime date;
    private DateTime taxDate;
    private BigDecimal taxAmount;
    private BigDecimal costBase;
    private BigDecimal calculatedCost;
    private String costCode;
    private BigDecimal grossGain;
    private BigDecimal calculatedGain;
    private String gainCode;
    private Integer daysHeld;
    private BigDecimal indexedCostBase;
    private BigDecimal reducedCostBase;
    private String parentInvId;
    private String parentInvCode;
    private String parentInvName;
    private String parentInvType;
    private BigDecimal costBaseGain;

    public AbstractCgtSecurityDto(String securityCode, String securityName, String securityType) {
        super();
        this.securityCode = securityCode;
        this.securityName = securityName;
        this.securityType = securityType;
    }

    public AbstractCgtSecurityDto(String securityCode, String securityName, String securityType, String parentInvId,
            String parentInvCode, String parentInvName, String parentInvType) {
        super();
        this.securityCode = securityCode;
        this.securityName = securityName;
        this.securityType = securityType;
        this.parentInvId = parentInvId;
        this.parentInvCode = parentInvCode;
        this.parentInvName = parentInvName;
        this.parentInvType = parentInvType;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#getKey()
     */

    @Override
    public Object getKey() {
        return securityCode;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#getSecurityCode()
     */
    @Override
    public String getSecurityCode() {
        return securityCode;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#getSecurityName()
     */
    @Override
    public String getSecurityName() {
        return securityName;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#getSecurityType()
     */
    @Override
    public String getSecurityType() {
        return securityType;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#getAmount()
     */
    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#setAmount(java.math.BigDecimal)
     */
    @Override
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#getQuantity()
     */
    @Override
    public Integer getQuantity() {
        return quantity;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#setQuantity(java.lang.Integer)
     */
    @Override
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#getDate()
     */
    @Override
    public DateTime getDate() {
        return date;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#setDate(org.joda.time.DateTime)
     */
    @Override
    public void setDate(DateTime date) {
        this.date = date;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#getTaxDate()
     */
    @Override
    public DateTime getTaxDate() {
        return taxDate;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#setTaxDate(org.joda.time.DateTime)
     */
    @Override
    public void setTaxDate(DateTime taxDate) {
        this.taxDate = taxDate;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#getTaxAmount()
     */
    @Override
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#setTaxAmount(java.math.BigDecimal)
     */
    @Override
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#getCostBase()
     */
    @Override
    public BigDecimal getCostBase() {
        return costBase;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#setCostBase(java.math.BigDecimal)
     */
    @Override
    public void setCostBase(BigDecimal costBase) {
        this.costBase = costBase;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#getCalculatedCost()
     */
    @Override
    public BigDecimal getCalculatedCost() {
        return calculatedCost;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#setCalculatedCost(java.math.BigDecimal)
     */
    @Override
    public void setCalculatedCost(BigDecimal calculatedCost) {
        this.calculatedCost = calculatedCost;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#getCostCode()
     */
    @Override
    public String getCostCode() {
        return costCode;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#setCostCode(java.lang.String)
     */
    @Override
    public void setCostCode(String costCode) {
        this.costCode = costCode;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#getGrossGain()
     */
    @Override
    public BigDecimal getGrossGain() {
        return grossGain;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#setGrossGain(java.math.BigDecimal)
     */
    @Override
    public void setGrossGain(BigDecimal grossGain) {
        this.grossGain = grossGain;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#getCalculatedGain()
     */
    @Override
    public BigDecimal getCalculatedGain() {
        return calculatedGain;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#setCalculatedGain(java.math.BigDecimal)
     */
    @Override
    public void setCalculatedGain(BigDecimal calculatedGain) {
        this.calculatedGain = calculatedGain;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#getGainCode()
     */
    @Override
    public String getGainCode() {
        return gainCode;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#setGainCode(java.lang.String)
     */
    @Override
    public void setGainCode(String gainCode) {
        this.gainCode = gainCode;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#getDaysHeld()
     */
    @Override
    public Integer getDaysHeld() {
        return daysHeld;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#setDaysHeld(java.lang.Integer)
     */
    @Override
    public void setDaysHeld(Integer daysHeld) {
        this.daysHeld = daysHeld;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#getIndexedCostBase()
     */
    @Override
    public BigDecimal getIndexedCostBase() {
        return indexedCostBase;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#setIndexedCostBase(java.math.BigDecimal)
     */
    @Override
    public void setIndexedCostBase(BigDecimal indexedCostBase) {
        this.indexedCostBase = indexedCostBase;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#getReducedCostBase()
     */
    @Override
    public BigDecimal getReducedCostBase() {
        return reducedCostBase;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#setReducedCostBase(java.math.BigDecimal)
     */
    @Override
    public void setReducedCostBase(BigDecimal reducedCostBase) {
        this.reducedCostBase = reducedCostBase;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#getParentInvId()
     */
    @Override
    public String getParentInvId() {
        return parentInvId;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#setParentInvId(java.lang.String)
     */
    @Override
    public void setParentInvId(String parentInvId) {
        this.parentInvId = parentInvId;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#getParentInvCode()
     */
    @Override
    public String getParentInvCode() {
        return parentInvCode;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#setParentInvCode(java.lang.String)
     */
    @Override
    public void setParentInvCode(String parentInvCode) {
        this.parentInvCode = parentInvCode;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#getParentInvName()
     */
    @Override
    public String getParentInvName() {
        return parentInvName;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#setParentInvName(java.lang.String)
     */
    @Override
    public void setParentInvName(String parentInvName) {
        this.parentInvName = parentInvName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#getParentInvType()
     */
    @Override
    public String getParentInvType() {
        return parentInvType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.bt.nextgen.api.cgt.model.CgtSecurity#setParentInvType(java.lang.String)
     */
    @Override
    public void setParentInvType(String parentInvType) {
        this.parentInvType = parentInvType;
    }

    @Override
    public BigDecimal getCostBaseGain() {
        return costBaseGain;
    }

    @Override
    public void setCostBaseGain(BigDecimal costBaseGain) {
        this.costBaseGain = costBaseGain;
    }

}
