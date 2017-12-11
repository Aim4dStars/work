package com.bt.nextgen.api.corporateaction.v1.service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsBaseDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDtoKey;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterFactory;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionResponseConverterService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionGroup;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Corporate action dto service implementation
 */

@Service
public class CorporateActionDetailsDtoServiceImpl implements CorporateActionDetailsDtoService {
    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    @Autowired
    private CorporateActionHelper helper;

    @Autowired
    private CorporateActionAccountDetailsDtoService corporateActionAccountDetails;

    @Autowired
    private CorporateActionPersistenceDtoService corporateActionPersistenceDtoService;

    @Autowired
    private CorporateActionCommonService corporateActionCommonService;

    @Autowired
    private CorporateActionConverterFactory corporateActionConverterFactory;

    @Autowired
    private ImCorporateActionPortfolioModelDtoService imCorporateActionPortfolioModelDtoService;

    @Autowired
    private CorporateActionServices corporateActionServices;

    /**
     * Main search to retrieve a list of corporate actions
     *
     * @param key           the corporate action dto key
     * @param serviceErrors the service errors object
     * @return a list of corporate action dto's within the date range.
     */
    @Override
    public CorporateActionDetailsBaseDto find(CorporateActionDtoKey key, ServiceErrors serviceErrors) {
        if (corporateActionCommonService.getUserProfileService().isDealerGroup()
                || corporateActionCommonService.getUserProfileService().isInvestmentManager()
                || corporateActionCommonService.getUserProfileService().isPortfolioManager()) {
            final String imId = corporateActionCommonService.getUserProfileService().getPositionId();

            CorporateActionContext context =
                    corporateActionServices.loadCorporateActionDetailsContextForIm(imId, key.getId(), serviceErrors);

            context.setBrokerPositionId(imId);
            context.setDealerGroup(corporateActionCommonService.getUserProfileService().isDealerGroup()
                    || corporateActionCommonService.getUserProfileService().isPortfolioManager());
            context.setInvestmentManager(corporateActionCommonService.getUserProfileService().isInvestmentManager());

            // For dealer group account election drill down screen
            // Return single portfolioModel and list of relevant accounts
            if (!StringUtils.isEmpty(key.getIpsId())) {
                context.setIpsId(key.getIpsId());

                return toCorporateActionDetailsDtoForDg(context, serviceErrors);
            }

            // Return list of portfolioModels
            return toCorporateActionDetailsDtoForIm(context, serviceErrors);
        }

        // Return list of accounts
        CorporateActionContext context =
                corporateActionServices.loadCorporateActionDetailsContext(key.getId(), key.getSummaryOnly(), serviceErrors);
        context.setAccountId(key.getAccountId());

        return toCorporateActionDetailsDto(context, serviceErrors);
    }

    /**
     * Converts Avaloq corporate action details object to corporate action details DTO
     *
     * @param context       corporate action context object
     * @param serviceErrors the service errors object
     * @return CorporateActionDetailsDto
     */
    private CorporateActionDetailsBaseDto toCorporateActionDetailsDto(CorporateActionContext context,
                                                                      ServiceErrors serviceErrors) {
        if (context.getCorporateActionDetails() != null) {
            CorporateActionDetailsDtoParams params = createCommonParams(context, serviceErrors);

            CorporateActionSavedDetails corporateActionSavedDetails =
                    getSavedDetails(context.getCorporateActionDetails().getOrderNumber(), params);

            if (corporateActionSavedDetails != null) {
                params.setResponseCode(corporateActionSavedDetails.getResponseCode());
            }

            filterAccounts(context);

            params.setAccounts(corporateActionAccountDetails
                    .toCorporateActionAccountDtoList(context, context.getCorporateActionAccountList(), corporateActionSavedDetails,
                            serviceErrors));

            CorporateActionResponseConverterService responseConverter =
                    corporateActionConverterFactory.getResponseConverterService(context.getCorporateActionDetails());

            responseConverter.setCorporateActionDetailsDtoParams(context, params);

            return new CorporateActionDetailsDto(params);
        }

        return new CorporateActionDetailsDto();
    }

    private CorporateActionSavedDetails getSavedDetails(String orderNumber, CorporateActionDetailsDtoParams params) {
        boolean notInvestor = !corporateActionCommonService.getUserProfileService().isInvestor();
        boolean notIM = !corporateActionCommonService.getUserProfileService().isInvestmentManager();
        return notInvestor && notIM ? corporateActionPersistenceDtoService
                .loadAndValidateElectedOptions(orderNumber, params.getOptions()) : null;
    }

    private void filterAccounts(CorporateActionContext context) {
        if (StringUtils.isNotEmpty(context.getAccountId())) {
            CorporateActionAccount account =
                    selectFirst(context.getCorporateActionAccountList(),
                            having(on(CorporateActionAccount.class).getAccountId(), equalTo(context.getAccountId())));

            List<CorporateActionAccount> filterAccounts = new ArrayList<>(1);

            if (account != null) {
                filterAccounts.add(account);
            }

            context.setCorporateActionAccountList(filterAccounts);
        }
    }

    private CorporateActionDetailsBaseDto toCorporateActionDetailsDtoForIm(CorporateActionContext context, ServiceErrors serviceErrors) {
        if (context.getCorporateActionDetails() != null) {
            CorporateActionDetailsDtoParams params = createCommonParams(context, serviceErrors);

            params.setPortfolioModels(
                    imCorporateActionPortfolioModelDtoService
                            .toCorporateActionPortfolioModelDto(context, context.getCorporateActionAccountList(), null, serviceErrors));

            CorporateActionResponseConverterService responseConverter =
                    corporateActionConverterFactory.getResponseConverterService(context.getCorporateActionDetails());

            responseConverter.setCorporateActionDetailsDtoParams(context, params);

            // Oversubscribe is never available to IM
            params.setOversubscribe(Boolean.FALSE);

            return new ImCorporateActionDetailsDto(params);
        }

        return new ImCorporateActionDetailsDto();
    }

    private CorporateActionDetailsBaseDto toCorporateActionDetailsDtoForDg(CorporateActionContext context, ServiceErrors serviceErrors) {
        if (context.getCorporateActionDetails() != null) {
            CorporateActionDetailsDtoParams params = createCommonParams(context, serviceErrors);

            CorporateActionSavedDetails corporateActionSavedDetails =
                    getSavedDetails(context.getCorporateActionDetails().getOrderNumber(), params);
            if (corporateActionSavedDetails != null) {
                params.setResponseCode(corporateActionSavedDetails.getResponseCode());
            }

            // Retrieve list of actionAccount including shadow-portfolio.
            List<CorporateActionAccount> list =
                    helper.filterByShadowPortfolioAccounts(context.getCorporateActionAccountList(), context.getIpsId());
            list.addAll(helper.filterByManagedPortfolioAccounts(context.getCorporateActionAccountList(), context.getIpsId()));

            params.setPortfolioModels(imCorporateActionPortfolioModelDtoService.toCorporateActionPortfolioModelDto(context,
                    list, corporateActionSavedDetails, serviceErrors));
            params.setAccounts(corporateActionAccountDetails.toCorporateActionAccountDtoList(context,
                    helper.filterByManagedPortfolioAccounts(context.getCorporateActionAccountList(), context.getIpsId()),
                    corporateActionSavedDetails, serviceErrors));

            CorporateActionResponseConverterService responseConverter = corporateActionConverterFactory
                    .getResponseConverterService(context.getCorporateActionDetails());

            responseConverter.setCorporateActionDetailsDtoParams(context, params);

            // Oversubscribe is never available to DG
            params.setOversubscribe(Boolean.FALSE);

            return new ImCorporateActionDetailsDto(params);
        }

        return new ImCorporateActionDetailsDto();
    }

    private CorporateActionDetailsDtoParams createCommonParams(CorporateActionContext context,
                                                               ServiceErrors serviceErrors) {
        CorporateActionDetailsDtoParams params = new CorporateActionDetailsDtoParams();

        CorporateActionDetails details = context.getCorporateActionDetails();

        params.setAsset(assetIntegrationService.loadAsset(details.getAssetId(), serviceErrors));
        params.setCloseDate(details.getCloseDate());
        params.setLastUpdatedDate(details.getLastUpdatedDate());
        params.setPayDate(details.getPayDate());
        params.setRecordDate(details.getRecordDate());
        params.setExDate(details.getExDate());
        params.setPartialElection(false);

        EffectiveCorporateActionType effectiveCorporateActionType = helper.getEffectiveCorporateActionType(details);
        params.setCorporateActionType(effectiveCorporateActionType.getCode());
        params.setCorporateActionTypeDescription(effectiveCorporateActionType.getDescription());

        CorporateActionResponseConverterService responseConverter = corporateActionConverterFactory.getResponseConverterService(details);

        params.setOptions(responseConverter.toElectionOptionDtos(context, serviceErrors));
        params.setOfferDocumentUrl(details.getOfferDocumentUrl());
        params.setSummary(responseConverter.toSummaryList(context, serviceErrors));
        params.setMandatory(CorporateActionGroup.MANDATORY.equals(details.getCorporateActionType().getGroup()));

        // Derive the corporate action status
        params.setStatus(helper.generateCorporateActionStatus(details.getCorporateActionType().getGroup(),
                details.getCorporateActionStatus(), details.getCloseDate(), details.getPayDate(), new DateTime()));

        params.setCurrentAssetPrice(corporateActionCommonService.getAssetPrice(params.getAsset(), serviceErrors));

        // Trustee approval status
        params.setTrusteeApprovalStatus(details.getTrusteeApprovalStatus());
        params.setEarlyClose(details.isEarlyClose());
        return params;
    }
}
