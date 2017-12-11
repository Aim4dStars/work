package com.bt.nextgen.api.staticreference.model;

import com.bt.nextgen.core.api.model.BaseDto;

/**
 * Created by M035801 on 21/07/2016.
 */
public class StaticReferenceDto extends BaseDto
{
    private String id;
    private String code;
    private String label;
    private String category;


    public StaticReferenceDto(String id, String code, String label)
    {
        this.id = id;
        this.code = code;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}