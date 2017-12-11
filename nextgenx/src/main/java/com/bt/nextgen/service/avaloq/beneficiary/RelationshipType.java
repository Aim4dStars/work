package com.bt.nextgen.service.avaloq.beneficiary;

/**
 * Created by M035995 on 11/07/2016.
 */
public enum RelationshipType {

    SPOUSE("spouse", "Spouse", 1),
    CHILD("child", "Child", 2),
    FINANCIAL_DEPENDENT("fin_dep", "Financial Dependent", 3),
    INTERDEPENDENT("interdependent", "Interdependent", 4),
    LPR("lpr", "Legal Personal Representative", 5);

    private String avaloqInternalId;
    private String name;
    private int orderId;

    RelationshipType(String avaloqInternalId, String name, int orderId) {
        this.avaloqInternalId = avaloqInternalId;
        this.name = name;
        this.orderId = orderId;
    }

    /**
     * This method returns a Relationship type object for an avaloq internal id
     *
     * @param avaloqInternalId internal id of avaloq
     * @return Object of {@link RelationshipType}
     */
    public static RelationshipType findByAvaloqId(String avaloqInternalId) {
        for (RelationshipType relationshipType : RelationshipType.values()) {
            if (avaloqInternalId.equals(relationshipType.getAvaloqInternalId())) {
                return relationshipType;
            }
        }
        return null;
    }

    /**
     * Returns the avaloq internal id for relationship type
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
