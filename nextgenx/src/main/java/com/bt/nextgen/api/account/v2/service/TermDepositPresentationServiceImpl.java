package com.bt.nextgen.api.account.v2.service;

import com.bt.nextgen.api.termdeposit.service.TermDepositCalculatorUtils;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.bt.nextgen.termdeposit.service.TermDepositAssetRateSearchKey;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.TermDepositAsset;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Deprecated
@Service
public class TermDepositPresentationServiceImpl implements TermDepositPresentationService {

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Qualifier("avaloqAssetIntegrationService")
    @Autowired
    private AssetIntegrationService assetIntegrationService;

    @Autowired
    private FeatureTogglesService featureTogglesService;

    @Autowired
    private CmsService cmsService;

    @Autowired
    private TermDepositCalculatorUtils termDepositCalculatorUtils;

    @Override
    public TermDepositPresentation getTermDepositPresentation(AccountKey accountKey,String assetId, ServiceErrors serviceErrors) {

        String brandName = Constants.EMPTY_STRING;
        String brandClass = Constants.EMPTY_STRING;
        String term = Constants.EMPTY_STRING;
        String paymentFrequency = Constants.EMPTY_STRING;
        boolean termDepositToggle = featureTogglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle("termDepositToggle");

        if (StringUtils.isNotEmpty(assetId)) {
            TermDepositAsset termDepositAsset = (TermDepositAsset) assetIntegrationService.loadAsset(assetId, serviceErrors);

            if (termDepositAsset != null) {
                brandName = getTermDepositBrandName(termDepositAsset);
                brandClass = getTermDepositBrandClass(termDepositAsset);

                if(!termDepositToggle){
                    TermDepositAssetDetail termDepositAssetDetail = getRates(accountKey, termDepositAsset.getGenericAssetId(),
                            serviceErrors);

                    if (termDepositAssetDetail != null) {
                        if (termDepositAssetDetail.getTerm() != null) {
                            term = termDepositAssetDetail.getTerm().toString().toLowerCase();
                        }
                        if (termDepositAssetDetail.getPaymentFrequency() != null) {
                            paymentFrequency = termDepositAssetDetail.getPaymentFrequency().getDisplayName().toLowerCase();
                        }
                    }
                }else{

                    List<TermDepositInterestRate> termDepositAssetDetails = getTermDepositInterestRates(accountKey,termDepositAsset.getGenericAssetId(),
                            serviceErrors);

                    if (CollectionUtils.isNotEmpty(termDepositAssetDetails)) {
                        if (termDepositAssetDetails.get(0).getTerm() != null) {
                            term = termDepositAssetDetails.get(0).getTerm().toString().toLowerCase();
                        }
                        if (termDepositAssetDetails.get(0).getPaymentFrequency() != null) {
                            paymentFrequency = termDepositAssetDetails.get(0).getPaymentFrequency().getDisplayName().toLowerCase();
                        }
                    }
                }

            }
        }

        TermDepositPresentation termDepositPresentation = new TermDepositPresentation();
        termDepositPresentation.setBrandName(brandName);
        termDepositPresentation.setBrandClass(brandClass);
        termDepositPresentation.setTerm(term);
        termDepositPresentation.setPaymentFrequency(paymentFrequency);

        return termDepositPresentation;
    }

    private String getTermDepositBrandClass(TermDepositAsset asset) {
        if (StringUtils.isNotEmpty(asset.getBrand())) {
            return cmsService.getContent(asset.getBrand() + "_class");
        }
        return Constants.EMPTY_STRING;
    }

    private String getTermDepositBrandName(TermDepositAsset asset) {
        if (StringUtils.isNotEmpty(asset.getBrand())) {
            return cmsService.getContent(Constants.TD_BRAND_PREFIX + asset.getBrand()) + " Term Deposit";
        }
        return Constants.EMPTY_STRING;
    }

    private TermDepositAssetDetail getRates(AccountKey accountKey, String assetId, ServiceErrors serviceErrors) {

        WrapAccount account = accountIntegrationService.loadWrapAccountWithoutContainers(accountKey, serviceErrors);
        BrokerKey brokerKey = account.getAdviserPositionId();

        if (assetId != null && brokerKey != null && brokerKey.getId() != null) {
            Asset asset = assetIntegrationService.loadAsset(assetId, serviceErrors);


                Map<String, TermDepositAssetDetail> assetRates = assetIntegrationService.loadTermDepositRates(brokerKey,
                        DateTime.now(), Collections.singletonList(asset), serviceErrors);
                if (assetRates != null) {
                    return assetRates.get(assetId);
                }


        }
        return null;
    }

    private List<TermDepositInterestRate> getTermDepositInterestRates(AccountKey accountKey,String assetId, ServiceErrors serviceErrors) {

        WrapAccount account = accountIntegrationService.loadWrapAccountWithoutContainers(accountKey, serviceErrors);
        BrokerKey brokerKey = account.getAdviserPositionId();
        final DateTime bankDate = termDepositCalculatorUtils.getBankDate();

        List<String> assetList = new ArrayList<>(Collections.singleton(assetId));
        if (assetId != null && brokerKey != null && brokerKey.getId() != null) {
            TermDepositAssetRateSearchKey termDepositAssetRateSearchKey = new TermDepositAssetRateSearchKey(account.getProductKey(),brokerKey,null,account.getAccountStructureType(),bankDate,assetList);
            List<TermDepositInterestRate> assetRates = assetIntegrationService.loadTermDepositRates(termDepositAssetRateSearchKey, serviceErrors);

            if (CollectionUtils.isNotEmpty(assetRates)) {
                return assetRates;
            }


        }
        return null;
    }
}
