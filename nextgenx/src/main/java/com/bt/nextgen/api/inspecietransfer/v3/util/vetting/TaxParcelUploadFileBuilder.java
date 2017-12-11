package com.bt.nextgen.api.inspecietransfer.v3.util.vetting;

import com.bt.nextgen.api.inspecietransfer.v3.util.TaxParcelUploadUtil;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.transfer.TransferType;

import java.util.List;

public class TaxParcelUploadFileBuilder {

    private TaxParcelHeader header;
    private List<TaxParcelRow> rows;
    private boolean isMacroEnabled;
    private boolean hasFormulas;
    private boolean isCbo;
    private TransferType transferType;
    private String sponsorName;
    private TaxParcelUploadUtil util;
    private AssetIntegrationService assetService;

    public static TaxParcelUploadFileBuilder uploadFile() {
        return new TaxParcelUploadFileBuilder();
    }

    public TaxParcelUploadFile build() {
        return new TaxParcelUploadFile(this);
    }

    public TaxParcelIndependentUploadFile buildIndependent() {
        return new TaxParcelIndependentUploadFile(this);
    }

    public TaxParcelUploadFileBuilder withHeader(TaxParcelHeader header) {
        this.header = header;
        return this;
    }

    public TaxParcelUploadFileBuilder withRows(List<TaxParcelRow> rows) {
        this.rows = rows;
        return this;
    }

    public TaxParcelUploadFileBuilder withIsMacroEnabled(boolean isMacroEnabled) {
        this.isMacroEnabled = isMacroEnabled;
        return this;
    }

    public TaxParcelUploadFileBuilder withHasFormulas(boolean hasFormulas) {
        this.hasFormulas = hasFormulas;
        return this;
    }

    public TaxParcelUploadFileBuilder withIsCbo(boolean isCbo) {
        this.isCbo = isCbo;
        return this;
    }

    public TaxParcelUploadFileBuilder withTransferType(TransferType transferType) {
        this.transferType = transferType;
        return this;
    }

    public TaxParcelUploadFileBuilder withSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
        return this;
    }

    public TaxParcelUploadFileBuilder withUtil(TaxParcelUploadUtil util) {
        this.util = util;
        return this;
    }

    public TaxParcelUploadFileBuilder withAssetService(AssetIntegrationService assetService) {
        this.assetService = assetService;
        return this;
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

    public TaxParcelUploadUtil getUtil() {
        return util;
    }

    public AssetIntegrationService getAssetService() {
        return assetService;
    }
}
