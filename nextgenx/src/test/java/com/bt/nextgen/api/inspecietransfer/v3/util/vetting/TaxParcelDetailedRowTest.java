package com.bt.nextgen.api.inspecietransfer.v3.util.vetting;

import com.bt.nextgen.api.inspecietransfer.v3.util.TaxParcelUploadUtil;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto.ErrorType;
import com.bt.nextgen.service.integration.asset.Asset;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TaxParcelDetailedRowTest {

    @Mock
    private TaxParcelUploadUtil util;

    private List<DomainApiErrorDto> errors;
    private DateTime bankDate;

    @Before
    public void setup() {
        DomainApiErrorDto error = new DomainApiErrorDto(null, null, "Error added", ErrorType.ERROR);
        DomainApiErrorDto warning = new DomainApiErrorDto(null, null, "Warning added", ErrorType.WARNING);

        Mockito.when(util.getError(Mockito.anyString())).thenReturn(error);
        Mockito.when(util.getError(Mockito.anyString(), Mockito.any(String[].class))).thenReturn(error);
        Mockito.when(util.getWarning(Mockito.anyString())).thenReturn(warning);
        Mockito.when(util.getWarning(Mockito.anyString(), Mockito.any(String[].class))).thenReturn(warning);

        errors = new ArrayList<>();
        bankDate = new DateTime("2016-10-20");
    }

    @Test
    public void testFieldsNotPopulatedIfInvalidRow() {
        List<String> rowData = Arrays.asList("column1", null, "column3");
        TaxParcelDetailedRow row = new TaxParcelDetailedRow(rowData, 1, util, bankDate);

        Assert.assertNull(row.getAssetCode());
        Assert.assertNull(row.getQuantity());
        Assert.assertNull(row.getOwner());
        Assert.assertNull(row.getCustodian());
        Assert.assertNull(row.getAcquisitionDate());
        Assert.assertNull(row.getOriginalCostBase());
        Assert.assertNull(row.getCostBase());
        Assert.assertNull(row.getReducedCostBase());
        Assert.assertNull(row.getIndexedCostBase());
    }

    @Test
    public void testFieldsPopulatedIfValidRow() {
        List<String> rowData = Arrays.asList("assetCode", "quantity", "owner", "custodian", "2016-10-20", null, "10", null, "20",
                "30", "40");
        TaxParcelDetailedRow row = new TaxParcelDetailedRow(rowData, 1, util, bankDate);

        Assert.assertEquals("ASSETCODE", row.getAssetCode());
        Assert.assertEquals("quantity", row.getQuantity());
        Assert.assertEquals("OWNER", row.getOwner());
        Assert.assertEquals("custodian", row.getCustodian());
        Assert.assertEquals(new DateTime("2016-10-20"), row.getAcquisitionDate());
        Assert.assertEquals("10", row.getOriginalCostBase());
        Assert.assertEquals("20", row.getCostBase());
        Assert.assertEquals("30", row.getReducedCostBase());
        Assert.assertEquals("40", row.getIndexedCostBase());
    }
    
    @Test
    public void testDateMandatory() {
        // Invalid
        TaxParcelDetailedRow row = makeRow(null, null, null, null, null);
        row.validateDateMandatory(errors);
        expectSingleError();
        
        // Valid
        row = makeRow("2016-10-20", null, null, null, null);
        row.validateDateMandatory(errors);
        expectNoError();
    }
    
    @Test
    public void testDateNotFutureDated() {
        // Invalid
        TaxParcelDetailedRow row = makeRow("2016-10-21", null, null, null, null);
        row.validateDateNotFutureDated(errors);
        expectSingleError();

        // Valid
        row = makeRow("2016-10-20", null, null, null, null);
        row.validateDateMandatory(errors);
        expectNoError();
    }

    @Test
    public void testCostBaseFormat() {
        // Invalid
        TaxParcelDetailedRow row = makeRow(null, "-10", "-20", "-30", "-40");
        row.validateCostBaseFormat(errors);
        expectSingleError();

        row = makeRow(null, "-10", "20", "30", null);
        row.validateCostBaseFormat(errors);
        expectSingleError();

        // Valid
        row = makeRow(null, "10", null, "30", "40");
        row.validateCostBaseFormat(errors);
        expectNoError();
    }

    @Test
    public void testIsValidCostBaseFormat() {
        // Invalid
        TaxParcelDetailedRow row = makeRow(null, null, null, null, null);
        Assert.assertFalse(row.isValidCostBaseFormat("-10"));
        Assert.assertFalse(row.isValidCostBaseFormat("10.123"));

        // Valid
        Assert.assertTrue(row.isValidCostBaseFormat("10"));
        Assert.assertTrue(row.isValidCostBaseFormat("10.12"));
        Assert.assertTrue(row.isValidCostBaseFormat("1,000.2"));
    }

    @Test
    public void testCostBaseCombination() {
        // Invalid
        TaxParcelDetailedRow row = makeRow(null, "10", "20", "30", null);
        row.validateCostBaseCombination(errors);
        expectSingleError();

        row = makeRow(null, null, null, null, null);
        row.validateCostBaseCombination(errors);
        expectSingleError();

        row = makeRow(null, null, null, "30", null);
        row.validateCostBaseCombination(errors);
        expectSingleError();

        row = makeRow(null, "10", null, "30", null);
        row.validateCostBaseCombination(errors);
        expectSingleError();

        // Valid
        row = makeRow(null, "10", null, null, null);
        row.validateCostBaseCombination(errors);
        expectNoError();

        row = makeRow(null, null, "20", null, null);
        row.validateCostBaseCombination(errors);
        expectNoError();

        row = makeRow(null, null, "20", "30", null);
        row.validateCostBaseCombination(errors);
        expectNoError();
    }

    @Test
    public void testCostBaseIcbRequired() {
        // Invalid
        TaxParcelDetailedRow row = makeRow("1985-09-20", null, null, null, null);
        row.validateCostBaseIcbRequired(errors);
        expectSingleError();

        // Valid
        row = makeRow("1985-09-19", null, null, null, null);
        row.validateCostBaseIcbRequired(errors);
        expectNoError();

        row = makeRow("1985-09-20", "10", null, null, null);
        row.validateCostBaseIcbRequired(errors);
        expectNoError();

        row = makeRow("1985-09-20", null, null, null, "40");
        row.validateCostBaseIcbRequired(errors);
        expectNoError();
    }

    @Test
    public void testCostBaseIcbNotRequired() {
        // Invalid
        TaxParcelDetailedRow row = makeRow("1985-09-19", null, null, null, "40");
        row.validateCostBaseIcbNotRequired(errors);
        expectSingleError();

        row = makeRow("1999-09-21", null, null, null, "40");
        row.validateCostBaseIcbNotRequired(errors);
        expectSingleError();
    }

    @Test
    public void testCostBaseRevenueAssetUsesOcb() {
        // Create revenue asset
        Asset revenueAsset = Mockito.mock(Asset.class);
        Mockito.when(revenueAsset.getRevenueAssetIndicator()).thenReturn("true");

        // Invalid
        TaxParcelDetailedRow row = makeRow(null, null, null, null, null);
        row.setAsset(revenueAsset);
        row.validateCostBaseRevenueAssetUsesOcb(errors);
        expectSingleError();

        // Valid
        row = makeRow(null, "10", null, null, null);
        row.setAsset(revenueAsset);
        row.validateCostBaseRevenueAssetUsesOcb(errors);
        expectNoError();

        // No longer revenue asset - previous invalid now valid
        Mockito.when(revenueAsset.getRevenueAssetIndicator()).thenReturn(null);
        row = makeRow(null, null, null, null, null);
        row.setAsset(revenueAsset);
        row.validateCostBaseRevenueAssetUsesOcb(errors);
        expectNoError();
    }

    @Test
    public void testCostBaseValueSizeComparison() {
        // Warn
        TaxParcelDetailedRow row = makeRow(null, null, "10", "500", null);
        row.validateCostBaseValueSizeComparison(errors);
        expectSingleError();

        // Valid
        row = makeRow(null, null, "10", "10", null);
        row.validateCostBaseValueSizeComparison(errors);
        expectNoError();

        // Don't warn - picked up by other validation
        row = makeRow(null, null, "cost base", "cheese", null);
        row.validateCostBaseValueSizeComparison(errors);
        expectNoError();
    }

    private TaxParcelDetailedRow makeRow(String date, String ocb, String cb, String rcb, String icb) {
        List<String> rowData = Arrays.asList("assetCode", null, null, null, date, null, ocb, null, cb, rcb, icb);
        TaxParcelDetailedRow row = new TaxParcelDetailedRow(rowData, 1, util, bankDate);
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
