package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.IFeeComponentForm;
import com.bt.nextgen.api.draftaccount.model.form.IFeeComponentTier;
import com.bt.nextgen.api.draftaccount.model.form.IFeesComponentsForm;
import com.bt.nextgen.api.draftaccount.model.form.IFeesForm;
import ns.btfin_com.product.common.investmentproduct.v1_1.ProductType;
import ns.btfin_com.sharedservices.common.fee.v1_2.FeeClassificationType;
import ns.btfin_com.sharedservices.common.fee.v1_2.FeeFrequencyType;
import ns.btfin_com.sharedservices.common.fee.v1_2.FeeInfoType;
import ns.btfin_com.sharedservices.common.fee.v1_2.FeeMethodType;
import ns.btfin_com.sharedservices.common.fee.v1_2.FeeSourceType;
import ns.btfin_com.sharedservices.common.fee.v1_2.TierType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static com.btfin.panorama.onboarding.helper.FeeHelper.establishment;
import static com.btfin.panorama.onboarding.helper.FeeHelper.fee;
import static com.btfin.panorama.onboarding.helper.FeeHelper.flatFee;
import static com.btfin.panorama.onboarding.helper.FeeHelper.percentageFee;
import static com.btfin.panorama.onboarding.helper.FeeHelper.slidingFee;
import static com.btfin.panorama.onboarding.helper.FeeHelper.tier;
import static java.util.Arrays.asList;
import static ns.btfin_com.product.common.investmentproduct.v1_1.ProductType.CASH_DEPOSIT;
import static ns.btfin_com.product.common.investmentproduct.v1_1.ProductType.CASH_FUNDS;
import static ns.btfin_com.product.common.investmentproduct.v1_1.ProductType.MANAGED_FUND;
import static ns.btfin_com.product.common.investmentproduct.v1_1.ProductType.MANAGED_PORTFOLIO;
import static ns.btfin_com.product.common.investmentproduct.v1_1.ProductType.SECURITIES_LISTED;
import static ns.btfin_com.product.common.investmentproduct.v1_1.ProductType.SUPERANNUATION_CONTRIBUTION;
import static ns.btfin_com.product.common.investmentproduct.v1_1.ProductType.TERM_DEPOSIT;
import static ns.btfin_com.sharedservices.common.fee.v1_2.FeeClassificationType.ADVISER_CONTRIBUTION;
import static ns.btfin_com.sharedservices.common.fee.v1_2.FeeClassificationType.LICENSEE_ADVICE;
import static ns.btfin_com.sharedservices.common.fee.v1_2.FeeClassificationType.ONGOING_ADVICE;
import static ns.btfin_com.sharedservices.common.fee.v1_2.FeeSourceType.EMPLOYER;
import static ns.btfin_com.sharedservices.common.fee.v1_2.FeeSourceType.SELF;
import static ns.btfin_com.sharedservices.common.fee.v1_2.FeeSourceType.SPOUSE;
import static org.springframework.util.StringUtils.hasText;

@Service
class FeesBuilder {

    private static final int FEE_BUFFER_SIZE = 8;

    FeeInfoType[] getFees(IFeesForm feesForm, IClientApplicationForm.AccountType accountType) {
        List<FeeInfoType> fees = new ArrayList<>(FEE_BUFFER_SIZE);
        fees.add(establishment(feesForm.getEstablishmentFee()));
        if (feesForm.hasOngoingFees()) {
           fees.add(fee(ONGOING_ADVICE, getAdviceFee(feesForm.getOngoingFeesComponent(),ONGOING_ADVICE ,accountType)));
        }
        if (feesForm.hasLicenseeFees()) {
            fees.add(fee(LICENSEE_ADVICE, getAdviceFee(feesForm.getLicenseeFeesComponent(), LICENSEE_ADVICE,accountType)));
        }
        if (feesForm.hasContributionFees()) {
            fees.add(fee(ADVISER_CONTRIBUTION, getAdviceFee(feesForm.getContributionFeesComponent(),ADVISER_CONTRIBUTION,accountType)));
        }
        return fees.toArray(new FeeInfoType[fees.size()]);
    }

    private FeeMethodType[] getAdviceFee(IFeesComponentsForm feesComponent, FeeClassificationType feeClassificationType, IClientApplicationForm.AccountType accountType) {
        final List<FeeMethodType> feeMethods = new ArrayList<>(FEE_BUFFER_SIZE);
        for (IFeeComponentForm component : feesComponent.getElements()) {
            switch (component.getLabel()) {
            case "Dollar fee component":
                feeMethods.add(flatFee(new BigDecimal(component.getAmount()), component.isCpiIndexed()));
                break;
            case "Percentage fee component":
            case "One-off fee component":
            case "Regular fee component":
                feeMethods.addAll(percentageFeeComponents(component, feeClassificationType,accountType ));
                break;
            case "Sliding scale fee component":
                feeMethods.add(getSlidingFeeInfoType(component));
                break;
            default:
                break;
            }
        }
        return feeMethods.toArray(new FeeMethodType[feeMethods.size()]);
    }

    private List<FeeMethodType> percentageFeeComponents(IFeeComponentForm feeComponent, FeeClassificationType feeClassificationType, IClientApplicationForm.AccountType accountType) {

        List<FeeMethodType> percentageFeeComponents;

        if(feeClassificationType == ADVISER_CONTRIBUTION) {
            percentageFeeComponents = getAsviserContributionFeeMethodTypes(feeComponent, accountType);
        }else{
            percentageFeeComponents = asList(getPercentageFeeInfoType(MANAGED_PORTFOLIO, feeComponent.getManagedPortfolio(),null, null),
                    getPercentageFeeInfoType(TERM_DEPOSIT, feeComponent.getTermDeposit(), null, null),
                    getPercentageFeeInfoType(CASH_FUNDS, feeComponent.getCashFunds(), null,null ),
                    getPercentageFeeInfoType(MANAGED_FUND, feeComponent.getManagedFund(), null,null),
                    getPercentageFeeInfoType(SECURITIES_LISTED, feeComponent.getListedSecurities(), null,null )
            );
        }

        return percentageFeeComponents;

    }

    private List<FeeMethodType> getAsviserContributionFeeMethodTypes(IFeeComponentForm feeComponent, IClientApplicationForm.AccountType accountType) {
        List<FeeMethodType> percentageFeeComponents;
        if (accountType == IClientApplicationForm.AccountType.SUPER_ACCUMULATION || accountType == IClientApplicationForm.AccountType.SUPER_PENSION){
            percentageFeeComponents = getSuperFeeMethodTypes(feeComponent);
        } else {
            FeeFrequencyType feeFrequencyType = feeComponent.getLabel().equals("One-off fee component")? FeeFrequencyType.ONE_OFF : FeeFrequencyType.ONGOING;
            percentageFeeComponents = asList(getPercentageFeeInfoType(CASH_DEPOSIT,feeComponent.getDeposit(), feeFrequencyType,null ));
        }
        return percentageFeeComponents;
    }

    private List<FeeMethodType> getSuperFeeMethodTypes(IFeeComponentForm feeComponent) {
        List<FeeMethodType> percentageFeeComponents;
        if (feeComponent.getLabel().equals("One-off fee component")) {
            percentageFeeComponents = asList(getPercentageFeeInfoType(SUPERANNUATION_CONTRIBUTION,feeComponent.getPersonalContribution(), FeeFrequencyType.ONE_OFF,SELF ),
                    getPercentageFeeInfoType(SUPERANNUATION_CONTRIBUTION,feeComponent.getEmployerContribution(), FeeFrequencyType.ONE_OFF, EMPLOYER),
                    getPercentageFeeInfoType(SUPERANNUATION_CONTRIBUTION,feeComponent.getSpouseContribution(), FeeFrequencyType.ONE_OFF,SPOUSE ));
        } else {
            percentageFeeComponents = asList(getPercentageFeeInfoType(SUPERANNUATION_CONTRIBUTION,feeComponent.getPersonalContribution(), FeeFrequencyType.ONGOING,SELF ),
                    getPercentageFeeInfoType(SUPERANNUATION_CONTRIBUTION,feeComponent.getSpouseContribution(),FeeFrequencyType.ONGOING,SPOUSE ));
        }
        return percentageFeeComponents;
    }

    private FeeMethodType getPercentageFeeInfoType(ProductType productType, String appliedFeeRate, FeeFrequencyType feeFrequency, FeeSourceType feeSourceType) {
        return percentageFee(productType, new BigDecimal(appliedFeeRate), feeFrequency, feeSourceType);
    }

    private FeeMethodType getSlidingFeeInfoType(IFeeComponentForm feeComponent) {
        final List<IFeeComponentTier> tierList = feeComponent.getSlidingScaleFeeTiers();
        final Collection<TierType> tiers = new ArrayList<>(tierList.size());
        for (IFeeComponentTier tier : tierList) {
            final BigDecimal percentage = new BigDecimal(tier.getPercentage());
            final BigDecimal from = new BigDecimal(tier.getLowerBound());
            final String upperBound = tier.getUpperBound();
            final TierType tierType;
            if (hasText(upperBound)) {
                tierType = tier(percentage, from, new BigDecimal(upperBound));
            } else {
                tierType = tier(percentage, from);
            }
            tiers.add(tierType);
        }
        return slidingFee(products(feeComponent), tiers);
    }

    private Set<ProductType> products(IFeeComponentForm feeComponent) {
        final Set<ProductType> products = EnumSet.noneOf(ProductType.class);
        if (feeComponent.isForManagedFund()) {
            products.add(MANAGED_FUND);
        }
        if (feeComponent.isForManagedPortfolio()) {
            products.add(MANAGED_PORTFOLIO);
        }
        if (feeComponent.isForCash()) {
            products.add(CASH_FUNDS);
        }
        if (feeComponent.isForTermDeposit()) {
            products.add(TERM_DEPOSIT);
        }
        if (feeComponent.isForListedSecurities()) {
            products.add(SECURITIES_LISTED);
        }
        return products;
    }
}
