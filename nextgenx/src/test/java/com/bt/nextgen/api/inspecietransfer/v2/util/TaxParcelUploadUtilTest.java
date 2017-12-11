package com.bt.nextgen.api.inspecietransfer.v2.util;

import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferKey;
import com.bt.nextgen.api.inspecietransfer.v2.model.SettlementRecordDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.SettlementRecordDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v2.model.SponsorDetailsDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v2.service.TransferAssetHelper;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.service.integration.transfer.TransferType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TaxParcelUploadUtilTest {

    @InjectMocks
    private TaxParcelUploadUtil util;

    @Mock
    CmsService cmsService;

    @Mock
    TransferAssetHelper assetHelper;

    InspecieTransferDtoImpl transferDto = null;
    List<String[]> taxParcels = new ArrayList<>();

    @Before
    public void setup() throws Exception {
        SettlementRecordDtoImpl asset1 = new SettlementRecordDtoImpl("121", "BHP", new BigDecimal("1234.00"));
        SettlementRecordDtoImpl asset3 = new SettlementRecordDtoImpl("121", "ABC", new BigDecimal("1235"));

        List<SettlementRecordDto> assets = new ArrayList<>();
        assets.add(asset1);
        assets.add(asset3);
        transferDto = new InspecieTransferDtoImpl(TransferType.LS_BROKER_SPONSORED.getDisplayName(), new SponsorDetailsDtoImpl(),
                assets, "123456", new InspecieTransferKey("124", "214"), Boolean.TRUE, new ArrayList<DomainApiErrorDto>());

        Mockito.when(cmsService.getContent(Mockito.any(String.class))).thenReturn("BT");
        Mockito.when(cmsService.getDynamicContent(Mockito.any(String.class), Mockito.any(String[].class))).thenReturn("BT");
        Mockito.when(assetHelper.chkRevenueAssetIdentifier(Mockito.any(String[].class), Mockito.anyList())).thenReturn(true);
    }

    @Test
    public void testValidateFile_validInputs() {
        String[] taxParcelHeader = { "Asset", "Tax Relevant Date", "Quantity", "Imported Cost Base", "Imported Reduced Cost Base" };
        String[] taxParcelField1 = { "abc", "1/12/1991", "1,235", "10", "0", "1,00" };
        String[] taxParcelField2 = { "BHP", "25/2/2014", "1234.00", "0", "0", null };
        taxParcels.add(taxParcelHeader);
        taxParcels.add(taxParcelField1);
        taxParcels.add(taxParcelField2);

        InspecieTransferDto resultDto = util.validateFile(taxParcels, transferDto);
        Assert.assertNotNull(resultDto);
        Assert.assertEquals(resultDto.getTaxParcels().get(0).getAssetCode().toLowerCase(), taxParcelField1[0].toLowerCase());
        Assert.assertEquals(resultDto.getTaxParcels().get(0).getQuantity(), new BigDecimal(taxParcelField1[2].replace(",", "")));
        Assert.assertEquals(resultDto.getTaxParcels().get(0).getCostBase(), new BigDecimal(taxParcelField1[3]));
        Assert.assertEquals(resultDto.getTaxParcels().get(0).getReducedCostBase(), new BigDecimal(taxParcelField1[4]));
        Assert.assertEquals(resultDto.getTaxParcels().get(0).getIndexedCostBase(),
                new BigDecimal(taxParcelField1[5].replace(",", "")));
        Assert.assertEquals(resultDto.getTaxParcels().get(1).getAssetCode().toLowerCase(), taxParcelField2[0].toLowerCase());
        Assert.assertEquals(resultDto.getTaxParcels().get(1).getQuantity(), new BigDecimal(taxParcelField2[2]));
        Assert.assertEquals(resultDto.getTaxParcels().get(1).getCostBase(), new BigDecimal(taxParcelField2[3]));
        Assert.assertEquals(resultDto.getTaxParcels().get(1).getReducedCostBase(), new BigDecimal(taxParcelField2[4]));
        Assert.assertTrue(resultDto.getWarnings().isEmpty());
    }

    @Test
    public void testValidateFile_assetBlank() {
        String[] taxParcelHeader = { "Asset", "Tax Relevant Date", "Quantity", "Imported Cost Base", "Imported Reduced Cost Base" };
        String[] taxParcelField1 = { " ", "1/12/2000", "1235", "0", "0" };
        String[] taxParcelField2 = { "BHP", "31/1/2014", "1234.00", "0", "0" };
        taxParcels.add(taxParcelHeader);
        taxParcels.add(taxParcelField1);
        taxParcels.add(taxParcelField2);

        InspecieTransferDto resultDto = util.validateFile(taxParcels, transferDto);
        Assert.assertNotNull(resultDto);
        Assert.assertFalse(resultDto.getWarnings().isEmpty());
        Assert.assertEquals(resultDto.getWarnings().size(), 1);
    }

    @Test
    public void testValidateFile_inValidDates() {
        String[] taxParcelHeader = { "Asset", "Tax Relevant Date", "Quantity", "Imported Cost Base", "Imported Reduced Cost Base" };
        String[] taxParcelField1 = { "ABC", "1/12/2000", "1235", "0", "0" };
        String[] taxParcelField2 = { "BHP", "32/1/2014", "1234.00", "0", "0" };
        taxParcels.add(taxParcelHeader);
        taxParcels.add(taxParcelField1);
        taxParcels.add(taxParcelField2);

        InspecieTransferDto resultDto = util.validateFile(taxParcels, transferDto);
        Assert.assertNotNull(resultDto);
        Assert.assertFalse(resultDto.getWarnings().isEmpty());
        Assert.assertEquals(resultDto.getWarnings().size(), 1);
    }

    @Test
    public void testValidateFile_inValidNoOfCols() {
        String[] taxParcelHeader = { "Asset", "Tax Relevant Date", "Quantity", "Imported Cost Base", "Imported Reduced Cost Base" };
        String[] taxParcelField1 = { "ABC", "1/12/2000", "1235", "0", "0" };
        String[] taxParcelField2 = { "BHP", "30/1/1991", "1234.00", "0", "0", "100", "120" };
        taxParcels.add(taxParcelHeader);
        taxParcels.add(taxParcelField1);
        taxParcels.add(taxParcelField2);

        InspecieTransferDto resultDto = util.validateFile(taxParcels, transferDto);
        Assert.assertNotNull(resultDto);
        Assert.assertFalse(resultDto.getWarnings().isEmpty());
        Assert.assertEquals(resultDto.getWarnings().size(), 1);
    }

    @Test
    public void testValidateFile_invalidHeaders() {
        String[] taxParcelHeader = { "Asset", "Tax Date", "Quantity", "Imported Cost Base", "Imported Reduced Cost Base" };
        taxParcels.add(taxParcelHeader);

        InspecieTransferDto resultDto = util.validateFile(taxParcels, transferDto);
        Assert.assertNotNull(resultDto);
        Assert.assertFalse(resultDto.getWarnings().isEmpty());
        Assert.assertEquals(resultDto.getWarnings().size(), 1);
        Assert.assertEquals(resultDto.getWarnings().get(0).getErrorId(), "Err.IP-0527");
    }

    @Test
    public void testValidateFile_fieldsNull() {
        String[] taxParcelHeader = { "Asset", "Tax Relevant Date", "Quantity", "Imported Cost Base", "Imported Reduced Cost Base" };
        String[] taxParcelField1 = { null, "10/12/2000", "1235", "100", "200" };
        taxParcels.add(taxParcelHeader);
        taxParcels.add(taxParcelField1);

        InspecieTransferDto resultDto = util.validateFile(taxParcels, transferDto);
        Assert.assertNotNull(resultDto);
        Assert.assertFalse(resultDto.getWarnings().isEmpty());
        Assert.assertEquals(resultDto.getWarnings().size(), 1);
        Assert.assertEquals(resultDto.getWarnings().get(0).getErrorId(), "Err.IP-0519");
    }

    @Test
    public void testValidateFile_invalidInputs() {
        String[] taxParcelHeader = { "Asset", "Tax Relevant Date", "Quantity", "Imported Cost Base", "Imported Reduced Cost Base" };
        String[] taxParcelField1 = { "ABC", "10/12/1990", "1235", "100", "200" };
        String[] taxParcelField2 = { "BHP", "12/12/2050", "1234", "100", "200" };
        taxParcels.add(taxParcelHeader);
        taxParcels.add(taxParcelField1);
        taxParcels.add(taxParcelField2);

        InspecieTransferDto resultDto = util.validateFile(taxParcels, transferDto);
        Assert.assertNotNull(resultDto);
        Assert.assertFalse(resultDto.getWarnings().isEmpty());
        Assert.assertEquals(resultDto.getWarnings().size(), 2);
    }

    @Test
    public void testValidateFile_InputsHaveCommas() {
        String[] taxParcelHeader = { "Asset", "Tax Relevant Date", "Quantity", "Imported Cost Base", "Imported Reduced Cost Base" };
        String[] taxParcelField1 = { "ABC", "10/12/2000", "1235", "100", "200" };
        String[] taxParcelField2 = { "bhp", "12/12/2014", "1234.00", "1", "234", "200" };
        taxParcels.add(taxParcelHeader);
        taxParcels.add(taxParcelField1);
        taxParcels.add(taxParcelField2);

        InspecieTransferDto resultDto = util.validateFile(taxParcels, transferDto);
        Assert.assertNotNull(resultDto);
        Assert.assertFalse(resultDto.getWarnings().isEmpty());
        Assert.assertEquals(resultDto.getWarnings().size(), 1);
    }
}
