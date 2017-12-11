package com.bt.nextgen.service.avaloq.transfer.transfergroup;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.transfer.TaxParcelImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.transfer.SponsorDetails;
import com.bt.nextgen.service.integration.transfer.TaxParcel;
import com.bt.nextgen.service.integration.transfer.transfergroup.TransferAsset;

import java.math.BigDecimal;
import java.util.List;

@ServiceBean(xpath = "asset")
public class TransferAssetImpl implements TransferAsset {

    @ServiceElement(xpath = "asset_id/val")
    private String assetId;

    @ServiceElement(xpath = "qty/val")
    private BigDecimal quantity;

    @ServiceElement(xpath = "is_bt_cash_xfer/val")
    private boolean isCashTransfer;

    @ServiceElement(xpath = "tax_parcel_list/tax_parcel", type = TaxParcelImpl.class)
    private List<TaxParcel> taxParcels;

    @ServiceElement(xpath = ".", type = SponsorDetailsImpl.class)
    private SponsorDetails sponsorDetails;

    // Asset-name.
    private String name;

    // Asset-type.
    private AssetType type;

    public TransferAssetImpl() {
        super();
    }

    public TransferAssetImpl(String assetId, BigDecimal quantity) {
        this.assetId = assetId;
        this.quantity = quantity;
    }

    @Override
    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    @Override
    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public AssetType getType() {
        return type;
    }

    public void setType(AssetType type) {
        this.type = type;
    }

    @Override
    public List<TaxParcel> getTaxParcels() {
        return taxParcels;
    }

    public void setTaxParcels(List<TaxParcel> taxParcels) {
        this.taxParcels = taxParcels;
    }

    @Override
    public SponsorDetails getSponsorDetails() {
        return sponsorDetails;
    }

    public void setSponsorDetails(SponsorDetails sponsorDetails) {
        this.sponsorDetails = sponsorDetails;
    }

    public void setIsCashTransfer(boolean isCashTransfer) {
        this.isCashTransfer = isCashTransfer;
    }

    @Override
    public boolean getIsCashTransfer() {
        return isCashTransfer;
    }
}
