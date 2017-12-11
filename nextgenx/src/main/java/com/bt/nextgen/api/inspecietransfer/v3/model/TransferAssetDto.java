package com.bt.nextgen.api.inspecietransfer.v3.model;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.service.integration.transfer.transfergroup.TransferAsset;
import com.fasterxml.jackson.annotation.JsonView;

import java.math.BigDecimal;
import java.util.List;

public class TransferAssetDto {

    @JsonView(JsonViews.Write.class)
    private AssetDto asset;

    @JsonView(JsonViews.Write.class)
    private BigDecimal quantity;

    @JsonView(JsonViews.Write.class)
    private SponsorDetailsDto sponsorDetails;

    @JsonView(JsonViews.Write.class)
    private List<TaxParcelDto> taxParcels;

    @JsonView(JsonViews.Write.class)
    private Boolean isCashTransfer;

    @JsonView(JsonViews.Write.class)
    private String amount;

    @JsonView(JsonViews.Write.class)
    private List<String> vettWarnings;

    public TransferAssetDto() {
        super();
    }

    public TransferAssetDto(TransferAsset transferAsset, AssetDto assetDto, SponsorDetailsDto sponsorDetails,
            List<TaxParcelDto> taxParcels) {
        this.asset = assetDto;
        this.quantity = transferAsset.getQuantity();
        this.sponsorDetails = sponsorDetails;
        this.taxParcels = taxParcels;
        isCashTransfer = transferAsset.getIsCashTransfer();
    }

    public TransferAssetDto(BigDecimal quantity, AssetDto assetDto, SponsorDetailsDto sponsorDetails,
            List<TaxParcelDto> taxParcels) {
        this.asset = assetDto;
        this.quantity = quantity;
        this.sponsorDetails = sponsorDetails;
        this.taxParcels = taxParcels;
    }

    public AssetDto getAsset() {
        return asset;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public SponsorDetailsDto getSponsorDetails() {
        return sponsorDetails;
    }

    public List<TaxParcelDto> getTaxParcels() {
        return taxParcels;
    }

    public void updateQuantity() {
        quantity = BigDecimal.ZERO;
        if (taxParcels != null) {
            for (TaxParcelDto tax : taxParcels) {
                quantity = quantity.add(tax.getQuantity());
            }
        }
    }

    public Boolean getIsCashTransfer() {
        return isCashTransfer;
    }

    public String getAmount() {
        return amount;
    }

    public List<String> getVettWarnings() {
        return vettWarnings;
    }
}
