package com.bt.nextgen.api.product.v1.model;

/**
 * Category to differentiate between standard product offerings.
 * Standard offers the full gamut of account types, including:
 * <ul><li>Individual</li>
 * <li>Joint</li>
 * <li>Company</li>
 * <li>Trust</li>
 * <li>SMSF</li>
 * </ul>
 * Super is specifically for Individuals, offering either Accumulation or Pension account types.
 */
public enum ProductCategory {

    /** Standard investment product category, allowing for Individual, Joint, Corporate, SMSF, and Trust accounts types. */
    STANDARD,

    /** Specialised Superannuation product category, allowed only for Individuals, with Accumulation and Pension flavours. */
    SUPER;
}
