package com.bt.nextgen.service.cms;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.cms.model.CmsDtoKey;

/**
 * Created by L070589 on 25/02/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class CmsCallImplTest {

    @InjectMocks
    private CmsCallImpl cmsCall;

    @Test
    @Ignore
    public void testReceiveFromCmsNoConnection() throws IOException {
        String response = cmsCall.sendAndReceiveFromCms(new CmsDtoKey());
        Assert.assertNotNull(response);
    }
}
