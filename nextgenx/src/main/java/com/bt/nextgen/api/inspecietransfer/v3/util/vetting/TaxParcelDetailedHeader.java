package com.bt.nextgen.api.inspecietransfer.v3.util.vetting;

import com.bt.nextgen.api.inspecietransfer.v3.util.TaxParcelUploadUtil;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;

//Manages validation of extra column headers required for NCBO transfer
public class TaxParcelDetailedHeader extends TaxParcelHeader {

    private static final String ERROR_INVALID_NCBO_TEMPLATE = "Err.IP-0694";
    private static final int NCBO_EXPECTED_COLUMN_COUNT = 11;
    private static final int NCBO_EXPECTED_FILLED_COLUMN_COUNT = 9;

    private String dateHeader;
    private String ocbHeader;
    private String cbHeader;
    private String rcbHeader;
    private String icbHeader;

    public TaxParcelDetailedHeader(List<String> header, TaxParcelUploadUtil util) {
        super(header, util);

        if (isValidHeaderArray(header)) {
            this.dateHeader = header.get(TaxParcelColumn.ACQUISITION_DATE.getColumnIndex());
            this.ocbHeader = header.get(TaxParcelColumn.ORIGINAL_COST_BASE.getColumnIndex());
            this.cbHeader = header.get(TaxParcelColumn.CGT_COST_BASE.getColumnIndex());
            this.rcbHeader = header.get(TaxParcelColumn.REDUCED_COST_BASE.getColumnIndex());
            this.icbHeader = header.get(TaxParcelColumn.INDEXED_COST_BASE.getColumnIndex());
        }
    }

    public String getDateHeader() {
        return this.dateHeader;
    }

    public String getOriginalCostBaseHeader() {
        return this.ocbHeader;
    }

    public String getCostBaseHeader() {
        return this.cbHeader;
    }

    public String getReducedCostBaseHeader() {
        return this.rcbHeader;
    }

    public String getIndexedCostBaseHeader() {
        return this.icbHeader;
    }

    @Override
    public boolean validate(List<DomainApiErrorDto> errors) {
        if (super.validate(errors)) {
            boolean[] headerConditions = { headerMatches(TaxParcelColumn.ACQUISITION_DATE.getColumnHeader(), dateHeader),
                    headerMatches(TaxParcelColumn.ORIGINAL_COST_BASE.getColumnHeader(), ocbHeader),
                    headerMatches(TaxParcelColumn.CGT_COST_BASE.getColumnHeader(), cbHeader),
                    headerMatches(TaxParcelColumn.REDUCED_COST_BASE.getColumnHeader(), rcbHeader),
                    headerMatches(TaxParcelColumn.INDEXED_COST_BASE.getColumnHeader(), icbHeader), };

            if (!BooleanUtils.and(headerConditions)) {
                errors.add(getTemplateError());
            }
        }
        return errors.isEmpty();
    }

    @Override
    protected DomainApiErrorDto getTemplateError() {
        return getUtil().getError(ERROR_INVALID_NCBO_TEMPLATE);
    }

    @Override
    protected int getExpectedColumnCount() {
        return NCBO_EXPECTED_COLUMN_COUNT;
    }

    @Override
    protected int getExpectedFilledColumnCount() {
        return NCBO_EXPECTED_FILLED_COLUMN_COUNT;
    }

    @Override
    protected List<Integer> getExpectedBlankColumnIndices() {
        return TaxParcelColumn.getBlankColumnIndices();
    }
}
