package com.bt.nextgen.api.smsf.service;

import com.bt.nextgen.core.api.dto.SearchByKeyDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.api.smsf.model.AssetClassDto;

import java.util.List;

/**
 * Created by m035801 on 26/03/2015.
 */
public interface AssetClassMappingService extends SearchByKeyDtoService<String, AssetClassDto>
{
    public List search(List list, ServiceErrors serviceErrors);
}
