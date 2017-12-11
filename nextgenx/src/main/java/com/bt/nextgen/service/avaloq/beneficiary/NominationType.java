package com.bt.nextgen.service.avaloq.beneficiary;

/**
 * Created by L067218 on 4/08/2016.
 */
public enum NominationType {

    AUTO_REVERSIONARY("nomn_auto_revsnry", "Australian Superannuation Death Benefits Nomination - Automatic Reversionary", 21),
    TRUSTEE_DISCRETION("nomn_bind_nlaps_trustd", "Australian Superannuation Death Benefits Nomination - Binding - Non Lapsing (Trust Deed)", 4),
    NON_LAPSING_NOMINATION("nomn_nbind_sis", "Australian Superannuation Death Benefits Nomination - Non Binding (SIS)", 2);

    private String avaloqInternalId;
    private String name;
    private int orderId;

    NominationType(String avaloqInternalId, String name, int orderId) {
        this.avaloqInternalId = avaloqInternalId;
        this.name = name;
        this.orderId = orderId;
    }

    /**
     * This method returns a NominationType  object for an avaloq internal id
     *
     * @param avaloqInternalId internal id of avaloq
     * @return Object of {@link NominationType}
     */
    public static NominationType findByAvaloqId(String avaloqInternalId) {
        for (NominationType nominationType : NominationType.values()) {
            if (avaloqInternalId.equals(nominationType.getAvaloqInternalId())) {
                return nominationType;
            }
        }
        return null;
    }

    /**
     * Returns the avaloq internal id for nomination type
     *
     * @return avaloq internal id
     */
    public String getAvaloqInternalId() {
        return avaloqInternalId;
    }

    /**
     * Returns the name
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Return the order of relationship type
     *
     * @return order id
     */
    public Integer getOrderId() {
        return orderId;
    }

    @Override
    public String toString() {
        return getAvaloqInternalId();
    }
}
