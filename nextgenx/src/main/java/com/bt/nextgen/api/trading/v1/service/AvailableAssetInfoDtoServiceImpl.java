package com.bt.nextgen.api.trading.v1.service;

import ch.lambdaj.Lambda;
import static ch.lambdaj.Lambda.on;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.trading.v1.model.AvailableAssetInfoDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetTypeDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.cache.CacheType;
import com.bt.nextgen.core.cache.GenericCache;
import com.bt.nextgen.core.security.profile.InvestorProfileService;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.avaloq.asset.CacheManagedTermDepositAssetRateIntegrationService;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.bt.nextgen.termdeposit.service.TermDepositAssetRateSearchKey;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerAnnotationHolder;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "squid:S1200" })
@Service
public class AvailableAssetInfoDtoServiceImpl implements AvailableAssetInfoDtoService {
    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    @Autowired
    private BankDateIntegrationService bankDateIntegrationService;

    @Autowired
    private GenericCache genericCache;

    @Qualifier("cacheAvaloqAccountIntegrationService")
    @Autowired
    public AccountIntegrationService accountService;

    @Autowired
    public BrokerIntegrationService brokerService;

    @Autowired
    private TradableAssetsDtoService tradeableAssetsDtoService;

    @Autowired
    private TradableAssetsTypeDtoService tradeableAssetsTypeDtoService;

    @Autowired
    private InvestorProfileService profileService;

    @Autowired
    private FeatureTogglesService featureTogglesService;

    @Override
    public AvailableAssetInfoDto find(AccountKey key, ServiceErrors serviceErrors) {
        AvailableAssetInfoDto aalInfo = new AvailableAssetInfoDto();

        getCacheInfo(aalInfo, serviceErrors);
        WrapAccount account = getAccountInfo(key, aalInfo, serviceErrors);
        getAssetInfo(account, aalInfo, serviceErrors);
        getProfileInfo(aalInfo);

        return aalInfo;
    }

    protected void getCacheInfo(AvailableAssetInfoDto aalInfo, ServiceErrors serviceErrors) {
        aalInfo.setCacheAssetLastRefreshed(new DateTime(genericCache.getLastRefreshed(CacheType.ASSET_DETAILS)));
        aalInfo.setCacheAssetLastUpdated(new DateTime(genericCache.getLastUpdated(CacheType.ASSET_DETAILS)));
        aalInfo.setCacheAssetCount(genericCache.getAll(CacheType.ASSET_DETAILS).size());

        aalInfo.setCacheTdLastRefreshed(new DateTime(genericCache.getLastRefreshed(CacheType.TERM_DEPOSIT_ASSET_RATE_CACHE)));
        aalInfo.setCacheTdLastUpdated(new DateTime(genericCache.getLastUpdated(CacheType.TERM_DEPOSIT_ASSET_RATE_CACHE)));
        aalInfo.setCacheTdCount(genericCache.getAll(CacheType.TERM_DEPOSIT_ASSET_RATE_CACHE).size());

        aalInfo.setCacheTdLastRefreshed(new DateTime(genericCache.getLastRefreshed(CacheType.TERM_DEPOSIT_PRODUCT_RATE_CACHE)));
        aalInfo.setCacheTdLastUpdated(new DateTime(genericCache.getLastUpdated(CacheType.TERM_DEPOSIT_PRODUCT_RATE_CACHE)));
        aalInfo.setCacheTdCount(genericCache.getAll(CacheType.TERM_DEPOSIT_PRODUCT_RATE_CACHE).size());

        aalInfo.setCacheOldAalLastRefreshed(new DateTime(genericCache.getLastRefreshed(CacheType.AVAILABLE_ASSET_LIST_CACHE)));
        aalInfo.setCacheOldAalLastUpdated(new DateTime(genericCache.getLastUpdated(CacheType.AVAILABLE_ASSET_LIST_CACHE)));
        aalInfo.setCacheOldAalCount(genericCache.getAll(CacheType.AVAILABLE_ASSET_LIST_CACHE).size());

        aalInfo.setCacheBrokerProductAssetsLastRefreshed(
                new DateTime(genericCache.getLastRefreshed(CacheType.AVAILABLE_ASSET_LIST_BROKER_CACHE)));
        aalInfo.setCacheBrokerProductAssetsLastUpdated(
                new DateTime(genericCache.getLastUpdated(CacheType.AVAILABLE_ASSET_LIST_BROKER_CACHE)));
        aalInfo.setCacheBrokerProductAssetsCount(genericCache.getAll(CacheType.AVAILABLE_ASSET_LIST_BROKER_CACHE).size());

        aalInfo.setCacheAalIssuersLastRefreshed(
                new DateTime(genericCache.getLastRefreshed(CacheType.AVAILABLE_ASSET_LIST_ISSUER_CACHE)));
        aalInfo.setCacheAalIssuersLastUpdated(
                new DateTime(genericCache.getLastUpdated(CacheType.AVAILABLE_ASSET_LIST_ISSUER_CACHE)));
        aalInfo.setCacheAalIssuersCount(genericCache.getAll(CacheType.AVAILABLE_ASSET_LIST_ISSUER_CACHE).size());

        aalInfo.setCacheAalIndexesLastRefreshed(
                new DateTime(genericCache.getLastRefreshed(CacheType.AVAILABLE_ASSET_LIST_INDEX_CACHE)));
        aalInfo.setCacheAalIndexesLastUpdated(
                new DateTime(genericCache.getLastUpdated(CacheType.AVAILABLE_ASSET_LIST_INDEX_CACHE)));
        aalInfo.setCacheAalIndexesCount(genericCache.getAll(CacheType.AVAILABLE_ASSET_LIST_INDEX_CACHE).size());

        aalInfo.setCacheIndexAssetsLastRefreshed(new DateTime(genericCache.getLastRefreshed(CacheType.INDEX_ASSET_CACHE)));
        aalInfo.setCacheIndexAssetsLastUpdated(new DateTime(genericCache.getLastUpdated(CacheType.INDEX_ASSET_CACHE)));
        aalInfo.setCacheIndexAssetsCount(genericCache.getAll(CacheType.INDEX_ASSET_CACHE).size());

        DateTime bankDate = bankDateIntegrationService.getBankDate(serviceErrors);
        aalInfo.setBankDate(bankDate);
    }

    protected WrapAccount getAccountInfo(AccountKey key, AvailableAssetInfoDto aalInfo, ServiceErrors serviceErrors) {
        final EncodedString encodedAccountId = new EncodedString(key.getAccountId());
        final String accountId = encodedAccountId.plainText();
        WrapAccountDetail account = accountService
                .loadWrapAccountDetail(com.bt.nextgen.service.integration.account.AccountKey.valueOf(accountId), serviceErrors);
        aalInfo.setAccountId(accountId);
        aalInfo.setAccountKey(key);

        if (account != null) {
            aalInfo.setAccountName(account.getAccountName());
            aalInfo.setAccountBpNumber(account.getAccountNumber());

            Broker adviser = brokerService.getBroker(account.getAdviserPositionId(), serviceErrors);
            aalInfo.setAdviserId(adviser.getKey().getId());
            aalInfo.setAdviserOEId(adviser.getBrokerOEKey());
            if (adviser instanceof BrokerAnnotationHolder) {
                aalInfo.setAdviserName(((BrokerAnnotationHolder) adviser).getPositionName());
            }
            aalInfo.setAdviserUserExperience(
                    adviser.getUserExperience() == null ? "unknown" : adviser.getUserExperience().getIntlId());

            aalInfo.setDealerId(adviser.getDealerKey().getId());
            aalInfo.setDealerOEId(adviser.getParentEBIKey().getId());
            aalInfo.setSuperDealerId(adviser.getSuperDealerKey().getId());
        }

        return account;
    }

    protected void getAssetInfo(WrapAccount account, AvailableAssetInfoDto aalInfo, ServiceErrors serviceErrors) {
        boolean termDepositToggle = featureTogglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle("termDepositToggle");
        if (account != null) {
            SubAccount directProductSubAccount = null;
            for (SubAccount subAccount : account.getSubAccounts()) {
                if (ContainerType.DIRECT.equals(subAccount.getSubAccountType())) {
                    directProductSubAccount = subAccount;
                    break;
                }
            }
            aalInfo.setDirectProductId(directProductSubAccount.getProductIdentifier().getProductKey().getId());

            BrokerKey dealerKey = BrokerKey.valueOf(aalInfo.getDealerId());
            List<Asset> aal = assetIntegrationService.loadAvailableAssets(dealerKey,
                    directProductSubAccount.getProductIdentifier().getProductKey(), serviceErrors);
            aalInfo.setAalServiceCount(aal.size());

            DateTime bankDate = bankDateIntegrationService.getBankDate(serviceErrors);
            if(!termDepositToggle){
                Map<String, TermDepositAssetDetail> tdRates = assetIntegrationService.loadTermDepositRates(dealerKey, bankDate, aal,
                        serviceErrors);
                aalInfo.setTdRatesCount(tdRates.size());
            }else{              //TODO::Query on the TD size.

                final List<String> assetIdList = Lambda.collect(aal, on(Asset.class).getAssetId());
                TermDepositAssetRateSearchKey termDepositAssetRateSearchKey = new TermDepositAssetRateSearchKey(account.getProductKey(),dealerKey,null,account.getAccountStructureType(),DateTime.now(),assetIdList);
                List<TermDepositInterestRate>termDepositInterestRates = assetIntegrationService.loadTermDepositRates(termDepositAssetRateSearchKey, serviceErrors);
                aalInfo.setTdRatesCount(termDepositInterestRates.size());
            }

            final ApiSearchCriteria accountIdCriteria = new ApiSearchCriteria("accountId", SearchOperation.EQUALS,
                    aalInfo.getKey().getAccountId(), OperationType.STRING);
            final ApiSearchCriteria queryCriteria = new ApiSearchCriteria("query", SearchOperation.EQUALS, "",
                    OperationType.STRING);
            final List<ApiSearchCriteria> criteriaList = new ArrayList<>();
            criteriaList.add(accountIdCriteria);
            criteriaList.add(queryCriteria);
            List<TradeAssetDto> tradableAssets = tradeableAssetsDtoService.search(criteriaList, serviceErrors);
            aalInfo.setTradableAssetsCount(tradableAssets.size());

            List<TradeAssetTypeDto> tradableAssetTypes = tradeableAssetsTypeDtoService.search(criteriaList, serviceErrors);
            aalInfo.setTradableAssetTypes(tradableAssetTypes);
            aalInfo.setTradableAssetTypesCount(tradableAssetTypes.size());
        }
    }

    protected void getProfileInfo(AvailableAssetInfoDto aalInfo) {
        Profile effectiveProfile = profileService.getEffectiveProfile();
        aalInfo.setActiveProfileId(effectiveProfile.getActiveJobProfile().getProfileId());
        List<String> availableProfileIds = new ArrayList<>();
        for (JobProfile jobProfile : effectiveProfile.getAvailableProfiles()) {
            availableProfileIds.add(jobProfile.getProfileId());
        }
        aalInfo.setAvailableProfileIds(availableProfileIds);
        aalInfo.setProfileUserId(effectiveProfile.getUserId());
        aalInfo.setUserExperience(effectiveProfile.getActiveJobProfile().getUserExperience() == null ? "unknown"
                : effectiveProfile.getActiveJobProfile().getUserExperience().getIntlId());
        List<String> functionalRoles = new ArrayList<>();
        for (FunctionalRole functionalRole : effectiveProfile.getActiveProfile().getFunctionalRoles()) {
            functionalRoles.add(functionalRole.getAvaloqRole());
        }
        aalInfo.setFunctionalRoles(functionalRoles);
    }
}
