package com.bt.nextgen.service.cms;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.cms.model.CmsDtoKey;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.util.Properties;

/**
 * Implementation to call AEM
 * Created by L070589 on 25/02/2015.
 */
@Service
public class CmsCallImpl implements CmsCall {

    private static final String AEM_URL = "aem.cms.url";
    private static final String AEM_SERVICE_OPS_URL = "aem.service.ops.url";

    @Autowired
    private UserProfileService userProfileService;

    public String sendAndReceiveFromCms(CmsDtoKey key) throws IOException {

        final String urlPath = getResourcePath(key);
        //Get mapped URL path if property is found in mapping file, otherwise, use exact path given in the key
        final String url = StringUtils.isNotBlank(urlPath) ? getBaseURL() + urlPath : getBaseURL() + key.getKey();

        URLConnection connection = new URL(url).openConnection(Proxy.NO_PROXY);
        return IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
    }

    private String getBaseURL() {
        return userProfileService.isEmulating() ? Properties.getString(AEM_SERVICE_OPS_URL) : Properties.getString(AEM_URL);
    }

    private String getResourcePath(CmsDtoKey key) {
        return MessageFormat.format(Properties.getString(key.getKey()), key.getQuery().split(","));
    }
}
