package com.bt.nextgen.service.avaloq.ips;

import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.modelportfolio.detail.OfferDetailImpl;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.ips.IpsProductAssociationInterface;
import com.bt.nextgen.service.integration.ips.IpsSummaryDetails;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ConstructionType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioDetail;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioStatus;
import com.bt.nextgen.service.integration.modelportfolio.detail.OfferDetail;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.request.AvaloqRequest;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class TbrAvaloqInvestmentPolicyIntegrationServiceImplTest {

    @InjectMocks
    private AvaloqInvestmentPolicyStatementIntegrationServiceImpl service;

    @Mock
    private AvaloqReportService avaloqService;

    @Mock
    private CacheIPSIntegrationServiceImpl cacheIPSIntegrationServiceImpl;

    @Test
    public void testGetAsscociatedProductAndIpsIds() {

        IpsProductListHolder holder = new IpsProductListHolder();
        List<IpsProductImpl> ipsList = new ArrayList<>();
        IpsProductImpl ips1a = new IpsProductImpl();
        IpsProductImpl ips1b = new IpsProductImpl();
        IpsProductImpl ips2 = new IpsProductImpl();
        ips1a.setIpsKey(IpsKey.valueOf("ips1"));
        ips1b.setIpsKey(IpsKey.valueOf("ips1"));
        ips2.setIpsKey(IpsKey.valueOf("ips2"));
        ips1a.setProductKey(ProductKey.valueOf("prod1"));
        ips1b.setProductKey(ProductKey.valueOf("prod2"));
        ips2.setProductKey(ProductKey.valueOf("prod3"));

        ipsList.add(ips1a);
        ipsList.add(ips1b);
        ipsList.add(ips2);

        holder.setIpsList(ipsList);
        Mockito.when(
                avaloqService.executeReportRequestToDomain(Mockito.any(AvaloqRequest.class), Mockito.any(Class.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(holder);
        Map<IpsKey, IpsProductAssociationInterface> result = service.getAsscociatedProductAndIpsIds(new FailFastErrorsImpl());
        Assert.assertEquals(2, result.size());

        Assert.assertEquals(2, result.get(IpsKey.valueOf("ips1")).getAsscociatedProductList().size());
        Assert.assertEquals(ProductKey.valueOf("prod1"), result.get(IpsKey.valueOf("ips1")).getAsscociatedProductList().get(0));
        Assert.assertEquals(ProductKey.valueOf("prod2"), result.get(IpsKey.valueOf("ips1")).getAsscociatedProductList().get(1));

        Assert.assertEquals(1, result.get(IpsKey.valueOf("ips2")).getAsscociatedProductList().size());
        Assert.assertEquals(ProductKey.valueOf("prod3"), result.get(IpsKey.valueOf("ips2")).getAsscociatedProductList().get(0));

    }

    @Test
    public void testGetModelDetails_validModelPortfolioMapCreated() {
        IpsDetails ips1 = Mockito.mock(IpsDetails.class);
        Mockito.when(ips1.getId()).thenReturn("id1");
        Mockito.when(ips1.getIpsKey()).thenReturn(IpsKey.valueOf("id1"));
        Mockito.when(ips1.getName()).thenReturn("name1");

        IpsDetailList ipsList = Mockito.mock(IpsDetailList.class);
        Mockito.when(ipsList.getIpsList()).thenReturn(Collections.singletonList(ips1));

        Mockito.when(
                avaloqService.executeReportRequestToDomain(Mockito.any(AvaloqRequest.class), Mockito.any(Class.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(ipsList);
        IpsKey key = IpsKey.valueOf("id1");
        Map<IpsKey, ModelPortfolioDetail> results = service.getModelDetails(Collections.singletonList(key),
                new FailFastErrorsImpl());

        Assert.assertNotNull(results);
        ModelPortfolioDetail ipsR = results.get(key);

        Assert.assertEquals(ips1.getId(), ipsR.getId());
        Assert.assertEquals(ips1.getName(), ipsR.getName());
    }

    @Test
    public void testGetModelDetails_modelPortfolioMapCreated() {
        IpsDetails ips1 = new IpsDetails();
        ips1.setAalId("aalId");
        ips1.setAccountType(ModelType.INVESTMENT.getCode());
        ips1.setId("ipsId");
        ips1.setInvestmentManagerId(null);
        ips1.setInvestmentStyle("style");
        ips1.setMinimumInvestment(BigDecimal.ONE);
        ips1.setModelAssetClass("class");
        ips1.setModelConstruction(ConstructionType.FIXED);
        ips1.setModelStructure("struct");
        ips1.setModelType("modelType");
        ips1.setName("name");
        ips1.setOpenDate(DateTime.now());
        ips1.setStatus(ModelPortfolioStatus.NEW);
        ips1.setSymbol("symbol");
        ips1.setTargetAllocations(null);
        ips1.setModelDescription("description");
        ips1.setMinimumTradePercent(BigDecimal.ONE);
        ips1.setMinimumTradeAmount(BigDecimal.ZERO);
        ips1.setInvestmentStyleDesc("other investment style");

        OfferDetail off = new OfferDetailImpl();
        off.setOfferId("offerId");
        ips1.setOfferDetails(Collections.singletonList(off));

        IpsDetailList ipsList = Mockito.mock(IpsDetailList.class);
        Mockito.when(ipsList.getIpsList()).thenReturn(Collections.singletonList(ips1));

        Mockito.when(
                avaloqService.executeReportRequestToDomain(Mockito.any(AvaloqRequest.class), Mockito.any(Class.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(ipsList);
        IpsKey key = IpsKey.valueOf("ipsId");
        Map<IpsKey, ModelPortfolioDetail> results = service.getModelDetails(Collections.singletonList(key),
                new FailFastErrorsImpl());

        Assert.assertNotNull(results);
        ModelPortfolioDetail ipsR = results.get(key);

        Assert.assertEquals(ips1.getId(), ipsR.getId());
        Assert.assertEquals(ips1.getName(), ipsR.getName());
        Assert.assertEquals(ips1.getAalId(), ipsR.getAalId());
        Assert.assertEquals(ips1.getAccountType(), ipsR.getAccountType());
        Assert.assertEquals(ips1.getInvestmentManagerId(), ipsR.getInvestmentManagerId());
        Assert.assertEquals(ips1.getInvestmentStyle(), ipsR.getInvestmentStyle());
        Assert.assertEquals(ips1.getMinimumInvestment(), ipsR.getMinimumInvestment());
        Assert.assertEquals(ips1.getModelAssetClass(), ipsR.getModelAssetClass());
        Assert.assertEquals(ips1.getModelConstruction(), ipsR.getModelConstruction());
        Assert.assertEquals(ips1.getModelType(), ipsR.getModelType());
        Assert.assertEquals(ips1.getOpenDate(), ipsR.getOpenDate());
        Assert.assertEquals(ips1.getStatus(), ipsR.getStatus());
        Assert.assertEquals(ips1.getSymbol(), ipsR.getSymbol());
        Assert.assertEquals(ips1.getTargetAllocations(), ipsR.getTargetAllocations());
        Assert.assertEquals(ips1.getOfferDetails().size(), ipsR.getOfferDetails().size());
        Assert.assertEquals(ips1.getOfferDetails().get(0).getOfferId(), ipsR.getOfferDetails().get(0).getOfferId());
        Assert.assertEquals(ips1.getModelDescription(), ipsR.getModelDescription());
        Assert.assertEquals(ips1.getMinimumTradePercent(), ipsR.getMinimumTradePercent());
        Assert.assertEquals(ips1.getMinimumTradeAmount(), ipsR.getMinimumTradeAmount());
        Assert.assertEquals(ips1.getInvestmentStyleDesc(), ipsR.getInvestmentStyleDesc());
    }

    @Test
    public void test_getDealerGroupIpsSummary() {

        IpsSummaryDetails sumDetails = Mockito.mock(IpsSummaryDetails.class);
        Mockito.when(sumDetails.getApirCode()).thenReturn("apirCode");
        Mockito.when(sumDetails.getInvestmentManagerId()).thenReturn(BrokerKey.valueOf("imId"));
        Mockito.when(sumDetails.getIpsOrderId()).thenReturn("ipsOrderId");
        Mockito.when(sumDetails.getModelCode()).thenReturn("modelCode");
        Mockito.when(sumDetails.getModelKey()).thenReturn(IpsKey.valueOf("ipsId"));
        Mockito.when(sumDetails.getModelName()).thenReturn("modelName");
        Mockito.when(sumDetails.getModelOrderId()).thenReturn("orderId");
        Mockito.when(sumDetails.getStatus()).thenReturn(ModelPortfolioStatus.OPEN);
        Mockito.when(sumDetails.getAccountType()).thenReturn(ModelType.INVESTMENT.getId());
        Mockito.when(sumDetails.getModelConstruction()).thenReturn(ConstructionType.FIXED);

        IpsSummaryList summaryList = Mockito.mock(IpsSummaryList.class);
        Mockito.when(summaryList.getSummaryDetailsList()).thenReturn(Collections.singletonList(sumDetails));

        Mockito.when(
                avaloqService.executeReportRequestToDomain(Mockito.any(AvaloqRequest.class), Mockito.any(Class.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(summaryList);
        BrokerKey key = BrokerKey.valueOf("brokerId");
        ServiceErrors errors = new ServiceErrorsImpl();
        List<IpsSummaryDetails> response = service.getDealerGroupIpsSummary(key, errors);
        Assert.assertTrue(response.size() == 1);
        IpsSummaryDetails responseSum = response.get(0);
        Assert.assertEquals(sumDetails.getApirCode(), responseSum.getApirCode());
        Assert.assertEquals(sumDetails.getInvestmentManagerId(), responseSum.getInvestmentManagerId());
        Assert.assertEquals(sumDetails.getIpsOrderId(), responseSum.getIpsOrderId());
        Assert.assertEquals(sumDetails.getModelCode(), responseSum.getModelCode());
        Assert.assertEquals(sumDetails.getModelKey(), responseSum.getModelKey());
        Assert.assertEquals(sumDetails.getModelName(), responseSum.getModelName());
        Assert.assertEquals(sumDetails.getModelOrderId(), responseSum.getModelOrderId());
        Assert.assertEquals(sumDetails.getStatus(), responseSum.getStatus());
        Assert.assertEquals(ModelType.INVESTMENT, ModelType.forId(responseSum.getAccountType()));
        Assert.assertEquals(ConstructionType.FIXED, responseSum.getModelConstruction());
    }

    @Test
    public void test_getInvestmentManagerFromModel() {

        IpsSummaryDetailsImpl sumDetails = new IpsSummaryDetailsImpl();
        sumDetails.setApirCode("apirCode");
        sumDetails.setInvestmentManagerId(BrokerKey.valueOf("imId"));
        sumDetails.setIpsOrderId("ipsOrderId");
        sumDetails.setModelCode("modelCode");
        sumDetails.setModelKey(IpsKey.valueOf("ipsId"));
        sumDetails.setModelName("modelName");
        sumDetails.setModelOrderId("orderId");
        sumDetails.setStatus(ModelPortfolioStatus.OPEN);
        sumDetails.setAccountType("8122");
        sumDetails.setModelConstruction(ConstructionType.FIXED);

        List<IpsSummaryDetails> list = new ArrayList<>();
        list.add(sumDetails);
        IpsSummaryList summaryList = new IpsSummaryList();
        summaryList.setSummaryDetailsList(list);

        Mockito.when(
                avaloqService.executeReportRequestToDomain(Mockito.any(AvaloqRequest.class), Mockito.any(Class.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(summaryList);
        BrokerKey key = BrokerKey.valueOf("brokerId");
        ServiceErrors errors = new ServiceErrorsImpl();

        List<BrokerKey> imList = service.getInvestmentManagerFromModel(key, errors);
        Assert.assertEquals(1, imList.size());
        Assert.assertEquals(sumDetails.getInvestmentManagerId(), imList.get(0));

    }

    @Test
    public void test_getInvestmentManagerFromModel_emptyResponse() {
        IpsSummaryList summaryList = new IpsSummaryList();
        summaryList.setSummaryDetailsList(null);

        Mockito.when(
                avaloqService.executeReportRequestToDomain(Mockito.any(AvaloqRequest.class), Mockito.any(Class.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(summaryList);
        BrokerKey key = BrokerKey.valueOf("brokerId");
        ServiceErrors errors = new ServiceErrorsImpl();

        List<BrokerKey> imList = service.getInvestmentManagerFromModel(key, errors);
        Assert.assertEquals(0, imList.size());
    }
}