package com.bt.nextgen.service.integration.marketondemand;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "rss")
@XmlAccessorType(XmlAccessType.FIELD)
public class RssFeed {

    @XmlElement(name = "channel")
    private RssFeedChannel rssFeedChannel;

    public RssFeedChannel getRssFeedChannel() {
        return rssFeedChannel;
    }

    public void setRssFeedChannel(RssFeedChannel rssFeedChannel) {
        this.rssFeedChannel = rssFeedChannel;
    }
}
