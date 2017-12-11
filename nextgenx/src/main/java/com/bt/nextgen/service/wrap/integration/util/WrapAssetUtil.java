package com.bt.nextgen.service.wrap.integration.util;

/**
 * Examine wrap Asset
 * Created by L067221 on 22/09/2017.
 */
public final class WrapAssetUtil {
    private WrapAssetUtil() {

    }

    private static final String WRAP_CASH_CODE = "WRAPWCA";

    /**
     * Examine whether securityCode represents Term Deposit asset
     *
     * @param securityCode
     *
     * @return
     */
    public static boolean isWrapTermDeposit(String securityCode) {
        return securityCode != null && securityCode.startsWith("WBC") && securityCode.endsWith("TD");
    }

    /**
     * Examine whether securityCode represents Cash asset
     *
     * @param securityCode
     *
     * @return
     */
    public static boolean isWrapCash(String securityCode) {
        return WRAP_CASH_CODE.equals(securityCode);
    }
}
