package com.bt.nextgen.service.integration.marketondemand;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
public class RssFeedItem {

    @XmlElement(name = "title")
    private String title;

    @XmlElement(name = "description")
    private String description;

    @XmlElement(name = "link")
    private String documentLink;

    @XmlElement(name = "pubDate")
    private String publishDate;

    @XmlElement(name = "guid")
    private String audioLink;

    @XmlElement(name = "duration", namespace = "http://www.itunes.com/dtds/podcast-1.0.dtd")
    private String duration;

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

    public String getDocumentLink() {
        return documentLink;
    }

    public void setDocumentLink(String documentLink) {
        this.documentLink = documentLink;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getAudioLink() {
        return audioLink;
    }

    public void setAudioLink(String audioLink) {
        this.audioLink = audioLink;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
