package com.bt.nextgen.api.inspecietransfer.v3.util;

import com.bt.nextgen.api.inspecietransfer.v3.util.vetting.TaxParcelDetailedHeader;
import com.bt.nextgen.api.inspecietransfer.v3.util.vetting.TaxParcelDetailedRow;
import com.bt.nextgen.api.inspecietransfer.v3.util.vetting.TaxParcelHeader;
import com.bt.nextgen.api.inspecietransfer.v3.util.vetting.TaxParcelRow;
import com.bt.nextgen.api.inspecietransfer.v3.util.vetting.TaxParcelUploadFile;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.transfer.TransferType;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.InputStream;

@RunWith(MockitoJUnitRunner.class)
public class FileParserUtilTest {

    @InjectMocks
    private FileParserUtil fileParserUtil;

    @Mock
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Mock
    private CmsService cmsService;

    @Mock
    private BankDateIntegrationService bankDateService;

    MockMultipartFile invalidFile;
    MockMultipartFile emptyMacroEnabledFile;
    MockMultipartFile cboFile;
    MockMultipartFile ncboFile;

    @Before
    public void setup() throws Exception {

        String excelMediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        InputStream invalidInput = getClass().getResourceAsStream("/csv/testUploadFile.csv");
        invalidFile = new MockMultipartFile("testUploadFile", "testUploadFile.csv", MediaType.TEXT_PLAIN_VALUE, invalidInput);

        InputStream emptyInput = getClass().getResourceAsStream("/xlsx/emptyMacroEnabled.xlsx");
        emptyMacroEnabledFile = new MockMultipartFile("emptyMacroEnabled", "emptyMacroEnabled.xlsx", excelMediaType, emptyInput);

        InputStream cboInput = getClass().getResourceAsStream("/xlsx/cboTransferTaxParcelUpload.xlsx");
        cboFile = new MockMultipartFile("cboTransferTaxParcelUpload", "cboTransferTaxParcelUpload.xlsx", excelMediaType, cboInput);

        InputStream ncboInput = getClass().getResourceAsStream("/xlsx/ncboTransferTaxParcelUpload.xlsx");
        ncboFile = new MockMultipartFile("ncboTransferTaxParcelUpload", "ncboTransferTaxParcelUpload.xlsx", excelMediaType,
                ncboInput);

        Mockito.when(bankDateService.getBankDate(Mockito.any(ServiceErrors.class))).thenReturn(new DateTime("2016-01-01"));

        // Mockito.when(cmsService.getContent(Mockito.anyString())).thenReturn("Err.IP-0164");
    }

    @Test
    public void testValidateFormat_invalid() {
        Assert.assertFalse(fileParserUtil.isValidExcel(invalidFile));
    }

    @Test
    public void testValidateFormat_valid() {
        Assert.assertTrue(fileParserUtil.isValidExcel(cboFile));
    }

    @Test
    public void testUploadInvalid_returnsNoFileValidator() {
        TaxParcelUploadFile validator = fileParserUtil.parse(false, TransferType.LS_BROKER_SPONSORED, "sponsorName", invalidFile,
                false);
        Assert.assertNull(validator);
    }

    @Test
    public void testUploadEmpty_returnsEmptyFileValidator() {
        TaxParcelUploadFile validator = fileParserUtil.parse(true, TransferType.LS_BROKER_SPONSORED, "sponsorName",
                emptyMacroEnabledFile, false);
        Assert.assertNotNull(validator);

        Assert.assertEquals(true, validator.getIsMacroEnabled());
        Assert.assertEquals(false, validator.getHasFormulas());
        Assert.assertEquals(true, validator.getIsCbo());
        Assert.assertEquals(TransferType.LS_BROKER_SPONSORED, validator.getTransferType());
        Assert.assertEquals("sponsorName", validator.getSponsorName());

        TaxParcelHeader header = validator.getHeader();
        Assert.assertNull(header.getAssetHeader());
        Assert.assertNull(header.getQuantityHeader());
        Assert.assertNull(header.getOwnerHeader());
        Assert.assertNull(header.getCustodianHeader());

        Assert.assertTrue(validator.getRows().isEmpty());
    }

    @Test
    public void testUploadCbo_buildsFileValidator() {
        TaxParcelUploadFile validator = fileParserUtil.parse(true, TransferType.LS_BROKER_SPONSORED, "sponsorName", cboFile,
                false);
        Assert.assertNotNull(validator);

        Assert.assertEquals(false, validator.getIsMacroEnabled());
        Assert.assertEquals(true, validator.getHasFormulas());
        Assert.assertEquals(true, validator.getIsCbo());
        Assert.assertEquals(TransferType.LS_BROKER_SPONSORED, validator.getTransferType());
        Assert.assertEquals("sponsorName", validator.getSponsorName());

        TaxParcelHeader header = validator.getHeader();
        Assert.assertNotNull(header);
        validateBasicHeaders(header);

        Assert.assertEquals(2, validator.getRows().size());

        TaxParcelRow row1 = validator.getRows().get(0);
        Assert.assertEquals("WOW", row1.getAssetCode());
        Assert.assertEquals("100", row1.getQuantity());
        Assert.assertEquals("X1234512345", row1.getOwner());
        Assert.assertEquals("Platform Name", row1.getCustodian());
        Assert.assertEquals("13", row1.getRowNumber());

        TaxParcelRow row2 = validator.getRows().get(1);
        Assert.assertEquals("TLS", row2.getAssetCode());
        Assert.assertEquals("20", row2.getQuantity());
        Assert.assertEquals("X1234512345", row2.getOwner());
        Assert.assertEquals("CELL_IS_FORMULA", row2.getCustodian());
        Assert.assertEquals("14", row2.getRowNumber());
    }

    @Test
    public void testUploadNcbo_buildsFileValidator() {
        TaxParcelUploadFile validator = fileParserUtil.parse(false, TransferType.LS_BROKER_SPONSORED, "sponsorName", ncboFile,
                false);
        Assert.assertNotNull(validator);

        Assert.assertEquals(false, validator.getIsMacroEnabled());
        Assert.assertEquals(false, validator.getHasFormulas());
        Assert.assertEquals(false, validator.getIsCbo());
        Assert.assertEquals(TransferType.LS_BROKER_SPONSORED, validator.getTransferType());
        Assert.assertEquals("sponsorName", validator.getSponsorName());

        TaxParcelDetailedHeader header = (TaxParcelDetailedHeader) validator.getHeader();
        Assert.assertNotNull(header);
        validateBasicHeaders(header);
        validateFullHeaders(header);

        Assert.assertEquals(2, validator.getRows().size());

        TaxParcelDetailedRow row1 = (TaxParcelDetailedRow) validator.getRows().get(0);
        Assert.assertEquals("WOW", row1.getAssetCode());
        Assert.assertEquals("100", row1.getQuantity());
        Assert.assertEquals("X1234512345", row1.getOwner());
        Assert.assertEquals("Platform Name", row1.getCustodian());
        Assert.assertEquals("25", row1.getRowNumber());
        Assert.assertEquals(new DateTime("2016-08-17"), row1.getAcquisitionDate());
        Assert.assertEquals("1", row1.getOriginalCostBase());
        Assert.assertEquals("0.5", row1.getCostBase());
        Assert.assertEquals("1", row1.getReducedCostBase());
        Assert.assertEquals("0.3", row1.getIndexedCostBase());

        TaxParcelDetailedRow row2 = (TaxParcelDetailedRow) validator.getRows().get(1);
        Assert.assertEquals("TLS", row2.getAssetCode());
        Assert.assertEquals("20", row2.getQuantity());
        Assert.assertEquals("X1234512345", row2.getOwner());
        Assert.assertEquals("Platform Name", row2.getCustodian());
        Assert.assertEquals("26", row2.getRowNumber());
        Assert.assertEquals(new DateTime("2016-08-15"), row2.getAcquisitionDate());
        Assert.assertEquals("1", row2.getOriginalCostBase());
        Assert.assertEquals("0.5", row2.getCostBase());
        Assert.assertEquals("0.55", row2.getReducedCostBase());
        Assert.assertEquals("0.3", row2.getIndexedCostBase());
    }

    private void validateBasicHeaders(TaxParcelHeader header) {
        Assert.assertTrue(header.getAssetHeader().startsWith("ASX Security Code or APIR Code"));
        Assert.assertTrue(header.getQuantityHeader().startsWith("Quantity"));
        Assert.assertTrue(header.getOwnerHeader().startsWith("HIN or SRN or Account Number"));
        Assert.assertTrue(header.getCustodianHeader().startsWith("Platform Name or Managed Fund Custodian"));
    }

    private void validateFullHeaders(TaxParcelDetailedHeader header) {
        Assert.assertTrue(header.getDateHeader().startsWith("Acquisition date"));
        Assert.assertTrue(header.getOriginalCostBaseHeader().startsWith("Original Cost Base"));
        Assert.assertTrue(header.getCostBaseHeader().startsWith("CGT Cost Base"));
        Assert.assertTrue(header.getReducedCostBaseHeader().startsWith("CGT Reduced Cost Base"));
        Assert.assertTrue(header.getIndexedCostBaseHeader().startsWith("CGT Indexed Cost Base"));
    }
}
