package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.model.JsonSchemaEnumsDto;

import java.util.List;

/**
 * Created by M040398 on 23/08/2016.
 */
public interface JsonSchemaHelperService {

    JsonSchemaEnumsDto getJsonSchemaEnums() throws ClassNotFoundException;

}
