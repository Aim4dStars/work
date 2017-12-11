package com.bt.nextgen.service.integration.transfer.transfergroup;

import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.transfer.SponsorDetails;
import com.bt.nextgen.service.integration.transfer.TaxParcel;

import java.math.BigDecimal;
import java.util.List;

public interface TransferAsset {

    /**
     * Asset Id.
     * 
     * @return
     */
    public String getAssetId();

    /**
     * Quantity to be transferred.
     * 
     * @return
     */
    public BigDecimal getQuantity();

    /**
     * Asset name
     * 
     * @return
     */
    public String getName();

    /**
     * Asset Type
     * 
     * @return
     */
    public AssetType getType();

    /**
     * Optional tax parcels associated with this transfer-asset.
     * 
     * @return
     */
    public List<TaxParcel> getTaxParcels();

    /**
     * Sponsor details. This including details such as PID, HIN, SRN etc. Only used for external-transfer.
     * 
     * @return
     */
    public SponsorDetails getSponsorDetails();

    /**
     * Flag to indicate if this is a cash-asset transfer.
     * 
     * @return
     */
    public boolean getIsCashTransfer();

}
