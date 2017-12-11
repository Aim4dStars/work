package com.bt.nextgen.api.account.v1.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * @deprecated Use V2
 */
@Deprecated
public class GrowthItemDto {

    /** The balance. */
    private BigDecimal balance;

    /** The code. */
    private String code;

    /** The display name. */
    private String displayName;

    /** The growth items. */
    private List<GrowthItemDto> growthItems;

    /**
     * Instantiates a new growth item dto.
     *
     * @param balance
     *            the balance
     * @param code
     *            the code
     * @param displayName
     *            the display name
     * @param growthItems
     *            the growth items
     */
    public GrowthItemDto(BigDecimal balance, String code, String displayName, List<GrowthItemDto> growthItems) {
        super();
        this.balance = balance;
        this.code = code;
        this.displayName = displayName;
        this.growthItems = growthItems;
    }

    /**
     * Gets the balance.
     *
     * @return the balance
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the growth items.
     *
     * @return the growth items
     */
    public List<GrowthItemDto> getGrowthItems() {
        return growthItems;
    }
}
