package com.bt.nextgen.service.provisio;

import com.bt.nextgen.service.integration.provisio.ProvisioService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import static com.bt.nextgen.core.util.Properties.getString;

@Service
public class ProvisioServiceImpl implements ProvisioService {

    private static final String PROVISIO_ENDPOINT = "provisio.endpoint";
    private static final String PROVISO_USER = "provisio.user";
    private static final String PROVISO_PASS = "provisio.password";

    private static final Logger logger = LoggerFactory.getLogger(ProvisioServiceImpl.class);

    @Override
    public String getProvisioToken() {
        final String url = getString(PROVISIO_ENDPOINT) + "/" + getString(PROVISO_USER) + "/" + getString(PROVISO_PASS);
        try {
            final URLConnection connection = new URL(url).openConnection();
            return IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            logger.error("Error getting provisio token", ex);
            return null;
        }
    }
}

