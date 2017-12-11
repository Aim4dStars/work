package com.bt.nextgen.reports.account.transactions;

import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.util.ArrayList;
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

import com.bt.nextgen.api.portfolio.v3.model.cashmovements.CashMovementsDto;
import com.bt.nextgen.api.portfolio.v3.model.cashmovements.OutstandingCash;
import com.bt.nextgen.api.portfolio.v3.model.cashmovements.OutstandingIncomeDto;
import com.bt.nextgen.api.portfolio.v3.model.cashmovements.OutstandingMovementsDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.service.cashmovements.CashMovementsDtoService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.btfin.panorama.service.integration.asset.AssetType;

@RunWith(MockitoJUnitRunner.class)
public class CashMovementsReportTest {

    @InjectMocks
    private CashMovementsReport cashMovementsReport;

    @Mock
    private CashMovementsDtoService cashMovementsDtoService;

    @Mock
    private CmsService cmsService;

    private Map<String, Object> params;
    private Map<String, Object> dataCollections;

    @Before
    public void setup() {
        params = new HashMap<>();
        dataCollections = new HashMap<>();
        params.put("account-id", "114129E83B02BCA4D4B3245CEFB3778405F178638F9731DD");
        params.put("effective-date", "2016-01-01");

        OutstandingCash outstandingCash = mock(OutstandingCash.class);
        Mockito.when(outstandingCash.getAmount()).thenReturn(BigDecimal.valueOf(200));
        Mockito.when(outstandingCash.getAssetName()).thenReturn("BT CMA");
        Mockito.when(outstandingCash.getAssetType()).thenReturn(AssetType.CASH);
        Mockito.when(outstandingCash.getMarketPrice()).thenReturn(BigDecimal.valueOf(200));
        Mockito.when(outstandingCash.getSettlementDate()).thenReturn(new DateTime("2018-06-02"));
        Mockito.when(outstandingCash.getTransactionDate()).thenReturn(new DateTime("2018-06-02"));

        TermDepositPresentation tdPresentation = mock(TermDepositPresentation.class);
        Mockito.when(tdPresentation.getTerm()).thenReturn("6 months");
        Mockito.when(tdPresentation.getPaymentFrequency()).thenReturn("at maturity");
        Mockito.when(tdPresentation.getBrandClass()).thenReturn("bt");

        OutstandingCash outstandingCash1 = mock(OutstandingCash.class);
        Mockito.when(outstandingCash1.getAmount()).thenReturn(BigDecimal.valueOf(200));
        Mockito.when(outstandingCash1.getAssetCode()).thenReturn("BT001");
        Mockito.when(outstandingCash1.getAssetName()).thenReturn("BT Term deposit");
        Mockito.when(outstandingCash1.getAssetType()).thenReturn(AssetType.TERM_DEPOSIT);
        Mockito.when(outstandingCash1.getMarketPrice()).thenReturn(BigDecimal.valueOf(200));
        Mockito.when(outstandingCash1.getSettlementDate()).thenReturn(new DateTime("2018-06-02"));
        Mockito.when(outstandingCash1.getTransactionDate()).thenReturn(new DateTime("2018-06-02"));
        Mockito.when(outstandingCash1.getTermDepositDetails()).thenReturn(tdPresentation);

        OutstandingCash outstandingCash2 = mock(OutstandingCash.class);
        Mockito.when(outstandingCash2.getAmount()).thenReturn(BigDecimal.valueOf(200));
        Mockito.when(outstandingCash2.getAssetCode()).thenReturn("BHP");
        Mockito.when(outstandingCash2.getAssetName()).thenReturn("BHP Billiton");
        Mockito.when(outstandingCash2.getAssetType()).thenReturn(AssetType.SHARE);
        Mockito.when(outstandingCash2.getQuantity()).thenReturn(BigDecimal.valueOf(15));
        Mockito.when(outstandingCash2.getMarketPrice()).thenReturn(BigDecimal.valueOf(200));
        Mockito.when(outstandingCash2.getMarketPrice()).thenReturn(BigDecimal.valueOf(200));
        Mockito.when(outstandingCash2.getSettlementDate()).thenReturn(new DateTime("2018-06-02"));
        Mockito.when(outstandingCash2.getTransactionDate()).thenReturn(new DateTime("2018-06-02"));

        OutstandingCash outstandingCash3 = mock(OutstandingCash.class);
        Mockito.when(outstandingCash3.getAmount()).thenReturn(BigDecimal.valueOf(200));
        Mockito.when(outstandingCash3.getAssetCode()).thenReturn("BR001");
        Mockito.when(outstandingCash3.getAssetName()).thenReturn("BlackRock Managed fund");
        Mockito.when(outstandingCash3.getAssetType()).thenReturn(AssetType.MANAGED_FUND);
        Mockito.when(outstandingCash3.getQuantity()).thenReturn(BigDecimal.valueOf(15));
        Mockito.when(outstandingCash3.getMarketPrice()).thenReturn(BigDecimal.valueOf(200));
        Mockito.when(outstandingCash3.getMarketPrice()).thenReturn(BigDecimal.valueOf(200));
        Mockito.when(outstandingCash3.getSettlementDate()).thenReturn(new DateTime("2018-06-02"));
        Mockito.when(outstandingCash3.getTransactionDate()).thenReturn(new DateTime("2018-06-02"));

        List<OutstandingCash> outstandingCashList = new ArrayList<>();
        outstandingCashList.add(outstandingCash);
        outstandingCashList.add(outstandingCash1);
        outstandingCashList.add(outstandingCash2);
        outstandingCashList.add(outstandingCash3);

        OutstandingIncomeDto outstandingIncomeDto = mock(OutstandingIncomeDto.class);
        Mockito.when(outstandingIncomeDto.getAmount()).thenReturn(BigDecimal.valueOf(2000));
        Mockito.when(outstandingIncomeDto.getCategory()).thenReturn("Category1");
        Mockito.when(outstandingIncomeDto.getOutstanding()).thenReturn(outstandingCashList);


        OutstandingMovementsDto outstandingMovementsDto = mock(OutstandingMovementsDto.class);
        Mockito.when(outstandingMovementsDto.getAmount()).thenReturn(BigDecimal.valueOf(200));
        Mockito.when(outstandingMovementsDto.getCategory()).thenReturn("Unsettled buys");
        Mockito.when(outstandingMovementsDto.getOutstanding()).thenReturn(outstandingCashList);

        OutstandingMovementsDto outstandingMovementsDto1 = mock(OutstandingMovementsDto.class);
        Mockito.when(outstandingMovementsDto1.getAmount()).thenReturn(BigDecimal.valueOf(200));
        Mockito.when(outstandingMovementsDto1.getCategory()).thenReturn("Unsettled sells");
        Mockito.when(outstandingMovementsDto1.getOutstanding()).thenReturn(outstandingCashList);

        OutstandingMovementsDto outstandingMovementsDto2 = mock(OutstandingMovementsDto.class);
        Mockito.when(outstandingMovementsDto2.getAmount()).thenReturn(BigDecimal.valueOf(200));
        Mockito.when(outstandingMovementsDto2.getCategory()).thenReturn("Undefined category");
        Mockito.when(outstandingMovementsDto2.getOutstanding()).thenReturn(outstandingCashList);

        List<OutstandingMovementsDto> outstandingMovements = new ArrayList<OutstandingMovementsDto>();
        outstandingMovements.add(outstandingMovementsDto);
        outstandingMovements.add(outstandingMovementsDto1);
        outstandingMovements.add(outstandingMovementsDto2);

        CashMovementsDto cashMovementsDto = mock(CashMovementsDto.class);
        Mockito.when(cashMovementsDto.getAvailableCash()).thenReturn(BigDecimal.valueOf(2000));
        Mockito.when(cashMovementsDto.getReservedCash()).thenReturn(BigDecimal.valueOf(100));
        Mockito.when(cashMovementsDto.getTradeDateCash()).thenReturn(BigDecimal.valueOf(200));
        Mockito.when(cashMovementsDto.getTotalCashMovements()).thenReturn(BigDecimal.valueOf(400));
        Mockito.when(cashMovementsDto.getOutstandingTotal()).thenReturn(BigDecimal.valueOf(500));
        Mockito.when(cashMovementsDto.getMinCash()).thenReturn(BigDecimal.valueOf(150));
        Mockito.when(cashMovementsDto.getValueDateCash()).thenReturn(BigDecimal.valueOf(250));
        Mockito.when(cashMovementsDto.getOther()).thenReturn(BigDecimal.valueOf(50));
        Mockito.when(cashMovementsDto.getOutstandingCash()).thenReturn(outstandingMovements);
        Mockito.when(cashMovementsDto.getOutstandingIncome()).thenReturn(outstandingIncomeDto);

        Mockito.when(cashMovementsDtoService.find(Mockito.any(DatedValuationKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(cashMovementsDto);
        Mockito.when(cmsService.getContent(Mockito.anyString()))
                .thenReturn("content1");

    }

    @Test
    public void test_CashMovementsReport() {
        Collection<?> cashMovements = cashMovementsReport.getData(params, dataCollections);
        CashMovementsReportData cashMovement = (CashMovementsReportData) cashMovements.iterator().next();

        OutstandingCashData outstandingCashData = cashMovement.getOutstandingCashData();
        OutstandingIncomeData outstandingIncomeData = cashMovement.getOutstandingIncomeData();
        List<OutstandingCashMovementsData> outstandingCashMovements = outstandingCashData.getChildren();
        OutstandingCashMovementsData outstandingCashMovement = outstandingCashMovements.get(0);
        List<OutstandingCashItemData> outstandingCashItemDatas =  outstandingCashMovement.getChildren();
        OutstandingCashItemData outstandingCashItem1 = outstandingCashItemDatas.get(0);
        OutstandingCashItemData outstandingCashItem2 = outstandingCashItemDatas.get(1);
        OutstandingCashItemData outstandingCashItem3 = outstandingCashItemDatas.get(3);
        CashAccountBalanceData cashAccountBalanceData = cashMovement.getCashAccountBalanceData();
        List<OutstandingCashItemData> incomeData = outstandingIncomeData.getChildren();

        Assert.assertEquals("$400.00", cashMovement.getTotalCashMovements());


        Assert.assertEquals("$200.00", outstandingCashMovement.getAmount());
        Assert.assertEquals("Total Unsettled buys", outstandingCashMovement.getTotalDescription());
        Assert.assertEquals("content1", cashMovementsReport.getDisclaimer());
        Assert.assertEquals("content1", cashMovementsReport.getNoIncomeMessage());
        Assert.assertEquals("Cash Movements", cashMovementsReport.getReportType(params, dataCollections));

        Assert.assertEquals("$200.00", outstandingCashItem1.getAmount());
        Assert.assertEquals("BT CMA", outstandingCashItem1.getAssetName());
        Assert.assertEquals("$200.00", outstandingCashItem1.getMarketPrice());
        Assert.assertEquals("-", outstandingCashItem1.getQuantity());
        Assert.assertEquals("02 Jun 2018", outstandingCashItem1.getSettlementDate());
        Assert.assertEquals("02 Jun 2018", outstandingCashItem1.getTransactionDate());

        Assert.assertEquals("$2,000.00", outstandingIncomeData.getAmount());
        Assert.assertEquals(4, incomeData.size());

        Assert.assertEquals("$250.00", cashAccountBalanceData.getCurrentCashAccountBalance());
        Assert.assertEquals("$150.00", cashAccountBalanceData.getMinimumCashBalance());
        Assert.assertEquals("$100.00", cashAccountBalanceData.getReservedCashBalance());
        Assert.assertEquals("$2,000.00", cashAccountBalanceData.getTotalAvailableCash());
        Assert.assertEquals(false, cashAccountBalanceData.getIsAvailableCashNegative());


        Assert.assertEquals("$500.00", outstandingCashData.getAmount());
        Assert.assertEquals("$50.00", outstandingCashData.getOtherAmount());

        Assert.assertTrue(outstandingCashItem2.getAssetName().contains("BT Term deposit"));
        Assert.assertEquals("bt", outstandingCashItem2.getAssetBrandClass());
        Assert.assertEquals("Term deposit", outstandingCashItem2.getAssetType());
        Assert.assertEquals("6 months term, interest payment at maturity", outstandingCashItem2.getTermDepositDetail());

        Assert.assertEquals("15.0000", outstandingCashItem3.getQuantity());

    }

    @Test
    public void test_WhenNoUnsettledBuysandSells() {

        OutstandingCash outstandingCash = mock(OutstandingCash.class);
        Mockito.when(outstandingCash.getAmount()).thenReturn(BigDecimal.valueOf(200));
        Mockito.when(outstandingCash.getAssetName()).thenReturn("BT CMA");
        Mockito.when(outstandingCash.getAssetType()).thenReturn(AssetType.CASH);
        Mockito.when(outstandingCash.getMarketPrice()).thenReturn(BigDecimal.valueOf(200));
        Mockito.when(outstandingCash.getSettlementDate()).thenReturn(new DateTime("2018-06-02"));
        Mockito.when(outstandingCash.getTransactionDate()).thenReturn(new DateTime("2018-06-02"));

        List<OutstandingCash> outstandingCashList = new ArrayList<>();
        outstandingCashList.add(outstandingCash);

        OutstandingMovementsDto outstandingMovementsDto = mock(OutstandingMovementsDto.class);
        Mockito.when(outstandingMovementsDto.getAmount()).thenReturn(BigDecimal.valueOf(200));
        Mockito.when(outstandingMovementsDto.getCategory()).thenReturn("Undefined category");
        Mockito.when(outstandingMovementsDto.getOutstanding()).thenReturn(outstandingCashList);

        List<OutstandingMovementsDto> outstandingMovements = new ArrayList<OutstandingMovementsDto>();
        outstandingMovements.add(outstandingMovementsDto);

        CashMovementsDto cashMovementsDto = mock(CashMovementsDto.class);
        Mockito.when(cashMovementsDto.getAvailableCash()).thenReturn(BigDecimal.valueOf(-2000));
        Mockito.when(cashMovementsDto.getReservedCash()).thenReturn(BigDecimal.valueOf(100));
        Mockito.when(cashMovementsDto.getTradeDateCash()).thenReturn(BigDecimal.valueOf(200));
        Mockito.when(cashMovementsDto.getTotalCashMovements()).thenReturn(BigDecimal.valueOf(400));
        Mockito.when(cashMovementsDto.getOutstandingTotal()).thenReturn(BigDecimal.valueOf(500));
        Mockito.when(cashMovementsDto.getMinCash()).thenReturn(BigDecimal.valueOf(150));
        Mockito.when(cashMovementsDto.getValueDateCash()).thenReturn(BigDecimal.valueOf(250));
        Mockito.when(cashMovementsDto.getOther()).thenReturn(BigDecimal.valueOf(50));
        Mockito.when(cashMovementsDto.getOutstandingCash()).thenReturn(outstandingMovements);

        Mockito.when(cashMovementsDtoService.find(Mockito.any(DatedValuationKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(cashMovementsDto);

        Collection<?> cashMovements = cashMovementsReport.getData(params, dataCollections);
        CashMovementsReportData cashMovement = (CashMovementsReportData) cashMovements.iterator().next();
        CashAccountBalanceData cashAccountBalanceData = cashMovement.getCashAccountBalanceData();

        OutstandingCashData outstandingCashData = cashMovement.getOutstandingCashData();
        List<OutstandingCashMovementsData> outstandingMovementDatas = outstandingCashData.getChildren();
        String amount = outstandingMovementDatas.get(1).getAmount();
        String name = outstandingMovementDatas.get(1).getCategory();

        Mockito.when(cmsService.getDynamicContent(Mockito.anyString(), Mockito.any(String[].class))).thenReturn(
                "* The available cash is -$2,000.00");
        
        Assert.assertEquals(true, cashAccountBalanceData.getIsAvailableCashNegative());
        Assert.assertEquals("-$2,000.00", cashAccountBalanceData.getAvailableCash());
        Assert.assertEquals("$0.00", cashAccountBalanceData.getTotalAvailableCash());
        Assert.assertEquals("Unsettled buys", name);
        Assert.assertEquals("$0.00", amount);
        Assert.assertEquals("* The available cash is -$2,000.00",
                cashMovementsReport.getActualAvailableCashNote(params, dataCollections));

    }

    @Test
    public void test_CashMovementsReportSubtitle() {
        String reportSubtitle = cashMovementsReport.getReportSubTitle(params);
        Assert.assertEquals("As at 01 Jan 2016", reportSubtitle);
    }

    @Test
    public void test_AvailableCashVisible() {
        boolean isAvailableCashVisible = cashMovementsReport.isAvailableCashVisible(params);
        Assert.assertEquals(false, isAvailableCashVisible);

        // test available cash visibility for current data
        params = new HashMap<>();
        dataCollections = new HashMap<>();
        params.put("account-id", "114129E83B02BCA4D4B3245CEFB3778405F178638F9731DD");
        isAvailableCashVisible = cashMovementsReport.isAvailableCashVisible(params);
        Assert.assertEquals(true, isAvailableCashVisible);
    }

}
