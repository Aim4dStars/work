package com.bt.nextgen.api.trading.v1.service;

import com.bt.nextgen.api.trading.v1.model.AvailableAssetInfoDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetTypeDto;
import com.bt.nextgen.core.cache.CacheType;
import com.bt.nextgen.core.cache.GenericCache;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.avaloq.asset.CacheManagedTermDepositAssetRateIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRateImpl;
import com.bt.nextgen.termdeposit.service.TermDepositAssetRateSearchKey;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.security.profile.InvestorProfileService;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.SubAccountImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.product.ProductIdentifierImpl;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.btfin.panorama.core.security.integration.customer.CustomerCredentialInformation;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.btfin.panorama.service.integration.broker.ExternalBrokerKey;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AvailableAssetInfoDtoServiceTest {

    @InjectMocks
    private AvailableAssetInfoDtoServiceImpl aalInfoService;

    @Mock
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private BankDateIntegrationService bankDateIntegrationService;

    @Mock
    private GenericCache genericCache;

    @Mock
    @Qualifier("cacheAvaloqAccountIntegrationService")
    public AccountIntegrationService accountService;

    @Mock
    public BrokerIntegrationService brokerService;

    @Mock
    private TradableAssetsDtoService tradeableAssetsDtoService;

    @Mock
    private TradableAssetsTypeDtoService tradeableAssetsTypeDtoService;

    @Mock
    private InvestorProfileService profileService;

    @Mock
    private FeatureTogglesService featureTogglesService;

    @Mock
    private FeatureToggles featureToggles;

    private com.bt.nextgen.api.account.v3.model.AccountKey accountKey;
    private Map cacheMap;
    private Date theDateIsNow;
    private DateTime theTimeIsNow;
    private WrapAccountDetailImpl account;
    private SubAccountImpl directSubAccount;
    private BrokerImpl broker;
    private List<Asset> aal;
    private Map<String, TermDepositAssetDetail> tdRates;
    private List<TermDepositInterestRate> termDepositInterestRates;
    private List<TradeAssetDto> tradableAssets;
    private List<TradeAssetTypeDto> tradableAssetTypes;
    private CustomerCredentialInformation baseCredential;
    JobProfileImpl job;
    List<JobProfile> profiles;
    List<FunctionalRole> roles;

    @Before
    public void setUp() throws Exception {
        accountKey = new com.bt.nextgen.api.account.v3.model.AccountKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0");
        theDateIsNow = new Date();
        theTimeIsNow = DateTime.now();
        cacheMap = new HashMap<>();
        cacheMap.put("one", "two");

        Mockito.when(genericCache.getLastRefreshed(Mockito.any(CacheType.class))).thenReturn(theDateIsNow);
        Mockito.when(genericCache.getLastUpdated(Mockito.any(CacheType.class))).thenReturn(theDateIsNow);
        Mockito.when(genericCache.getAll(Mockito.any(CacheType.class))).thenReturn(cacheMap);

        Mockito.when(bankDateIntegrationService.getBankDate(Mockito.any(ServiceErrors.class))).thenReturn(theTimeIsNow);
        account = new WrapAccountDetailImpl();
        account.setAccountName("Wrap Account Name 1");
        account.setAccountNumber("account number");
        account.setAccountKey(AccountKey.valueOf("accountId"));
        directSubAccount = new SubAccountImpl();
        directSubAccount.setSubAccountType(ContainerType.DIRECT);
        ProductIdentifierImpl productIdentifier = new ProductIdentifierImpl();
        productIdentifier.setProductKey(ProductKey.valueOf("directProductIdentifier"));
        directSubAccount.setProductIdentifier(productIdentifier);
        List<SubAccount> subAccountlist = new ArrayList<>();
        subAccountlist.add(directSubAccount);
        account.setSubAccounts(subAccountlist);

        Mockito.when(accountService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(account);

        broker = new BrokerImpl(BrokerKey.valueOf("66773"), BrokerType.ADVISER);
        broker.setBrokerOEKey("brokerOEKey");
        broker.setPositionName("positionName");
        broker.setDealerKey(BrokerKey.valueOf("dealerKey"));
        broker.setParentEBIKey(ExternalBrokerKey.valueOf("parentEBIKey"));
        broker.setSuperDealerKey(BrokerKey.valueOf("superDealerKey"));
        when(brokerService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(broker);
        termDepositInterestRates = new ArrayList<>();
        TermDepositInterestRate termDepositInterestRate01 = new TermDepositInterestRateImpl.TermDepositInterestRateBuilder().withAssetKey(AssetKey.valueOf("28100")).buildTermDepositRate();
        TermDepositInterestRate termDepositInterestRate02 = new TermDepositInterestRateImpl.TermDepositInterestRateBuilder().withAssetKey(AssetKey.valueOf("28100")).buildTermDepositRate();
        termDepositInterestRates.add(termDepositInterestRate01);
        termDepositInterestRates.add(termDepositInterestRate02);


        aal = new ArrayList<>();
        aal.add(new AssetImpl());
        Mockito.when(assetIntegrationService.loadAvailableAssets(Mockito.any(BrokerKey.class), Mockito.any(ProductKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(aal);

        tdRates = Mockito.mock(HashMap.class);
        when(tdRates.size()).thenReturn(7);
        Mockito.when(assetIntegrationService.loadTermDepositRates(Mockito.any(BrokerKey.class), Mockito.any(DateTime.class),
                Mockito.anyList(), Mockito.any(ServiceErrors.class))).thenReturn(tdRates);

        Mockito.when(assetIntegrationService.loadTermDepositRates(Mockito.any(TermDepositAssetRateSearchKey.class),Mockito.any(ServiceErrors.class))).thenReturn(termDepositInterestRates);

        tradableAssets = Mockito.mock(ArrayList.class);
        when(tradableAssets.size()).thenReturn(9);
        Mockito.when(tradeableAssetsDtoService.search(Mockito.anyList(), Mockito.any(ServiceErrors.class)))
                .thenReturn(tradableAssets);

        tradableAssetTypes = Mockito.mock(ArrayList.class);
        when(tradableAssetTypes.size()).thenReturn(11);
        Mockito.when(tradeableAssetsTypeDtoService.search(Mockito.anyList(), Mockito.any(ServiceErrors.class)))
                .thenReturn(tradableAssetTypes);

        UserInformationImpl user = new UserInformationImpl();
        user.setClientKey(com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf("customerId"));
        roles = new ArrayList<>();
        roles.add(FunctionalRole.Trade_entry);
        roles.add(FunctionalRole.Trade_Submit);
        user.setFunctionalRoles(roles);
        job = new JobProfileImpl();
        job.setJobRole(JobRole.ADVISER);
        job.setJob(JobKey.valueOf("jobId"));
        profiles = new ArrayList<>();
        profiles.add(job);
        UserProfile userProfile = new UserProfileAdapterImpl(user, job);

        baseCredential = new SamlToken("");
        Profile profile = new Profile(baseCredential, null);
        profile.setAvailableProfiles(profiles);
        profile.setActiveJobProfile(job);
        profile.setActiveProfile(userProfile);

        Mockito.when(profileService.getEffectiveProfile()).thenReturn(profile);
        Mockito.when(featureTogglesService.findOne(any(ServiceErrors.class))).thenReturn(featureToggles);
    }

    @Test
    public final void testGetCacheInfo() {
        AvailableAssetInfoDto aalInfo = new AvailableAssetInfoDto();
        aalInfoService.getCacheInfo(aalInfo, new ServiceErrorsImpl());
        assertEquals(theDateIsNow.getTime(), aalInfo.getCacheAssetLastRefreshed().getMillis());
        assertEquals(theDateIsNow.getTime(), aalInfo.getCacheAssetLastUpdated().getMillis());
        assertEquals(cacheMap.size(), aalInfo.getCacheAssetCount());
        assertEquals(theDateIsNow.getTime(), aalInfo.getCacheTdLastRefreshed().getMillis());
        assertEquals(theDateIsNow.getTime(), aalInfo.getCacheTdLastUpdated().getMillis());
        assertEquals(cacheMap.size(), aalInfo.getCacheTdCount());
        assertEquals(theDateIsNow.getTime(), aalInfo.getCacheOldAalLastRefreshed().getMillis());
        assertEquals(theDateIsNow.getTime(), aalInfo.getCacheOldAalLastUpdated().getMillis());
        assertEquals(cacheMap.size(), aalInfo.getCacheOldAalCount());
        assertEquals(theDateIsNow.getTime(), aalInfo.getCacheBrokerProductAssetsLastRefreshed().getMillis());
        assertEquals(theDateIsNow.getTime(), aalInfo.getCacheBrokerProductAssetsLastUpdated().getMillis());
        assertEquals(cacheMap.size(), aalInfo.getCacheBrokerProductAssetsCount());
        assertEquals(theDateIsNow.getTime(), aalInfo.getCacheAalIssuersLastRefreshed().getMillis());
        assertEquals(theDateIsNow.getTime(), aalInfo.getCacheAalIssuersLastUpdated().getMillis());
        assertEquals(cacheMap.size(), aalInfo.getCacheAalIssuersCount());
        assertEquals(theDateIsNow.getTime(), aalInfo.getCacheAalIndexesLastRefreshed().getMillis());
        assertEquals(theDateIsNow.getTime(), aalInfo.getCacheAalIndexesLastUpdated().getMillis());
        assertEquals(cacheMap.size(), aalInfo.getCacheAalIndexesCount());
        assertEquals(theDateIsNow.getTime(), aalInfo.getCacheIndexAssetsLastRefreshed().getMillis());
        assertEquals(theDateIsNow.getTime(), aalInfo.getCacheIndexAssetsLastUpdated().getMillis());
        assertEquals(cacheMap.size(), aalInfo.getCacheIndexAssetsCount());
        assertEquals(theTimeIsNow, aalInfo.getBankDate());
    }

    @Test
    public final void testGetAccountInfo() {
        AvailableAssetInfoDto aalInfo = new AvailableAssetInfoDto();
        aalInfoService.getAccountInfo(accountKey, aalInfo, new ServiceErrorsImpl());
        assertEquals(accountKey.getAccountId(), aalInfo.getKey().getAccountId());
        assertEquals(account.getAccountNumber(), aalInfo.getAccountBpNumber());
        assertEquals(account.getAccountName(), aalInfo.getAccountName());
        assertEquals(broker.getKey().getId(), aalInfo.getAdviserId());
        assertEquals(broker.getBrokerOEKey(), aalInfo.getAdviserOEId());
        assertEquals(null, aalInfo.getAdviserName());
        assertEquals("unknown", aalInfo.getAdviserUserExperience());
        assertEquals(broker.getDealerKey().getId(), aalInfo.getDealerId());
        assertEquals(broker.getParentEBIKey().getId(), aalInfo.getDealerOEId());
        assertEquals(broker.getSuperDealerKey().getId(), aalInfo.getSuperDealerId());
    }

    @Test
    public final void testGetAssetInfo() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        AvailableAssetInfoDto aalInfo = new AvailableAssetInfoDto();
        aalInfo.setAccountKey(accountKey);
        aalInfoService.getAssetInfo(account, aalInfo, new ServiceErrorsImpl());
        assertEquals(directSubAccount.getProductIdentifier().getProductKey().getId(), aalInfo.getDirectProductId());
        assertEquals(aal.size(), aalInfo.getAalServiceCount());
        assertEquals(tdRates.size(), aalInfo.getTdRatesCount());
        // assertEquals(account.getAccountKey(), aalInfo.getTradableAssets());
        assertEquals(tradableAssets.size(), aalInfo.getTradableAssetsCount());
        // assertEquals(account.getAccountKey(), aalInfo.getTradableAssetTypes());
        assertEquals(tradableAssetTypes.size(), aalInfo.getTradableAssetTypesCount());
    }

    @Test
    public final void testGetAssetInfoForTD(){
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(true);
        AvailableAssetInfoDto aalInfo = new AvailableAssetInfoDto();
        aalInfo.setAccountKey(accountKey);
        aalInfoService.getAssetInfo(account, aalInfo, new ServiceErrorsImpl());
        assertEquals(directSubAccount.getProductIdentifier().getProductKey().getId(), aalInfo.getDirectProductId());
        assertEquals(aal.size(), aalInfo.getAalServiceCount());
        assertEquals(termDepositInterestRates.size(), aalInfo.getTdRatesCount());
        // assertEquals(account.getAccountKey(), aalInfo.getTradableAssets());
        assertEquals(tradableAssets.size(), aalInfo.getTradableAssetsCount());
        // assertEquals(account.getAccountKey(), aalInfo.getTradableAssetTypes());
        assertEquals(tradableAssetTypes.size(), aalInfo.getTradableAssetTypesCount());

    }

    @Test
    public final void testGetProfileInfo() {
        AvailableAssetInfoDto aalInfo = new AvailableAssetInfoDto();
        aalInfoService.getProfileInfo(aalInfo);
        assertEquals(job.getProfileId(), aalInfo.getActiveProfileId());
        assertEquals(profiles.size(), aalInfo.getAvailableProfileIds().size());
        assertEquals(null, aalInfo.getProfileUserId());
        assertEquals("unknown", aalInfo.getUserExperience());
        assertEquals(roles.size(), aalInfo.getFunctionalRoles().size());
    }
}
