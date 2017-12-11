package com.bt.nextgen.service.cms;

import java.io.IOException;

import com.bt.nextgen.api.cms.model.CmsDtoKey;

/**
 * Interface  to call AEM.
 * Created by L070589 on 25/02/2015.
 */
public interface CmsCall {

    String sendAndReceiveFromCms(CmsDtoKey key) throws IOException;
}
