package com.bt.nextgen.service.avaloq.insurance.model;

import com.bt.nextgen.api.staticreference.model.StaticReference;
import com.bt.nextgen.service.integration.account.AccountStructureType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static com.bt.nextgen.service.avaloq.insurance.model.BenefitOptionType.*;

public enum BenefitType implements StaticReference {

    DEATH("Death", "Death Benefit", DEATH_BUSINESS_COVER),
    SUPER_PLUS_TPD("SuperPlusTPD", "Super Plus TPD Benefit", TPD_BUY_BACK, TPD_DOUBLE, TPD_BUSINESS_COVER, WAIVER_LIFE_PREMIUM),
    TPD("TPD", "TPD Benefit", TPD_BUY_BACK, TPD_DOUBLE, TPD_BUSINESS_COVER, WAIVER_LIFE_PREMIUM),
    LIVING_PLUS("LivingPlus", "Living Benefit Plus", LIVING_REINSTATEMENT, LIVING_DOUBLE, LIVING_BUSINESS_COVER),
    LIVING("Living", "Living Benefit", LIVING_REINSTATEMENT, LIVING_DOUBLE, LIVING_BUSINESS_COVER),
    CHILD("Child", "Children's Benefit"),
    NEEDLE_STICK("NeedleStick", "Needlestick Benefit"),

    //add supercontribtion (ipratio field)
    INCOME_PROTECTION("IncomeProtection", "Income Protection", ACCIDENT),
    INCOME_PROTECTION_PLUS("IncomeProtectionPlus", "Income Protection Plus"), // For UI display only
    KEY_PERSON_INCOME("KeyPersonIncome", "Key Person Income"),
    BUSINESS_OVERHEAD("BusinessOverhead", "Business Overheads"),

    //add supercontribtion (ipratio field). extra fields need to be mapped. beneftype (policysubtype in json), period,
    SUPER_PLUS_INCOME_PROTECTION("SuperPlusIncomeProtection", "Super Plus IP Benefit", ACCIDENT),
    DETACHED_INCOME_PROTECTION("DetachedIncomeProtection"),
    ACCIDENTAL_DEATH("AccidentalDeath"),
    BILL_COVER("BillCover"),
    OTHER("Other"),
    UNKNOWN("Unknown"),
    NOT_AVAILABLE("Not Available");

    private String value;
    private String label;
    private Collection<BenefitOptionType> options;

    BenefitType(String value, BenefitOptionType... options) {
        this.value = value;
        this.options = (Collection) (options.length > 0 ? Collections.unmodifiableCollection(Arrays.asList(options)) : Collections.emptyList());
    }

    BenefitType(String value, String label, BenefitOptionType... options) {
        this(value, options);
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public Collection<BenefitOptionType> getOptions() {
        return options;
    }

    /**
     * Returns benefit options depending on the policy type and the account type
     *
     * @param policy               policy domain object
     * @param accountStructureType Account type for the owner of this policy
     * @return Collection of <code>BenefitOptionType</code> that applies to this policy
     */
    public Collection<BenefitOptionType> getOptions(Policy policy, AccountStructureType accountStructureType) {
        Collection<BenefitOptionType> optionsForPolicy = options;

        // Remove benefitOptions for BUSINESS_OVERHEAD as a optional benefit under the following circumstances
        if ((PolicyType.STAND_ALONE_TPD.equals(policy.getPolicyType()) && BenefitType.TPD.equals(this)) ||
                PolicyType.KEY_PERSON_INCOME.equals(policy.getPolicyType()) ||
                (BenefitType.INCOME_PROTECTION.equals(this) &&
                        PolicySubType.BUSINESS_OVERHEAD.equals(policy.getPolicySubType()))
                ) {
            return Collections.emptyList();
        } else if (PolicyType.STAND_ALONE_LIVING.equals(policy.getPolicyType()) && (BenefitType.LIVING.equals(this) || BenefitType.LIVING_PLUS.equals(this))) {
            return Collections.singletonList(BenefitOptionType.LIVING_REINSTATEMENT);
        }

        // Remove WAIVER_LIFE_PREMIUM as a optional benefit under the following circumstances:
        if (PolicyType.TERM_LIFE.equals(policy.getPolicyType()) && BenefitType.TPD.equals(this)
                && accountStructureType != null && accountStructureType.SUPER.equals(accountStructureType)) {
            optionsForPolicy = new ArrayList<>(options);
            optionsForPolicy.remove(BenefitOptionType.WAIVER_LIFE_PREMIUM);
        }

        // Remove WAIVER_LIFE_PREMIUM as a optional benefit under the following circumstances
        if ((PolicyType.FLEXIBLE_LINKING_PLUS.equals(policy.getPolicyType()) || PolicyType.TERM_LIFE_AS_SUPER.equals(policy.getPolicyType()))
                && (BenefitType.TPD.equals(this) || BenefitType.SUPER_PLUS_TPD.equals(this))) //Flexible linking plus policytype is only for IP account
        {
            optionsForPolicy = new ArrayList<>(options);
            optionsForPolicy.remove(BenefitOptionType.WAIVER_LIFE_PREMIUM);
        }

        // Add SUPER_CONTRIBUTION as a optional benefit under the following circumstances:
        if ((PolicyType.INCOME_LINKING_PLUS.equals(policy.getPolicyType()) || PolicyType.INCOME_PROTECTION.equals(policy.getPolicyType())
                || PolicyType.INCOME_PROTECTION_PLUS.equals(policy.getPolicyType()) || PolicyType.INCOME_PROTECTION_AS_SUPER.equals(policy.getPolicyType()))
                && (BenefitType.SUPER_PLUS_INCOME_PROTECTION.equals(this) || BenefitType.INCOME_PROTECTION.equals(this) || BenefitType.INCOME_PROTECTION.equals(this))) {
            optionsForPolicy = new ArrayList<>(options);
            BenefitOptionType benefitOptionType = BenefitOptionType.SUPER_CONTRIBUTION;
            if (policy.getIPIncomeRatioPercent().compareTo(BigDecimal.ZERO) > 0) {
                benefitOptionType.setStatus(true);
            } else {
                benefitOptionType.setStatus(false);
            }
            optionsForPolicy.add(benefitOptionType);
        }

        return optionsForPolicy;
    }

    public static BenefitType forValue(String value) {
        for (BenefitType benefit : BenefitType.values()) {
            if (benefit.getValue().equals(value)) {
                return benefit;
            }
        }
        return NOT_AVAILABLE;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getLabel() {
        return label != null ? label : value;
    }
}