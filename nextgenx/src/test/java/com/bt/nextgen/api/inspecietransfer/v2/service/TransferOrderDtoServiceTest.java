package com.bt.nextgen.api.inspecietransfer.v2.service;

import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.SettlementRecordDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.SponsorDetailsDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v2.model.TransferAssetDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v2.model.TransferDest;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedPortfolioAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.WrapAccountValuationImpl;
import com.bt.nextgen.service.avaloq.transfer.TransferItemImpl;
import com.bt.nextgen.service.avaloq.transfer.TransferOrderImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.order.OrderStatus;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.transfer.BeneficialOwnerChangeStatus;
import com.bt.nextgen.service.integration.transfer.InspecieTransferIntegrationService;
import com.bt.nextgen.service.integration.transfer.TransferItem;
import com.bt.nextgen.service.integration.transfer.TransferOrder;
import com.bt.nextgen.service.integration.transfer.TransferType;
import org.joda.time.DateTime;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class TransferOrderDtoServiceTest {

    @InjectMocks
    private TransferOrderDtoServiceImpl orderService;

    @Mock
    private TransferAssetHelper assetHelper;

    @Mock
    private InspecieTransferIntegrationService transferService;

    @Mock
    private PortfolioIntegrationService portfolioService;

    private Map<String, Asset> contMap;

    @Before
    public void setup() throws Exception {
        AssetImpl asset = new AssetImpl();
        asset.setAssetId("assetId");
        asset.setAssetCode("assetCode");
        asset.setAssetName("assetName");

        contMap = new HashMap<>();
        contMap.put("destId", asset);

        // Mock assetHelper service
        List<SettlementRecordDto> recDtoList = new ArrayList<>();
        TransferAssetDtoImpl dto = new TransferAssetDtoImpl("assetId", "assetCode", new BigDecimal(23d),
                OrderStatus.CANCELLED.name());
        recDtoList.add(dto);
        Mockito.when(assetHelper.toTransferItemDto(Mockito.anyList())).thenReturn(recDtoList);
    }

    @Test
    public void testGetContainerDetails_withNullParams() {

        TransferDest dest = orderService.getContainerDetails(null, null);
        Assert.assertNull(dest);
    }

    @Test
    public void testGetContainerDetails_transferDetailsCreated() {
        Asset asset = contMap.get("destId");

        TransferOrderImpl order = new TransferOrderImpl();
        order.setDestContainerId("destId");

        TransferDest dest = orderService.getContainerDetails(order, contMap);
        Assert.assertEquals("destId", dest.getDestContainerId());
        Assert.assertEquals(asset.getAssetCode(), dest.getAssetCode());
        Assert.assertEquals(asset.getAssetId(), dest.getAssetId());
        Assert.assertEquals(asset.getAssetName(), dest.getAssetName());
    }

    @Test
    public void testGetContainerDetails_withNoMapValue_transferDetailsCreated() {

        // TransferOrder with destinationContainerId not found in map.
        TransferOrderImpl order = new TransferOrderImpl();
        order.setDestContainerId("destId_1");

        TransferDest dest = orderService.getContainerDetails(order, contMap);
        Assert.assertEquals(order.getDestContainerId(), dest.getDestContainerId());
        Assert.assertNull(dest.getAssetCode());
        Assert.assertNull(dest.getAssetId());
        Assert.assertNull(dest.getAssetName());
    }

    @Test
    public void test_toTransferDto() {
        TransferOrderImpl order = new TransferOrderImpl();
        order.setDestContainerId("destId");
        order.setTransferType(TransferType.LS_OTHER);
        order.setChangeOfBeneficialOwnership(BeneficialOwnerChangeStatus.NO);
        order.setTransferId("transferId");
        order.setAccountId("accountId");
        order.setInvestmentId("investmentId");
        order.setPlatformId("platformId");
        order.setSponsorId("sponsorId");

        DateTime dt = DateTime.now();
        order.setTransferDate(dt);
        order.setStatus(OrderStatus.CANCELLED);

        InspecieTransferDto dto = orderService.toTransferDto(order, contMap);
        Assert.assertNotNull(dto);
        Assert.assertEquals(Boolean.FALSE, dto.getIsCBO());
        Assert.assertEquals(order.getStatus().name(), dto.getTransferStatus());
        Assert.assertEquals(order.getTransferType().name(), dto.getTransferType());
        Assert.assertEquals(dt, dto.getTransferDate());

        SponsorDetailsDtoImpl sponsor = dto.getSponsorDetails();
        Assert.assertEquals(order.getSponsorId(), sponsor.getPid());
        Assert.assertEquals(order.getInvestmentId(), sponsor.getHin());
    }

    @Test
    public void test_toTransferDto_withStatusFromChild() {
        TransferOrderImpl order = new TransferOrderImpl();
        order.setDestContainerId("destId");
        order.setTransferType(TransferType.LS_OTHER);
        order.setChangeOfBeneficialOwnership(BeneficialOwnerChangeStatus.NO);
        order.setTransferId("transferId");
        order.setAccountId("accountId");
        order.setInvestmentId("investmentId");
        order.setPlatformId("platformId");
        order.setSponsorId("sponsorId");

        DateTime dt = DateTime.now();
        order.setTransferDate(dt);
        order.setStatus(null);

        TransferItemImpl transferItem = new TransferItemImpl();
        transferItem.setTransferStatus(OrderStatus.WAITING_SUPPORT_DOC);
        List<TransferItem> transferItems = new ArrayList<>();
        transferItems.add(transferItem);

        order.setTransferItems(transferItems);

        InspecieTransferDto dto = orderService.toTransferDto(order, contMap);
        Assert.assertNotNull(dto);
        Assert.assertEquals(Boolean.FALSE, dto.getIsCBO());
        Assert.assertEquals(transferItem.getTransferStatus().name(), dto.getTransferStatus());
        Assert.assertEquals(order.getTransferType().name(), dto.getTransferType());
        Assert.assertEquals(dt, dto.getTransferDate());

        SponsorDetailsDtoImpl sponsor = dto.getSponsorDetails();
        Assert.assertEquals(order.getSponsorId(), sponsor.getPid());
        Assert.assertEquals(order.getInvestmentId(), sponsor.getHin());
    }

    @Test
    public void test_toTransferDto_Null() {
        InspecieTransferDto dto = orderService.toTransferDto(null, contMap);
        Assert.assertNull(dto);
    }

    @Test
    public void test_searchAccountTransferDto() {
        String accountId = EncodedString.fromPlainText("accountId").toString();
        ApiSearchCriteria criteria = new ApiSearchCriteria("accountId", SearchOperation.EQUALS, accountId, OperationType.STRING);
        List<ApiSearchCriteria> criteriaList = Collections.singletonList(criteria);
        ServiceErrorsImpl errors = new ServiceErrorsImpl();

        // Mock inspecieTransferIntegrationService
        List<TransferOrder> transferOrders = new ArrayList<>();
        TransferOrderImpl order = new TransferOrderImpl();
        order.setDestContainerId("destId");
        order.setAccountId("accountId");
        order.setChangeOfBeneficialOwnership(BeneficialOwnerChangeStatus.NO);
        order.setTransferType(TransferType.LS_BROKER_SPONSORED);
        order.setStatus(OrderStatus.COMPLETED);
        transferOrders.add(order);

        TransferItemImpl item1 = new TransferItemImpl();
        item1.setAssetId("assetId");
        item1.setQuantity(BigDecimal.TEN);
        item1.setTransactionDateTime(DateTime.now());
        item1.setTransferStatus(OrderStatus.IN_PROGRESS);
        List<TransferItem> items = Collections.singletonList((TransferItem) item1);
        order.setTransferItems(items);

        Mockito.when(transferService.loadAccountTransferOrders(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(transferOrders);

        ManagedPortfolioAccountValuationImpl mp = new ManagedPortfolioAccountValuationImpl();
        mp.setAsset(contMap.get("destId"));
        mp.setSubAccountKey(SubAccountKey.valueOf("destId"));
        WrapAccountValuationImpl val = new WrapAccountValuationImpl();
        val.setSubAccountValuations(Collections.singletonList((SubAccountValuation) mp));

        Mockito.when(portfolioService.loadWrapAccountValuation(Mockito.any(AccountKey.class), Mockito.any(DateTime.class),
                Mockito.any(ServiceErrors.class))).thenReturn(val);

        List<InspecieTransferDto> dtoList = orderService.search(criteriaList, errors);

        Assert.assertNotNull(dtoList);
        InspecieTransferDto dto = dtoList.get(0);
        Assert.assertNotNull(dto);
        Assert.assertEquals(Boolean.FALSE, dto.getIsCBO());
        Assert.assertEquals(order.getStatus().name(), dto.getTransferStatus());
        Assert.assertEquals(order.getTransferType().name(), dto.getTransferType());
    }
}
