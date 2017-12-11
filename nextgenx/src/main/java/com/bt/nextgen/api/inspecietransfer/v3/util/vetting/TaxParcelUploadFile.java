package com.bt.nextgen.api.inspecietransfer.v3.util.vetting;

import ch.lambdaj.Lambda;
import ch.lambdaj.group.Group;
import com.bt.nextgen.api.inspecietransfer.v3.util.TaxParcelUploadUtil;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.transfer.TransferType;
import org.hamcrest.core.IsNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TaxParcelUploadFile {

    private static final String ERROR_HAS_FORMULAS = "Err.IP-0696";
    private static final String ERROR_HAS_MACROS = "Err.IP-0697";
    private static final String ERROR_INCONSISTENT_OCB = "Err.IP-0704";
    private static final String ERROR_INCONSISTENT_CB = "Err.IP-0705";
    private static final String ERROR_DUPLICATE_ASSET = "Err.IP-0708";
    private static final String ERROR_INCONSISTENT_HIN = "Err.IP-0710";
    private static final String ERROR_MISSING_DATA = "Err.IP-0712";
    private static final String WARNING_DIFFERENT_SRN_FOR_ASSET = "Err.IP-0714";
    private static final String ERROR_SRN_HAS_MULTIPLE_ASSETS = "Err.IP-0718";
    private static final String ERROR_OCB_ALL_ZERO = "Err.IP-0829";
    private static final int ORIG_COST_BASE = 0;
    private static final int COST_BASE = 1;

    private TaxParcelHeader header;
    private List<TaxParcelRow> rows;
    private boolean isMacroEnabled;
    private boolean hasFormulas;
    private boolean isCbo;
    private TransferType transferType;
    private String sponsorName;
    protected TaxParcelUploadUtil util;
    private AssetIntegrationService assetService;

    public TaxParcelUploadFile(TaxParcelUploadFileBuilder builder) {
        this.header = builder.getHeader();
        this.rows = builder.getRows();
        this.isMacroEnabled = builder.getIsMacroEnabled();
        this.hasFormulas = builder.getHasFormulas();
        this.isCbo = builder.getIsCbo();
        this.transferType = builder.getTransferType();
        this.sponsorName = builder.getSponsorName();
        this.util = builder.getUtil();
        this.assetService = builder.getAssetService();
    }

    public TaxParcelHeader getHeader() {
        return header;
    }

    public List<TaxParcelRow> getRows() {
        return rows;
    }

    public boolean getIsMacroEnabled() {
        return isMacroEnabled;
    }

    public boolean getHasFormulas() {
        return hasFormulas;
    }

    public boolean getIsCbo() {
        return isCbo;
    }

    public TransferType getTransferType() {
        return transferType;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public boolean validate(List<DomainApiErrorDto> errors) {
        boolean passedFirstGate = firstValidationGate(errors);
        if (passedFirstGate) {
            secondValidationGate(errors);
        }
        return util.containsValidationWarningOnly(errors);
    }

    protected boolean firstValidationGate(List<DomainApiErrorDto> errors) {
        validateNoFormulas(errors);
        validateNoMacros(errors);
        header.validate(errors);
        return errors.isEmpty();
    }

    protected boolean secondValidationGate(List<DomainApiErrorDto> errors) {
        if (rows.isEmpty()) {
            errors.add(util.getError(ERROR_MISSING_DATA));
            return false;
        }

        Map<String, Asset> assetMap = getAssetsFromUniverse();
        boolean hasRevenueAsset = isRevenueAssetPresent(assetMap);

        if (isCbo) {
            validateAssetOwnerCombinationUnique(errors);
        } else {
            validateCostBaseConsistent(errors, hasRevenueAsset);
            validateOriginalCostBaseValue(errors);
        }

        if (TransferType.LS_BROKER_SPONSORED.equals(transferType)) {
            validateOwnerConsistentHin(errors);
        }

        if (TransferType.LS_ISSUER_SPONSORED.equals(transferType)) {
            validateWarnDifferentSrnForAsset(errors);
            validateErrorDifferentAssetForSrn(errors);
        }

        for (TaxParcelRow row : rows) {
            row.setAsset(assetMap.get(row.getAssetCode()));
            row.validate(errors, transferType);
        }
        return util.containsValidationWarningOnly(errors);
    }

    protected Map<String, Asset> getAssetsFromUniverse() {
        List<String> assetCodes = new ArrayList<>();
        for (TaxParcelRow row : rows) {
            if (row.getAssetCode() != null) {
                assetCodes.add(row.getAssetCode());
            }
        }
        List<Asset> assets = assetService.loadAssetsForAssetCodes(assetCodes, new ServiceErrorsImpl());
        Map<String, Asset> assetMap = Lambda.index(assets, Lambda.on(Asset.class).getAssetCode().toUpperCase());
        return assetMap;
    }

    protected boolean isRevenueAssetPresent(Map<String, Asset> assetMap) {
        Iterator<?> iter = assetMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Asset> entry = (Entry<String, Asset>) iter.next();
            Asset assetVal = entry.getValue();
            if (assetVal.getRevenueAssetIndicator() != null) {
                return true;
            }
        }
        return false;
    }

    protected void validateNoFormulas(List<DomainApiErrorDto> errors) {
        if (hasFormulas) {
            errors.add(util.getError(ERROR_HAS_FORMULAS));
        }
    }

    protected void validateNoMacros(List<DomainApiErrorDto> errors) {
        if (isMacroEnabled) {
            errors.add(util.getError(ERROR_HAS_MACROS));
        }
    }

    /**
     * Only one HIN is present in uploaded file
     * 
     * @param errors
     */
    protected void validateOwnerConsistentHin(List<DomainApiErrorDto> errors) {
        Set<String> hinSet = new HashSet<>();
        for (TaxParcelRow row : rows) {
            if (row.getOwner() != null) {
                hinSet.add(row.getOwner());
            }
        }

        if (hinSet.size() > 1) {
            String[] params = { sponsorName, };
            errors.add(util.getError(ERROR_INCONSISTENT_HIN, params));
        }
    }

    /**
     * Either OCB or CB&RCB columns are to be used throughout file unless a revenue asset is present. NCBO only.
     * 
     * @param errors
     * @param hasRevenueAsset
     */
    protected void validateCostBaseConsistent(List<DomainApiErrorDto> errors, boolean hasRevenueAsset) {
        if (!hasRevenueAsset) {
            boolean firstIsOcb = isCostBaseOfType(rows.get(0), ORIG_COST_BASE);
            boolean firstIsCb = isCostBaseOfType(rows.get(0), COST_BASE);

            // If the first row has valid data, apply the consistency rule based on this.
            if (firstIsOcb || firstIsCb) {
                for (TaxParcelRow row : rows) {
                    boolean ocbConsistent = checkOcbConsistent(errors, (TaxParcelDetailedRow) row, firstIsOcb);
                    boolean cbConsistent = checkCbConsistent(errors, (TaxParcelDetailedRow) row, firstIsCb);

                    // Don't add multiple errors
                    if (!ocbConsistent || !cbConsistent) {
                        break;
                    }
                }
            }
        }
    }

    protected boolean checkOcbConsistent(List<DomainApiErrorDto> errors, TaxParcelDetailedRow row, boolean firstIsOcb) {
        if (firstIsOcb && row.getCostBase() != null) {
            errors.add(util.getError(ERROR_INCONSISTENT_OCB));
            return false;
        }
        return true;
    }

    protected boolean checkCbConsistent(List<DomainApiErrorDto> errors, TaxParcelDetailedRow row, boolean firstIsCb) {
        if (firstIsCb && row.getOriginalCostBase() != null) {
            errors.add(util.getError(ERROR_INCONSISTENT_CB));
            return false;
        }
        return true;
    }

    /**
     * An asset can have multiple SRNs but user must be warned this is the case
     * 
     * @param errors
     */
    protected void validateWarnDifferentSrnForAsset(List<DomainApiErrorDto> errors) {
        Group<TaxParcelRow> groupedRows = Lambda.group(
                Lambda.select(rows, Lambda.having(Lambda.on(TaxParcelRow.class).getAssetCode(), IsNull.notNullValue())),
                Lambda.by(Lambda.on(TaxParcelRow.class).getAssetCode()));

        Iterator<String> iter = groupedRows.keySet().iterator();
        while (iter.hasNext()) {
            List<TaxParcelRow> rows = groupedRows.find(iter.next());
            Set<String> ownersForAsset = new HashSet<>(Lambda.extract(Lambda.select(rows,
                    Lambda.having(Lambda.on(TaxParcelRow.class).getOwner(), IsNull.notNullValue())),
                    Lambda.on(TaxParcelRow.class).getOwner()));

            if (ownersForAsset.size() > 1) {
                errors.add(util.getWarning(WARNING_DIFFERENT_SRN_FOR_ASSET));
                break;
            }
        }
    }

    /**
     * An SRN can never be applied to more than one asset
     * 
     * @param errors
     */
    protected void validateErrorDifferentAssetForSrn(List<DomainApiErrorDto> errors) {
        Group<TaxParcelRow> groupedRows = Lambda.group(
                Lambda.select(rows, Lambda.having(Lambda.on(TaxParcelRow.class).getOwner(), IsNull.notNullValue())),
                Lambda.by(Lambda.on(TaxParcelRow.class).getOwner()));

        Iterator<String> iter = groupedRows.keySet().iterator();
        while (iter.hasNext()) {
            List<TaxParcelRow> rows = groupedRows.find(iter.next());
            Set<String> assetsForOwner = new HashSet<>();

            for (TaxParcelRow row : rows) {
                if (assetsForOwner.add(row.getAssetCode()) && assetsForOwner.size() > 1) {
                    String[] params = { row.getAssetCode(), row.getRowNumber(), };
                    errors.add(util.getError(ERROR_SRN_HAS_MULTIPLE_ASSETS, params));
                }
            }
        }
    }

    /**
     * Combination of asset and owner columns can't repeat. CBO only.
     * 
     * @param errors
     */
    protected void validateAssetOwnerCombinationUnique(List<DomainApiErrorDto> errors) {
        Group<TaxParcelRow> groupedRows = Lambda.group(
                Lambda.select(rows, Lambda.having(Lambda.on(TaxParcelRow.class).getAssetCode(), IsNull.notNullValue())),
                Lambda.by(Lambda.on(TaxParcelRow.class).getAssetCode()));

        Iterator<String> iter = groupedRows.keySet().iterator();
        while (iter.hasNext()) {
            List<TaxParcelRow> rows = groupedRows.find(iter.next());
            Set<String> ownersForAsset = new HashSet<>();

            for (TaxParcelRow row : rows) {
                if (!ownersForAsset.add(row.getOwner())) {
                    String[] params = { row.getAssetCode(), row.getRowNumber(), };
                    errors.add(util.getError(ERROR_DUPLICATE_ASSET, params));
                }
            }
        }
    }

    /**
     * Based on the row-data specified, determine if the data provided contains either Original-Cost-Base or Cost-Base. The type
     * of cost-base will in return results in different data-vetting rule.
     * 
     * @param row
     * @param indicator
     * @return
     */
    private boolean isCostBaseOfType(TaxParcelRow row, int indicator) {
        String firstOcb = ((TaxParcelDetailedRow) row).getOriginalCostBase();
        String firstCb = ((TaxParcelDetailedRow) row).getCostBase();
        String firstRcb = ((TaxParcelDetailedRow) row).getReducedCostBase();

        switch (indicator) {
            case ORIG_COST_BASE:
                return firstOcb != null && firstCb == null && firstRcb == null;
            case COST_BASE:
                return firstOcb == null && firstCb != null;
            default:
                return false;
        }
    }

    /**
     * If any of the original-cost-base data provided is set to 0, all other tax-data needs to have original cost base set to 0 as
     * well.
     * 
     * @param errors
     */
    protected void validateOriginalCostBaseValue(List<DomainApiErrorDto> errors) {
        int zeroCount = 0;
        int nonZeroCount = 0;

        for (TaxParcelRow row : rows) {
            TaxParcelDetailedRow dRow = (TaxParcelDetailedRow) row;
            String strCost = dRow.getOriginalCostBase();
            if (strCost != null) {
                BigDecimal cost = new BigDecimal(strCost);
                if (BigDecimal.ZERO.equals(cost)) {
                    zeroCount++;
                    continue;
                }
            }
            nonZeroCount++;
        }
        if (zeroCount > 0 && nonZeroCount > 0) {
            errors.add(util.getError(ERROR_OCB_ALL_ZERO));
        }
    }
}
