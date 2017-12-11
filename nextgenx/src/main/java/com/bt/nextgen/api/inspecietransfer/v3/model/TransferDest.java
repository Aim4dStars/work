package com.bt.nextgen.api.inspecietransfer.v3.model;


public class TransferDest {

    private String destContainerId;
    private String assetId;
    private String assetName;
    private String assetType;
    private String assetCode;

    public TransferDest() {
        // Default Constructor
    }

    public TransferDest(String destContainerId, String assetId, String assetName, String assetType, String assetCode) {
        this.destContainerId = destContainerId;
        this.assetId = assetId;
        this.assetName = assetName;
        this.assetType = assetType;
        this.assetCode = assetCode;
    }

    public String getDestContainerId() {
        return destContainerId;
    }

    public void setDestContainerId(String destContainerId) {
        this.destContainerId = destContainerId;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

}
