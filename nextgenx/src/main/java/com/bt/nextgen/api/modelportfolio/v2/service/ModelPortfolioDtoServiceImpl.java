package com.bt.nextgen.api.modelportfolio.v2.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.modelportfolio.v2.model.CashForecastDto;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioDto;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.ShadowPortfolioDto;
import com.bt.nextgen.api.modelportfolio.v2.model.ShadowTransactionDto;
import com.bt.nextgen.api.modelportfolio.v2.util.common.ModelPortfolioHelper;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.Constants;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentData;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentIntegrationService;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentType;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.CashForecast;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolio;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummary;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummaryIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolio;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolioAsset;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolioAssetSummary;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolioDetail;
import com.bt.nextgen.service.integration.modelportfolio.ShadowTransaction;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

@Service("ModelPortfolioDtoServiceV2")
@Transactional(value = "springJpaTransactionManager")
// TODO break this cleass up
@SuppressWarnings("squid:S1200")
public class ModelPortfolioDtoServiceImpl implements ModelPortfolioDtoService {

    @Autowired
    private ModelPortfolioIntegrationService modelPortfolioService;

    @Autowired
    private ModelPortfolioSummaryIntegrationService modelPortfolioSummaryService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private BrokerIntegrationService brokerService;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Autowired
    private FinancialDocumentIntegrationService documentService;

    @Autowired
    private ModelPortfolioHelper helper;

    @Override
    public ModelPortfolioDto find(ModelPortfolioKey modelKey, ServiceErrors serviceErrors) {
        BrokerKey brokerKey = helper.getCurrentBroker(serviceErrors);
        ModelPortfolio modelPortfolio = modelPortfolioService.loadModel(IpsKey.valueOf(modelKey.getModelId()), serviceErrors);
        Collection<ModelPortfolioSummary> summaries = modelPortfolioSummaryService.loadModels(brokerKey, serviceErrors);
        ModelPortfolioSummary summary = getSummaryForModel(modelPortfolio.getModelKey(), summaries);
        return toModelPortfolioDto(modelPortfolio, summary, serviceErrors);
    }

    @Override
    public List<ModelPortfolioDto> findAll(ServiceErrors serviceErrors) {
        BrokerKey brokerKey = helper.getCurrentBroker(serviceErrors);
        Collection<ModelPortfolioSummary> summaries = modelPortfolioSummaryService.loadModels(brokerKey, serviceErrors);
        return toModelPortfolioDto(modelPortfolioService.loadModels(brokerKey, serviceErrors), summaries, serviceErrors);
    }

    protected List<ModelPortfolioDto> toModelPortfolioDto(Collection<ModelPortfolio> models,
            Collection<ModelPortfolioSummary> summaries, ServiceErrors serviceErrors) {
        List<ModelPortfolioDto> modelPortfolioDtos = new ArrayList<>();

        if (models != null) {
            for (ModelPortfolio model : models) {
                modelPortfolioDtos
                        .add(toModelPortfolioDto(model, getSummaryForModel(model.getModelKey(), summaries), serviceErrors));
            }
        }
        return modelPortfolioDtos;
    }

    protected ModelPortfolioDto toModelPortfolioDto(ModelPortfolio model, ModelPortfolioSummary summary,
            ServiceErrors serviceErrors) {
        Map<String, Asset> assets = getAssetsForModels(model, serviceErrors);

        ModelPortfolioDto modelPortfolioDto = new ModelPortfolioDto(model, summary, toCashForecastDto(model.getCashForecast()),
                toShadowPortfolioDto(model.getShadowPortfolio(), assets),
                toShadowTransactionDto(model.getShadowTransactions(), assets));

        return modelPortfolioDto;
    }

    private ModelPortfolioSummary getSummaryForModel(IpsKey modelKey, Collection<ModelPortfolioSummary> summaries) {
        for (ModelPortfolioSummary summary : summaries) {
            if (summary.getModelKey().equals(modelKey))
                return summary;
        }
        return null;
    }

    protected CashForecastDto toCashForecastDto(CashForecast cashForecast) {
        if (cashForecast != null) {
            return new CashForecastDto(cashForecast.getAmountToday(), cashForecast.getAmountTodayPlus1(),
                    cashForecast.getAmountTodayPlus2(), cashForecast.getAmountTodayPlus3(), cashForecast.getAmountTodayPlusMax());
        }

        return null;
    }

    protected ShadowTransactionDto toShadowTransactionDto(ShadowTransaction transaction, AssetType assetClass) {
        return new ShadowTransactionDto(transaction.getTransactionId(), transaction.getTransactionType(),
                transaction.getAssetHolding(), transaction.getStatus(), transaction.getTradeDate(), transaction.getValueDate(),
                transaction.getPerformanceDate(), AssetType.CASH.equals(assetClass) ? transaction.getAmount() : null,
                !AssetType.CASH.equals(assetClass) ? transaction.getAmount() : null, transaction.getUnitPrice(),
                transaction.getDescription(), assetClass != null ? assetClass.getDisplayName() : null);
    }

    protected List<ShadowTransactionDto> toShadowTransactionDto(List<ShadowTransaction> transactions, Map<String, Asset> assets) {
        List<ShadowTransactionDto> shadowTransactionDtos = getTransactionsDtos(transactions, assets);

        if (!shadowTransactionDtos.isEmpty()) {

            Collections.sort(shadowTransactionDtos, new Comparator<ShadowTransactionDto>() {
                @Override
                public int compare(ShadowTransactionDto o1, ShadowTransactionDto o2) {
                    if (o1.getTradeDate() == null || o2.getTradeDate() == null)
                        return 0;
                    else if (o1.getTradeDate().compareTo(o2.getTradeDate()) == 0) {
                        return o1.getTransactionId().compareTo(o2.getTransactionId());
                    }
                    return o1.getTradeDate().compareTo(o2.getTradeDate());
                }
            });
        }

        return shadowTransactionDtos;
    }

    private List<ShadowTransactionDto> getTransactionsDtos(List<ShadowTransaction> transactions, Map<String, Asset> assets) {
        List<ShadowTransactionDto> shadowTransactionDtos = new ArrayList<>();
        if (transactions != null) {
            for (ShadowTransaction transaction : transactions) {
                Asset asset = assets.get(transaction.getAssetId());
                shadowTransactionDtos.add(toShadowTransactionDto(transaction, asset == null ? null : asset.getAssetType()));
            }
        }
        return shadowTransactionDtos;
    }

    protected Map<String, Asset> getAssetsForModels(ModelPortfolio model, ServiceErrors serviceErrors) {
        Set<String> assetIds = new HashSet<>();

        if (model.getShadowPortfolio() != null) {
            for (ShadowPortfolioAssetSummary summary : model.getShadowPortfolio().getAssetSummaries()) {
                for (ShadowPortfolioAsset shadowAsset : summary.getAssets()) {
                    assetIds.add(shadowAsset.getAssetId());
                }
            }
        }

        if (model.getShadowTransactions() != null) {
            for (ShadowTransaction transaction : model.getShadowTransactions()) {
                assetIds.add(transaction.getAssetId());
            }
        }

        return assetService.loadAssets(assetIds, serviceErrors);
    }

    public List<ShadowPortfolioDto> toShadowPortfolioDto(ShadowPortfolio shadowPortfolio, Map<String, Asset> assets) {
        List<ShadowPortfolioDto> shadowPortfolioReportData = new ArrayList<>();
        if (shadowPortfolio != null) {
            List<ShadowPortfolioAssetSummary> shadowPortfolioAssetSummaries = getSortedAssetSummaries(
                    shadowPortfolio.getAssetSummaries());
            if (shadowPortfolioAssetSummaries != null) {
                for (ListIterator<ShadowPortfolioAssetSummary> iter = shadowPortfolioAssetSummaries.listIterator(); iter
                        .hasNext();) {
                    ShadowPortfolioAssetSummary shadowPortfolioAssetSummary = iter.next();
                    List<ShadowPortfolioAsset> shadowPortfolioAssets = getSortedAssets(shadowPortfolioAssetSummary.getAssets(),
                            assets);
                    ShadowPortfolioDetail shadowPortfolioTotal = shadowPortfolioAssetSummary.getTotal();
                    if (shadowPortfolioAssets != null) {
                        for (ListIterator<ShadowPortfolioAsset> iterator = shadowPortfolioAssets.listIterator(); iterator
                                .hasNext();) {
                            ShadowPortfolioAsset shadowPortfolioAsset = iterator.next();

                            shadowPortfolioReportData.add(new ShadowPortfolioDto(shadowPortfolioAssetSummary.getAssetClass(),
                                    getAssetCodeForCash(shadowPortfolioAssetSummary.getAssetClass(),
                                            shadowPortfolioAsset.getAssetId(), assets),
                                    getAssetNameForCash(shadowPortfolioAssetSummary.getAssetClass(),
                                            shadowPortfolioAsset.getAssetId(), assets),
                                    shadowPortfolioAsset.getShadowDetail().getLastUpdatedTargetPercent(),
                                    shadowPortfolioAsset.getShadowDetail().getFloatingTargetPercent(),
                                    shadowPortfolioAsset.getShadowDetail().getUnits(),
                                    shadowPortfolioAsset.getShadowDetail().getMarketValue(),
                                    shadowPortfolioAsset.getShadowDetail().getShadowPercent(),
                                    shadowPortfolioAsset.getShadowDetail().getDifferencePercent(), false));

                        }
                    }
                    shadowPortfolioReportData.add(new ShadowPortfolioDto("TOTAL " + shadowPortfolioAssetSummary.getAssetClass(),
                            getAssetCodeForTotal(shadowPortfolioAssetSummary.getAssetClass()), "",
                            shadowPortfolioTotal.getLastUpdatedTargetPercent(), shadowPortfolioTotal.getFloatingTargetPercent(),
                            shadowPortfolioTotal.getUnits(), shadowPortfolioTotal.getMarketValue(),
                            shadowPortfolioTotal.getShadowPercent(), shadowPortfolioTotal.getDifferencePercent(), true));
                }
            }
            shadowPortfolioReportData
                    .add(new ShadowPortfolioDto("", "", "", shadowPortfolio.getTotal().getLastUpdatedTargetPercent(),
                            shadowPortfolio.getTotal().getFloatingTargetPercent(), shadowPortfolio.getTotal().getUnits(),
                            shadowPortfolio.getTotal().getMarketValue(), shadowPortfolio.getTotal().getShadowPercent(),
                            shadowPortfolio.getTotal().getDifferencePercent(), true));

        }
        return shadowPortfolioReportData;

    }

    private List<ShadowPortfolioAsset> getSortedAssets(List<ShadowPortfolioAsset> shadowAssets, final Map<String, Asset> assets) {
        if (!shadowAssets.isEmpty()) {
            Collections.sort(shadowAssets, new Comparator<ShadowPortfolioAsset>() {
                @Override
                public int compare(final ShadowPortfolioAsset object1, final ShadowPortfolioAsset object2) {
                    Asset asset1 = assets.get(object1.getAssetId());
                    Asset asset2 = assets.get(object2.getAssetId());

                    if (asset1 == null || asset1.getAssetCode() == null) {
                        return -1;
                    }
                    if (asset2 == null || asset2.getAssetCode() == null) {
                        return -1;
                    }
                    return assets.get(object1.getAssetId()).getAssetCode()
                            .compareTo(assets.get(object2.getAssetId()).getAssetCode());
                }
            });
        }
        return shadowAssets;
    }

    private List<ShadowPortfolioAssetSummary> getSortedAssetSummaries(List<ShadowPortfolioAssetSummary> assetSummaries) {
        if (!assetSummaries.isEmpty()) {
            Collections.sort(assetSummaries, new Comparator<ShadowPortfolioAssetSummary>() {
                @Override
                public int compare(final ShadowPortfolioAssetSummary object1, final ShadowPortfolioAssetSummary object2) {
                    if (object1.getAssetClass() == null || object2.getAssetClass() == null)
                        return 0;
                    if (object1.getAssetClass().equalsIgnoreCase(Constants.ASSET_TYPE_CASH)) {
                        return 1;
                    }
                    if (object2.getAssetClass().equalsIgnoreCase(Constants.ASSET_TYPE_CASH)) {
                        return 2;
                    }
                    return object1.getAssetClass().compareTo(object2.getAssetClass());
                }
            });
        }
        return assetSummaries;
    }

    private String getAssetCodeForCash(String assetClass, String assetId, Map<String, Asset> assets) {
        if ("Cash".equalsIgnoreCase(assetClass)) {
            return "Cash (T+n)";
        } else {
            if (assets.get(assetId) != null) {
                return assets.get(assetId).getAssetCode();
            }

        }
        return null;
    }

    private String getAssetNameForCash(String assetClass, String assetId, Map<String, Asset> assets) {
        if ("Cash".equalsIgnoreCase(assetClass)) {
            return "";
        } else {
            if (assets.get(assetId) != null) {
                return assets.get(assetId).getAssetName();
            }
        }
        return null;
    }

    private String getAssetCodeForTotal(String assetClass) {
        String assetCodeCashTotal = "";
        if ("Cash".equalsIgnoreCase(assetClass)) {
            assetCodeCashTotal = "CASH TOTAL";
        }
        return assetCodeCashTotal;
    }

    public FinancialDocumentData loadMonthlyModelDocument(String modelId, FinancialDocumentType financialDocumentType) {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        DateTime datetime = DateTime.now().millisOfDay().withMaximumValue();
        DateTime toDate = datetime.dayOfMonth().withMaximumValue();
        BrokerKey brokerKey = helper.getCurrentBroker(serviceErrors);
        String modelCode = null;

        if (modelId != null) {
            Collection<ModelPortfolioSummary> summaries = modelPortfolioSummaryService.loadModels(brokerKey, serviceErrors);
            ModelPortfolioSummary modelSummary = Lambda.selectFirst(summaries,
                    Lambda.having(Lambda.on(ModelPortfolioSummary.class).getModelKey().getId(), Matchers.equalTo(modelId)));

            modelCode = modelSummary.getModelCode();
        }

        String brokerReference = userProfileService.getInvestmentManager(serviceErrors).getBankReferenceId();
        FinancialDocumentData data = documentService.loadIMDocument(financialDocumentType.getCode(), modelCode, toDate,
                brokerReference.replaceAll("^0*", ""), "INVST_MGR", serviceErrors);

        return data;
    }

}
