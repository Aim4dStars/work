package com.bt.nextgen.api.version.service;

import com.bt.nextgen.api.version.model.ModuleKey;
import com.bt.nextgen.api.version.model.ModuleVersionDto;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.service.ServiceErrors;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
class VersionDtoServiceImpl implements VersionDtoService {

    // Short term implementation to support initial mobile client release. Do
    // not add to this - full solution will be put in place. Speak to
    // James/Andy/DP before making changes here.
    private static final ModuleKey HERITAGE_MODULE_KEY = new ModuleKey("heritage");
    private static final Map<ModuleKey, ModuleVersionDto> VERSIONS = ImmutableMap.of(HERITAGE_MODULE_KEY, new ModuleVersionDto(
            HERITAGE_MODULE_KEY, ApiVersion.CURRENT_VERSION));

    @Override
    public ModuleVersionDto find(ModuleKey key, ServiceErrors serviceErrors) {
        return VERSIONS.get(key);
    }

    @Override
    public List<ModuleVersionDto> findAll(ServiceErrors serviceErrors) {
        return new ArrayList<ModuleVersionDto>(VERSIONS.values());
    }

}
