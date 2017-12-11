package com.bt.nextgen.api.cms.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

/**
 * Created by L070589 on 23/02/2015.
 */
public class CmsFileMetaDto extends BaseDto implements KeyedDto<CmsDtoKey> {

    private String value;
    private CmsDtoKey key;

    @Override
    public CmsDtoKey getKey() {
        return key;
    }

    public void setKey(CmsDtoKey key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
