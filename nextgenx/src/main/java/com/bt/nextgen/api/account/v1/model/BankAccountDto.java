package com.bt.nextgen.api.account.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;

/**
 * @deprecated Use V2
 */
@Deprecated
public class BankAccountDto extends BaseDto {

    /** The bsb. */
    private String bsb;

    /** The account number. */
    private String accountNumber;

    /** The name. */
    private String name;

    /** The nick name. */
    private String nickName;

    /**
     * Gets the bsb.
     *
     * @return the bsb
     */
    public String getBsb() {
        return bsb;
    }

    /**
     * Sets the bsb.
     *
     * @param bsb
     *            the new bsb
     */
    public void setBsb(String bsb) {
        this.bsb = bsb;
    }

    /**
     * Gets the account number.
     *
     * @return the account number
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the account number.
     *
     * @param accountNumber
     *            the new account number
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name
     *            the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the nick name.
     *
     * @return the nick name
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * Sets the nick name.
     *
     * @param nickName
     *            the new nick name
     */
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
