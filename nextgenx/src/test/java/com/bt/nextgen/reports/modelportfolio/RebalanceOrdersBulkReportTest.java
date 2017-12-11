package com.bt.nextgen.reports.modelportfolio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.RebalanceOrderDetailsDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.RebalanceOrderGroupDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.RebalanceOrdersDto;
import com.bt.nextgen.api.modelportfolio.v2.service.rebalance.RebalanceOrdersDtoService;
import com.bt.nextgen.api.modelportfolio.v2.util.rebalance.RebalanceOrdersSortingHelper;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.ips.IpsKey;

@RunWith(MockitoJUnitRunner.class)
public class RebalanceOrdersBulkReportTest {

    @InjectMocks
    private RebalanceOrdersBulkReport rebalanceOrdersBulkReport;

    @Mock
    private RebalanceOrdersDtoService rebalanceOrdersService;

    @Mock
    private RebalanceOrdersSortingHelper sortingHelper;

    @Mock
    private Map<String, Object> mockParams;

    private List<RebalanceOrderGroupDto> data;
    private List<RebalanceOrderGroupDto> fullRedemptionData;

    private static final String PARAM_IPS_ID = "ips";

    @Before
    public void setup() {

        RebalanceOrderDetailsDto details = Mockito.mock(RebalanceOrderDetailsDto.class);
        Mockito.when(details.getAccountName()).thenReturn("Joe");
        Mockito.when(details.getAccountNumber()).thenReturn("12999909");
        Mockito.when(details.getAssetName()).thenReturn("BHP Billiton");
        Mockito.when(details.getAssetClass()).thenReturn("Australian Shares");
        Mockito.when(details.getIsSellAll()).thenReturn(false);
        Mockito.when(details.getAssetType()).thenReturn(AssetType.SHARE.toString());

        RebalanceOrderDetailsDto details1 = Mockito.mock(RebalanceOrderDetailsDto.class);
        Mockito.when(details1.getAccountName()).thenReturn("Joe");
        Mockito.when(details1.getAccountNumber()).thenReturn("12999910");
        Mockito.when(details1.getAssetName()).thenReturn("BHP Billiton");
        Mockito.when(details1.getAssetClass()).thenReturn("Australian Shares");
        Mockito.when(details1.getIsSellAll()).thenReturn(true);
        Mockito.when(details1.getAssetType()).thenReturn(AssetType.SHARE.toString());

        RebalanceOrderDetailsDto details2 = Mockito.mock(RebalanceOrderDetailsDto.class);
        Mockito.when(details2.getAccountName()).thenReturn("Joe");
        Mockito.when(details2.getAccountNumber()).thenReturn("12999910");
        Mockito.when(details2.getAssetName()).thenReturn("TMP Cash");
        Mockito.when(details2.getAssetClass()).thenReturn("Cash");
        Mockito.when(details2.getIsSellAll()).thenReturn(false);
        Mockito.when(details2.getAssetType()).thenReturn(AssetType.CASH.toString());

        RebalanceOrderDetailsDto details4 = Mockito.mock(RebalanceOrderDetailsDto.class);
        Mockito.when(details4.getAccountName()).thenReturn("Joe");
        Mockito.when(details4.getAccountNumber()).thenReturn("12999909");
        Mockito.when(details4.getAssetName()).thenReturn("Rio Tinto");
        Mockito.when(details4.getAssetClass()).thenReturn("Australian Shares");
        Mockito.when(details4.getIsSellAll()).thenReturn(false);
        Mockito.when(details4.getAssetType()).thenReturn(AssetType.SHARE.toString());

        List<RebalanceOrderDetailsDto> detailList = new ArrayList<>();
        detailList.add(details1);
        detailList.add(details2);

        List<RebalanceOrderDetailsDto> detailList1 = new ArrayList<>();
        detailList1.add(details1);
        detailList1.add(details4);

        RebalanceOrderGroupDto rebalanceOrderGroup1 = Mockito.mock(RebalanceOrderGroupDto.class);
        Mockito.when(rebalanceOrderGroup1.getAdviserNumber()).thenReturn("adviserNumber1");
        Mockito.when(rebalanceOrderGroup1.getModelSymbol()).thenReturn("modelSymbol");
        Mockito.when(rebalanceOrderGroup1.getRebalanceDate()).thenReturn(new DateTime("2016-02-03"));
        Mockito.when(rebalanceOrderGroup1.getOrderDetails()).thenReturn(Arrays.asList(details));

        RebalanceOrderGroupDto rebalanceOrderGroup2 = Mockito.mock(RebalanceOrderGroupDto.class);
        Mockito.when(rebalanceOrderGroup2.getAdviserNumber()).thenReturn("adviserNumber2");
        Mockito.when(rebalanceOrderGroup2.getModelSymbol()).thenReturn("modelSymbol");
        Mockito.when(rebalanceOrderGroup2.getRebalanceDate()).thenReturn(new DateTime("2016-02-03"));
        Mockito.when(rebalanceOrderGroup2.getOrderDetails()).thenReturn(Arrays.asList(details));

        RebalanceOrderGroupDto rebalanceOrderGroup3 = Mockito.mock(RebalanceOrderGroupDto.class);
        Mockito.when(rebalanceOrderGroup3.getAdviserNumber()).thenReturn("adviserNumber3");
        Mockito.when(rebalanceOrderGroup3.getModelSymbol()).thenReturn("modelSymbol");
        Mockito.when(rebalanceOrderGroup3.getRebalanceDate()).thenReturn(new DateTime("2016-02-03"));
        Mockito.when(rebalanceOrderGroup3.getOrderDetails()).thenReturn(detailList);

        RebalanceOrderGroupDto rebalanceOrderGroup4 = Mockito.mock(RebalanceOrderGroupDto.class);
        Mockito.when(rebalanceOrderGroup4.getAdviserNumber()).thenReturn("adviserNumber3");
        Mockito.when(rebalanceOrderGroup4.getModelSymbol()).thenReturn("modelSymbol");
        Mockito.when(rebalanceOrderGroup4.getRebalanceDate()).thenReturn(new DateTime("2016-02-03"));
        Mockito.when(rebalanceOrderGroup4.getOrderDetails()).thenReturn(detailList1);

        data = Arrays.asList(rebalanceOrderGroup1, rebalanceOrderGroup2, rebalanceOrderGroup3);
        fullRedemptionData = Arrays.asList(rebalanceOrderGroup4);
    }

    @Test
    public void test_FilterNonCashRebalanceOrders() {
        RebalanceOrdersDto rebalanceOrdersDto = Mockito.mock(RebalanceOrdersDto.class);
        Mockito.when(rebalanceOrdersDto.getOrderGroups()).thenReturn(fullRedemptionData);

        Mockito.when(rebalanceOrdersService.find(Mockito.any(IpsKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                rebalanceOrdersDto);

        Mockito.when(mockParams.get(PARAM_IPS_ID)).thenReturn("ipsId");
        Map<String, Object> dataCollections = new HashMap<String, Object>();

        Collection<?> data = rebalanceOrdersBulkReport.getData(mockParams, dataCollections);

        Assert.assertNotNull(data);
        Assert.assertEquals(1, data.size());

        RebalanceOrderGroupDto rebalanceOrderGroup = (RebalanceOrderGroupDto) data.iterator().next();
        Assert.assertEquals(2, rebalanceOrderGroup.getOrderDetails().size());
    }

    @Test
    public void test_getData_getRebalanceOrderGroups() {

        RebalanceOrdersDto rebalanceOrdersDto = Mockito.mock(RebalanceOrdersDto.class);
        Mockito.when(rebalanceOrdersDto.getOrderGroups()).thenReturn(data);

        Mockito.when(rebalanceOrdersService.find(Mockito.any(IpsKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                rebalanceOrdersDto);

        Mockito.when(mockParams.get(PARAM_IPS_ID)).thenReturn("ipsId");
        Map<String, Object> dataCollections = new HashMap<String, Object>();

        Collection<?> data = rebalanceOrdersBulkReport.getData(mockParams, dataCollections);

        Assert.assertNotNull(data);
        Assert.assertEquals(3, data.size());
    }

    @Test
    public void test_getReportPageNames() {

        Collection<String> pageNames = rebalanceOrdersBulkReport.getReportPageNames(data);

        Assert.assertNotNull(pageNames);
        Assert.assertEquals(3, pageNames.size());

        String[] pageNameList = pageNames.toArray(new String[3]);

        Assert.assertEquals("REBAL_modelSymbol_adviserNumber1_03022016", pageNameList[0]);
        Assert.assertEquals("REBAL_modelSymbol_adviserNumber2_03022016", pageNameList[1]);
        Assert.assertEquals("REBAL_modelSymbol_adviserNumber3_03022016", pageNameList[2]);
    }

    @Test
    public void test_getReportFileName() {

        String fileName = rebalanceOrdersBulkReport.getReportFileName(data);

        Assert.assertEquals("REBAL_modelSymbol_03022016", fileName);
    }


}
