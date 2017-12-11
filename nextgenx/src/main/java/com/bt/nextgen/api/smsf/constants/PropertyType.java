package com.bt.nextgen.api.smsf.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Property type representation of the avaloq static code category
 * <code>btfg$ass_property_type</code>
 */
public enum PropertyType
{
    COMMERCIAL_PROPERTY("Commercial Property", "comm", 1, "Commercial"),
    RESIDENTIAL_PROPERTY("Residential Property", "res", 2, "Residential");

    private String description = "";
    private String code = "";
    private int order = 999;
    private String shortDesc = "";

    PropertyType(String description, String code, int order, String shortDesc)
    {
        this.description = description;
        this.code = code;
        this.order = order;
        this.shortDesc = shortDesc;
    }

    private static final Map<String, PropertyType> lookup = new HashMap<>();
    private static final Map<String, Integer> sortOrderLookup = new HashMap<>();

    static
    {
        for (PropertyType propertyType : PropertyType.values())
        {
            lookup.put(propertyType.getCode(), propertyType);
            sortOrderLookup.put(propertyType.getCode(), propertyType.getOrder());
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public static PropertyType getByCode(String code)
    {
        return lookup.get(code);
    }

    public static Map<String, PropertyType> getLookup() { return lookup; }

    public static Map<String, Integer> getSortOrderLookup() { return sortOrderLookup; }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    @Override
    public String toString() {
        return code;
    }
}
