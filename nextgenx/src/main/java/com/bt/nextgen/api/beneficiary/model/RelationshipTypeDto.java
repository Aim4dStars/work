package com.bt.nextgen.api.beneficiary.model;

import com.bt.nextgen.core.api.model.BaseDto;

/**
 * This is the pojo for Relationship type list.
 * Created by M035995 on 4/07/2016.
 */
public class RelationshipTypeDto extends BaseDto {

    private String id;

    private String value;

    private String intlId;

    private String listName;

    private String label;

    private boolean dependent;

    private Integer orderId;

    /**
     * This parametrised constructor sets the object for {@link RelationshipTypeDto}
     *
     * @param id        id of the relationship type field
     * @param value     value of the relationship type field
     * @param intlId    internal id of the relationship type field
     * @param listName  list Name of the relationship type field
     * @param label     label of the relationship type field
     * @param dependent flag for dependency on the other field
     * @param orderId   order id of relationship types
     */
    public RelationshipTypeDto(String id, String value, String intlId, String listName, String label, boolean dependent,
                               Integer orderId) {
        this.id = id;
        this.value = value;
        this.intlId = intlId;
        this.listName = listName;
        this.label = label;
        this.dependent = dependent;
        this.orderId = orderId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getIntlId() {
        return intlId;
    }

    public void setIntlId(String intlId) {
        this.intlId = intlId;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isDependent() {
        return dependent;
    }

    public void setDependent(boolean dependent) {
        this.dependent = dependent;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
}
