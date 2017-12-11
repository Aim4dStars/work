package com.bt.nextgen.api.account.v1.model;

/**
 * @deprecated Use V2
 */
@Deprecated
public class BillerKey {

    /** The key. */
    private String key;

    /**
     * Instantiates a new biller key.
     */
    public BillerKey() {
    }

    /**
     * Instantiates a new biller key.
     *
     * @param billerKey
     *            the biller key
     */
    public BillerKey(String billerKey) {
        this.key = billerKey;
    }

    /**
     * Gets the key.
     *
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key.
     *
     * @param key
     *            the new key
     */
    public void setKey(String key) {
        this.key = key;
    }
}
