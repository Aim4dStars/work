package com.bt.nextgen.api.inspecietransfer.v3.util.vetting;

import com.bt.nextgen.api.inspecietransfer.v3.util.TaxParcelUploadUtil;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

//Manages validation of basic column headers required for both CBO and NCBO transfers
public class TaxParcelHeader {

    private static final String ERROR_INVALID_CBO_TEMPLATE = "Err.IP-0695";
    private static final int CBO_EXPECTED_COLUMN_COUNT = 4;
    private static final int CBO_EXPECTED_FILLED_COLUMN_COUNT = 4;

    private String assetHeader;
    private String quantityHeader;
    private String ownerHeader;
    private String custodianHeader;
    private TaxParcelUploadUtil util;

    public TaxParcelHeader(List<String> header, TaxParcelUploadUtil util) {
        if (isValidHeaderArray(header)) {
            this.assetHeader = header.get(TaxParcelColumn.ASSET_CODE.getColumnIndex());
            this.quantityHeader = header.get(TaxParcelColumn.QUANTITY.getColumnIndex());
            this.ownerHeader = header.get(TaxParcelColumn.OWNER.getColumnIndex());
            this.custodianHeader = header.get(TaxParcelColumn.CUSTODIAN.getColumnIndex());
        }
        this.util = util;
    }

    protected boolean isValidHeaderArray(List<String> header) {
        if (header != null) {
            List<String> processedHeader;

            // Ignore values outside expected range
            if (header.size() > getExpectedColumnCount()) {
                processedHeader = header.subList(0, getExpectedColumnCount());
            } else {
                processedHeader = header;
            }

            if (processedHeader.size() == getExpectedColumnCount()) {
                // Ignore values in columns expected to be blank
                for (Integer index : getExpectedBlankColumnIndices()) {
                    processedHeader.set(index, null);
                }

                // Check the right number of columns are filled
                int actualHeadingsCount = 0;
                for (String heading : processedHeader) {
                    if (heading != null) {
                        actualHeadingsCount++;
                    }
                }
                return actualHeadingsCount == getExpectedFilledColumnCount();
            }
        }
        return false;
    }

    public String getAssetHeader() {
        return assetHeader;
    }

    public String getQuantityHeader() {
        return quantityHeader;
    }

    public String getOwnerHeader() {
        return ownerHeader;
    }

    public String getCustodianHeader() {
        return custodianHeader;
    }

    public TaxParcelUploadUtil getUtil() {
        return util;
    }

    public boolean validate(List<DomainApiErrorDto> errors) {
        boolean[] headerConditions = { headerMatches(TaxParcelColumn.ASSET_CODE.getColumnHeader(), assetHeader),
                headerMatches(TaxParcelColumn.QUANTITY.getColumnHeader(), quantityHeader),
                headerMatches(TaxParcelColumn.OWNER.getColumnHeader(), ownerHeader),
                headerMatches(TaxParcelColumn.CUSTODIAN.getColumnHeader(), custodianHeader), };

        if (!BooleanUtils.and(headerConditions)) {
            errors.add(getTemplateError());
        }
        return errors.isEmpty();
    }

    protected boolean headerMatches(String expected, String actual) {
        if (StringUtils.isNotBlank(actual)) {
            return StringUtils.trim(actual).equals(expected);
        }
        return false;
    }

    protected DomainApiErrorDto getTemplateError() {
        return util.getError(ERROR_INVALID_CBO_TEMPLATE);
    }

    protected int getExpectedColumnCount() {
        return CBO_EXPECTED_COLUMN_COUNT;
    }

    protected int getExpectedFilledColumnCount() {
        return CBO_EXPECTED_FILLED_COLUMN_COUNT;
    }

    protected List<Integer> getExpectedBlankColumnIndices() {
        return Collections.emptyList();
    }
}
