package com.bt.nextgen.api.account.v1.util;

/**
 * Created by L069552 on 12/10/2015.
 */
@Deprecated
public enum AssetTransferEnum {

    TAX_LOTS_APPROVED("TAX_LOTS_APPROVED","Transfer in progress"),
    FOUND_SENT_FM("FOUND_SENT_FM","Sent to Fund Manager"),
    FOUND_SENT_OTHER_PLATFORMS("FOUND_SENT_OTHER_PLATFORMS","Sent to Other Platform"),
    RUN("RUN","Transfer in progress"),
    RUN_BOOK("RUN_BOOK","Transfer in progress"),
    PENDING_TRANSFER_IN("PENDING_TRANSFER_IN","Transfer in progress"),
    DONE("DONE","Complete"),
    REVERSED("REVERSED","Reversed"),
    DISCARDED("DISCARDED","Discarded"),
    SETTLE_DISCARDED("SETTLE_DISCARDED","Discarded"),
    RUN_CANCEL("RUN_CANCEL","Transfer cancellation in progress");

    private final String name;
    private final String enumVal;

    AssetTransferEnum(String name,String enumVal)
    {
        this.name = name;
        this.enumVal = enumVal;
    }

    public String value()
    {
        return enumVal;
    }

    public static AssetTransferEnum fromValue(String name)
    {
        for (AssetTransferEnum assetTransfer : AssetTransferEnum.values())
        {
            if (assetTransfer.name.equals(name))
            {
                return assetTransfer;
            }
        }
        throw new IllegalArgumentException(name);
    }

}
