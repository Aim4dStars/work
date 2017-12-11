package com.bt.nextgen.api.version.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ModuleVersionDto extends BaseDto implements KeyedDto<ModuleKey> {
    private ModuleKey module;
    private List<String> versions;

    public ModuleVersionDto(ModuleKey module, String... versions) {
        this.module = module;
        this.versions = Collections.unmodifiableList(Arrays.asList(versions));
    }

    public ModuleVersionDto(ModuleKey module, List<String> versions) {
        this.module = module;
        this.versions = Collections.unmodifiableList(versions);
    }

    public List<String> getVersions() {
        return versions;
    }

    @Override
    public ModuleKey getKey() {
        return module;
    }

}
