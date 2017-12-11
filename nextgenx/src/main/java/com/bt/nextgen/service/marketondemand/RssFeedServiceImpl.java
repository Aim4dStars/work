package com.bt.nextgen.service.marketondemand;

import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.core.util.Properties;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.marketondemand.RssFeed;
import com.bt.nextgen.service.integration.marketondemand.RssFeedService;
import com.bt.nextgen.service.web.UrlProxyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

@Service
public class RssFeedServiceImpl implements RssFeedService {

    @Autowired
    private UrlProxyService urlProxyService;

    private static final Logger logger = LoggerFactory.getLogger(RssFeedServiceImpl.class);

    @Override
    @Cacheable(key = "#propertyKey", value = "com.bt.nextgen.service.marketondemand.RssFeed")
    public RssFeed readRSSFeed(String propertyKey, ServiceErrors serviceErrors) {
        final String urlString = Properties.get(propertyKey);
        try {
            final URLConnection connection = urlProxyService.connect(new URL(urlString));
            return JaxbUtil.unmarshall(connection.getInputStream(), RssFeed.class);
        } catch (IOException e) {
            logger.error("Error reading Rss feed: {}", e);
            serviceErrors.addError(new ServiceErrorImpl("Unable to read from \"" + urlString + "\": " + e));
        }
        return null;
    }
}
