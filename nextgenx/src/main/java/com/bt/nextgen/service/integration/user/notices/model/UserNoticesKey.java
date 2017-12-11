package com.bt.nextgen.service.integration.user.notices.model;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Embeddable
public class UserNoticesKey implements Serializable {

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "NOTICE_ID")
    @Enumerated(EnumType.STRING)
    private NoticeType noticeTypeId;

    @Column(name = "VERSION")
    private Integer version;


    public UserNoticesKey() {
        // default constructor for hibernate
    }

    public UserNoticesKey(String userId, NoticeType noticeTypeId, Integer version) {
        this.userId = userId;
        this.noticeTypeId = noticeTypeId;
        this.version = version;
    }

    public NoticeType getNoticeTypeId() {
        return noticeTypeId;
    }

    public String getUserId() {
        return userId;
    }

    public Integer getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        result = prime * result + ((noticeTypeId == null) ? 0 : noticeTypeId.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142",
            "checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.NPathComplexityCheck" })
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserNoticesKey other = (UserNoticesKey) obj;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        if (noticeTypeId != other.noticeTypeId)
            return false;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        return true;
    }
}
