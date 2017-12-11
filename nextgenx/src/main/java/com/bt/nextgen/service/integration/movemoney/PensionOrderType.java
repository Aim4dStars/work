package com.bt.nextgen.service.integration.movemoney;


/**
 * Pension OrderType
 */
public enum PensionOrderType {
    NEW_REGULAR_PENSION_PAYMENT("stord_new_super_pens", "Regular payment"),
    MODIFIED_REGULAR_PENSION_PAYMENT("sa_stord_mdf", "Regular payment"),
    LUMP_SUM_PENSION_PAYMENT("pay#super_opn_lmpsm", "Lump sum withdrawal"),
    ONE_OFF_PENSION_PAYMENT("pay#super_pens_oneoff", "Pension payment");

    private String id;
    private String label;

    PensionOrderType(String id, String label) {
        this.id = id;
        this.label = label;
    }

    /**
     * Converts Avaloq internal ID to pension order type.
     *
     * @param id Avaloq internal ID
     *
     * @return {code PensionOrderType} enum, null if no matching ID
     */
    public static PensionOrderType forId(String id) {
        for (PensionOrderType orderType : PensionOrderType.values()) {
            if (orderType.id.equals(id)) {
                return orderType;
            }
        }

        return null;
    }

    /**
     * The avaloq internal ID
     *
     * @return Avaloq internal ID code
     */
    public String getId() {
        return id;
    }

    /**
     * The name of the enum
     *
     * @return name of this enum.  Equiv to this.name()
     */
    public String getCode() {
        return this.name();
    }

    public String getLabel() {
        return label;
    }
}

