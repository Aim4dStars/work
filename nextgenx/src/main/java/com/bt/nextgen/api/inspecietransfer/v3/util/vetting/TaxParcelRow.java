package com.bt.nextgen.api.inspecietransfer.v3.util.vetting;

import com.bt.nextgen.api.inspecietransfer.v3.util.TaxParcelUploadUtil;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.service.integration.asset.Asset;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.ShareAsset;
import com.bt.nextgen.service.integration.transfer.TransferType;

import java.util.List;

// Manages validation of basic columns required for both CBO and NCBO transfers
public class TaxParcelRow {

    private static final String ERROR_ASSET_NOT_IN_UNIVERSE = "Err.IP-0698";
    private static final String WARNING_WITHOLDING_TAX = "Err.IP-0699";
    private static final String ERROR_HIN_FORMAT = "Err.IP-0709";
    private static final String WARNING_BROKER_HAS_CUSTODIAN = "Err.IP-0713";
    private static final String ERROR_SRN_FORMAT = "Err.IP-0715";
    private static final String ERROR_QUANTITY_FORMAT = "Err.IP-0716";
    private static final String WARNING_ISSUER_HAS_CUSTODIAN = "Err.IP-0719";
    private static final String ERROR_MANDATORY_FIELD_MISSING = "Err.IP-0722";
    private static final String ERROR_MF_QUANTITY_FORMAT = "Err.IP-0723";
    private static final String ERROR_WRONG_ASSET_TYPE = "Err.IP-0732";
    private static final int CBO_COLUMN_COUNT = 4;

    private Asset asset;
    private String assetCode;
    private String quantity;
    private String owner;
    private String custodian;
    private int rowNumber;
    private TaxParcelUploadUtil util;

    public TaxParcelRow(List<String> row, int rowNumber, TaxParcelUploadUtil util) {
        if (isValidRowArray(row)) {
            this.assetCode = toSafeUpperCase(row.get(TaxParcelColumn.ASSET_CODE.getColumnIndex()));
            this.quantity = row.get(TaxParcelColumn.QUANTITY.getColumnIndex());
            this.owner = toSafeUpperCase(row.get(TaxParcelColumn.OWNER.getColumnIndex()));
            this.custodian = row.get(TaxParcelColumn.CUSTODIAN.getColumnIndex());
        }
        this.rowNumber = rowNumber;
        this.util = util;
    }

    private String toSafeUpperCase(String string) {
        if (string != null) {
            return string.toUpperCase();
        }
        return null;
    }

    protected boolean isValidRowArray(List<String> row) {
        return row != null && row.size() == CBO_COLUMN_COUNT;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getOwner() {
        return owner;
    }

    public String getCustodian() {
        return custodian;
    }

    public String getRowNumber() {
        return Integer.toString(rowNumber);
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public TaxParcelUploadUtil getUtil() {
        return util;
    }

    public boolean validate(List<DomainApiErrorDto> errors, TransferType transferType) {
        validateAssetExists(errors);
        validateAssetIncursWitholdingTax(errors);

        if (TransferType.LS_BROKER_SPONSORED.equals(transferType)) {
            validateMandatory(errors, false);
            validateAssetAsxCode(errors);
            validateShareQuantityFormat(errors);
            validateOwnerHinFormat(errors);
            validateCustodianNotRequiredForBroker(errors);
        }

        if (TransferType.LS_ISSUER_SPONSORED.equals(transferType)) {
            validateMandatory(errors, false);
            validateAssetAsxCode(errors);
            validateShareQuantityFormat(errors);
            validateOwnerSrnFormat(errors);
            validateCustodianNotRequiredForIssuer(errors);
        }

        if (TransferType.MANAGED_FUND.equals(transferType)) {
            validateMandatory(errors, true);
            validateAssetIsFund(errors);
            validateMFQuantityFormat(errors);
        }

        if (TransferType.OTHER_PLATFORM.equals(transferType)) {
            validateMandatory(errors, true);
            validateAssetIsFundOrAsxCode(errors);
            validateQuantityFormat(errors);
        }

        return errors.isEmpty();
    }

    /**
     * Asset code, quantity and owner must always be provided. Custodian is only mandatory for some types of transfer.
     * 
     * @param errors
     * @param validateCustodian
     */
    protected void validateMandatory(List<DomainApiErrorDto> errors, boolean validateCustodian) {
        boolean basicMissing = assetCode == null || quantity == null || owner == null;
        boolean custodianMissing = validateCustodian && custodian == null;
        if (basicMissing || custodianMissing) {
            String[] params = { getRowNumber(), };
            errors.add(util.getError(ERROR_MANDATORY_FIELD_MISSING, params));
        }
    }

    /**
     * Asset code provided must be found in asset universe
     * 
     * @param errors
     */
    protected void validateAssetExists(List<DomainApiErrorDto> errors) {
        if (assetCode != null && asset == null) {
            String[] params = { assetCode, };
            errors.add(util.getError(ERROR_ASSET_NOT_IN_UNIVERSE, params));
        }
    }

    /**
     * Warn that assets paying foreign income may need extra documentation to be filled out
     * 
     * @param errors
     */
    protected void validateAssetIncursWitholdingTax(List<DomainApiErrorDto> errors) {
        if (assetCode != null && asset != null && AssetType.SHARE.equals(asset.getAssetType())) {
            Boolean taxAsset = ((ShareAsset) asset).getTaxAssetDomicile();
            if (taxAsset != null && taxAsset) {
                String[] params = { assetCode, };
                errors.add(util.getWarning(WARNING_WITHOLDING_TAX, params));
            }
        }
    }

    protected void validateAssetAsxCode(List<DomainApiErrorDto> errors) {
        if (assetCode != null && !isValidAsx()) {
            String[] params = { assetCode, getRowNumber(), };
            errors.add(util.getError(ERROR_WRONG_ASSET_TYPE, params));
        }
    }

    protected void validateAssetIsFund(List<DomainApiErrorDto> errors) {
        if (assetCode != null && !isValidMF()) {
            String[] params = { assetCode, getRowNumber(), };
            errors.add(util.getError(ERROR_WRONG_ASSET_TYPE, params));
        }
    }

    protected void validateAssetIsFundOrAsxCode(List<DomainApiErrorDto> errors) {
        if (assetCode != null && !isValidMF() && !isValidAsx()) {
            String[] params = { assetCode, getRowNumber(), };
            errors.add(util.getError(ERROR_WRONG_ASSET_TYPE, params));
        }
    }

    /**
     * Validate format of ASX code (allows shares (WBC), bonds (WBCHA), options (WBCR))
     * 
     * @return true if valid
     */
    protected boolean isValidAsx() {
        String validFormat = "^[a-zA-Z0-9]{3,6}$";
        return assetCode != null && assetCode.matches(validFormat);
    }

    /**
     * Validate asset is specifically a managed fund
     * 
     * @return true if valid
     */
    protected boolean isValidMF() {
        return asset != null && AssetType.MANAGED_FUND.equals(asset.getAssetType());
    }

    /**
     * Share quantity is a whole positive number: eg 10, not -10 or 10.1
     * 
     * @param errors
     */
    protected void validateShareQuantityFormat(List<DomainApiErrorDto> errors) {
        String validFormat = "^\\d*$";
        if (assetCode != null && quantity != null && !quantity.matches(validFormat)) {
            String[] params = { assetCode, getRowNumber(), };
            errors.add(util.getError(ERROR_QUANTITY_FORMAT, params));
        }
    }

    /**
     * MF quantity is positive number up to 8dp: eg 10 or 10.1234, not -10 or -10.123456789
     * 
     * @param errors
     */
    protected void validateMFQuantityFormat(List<DomainApiErrorDto> errors) {
        String validFormat = "^\\d*(\\.\\d{1,8})?$";
        if (assetCode != null && quantity != null && !quantity.matches(validFormat)) {
            String[] params = { assetCode, getRowNumber(), };
            errors.add(util.getError(ERROR_MF_QUANTITY_FORMAT, params));
        }
    }

    /**
     * Validate the given quantities are valid for the asset type found in that row
     * 
     * @param errors
     */
    protected void validateQuantityFormat(List<DomainApiErrorDto> errors) {
        if (isValidAsx()) {
            validateShareQuantityFormat(errors);
        } else if (isValidMF()) {
            validateMFQuantityFormat(errors);
        }
    }

    /**
     * HIN format must be an X followed by ten digits
     * 
     * @param errors
     */
    protected void validateOwnerHinFormat(List<DomainApiErrorDto> errors) {
        String validFormat = "^[xX]\\d{10}$";
        if (assetCode != null && owner != null && !owner.matches(validFormat)) {
            String[] params = { assetCode, getRowNumber(), };
            errors.add(util.getError(ERROR_HIN_FORMAT, params));
        }
    }

    /**
     * SRN format must be an I followed by up to 15 digits
     * 
     * @param errors
     */
    protected void validateOwnerSrnFormat(List<DomainApiErrorDto> errors) {
        String validFormat = "^[iI]\\d{1,15}$";
        if (assetCode != null && owner != null && !owner.matches(validFormat)) {
            String[] params = { assetCode, getRowNumber(), };
            errors.add(util.getError(ERROR_SRN_FORMAT, params));
        }
    }

    /**
     * Warn once only that the custodian isn't required for a LS broker sponsored transfer
     * 
     * @param errors
     */
    protected void validateCustodianNotRequiredForBroker(List<DomainApiErrorDto> errors) {
        if (custodian != null) {
            DomainApiErrorDto warning = util.getWarning(WARNING_BROKER_HAS_CUSTODIAN);
            if (!errors.contains(warning)) {
                errors.add(warning);
            }
        }
    }

    /**
     * Warn once only that the custodian isn't required for a LS issuer sponsored transfer
     * 
     * @param errors
     */
    protected void validateCustodianNotRequiredForIssuer(List<DomainApiErrorDto> errors) {
        if (custodian != null) {
            DomainApiErrorDto warning = util.getWarning(WARNING_ISSUER_HAS_CUSTODIAN);
            if (!errors.contains(warning)) {
                errors.add(warning);
            }
        }
    }
}
