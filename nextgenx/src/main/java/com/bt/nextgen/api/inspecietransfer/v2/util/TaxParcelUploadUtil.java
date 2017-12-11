package com.bt.nextgen.api.inspecietransfer.v2.util;

import au.com.bytecode.opencsv.CSVReader;
import ch.lambdaj.Lambda;
import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v2.model.SettlementRecordDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.TaxParcelDto;
import com.bt.nextgen.api.inspecietransfer.v2.service.TransferAssetHelper;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto.ErrorType;
import com.bt.nextgen.core.exception.ParseException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @deprecated Use V3
 */
@Deprecated
@Service("TaxParcelUploadUtilV2")
public class TaxParcelUploadUtil {
    private static final String PREFIX = "0";
    private static final String DELIM = "/";
    private static final Logger logger = LoggerFactory.getLogger(TaxParcelUploadUtil.class);
    private static final String ASSET_HEADER = "Asset";
    private static final String TAX_RELEVANT_DATE_HEADER = "Tax Relevant Date";
    private static final String QUANTITY_HEADER = "Quantity";
    private static final String IMPORT_COST_BASE_HEADER = "Imported Cost Base";
    private static final String IMPORT_REDUCED_COST_BASE_HEADER = "Imported Reduced Cost Base";
    private static final String START_DATE = "01/01/1989";
    private static final String END_DATE = "31/12/1999";
    private static final String FILE_EXT = "csv";

    @Autowired
    private TransferAssetHelper assetHelper;

    @Autowired
    private CmsService cmsService;

    public InspecieTransferDto parseFile(MultipartFile file, InspecieTransferDtoImpl transferDto) {
        CSVReader reader = null;
        try {
            if (!(fileFormatSizeCheck(file, transferDto).getWarnings().isEmpty()))
                return transferDto;

            reader = new CSVReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
            return validate(transferDto, reader.readAll());

        } catch (IOException | NumberFormatException e) {
            logger.error("failed to read upload tax parcels file", e);
            throw new BadRequestException(ApiVersion.CURRENT_VERSION, cmsService.getContent("Err.IP-0164"));
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                logger.error("failed to close upload file", e);
            }
        }
    }

    private InspecieTransferDtoImpl fileFormatSizeCheck(MultipartFile file, InspecieTransferDtoImpl transferDto) {
        if (isValidFileFormat(file)) {
            if (file.isEmpty()) {
                transferDto.setWarnings(addStaticError("Err.IP-0527"));
            }
        } else {
            transferDto.setWarnings(addStaticError("Err.IP-0119"));
        }
        return transferDto;
    }

    private InspecieTransferDto validate(InspecieTransferDtoImpl transferDto, List<String[]> taxParcels) {
        if (taxParcels != null && transferDto != null) {
            return validateFile(taxParcels, transferDto);
        }
        return null;
    }

    private boolean isValidFileFormat(MultipartFile file) {
        if (!(("text/csv".equals(file.getContentType()) || MediaType.TEXT_PLAIN_VALUE.equals(file.getContentType()) || "application/vnd.ms-excel"
                .equals(file.getContentType())) && FILE_EXT.equals(getFileExtension(file)))) {
            return false;
        }
        return true;
    }

    private String getFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i >= 0) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    public InspecieTransferDto validateFile(List<String[]> taxParcels, InspecieTransferDtoImpl transferDto) {

        int rowNum = 1;
        List<DomainApiErrorDto> errors = new ArrayList<>();
        for (String[] taxParcel : taxParcels) {
            // Header Row in the input file
            if (rowNum == 1 && !checkMandatoryHeaders(taxParcel)) {
                transferDto.setWarnings(addStaticError("Err.IP-0527"));
                return transferDto;
            }
            // Fields in the input file
            if (rowNum > 1) {
                boolean[] isValid = { checkMandatoryFields(taxParcel, transferDto), isValidNumberOfColumns(taxParcel), };
                if (BooleanUtils.and(isValid)) {
                    List<DomainApiErrorDto> rowErrors = new ArrayList<>();
                    errors.addAll(performFieldLevelValidations(rowNum, taxParcel, rowErrors));
                    // check for more than 100 errors
                    if (errors.size() > 100) {
                        return constructTransferDto(null, taxParcels, transferDto, errors);
                    }
                } else {
                    return populateFieldError(isValid[0], isValid[1], transferDto);
                }
            }
            rowNum++;
        }

        taxParcels.remove(0);

        assetHelper.assetRelatedCumulativeValidations(taxParcels, transferDto, errors);
        return constructTransferDto(assetHelper.constructAssetDtoMap(transferDto.getSettlementRecords()), taxParcels,
                transferDto, errors);
    }

    private InspecieTransferDtoImpl constructTransferDto(Map<String, SettlementRecordDto> assetCodeDtoMap,
            List<String[]> taxParcels, InspecieTransferDtoImpl transferDto, List<DomainApiErrorDto> errors) {
        if (errors.isEmpty()) {

            List<TaxParcelDto> taxParcelsDto = new ArrayList<>();
            for (String[] taxParcel : taxParcels) {
                TaxParcelDto taxParcelDto = new TaxParcelDto(taxParcel[0].trim().toLowerCase(), getDate(taxParcel[1]), null,
                        removeComma(taxParcel[2]), removeComma(taxParcel[3]), chkReducedCostBase(taxParcel),
                        chkIndexedCostBase(taxParcel));
                taxParcelsDto.add(taxParcelDto);

            }
            for (TaxParcelDto taxParcelDto : taxParcelsDto) {
                if (assetCodeDtoMap.get(taxParcelDto.getAssetCode()) != null) {
                    taxParcelDto.setAssetId(assetCodeDtoMap.get(taxParcelDto.getAssetCode()).getAssetId());
                }

            }
            transferDto.setTaxParcels(taxParcelsDto);
        } else {
            transferDto.setWarnings(errors);
        }
        return transferDto;
    }

    private boolean isValidNumberOfColumns(String[] fields) {
        if (getDate(fields[1]) != null) {
            if (withinDateRange(fields)) {
                return !(fields.length > 6);

            } else {
                return !(indexedCostBasePresent(fields));
            }
        }
        return true;

    }

    private boolean withinDateRange(String[] fields) {
        return getDate(fields[1]).isAfter(getDate(START_DATE)) && getDate(fields[1]).isBefore(getDate(END_DATE));
    }

    private boolean indexedCostBasePresent(String[] fields) {
        return (fields.length > 5) && (fields[5] != null) && (!fields[5].trim().isEmpty());
    }

    private List<DomainApiErrorDto> addStaticError(String errorCode) {
        List<DomainApiErrorDto> errors = new ArrayList<>();
        errors.add(new DomainApiErrorDto(errorCode, null, null, cmsService.getContent(errorCode), ErrorType.ERROR));
        return errors;
    }

    private InspecieTransferDto populateFieldError(boolean hasMandatoryFields, boolean hasValidNoOfColumns,
            InspecieTransferDtoImpl transferDto) {
        List<DomainApiErrorDto> errors = new ArrayList<>();
        if (!hasMandatoryFields) {
            DomainApiErrorDto error = new DomainApiErrorDto("Err.IP-0519", null, null, cmsService.getContent("Err.IP-0519"),
                    ErrorType.ERROR);
            errors.add(error);
        }
        if (!hasValidNoOfColumns) {
            DomainApiErrorDto error = new DomainApiErrorDto("Err.IP-0546", null, null, cmsService.getContent("Err.IP-0546"),
                    ErrorType.ERROR);
            errors.add(error);
        }
        transferDto.setWarnings(errors);
        return transferDto;
    }

    private List<DomainApiErrorDto> performFieldLevelValidations(int rowNum, String[] line, List<DomainApiErrorDto> errors) {

        if (!checkRelevanceDate(line[1])) {
            String[] params = new String[2];
            params[0] = line[0];
            params[1] = String.valueOf(rowNum);
            errors.add(new DomainApiErrorDto("Err.IP-0524", null, null, cmsService.getDynamicContent("Err.IP-0524", params),
                    ErrorType.ERROR));
        }

        isIndexedCostBaseRequired(line, rowNum, errors);

        return errors;
    }

    private boolean isIndexedCostBaseRequired(String[] line, int rowNum, List<DomainApiErrorDto> errors) {
        String relevanceDate = line[1];
        boolean result = false;
        if (getDate(relevanceDate).isAfter(getDate(START_DATE)) && getDate(relevanceDate).isBefore(getDate(END_DATE))) {
            if (line.length == 6 && line[5] != null && !line[5].trim().isEmpty()) {
                result = true;
            } else {
                String[] params = new String[2];
                params[0] = line[0];
                params[1] = String.valueOf(rowNum);
                errors.add(new DomainApiErrorDto("Err.IP-0523", null, null, cmsService.getDynamicContent("Err.IP-0523", params),
                        ErrorType.ERROR));
                result = false;
            }
        }
        return result;

    }

    private boolean checkRelevanceDate(String relevanceDate) {
        return getDate(relevanceDate).isBefore(new DateTime(new Date()));
    }

    private boolean checkMandatoryHeaders(String[] headers) {
        boolean result = false;

        if (headers.length < 5) {
            return result;
        }

        String assetHeader = headers[0];
        String relevanceDate = headers[1];
        String quantity = headers[2];
        String costBase = headers[3];
        String reducedCostBase = headers[4];
        boolean[] headersArray = { assetHeader.trim().equalsIgnoreCase(ASSET_HEADER),
                relevanceDate.trim().equalsIgnoreCase(TAX_RELEVANT_DATE_HEADER),
                quantity.trim().equalsIgnoreCase(QUANTITY_HEADER), costBase.trim().equalsIgnoreCase(IMPORT_COST_BASE_HEADER),
                reducedCostBase.trim().equalsIgnoreCase(IMPORT_REDUCED_COST_BASE_HEADER), };
        return BooleanUtils.and(headersArray);

    }

    private boolean checkMandatoryFields(String[] fields, InspecieTransferDtoImpl transferDto) {
        String asset = fields[0];
        String relevanceDate = fields[1];
        String quantity = fields[2];
        String costBase = fields[3];
        String reducedCostBase = fields[4];

        boolean[] fieldsArray = { asset != null, relevanceDate != null, quantity != null, costBase != null, };

        if (BooleanUtils.and(fieldsArray) && reducedCostBaseValidation(fields, transferDto)) {
            return checkValidFormats(asset, relevanceDate, quantity, costBase) && checkReducedCostBaseFormat(reducedCostBase);
        } else {
            return false;
        }

    }

    private boolean checkReducedCostBaseFormat(String reducedCostBase) {
        if (reducedCostBase != null && !reducedCostBase.trim().isEmpty()) {
            return isValidNumber(reducedCostBase);
        }
        return true;

    }

    private boolean reducedCostBaseValidation(String[] fields, InspecieTransferDtoImpl transferDto) {
        List<String> assetIds = Lambda.extract(transferDto.getSettlementRecords(), Lambda.on(SettlementRecordDto.class)
                .getAssetId());
        return assetHelper.chkRevenueAssetIdentifier(fields, assetIds);
    }

    public boolean checkValidFormats(String asset, String relevanceDate, String quantity, String costBase) {
        boolean[] checks = { !asset.trim().isEmpty(), isValidDate(relevanceDate), isValidNumber(quantity),
                isValidNumber(costBase), };

        return BooleanUtils.and(checks);

    }

    public boolean isValidDate(String value) {
        return getDate(value) != null;
    }

    private DateTime getDate(String value) {
        Date date = null;
        DateTime dateTime = null;
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);

            if (checkIfDateStringIsValid(value)) {
                String validDateString = addPrefixZero(value);
                date = sdf.parse(validDateString);
                dateTime = new DateTime(date);
                if (!validDateString.equals(sdf.format(date))) {
                    dateTime = null;
                }
            }

        } catch (ParseException ex) {
            logger.error("failed to parse upload tax parcels relevance date:" + value);
        } catch (java.text.ParseException e) {
            logger.error("failed to parse upload tax parcels relevance date:" + value);
        }
        return dateTime;
    }

    private String addPrefixZero(String value) {
        String[] tokens = value.split(DELIM);
        checkIfPrefixRequired(tokens);
        return StringUtils.join(tokens, DELIM);
    }

    private void checkIfPrefixRequired(String[] tokens) {
        for (int i = 0; i <= 1; i++) {
            if (Integer.parseInt(tokens[i]) < 10 && tokens[i].length() < 2) {
                tokens[i] = PREFIX.concat(tokens[i]);
            }
        }
    }

    private boolean checkIfDateStringIsValid(String value) {
        if (value.trim().isEmpty())
            return false;
        String regex = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((18|19|20|21)\\d\\d)";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(value).matches();
    }

    public boolean isValidNumber(String number) {
        if (number.trim().isEmpty())
            return false;
        String regex = "^[0-9]\\d*(\\.\\d+)?$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(number.replace(",", "")).matches();

    }

    private BigDecimal removeComma(String numericData) {
        return new BigDecimal(numericData.replace(",", ""));
    }

    private BigDecimal chkIndexedCostBase(String[] taxParcel) {
        BigDecimal indexedCostBase = null;
        if (taxParcel.length > 5 && taxParcel[5] != null && !taxParcel[5].trim().isEmpty()) {
            indexedCostBase = removeComma(taxParcel[5]);
        }
        return indexedCostBase;
    }

    private BigDecimal chkReducedCostBase(String[] taxParcel) {
        BigDecimal reducedCostBase = null;
        if (taxParcel.length > 4 && taxParcel[4] != null && !taxParcel[4].trim().isEmpty()) {
            reducedCostBase = removeComma(taxParcel[4]);
        }
        return reducedCostBase;
    }

}