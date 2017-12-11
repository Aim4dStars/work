package com.bt.nextgen.api.version.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.version.model.MobileAppVersionDto;
import com.bt.nextgen.core.domain.key.StringIdKey;
import com.bt.nextgen.core.repository.MobileAppVersion;
import com.bt.nextgen.core.repository.MobileAppVersionRepository;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

@RunWith(MockitoJUnitRunner.class)
public class MobileAppVersionDtoServiceImplTest {

    @InjectMocks
    MobileAppVersionDtoServiceImpl mobileVersionDtoService;

    @Mock
    private MobileAppVersionRepository mobileAppVersionRepository;

    private ServiceErrors serviceErrors;
    private List<MobileAppVersion> mobileAppVersions = new ArrayList<>();


    @Before
    public void setup() {
        serviceErrors = new ServiceErrorsImpl();
        mobileAppVersions.add(new MobileAppVersion("ios", "1.0"));
        mobileAppVersions.add(new MobileAppVersion("android", "1.8"));
        mobileAppVersions.add(new MobileAppVersion("windows", "2.3.0"));
    }

    @Test
    public void testFindAll() {
        Mockito.when(mobileAppVersionRepository.findAppVersions()).thenReturn(mobileAppVersions);
        List<MobileAppVersionDto> result = mobileVersionDtoService.findAll(serviceErrors);
        Assert.assertEquals(result.size(), 3);

        Assert.assertEquals(result.get(0).getKey().getId(), "ios");
        Assert.assertEquals(result.get(0).getMinVersion(), "1.0");

        Assert.assertEquals(result.get(1).getKey().getId(), "android");
        Assert.assertEquals(result.get(1).getMinVersion(), "1.8");

        Assert.assertEquals(result.get(2).getKey().getId(), "windows");
        Assert.assertEquals(result.get(2).getMinVersion(), "2.3.0");
    }

    @Test
    public void testFindByKey() {
        Mockito.when(mobileAppVersionRepository.findAppVersions()).thenReturn(mobileAppVersions);
        MobileAppVersionDto commonResult = mobileVersionDtoService.find(new StringIdKey("common"), serviceErrors);
        MobileAppVersionDto platformResult = mobileVersionDtoService.find(new StringIdKey("android"), serviceErrors);

        Assert.assertEquals(commonResult.getKey().getId(), "common");
        Assert.assertEquals(commonResult.getMinVersion(), "2.0.0");
        Assert.assertEquals(platformResult.getKey().getId(), "android");
        Assert.assertEquals(platformResult.getMinVersion(), "1.8");
    }

    public void testFindAllNoData() {
        Mockito.when(mobileAppVersionRepository.findAppVersions()).thenReturn(null);
        List<MobileAppVersionDto> result = mobileVersionDtoService.findAll(serviceErrors);
        Assert.assertEquals(result.size(), 0);
    }
}
