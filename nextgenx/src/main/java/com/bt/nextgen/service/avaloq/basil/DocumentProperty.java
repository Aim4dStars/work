package com.bt.nextgen.service.avaloq.basil;

public enum DocumentProperty {

    DOCUMENTTYPE("DocumentType"),
    SPORTFOLIONUMBER("sNumber"),
    SPOLICYID("sPolicyID"),
    EFFECTIVEDATE("EffectiveDate"),
    BLOBTYPE("BlobType"),
    SNOTES("sNotes"),
    BUSINESSLINE("BusinessLine"),
    CURRENTTRAY("CurrentTray"),


    UNKNOWN("unknown");

    private String code;

    DocumentProperty(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static DocumentProperty findByCode(String code) {
        for (DocumentProperty property : DocumentProperty.values()) {
            if (property.getCode().equals(code)) {
                return property;
            }
        }
        return UNKNOWN;
    }

    @Override
    public String toString() {
        return code;
    }
}
