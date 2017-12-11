package com.bt.nextgen.api.inspecietransfer.v3.util;

import com.bt.nextgen.api.inspecietransfer.v3.util.vetting.TaxParcelDetailedHeader;
import com.bt.nextgen.api.inspecietransfer.v3.util.vetting.TaxParcelDetailedRow;
import com.bt.nextgen.api.inspecietransfer.v3.util.vetting.TaxParcelHeader;
import com.bt.nextgen.api.inspecietransfer.v3.util.vetting.TaxParcelRow;
import com.bt.nextgen.api.inspecietransfer.v3.util.vetting.TaxParcelUploadFile;
import com.bt.nextgen.api.inspecietransfer.v3.util.vetting.TaxParcelUploadFileBuilder;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.transfer.TransferType;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class FileParserUtil {

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Autowired
    private CmsService cmsService;

    @Autowired
    private TaxParcelUploadUtil util;

    @Autowired
    private BankDateIntegrationService bankDateService;

    private static final Logger logger = LoggerFactory.getLogger(FileParserUtil.class);

    private static final String UPLOAD_ERROR = "Err.IP-0164";
    private static final String XLSX_FILE_EXT = "xlsx";
    private static final String FORMULA_MARKER = "CELL_IS_FORMULA";

    private static final int CBO_EXPECTED_HEADER_ROW_INDEX = 11;
    private static final int CBO_EXPECTED_FIRST_DATA_ROW_INDEX = 12;
    private static final int CBO_MAX_COLUMN_COUNT = 4;
    private static final int NCBO_EXPECTED_HEADER_ROW_INDEX = 22;
    private static final int NCBO_EXPECTED_FIRST_DATA_ROW_INDEX = 24;
    private static final int NCBO_MAX_COLUMN_COUNT = 11;

    private PolicyFactory policy = Sanitizers.FORMATTING;

    public boolean isValidExcel(MultipartFile file) {
        if (file != null && file.getOriginalFilename() != null) {
            return file.getOriginalFilename().endsWith(XLSX_FILE_EXT);
        }
        return false;
    }

    public TaxParcelUploadFile parse(boolean isCbo, TransferType transferType, String sponsorName, MultipartFile file,
            boolean independentTaxParcels) {
        TaxParcelUploadFile validator = null;
        try {
            if (isValidExcel(file)) {
                XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
                List<List<String>> taxParcelData = convertToList(workbook.getSheetAt(0), isCbo);
                validator = buildUploadFile(taxParcelData, isCbo, transferType, sponsorName, workbook.isMacroEnabled(),
                        independentTaxParcels);
            }
            return validator;
        } catch (IOException | NumberFormatException e) {
            logger.error("failed to read upload tax parcels file", e);
            throw new BadRequestException(ApiVersion.CURRENT_VERSION, cmsService.getContent(UPLOAD_ERROR));
        } finally {
            try {
                if (file.getInputStream() != null) {
                    file.getInputStream().close();
                }
            } catch (IOException e) {
                logger.error("failed to close upload file", e);
            }
        }
    }

    private List<List<String>> convertToList(XSSFSheet spreadSheet, boolean isCbo) {
        List<List<String>> taxParcelData = new ArrayList<>();
        for (int i = 0; i <= spreadSheet.getLastRowNum(); i++) {
            XSSFRow row = spreadSheet.getRow(i);
            List<String> rowData = new ArrayList<>();
            if (row != null) {
                rowData = processCells(row, isCbo);
            }
            taxParcelData.add(rowData);
        }
        return taxParcelData;
    }

    private List<String> processCells(XSSFRow row, boolean isCbo) {
        List<String> rowData = new ArrayList<>();
        int maxColumnCount = isCbo ? CBO_MAX_COLUMN_COUNT : NCBO_MAX_COLUMN_COUNT;
        for (int i = 0; i < maxColumnCount; i++) {
            XSSFCell cell = row.getCell(i, Row.RETURN_BLANK_AS_NULL);
            rowData.add(getCellValue(cell));
        }
        return rowData;
    }

    private String getCellValue(XSSFCell cell) {
        String cellValue = null;
        if (cell != null) {
            switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                cellValue = cell.getRichStringCellValue().getString();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                cellValue = getDateOrNumber(cell);
                break;
            case Cell.CELL_TYPE_FORMULA:
                cellValue = FORMULA_MARKER;
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                cellValue = cell.getRawValue().toString();
                break;
            default:
                cellValue = null;
                break;
            }
        }
        // Sanitize the input values. If value is null, empty string will be returned.
        cellValue = policy.sanitize(cellValue);
        return StringUtils.isBlank(cellValue) ? null : cellValue;
    }

    private String getDateOrNumber(XSSFCell cell) {
        String cellValue;
        if (DateUtil.isCellDateFormatted(cell)) {
            cellValue = getDateString(cell.getDateCellValue());
        } else {
            cellValue = new DataFormatter().formatCellValue(cell).replace(",", "");
        }
        return cellValue;
    }

    private String getDateString(Date date) {
        String formattedDate = null;
        if (date != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            formattedDate = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE);
        }
        return formattedDate;
    }

    private TaxParcelUploadFile buildUploadFile(List<List<String>> taxParcelData, boolean isCbo, TransferType transferType,
            String sponsorName, boolean isMacroEnabled, boolean independentTaxParcels) {
        boolean fileContainsFormulas = getHasFormulas(taxParcelData);
        TaxParcelHeader header = createHeader(taxParcelData, isCbo);
        List<TaxParcelRow> rows = createRows(taxParcelData, isCbo);

        if (independentTaxParcels) {
            return TaxParcelUploadFileBuilder.uploadFile().withHeader(header).withRows(rows).withIsMacroEnabled(isMacroEnabled)
                    .withHasFormulas(fileContainsFormulas).withIsCbo(isCbo).withTransferType(transferType)
                    .withSponsorName(sponsorName).withUtil(util).withAssetService(assetService).buildIndependent();
        } else {
            return TaxParcelUploadFileBuilder.uploadFile().withHeader(header).withRows(rows).withIsMacroEnabled(isMacroEnabled)
                    .withHasFormulas(fileContainsFormulas).withIsCbo(isCbo).withTransferType(transferType)
                    .withSponsorName(sponsorName).withUtil(util).withAssetService(assetService).build();
        }
    }

    private boolean getHasFormulas(List<List<String>> taxParcelData) {
        for (List<String> row : taxParcelData) {
            for (String cell : row) {
                if (cell != null && FORMULA_MARKER.equals(cell)) {
                    return true;
                }
            }
        }
        return false;
    }

    private TaxParcelHeader createHeader(List<List<String>> taxParcelData, boolean isCbo) {
        List<String> header = null;
        int index = isCbo ? CBO_EXPECTED_HEADER_ROW_INDEX : NCBO_EXPECTED_HEADER_ROW_INDEX;
        if (taxParcelData.size() > index) {
            header = taxParcelData.get(index);
        }

        if (isCbo) {
            return new TaxParcelHeader(header, util);
        } else {
            return new TaxParcelDetailedHeader(header, util);
        }
    }

    private List<TaxParcelRow> createRows(List<List<String>> taxParcelData, boolean isCbo) {
        List<TaxParcelRow> rows = new ArrayList<>();
        DateTime avaloqBankDate = isCbo ? null : bankDateService.getBankDate(new ServiceErrorsImpl());
        int index = isCbo ? CBO_EXPECTED_FIRST_DATA_ROW_INDEX : NCBO_EXPECTED_FIRST_DATA_ROW_INDEX;

        if (taxParcelData.size() > index) {
            for (int i = index; i < taxParcelData.size(); i++) {
                int rowNumber = i + 1;
                List<String> row = taxParcelData.get(i);
                if (!isEmptyRow(row)) {
                    if (isCbo) {
                        rows.add(new TaxParcelRow(row, rowNumber, util));
                    } else {
                        rows.add(new TaxParcelDetailedRow(row, rowNumber, util, avaloqBankDate));
                    }
                }
            }
        }
        return rows;
    }

    private boolean isEmptyRow(List<String> rowData) {
        if (!rowData.isEmpty()) {
            for (String cell : rowData) {
                if (cell != null) {
                    return false;
                }
            }
        }
        return true;
    }
}
