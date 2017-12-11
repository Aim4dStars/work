package com.bt.nextgen.api.holdingbreach.v1.service;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.InterestRateDto;
import com.bt.nextgen.api.asset.model.ManagedFundAssetDto;
import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.api.asset.model.ShareAssetDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.holdingbreach.v1.model.HoldingBreachSummaryDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.asset.ShareAssetImpl;
import com.bt.nextgen.service.avaloq.holdingbreach.HoldingBreachAssetImpl;
import com.bt.nextgen.service.avaloq.holdingbreach.HoldingBreachImpl;
import com.bt.nextgen.service.avaloq.holdingbreach.HoldingBreachSummaryImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.ManagedFundAsset;
import com.bt.nextgen.service.integration.asset.ShareAsset;
import com.bt.nextgen.service.integration.holdingbreach.HoldingBreach;
import com.bt.nextgen.service.integration.holdingbreach.HoldingBreachAsset;
import com.bt.nextgen.service.integration.holdingbreach.HoldingBreachIntegrationService;
import com.btfin.panorama.service.integration.account.AccountSecurityIntegrationService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class HoldingBreachDtoServiceTest {
    @InjectMocks
    private HoldingBreachDtoServiceImpl breachDtoService;

    @Mock
    private HoldingBreachIntegrationService holdingBreachService;

    @Mock
    private AssetIntegrationService assetService;

    @Mock
    private AssetDtoConverter converter;

    @Mock
    private AccountSecurityIntegrationService accountService;

    List<HoldingBreach> emptyBreachList;
    HoldingBreachAssetImpl breachAsset1;
    HoldingBreachAssetImpl breachAsset2;
    HoldingBreachAssetImpl breachAsset3;
    HoldingBreachAssetImpl breachAsset4;
    List<HoldingBreachAsset> breachAssets1;
    List<HoldingBreachAsset> breachAssets2;
    HoldingBreachImpl breach1;
    HoldingBreachImpl breach2;
    List<HoldingBreach> breaches;
    HoldingBreachSummaryImpl breachSummary;
    HoldingBreachSummaryImpl emptyBreachSummary;
    HoldingBreachImpl emptyBreach;
    HoldingBreachSummaryImpl emptyBreachAssetSummary;
    List<WrapAccount> accounts;
    Map<AccountKey, WrapAccount> accountMap;
    Map<String, AssetDto> assetDtoMap;
    ShareAssetImpl asset1;
    ShareAssetImpl asset2;
    ShareAssetImpl asset3;
    ShareAssetImpl asset4;
    DateTime now;
    WrapAccountImpl account1;
    WrapAccountImpl account2;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        now = DateTime.now();

        emptyBreachList = new ArrayList<>();
        breachAssets1 = new ArrayList<>();
        breachAssets2 = new ArrayList<>();

        breachAsset1 = new HoldingBreachAssetImpl();
        breachAsset1.setAssetId("asset1");
        breachAsset1.setBreachAmount(new BigDecimal("1000"));
        breachAsset1.setHoldingLimitPercent(new BigDecimal("1"));
        breachAsset1.setMarketValue(new BigDecimal("1111"));
        breachAsset1.setPortfolioPercent(new BigDecimal("11"));

        breachAsset2 = new HoldingBreachAssetImpl();
        breachAsset2.setAssetId("asset2");
        breachAsset2.setBreachAmount(new BigDecimal("2000"));
        breachAsset2.setHoldingLimitPercent(new BigDecimal("2"));
        breachAsset2.setMarketValue(new BigDecimal("2222"));
        breachAsset2.setPortfolioPercent(new BigDecimal("22"));

        breachAsset3 = new HoldingBreachAssetImpl();
        breachAsset3.setAssetId("asset3");
        breachAsset3.setBreachAmount(new BigDecimal("3000"));
        breachAsset3.setHoldingLimitPercent(new BigDecimal("3"));
        breachAsset3.setMarketValue(new BigDecimal("3333"));
        breachAsset3.setPortfolioPercent(new BigDecimal("33"));

        breachAssets1.add(breachAsset1);
        breachAssets1.add(breachAsset2);
        breachAssets1.add(breachAsset3);

        breach1 = new HoldingBreachImpl();
        breach1.setAccountId("account1");
        breach1.setValuationAmount(new BigDecimal("12345"));
        breach1.setBreachAssets(breachAssets1);

        breachAsset4 = new HoldingBreachAssetImpl();
        breachAsset4.setAssetId("asset4");
        breachAsset4.setBreachAmount(new BigDecimal("4000"));
        breachAsset4.setHoldingLimitPercent(new BigDecimal("4"));
        breachAsset4.setMarketValue(new BigDecimal("4444"));
        breachAsset4.setPortfolioPercent(new BigDecimal("44"));
        breachAssets2.add(breachAsset4);

        breach2 = new HoldingBreachImpl();
        breach2.setAccountId("account2");
        breach2.setValuationAmount(new BigDecimal("67890"));
        breach2.setBreachAssets(breachAssets1);

        breaches = new ArrayList<>();
        breaches.add(breach1);
        breaches.add(breach2);

        breachSummary = new HoldingBreachSummaryImpl();
        breachSummary.setReportDate(now);
        breachSummary.setHoldingBreaches(breaches);

        emptyBreach = new HoldingBreachImpl();
        List<HoldingBreach> emptyBreaches = new ArrayList<>();
        emptyBreaches.add(emptyBreach);

        emptyBreachAssetSummary = new HoldingBreachSummaryImpl();
        emptyBreachAssetSummary.setReportDate(now);
        emptyBreachAssetSummary.setHoldingBreaches(emptyBreaches);

        breach2 = new HoldingBreachImpl();
        breach2.setAccountId("account2");
        breach2.setValuationAmount(new BigDecimal("67890"));

        emptyBreachSummary = new HoldingBreachSummaryImpl();
        emptyBreachSummary.setReportDate(now);

        accountMap = new HashMap<AccountKey, WrapAccount>();
        accounts = new ArrayList<>();
        account1 = new WrapAccountImpl();
        account1.setAccountKey(AccountKey.valueOf("account1"));
        account1.setAccountName("Robert Gilby");
        account1.setSuperAccountSubType(AccountSubType.PENSION);
        account2 = new WrapAccountImpl();
        account2.setAccountKey(AccountKey.valueOf("account2"));
        account2.setAccountName("Homer Simpson");
        account2.setSuperAccountSubType(AccountSubType.ACCUMULATION);
        accounts.add(account1);
        accounts.add(account2);
        accountMap.put(account1.getAccountKey(), account1);
        accountMap.put(account2.getAccountKey(), account2);
        Mockito.when(accountService.loadWrapAccountWithoutContainers(Mockito.any(ServiceErrors.class))).thenReturn(accountMap);

        Map<String, Asset> assetMap = new HashMap<>();
        assetDtoMap = new HashMap<>();

        asset1 = new ShareAssetImpl();
        asset1.setAssetId("asset1");
        asset1.setAssetCode("BHP");
        asset1.setAssetType(AssetType.SHARE);
        asset1.setAssetName("BHP Billiton");
        asset1.setStatus(AssetStatus.OPEN);
        assetMap.put("asset1", asset1);
        assetDtoMap.put("asset1", new ShareAssetDto(asset1));

        asset2 = new ShareAssetImpl();
        asset2.setAssetId("asset2");
        asset2.setAssetCode("WBC");
        asset2.setAssetType(AssetType.SHARE);
        asset2.setAssetName("Westpac");
        asset2.setStatus(AssetStatus.OPEN);
        assetMap.put("asset2", asset2);
        assetDtoMap.put("asset2", new ShareAssetDto(asset2));

        asset3 = new ShareAssetImpl();
        asset3.setAssetId("asset3");
        asset3.setAssetCode("CBA");
        asset3.setAssetType(AssetType.SHARE);
        asset3.setAssetName("Commonwealth");
        asset3.setStatus(AssetStatus.OPEN);
        assetMap.put("asset3", asset3);
        assetDtoMap.put("asset3", new ShareAssetDto(asset3));

        asset4 = new ShareAssetImpl();
        asset4.setAssetId("asset4");
        asset4.setAssetCode("NAB");
        asset4.setAssetType(AssetType.SHARE);
        asset4.setAssetName("National australia");
        asset4.setStatus(AssetStatus.OPEN);
        assetMap.put("asset4", asset4);
        assetDtoMap.put("asset4", new ShareAssetDto(asset4));

        Mockito.when(assetService.loadAssets(Mockito.anyList(), Mockito.any(ServiceErrors.class))).thenReturn(assetMap);

        Answer<Map<String, AssetDto>> assetAnswer = new Answer<Map<String, AssetDto>>() {
            @Override
            public Map<String, AssetDto> answer(InvocationOnMock invocation) throws Throwable {
                Map<String, Asset> assets = (Map<String, Asset>) invocation.getArguments()[0];
                Map<String, AssetDto> result = new HashMap<>();
                for (Asset asset : assets.values()) {
                    if (asset.getAssetType() == AssetType.MANAGED_PORTFOLIO) {
                        result.put(asset.getAssetId(), new ManagedPortfolioAssetDto(asset));
                    } else if (asset.getAssetType() == AssetType.MANAGED_FUND) {
                        ManagedFundAsset mfAsset = (ManagedFundAsset) asset;
                        result.put(asset.getAssetId(), new ManagedFundAssetDto(mfAsset));
                    } else if (asset.getAssetType() == AssetType.SHARE) {
                        ShareAsset shareAsset = (ShareAsset) asset;
                        result.put(asset.getAssetId(), new ShareAssetDto(shareAsset));
                    } else {
                        List<InterestRateDto> interestBands = Collections.emptyList();
                        result.put(asset.getAssetId(), new TermDepositAssetDto(asset, asset.getAssetName(), null, null, null,
                                null, null, null, interestBands, null));
                    }
                }

                return result;
            }
        };
        Mockito.when(converter.toAssetDto(Mockito.anyMap(), Mockito.anyMap())).thenAnswer(assetAnswer);
        Mockito.when(converter.toAssetDto(Mockito.anyMap(), Mockito.anyMap(), Mockito.anyBoolean())).thenAnswer(assetAnswer);
    }

    @Test
    public void testToHoldingBreachSummaryDto_sizeMatches() {
        HoldingBreachSummaryDto breachSummaryDto = breachDtoService.toHoldingBreachSummaryDto(breachSummary, assetDtoMap,
                new ServiceErrorsImpl());
        assertNotNull(breachSummaryDto);
        assertEquals(2, breachSummaryDto.getHoldingBreaches().size());
    }

    @Test
    public void testToHoldingBreachSummaryDto_valueMatches_whenAccountMap_passed() {
        HoldingBreachSummaryDto breachSummaryDto = breachDtoService.toHoldingBreachSummaryDto(breachSummary, assetDtoMap,
                new ServiceErrorsImpl());
        assertNotNull(breachSummaryDto);
        assertEquals(breachSummary.getReportDate(), breachSummaryDto.getReportDate());

        assertEquals(breachSummary.getHoldingBreaches().size(), breachSummaryDto.getHoldingBreaches().size());
        assertEquals(account1.getAccountNumber(), breachSummaryDto.getHoldingBreaches().get(0).getAccountNumber());
        assertEquals(account1.getSuperAccountSubType().getAccountType(),
                breachSummaryDto.getHoldingBreaches().get(0).getProductName());
        assertEquals(breachSummary.getHoldingBreaches().get(0).getValuationAmount(),
                breachSummaryDto.getHoldingBreaches().get(0).getValuationAmount());

        assertEquals(breachSummary.getHoldingBreaches().get(0).getBreachAssets().size(),
                breachSummaryDto.getHoldingBreaches().get(0).getBreachAssets().size());
        assertEquals(breachSummary.getHoldingBreaches().get(0).getBreachAssets().get(0).getAssetId(),
                breachSummaryDto.getHoldingBreaches().get(0).getBreachAssets().get(0).getAsset().getAssetId());
        assertEquals(breachSummary.getHoldingBreaches().get(0).getBreachAssets().get(0).getBreachAmount(),
                breachSummaryDto.getHoldingBreaches().get(0).getBreachAssets().get(0).getBreachAmount());
        assertEquals(breachSummary.getHoldingBreaches().get(0).getBreachAssets().get(0).getHoldingLimitPercent(),
                breachSummaryDto.getHoldingBreaches().get(0).getBreachAssets().get(0).getHoldingLimitPercent());
        assertEquals(breachSummary.getHoldingBreaches().get(0).getBreachAssets().get(0).getMarketValue(),
                breachSummaryDto.getHoldingBreaches().get(0).getBreachAssets().get(0).getMarketValue());
        assertEquals(breachSummary.getHoldingBreaches().get(0).getBreachAssets().get(0).getPortfolioPercent(),
                breachSummaryDto.getHoldingBreaches().get(0).getBreachAssets().get(0).getPortfolioPercent());
    }

    @Test
    public void testToHoldingBreachSummaryDto_whenNoBreaches_thenEmptyDto() {
        HoldingBreachSummaryDto breachSummaryDto = breachDtoService.toHoldingBreachSummaryDto(emptyBreachSummary, assetDtoMap,
                new ServiceErrorsImpl());
        assertNotNull(breachSummaryDto);
        assertEquals(breachSummary.getReportDate(), breachSummaryDto.getReportDate());
        assertEquals(0, breachSummaryDto.getHoldingBreaches().size());
    }

    @Test
    public void testToHoldingBreachSummaryDto_whenNoBreachAssets_thenEmptyDto() {
        HoldingBreachSummaryDto breachSummaryDto = breachDtoService.toHoldingBreachSummaryDto(emptyBreachAssetSummary,
                assetDtoMap, new ServiceErrorsImpl());
        assertNotNull(emptyBreach.getBreachAssets());
        assertNotNull(breachSummaryDto);
        assertEquals(breachSummary.getReportDate(), breachSummaryDto.getReportDate());
        assertEquals(0, breachSummaryDto.getHoldingBreaches().size());
    }

    @Test
    public void testFindOne() {
        Mockito.when(holdingBreachService.loadHoldingBreaches(Mockito.any(ServiceErrors.class))).thenReturn(breachSummary);
        HoldingBreachSummaryDto breachSummaryDto = breachDtoService.findOne(null);
        Assert.assertNotNull(breachSummaryDto);
        Assert.assertEquals(now, breachSummaryDto.getReportDate());
    }
}
