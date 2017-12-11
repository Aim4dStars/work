package com.bt.nextgen.api.inspecietransfer.v3.service;

import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferKey;
import com.bt.nextgen.api.inspecietransfer.v3.model.TransferDest;
import com.bt.nextgen.api.inspecietransfer.v3.model.TransferItemDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.TransferOrderDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedPortfolioAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.WrapAccountValuationImpl;
import com.bt.nextgen.service.avaloq.transfer.TransferItemImpl;
import com.bt.nextgen.service.avaloq.transfer.TransferOrderImpl;
import com.bt.nextgen.service.integration.Origin;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetType;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class TransferOrderDtoServiceTest {

    @InjectMocks
    private TransferOrderDtoServiceImpl orderDtoService;

    @Mock
    private InspecieTransferIntegrationService transferService;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private AssetIntegrationService assetService;

    @Mock
    private PortfolioIntegrationService portfolioService;

    private Map<String, Asset> contMap;
    private Map<String, Asset> assetMap;

    @Before
    public void setup() throws Exception {
        AssetImpl asset = new AssetImpl();
        asset.setAssetId("assetId");
        asset.setAssetCode("assetCode");
        asset.setAssetName("assetName");
        asset.setAssetType(AssetType.MANAGED_PORTFOLIO);

        contMap = new HashMap<>();
        contMap.put("destId", asset);

        assetMap = new HashMap<>();
        assetMap.put("assetId", asset);

        Mockito.when(assetService.loadAssets(Mockito.anyCollectionOf(String.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(assetMap);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testSearchWithInvalidCriteria() {        
        ApiSearchCriteria criteria = new ApiSearchCriteria("invalidKey", SearchOperation.EQUALS, "value", OperationType.STRING);
        List<ApiSearchCriteria> criteriaList = Collections.singletonList(criteria);
        
        orderDtoService.search(criteriaList, new FailFastErrorsImpl());
    }

    @Test
    public void testSearchWhenNoOrders() {
        List<TransferOrder> emptyTransferOrders = new ArrayList<>();
        Mockito.when(transferService.loadAccountTransferOrders(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
        .thenReturn(emptyTransferOrders);
        
        String accountId = EncodedString.fromPlainText("accountId").toString();
        ApiSearchCriteria criteria = new ApiSearchCriteria("accountId", SearchOperation.EQUALS, accountId, OperationType.STRING);
        List<ApiSearchCriteria> criteriaList = Collections.singletonList(criteria);

        List<TransferOrderDto> dtos = orderDtoService.search(criteriaList, new FailFastErrorsImpl());
        Assert.assertEquals(0, dtos.size());        
    }
    
    @Test
    public void testSearchOrdersPresent() {

        String accountId = EncodedString.fromPlainText("accountId").toString();
        ApiSearchCriteria criteria = new ApiSearchCriteria("accountId", SearchOperation.EQUALS, accountId, OperationType.STRING);
        List<ApiSearchCriteria> criteriaList = Collections.singletonList(criteria);

        TransferItem transferItem = Mockito.mock(TransferItemImpl.class);
        Mockito.when(transferItem.getAssetId()).thenReturn("assetId");
        Mockito.when(transferItem.getQuantity()).thenReturn(BigDecimal.TEN);
        Mockito.when(transferItem.getTransactionDateTime()).thenReturn(new DateTime("2016-01-01"));
        Mockito.when(transferItem.getTransferStatus()).thenReturn(OrderStatus.IN_PROGRESS);

        TransferOrder order = Mockito.mock(TransferOrderImpl.class);
        Mockito.when(order.getDestContainerId()).thenReturn("destId");
        Mockito.when(order.getAccountId()).thenReturn("accountId");
        Mockito.when(order.getTransferId()).thenReturn("transferId");
        Mockito.when(order.getChangeOfBeneficialOwnership()).thenReturn(BeneficialOwnerChangeStatus.NO);
        Mockito.when(order.getTransferType()).thenReturn(TransferType.LS_BROKER_SPONSORED);
        Mockito.when(order.getStatus()).thenReturn(OrderStatus.COMPLETED);
        Mockito.when(order.getMedium()).thenReturn(Origin.BACK_OFFICE);
        Mockito.when(order.getTransferDate()).thenReturn(new DateTime("2016-01-01"));
        Mockito.when(order.getTransferItems()).thenReturn(Arrays.asList(transferItem));

        Mockito.when(transferService.loadAccountTransferOrders(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(order));

        ManagedPortfolioAccountValuationImpl mp = new ManagedPortfolioAccountValuationImpl();
        mp.setAsset(contMap.get("destId"));
        mp.setSubAccountKey(SubAccountKey.valueOf("destId"));
        WrapAccountValuationImpl val = new WrapAccountValuationImpl();
        val.setSubAccountValuations(Collections.singletonList((SubAccountValuation) mp));

        Mockito.when(portfolioService.loadWrapAccountValuation(Mockito.any(AccountKey.class), Mockito.any(DateTime.class),
                Mockito.any(ServiceErrors.class))).thenReturn(val);

        List<TransferOrderDto> dtoList = orderDtoService.search(criteriaList, new FailFastErrorsImpl());

        Assert.assertNotNull(dtoList);
        Assert.assertEquals(1, dtoList.size());

        TransferOrderDto dto = dtoList.get(0);
        Assert.assertNotNull(dto);
        Assert.assertEquals(new DateTime("2016-01-01"), dto.getTransferDate());
        Assert.assertEquals(Boolean.FALSE, dto.getIsCBO());
        Assert.assertEquals(Boolean.FALSE, dto.getInitiatedOnline());
        Assert.assertEquals(OrderStatus.COMPLETED.name(), dto.getTransferStatus());
        Assert.assertEquals(TransferType.LS_BROKER_SPONSORED.name(), dto.getTransferType());

        InspecieTransferKey key = dto.getKey();
        Assert.assertNotNull(key);
        Assert.assertEquals("accountId", key.getAccountId());
        Assert.assertEquals("transferId", key.getTransferId());

        TransferDest destDto = dto.getDest();
        Assert.assertNotNull(destDto);
        Assert.assertEquals("destId", destDto.getDestContainerId());
        Assert.assertEquals("assetId", destDto.getAssetId());
        Assert.assertEquals("assetCode", destDto.getAssetCode());
        Assert.assertEquals("assetName", destDto.getAssetName());
        Assert.assertEquals(AssetType.MANAGED_PORTFOLIO.name(), destDto.getAssetType());

        Assert.assertEquals(1, dto.getTransferItems().size());
        TransferItemDto itemDto = dto.getTransferItems().get(0);
        Assert.assertEquals("assetCode", itemDto.getAssetCode());
        Assert.assertEquals("assetName", itemDto.getAssetName());
        Assert.assertEquals(BigDecimal.TEN, itemDto.getQuantity());
        Assert.assertEquals(OrderStatus.IN_PROGRESS.name(), itemDto.getTransferStatus());
        Assert.assertEquals(new DateTime("2016-01-01"), itemDto.getTransferDate());
    }
}
