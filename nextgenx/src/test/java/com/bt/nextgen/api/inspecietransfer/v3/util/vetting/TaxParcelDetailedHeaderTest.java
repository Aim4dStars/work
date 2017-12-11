package com.bt.nextgen.api.inspecietransfer.v3.util.vetting;

import com.bt.nextgen.api.inspecietransfer.v3.util.TaxParcelUploadUtil;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto.ErrorType;
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
public class TaxParcelDetailedHeaderTest {

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
        List<String> headerData = Arrays.asList(null, null, null, null, null, null, null, null, null, null, null);
        TaxParcelDetailedHeader header = new TaxParcelDetailedHeader(headerData, util);
        assertAllHeadersNull(header);

        headerData = Arrays.asList("assetCode", null, "owner");
        header = new TaxParcelDetailedHeader(headerData, util);
        assertAllHeadersNull(header);

        headerData = Arrays.asList("assetCode", null, "owner", "custodian", "date", null, "ocb", null, "cb", "rcb", "icb");
        header = new TaxParcelDetailedHeader(headerData, util);
        assertAllHeadersNull(header);
    }

    @Test
    public void testFieldsPopulatedIfValidRow() {
        List<String> headerData = Arrays.asList("assetCode", "quantity", "owner", "custodian", "date", null, "ocb", null, "cb",
                "rcb", "icb");
        TaxParcelDetailedHeader header = new TaxParcelDetailedHeader(headerData, util);
        assertAllHeadersFilled(header);

        // Ignore extra fields
        headerData = Arrays.asList("assetCode", "quantity", "owner", "custodian", "date", null, "ocb",
                null, "cb", "rcb", "icb", null, null, "cheese");
        header = new TaxParcelDetailedHeader(headerData, util);
        assertAllHeadersFilled(header);

        headerData = Arrays.asList("assetCode", "quantity", "owner", "custodian", "date", "this is meant to be null!", "ocb",
                "this too", "cb", "rcb", "icb", null, null, "cheese");
        header = new TaxParcelDetailedHeader(headerData, util);
        assertAllHeadersFilled(header);
    }

    @Test
    public void testValidate() {
        // Invalid
        List<String> headerData = Arrays.asList("assetCode", "quantity", "owner", "custodian", "date", null, "ocb", null, "cb",
                "rcb", "icb");
        TaxParcelDetailedHeader header = new TaxParcelDetailedHeader(headerData, util);
        header.validate(errors);
        expectSingleError();

        // Valid
        headerData = Arrays.asList(TaxParcelColumn.ASSET_CODE.getColumnHeader(), TaxParcelColumn.QUANTITY.getColumnHeader(),
                TaxParcelColumn.OWNER.getColumnHeader(), TaxParcelColumn.CUSTODIAN.getColumnHeader(),
                TaxParcelColumn.ACQUISITION_DATE.getColumnHeader(), null, TaxParcelColumn.ORIGINAL_COST_BASE.getColumnHeader(),
                null, TaxParcelColumn.CGT_COST_BASE.getColumnHeader(), TaxParcelColumn.REDUCED_COST_BASE.getColumnHeader(),
                TaxParcelColumn.INDEXED_COST_BASE.getColumnHeader());
        header = new TaxParcelDetailedHeader(headerData, util);
        header.validate(errors);
        expectNoError();
    }

    private void expectSingleError() {
        Assert.assertEquals(1, errors.size());
        errors.clear();
    }

    private void expectNoError() {
        Assert.assertEquals(0, errors.size());
        errors.clear();
    }

    private void assertAllHeadersNull(TaxParcelDetailedHeader header) {
        Assert.assertNull(header.getAssetHeader());
        Assert.assertNull(header.getQuantityHeader());
        Assert.assertNull(header.getOwnerHeader());
        Assert.assertNull(header.getCustodianHeader());
        Assert.assertNull(header.getDateHeader());
        Assert.assertNull(header.getOriginalCostBaseHeader());
        Assert.assertNull(header.getCostBaseHeader());
        Assert.assertNull(header.getReducedCostBaseHeader());
        Assert.assertNull(header.getIndexedCostBaseHeader());
    }

    private void assertAllHeadersFilled(TaxParcelDetailedHeader header) {
        Assert.assertEquals("assetCode", header.getAssetHeader());
        Assert.assertEquals("quantity", header.getQuantityHeader());
        Assert.assertEquals("owner", header.getOwnerHeader());
        Assert.assertEquals("custodian", header.getCustodianHeader());
        Assert.assertEquals("date", header.getDateHeader());
        Assert.assertEquals("ocb", header.getOriginalCostBaseHeader());
        Assert.assertEquals("cb", header.getCostBaseHeader());
        Assert.assertEquals("rcb", header.getReducedCostBaseHeader());
        Assert.assertEquals("icb", header.getIndexedCostBaseHeader());
    }
}
