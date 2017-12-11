package com.bt.nextgen.reports.account.transfer.intraaccount;

import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferKey;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.reports.account.transfer.IntraAccountTransferReport;
import com.bt.nextgen.reports.account.transfer.TransferAssetReportData;
import com.bt.nextgen.reports.account.transfer.TransferCategoryReportData;
import com.bt.nextgen.reports.account.transfer.TransferGroupReportData;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.order.OrderType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IntraAccountTransferReportTest {

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private CmsService contentService;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private IntraAccountTransferReport report;

    private InspecieTransferDtoImpl intraTransferDto;

    @Before
    public void setup() {
        InspecieTransferKey xferKey = mock(InspecieTransferKey.class);
        when(xferKey.getAccountId()).thenReturn("accountId");
        when(xferKey.getTransferId()).thenReturn("transferId");

        intraTransferDto = mock(InspecieTransferDtoImpl.class);
        when(intraTransferDto.getKey()).thenReturn(xferKey);
        when(intraTransferDto.getIsCBO()).thenReturn(Boolean.TRUE);
        when(intraTransferDto.getOrderType()).thenReturn(OrderType.INTRA_ACCOUNT_TRANSFER.getDisplayName());

    }

    @Test
    public void test_intraXfer_withVettingErrors_warningsAreIncludedInData() {
        String intra_xfer = "{\"targetContainerId\":\"6AFD32BF3A496A191277A606E4D52F297FB34B1E5AF22211\",\"sourceContainerId\":\"327FDA1E8A426D9D289998A9CCB06D28CCB4EF750E32C2D9\",\"isFullClose\":false,\"transferAssets\":[{\"asset\":{\"assetId\":\"111231\",\"assetCode\":\"ETL0032AU\",\"assetName\":\"Aberdeen Emerging Opportunities Fund\",\"assetType\":\"Managed funds\",\"type\":\"ManagedFundAsset\"},\"amount\":\"$1.00\",\"quantity\":1,\"isCashTransfer\":false,\"vettWarnings\":[\"ETL0032AU is not in the model portfolio and will be sold during a rebalance.\",\"The distribution preference will be set to cash upon completion of the transfer.\"]},{\"asset\":{\"assetName\":\"BT Cash\",\"type\":\"Asset\"},\"quantity\":1,\"isCashTransfer\":true,\"vettWarnings\":[]}],\"transferType\":\"Internal transfer\",\"sourceAccountKey\":{\"accountId\":\"2070A14E9E08FF9DA7C5E7C037E247B35AB7F5F0F01768A9\"},\"targetAccountKey\":{\"accountId\":\"2070A14E9E08FF9DA7C5E7C037E247B35AB7F5F0F01768A9\"},\"action\":\"validate\",\"key\":{\"transferId\":\"5864799\"},\"orderType\":\"Intra account transfer\"}";
        List<Object> objs = report.getData(setupIntraXferResponseMapper(intra_xfer), null);
        assertEquals(objs.size(), 1);

        TransferGroupReportData rptData = (TransferGroupReportData) objs.get(0);
        assertEquals("$1234", rptData.getPortfolioValue());
        assertEquals("None", rptData.getPreferenceFlag());
        assertEquals("Transfer", rptData.getIncomePreference());
        assertEquals("destCode", rptData.getDestAssetCode());
        assertEquals("destName", rptData.getDestContainerName());
        assertEquals("sourceCode", rptData.getSourceAssetCode());
        assertEquals("sourceName", rptData.getSourceContainerName());
        assertEquals("cashAssetName", rptData.getCashAssetName());
        assertTrue(rptData.getHasWarnings());

        List<String> assetWarnings = rptData.getChildren().get(0).getChildren().get(0).getWarnings();
        assertEquals(2, assetWarnings.size());
        assertEquals("ETL0032AU is not in the model portfolio and will be sold during a rebalance.", assetWarnings.get(0));
    }

    @Test
    public void test_intraXfer_pension_tmpToDirect_cashOnly() {
        String intra_xfer = "{\"targetContainerId\":\"D4BCCF829FFE616938F0B5AC11CBBC82004C7931B57AEFA7\",\"sourceContainerId\":\"5E77D2D9422C9F80E310301004B6F283F509E866B00FD137\",\"isFullClose\":false,\"transferAssets\":[{\"asset\":{\"assetName\":\"Cash account\",\"type\":\"Asset\"},\"quantity\":1,\"isCashTransfer\":true,\"vettWarnings\":[]}],\"transferType\":\"Internal transfer\",\"sourceAccountKey\":{\"accountId\":\"8350A325F36277C69CDFCBF8FACBC298DD4C9E2F73BA6219\"},\"targetAccountKey\":{\"accountId\":\"8350A325F36277C69CDFCBF8FACBC298DD4C9E2F73BA6219\"},\"action\":\"validate\",\"key\":{\"transferId\":\"5864650\"},\"orderType\":\"Intra account transfer\"}";
        List<Object> objs = report.getData(setupIntraXferResponseMapper(intra_xfer), null);
        assertEquals(objs.size(), 1);

        TransferGroupReportData rptData = (TransferGroupReportData) objs.get(0);
        List<TransferCategoryReportData> catDataList = rptData.getChildren();
        assertEquals(1, catDataList.size());

        TransferAssetReportData data = catDataList.get(0).getChildren().get(1);
        assertEquals("cashAssetName transfer", data.getAssetName());
        assertEquals("5864650", rptData.getOrderId());

        // Verify summary amount
        assertEquals("$1.00", rptData.getCashOnlyTransferAmount());
        assertEquals("$0.00", rptData.getNonCashTransferAmount());
        assertEquals("$1.00", rptData.getTotalTransferAmount());
    }

    @Test
    public void testGenericGetterMethods() {
        assertEquals("", report.getSummaryDescription(null, null));
        assertEquals("", report.getSummaryValue(null, null));
        assertEquals("Intra account transfer receipt", report.getReportType(null, null));
        assertEquals("intraAccountTransferReport", report.getReportFileName(null));
        assertEquals("Intra account transfer has been submitted", report.getReportSubTitle());
    }

    private Map<String, Object> setupIntraXferResponseMapper(String response) {
        JsonObjectMapper objectMapper = new JsonObjectMapper();
        InspecieTransferDtoImpl intraDto;
        try {
            intraDto = objectMapper.readValue(response, InspecieTransferDtoImpl.class);
            Mockito.when(mapper.readValue(Mockito.eq(response), Mockito.any(Class.class))).thenReturn(intraDto);
        } catch (IOException e) {
            e.printStackTrace();
        }

        InspecieTransferKey xferKey = mock(InspecieTransferKey.class);
        when(xferKey.getAccountId()).thenReturn("accountId");
        when(xferKey.getTransferId()).thenReturn("transferId");

        Map<String, Object> params = new HashMap<>();
        params.put("transferData", response);
        params.put("holdingvalue", "$1234");
        params.put("preference", "None");
        params.put("incomePreference", "Transfer");
        params.put("destAssetCode", "destCode");
        params.put("destAssetName", "destName");
        params.put("sourceAssetCode", "sourceCode");
        params.put("sourceAssetName", "sourceName");
        params.put("cashAssetName", "cashAssetName");

        return params;
    }
}
