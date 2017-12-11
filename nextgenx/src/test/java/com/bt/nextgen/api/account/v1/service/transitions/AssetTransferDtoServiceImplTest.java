package com.bt.nextgen.api.account.v1.service.transitions;


import com.bt.nextgen.api.account.v1.model.transitions.TransitionAssetDto;
import com.bt.nextgen.api.account.v1.service.AssetTransferDtoServiceImpl;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetCluster;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus;
import com.bt.nextgen.service.avaloq.asset.ManagedFundAssetImpl;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.integration.account.*;

import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by L069552 on 13/10/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class AssetTransferDtoServiceImplTest {

    @InjectMocks
    AssetTransferDtoServiceImpl assetTransferDtoService;

    @Mock
    AccountIntegrationService accountService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    TransitionSettlementsIntegrationService transitionSettlementsIntegrationService;

    @Mock
    AssetIntegrationService cacheManagedAssetIntegrationService;

    private WrapAccountDetailImpl account;
    private AccountKey accountKey;
    private ProductImpl product;
    private List<TransitionSettlements> transitionSettlements;
    private AccountTransitionSettlements transitionSettlement;
    private Map<ProductKey, Product> mapProducts;
    private Map<String, Asset> assetMap;
    private TransitionSettlementsHolder transitionAccountDetailHolder;
    private List<TransitionAssetDto> transitionAssetDtos;
    TransitionAssetDto transitionAssetDto;

    @Before
    public void setup() {
        account = new WrapAccountDetailImpl();
        accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("34523654");
        account.setAccountKey(accountKey);
        account.setProductKey(ProductKey.valueOf("1234"));
        account.setAccountName("Tom Peters");
        account.setAccountNumber("120011366");
        account.setAdviserPositionId(BrokerKey.valueOf("66773"));
        account.setAccountStructureType(AccountStructureType.Individual);
        account.setAccountStatus(AccountStatus.ACTIVE);

        product = new ProductImpl();
        product.setProductKey(ProductKey.valueOf("1234"));
        product.setProductName("Product1");

        transitionSettlements = new ArrayList<>();
        transitionSettlement = new AccountTransitionSettlements();
        transitionSettlement.setAccountId("34523654");

        transitionSettlement.setAmount(new BigDecimal(123));

        transitionSettlement.setAssetId("121308");
        transitionSettlement.setOrderNumber("12345");
        transitionSettlement.setQuantity("34");
        transitionSettlement.setTransitionDate(new DateTime());
        transitionSettlement.setTransitionWorkflowStatus(ApplicationStatus.DONE);
        transitionSettlements.add(transitionSettlement);

        mapProducts = new HashMap<ProductKey, Product>();
        mapProducts.put(ProductKey.valueOf("1234"), product);
        com.bt.nextgen.api.account.v2.model.AccountKey accountKeyVal = new com.bt.nextgen.api.account.v2.model.AccountKey("34523654");

        assetMap = new HashMap<>();
        ManagedFundAssetImpl asset = new ManagedFundAssetImpl();
        asset.setAssetCode("163253");
        asset.setAssetName("CNA0805AU Macquarie International Infrastructure Securities Fund");
        asset.setAssetType(AssetType.MANAGED_FUND);
        asset.setAssetId("121308");
        asset.setAssetCluster(AssetCluster.MANAGED_FUND);
        asset.setPrice(new BigDecimal(45.88));
        assetMap.put(asset.getAssetId(), asset);

        transitionAccountDetailHolder = new TransitionSettlementsHolderImpl();
        transitionAccountDetailHolder.setTransitionSettlements(transitionSettlements);
        transitionAccountDetailHolder.setAccountKey(accountKey);

        transitionAssetDtos = new ArrayList<>();
        transitionAssetDto = new TransitionAssetDto(accountKeyVal);
        transitionAssetDto.setAssetCluster(AssetCluster.MANAGED_FUND.name());
        transitionAssetDto.setAssetName("FDHG AQR Wholesale Managed Futures Fund- Class 1P");
        transitionAssetDto.setAssetCode("121308");
        transitionAssetDto.setConsiderationAmt(new BigDecimal(567));
        transitionAssetDto.setSubmittedTimestamp(new DateTime());
        transitionAssetDto.setOrderId("12345");
        transitionAssetDto.setTransitionStatus("Complete");
        transitionAssetDto.setAccountName("Tom Peters");
        transitionAssetDto.setAccountNumber("120011366");
        transitionAssetDto.setProductName(product.getProductName());
        transitionAssetDto.setAccountType("Individual");
        transitionAssetDto.setQuantity(new BigDecimal(34));
        transitionAssetDtos.add(transitionAssetDto);

        when(productIntegrationService.loadProductsMap(any(ServiceErrors.class))).thenReturn(mapProducts);
        when(accountService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(ServiceErrors.class))).thenReturn(account);
        when(cacheManagedAssetIntegrationService.loadExternalAssets(any(ServiceErrors.class))).thenReturn(assetMap);
        when(transitionSettlementsIntegrationService.getAssetTransferStatus(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(transitionAccountDetailHolder);
    }

    @Test
    public void testSearchAccountsForTransfer() {
        List<ApiSearchCriteria> criteria = new ArrayList<ApiSearchCriteria>();
        criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS, "901CBD11D6E7365E4DFA7994B2DE32A9C6073F5E7186AE3F", ApiSearchCriteria.OperationType.STRING));
        List<TransitionAssetDto> transitionAssets = assetTransferDtoService.search(criteria, new ServiceErrorsImpl());

        Assert.assertNotNull(transitionAssets);
        Assert.assertEquals(transitionAssets.size(), transitionAssetDtos.size());
        Assert.assertEquals(transitionAssets.get(0).getTransitionStatus(), transitionAssetDtos.get(0).getTransitionStatus());
    }

    @Test
    public void testSearchAccountsForTransferWithRunCancellationStatus() {
        setUpAssetTransferStatus("Transfer cancellation in progress", ApplicationStatus.RUN_CANCEL);

        List<ApiSearchCriteria> criteria = new ArrayList<ApiSearchCriteria>();
        criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS, "901CBD11D6E7365E4DFA7994B2DE32A9C6073F5E7186AE3F", ApiSearchCriteria.OperationType.STRING));
        List<TransitionAssetDto> transitionAssets = assetTransferDtoService.search(criteria, new ServiceErrorsImpl());

        Assert.assertNotNull(transitionAssets);
        Assert.assertEquals(transitionAssets.size(), transitionAssetDtos.size());
        Assert.assertEquals(transitionAssets.get(0).getTransitionStatus(), transitionAssetDtos.get(0).getTransitionStatus());
    }

    private void setUpAssetTransferStatus(String dtoStatus, ApplicationStatus workflowStatus) {
        transitionAssetDtos = new ArrayList<>();
        transitionAssetDto.setTransitionStatus(dtoStatus);
        transitionAssetDtos.add(transitionAssetDto);

        transitionSettlements = new ArrayList<>();
        transitionSettlement.setTransitionWorkflowStatus(workflowStatus);
        transitionSettlements.add(transitionSettlement);
    }
}
