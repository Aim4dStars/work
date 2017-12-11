package com.bt.nextgen.service.integration.modelportfolio;

public enum UploadAssetCodeEnum {

    MPCASH("MPCASH"),
    MODELCASH("MACC.MP.AUD"),
    TMP_CASH("MACC.TMP.AUD"),
    SUPER_TMP_CASH("MACC.STMP.AUD"),
    ADVISER_MODEL_CASH("MACC.MOD.AUD");

    private final String code;

    UploadAssetCodeEnum(String code) {
        this.code = code;
    }

    public String value() {
        return code;
    }

    public static UploadAssetCodeEnum fromValue(String code) {
        for (UploadAssetCodeEnum assetType : UploadAssetCodeEnum.values()) {
            if (assetType.code.equals(code)) {
                return assetType;
            }
        }
        throw new IllegalArgumentException(code);
    }

    public static boolean isCashAsset(String code) {
        for (UploadAssetCodeEnum assetType : UploadAssetCodeEnum.values()) {
            if (assetType.code.equals(code)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

}