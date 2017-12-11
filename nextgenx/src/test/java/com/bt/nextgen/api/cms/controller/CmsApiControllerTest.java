package com.bt.nextgen.api.cms.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.cms.model.CmsDtoKey;
import com.bt.nextgen.api.cms.model.CmsFileMetaDto;
import com.bt.nextgen.api.cms.service.CmsFileMetaDataDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.NotFoundException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.service.ServiceErrors;

/**
 * Created by L070589 on 13/03/2015.
 */

@RunWith(MockitoJUnitRunner.class)
public class CmsApiControllerTest {

    @InjectMocks
    private CmsApiController cmsApiController;

    @Mock
    private CmsFileMetaDataDtoService cmsService;

    @Test(expected = NotFoundException.class)
    public void testGetCmsResourceWithNoKey() throws Exception {
        ApiResponse response = cmsApiController.getCmsResource("Pro.IP-0001", "");
        Assert.assertNull(response.getData());
    }

    @Test
    public void testGetCmsByKey() {
        CmsFileMetaDto cmsResponse = getCmsDto("Pro.IP-0001", "bt", "this is the response value");
        Mockito.when(cmsService.find(Mockito.any(CmsDtoKey.class), Mockito.any(ServiceErrors.class))).thenReturn(cmsResponse);
        ApiResponse response = cmsApiController.getCmsResource("Pro.IP-0001", "bt");
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getApiVersion(), ApiVersion.CURRENT_VERSION);
        Assert.assertEquals(response.getError(), null);
        Assert.assertEquals(((CmsFileMetaDto) response.getData()).getKey().getKey(), "Pro.IP-0001");
        Assert.assertEquals(((CmsFileMetaDto) response.getData()).getKey().getQuery(), "bt");
        Assert.assertEquals(((CmsFileMetaDto) response.getData()).getValue(), "this is the response value");
    }

    private CmsFileMetaDto getCmsDto(String key, String query, String value) {
        CmsFileMetaDto dto = new CmsFileMetaDto();
        dto.setKey(new CmsDtoKey(key, query));
        dto.setValue(value);
        return dto;
    }
}
