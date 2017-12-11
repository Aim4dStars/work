package com.bt.nextgen.api.account.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

/**
 * @deprecated Use V2
 */
@Deprecated
public class BpayBillerDto extends BaseDto implements KeyedDto<BillerKey> {

    /** The biller name. */
    private String billerName;

    /** The biller code. */
    private String billerCode;

    /** The key. */
    private BillerKey key;

    /**
     * Instantiates a new bpay biller dto.
     */
    public BpayBillerDto() {
    }

    /**
     * Instantiates a new bpay biller dto.
     *
     * @param billerCode
     *            the biller code
     */
    public BpayBillerDto(String billerCode) {
        this.billerCode = billerCode;
    }

    /**
     * Instantiates a new bpay biller dto.
     *
     * @param key
     *            the key
     */
    public BpayBillerDto(BillerKey key) {
        this.key = key;
    }

    /**
     * Instantiates a new bpay biller dto.
     *
     * @param billerCode
     *            the biller code
     * @param billerName
     *            the biller name
     */
    public BpayBillerDto(String billerCode, String billerName) {
        this.billerCode = billerCode;
        this.billerName = billerName;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.core.api.model.KeyedDto#getKey()
     */
    @Override
    public BillerKey getKey() {
        return key;
    }

    /**
     * Sets the key.
     *
     * @param key
     *            the new key
     */
    public void setKey(BillerKey key) {
        this.key = key;
    }

    /**
     * Gets the biller code.
     *
     * @return the biller code
     */
    public String getBillerCode() {
        return billerCode;
    }

    /**
     * Gets the biller name.
     *
     * @return the biller name
     */
    public String getBillerName() {
        return billerName;
    }

    /**
     * Sets the biller name.
     *
     * @param billerName
     *            the new biller name
     */
    public void setBillerName(String billerName) {
        this.billerName = billerName;
    }

    /**
     * Sets the biller code.
     *
     * @param billerCode
     *            the new biller code
     */
    public void setBillerCode(String billerCode) {
        this.billerCode = billerCode;
    }
}
