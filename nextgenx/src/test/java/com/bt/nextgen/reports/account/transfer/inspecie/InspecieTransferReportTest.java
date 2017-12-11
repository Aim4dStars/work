package com.bt.nextgen.reports.account.transfer.inspecie;

import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferKey;
import com.bt.nextgen.api.inspecietransfer.v2.model.SettlementRecordDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.SponsorDetailsDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v2.service.InspecieTransferDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class InspecieTransferReportTest {

    private static final String ACCOUNT_ID = "account-id";
    private static final String TRANSFER_ID = "transferId";

    @InjectMocks
    private InspecieTransferReport inspecieTransferReport;

    @Mock
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Mock
    private InspecieTransferDtoService transferService;

    @Test(expected = IllegalArgumentException.class)
    public void testGetData_whenAccountIdBlank_thenIllegalArgumentException() {
        Map<String, Object> params = new HashMap<>();
        params.put(TRANSFER_ID, "transferId");

        inspecieTransferReport.getData(params, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetData_whenTransferIdBlank_thenIllegalArgumentException() {
        Map<String, Object> params = new HashMap<>();
        params.put(ACCOUNT_ID, "accountId");

        inspecieTransferReport.getData(params, null);
    }

    @Test
    public void testGetData_whenArgumentsValid_thenReportDataReturned() {
        Map<String, Object> params = new HashMap<>();
        params.put(ACCOUNT_ID, "accountId");
        params.put(TRANSFER_ID, "transferId");

        Mockito.when(assetService.loadAssets(Mockito.anyListOf(String.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<Map<String, Asset>>() {

                    @Override
                    public Map<String, Asset> answer(InvocationOnMock invocation) throws Throwable {
                        List<String> assetIds = (List<String>) invocation.getArguments()[0];
                        Assert.assertEquals("12345", assetIds.get(0));
                        Assert.assertEquals("23456", assetIds.get(1));

                        Asset asset = Mockito.mock(Asset.class);
                        Mockito.when(asset.getAssetName()).thenReturn("assetName");
                        Mockito.when(asset.getAssetCode()).thenReturn("assetCode");

                        Asset asset2 = Mockito.mock(Asset.class);
                        Mockito.when(asset2.getAssetName()).thenReturn("assetName2");

                        Map<String, Asset> assetMap = new HashMap<>();
                        assetMap.put("12345", asset);
                        assetMap.put("23456", asset2);

                        return assetMap;
                    }

                });

        Mockito.when(transferService.find(Mockito.any(InspecieTransferKey.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<InspecieTransferDto>() {

                    @Override
                    public InspecieTransferDto answer(InvocationOnMock invocation) throws Throwable {
                        InspecieTransferKey key = (InspecieTransferKey) invocation.getArguments()[0];
                        Assert.assertEquals("accountId", key.getAccountId());
                        Assert.assertEquals("transferId", key.getTransferId());

                        SponsorDetailsDtoImpl sponsorDto = Mockito.mock(SponsorDetailsDtoImpl.class);
                        Mockito.when(sponsorDto.getAccNumber()).thenReturn("accountNumber");
                        Mockito.when(sponsorDto.getHin()).thenReturn("hin");
                        Mockito.when(sponsorDto.getSrn()).thenReturn("srn");
                        Mockito.when(sponsorDto.getCustodian()).thenReturn("custodian");
                        Mockito.when(sponsorDto.getPidName()).thenReturn("sponsorName");

                        SettlementRecordDto settlementRecordDto = Mockito.mock(SettlementRecordDto.class);
                        Mockito.when(settlementRecordDto.getAssetId()).thenReturn("12345");
                        Mockito.when(settlementRecordDto.getQuantity()).thenReturn(BigDecimal.TEN);

                        SettlementRecordDto settlementRecordDto2 = Mockito.mock(SettlementRecordDto.class);
                        Mockito.when(settlementRecordDto2.getAssetId()).thenReturn("23456");
                        Mockito.when(settlementRecordDto2.getQuantity()).thenReturn(BigDecimal.ONE);

                        InspecieTransferDto transferDto = Mockito.mock(InspecieTransferDto.class);
                        Mockito.when(transferDto.getTransferType()).thenReturn("Listed Securities Broker Sponsored");
                        Mockito.when(transferDto.getSponsorDetails()).thenReturn(sponsorDto);
                        Mockito.when(transferDto.getSettlementRecords()).thenReturn(
                                Arrays.asList(settlementRecordDto, settlementRecordDto2));

                        return transferDto;
                    }
                });

        List<InspecieTransferReportData> reportDataList = (List<InspecieTransferReportData>) inspecieTransferReport.getData(
                params, null);

        InspecieTransferReportData reportData = reportDataList.get(0);
        Assert.assertEquals("transferId", reportData.getTransferId());
        Assert.assertTrue(reportData.getIsBrokerSponsoredShareTransfer());
        Assert.assertFalse(reportData.getIsIssuerSponsoredShareTransfer());
        Assert.assertFalse(reportData.getIsOtherShareTransfer());
        Assert.assertFalse(reportData.getIsManagedFundTransfer());
        Assert.assertEquals(2, reportData.getInspecieAssets().size());

        InspecieAssetReportData assetData = reportData.getInspecieAssets().get(0);
        Assert.assertEquals("accountNumber", assetData.getAccountNumber());
        Assert.assertEquals("custodian", assetData.getCustodian());
        Assert.assertEquals("hin", assetData.getHin());
        Assert.assertEquals("srn", assetData.getSrn());
        Assert.assertEquals("sponsorName", assetData.getSponsorName());
        Assert.assertEquals("10", assetData.getQuantity());
        Assert.assertEquals("<b>assetCode</b> &#183 assetName", assetData.getAssetTitle());
        Assert.assertEquals("accountNumber", assetData.getAccountNumberOrHin());

        assetData = reportData.getInspecieAssets().get(1);
        Assert.assertEquals("accountNumber", assetData.getAccountNumber());
        Assert.assertEquals("custodian", assetData.getCustodian());
        Assert.assertEquals("hin", assetData.getHin());
        Assert.assertEquals("srn", assetData.getSrn());
        Assert.assertEquals("sponsorName", assetData.getSponsorName());
        Assert.assertEquals("1", assetData.getQuantity());
        Assert.assertEquals("assetName2", assetData.getAssetTitle());
    }

    @Test
    public void testGetData_whenNoAccountNumberInSponsorDetails_thenReportDataReturned() {
        Map<String, Object> params = new HashMap<>();
        params.put(ACCOUNT_ID, "accountId");
        params.put(TRANSFER_ID, "transferId");

        Mockito.when(assetService.loadAssets(Mockito.anyListOf(String.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<Map<String, Asset>>() {

                    @Override
                    public Map<String, Asset> answer(InvocationOnMock invocation) throws Throwable {
                        List<String> assetIds = (List<String>) invocation.getArguments()[0];
                        Assert.assertEquals("12345", assetIds.get(0));

                        Asset asset = Mockito.mock(Asset.class);
                        Mockito.when(asset.getAssetName()).thenReturn("assetName");
                        Mockito.when(asset.getAssetCode()).thenReturn("assetCode");

                        Map<String, Asset> assetMap = new HashMap<>();
                        assetMap.put("12345", asset);

                        return assetMap;
                    }

                });

        Mockito.when(transferService.find(Mockito.any(InspecieTransferKey.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<InspecieTransferDto>() {

                    @Override
                    public InspecieTransferDto answer(InvocationOnMock invocation) throws Throwable {
                        InspecieTransferKey key = (InspecieTransferKey) invocation.getArguments()[0];
                        Assert.assertEquals("accountId", key.getAccountId());
                        Assert.assertEquals("transferId", key.getTransferId());

                        SponsorDetailsDtoImpl sponsorDto = Mockito.mock(SponsorDetailsDtoImpl.class);
                        Mockito.when(sponsorDto.getHin()).thenReturn("hin");

                        SettlementRecordDto settlementRecordDto = Mockito.mock(SettlementRecordDto.class);
                        Mockito.when(settlementRecordDto.getAssetId()).thenReturn("12345");
                        Mockito.when(settlementRecordDto.getQuantity()).thenReturn(BigDecimal.TEN);

                        InspecieTransferDto transferDto = Mockito.mock(InspecieTransferDto.class);
                        Mockito.when(transferDto.getTransferType()).thenReturn("Listed Securities Other Platform or Custodian");
                        Mockito.when(transferDto.getSponsorDetails()).thenReturn(sponsorDto);
                        Mockito.when(transferDto.getSettlementRecords()).thenReturn(Arrays.asList(settlementRecordDto));

                        return transferDto;
                    }
                });

        List<InspecieTransferReportData> reportDataList = (List<InspecieTransferReportData>) inspecieTransferReport.getData(
                params, null);

        InspecieTransferReportData reportData = reportDataList.get(0);
        Assert.assertEquals("transferId", reportData.getTransferId());
        Assert.assertFalse(reportData.getIsBrokerSponsoredShareTransfer());
        Assert.assertFalse(reportData.getIsIssuerSponsoredShareTransfer());
        Assert.assertTrue(reportData.getIsOtherShareTransfer());
        Assert.assertFalse(reportData.getIsManagedFundTransfer());
        Assert.assertEquals(1, reportData.getInspecieAssets().size());

        InspecieAssetReportData assetData = reportData.getInspecieAssets().get(0);
        Assert.assertNull(assetData.getAccountNumber());
        Assert.assertEquals("hin", assetData.getHin());
        Assert.assertEquals("10", assetData.getQuantity());
        Assert.assertEquals("<b>assetCode</b> &#183 assetName", assetData.getAssetTitle());
        Assert.assertEquals("hin", assetData.getAccountNumberOrHin());
    }

    @Test
    public void testGetStaticDataFields() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> collections = new HashMap<>();
        Assert.assertEquals("Transfer details", inspecieTransferReport.getReportType(params, collections));
    }
}
