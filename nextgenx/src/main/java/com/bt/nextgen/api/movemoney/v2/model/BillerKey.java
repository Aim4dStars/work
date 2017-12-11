package com.bt.nextgen.api.movemoney.v2.model;

public class BillerKey {

    /** The key. */
    private String key;

    /**
     * Instantiates a new biller key.
     */
    public BillerKey() {
        // comments
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
