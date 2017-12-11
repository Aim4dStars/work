package com.bt.nextgen.service.integration.user.notices.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Embeddable
public class NoticesKey implements Serializable{

    @Column(name = "NOTICE_ID")
    @Enumerated(EnumType.STRING)
    private NoticeType noticeTypeId;

    @Column(name = "VERSION")
    private Integer version;

    public NoticesKey() {
        // Default constructor for hibernate
    }

    public NoticesKey(NoticeType noticeTypeId, Integer version) {
        this.noticeTypeId = noticeTypeId;
        this.version = version;
    }

    public NoticeType getNoticeTypeId() {
        return noticeTypeId;
    }

    public Integer getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((noticeTypeId == null) ? 0 : noticeTypeId.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings({"squid:MethodCyclomaticComplexity", "squid:S1142"})
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NoticesKey other = (NoticesKey) obj;
        if (noticeTypeId != other.noticeTypeId) {
            return false;
        }
        if (version == null) {
            if (version != other.version) {
                return false;
            }
        } else if (!version.equals(other.version)) {
            return false;
        }
        return true;
    }
}
