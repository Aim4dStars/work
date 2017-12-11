package com.bt.nextgen.api.beneficiary.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.util.List;

/**
 * Created by L067218 on 1/07/2016.
 */
public class SuperNominationTypeDto extends BaseDto {

    private String id;
    private String value;
    private String intlId;
    private String listName;
    private String label;
    private List<String> supportedSuperAccountSubTypes;
    private boolean dependentOnly;
    private boolean soleNominationOnly;

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

    public List<String> getSupportedSuperAccountSubTypes() {
        return supportedSuperAccountSubTypes;
    }

    public void setSupportedSuperAccountSubTypes(List<String> supportedSuperAccountSubTypes) {
        this.supportedSuperAccountSubTypes = supportedSuperAccountSubTypes;
    }

    public boolean isSoleNominationOnly() {
        return soleNominationOnly;
    }

    public void setSoleNominationOnly(boolean soleNominationOnly) {
        this.soleNominationOnly = soleNominationOnly;
    }

    public boolean isDependentOnly() {
        return dependentOnly;
    }

    public void setDependentOnly(boolean dependentOnly) {
        this.dependentOnly = dependentOnly;
    }

}
