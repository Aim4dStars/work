package com.bt.nextgen.api.inspecietransfer.v3.util.vetting;

import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.SettlementRecordDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.SponsorDetailsDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v3.util.TaxParcelUploadUtil;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto.ErrorType;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TaxParcelIndependentUploadFileTest {

    @Mock
    private TaxParcelUploadUtil util;

    @Mock
    private AssetIntegrationService assetService;

    private List<DomainApiErrorDto> errors;

    @Before
    public void setup() {
        DomainApiErrorDto error = new DomainApiErrorDto(null, null, "Error added", ErrorType.ERROR);
        DomainApiErrorDto warning = new DomainApiErrorDto(null, null, "Warning added", ErrorType.WARNING);

        Mockito.when(util.getError(Mockito.anyString())).thenReturn(error);
        Mockito.when(util.getError(Mockito.anyString(), Mockito.any(String[].class))).thenReturn(error);
        Mockito.when(util.getWarning(Mockito.anyString())).thenReturn(warning);
        Mockito.when(util.getWarning(Mockito.anyString(), Mockito.any(String[].class))).thenReturn(warning);

        errors = new ArrayList<>();
    }

    @Test
    public void testUploadedAssetsExistInOrder() {

        SettlementRecordDto settlementRecord1 = Mockito.mock(SettlementRecordDto.class);
        Mockito.when(settlementRecord1.getAssetCode()).thenReturn("WOW");

        SettlementRecordDto settlementRecord2 = Mockito.mock(SettlementRecordDto.class);
        Mockito.when(settlementRecord2.getAssetCode()).thenReturn("BHP");

        InspecieTransferDto transferDto = Mockito.mock(InspecieTransferDto.class);
        Mockito.when(transferDto.getSettlementRecords()).thenReturn(Arrays.asList(settlementRecord1, settlementRecord2));

        TaxParcelRow row1 = makeRow("WOW", null, null, null);
        TaxParcelRow row2 = makeRow("BHP", null, null, null);
        TaxParcelRow row3 = makeRow("TLS", null, null, null);

        // Invalid
        List<TaxParcelRow> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);

        TaxParcelIndependentUploadFile uploadFile = makeFile(rows);
        uploadFile.validateUploadedAssetsExistInOrder(transferDto, errors);
        expectSingleError();

        // Valid
        rows.clear();
        rows.add(row1);
        rows.add(row2);

        uploadFile = makeFile(rows);
        uploadFile.validateUploadedAssetsExistInOrder(transferDto, errors);
        expectNoError();
    }

    @Test
    public void testOrderAssetsExistInUpload() {

        SettlementRecordDto settlementRecord1 = Mockito.mock(SettlementRecordDto.class);
        Mockito.when(settlementRecord1.getAssetCode()).thenReturn("WOW");

        SettlementRecordDto settlementRecord2 = Mockito.mock(SettlementRecordDto.class);
        Mockito.when(settlementRecord2.getAssetCode()).thenReturn("BHP");

        SettlementRecordDto settlementRecord3 = Mockito.mock(SettlementRecordDto.class);
        Mockito.when(settlementRecord3.getAssetCode()).thenReturn("TLS");

        InspecieTransferDto transferDto = Mockito.mock(InspecieTransferDto.class);
        Mockito.when(transferDto.getSettlementRecords()).thenReturn(
                Arrays.asList(settlementRecord1, settlementRecord2, settlementRecord3));

        TaxParcelRow row1 = makeRow("WOW", null, null, null);
        TaxParcelRow row2 = makeRow("BHP", null, null, null);
        TaxParcelRow row3 = makeRow("TLS", null, null, null);

        // Invalid
        List<TaxParcelRow> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);

        TaxParcelIndependentUploadFile uploadFile = makeFile(rows);
        uploadFile.validateOrderAssetsExistInUpload(transferDto, errors);
        expectSingleError();

        // Valid
        rows.clear();
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);

        uploadFile = makeFile(rows);
        uploadFile.validateOrderAssetsExistInUpload(transferDto, errors);
        expectNoError();
    }

    @Test
    public void testQuantitiesAgainstOrder() {

        SettlementRecordDto settlementRecord1 = Mockito.mock(SettlementRecordDto.class);
        Mockito.when(settlementRecord1.getAssetCode()).thenReturn("WOW");
        Mockito.when(settlementRecord1.getQuantity()).thenReturn(BigDecimal.TEN);

        InspecieTransferDto transferDto = Mockito.mock(InspecieTransferDto.class);
        Mockito.when(transferDto.getSettlementRecords()).thenReturn(Arrays.asList(settlementRecord1));

        TaxParcelRow row1 = makeRow("WOW", "1", null, null);
        TaxParcelRow row2 = makeRow("WOW", "2", null, null);
        TaxParcelRow row3 = makeRow("WOW", "7", null, null);

        // Invalid
        List<TaxParcelRow> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);

        TaxParcelIndependentUploadFile uploadFile = makeFile(rows);
        uploadFile.validateQuantitiesAgainstOrder(transferDto, errors);
        expectSingleError();

        // Valid
        rows.clear();
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);

        uploadFile = makeFile(rows);
        uploadFile.validateQuantitiesAgainstOrder(transferDto, errors);
        expectNoError();
    }

    @Test
    public void testOriginalCostBaseValue() {

        TaxParcelDetailedRow row1 = makeDetailedRow(null, "0", null, null, null);
        TaxParcelDetailedRow row2 = makeDetailedRow(null, "10", null, null, null);
        TaxParcelDetailedRow row3 = makeDetailedRow(null, "20", null, null, null);

        // Invalid
        List<TaxParcelRow> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);

        TaxParcelIndependentUploadFile uploadFile = makeFile(rows);
        uploadFile.validateOriginalCostBaseValue(errors);
        expectSingleError();

        // Valid
        rows.clear();
        rows.add(row2);
        rows.add(row3);

        uploadFile = makeFile(rows);
        uploadFile.validateOriginalCostBaseValue(errors);
        expectNoError();
    }

    @Test
    public void testGetAvailableOwner() {

        TaxParcelRow row1 = makeRow(null, null, null, null);
        TaxParcelIndependentUploadFile uploadFile = makeFile(Arrays.asList(row1));

        SponsorDetailsDtoImpl sponsorDetails = Mockito.mock(SponsorDetailsDtoImpl.class);
        Mockito.when(sponsorDetails.getHin()).thenReturn("HIN");
        Mockito.when(sponsorDetails.getSrn()).thenReturn(null);
        Mockito.when(sponsorDetails.getAccNumber()).thenReturn(null);

        InspecieTransferDto transferDto = Mockito.mock(InspecieTransferDto.class);
        Mockito.when(transferDto.getSponsorDetails()).thenReturn(sponsorDetails);

        Assert.assertEquals("HIN", uploadFile.getAvailableOwner(transferDto));

        Mockito.when(sponsorDetails.getHin()).thenReturn(null);
        Mockito.when(sponsorDetails.getSrn()).thenReturn("SRN");
        Mockito.when(sponsorDetails.getAccNumber()).thenReturn(null);

        Assert.assertEquals("SRN", uploadFile.getAvailableOwner(transferDto));

        Mockito.when(sponsorDetails.getHin()).thenReturn(null);
        Mockito.when(sponsorDetails.getSrn()).thenReturn(null);
        Mockito.when(sponsorDetails.getAccNumber()).thenReturn("AccountNumber");

        Assert.assertEquals("AccountNumber", uploadFile.getAvailableOwner(transferDto));

        Mockito.when(sponsorDetails.getHin()).thenReturn(null);
        Mockito.when(sponsorDetails.getSrn()).thenReturn(null);
        Mockito.when(sponsorDetails.getAccNumber()).thenReturn(null);

        Assert.assertNull(uploadFile.getAvailableOwner(transferDto));
    }

    @Test
    public void testUploadedOwnerMatchesOrder() {

        SponsorDetailsDtoImpl sponsorDetails = Mockito.mock(SponsorDetailsDtoImpl.class);
        Mockito.when(sponsorDetails.getHin()).thenReturn("HIN");

        InspecieTransferDto transferDto = Mockito.mock(InspecieTransferDto.class);
        Mockito.when(transferDto.getSponsorDetails()).thenReturn(sponsorDetails);

        TaxParcelRow row1 = makeRow(null, null, "Different HIN", null);
        TaxParcelRow row2 = makeRow(null, null, "Another HIN", null);
        TaxParcelRow row3 = makeRow(null, null, "HIN", null);

        // Invalid
        List<TaxParcelRow> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);

        TaxParcelIndependentUploadFile uploadFile = makeFile(rows);
        uploadFile.validateUploadedOwnerMatchesOrder(transferDto, errors);
        expectSingleError();

        // Valid
        rows = new ArrayList<>();
        rows.add(row3);

        uploadFile = makeFile(rows);
        uploadFile.validateUploadedOwnerMatchesOrder(transferDto, errors);
        expectNoError();
    }

    @Test
    public void testUploadedCustodianMatchesOrder() {

        SponsorDetailsDtoImpl sponsorDetails = Mockito.mock(SponsorDetailsDtoImpl.class);
        Mockito.when(sponsorDetails.getCustodian()).thenReturn("Custodian");

        InspecieTransferDto transferDto = Mockito.mock(InspecieTransferDto.class);
        Mockito.when(transferDto.getSponsorDetails()).thenReturn(sponsorDetails);

        TaxParcelRow row1 = makeRow(null, null, null, "Platform name");
        TaxParcelRow row2 = makeRow(null, null, null, "Custodian");
        TaxParcelRow row3 = makeRow(null, null, null, "Platypus dame");

        // Invalid
        List<TaxParcelRow> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);

        TaxParcelIndependentUploadFile uploadFile = makeFile(rows);
        uploadFile.validateUploadedCustodianMatchesOrder(transferDto, errors);
        expectSingleError();

        // Valid
        rows = new ArrayList<>();
        rows.add(row2);

        uploadFile = makeFile(rows);
        uploadFile.validateUploadedCustodianMatchesOrder(transferDto, errors);
        expectNoError();
    }

    private TaxParcelIndependentUploadFile makeFile(List<TaxParcelRow> rows) {
        return TaxParcelUploadFileBuilder.uploadFile().withHeader(null).withRows(rows).withIsMacroEnabled(false)
                .withHasFormulas(false).withIsCbo(false).withTransferType(null).withSponsorName(null).withUtil(util)
                .withAssetService(assetService).buildIndependent();
    }

    private TaxParcelRow makeRow(String assetCode, String quantity, String owner, String custodian) {
        List<String> rowData = Arrays.asList(assetCode, quantity, owner, custodian);
        TaxParcelRow row = new TaxParcelRow(rowData, 1, util);
        return row;
    }

    private TaxParcelDetailedRow makeDetailedRow(String date, String ocb, String cb, String rcb, String icb) {
        List<String> rowData = Arrays.asList("assetCode", null, null, null, date, null, ocb, null, cb, rcb, icb);
        TaxParcelDetailedRow row = new TaxParcelDetailedRow(rowData, 1, util, new DateTime("2016-01-01"));
        return row;
    }

    private void expectSingleError() {
        Assert.assertEquals(1, errors.size());
        errors.clear();
    }

    private void expectNoError() {
        Assert.assertEquals(0, errors.size());
        errors.clear();
    }
}
