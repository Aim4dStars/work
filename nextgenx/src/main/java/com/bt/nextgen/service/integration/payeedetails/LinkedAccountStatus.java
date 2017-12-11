package com.bt.nextgen.service.integration.payeedetails;

/**
 * Created by L078480 on 27/07/2017.
 */
public enum LinkedAccountStatus {
   VERIFIED("Verified","vfy"),
   UNVERIFIED("Unverfied","unvfy"),
   EXPIRED("Expired","expir"),
   LOCKED("Locked","lock"),
   VERIFICATION_IN_PROGRESS("Verification in Progress","veri_in_progress"),
   PAYMENT_FAILED("Payment Failed","pay_fail"),
   UNVERIFIED_GRACE_PERIOD("Unverified Grace Period","unvfy_grace_prd"),
    VERIFICATION_IN_PROGRESS_GRACE_PERIOD("Verification in grace period","veri_in_progress_grace_prd" ),
    PAYMENT_FAILED_GRACE_PERIOD("Payment failed in grace period","pay_fail_grace_prd"),
    EXPIRED_GRACE_PERIOD("Expired in grace period","expir_grace_prd"),
    LOCKED_GRACE_PERIOD("Locked in grace period","lock_grace_prd");






    private String description = "";
    private String code = "";

    LinkedAccountStatus(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return this.description;
    }

    public String getIntlId() {
        return code;
    }

    public static LinkedAccountStatus forIntlId(String intlId) {
        for (LinkedAccountStatus style : values()) {
            if (style.getIntlId().equals(intlId)) {
                return style;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return code;
    }

}
