package com.bt.nextgen.reports.modelportfolio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.AccountRebalanceKey;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.ModelPortfolioRebalanceAccountDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.ModelPortfolioRebalanceDetailDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.RebalanceOrderDetailsDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.RebalanceOrderGroupDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.RebalanceOrdersDto;
import com.bt.nextgen.api.modelportfolio.v2.service.rebalance.ModelPortfolioRebalanceDetailDtoService;
import com.bt.nextgen.api.modelportfolio.v2.service.rebalance.RebalanceOrdersDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.ips.IpsKey;

@RunWith(MockitoJUnitRunner.class)
public class RebalanceOrdersReportTest {

    @InjectMocks
    private RebalanceOrdersReport rebalanceOrdersReport;
    
    @Mock
    private RebalanceOrdersDtoService rebalanceOrdersService;

    @Mock
    private ModelPortfolioRebalanceDetailDtoService rebalanceDetailsService;
    
    @Mock
    private Map<String, Object> mockParams;
    
    private static final String PARAM_IPS_ID = "ips";
    private static final String PARAM_ORDER_ID = "order-id";
    
    @Test
    public void test_getData_getRebalanceOrderGroups() {
        
        RebalanceOrderGroupDto rebalanceOrderGroup = Mockito.mock(RebalanceOrderGroupDto.class);
        RebalanceOrderGroupDto rebalanceOrderGroup1 = Mockito.mock(RebalanceOrderGroupDto.class);
        
        RebalanceOrderDetailsDto details1 = Mockito.mock(RebalanceOrderDetailsDto.class);
        Mockito.when(details1.getAccountName()).thenReturn("Joe");
        Mockito.when(details1.getAccountNumber()).thenReturn("12999910");
        Mockito.when(details1.getAssetName()).thenReturn("BHP Billiton");
        Mockito.when(details1.getAssetClass()).thenReturn("Australian Shares");
        Mockito.when(details1.getIsSellAll()).thenReturn(true);
        Mockito.when(details1.getAssetType()).thenReturn(AssetType.SHARE.toString());
        Mockito.when(details1.isHideOrder()).thenReturn(false);
        Mockito.when(details1.getIsFullModelRedemption()).thenReturn(Boolean.TRUE);

        RebalanceOrderDetailsDto details2 = Mockito.mock(RebalanceOrderDetailsDto.class);
        Mockito.when(details2.getAccountName()).thenReturn("Joe");
        Mockito.when(details2.getAccountNumber()).thenReturn("12999910");
        Mockito.when(details2.getAssetName()).thenReturn("TMP Cash");
        Mockito.when(details2.getAssetClass()).thenReturn("Cash");
        Mockito.when(details2.getIsSellAll()).thenReturn(false);
        Mockito.when(details2.getAssetType()).thenReturn(AssetType.CASH.toString());
        Mockito.when(details1.isHideOrder()).thenReturn(true);
        Mockito.when(details2.getIsFullModelRedemption()).thenReturn(Boolean.TRUE);

        List<RebalanceOrderDetailsDto> detailList = new ArrayList<>();
        detailList.add(details1);
        detailList.add(details2);

        RebalanceOrdersDto rebalanceOrdersDto = Mockito.mock(RebalanceOrdersDto.class);
        Mockito.when(rebalanceOrdersDto.getOrderGroups()).thenReturn(Arrays.asList(rebalanceOrderGroup, rebalanceOrderGroup1));
        
        Mockito.when(
                rebalanceOrdersService.findByDocIds(Mockito.any(IpsKey.class), Mockito.anyListOf(String.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(rebalanceOrdersDto);
        
        Mockito.when(mockParams.get(PARAM_IPS_ID)).thenReturn("ipsId");
        Mockito.when(mockParams.get(PARAM_ORDER_ID)).thenReturn("orderId");
        Mockito.when(rebalanceOrderGroup.getOrderDetails()).thenReturn(detailList);
        Map<String, Object> dataCollections = new HashMap<String, Object>();
        
        Collection<?> data = rebalanceOrdersReport.getData(mockParams, dataCollections);
        
        Assert.assertNotNull(data);
        Assert.assertEquals(1, data.size());

        Assert.assertEquals(1, ((RebalanceOrderGroupDto) data.iterator().next()).getOrderDetails().size());

    }

    @Test
    public void test_getModelValue_getRebalanceAccount() {

        Mockito.when(mockParams.get(PARAM_IPS_ID)).thenReturn("ipsId");
        Mockito.when(mockParams.get(PARAM_ORDER_ID)).thenReturn("orderId");
        Map<String, Object> dataCollections = new HashMap<String, Object>();
        ModelPortfolioRebalanceAccountDto account1 = Mockito.mock(ModelPortfolioRebalanceAccountDto.class);
        Mockito.when(account1.getKey()).thenReturn(
                new AccountRebalanceKey("ipsId", EncodedString.fromPlainText("notAccountId").toString()));
        Mockito.when(account1.getModelValue()).thenReturn(BigDecimal.ONE);

        ModelPortfolioRebalanceAccountDto account2 = Mockito.mock(ModelPortfolioRebalanceAccountDto.class);
        Mockito.when(account2.getKey()).thenReturn(
                new AccountRebalanceKey("ipsId", EncodedString.fromPlainText("accountId").toString()));
        Mockito.when(account2.getModelValue()).thenReturn(BigDecimal.TEN);

        ModelPortfolioRebalanceDetailDto detailDto = Mockito.mock(ModelPortfolioRebalanceDetailDto.class);
        Mockito.when(detailDto.getRebalanceAccounts()).thenReturn(Arrays.asList(account1, account2));

        Mockito.when(rebalanceDetailsService.find(Mockito.any(ModelPortfolioKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(detailDto);

        Mockito.when(mockParams.get(UriMappingConstants.ACCOUNT_ID_URI_MAPPING)).thenReturn(
                EncodedString.fromPlainText("accountId").toString());

        RebalanceOrderGroupDto rebalanceOrderGroup = Mockito.mock(RebalanceOrderGroupDto.class);
        RebalanceOrderDetailsDto details1 = Mockito.mock(RebalanceOrderDetailsDto.class);
        Mockito.when(details1.getAccountName()).thenReturn("Joe");
        Mockito.when(details1.getAccountNumber()).thenReturn("12999910");
        Mockito.when(details1.getAssetName()).thenReturn("BHP Billiton");
        Mockito.when(details1.getAssetClass()).thenReturn("Australian Shares");
        Mockito.when(details1.getIsSellAll()).thenReturn(false);
        Mockito.when(details1.getAssetType()).thenReturn(AssetType.SHARE.toString());
        Mockito.when(details1.getIsFullModelRedemption()).thenReturn(Boolean.TRUE);

        List<RebalanceOrderDetailsDto> detailList = new ArrayList<>();
        detailList.add(details1);

        RebalanceOrdersDto rebalanceOrdersDto = Mockito.mock(RebalanceOrdersDto.class);
        Mockito.when(rebalanceOrdersDto.getOrderGroups()).thenReturn(Arrays.asList(rebalanceOrderGroup));

        Mockito.when(
                rebalanceOrdersService.findByDocIds(Mockito.any(IpsKey.class), Mockito.anyListOf(String.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(rebalanceOrdersDto);

        BigDecimal modelValue = rebalanceOrdersReport.getModelValue(mockParams, dataCollections);

        Assert.assertEquals(BigDecimal.TEN, modelValue);
    }

}
