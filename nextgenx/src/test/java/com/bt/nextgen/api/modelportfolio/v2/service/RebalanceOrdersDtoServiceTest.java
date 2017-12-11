package com.bt.nextgen.api.modelportfolio.v2.service;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.RebalanceOrderDetailsDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.RebalanceOrderGroupDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.RebalanceOrdersDto;
import com.bt.nextgen.api.modelportfolio.v2.service.rebalance.RebalanceOrdersDtoServiceImpl;
import com.bt.nextgen.api.modelportfolio.v2.util.common.ModelPortfolioHelper;
import com.bt.nextgen.api.modelportfolio.v2.util.rebalance.RebalanceOrdersSortingHelper;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalanceIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceOrderDetails;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceOrderGroup;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class RebalanceOrdersDtoServiceTest {

    @InjectMocks
    private RebalanceOrdersDtoServiceImpl rebalanceOrdersDtoService;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private TailorMadePortfolioDtoService tmpDtoService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private ModelPortfolioRebalanceIntegrationService rebalanceIntegrationService;

    @Mock
    private ModelPortfolioHelper helper;

    @Mock
    private RebalanceOrdersSortingHelper sortingHelper;

    private RebalanceOrderGroup orderGroup1;
    private RebalanceOrderGroup orderGroup2;
    private RebalanceOrderGroup orderGroup3;

    @Before
    public void setup() {
        mockAccountService();
        mockBrokerService();
        mockAssetService();
        mockRebalanceData();
    }

    @Test
    public void testGetRebalanceOrdersForIps() {
        Mockito.when(
                rebalanceIntegrationService.loadRebalanceOrdersForIps(Mockito.any(IpsKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(orderGroup1, orderGroup2));

        RebalanceOrdersDto ordersDto = rebalanceOrdersDtoService.find(IpsKey.valueOf("ipsId"), new FailFastErrorsImpl());

        verifyRebalanceData(ordersDto);
    }

    @Test
    public void testGetRebalanceOrders() {
        Mockito.when(
                rebalanceIntegrationService.loadRebalanceOrders(Mockito.anyListOf(String.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(orderGroup1, orderGroup2));

        RebalanceOrdersDto ordersDto = rebalanceOrdersDtoService.findByDocIds(IpsKey.valueOf("ipsId"),
                Arrays.asList("200", "201"), new FailFastErrorsImpl());

        verifyRebalanceData(ordersDto);
    }

    @Test
    public void testFullRedemption() {
        Mockito.when(
                rebalanceIntegrationService.loadRebalanceOrders(Mockito.anyListOf(String.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(orderGroup3));

        RebalanceOrdersDto ordersDto = rebalanceOrdersDtoService.findByDocIds(IpsKey.valueOf("ipsId"),
                Arrays.asList("200", "201"), new FailFastErrorsImpl());

        verifyFullRedemption(ordersDto);
    }

    private void verifyRebalanceData(RebalanceOrdersDto ordersDto) {

        Assert.assertNotNull(ordersDto);
        Assert.assertEquals(IpsKey.valueOf("ipsId"), ordersDto.getKey());

        List<RebalanceOrderGroupDto> orderGroupDtoList = ordersDto.getOrderGroups();

        Assert.assertEquals(1, orderGroupDtoList.size());

        RebalanceOrderGroupDto orderGroupDto = orderGroupDtoList.get(0);

        Assert.assertEquals("fname mname lname", orderGroupDto.getAdviserName());
        Assert.assertEquals("Model Name", orderGroupDto.getModelName());
        Assert.assertEquals("Model Code", orderGroupDto.getModelSymbol());
        Assert.assertEquals(new DateTime("2016-03-02"), orderGroupDto.getRebalanceDate());

        List<RebalanceOrderDetailsDto> orderDetailsDtoList = orderGroupDto.getOrderDetails();

        Assert.assertEquals(2, orderDetailsDtoList.size());

        RebalanceOrderDetailsDto orderDetailsDto = orderDetailsDtoList.get(0);

        Assert.assertEquals("Account Name", orderDetailsDto.getAccountName());
        Assert.assertEquals("Account Number", orderDetailsDto.getAccountNumber());
        Assert.assertEquals(AccountStructureType.Individual.name(), orderDetailsDto.getAccountType());
        Assert.assertEquals("Asset Name", orderDetailsDto.getAssetName());
        Assert.assertEquals("Asset Code", orderDetailsDto.getAssetCode());
        Assert.assertEquals("Cash", orderDetailsDto.getAssetClass());
        Assert.assertEquals(AssetType.MANAGED_FUND.getDisplayName(), orderDetailsDto.getAssetType());
        Assert.assertEquals("Preference", orderDetailsDto.getPreference());
        Assert.assertEquals(BigDecimal.valueOf(2), orderDetailsDto.getModelWeight());
        Assert.assertEquals(BigDecimal.valueOf(2), orderDetailsDto.getTargetWeight());
        Assert.assertEquals(BigDecimal.valueOf(1.5), orderDetailsDto.getCurrentWeight());
        Assert.assertEquals(BigDecimal.valueOf(-0.5), orderDetailsDto.getDiffWeight());
        Assert.assertEquals(BigDecimal.TEN, orderDetailsDto.getTargetValue());
        Assert.assertEquals(BigDecimal.valueOf(9), orderDetailsDto.getCurrentValue());
        Assert.assertEquals(BigDecimal.valueOf(-1), orderDetailsDto.getDiffValue());
        Assert.assertEquals(BigDecimal.valueOf(100), orderDetailsDto.getTargetQuantity());
        Assert.assertEquals(BigDecimal.valueOf(90), orderDetailsDto.getCurrentQuantity());
        Assert.assertEquals(BigDecimal.valueOf(-10), orderDetailsDto.getDiffQuantity());
        Assert.assertEquals("Buy", orderDetailsDto.getOrderType());
        Assert.assertEquals(BigDecimal.ONE, orderDetailsDto.getOrderAmount());
        Assert.assertEquals(BigDecimal.valueOf(0.1), orderDetailsDto.getEstimatedPrice());
        Assert.assertEquals(BigDecimal.TEN, orderDetailsDto.getOrderQuantity());
        Assert.assertEquals(BigDecimal.valueOf(2), orderDetailsDto.getFinalWeight());
        Assert.assertEquals(BigDecimal.TEN, orderDetailsDto.getFinalValue());
        Assert.assertEquals(BigDecimal.valueOf(100), orderDetailsDto.getFinalQuantity());
        Assert.assertEquals("Good reason", orderDetailsDto.getComments());
        Assert.assertEquals(Boolean.FALSE, orderDetailsDto.getIsSellAll());
        Assert.assertEquals(Boolean.FALSE, orderDetailsDto.getIsFullModelRedemption());
        Assert.assertEquals(Boolean.FALSE, orderDetailsDto.isHideOrder());

        RebalanceOrderDetailsDto orderDetailsDto2 = orderDetailsDtoList.get(1);

        Assert.assertEquals("Sell all", orderDetailsDto2.getComments());
        Assert.assertEquals("Sell", orderDetailsDto2.getOrderType());
        Assert.assertEquals(Boolean.TRUE, orderDetailsDto2.getIsSellAll());
        Assert.assertEquals(BigDecimal.valueOf(90), orderDetailsDto2.getOrderQuantity());
        Assert.assertEquals(Boolean.TRUE, orderDetailsDto2.getIsFullModelRedemption());
        Assert.assertEquals(Boolean.FALSE, orderDetailsDto2.isHideOrder());
    }

    private void verifyFullRedemption(RebalanceOrdersDto ordersDto) {
        List<RebalanceOrderGroupDto> orderGroupDtoList = ordersDto.getOrderGroups();

        Assert.assertEquals(1, orderGroupDtoList.size());
        RebalanceOrderGroupDto orderGroupDto = orderGroupDtoList.get(0);

        List<RebalanceOrderDetailsDto> orderDetailsDtoList = orderGroupDto.getOrderDetails();

        Assert.assertEquals(3, orderDetailsDtoList.size());

        RebalanceOrderDetailsDto orderDetailsDto = orderDetailsDtoList.get(0);

        Assert.assertEquals(true, orderDetailsDto.getIsFullModelRedemption());
        Assert.assertEquals(null, orderDetailsDto.getCurrentWeight());
        Assert.assertEquals(null, orderDetailsDto.getDiffWeight());
    }

    private void mockAccountService() {
        WrapAccount account = Mockito.mock(WrapAccount.class);
        Mockito.when(account.getAccountName()).thenReturn("Account Name");
        Mockito.when(account.getAccountNumber()).thenReturn("Account Number");
        Mockito.when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        accountMap.put(AccountKey.valueOf("accountId"), account);

        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers(Mockito.any(ServiceErrors.class))).thenReturn(
                accountMap);
    }

    private void mockBrokerService() {
        BrokerUser user = Mockito.mock(BrokerUser.class);
        Mockito.when(user.getFirstName()).thenReturn("fname");
        Mockito.when(user.getLastName()).thenReturn("lname");
        Mockito.when(user.getMiddleName()).thenReturn("mname");

        Mockito.when(
                brokerIntegrationService.getAdviserBrokerUser(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(user);
    }

    private void mockAssetService() {
        Asset asset = Mockito.mock(Asset.class);
        Mockito.when(asset.getAssetName()).thenReturn("Asset Name");
        Mockito.when(asset.getAssetCode()).thenReturn("Asset Code");
        Mockito.when(asset.getAssetClass()).thenReturn(AssetClass.CASH);
        Mockito.when(asset.getAssetType()).thenReturn(AssetType.MANAGED_FUND);

        Asset asset1 = Mockito.mock(Asset.class);
        Mockito.when(asset1.getAssetName()).thenReturn("Asset Name1");
        Mockito.when(asset1.getAssetCode()).thenReturn("Asset Code1");
        Mockito.when(asset1.getAssetType()).thenReturn(AssetType.SHARE);

        Asset asset2 = Mockito.mock(Asset.class);
        Mockito.when(asset2.getAssetName()).thenReturn("Asset Name2");
        Mockito.when(asset2.getAssetCode()).thenReturn("Asset Code2");
        Mockito.when(asset2.getAssetClass()).thenReturn(AssetClass.CASH);
        Mockito.when(asset2.getAssetType()).thenReturn(AssetType.CASH);
        Mockito.when(asset2.getAssetId()).thenReturn("tmpAssetId");

        Map<String, Asset> assetMap = new HashMap<>();
        assetMap.put("assetId", asset);
        assetMap.put("assetId1", asset1);
        assetMap.put("assetId2", asset2);

        AssetDto assetDto = new AssetDto();
        assetDto.setAssetId("tmpAssetId");
        assetDto.setAssetClass(AssetClass.CASH.toString());
        
        Mockito.when(assetIntegrationService.loadAssets(Mockito.anyListOf(String.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(assetMap);
        
        Mockito.when(tmpDtoService.findOne(Mockito.any(ServiceErrors.class))).thenReturn(assetDto);
    }

    private void mockRebalanceData() {

        orderGroup1 = Mockito.mock(RebalanceOrderGroup.class);
        Mockito.when(orderGroup1.getAdviser()).thenReturn(BrokerKey.valueOf("brokerKey"));
        Mockito.when(orderGroup1.getModelName()).thenReturn("Model Name");
        Mockito.when(orderGroup1.getModelSymbol()).thenReturn("Model Code");
        Mockito.when(orderGroup1.getRebalanceDate()).thenReturn(new DateTime("2016-03-02"));

        RebalanceOrderDetails orderDetails = Mockito.mock(RebalanceOrderDetails.class);
        Mockito.when(orderDetails.getAccount()).thenReturn("accountId");
        Mockito.when(orderDetails.getAsset()).thenReturn("assetId");
        Mockito.when(orderDetails.getPreference()).thenReturn("Preference");
        Mockito.when(orderDetails.getModelWeight()).thenReturn(BigDecimal.valueOf(2));
        Mockito.when(orderDetails.getTargetWeight()).thenReturn(BigDecimal.valueOf(2));
        Mockito.when(orderDetails.getCurrentWeight()).thenReturn(BigDecimal.valueOf(1.5));
        Mockito.when(orderDetails.getDiffWeight()).thenReturn(BigDecimal.valueOf(0.5));
        Mockito.when(orderDetails.getTargetValue()).thenReturn(BigDecimal.TEN);
        Mockito.when(orderDetails.getCurrentValue()).thenReturn(BigDecimal.valueOf(9));
        Mockito.when(orderDetails.getDiffValue()).thenReturn(BigDecimal.ONE);
        Mockito.when(orderDetails.getTargetQuantity()).thenReturn(BigDecimal.valueOf(100));
        Mockito.when(orderDetails.getCurrentQuantity()).thenReturn(BigDecimal.valueOf(90));
        Mockito.when(orderDetails.getDiffQuantity()).thenReturn(BigDecimal.TEN);
        Mockito.when(orderDetails.getOrderType()).thenReturn("Buy");
        Mockito.when(orderDetails.getIsSellAll()).thenReturn(false);
        Mockito.when(orderDetails.getOrderValue()).thenReturn(BigDecimal.ONE);
        Mockito.when(orderDetails.getOrderQuantity()).thenReturn(BigDecimal.TEN);
        Mockito.when(orderDetails.getFinalWeight()).thenReturn(BigDecimal.valueOf(2));
        Mockito.when(orderDetails.getFinalValue()).thenReturn(BigDecimal.TEN);
        Mockito.when(orderDetails.getFinalQuantity()).thenReturn(BigDecimal.valueOf(100));
        Mockito.when(orderDetails.getReasonForExclusion()).thenReturn("Good reason");

        Mockito.when(orderGroup1.getOrderDetails()).thenReturn(Arrays.asList(orderDetails));

        orderGroup2 = Mockito.mock(RebalanceOrderGroup.class);
        Mockito.when(orderGroup2.getAdviser()).thenReturn(BrokerKey.valueOf("brokerKey2"));
        Mockito.when(orderGroup2.getModelName()).thenReturn("Model Name");
        Mockito.when(orderGroup2.getModelSymbol()).thenReturn("Model Code");
        Mockito.when(orderGroup2.getRebalanceDate()).thenReturn(new DateTime("2016-03-02"));

        RebalanceOrderDetails orderDetails2 = Mockito.mock(RebalanceOrderDetails.class);
        Mockito.when(orderDetails.getAccount()).thenReturn("accountId");
        Mockito.when(orderDetails2.getIsSellAll()).thenReturn(Boolean.valueOf(true));
        Mockito.when(orderDetails2.getTargetWeight()).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(orderDetails2.getReasonForExclusion()).thenReturn(null);
        Mockito.when(orderDetails2.getCurrentQuantity()).thenReturn(BigDecimal.valueOf(90));
        Mockito.when(orderDetails2.getAsset()).thenReturn("assetId");

        Mockito.when(orderGroup2.getOrderDetails()).thenReturn(Arrays.asList(orderDetails2));

        orderGroup3 = Mockito.mock(RebalanceOrderGroup.class);
        Mockito.when(orderGroup3.getAdviser()).thenReturn(BrokerKey.valueOf("brokerKey1"));
        Mockito.when(orderGroup3.getModelName()).thenReturn("Model Name1");
        Mockito.when(orderGroup3.getModelSymbol()).thenReturn("Model Code1");
        Mockito.when(orderGroup3.getRebalanceDate()).thenReturn(new DateTime("2016-03-02"));

        RebalanceOrderDetails orderDetails3 = Mockito.mock(RebalanceOrderDetails.class);
        Mockito.when(orderDetails3.getAccount()).thenReturn("accountId");
        Mockito.when(orderDetails3.getAsset()).thenReturn("assetId1");
        Mockito.when(orderDetails3.getPreference()).thenReturn("Preference");
        Mockito.when(orderDetails3.getModelWeight()).thenReturn(BigDecimal.valueOf(2));
        Mockito.when(orderDetails3.getTargetWeight()).thenReturn(BigDecimal.valueOf(0));
        Mockito.when(orderDetails3.getCurrentWeight()).thenReturn(BigDecimal.valueOf(1.5));
        Mockito.when(orderDetails3.getDiffWeight()).thenReturn(BigDecimal.valueOf(0.5));
        Mockito.when(orderDetails3.getTargetValue()).thenReturn(BigDecimal.TEN);
        Mockito.when(orderDetails3.getCurrentValue()).thenReturn(BigDecimal.valueOf(9));
        Mockito.when(orderDetails3.getDiffValue()).thenReturn(BigDecimal.ONE);
        Mockito.when(orderDetails3.getTargetQuantity()).thenReturn(BigDecimal.valueOf(100));
        Mockito.when(orderDetails3.getCurrentQuantity()).thenReturn(BigDecimal.valueOf(90));
        Mockito.when(orderDetails3.getDiffQuantity()).thenReturn(BigDecimal.TEN);
        Mockito.when(orderDetails3.getOrderType()).thenReturn("Sell");
        Mockito.when(orderDetails3.getIsSellAll()).thenReturn(null);
        Mockito.when(orderDetails3.getOrderValue()).thenReturn(BigDecimal.ONE);
        Mockito.when(orderDetails3.getOrderQuantity()).thenReturn(BigDecimal.TEN);
        Mockito.when(orderDetails3.getFinalWeight()).thenReturn(BigDecimal.valueOf(2));
        Mockito.when(orderDetails3.getFinalValue()).thenReturn(BigDecimal.TEN);
        Mockito.when(orderDetails3.getFinalQuantity()).thenReturn(BigDecimal.valueOf(100));
        Mockito.when(orderDetails3.getReasonForExclusion()).thenReturn("Good reason");

        RebalanceOrderDetails orderDetails4 = Mockito.mock(RebalanceOrderDetails.class);
        Mockito.when(orderDetails4.getAccount()).thenReturn("accountId");
        Mockito.when(orderDetails4.getAsset()).thenReturn("assetId2");
        Mockito.when(orderDetails4.getPreference()).thenReturn("Preference");
        Mockito.when(orderDetails4.getModelWeight()).thenReturn(BigDecimal.valueOf(2));
        Mockito.when(orderDetails4.getTargetWeight()).thenReturn(BigDecimal.valueOf(2));
        Mockito.when(orderDetails4.getCurrentWeight()).thenReturn(BigDecimal.valueOf(1.5));
        Mockito.when(orderDetails4.getDiffWeight()).thenReturn(BigDecimal.valueOf(0.5));
        Mockito.when(orderDetails4.getTargetValue()).thenReturn(BigDecimal.TEN);
        Mockito.when(orderDetails4.getCurrentValue()).thenReturn(BigDecimal.valueOf(9));
        Mockito.when(orderDetails4.getDiffValue()).thenReturn(BigDecimal.ONE);
        Mockito.when(orderDetails4.getTargetQuantity()).thenReturn(BigDecimal.valueOf(100));
        Mockito.when(orderDetails4.getCurrentQuantity()).thenReturn(BigDecimal.valueOf(90));
        Mockito.when(orderDetails4.getDiffQuantity()).thenReturn(BigDecimal.TEN);
        Mockito.when(orderDetails4.getOrderType()).thenReturn("Buy");
        Mockito.when(orderDetails4.getIsSellAll()).thenReturn(false);
        Mockito.when(orderDetails4.getOrderValue()).thenReturn(BigDecimal.ONE);
        Mockito.when(orderDetails4.getOrderQuantity()).thenReturn(BigDecimal.TEN);
        Mockito.when(orderDetails4.getFinalWeight()).thenReturn(BigDecimal.valueOf(2));
        Mockito.when(orderDetails4.getFinalValue()).thenReturn(BigDecimal.TEN);
        Mockito.when(orderDetails4.getFinalQuantity()).thenReturn(BigDecimal.valueOf(100));
        Mockito.when(orderDetails4.getReasonForExclusion()).thenReturn("Good reason");

        RebalanceOrderDetails orderDetails5 = Mockito.mock(RebalanceOrderDetails.class);
        Mockito.when(orderDetails5.getAccount()).thenReturn("accountId");
        Mockito.when(orderDetails5.getAsset()).thenReturn("assetId2");
        Mockito.when(orderDetails5.getPreference()).thenReturn("Preference");
        Mockito.when(orderDetails5.getModelWeight()).thenReturn(BigDecimal.valueOf(2));
        Mockito.when(orderDetails5.getTargetWeight()).thenReturn(BigDecimal.valueOf(2));
        Mockito.when(orderDetails5.getCurrentWeight()).thenReturn(BigDecimal.valueOf(1.5));
        Mockito.when(orderDetails5.getDiffWeight()).thenReturn(BigDecimal.valueOf(0.5));
        Mockito.when(orderDetails5.getTargetValue()).thenReturn(BigDecimal.TEN);
        Mockito.when(orderDetails5.getCurrentValue()).thenReturn(BigDecimal.valueOf(9));
        Mockito.when(orderDetails5.getDiffValue()).thenReturn(BigDecimal.ONE);
        Mockito.when(orderDetails5.getTargetQuantity()).thenReturn(BigDecimal.valueOf(100));
        Mockito.when(orderDetails5.getCurrentQuantity()).thenReturn(BigDecimal.valueOf(90));
        Mockito.when(orderDetails5.getDiffQuantity()).thenReturn(BigDecimal.TEN);
        Mockito.when(orderDetails5.getOrderType()).thenReturn("Sell");
        Mockito.when(orderDetails5.getIsSellAll()).thenReturn(null);
        Mockito.when(orderDetails5.getOrderValue()).thenReturn(null);
        Mockito.when(orderDetails5.getOrderQuantity()).thenReturn(BigDecimal.TEN);
        Mockito.when(orderDetails5.getFinalWeight()).thenReturn(BigDecimal.valueOf(2));
        Mockito.when(orderDetails5.getFinalValue()).thenReturn(BigDecimal.TEN);
        Mockito.when(orderDetails5.getFinalQuantity()).thenReturn(BigDecimal.valueOf(100));
        Mockito.when(orderDetails5.getReasonForExclusion()).thenReturn("Good reason");

        Mockito.when(orderGroup3.getOrderDetails()).thenReturn(Arrays.asList(orderDetails3, orderDetails4, orderDetails5));
    }
}
