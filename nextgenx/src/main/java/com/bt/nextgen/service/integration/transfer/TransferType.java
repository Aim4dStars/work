package com.bt.nextgen.service.integration.transfer;

import java.util.HashMap;
import java.util.Map;

/**
 * Different type of In-specie-transfer that the platform supports from the front end.
 * 
 * @author m028796
 * 
 */
public enum TransferType {

    LS_BROKER_SPONSORED("btfg$ls_broker_sponsor", "Listed Securities Broker Sponsored"),
    LS_ISSUER_SPONSORED("btfg$ls_issuer_sponsor", "Listed Securities Issuer Sponsored"),
    LS_OTHER("btfg$ls_oth_pltf_cust", "Listed Securities Other Platform or Custodian"),
    MANAGED_FUND("btfg$mngd_fund", "Managed Funds"),
    INTERNAL_TRANSFER("btfg@ls_intl_xfer", "Internal transfer"),
    OTHER_PLATFORM("btfg$asset_oth_pltf_cust", "Assets from other platform or Custodian");

    private String code;
    private String displayName;
    private static Map<String, TransferType> transferTypeMap;

    static {
        transferTypeMap = new HashMap<>();
        for (TransferType type : values()) {
            transferTypeMap.put(type.code, type);
        }
    }

    public static TransferType getTransferType(String id) {
        return transferTypeMap.get(id);
    }

    TransferType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public static TransferType forCode(String code) {
        for (TransferType transferType : TransferType.values()) {
            if (transferType.code.equals(code)) {
                return transferType;
            }
        }

        return null;
    }

    public static TransferType forDisplay(String display) {
        for (TransferType transferType : TransferType.values()) {
            if (transferType.displayName.equals(display)) {
                return transferType;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return code;
    }
}
