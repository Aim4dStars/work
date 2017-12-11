package com.bt.nextgen.service.integration.marketondemand;

import com.bt.nextgen.service.ServiceErrors;

public interface RssFeedService {

    RssFeed readRSSFeed(String propertyKey, ServiceErrors serviceErrors);
}
