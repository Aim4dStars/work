package com.bt.nextgen.api.version.service;


import com.bt.nextgen.api.version.model.ModuleKey;
import com.bt.nextgen.api.version.model.ModuleVersionDto;
import com.bt.nextgen.core.api.ApiVersion;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class VersionDtoServiceImplTest {
    private VersionDtoServiceImpl versionService = new VersionDtoServiceImpl();

    @Test
    public void testFind_whenValidModuleSupplied_thatVersionIsReturned() {
        ServiceErrors errors = new FailFastErrorsImpl();
        ModuleKey key = new ModuleKey("heritage");
        ModuleVersionDto version = versionService.find(key, errors);
        Assert.assertEquals(key, version.getKey());
        Assert.assertEquals(1, version.getVersions().size());
        Assert.assertEquals(ApiVersion.CURRENT_VERSION, version.getVersions().get(0));
    }

    @Test
    public void testFind_whenUnknownModuleSupplied_thatNullIsReturned() {
        ServiceErrors errors = new FailFastErrorsImpl();
        ModuleKey key = new ModuleKey("invalid");
        ModuleVersionDto version = versionService.find(key, errors);
        Assert.assertNull(version);
    }

    @Test
    public void testFindAll_whenInvoked_allModulesAreReturned() {
        ServiceErrors errors = new FailFastErrorsImpl();
        ModuleKey key = new ModuleKey("heritage");
        List<ModuleVersionDto> versions = versionService.findAll(errors);
        Assert.assertEquals(1, versions.size());
        ModuleVersionDto version = versions.get(0);
        Assert.assertEquals(key, version.getKey());
        Assert.assertEquals(1, version.getVersions().size());
        Assert.assertEquals(ApiVersion.CURRENT_VERSION, version.getVersions().get(0));
    }

}
