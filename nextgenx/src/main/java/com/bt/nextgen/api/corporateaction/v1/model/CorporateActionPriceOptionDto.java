package com.bt.nextgen.api.corporateaction.v1.model;

public class CorporateActionPriceOptionDto {
    private Integer id;
    private String title;
    private Boolean isDefault;

    public CorporateActionPriceOptionDto() {
        // Empty constructor
    }

    public CorporateActionPriceOptionDto(Integer id, String title, Boolean isDefault) {
        this.id = id;
        this.title = title;
        this.isDefault = isDefault;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }
}
