package com.bt.nextgen.api.user.v1.model;


public class UserNoticesDtoKey {

    private String userId;
    private String noticeId;
    private Integer version;

    public UserNoticesDtoKey(String userId, String noticeId, Integer version) {
        this.userId = userId;
        this.noticeId = noticeId;
        this.version = version;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
