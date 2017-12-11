package com.bt.nextgen.api.inspecietransfer.v3.util.vetting;

import com.bt.nextgen.api.inspecietransfer.v3.util.TaxParcelUploadUtil;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto.ErrorType;
import com.bt.nextgen.service.avaloq.asset.ShareAssetImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.ShareAsset;
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
public class TaxParcelRowTest {

    @Mock
    private TaxParcelUploadUtil util;

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
    public void testFieldsNotPopulatedIfInvalidRow() {
        List<String> rowData = Arrays.asList("column1", null, "column3");
        TaxParcelRow row = new TaxParcelRow(rowData, 1, util);

        Assert.assertNull(row.getAssetCode());
        Assert.assertNull(row.getQuantity());
        Assert.assertNull(row.getOwner());
        Assert.assertNull(row.getCustodian());
    }

    @Test
    public void testFieldsPopulatedIfValidRow() {
        List<String> rowData = Arrays.asList("assetCode", "quantity", "owner", "custodian");
        TaxParcelRow row = new TaxParcelRow(rowData, 1, util);

        Assert.assertEquals("ASSETCODE", row.getAssetCode());
        Assert.assertEquals("quantity", row.getQuantity());
        Assert.assertEquals("OWNER", row.getOwner());
        Assert.assertEquals("custodian", row.getCustodian());
    }
    
    @Test
    public void testMandatory() {
        // Invalid
        TaxParcelRow rowOfNulls = makeRow(null, null, null, null);
        rowOfNulls.validateMandatory(errors, true);
        expectSingleError();
        
        // Valid when custodian not required
        TaxParcelRow rowWithoutCustodian = makeRow("assetCode", "quantity", "owner", null);
        rowWithoutCustodian.validateMandatory(errors, true);
        expectSingleError();
        rowWithoutCustodian.validateMandatory(errors, false);
        expectNoError();

        // Always valid
        TaxParcelRow rowAllData = makeRow("assetCode", "quantity", "owner", "custodian");
        rowAllData.validateMandatory(errors, true);
        rowAllData.validateMandatory(errors, false);
        expectNoError();
    }
    
    @Test
    public void testAssetExists() {
        // No asset object found
        TaxParcelRow row = makeRow("assetCode", null, null, null);
        row.validateAssetExists(errors);
        expectSingleError();

        // Asset object found
        row.setAsset(Mockito.mock(Asset.class));
        row.validateAssetExists(errors);
        expectNoError();
    }

    @Test
    public void testAssetIncursWitholdingTax() {
        // Create tax asset
        ShareAsset share = Mockito.mock(ShareAssetImpl.class);
        Mockito.when(share.getAssetType()).thenReturn(AssetType.SHARE);
        Mockito.when(share.getTaxAssetDomicile()).thenReturn(Boolean.TRUE);

        TaxParcelRow row = makeRow("assetCode", null, null, null);
        row.setAsset(share);
        row.validateAssetIncursWitholdingTax(errors);
        expectSingleError();

        // Asset is not tax asset
        Mockito.when(share.getTaxAssetDomicile()).thenReturn(Boolean.FALSE);
        row.validateAssetIncursWitholdingTax(errors);
        expectNoError();
    }

    @Test
    public void testAssetAsxCode() {
        // Invalid
        TaxParcelRow row = makeRow("assetCode", null, null, null);
        row.validateAssetAsxCode(errors);
        expectSingleError();

        // Valid
        row = makeRow("S32", null, null, null);
        row.validateAssetAsxCode(errors);
        expectNoError();

        // Valid
        row = makeRow("ASDFGH", null, null, null);
        row.validateAssetAsxCode(errors);
        expectNoError();
    }

    @Test
    public void testAssetIsFund() {
        // Invalid
        Asset share = Mockito.mock(Asset.class);
        Mockito.when(share.getAssetType()).thenReturn(AssetType.SHARE);

        TaxParcelRow row = makeRow("assetCode", null, null, null);
        row.setAsset(share);
        row.validateAssetIsFund(errors);
        expectSingleError();

        // Valid
        Asset fund = Mockito.mock(Asset.class);
        Mockito.when(fund.getAssetType()).thenReturn(AssetType.MANAGED_FUND);

        row.setAsset(fund);
        row.validateAssetIsFund(errors);
        expectNoError();
    }

    @Test
    public void testShareQuantityFormat() {
        // Invalid
        TaxParcelRow row = makeRow("assetCode", "quantity", null, null);
        row.validateShareQuantityFormat(errors);
        expectSingleError();

        // Invalid
        row = makeRow("assetCode", "10.12", null, null);
        row.validateShareQuantityFormat(errors);
        expectSingleError();

        // Invalid
        row = makeRow("assetCode", "-1", null, null);
        row.validateShareQuantityFormat(errors);
        expectSingleError();

        // Valid
        row = makeRow("assetCode", "30", null, null);
        row.validateShareQuantityFormat(errors);
        expectNoError();
    }

    @Test
    public void testMFQuantityFormat() {
        // Invalid
        TaxParcelRow row = makeRow("assetCode", "quantity", null, null);
        row.validateMFQuantityFormat(errors);
        expectSingleError();
        
        // Invalid
        row = makeRow("assetCode", "-1", null, null);
        row.validateMFQuantityFormat(errors);
        expectSingleError();
        
        // Invalid
        row = makeRow("assetCode", "10.123456789", null, null);
        row.validateMFQuantityFormat(errors);
        expectSingleError();
        
        // Valid
        row = makeRow("assetCode", "10", null, null);
        row.validateMFQuantityFormat(errors);
        expectNoError();
        
        // Valid
        row = makeRow("assetCode", "10.12345678", null, null);
        row.validateMFQuantityFormat(errors);
        expectNoError();
    }

    @Test
    public void testOwnerHinFormat() {
        // Invalid
        TaxParcelRow row = makeRow("assetCode", null, "owner", null);
        row.validateOwnerHinFormat(errors);
        expectSingleError();

        // Invalid
        row = makeRow("assetCode", null, "1234", null);
        row.validateOwnerHinFormat(errors);
        expectSingleError();

        // Invalid
        row = makeRow("assetCode", null, "X12345678901", null);
        row.validateOwnerHinFormat(errors);
        expectSingleError();

        // Valid
        row = makeRow("assetCode", null, "X1234567890", null);
        row.validateOwnerHinFormat(errors);
        expectNoError();
    }

    @Test
    public void testOwnerSrnFormat() {
        // Invalid
        TaxParcelRow row = makeRow("assetCode", null, "owner", null);
        row.validateOwnerSrnFormat(errors);
        expectSingleError();

        // Invalid
        row = makeRow("assetCode", null, "I1234567890123456", null);
        row.validateOwnerSrnFormat(errors);
        expectSingleError();

        // Valid
        row = makeRow("assetCode", null, "I123456789012345", null);
        row.validateOwnerSrnFormat(errors);
        expectNoError();
    }

    @Test
    public void testCustodianNotRequiredForBroker() {
        // Warn once
        TaxParcelRow row = makeRow(null, null, null, "custodian");
        row.validateCustodianNotRequiredForBroker(errors);
        row.validateCustodianNotRequiredForBroker(errors);
        expectSingleError();

        // Valid
        row = makeRow(null, null, null, null);
        expectNoError();
    }

    @Test
    public void testCustodianNotRequiredForIssuer() {
        // Warn once
        TaxParcelRow row = makeRow(null, null, null, "custodian");
        row.validateCustodianNotRequiredForIssuer(errors);
        row.validateCustodianNotRequiredForIssuer(errors);
        expectSingleError();

        // Valid
        row = makeRow(null, null, null, null);
        expectNoError();
    }

    private TaxParcelRow makeRow(String assetCode, String quantity, String owner, String custodian) {
        List<String> rowData = Arrays.asList(assetCode, quantity, owner, custodian);
        TaxParcelRow row = new TaxParcelRow(rowData, 1, util);
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
