package com.bt.nextgen.api.inspecietransfer.v3.util.vetting;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.SettlementRecordDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Check vetting rules which only apply when uploading tax parcels for an existing transfer
public class TaxParcelIndependentUploadFile extends TaxParcelUploadFile {

    private static final String ERROR_ASSET_NOT_IN_ORDER = "Err.IP-0821";
    private static final String ERROR_ASSET_NOT_UPLOADED = "Err.IP-0822";
    private static final String ERROR_QUANTITY_MISMATCH = "Err.IP-0522";
    private static final String ERROR_OCB_WAS_ZERO = "Err.IP-0823";
    private static final String WARNING_CUSTODIAN_DIFFERENT = "Err.IP-0868";
    private static final String WARNING_OWNER_DIFFERENT = "Err.IP-0869";

    public TaxParcelIndependentUploadFile(TaxParcelUploadFileBuilder builder) {
        super(builder);
    }

    public boolean validate(InspecieTransferDto transferDto, List<DomainApiErrorDto> errors) {
        if (super.validate(errors)) {
            boolean uploadMatchesOrder = validateUploadedAssetsExistInOrder(transferDto, errors);
            boolean orderMatchesUpload = validateOrderAssetsExistInUpload(transferDto, errors);

            if (uploadMatchesOrder && orderMatchesUpload) {
                validateQuantitiesAgainstOrder(transferDto, errors);
                validateUploadedOwnerMatchesOrder(transferDto, errors);
                validateUploadedCustodianMatchesOrder(transferDto, errors);
            }
        }
        return util.containsValidationWarningOnly(errors);
    }

    /**
     * All assets uploaded must exist in submitted transfer
     * 
     * @param transferOrder
     *            details of submitted transfer
     * @param errors
     */
    protected boolean validateUploadedAssetsExistInOrder(InspecieTransferDto transferDto, List<DomainApiErrorDto> errors) {
        boolean assetsConsistent = true;
        Set<String> assets = new HashSet<>(Lambda.extract(transferDto.getSettlementRecords(), Lambda
                .on(SettlementRecordDto.class)
                .getAssetCode()));

        for (TaxParcelRow row : getRows()) {
            if (!assets.contains(row.getAssetCode())) {
                String[] params = { row.getAssetCode(), row.getRowNumber(), };
                errors.add(util.getError(ERROR_ASSET_NOT_IN_ORDER, params));
                assetsConsistent = false;

                // Prevent multiple errors being raised for one missing asset code
                assets.add(row.getAssetCode());
            }
        }
        return assetsConsistent;
    }

    /**
     * All assets in submitted transfer must have corresponding cost base information
     * 
     * @param transferOrder
     *            details of submitted transfer
     * @param errors
     */
    protected boolean validateOrderAssetsExistInUpload(InspecieTransferDto transferDto, List<DomainApiErrorDto> errors) {
        boolean assetsConsistent = true;
        Set<String> assetsInOrder = new HashSet<>(Lambda.extract(transferDto.getSettlementRecords(),
                Lambda.on(SettlementRecordDto.class).getAssetCode()));
        Set<String> assetsInUpload = new HashSet<>(Lambda.extract(getRows(), Lambda.on(TaxParcelRow.class).getAssetCode()));

        for (String assetCode : assetsInOrder) {
            if (!assetsInUpload.contains(assetCode)) {
                String[] params = { assetCode, };
                errors.add(util.getError(ERROR_ASSET_NOT_UPLOADED, params));
                assetsConsistent = false;
            }
        }
        return assetsConsistent;
    }

    /**
     * Total quantity for each asset in submitted order must match the uploaded cost base information
     * 
     * @param transferOrder
     *            details of submitted transfer
     * @param errors
     */
    protected void validateQuantitiesAgainstOrder(InspecieTransferDto transferDto, List<DomainApiErrorDto> errors) {
        Map<String, BigDecimal> assetQuantityMap = new HashMap<>();
        for (TaxParcelRow row : getRows()) {
            BigDecimal assetTotal = assetQuantityMap.get(row.getAssetCode());
            if (assetTotal == null) {
                assetQuantityMap.put(row.getAssetCode(), new BigDecimal(row.getQuantity()));
            } else {
                assetTotal = assetTotal.add(new BigDecimal(row.getQuantity()));
                assetQuantityMap.put(row.getAssetCode(), assetTotal);
            }
        }

        for (SettlementRecordDto record : transferDto.getSettlementRecords()) {
            BigDecimal uploadedQuantity = assetQuantityMap.get(record.getAssetCode());
            if (!uploadedQuantity.equals(record.getQuantity())) {
                String[] params = { record.getAssetCode(), };
                errors.add(util.getError(ERROR_QUANTITY_MISMATCH, params));
            }
        }
    }

    /**
     * Original cost base with zero value not allowed. Runs as part of vetting gate 2
     * 
     * @param errors
     */
    @Override
    protected void validateOriginalCostBaseValue(List<DomainApiErrorDto> errors) {
        for (TaxParcelRow row : getRows()) {
            TaxParcelDetailedRow dRow = (TaxParcelDetailedRow) row;
            String strOcb = dRow.getOriginalCostBase();
            if (strOcb != null && BigDecimal.ZERO.equals(new BigDecimal(strOcb))) {
                String[] params = { row.getAssetCode(), row.getRowNumber(), };
                errors.add(util.getError(ERROR_OCB_WAS_ZERO, params));
            }
        }
    }

    /**
     * Warn if uploaded HIN/SRN/Account number differs to that in submitted order
     * 
     */
    protected void validateUploadedOwnerMatchesOrder(InspecieTransferDto transferDto, List<DomainApiErrorDto> errors) {
        String originalOwner = getAvailableOwner(transferDto);
        for (TaxParcelRow row : getRows()) {
            if (originalOwner != null && !row.getOwner().equals(originalOwner)) {
                String[] params = { originalOwner, };
                errors.add(util.getWarning(WARNING_OWNER_DIFFERENT, params));
                break;
            }
        }
    }

    /**
     * Warn if uploaded Platform name/Custodian differs to that in submitted order
     * 
     */
    protected void validateUploadedCustodianMatchesOrder(InspecieTransferDto transferDto, List<DomainApiErrorDto> errors) {
        String originalCustodian = transferDto.getSponsorDetails().getCustodian();
        for (TaxParcelRow row : getRows()) {
            if (originalCustodian != null && !row.getCustodian().equals(originalCustodian)) {
                String[] params = { originalCustodian, };
                errors.add(util.getWarning(WARNING_CUSTODIAN_DIFFERENT, params));
                break;
            }
        }
    }

    protected String getAvailableOwner(InspecieTransferDto transferDto) {
        String owner = null;
        if (transferDto.getSponsorDetails().getHin() != null) {
            owner = transferDto.getSponsorDetails().getHin();
        } else if (transferDto.getSponsorDetails().getSrn() != null) {
            owner = transferDto.getSponsorDetails().getSrn();
        } else if (transferDto.getSponsorDetails().getAccNumber() != null) {
            owner = transferDto.getSponsorDetails().getAccNumber();
        }
        return owner;
    }
}
