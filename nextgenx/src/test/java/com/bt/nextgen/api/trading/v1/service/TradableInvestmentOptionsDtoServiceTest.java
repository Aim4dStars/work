package com.bt.nextgen.api.trading.v1.service;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.trading.v1.model.TradableInvestmentOptionDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.ExternalBrokerKey;
import com.bt.nextgen.service.integration.code.CodeCategoryInterface;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.bt.nextgen.service.integration.ips.IpsFee;
import com.bt.nextgen.service.integration.ips.IpsKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class TradableInvestmentOptionsDtoServiceTest {

    @Mock
    private InvestmentPolicyStatementIntegrationService ipsService;

    @Mock
    private TradableAssetsDtoService tradableAssetsDtoService;

    @Mock
    BrokerIntegrationService brokerService;

    @Mock
    StaticIntegrationService staticIntegrationService;

    @InjectMocks
    TradableInvestmentOptionsDtoServiceImpl dtoService;

    Map<IpsKey, InvestmentPolicyStatementInterface> ipsDetailsMap = new HashMap<>();
    ServiceErrors serviceErrors;
    List<ApiSearchCriteria> criteriaList;
    List<TradeAssetDto> tradeAssetDtos;

    @Before
    public void setup() {
        serviceErrors = new ServiceErrorsImpl();
        criteriaList = new ArrayList<>();
        criteriaList.add(new ApiSearchCriteria("accountId", ApiSearchCriteria.SearchOperation.EQUALS, "accountId",
                ApiSearchCriteria.OperationType.STRING));

        ipsDetailsMap.put(IpsKey.valueOf("ipsid1"), getIpsDetail("ipsid1", "apir1", "9500"));
        ipsDetailsMap.put(IpsKey.valueOf("ipsid2"), getIpsDetail("ipsid2", "apir2", "9500"));
        ipsDetailsMap.put(IpsKey.valueOf("ipsid3"), getIpsDetail("ipsid3", "apir3", "9500"));
        ipsDetailsMap.put(IpsKey.valueOf("ipsid4"), getIpsDetail("ipsid4", "apir4", "9500"));

        tradeAssetDtos = new ArrayList<>();

    }

    @Test
    public void testSearchSuccess() {

        TradeAssetDto managedPortfolioTradeAssetDto1 = Mockito.mock(TradeAssetDto.class);
        AssetDto managedPortfolioAsset1 = Mockito.mock(AssetDto.class);
        Mockito.when(managedPortfolioTradeAssetDto1.getAsset()).thenReturn(managedPortfolioAsset1);
        Mockito.when(managedPortfolioTradeAssetDto1.getAsset().getAssetType()).thenReturn(
                AssetType.MANAGED_PORTFOLIO.getDisplayName());
        Mockito.when(managedPortfolioTradeAssetDto1.getAsset().getIpsId()).thenReturn("ipsid2");

        TradeAssetDto managedPortfolioTradeAssetDto2 = Mockito.mock(TradeAssetDto.class);
        AssetDto managedPortfolioAsset2 = Mockito.mock(AssetDto.class);
        Mockito.when(managedPortfolioTradeAssetDto2.getAsset()).thenReturn(managedPortfolioAsset2);
        Mockito.when(managedPortfolioTradeAssetDto2.getAsset().getAssetType()).thenReturn(
                AssetType.MANAGED_PORTFOLIO.getDisplayName());
        Mockito.when(managedPortfolioTradeAssetDto2.getAsset().getIpsId()).thenReturn("ipsid4");

        tradeAssetDtos.add(managedPortfolioTradeAssetDto1);
        tradeAssetDtos.add(managedPortfolioTradeAssetDto2);

        Broker broker = Mockito.mock(Broker.class);
        Mockito.when(tradableAssetsDtoService.search(Mockito.anyList(), Mockito.any(ServiceErrors.class))).thenReturn(
                tradeAssetDtos);
        Mockito.when(ipsService.getInvestmentPolicyStatements(Mockito.any(ServiceErrors.class))).thenReturn(ipsDetailsMap);
        Mockito.when(brokerService.getBroker(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class))).thenReturn(broker);
        Mockito.when(
                staticIntegrationService.loadCode(Mockito.any(CodeCategoryInterface.class), Mockito.anyString(),
                        Mockito.any(ServiceErrors.class))).thenReturn(new CodeImpl(null, null, "Index"));
        Mockito.when(broker.getExternalBrokerKey()).thenReturn(ExternalBrokerKey.valueOf("1234"));

        List<TradableInvestmentOptionDto> result = dtoService.search(criteriaList, serviceErrors);
        Assert.assertEquals(result.size(), 2);
        Assert.assertNotNull(result.get(0).getAsset());
        Assert.assertEquals(result.get(0).getAsset().getIpsId(), "ipsid2");
        Assert.assertEquals(result.get(0).getApirCode(), "apir2");
        Assert.assertEquals(result.get(0).getInvestmentStyle(), "Index");
        Assert.assertEquals(result.get(0).getMinAmount(), new BigDecimal(500));
    }

    @Test
    public void testSearchFail() {

        TradeAssetDto managedPortfolioTradeAssetDto1 = Mockito.mock(TradeAssetDto.class);
        AssetDto managedPortfolioAsset1 = Mockito.mock(AssetDto.class);
        Mockito.when(managedPortfolioTradeAssetDto1.getAsset()).thenReturn(managedPortfolioAsset1);
        Mockito.when(managedPortfolioTradeAssetDto1.getAsset().getAssetType()).thenReturn(
                AssetType.MANAGED_PORTFOLIO.getDisplayName());
        Mockito.when(managedPortfolioTradeAssetDto1.getAsset().getIpsId()).thenReturn("ipsid9");

        TradeAssetDto managedPortfolioTradeAssetDto2 = Mockito.mock(TradeAssetDto.class);
        AssetDto managedPortfolioAsset2 = Mockito.mock(AssetDto.class);
        Mockito.when(managedPortfolioTradeAssetDto2.getAsset()).thenReturn(managedPortfolioAsset2);
        Mockito.when(managedPortfolioTradeAssetDto2.getAsset().getAssetType()).thenReturn(
                AssetType.MANAGED_PORTFOLIO.getDisplayName());
        Mockito.when(managedPortfolioTradeAssetDto2.getAsset().getIpsId()).thenReturn("ipsid8");

        tradeAssetDtos.add(managedPortfolioTradeAssetDto1);
        tradeAssetDtos.add(managedPortfolioTradeAssetDto2);

        Broker broker = Mockito.mock(Broker.class);
        Mockito.when(tradableAssetsDtoService.search(Mockito.anyList(), Mockito.any(ServiceErrors.class))).thenReturn(
                tradeAssetDtos);
        Mockito.when(ipsService.getInvestmentPolicyStatements(Mockito.any(ServiceErrors.class))).thenReturn(ipsDetailsMap);
        Mockito.when(brokerService.getBroker(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class))).thenReturn(broker);
        Mockito.when(broker.getExternalBrokerKey()).thenReturn(ExternalBrokerKey.valueOf("1234"));

        List<TradableInvestmentOptionDto> result = dtoService.search(criteriaList, serviceErrors);
        Assert.assertEquals(result.size(), 0);
    }

    InvestmentPolicyStatementInterface getIpsDetail(final String ipsId, final String apirCode, final String investmentStyleId) {
        return new InvestmentPolicyStatementInterface() {
            @Override
            public String getInvestmentName() {
                return null;
            }

            @Override
            public String getCode() {
                return null;
            }

            @Override
            public String getApirCode() {
                return apirCode;
            }

            @Override
            public String getAssetClassId() {
                return null;
            }

            @Override
            public String getInvestmentStyleId() {
                return investmentStyleId;
            }

            @Override
            public BigDecimal getPercentage() {
                return null;
            }

            @Override
            public BigDecimal getMinInitInvstAmt() {
                return new BigDecimal(500);
            }

            @Override
            public String getInvestmentManagerPersonId() {
                return null;
            }

            @Override
            public Boolean getTaxAssetDomicile() {
                return null;
            }

            @Override
            public IpsKey getIpsKey() {
                return IpsKey.valueOf(ipsId);
            }

            @Override
            public void setIpsKey(IpsKey ipsKey) {

            }

            @Override
            public List<IpsFee> getFeeList() {
                return null;
            }

            @Override
            public BigDecimal getWeightedIcr() {
                return null;
            }
        };
    }
}
