package com.bt.nextgen.api.fees.v1.service;

import com.bt.nextgen.api.fees.v1.model.BaseFeeType;
import com.bt.nextgen.api.fees.v1.model.DollarFeeAmount;
import com.bt.nextgen.api.fees.v1.model.FeeComponentType;
import com.bt.nextgen.api.fees.v1.model.LicenseAdviserFeeDto;
import com.bt.nextgen.api.fees.v1.model.PercentageFee;
import com.bt.nextgen.api.fees.v1.model.SlidingScaleFee;
import com.bt.nextgen.api.fees.v1.model.SlidingScaleFeeTier;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.licenseadviserfee.CacheManagedAvaloqLicenseAdviserFeeIntegrationService;
import com.bt.nextgen.service.avaloq.licenseadviserfee.FeeDgOngoingApplyDef;
import com.bt.nextgen.service.avaloq.licenseadviserfee.FeeDgOngoingTariff;
import com.bt.nextgen.service.avaloq.licenseadviserfee.FeeDgOngoingTariffBound;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.Broker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bt.nextgen.service.avaloq.fees.FeesMiscType.DOLLAR_FEE;
import static com.bt.nextgen.service.avaloq.fees.FeesMiscType.PERCENT_CASH;
import static com.bt.nextgen.service.avaloq.fees.FeesMiscType.SLIDING_SCALE_FEE;

/**
 * Created by l078480 on 22/11/2016.
 */

/**
 * @deprecated Use V2
 */
@Deprecated
@Service
public class LicenseAdviserFeeServiceImpl implements LicenseAdviserFeeService {

    @Autowired
    private CacheManagedAvaloqLicenseAdviserFeeIntegrationService licenseAdviserFeeIntegrationServiceeeService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    private static final String LICENSE_FEE = "licenseeFee";

    private static final BigDecimal TARIFFACTOR = new BigDecimal(100);

    public LicenseAdviserFeeDto findLicenseAdviserFee(String adviserPositionId, String productId, ServiceErrors serviceErrors) {

        Broker dealerGroup = brokerIntegrationService.getBroker(BrokerKey.valueOf(adviserPositionId), serviceErrors);
        List<FeeDgOngoingTariff> licenseAdviserFees = licenseAdviserFeeIntegrationServiceeeService
                .loadLicenseAdviseFees(productId, dealerGroup.getDealerKey().getId(), adviserPositionId, serviceErrors);
        return populateLicenseAdviserFees(licenseAdviserFees);
    }

    public LicenseAdviserFeeDto findLicenseFeeForDealerGroup(String adviserPositionId, ServiceErrors serviceErrors) {

        Broker dealerGroup = brokerIntegrationService.getBroker(BrokerKey.valueOf(adviserPositionId), serviceErrors);

        List<FeeDgOngoingTariff> licenseAdviserFees = licenseAdviserFeeIntegrationServiceeeService
                .loadLicenseFeesForDealer(dealerGroup.getDealerKey().getId(), serviceErrors);
        return populateLicenseAdviserFees(licenseAdviserFees);

    }

    private LicenseAdviserFeeDto populateLicenseAdviserFees(List<FeeDgOngoingTariff> licenseAdviserFees) {
        LicenseAdviserFeeDto licenseAdviserFeeDto = null;
        if (null != licenseAdviserFees && !licenseAdviserFees.isEmpty()) {
            licenseAdviserFeeDto = new LicenseAdviserFeeDto();
            FeeComponentType feeComponentType = new FeeComponentType();
            Map<String, BaseFeeType> feeComponents = new HashMap();
            for (FeeDgOngoingTariff feeDgOngoingTariff : licenseAdviserFees) {
                feeComponents = populateLicenseAdviserFeeTariff(feeDgOngoingTariff, feeComponents);

            }
            feeComponentType.setFeeType(feeComponents);
            feeComponentType.setType(LICENSE_FEE);
            licenseAdviserFeeDto.setFeeComponentType(feeComponentType);
        }
        return licenseAdviserFeeDto;
    }

    private Map<String, BaseFeeType> populateLicenseAdviserFeeTariff(FeeDgOngoingTariff feeDgOngoingTariff,
            Map<String, BaseFeeType> feeComponents) {

        switch (feeDgOngoingTariff.getCostMiscType()) {
            case DOLLAR_FEE:
                dollarFeeAmount(feeDgOngoingTariff, feeComponents);
                break;
            case PERCENT_MANAGED_PORTFOLIO:
            case PERCENT_TERM_DEPOSIT:
            case PERCENT_CASH:
            case PERCENT_MANAGED_FUND:
            case PERCENT_SHARE:
                tariffFeeAmount(feeDgOngoingTariff, feeComponents);
                break;
            case SLIDING_SCALE_FEE:
                scallingFeeAmount(feeDgOngoingTariff, feeComponents);
                break;
            default:
                break;

        }
        return feeComponents;
    }

    private void dollarFeeAmount(FeeDgOngoingTariff feeDgOngoingTariff, Map<String, BaseFeeType> feeComponents) {
        DollarFeeAmount dollarFeeAmount = new DollarFeeAmount();
        Map<String, String> fees = new HashMap<>();
        dollarFeeAmount.setCpiIndex(feeDgOngoingTariff.isCpi());
        dollarFeeAmount.setLabel(DOLLAR_FEE.getLabel());
        fees.put(DOLLAR_FEE.getLabel(), feeDgOngoingTariff.getTariffAmnt().toString());
        dollarFeeAmount.setFees(fees);
        feeComponents.put(DOLLAR_FEE.getLabel(), dollarFeeAmount);
    }

    private void tariffFeeAmount(FeeDgOngoingTariff feeDgOngoingTariff, Map<String, BaseFeeType> feeComponents) {

        PercentageFee percentageFee = (PercentageFee) feeComponents.get(PERCENT_CASH.getLabel());
        if (null == percentageFee) {
            percentageFee = new PercentageFee();
        }
        Map<String, String> fees = percentageFee.getFees();
        if (null == fees) {
            fees = new HashMap<>();
        }
        percentageFee.setLabel(PERCENT_CASH.getLabel());

        fees.put(feeDgOngoingTariff.getCostMiscType().getLabel(),
                decimalFormatter((feeDgOngoingTariff.getTariffFactor().multiply(TARIFFACTOR)).toString()));
        percentageFee.setFees(fees);
        feeComponents.put(PERCENT_CASH.getLabel(), percentageFee);
    }

    private String decimalFormatter(String tariffFactor) {
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(Double.parseDouble(tariffFactor));
    }

    private void scallingFeeAmount(FeeDgOngoingTariff feeDgOngoingTariff, Map<String, BaseFeeType> feeComponents) {

        SlidingScaleFee slidingScaleFee = new SlidingScaleFee();
        Map<String, String> fees;
        List<SlidingScaleFeeTier> slidingScaleFeeTierList;
        slidingScaleFee.setLabel(SLIDING_SCALE_FEE.getLabel());
        if (null != feeDgOngoingTariff.getFeeDgOngoingTariffBoundList()
                && !feeDgOngoingTariff.getFeeDgOngoingTariffBoundList().isEmpty()) {
            slidingScaleFeeTierList = new ArrayList<>();
            for (FeeDgOngoingTariffBound feeDgOngoingTariffBound : feeDgOngoingTariff.getFeeDgOngoingTariffBoundList()) {
                String percentageFeeTariff = decimalFormatter(
                        (feeDgOngoingTariffBound.getTariffFactor().multiply(TARIFFACTOR)).toString());
                SlidingScaleFeeTier slidingScaleFeeTier = new SlidingScaleFeeTier();
                slidingScaleFeeTier.setLowerBound(
                        null != feeDgOngoingTariffBound.getBoundFrom() ? feeDgOngoingTariffBound.getBoundFrom().toString() : "");
                slidingScaleFeeTier.setUpperBound(
                        null != feeDgOngoingTariffBound.getBoundTo() ? feeDgOngoingTariffBound.getBoundTo().toString() : "");
                slidingScaleFeeTier.setPercentage(null != feeDgOngoingTariffBound.getTariffFactor() ? percentageFeeTariff : "");

                slidingScaleFeeTierList.add(slidingScaleFeeTier);
            }
            slidingScaleFee.setScaleFeeTierList(slidingScaleFeeTierList);
        }
        if (null != feeDgOngoingTariff.getFeeDgOngoingApplyDefList()
                && !feeDgOngoingTariff.getFeeDgOngoingApplyDefList().isEmpty()) {
            fees = new HashMap<>();
            for (FeeDgOngoingApplyDef feeDgOngoingApplyDef : feeDgOngoingTariff.getFeeDgOngoingApplyDefList()) {
                fees.put(feeDgOngoingApplyDef.getfeesMiscType().getLabel(), "true");
            }
            slidingScaleFee.setFees(fees);

        }
        feeComponents.put(SLIDING_SCALE_FEE.getLabel(), slidingScaleFee);

    }
}
