package com.bt.nextgen.api.inspecietransfer.v3.util.vetting;

import com.bt.nextgen.api.inspecietransfer.v3.util.TaxParcelUploadUtil;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto.ErrorType;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class TaxParcelUploadFileTest {

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
    public void testSanitizer() {
        String input = "<img src=x onerror=\"&#0000106&#0000097&#0000118&#0000097&#0000115&#0000099&#0000114&#0000105&#0000112&#0000116&#0000058&#0000097&#0000108&#0000101&#0000114&#0000116&#0000040&#0000039&#0000088&#0000083&#0000083&#0000039&#0000041\">";
        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
        String safeHTML = policy.sanitize(input);
        System.out.println("[" + safeHTML + "]");

        input = "BHP";
        safeHTML = policy.sanitize(input);
        System.out.println("[" + safeHTML + "]");
    }
    
    @Test
    public void testIsRevenueAssetPresent() {

        TaxParcelUploadFile uploadFile = TaxParcelUploadFileBuilder.uploadFile().withHeader(null).withRows(null)
                .withIsMacroEnabled(false).withHasFormulas(false).withIsCbo(false).withTransferType(null).withSponsorName(null)
                .withUtil(util).withAssetService(assetService)
                .build();

        Asset asset = Mockito.mock(Asset.class);
        Mockito.when(asset.getAssetCode()).thenReturn("BHP");
        
        Asset revenueAsset = Mockito.mock(Asset.class);
        Mockito.when(revenueAsset.getAssetCode()).thenReturn("REVU");
        Mockito.when(revenueAsset.getRevenueAssetIndicator()).thenReturn("YEP");
        
        Map<String, Asset> assetMap = new HashMap<>();
        assetMap.put("BHP", asset);
        
        boolean present = uploadFile.isRevenueAssetPresent(assetMap);
        Assert.assertFalse(present);
        
        assetMap.put("REVU", revenueAsset);
        present = uploadFile.isRevenueAssetPresent(assetMap);
        Assert.assertTrue(present);
    }

    @Test
    public void testNoFormulas() {
        // Invalid
        TaxParcelUploadFile uploadFile = TaxParcelUploadFileBuilder.uploadFile().withHeader(null).withRows(null)
                .withIsMacroEnabled(false).withHasFormulas(true).withIsCbo(false).withTransferType(null).withSponsorName(null)
                .withUtil(util).withAssetService(assetService).build();

        uploadFile.validateNoFormulas(errors);
        expectSingleError();

        // Valid
        uploadFile = TaxParcelUploadFileBuilder.uploadFile().withHeader(null).withRows(null).withIsMacroEnabled(false)
                .withHasFormulas(false).withIsCbo(false).withTransferType(null).withSponsorName(null).withUtil(util)
                .withAssetService(assetService).build();

        uploadFile.validateNoFormulas(errors);
        expectNoError();
    }

    @Test
    public void testNoMacros() {
        // Invalid
        TaxParcelUploadFile uploadFile = TaxParcelUploadFileBuilder.uploadFile().withHeader(null).withRows(null)
                .withIsMacroEnabled(true).withHasFormulas(false).withIsCbo(false).withTransferType(null).withSponsorName(null)
                .withUtil(util).withAssetService(assetService).build();

        uploadFile.validateNoMacros(errors);
        expectSingleError();

        // Valid
        uploadFile = TaxParcelUploadFileBuilder.uploadFile().withHeader(null).withRows(null).withIsMacroEnabled(false)
                .withHasFormulas(false).withIsCbo(false).withTransferType(null).withSponsorName(null).withUtil(util)
                .withAssetService(assetService).build();

        uploadFile.validateNoMacros(errors);
        expectNoError();
    }

    @Test
    public void testOwnerConsistentHin() {

        // Invalid
        TaxParcelRow row = makeRow(null, null, "owner", null);
        TaxParcelRow row2 = makeRow(null, null, "owner2", null);
        TaxParcelRow row3 = makeRow(null, null, "owner3", null);
        TaxParcelRow row4 = makeRow(null, null, null, null);
        List<TaxParcelRow> rows = Arrays.asList(row, row2, row3, row4);
        TaxParcelUploadFile uploadFile = TaxParcelUploadFileBuilder.uploadFile().withHeader(null).withRows(rows)
                .withIsMacroEnabled(false).withHasFormulas(false).withIsCbo(false).withTransferType(null).withSponsorName(null)
                .withUtil(util).withAssetService(assetService).build();

        uploadFile.validateOwnerConsistentHin(errors);
        expectSingleError();

        // Valid
        rows = Arrays.asList(row, row4, row);
        uploadFile = TaxParcelUploadFileBuilder.uploadFile().withHeader(null).withRows(rows).withIsMacroEnabled(false)
                .withHasFormulas(false).withIsCbo(false).withTransferType(null).withSponsorName(null).withUtil(util)
                .withAssetService(assetService).build();

        uploadFile.validateOwnerConsistentHin(errors);
        expectNoError();
    }

    @Test
    public void testCostBaseConsistent() {

        TaxParcelDetailedRow row1 = makeDetailedRow(null, "10", null, null, null);
        TaxParcelDetailedRow row2 = makeDetailedRow(null, null, "20", null, null);
        TaxParcelDetailedRow row3 = makeDetailedRow(null, "10", "20", null, null);

        // Invalid
        List<TaxParcelRow> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);

        TaxParcelUploadFile uploadFile = makeFile(rows);
        uploadFile.validateCostBaseConsistent(errors, false);
        expectSingleError();

        // Invalid but no error - revenue asset is present
        uploadFile = makeFile(rows);
        uploadFile.validateCostBaseConsistent(errors, true);
        expectNoError();

        // Invalid
        rows.clear();
        rows.add(row2);
        rows.add(row1);
        rows.add(row3);

        uploadFile = makeFile(rows);
        uploadFile.validateCostBaseConsistent(errors, false);
        expectSingleError();

        // Valid
        rows.clear();
        rows.add(row1);
        rows.add(row1);

        uploadFile = makeFile(rows);
        uploadFile.validateCostBaseConsistent(errors, false);
        expectNoError();

        // Valid
        rows.clear();
        rows.add(row2);
        rows.add(row2);

        uploadFile = makeFile(rows);
        uploadFile.validateCostBaseConsistent(errors, false);
        expectNoError();

        // Invalid but no error - picked up by validation on row data
        rows.clear();
        rows.add(row3);
        rows.add(row1);
        rows.add(row2);

        uploadFile = makeFile(rows);
        uploadFile.validateCostBaseConsistent(errors, false);
        expectNoError();
    }

    @Test
    public void testWarnDifferentSrnForAsset() {

        TaxParcelRow row1 = makeRow("WOW", null, "owner", null);
        TaxParcelRow row2 = makeRow("WOW", null, "owner", null);
        TaxParcelRow row3 = makeRow("WOW", null, "different", null);
        TaxParcelRow row4 = makeRow("WOW", null, null, null);
        TaxParcelRow row5 = makeRow("BHP", null, "different", null);
        TaxParcelRow row6 = makeRow("BHP", null, null, null);

        // Invalid
        List<TaxParcelRow> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row5);
        rows.add(row5);

        TaxParcelUploadFile uploadFile = makeFile(rows);
        uploadFile.validateWarnDifferentSrnForAsset(errors);
        expectSingleError();

        // Valid
        rows.clear();
        rows.add(row1);
        rows.add(row2);
        rows.add(row4);
        rows.add(row5);
        rows.add(row6);

        uploadFile = makeFile(rows);
        uploadFile.validateWarnDifferentSrnForAsset(errors);
        expectNoError();
    }

    @Test
    public void testErrorDifferentAssetForSrn() {

        TaxParcelRow row1 = makeRow("WOW", null, "owner", null);
        TaxParcelRow row2 = makeRow("WOW", null, "owner", null);
        TaxParcelRow row3 = makeRow("WOW", null, "different", null);
        TaxParcelRow row4 = makeRow("WOW", null, null, null);
        TaxParcelRow row5 = makeRow("BHP", null, "different", null);
        TaxParcelRow row6 = makeRow("BHP", null, null, null);

        // Invalid
        List<TaxParcelRow> rows = new ArrayList<>();
        rows.add(row3);
        rows.add(row5);

        TaxParcelUploadFile uploadFile = makeFile(rows);
        uploadFile.validateErrorDifferentAssetForSrn(errors);
        expectSingleError();

        // Valid
        rows.clear();
        rows.add(row1);
        rows.add(row2);
        rows.add(row4);
        rows.add(row5);
        rows.add(row6);

        uploadFile = makeFile(rows);
        uploadFile.validateErrorDifferentAssetForSrn(errors);
        expectNoError();
    }

    @Test
    public void testAssetOwnerCombinationUnique() {

        TaxParcelRow row1 = makeRow("WOW", null, "owner", null);
        TaxParcelRow row2 = makeRow("WOW", null, "owner", null);
        TaxParcelRow row3 = makeRow("WOW", null, "different", null);
        TaxParcelRow row4 = makeRow("WOW", null, null, null);
        TaxParcelRow row5 = makeRow("BHP", null, "different", null);
        TaxParcelRow row6 = makeRow("BHP", null, null, null);

        // Invalid
        List<TaxParcelRow> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);

        TaxParcelUploadFile uploadFile = makeFile(rows);
        uploadFile.validateAssetOwnerCombinationUnique(errors);
        expectSingleError();

        // Valid
        rows.clear();
        rows.add(row1);
        rows.add(row3);
        rows.add(row4);
        rows.add(row5);
        rows.add(row6);

        uploadFile = makeFile(rows);
        uploadFile.validateAssetOwnerCombinationUnique(errors);
        expectNoError();
    }

    @Test
    public void testOriginalCostBaseValue() {

        TaxParcelDetailedRow row1 = makeDetailedRow(null, "0", null, null, null);
        TaxParcelDetailedRow row2 = makeDetailedRow(null, null, "20", null, null);
        TaxParcelDetailedRow row3 = makeDetailedRow(null, "0", "20", null, null);
        TaxParcelDetailedRow row4 = makeDetailedRow(null, "10", "20", "30", null);

        // Invalid
        List<TaxParcelRow> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);

        TaxParcelUploadFile uploadFile = makeFile(rows);
        uploadFile.validateOriginalCostBaseValue(errors);
        expectSingleError();

        // Invalid
        rows.clear();
        rows.add(row1);
        rows.add(row3);
        rows.add(row4);

        uploadFile = makeFile(rows);
        uploadFile.validateOriginalCostBaseValue(errors);
        expectSingleError();

        // Valid
        rows.clear();
        rows.add(row1);
        rows.add(row3);

        uploadFile = makeFile(rows);
        uploadFile.validateOriginalCostBaseValue(errors);
        expectNoError();

        // Valid
        rows.clear();
        rows.add(row2);
        rows.add(row4);

        uploadFile = makeFile(rows);
        uploadFile.validateOriginalCostBaseValue(errors);
        expectNoError();
    }

    private TaxParcelUploadFile makeFile(List<TaxParcelRow> rows) {
        return TaxParcelUploadFileBuilder.uploadFile().withHeader(null).withRows(rows).withIsMacroEnabled(false)
                .withHasFormulas(false).withIsCbo(false).withTransferType(null).withSponsorName(null).withUtil(util)
                .withAssetService(assetService).build();
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
