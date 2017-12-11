package com.bt.nextgen.service.integration.marketondemand;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "channel")
@XmlAccessorType(XmlAccessType.FIELD)
public class RssFeedChannel {

    @XmlElement(name = "title")
    private String title;

    @XmlElement(name = "description")
    private String description;

    @XmlElement(name = "item")
    private List<RssFeedItem> rssFeedItemList;

    @XmlElement(name = "image")
    private RssFeedImage rssFeedImage;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RssFeedImage getRssFeedImage() {
        return rssFeedImage;
    }

    public void setRssFeedImage(RssFeedImage rssFeedImage) {
        this.rssFeedImage = rssFeedImage;
    }

    public List<RssFeedItem> getRssFeedItemList() {
        return rssFeedItemList;
    }

    public void setRssFeedItemList(List<RssFeedItem> rssFeedItemList) {
        this.rssFeedItemList = rssFeedItemList;
    }
}
