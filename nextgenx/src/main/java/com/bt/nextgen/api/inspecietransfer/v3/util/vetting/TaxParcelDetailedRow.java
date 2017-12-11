package com.bt.nextgen.api.inspecietransfer.v3.util.vetting;

import com.bt.nextgen.api.inspecietransfer.v3.util.TaxParcelUploadUtil;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.service.integration.transfer.TransferType;
import org.apache.commons.lang3.BooleanUtils;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;

// Manages validation of extra rows required for NCBO transfer
public class TaxParcelDetailedRow extends TaxParcelRow {

    private static final String ERROR_ICB_REQUIRED = "Err.IP-0523";
    private static final String ERROR_DATE_FORMAT = "Err.IP-0700";
    private static final String ERROR_FUTURE_DATED = "Err.IP-0701";
    private static final String ERROR_CB_FORMAT = "Err.IP-0702";
    private static final String ERROR_CB_COMBINATION = "Err.IP-0703";
    private static final String ERROR_REVENUE_ASSET_OCB = "Err.IP-0706";
    private static final String ERROR_ICB_NOT_REQUIRED = "Err.IP-0707";
    private static final String WARNING_REDUCED_COST_BASE_VALUE = "Err.IP-0828";
    private static final String TAX_START_DATE = "1985-09-19";
    private static final String TAX_END_DATE = "1999-09-21";
    private static final int NCBO_COLUMN_COUNT = 11;

    private DateTime date;
    private String ocb;
    private String cb;
    private String rcb;
    private String icb;
    private DateTime avaloqBankDate;

    public TaxParcelDetailedRow(List<String> row, int rowNumber, TaxParcelUploadUtil util, DateTime avaloqBankDate) {
        super(row, rowNumber, util);

        if (isValidRowArray(row)) {
            this.date = getDateIfValid(row.get(TaxParcelColumn.ACQUISITION_DATE.getColumnIndex()));
            this.ocb = row.get(TaxParcelColumn.ORIGINAL_COST_BASE.getColumnIndex());
            this.cb = row.get(TaxParcelColumn.CGT_COST_BASE.getColumnIndex());
            this.rcb = row.get(TaxParcelColumn.REDUCED_COST_BASE.getColumnIndex());
            this.icb = row.get(TaxParcelColumn.INDEXED_COST_BASE.getColumnIndex());
        }
        this.avaloqBankDate = avaloqBankDate;
    }

    @Override
    protected boolean isValidRowArray(List<String> row) {
        return row != null && row.size() == NCBO_COLUMN_COUNT;
    }

    private DateTime getDateIfValid(String value) {
        String validFormat = "^\\d{4}-\\d{1,2}-\\d{1,2}$";
        if (value != null && value.matches(validFormat)) {
            return new DateTime(value);
        }
        return null;
    }

    public DateTime getAcquisitionDate() {
        return date;
    }

    public String getOriginalCostBase() {
        return ocb;
    }

    public String getCostBase() {
        return cb;
    }

    public String getReducedCostBase() {
        return rcb;
    }

    public String getIndexedCostBase() {
        return icb;
    }

    @Override
    public boolean validate(List<DomainApiErrorDto> errors, TransferType transferType) {
        super.validate(errors, transferType);

        validateDateMandatory(errors);
        validateDateNotFutureDated(errors);
        validateCostBaseFormat(errors);
        validateCostBaseCombination(errors);
        validateCostBaseIcbRequired(errors);
        validateCostBaseIcbNotRequired(errors);
        validateCostBaseRevenueAssetUsesOcb(errors);
        validateCostBaseValueSizeComparison(errors);

        return errors.isEmpty();
    }

    /**
     * Acquisition Date must always be provided. NCBO only.
     * 
     * @param errors
     */
    protected void validateDateMandatory(List<DomainApiErrorDto> errors) {
        if (getAssetCode() != null && date == null) {
            String[] params = { getAssetCode(), };
            errors.add(getUtil().getError(ERROR_DATE_FORMAT, params));
        }
    }

    /**
     * Acquisition Date must be not be after the current bank date. NCBO only.
     * 
     * @param errors
     */
    protected void validateDateNotFutureDated(List<DomainApiErrorDto> errors) {
        if (getAssetCode() != null && date != null && date.isAfter(avaloqBankDate)) {
            String[] params = { getAssetCode(), };
            errors.add(getUtil().getError(ERROR_FUTURE_DATED, params));
        }
    }

    /**
     * Any values entered into the four cost base columns must be valid numbers. NCBO only.
     * 
     * @param errors
     */
    protected void validateCostBaseFormat(List<DomainApiErrorDto> errors) {
        boolean[] costBaseFormatConditions = { isValidCostBaseFormat(ocb), isValidCostBaseFormat(cb), isValidCostBaseFormat(rcb),
                isValidCostBaseFormat(icb), };

        if (getAssetCode() != null && !BooleanUtils.and(costBaseFormatConditions)) {
            String[] params = { getAssetCode(), };
            errors.add(getUtil().getError(ERROR_CB_FORMAT, params));
        }
    }

    /**
     * A valid cost base number is a whole positive number up to 2dp: eg 10 or 10.12, not -10 or -10.123. NCBO only.
     * 
     * @param number
     * @return true if valid
     */
    protected boolean isValidCostBaseFormat(String number) {
        if (number != null) {
            String regex = "^\\d*(\\.\\d{1,2})?$";
            Pattern pattern = Pattern.compile(regex);
            return pattern.matcher(number.replace(",", "")).matches();
        }
        return true;
    }

    /**
     * Acceptable combinations are OCB only, CB only or CB and RCB. NCBO only.
     * 
     * @param errors
     */
    protected void validateCostBaseCombination(List<DomainApiErrorDto> errors) {
        boolean requiredCostBaseMissing = ocb == null && cb == null;
        boolean invalidCombination = ocb != null && (cb != null || rcb != null);

        if (requiredCostBaseMissing || invalidCombination) {
            String[] params = { getAssetCode(), getRowNumber() };
            errors.add(getUtil().getError(ERROR_CB_COMBINATION, params));
        }
    }

    /**
     * ICB mandatory if relevant and OCB not provided. NCBO only.
     * 
     * @param errors
     */
    protected void validateCostBaseIcbRequired(List<DomainApiErrorDto> errors) {
        if (getAssetCode() != null && ocb == null && isIndexedCbRelevant() && icb == null) {
            String[] params = { getAssetCode(), getRowNumber() };
            errors.add(getUtil().getError(ERROR_ICB_REQUIRED, params));
        }
    }

    /**
     * ICB not required if not relevant. NCBO only.
     * 
     * @param errors
     */
    protected void validateCostBaseIcbNotRequired(List<DomainApiErrorDto> errors) {
        if (getAssetCode() != null && !isIndexedCbRelevant() && icb != null) {
            String[] params = { getAssetCode(), };
            errors.add(getUtil().getError(ERROR_ICB_NOT_REQUIRED, params));
        }
    }

    /**
     * ICB is relevant if acquisition date for asset is within the period 1985-09-20 to 1999-09-20 inclusive. NCBO only.
     * 
     * @return
     */
    protected boolean isIndexedCbRelevant() {
        DateTime start = new DateTime(TAX_START_DATE);
        DateTime end = new DateTime(TAX_END_DATE);
        if (date != null && date.isAfter(start) && date.isBefore(end)) {
            return true;
        }
        return false;
    }

    /**
     * If asset is a revenue asset, it must use the OCB column. NCBO only.
     * 
     * @param errors
     */
    protected void validateCostBaseRevenueAssetUsesOcb(List<DomainApiErrorDto> errors) {
        if (getAssetCode() != null && getAsset() != null && getAsset().getRevenueAssetIndicator() != null && ocb == null) {
            String[] params = { getAssetCode(), };
            errors.add(getUtil().getError(ERROR_REVENUE_ASSET_OCB, params));
        }
    }

    /**
     * Create a warning for each row where the Reduced-cost-base > cost-base.
     * 
     * @param errors
     */
    protected void validateCostBaseValueSizeComparison(List<DomainApiErrorDto> errors) {
        if (cb != null && isValidCostBaseFormat(cb) && rcb != null && isValidCostBaseFormat(rcb)) {
            BigDecimal cbValue = new BigDecimal(cb);
            BigDecimal rcbValue = new BigDecimal(rcb);
            if (rcbValue.compareTo(cbValue) > 0) {
                String[] params = { getAssetCode(), getRowNumber() };
                errors.add(getUtil().getWarning(WARNING_REDUCED_COST_BASE_VALUE, params));
            }
        }
    }
}
