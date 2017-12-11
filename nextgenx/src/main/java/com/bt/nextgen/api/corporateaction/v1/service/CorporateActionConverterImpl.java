package com.bt.nextgen.api.corporateaction.v1.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionApprovalDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionBaseDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListResult;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.ShareAsset;
import com.bt.nextgen.service.integration.corporateaction.CorporateAction;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionStatus;
import com.bt.nextgen.service.integration.trustee.IrgApprovalStatus;
import com.bt.nextgen.service.integration.trustee.TrusteeApprovalStatus;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static org.hamcrest.core.IsEqual.equalTo;

@Service
public class CorporateActionConverterImpl implements CorporateActionConverter {
    private static final Logger logger = LoggerFactory.getLogger(CorporateActionConverterImpl.class);

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    private CorporateActionHelper helper;

    @Override
    public CorporateActionListDto toCorporateActionListDto(CorporateActionGroup group, CorporateActionListResult corporateActionListResult,
                                                           String accountId, ServiceErrors serviceErrors) {
        List<CorporateActionDtoParams> dtoParamsList =
                createCommonCorporateActionDtoParamsList(group, corporateActionListResult.getCorporateActions(), false, serviceErrors);
        final String accountNumber = accountId != null ? getAccountNumber(accountId, serviceErrors) : null;

        CorporateActionListDto corporateActionListDto =
                new CorporateActionListDto(corporateActionListResult.getHasSuperPension(), new ArrayList<CorporateActionBaseDto>());

        for (CorporateActionDtoParams params : dtoParamsList) {
            params.setAccountId(accountNumber);
            corporateActionListDto.getCorporateActions().add(new CorporateActionDto(params.getOrderNumber(), params));
        }

        return corporateActionListDto;
    }

    @Override
    public CorporateActionListDto toCorporateActionApprovalListDto(CorporateActionGroup group,
                                                                   CorporateActionListResult corporateActionListResult,
                                                                   ServiceErrors serviceErrors) {
        List<CorporateActionDtoParams> dtoParamsList =
                createCommonCorporateActionDtoParamsList(group, corporateActionListResult.getCorporateActions(), false, serviceErrors);

        CorporateActionListDto corporateActionListDto =
                new CorporateActionListDto(corporateActionListResult.getHasSuperPension(), new ArrayList<CorporateActionBaseDto>());

        for (CorporateActionDtoParams params : dtoParamsList) {
            corporateActionListDto.getCorporateActions().add(new CorporateActionApprovalDto(params.getOrderNumber(), params));
        }

        return corporateActionListDto;
    }

    @Override
    public CorporateActionListDto toCorporateActionListDtoForIm(CorporateActionGroup group,
                                                                CorporateActionListResult corporateActionListResult,
                                                                String portfolioModelId, ServiceErrors serviceErrors) {
        List<CorporateActionDtoParams> dtoParamsList =
                createCommonCorporateActionDtoParamsList(group, corporateActionListResult.getCorporateActions(), true, serviceErrors);

        CorporateActionListDto corporateActionListDto =
                new CorporateActionListDto(corporateActionListResult.getHasSuperPension(), new ArrayList<CorporateActionBaseDto>());

        for (CorporateActionDtoParams params : dtoParamsList) {
            params.setPortfolioModelId(portfolioModelId);
            corporateActionListDto.getCorporateActions().add(new ImCorporateActionDto(params.getOrderNumber(), params));
        }

        return corporateActionListDto;
    }

    private List<CorporateActionDtoParams> createCommonCorporateActionDtoParamsList(CorporateActionGroup group,
                                                                                    List<CorporateAction> corporateActions,
                                                                                    boolean isDgIm,
                                                                                    ServiceErrors serviceErrors) {
        List<CorporateActionDtoParams> paramsList = new ArrayList<>();
        final List<String> assetIds = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(corporateActions)) {
            // Build up asset id's for bulk retrieval
            for (CorporateAction ca : corporateActions) {
                assetIds.add(ca.getAssetId());
            }

            // logger.info("Invoking Asset service for asset Ids {}", assetIds.size());

            final Map<String, Asset> assetsMap = assetIntegrationService.loadAssets(assetIds, serviceErrors);
            final DateTime currentDateTime = new DateTime();

            List<CorporateAction> corporateActionFilteredList = select(corporateActions,
                    having(on(CorporateAction.class).getVoluntaryFlag(), equalTo(group.getId())));

            // Go through each and create the dto object
            for (CorporateAction ca : corporateActionFilteredList) {
                // Retrieve the asset details based on Avaloq's asset ID
                if (ca.getCorporateActionType() != null && ca.getCorporateActionType().isAvailable(isDgIm)) {
                    CorporateActionStatus status =
                            helper.generateCorporateActionStatus(group, ca.getCorporateActionStatus(), ca.getCloseDate(),
                                    ca.getPayDate(), currentDateTime);

                    final Asset asset = assetsMap.get(ca.getAssetId());
                    CorporateActionDtoParams params = new CorporateActionDtoParams();

                    params.setOrderNumber(EncodedString.fromPlainText(ca.getOrderNumber()).toString());
                    params.setCloseDate(ca.getCloseDate());
                    params.setAnnouncementDate(ca.getAnnouncementDate());
                    params.setAsset(asset);

                    EffectiveCorporateActionType effectiveCorporateActionType = helper.getEffectiveCorporateActionType(
                            ca.getCorporateActionType(), ca.getCorporateActionOfferType(), ca.getCorporateActionSecurityExchangeType(),
                            ca.isNonProRata());

                    params.setCorporateActionType(effectiveCorporateActionType.getCode());
                    params.setCorporateActionTypeDescription(effectiveCorporateActionType.getDescription());
                    params.setStatus(status);
                    params.setEligible(ca.getEligible());
                    params.setUnconfirmed(ca.getUnconfirmed());
                    params.setPayDate(ca.getPayDate());
                    params.setTrusteeApprovalStatus(
                            ca.getTrusteeApprovalStatus() != null ? ca.getTrusteeApprovalStatus() : TrusteeApprovalStatus.PENDING);
                    params.setTrusteeApprovalStatusDate(ca.getTrusteeApprovalStatusDate());
                    params.setTrusteeApprovalUserId(ca.getTrusteeApprovalUserId());
                    params.setTrusteeApprovalUserName(ca.getTrusteeApprovalUserName());
                    params.setIrgApprovalStatus(ca.getIrgApprovalStatus() != null ? ca.getIrgApprovalStatus() : IrgApprovalStatus.PENDING);
                    params.setIrgApprovalStatusDate(ca.getIrgApprovalStatusDate());
                    params.setIrgApprovalUserId(ca.getIrgApprovalUserId());
                    params.setIrgApprovalUserName(ca.getIrgApprovalUserName());
                    params.setEarlyClose(ca.isEarlyClose() ? "Yes" : "No");

                    if (asset instanceof ShareAsset) {
                        params.setHoldingLimitPercent(((ShareAsset) asset).getInvestmentHoldingLimit());
                    }

                    paramsList.add(params);
                }
            }
        }

        return paramsList;
    }

    private String getAccountNumber(String accountId, ServiceErrors serviceErrors) {
        Map<AccountKey, WrapAccount> accountMap = accountService.loadWrapAccountWithoutContainers(serviceErrors);
        WrapAccount wrapAccount = accountMap.get(AccountKey.valueOf(accountId));

        if (wrapAccount != null) {
            return wrapAccount.getAccountNumber();
        }

        return null;
    }
}
