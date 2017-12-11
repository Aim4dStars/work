package com.bt.nextgen.service.integration.marketondemand;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "image")
@XmlAccessorType(XmlAccessType.FIELD)
public class RssFeedImage {

    @XmlElement(name = "url")
    private String url;

    @XmlElement(name = "title")
    private String title;

    @XmlElement(name = "height")
    private String height;

    @XmlElement(name = "width")
    private String width;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }
}
