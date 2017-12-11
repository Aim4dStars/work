package com.bt.nextgen.api.account.v3.model;

import com.bt.nextgen.core.api.model.BaseDto;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * Created by L067218 on 10/05/2016.

 */
public class TaxAndPreservationDetailsDto extends BaseDto {

    private BigDecimal nonTaxableAmount;
    private BigDecimal taxableAmount;
    private BigDecimal totalAmount;
    private BigDecimal nonTaxablePercentage;
    private BigDecimal taxablePercentage;
    private BigDecimal preservedAmount;
    private BigDecimal restrictedNonPreservedAmount;
    private BigDecimal unrestrictedNonPreservedAmount;
    private DateTime eligibleServiceDate;


    /*private String totalTaxComponent;
    private String totalPreservationComponent; both replaces with total amount*/
    public TaxAndPreservationDetailsDto(){
        nonTaxableAmount = BigDecimal.ZERO;
        taxableAmount = BigDecimal.ZERO;
        totalAmount = BigDecimal.ZERO;
        nonTaxablePercentage = BigDecimal.ZERO;
        taxablePercentage = BigDecimal.ZERO;
        preservedAmount = BigDecimal.ZERO;
        restrictedNonPreservedAmount = BigDecimal.ZERO;
        unrestrictedNonPreservedAmount = BigDecimal.ZERO;
    }

    public BigDecimal getNonTaxableAmount() {
        return nonTaxableAmount;
    }

    public void setNonTaxableAmount(BigDecimal nonTaxableAmount) {
        this.nonTaxableAmount = nonTaxableAmount;
    }

    public BigDecimal getTaxableAmount() {
        return taxableAmount;
    }

    public void setTaxableAmount(BigDecimal taxableAmount) {
        this.taxableAmount = taxableAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getNonTaxablePercentage() {
        return nonTaxablePercentage;
    }

    public void setNonTaxablePercentage(BigDecimal nonTaxablePercentage) {
        this.nonTaxablePercentage = nonTaxablePercentage;
    }

    public BigDecimal getTaxablePercentage() {
        return taxablePercentage;
    }

    public void setTaxablePercentage(BigDecimal taxablePercentage) {
        this.taxablePercentage = taxablePercentage;
    }

    public BigDecimal getPreservedAmount() {
        return preservedAmount;
    }

    public void setPreservedAmount(BigDecimal preservedAmount) {
        this.preservedAmount = preservedAmount;
    }

    public BigDecimal getRestrictedNonPreservedAmount() {
        return restrictedNonPreservedAmount;
    }

    public void setRestrictedNonPreservedAmount(BigDecimal restrictedNonPreservedAmount) {
        this.restrictedNonPreservedAmount = restrictedNonPreservedAmount;
    }

    public BigDecimal getUnrestrictedNonPreservedAmount() {
        return unrestrictedNonPreservedAmount;
    }

    public void setUnrestrictedNonPreservedAmount(BigDecimal unrestrictedNonPreservedAmount) {
        this.unrestrictedNonPreservedAmount = unrestrictedNonPreservedAmount;
    }

    public DateTime getEligibleServiceDate() {
        return eligibleServiceDate;
    }

    public void setEligibleServiceDate(DateTime eligibleServiceDate) {
        this.eligibleServiceDate = eligibleServiceDate;
    }



}
