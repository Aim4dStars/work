package com.bt.nextgen.service.integration.marketondemand;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * This ObjectFactory creates RssFeed objects
 */
@XmlRegistry
public class ObjectFactory {

    public ObjectFactory() {
    }

    public RssFeed createRssFeed() {
        return new RssFeed();
    }

    public RssFeedChannel createRssFeedChannel() {
        return new RssFeedChannel();
    }

    public RssFeedImage createRssFeedImage() {
        return new RssFeedImage();
    }

    public RssFeedItem createRssFeedItem() {
        return new RssFeedItem();
    }
}
