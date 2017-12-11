package com.bt.nextgen.api.order.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.InterestRateDto;
import com.bt.nextgen.api.asset.model.ManagedFundAssetDto;
import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.api.asset.model.ShareAssetDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverterV2;
import com.bt.nextgen.api.order.service.helper.AssetHelperV2;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.CacheManagedTermDepositAssetRateIntegrationService;
import com.bt.nextgen.service.avaloq.order.OrderItemImpl;
import com.bt.nextgen.service.avaloq.order.OrderItemSummaryImpl;
import com.btfin.panorama.service.integration.account.Account;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.account.IncomePreference;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.asset.ManagedFundAsset;
import com.bt.nextgen.service.integration.asset.ShareAsset;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.order.OrderItem;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRateImpl;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.bt.nextgen.termdeposit.service.TermDepositAssetRateSearchKey;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.service.integration.broker.Broker;
import junit.framework.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by L069552 on 31/07/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class AssetHelperV2Test {

    @InjectMocks
    private AssetHelperV2 assetHelperV2;

    @Mock
    private BrokerIntegrationService brokerService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private AssetDtoConverterV2 assetDtoConverter;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    List<OrderItem> listTDOrders;
    private OrderItemSummaryImpl summary;
    private OrderItemImpl buyOrderModel1;

    @Before
    public void setUp() throws Exception{

        listTDOrders = new ArrayList<>();
        List<Pair<String, BigDecimal>> allocations = new ArrayList<>();
        Pair<String, BigDecimal> allocation = new ImmutablePair<String, BigDecimal>("9876", new BigDecimal("12345"));
        Pair<String, BigDecimal> allocation2 = new ImmutablePair<String, BigDecimal>("54321", new BigDecimal("67890"));

        allocations.add(allocation);
        allocations.add(allocation2);
        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, DistributionMethod.CASH.getDisplayName(), null, null,
                null, null);
        buyOrderModel1 = new OrderItemImpl("641961", "buy", AssetType.TERM_DEPOSIT, "28100", summary, allocations);
        buyOrderModel1.setSubAccountKey(SubAccountKey.valueOf("mpsubaccount"));
        buyOrderModel1.setIncomePreference(IncomePreference.REINVEST);
        listTDOrders.add(buyOrderModel1);

        Mockito.when(assetIntegrationService.loadAssets(Mockito.anyCollection(), any(ServiceErrors.class))).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) {
                        Object[] args = invocation.getArguments();
                        Collection<String> assetIds = (Collection<String>) args[0];
                        Map<String, Asset> assetMap = new HashMap<>();
                        for (String assetId : assetIds) {
                            if (assetId.equals(assetId)) {
                                AssetImpl tdAsset = mock(AssetImpl.class);
                                Mockito.when(tdAsset.getAssetType()).thenReturn(AssetType.TERM_DEPOSIT);
                                Mockito.when(tdAsset.getAssetId()).thenReturn("28100");
                                assetMap.put(assetId, tdAsset);
                            }
                        }
                        return assetMap;
                    }
                });

       Mockito.when(assetDtoConverter.toAssetDto(anyMap(),anyList())).thenAnswer(new Answer<Object>() {
           @Override
           public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
               Map<String, Asset> assets = (Map<String, Asset>) invocationOnMock.getArguments()[0];
               List<TermDepositInterestRate> termDepositInterestRates = (List<TermDepositInterestRate>)invocationOnMock.getArguments()[1];
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
                           if(CollectionUtils.isNotEmpty(termDepositInterestRates) && isTDAssetPresent(termDepositInterestRates,asset.getAssetId())){
                           List<InterestRateDto> interestBands = Collections.emptyList();
                           result.put(asset.getAssetId(), new TermDepositAssetDto(asset, asset.getAssetName(), null, null, null,
                                   null, null, null, interestBands, null));
                        }
                   }
               }
               return result;
           }
       });

        Mockito.when(
                accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) {
                        WrapAccountDetail accountDetail = mock(WrapAccountDetail.class);
                        Mockito.when(accountDetail.isOpen()).thenReturn(false);
                        Mockito.when(accountDetail.getAdviserKey()).thenReturn(BrokerKey.valueOf("brokerKey"));
                        Mockito.when(accountDetail.getAdminFeeRate()).thenReturn(new BigDecimal("9.98"));
                        Mockito.when(accountDetail.getProductKey()).thenReturn(ProductKey.valueOf("20125"));
                        Mockito.when(accountDetail.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
                        return accountDetail;
                    }
                });

        Mockito.when(
                    brokerService.getBroker(any(BrokerKey.class),any(ServiceErrors.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Broker broker = mock(Broker.class);
                Mockito.when(broker.getKey()).thenReturn(BrokerKey.valueOf("testUser"));
                Mockito.when(broker.getDealerKey()).thenReturn(BrokerKey.valueOf("123"));
                return broker;
            }
        });

    }

    private boolean isTDAssetPresent(List<TermDepositInterestRate> termDepositInterestRates, final String assetId) {
        TermDepositInterestRate termDepositInterestRate =  Lambda.selectFirst(termDepositInterestRates,new LambdaMatcher<TermDepositInterestRate>() {
            @Override
            protected boolean matchesSafely(TermDepositInterestRate termDepositInterestRate) {
                return assetId.equalsIgnoreCase(termDepositInterestRate.getAssetKey().getId());
            }
        });
      return termDepositInterestRate != null;
    }

    @Test
    public void testGetAssetsForTd(){
        List<TermDepositInterestRate> termDepositInterestRateList = new ArrayList<>();
        TermDepositInterestRate termDepositInterestRate = new TermDepositInterestRateImpl.TermDepositInterestRateBuilder().withAccountStructureType(AccountStructureType.Individual).withAssetKey(AssetKey.valueOf("28100"))
        .withDealerGroupKey(BrokerKey.valueOf("99971")).withIssuerId("80000152").withIssuerName("BT").withRate(new BigDecimal(0.015)).withLowerLimit(new BigDecimal(0)).withUpperLimit(new BigDecimal(500))
                .buildTermDepositRate();
        termDepositInterestRateList.add(termDepositInterestRate);

        Mockito.when(assetIntegrationService.loadTermDepositRates(any(TermDepositAssetRateSearchKey.class), any(ServiceErrors.class)))
        .thenReturn(termDepositInterestRateList);
        Map<String, AssetDto> assetDtoMap = assetHelperV2.getAssetsForOrders(AccountKey.valueOf("12345"), listTDOrders, new FailFastErrorsImpl());
        Assert.assertNotNull(assetDtoMap);
        if(CollectionUtils.isNotEmpty(listTDOrders)){
            for(OrderItem orderItem : listTDOrders){
                Assert.assertNotNull(assetDtoMap.get(orderItem.getAssetId()));
            }
        }

    }

}
