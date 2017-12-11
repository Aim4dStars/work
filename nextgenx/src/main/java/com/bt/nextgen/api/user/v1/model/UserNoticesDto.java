package com.bt.nextgen.api.user.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import org.joda.time.DateTime;

public class UserNoticesDto extends BaseDto implements KeyedDto<UserNoticesDtoKey> {

    private UserNoticesDtoKey key;
    private String noticeTypeName;
    private String description;
    private DateTime lastUpdatedOn;

    public UserNoticesDto(UserNoticesDtoKey userNoticesDtoKey) {
        this.key = userNoticesDtoKey;
    }

    public UserNoticesDto(UserNoticesDtoKey userNoticesDtoKey, String noticeTypeName, String description, DateTime lastUpdatedOn) {
        this.key = userNoticesDtoKey;
        this.noticeTypeName = noticeTypeName;
        this.description = description;
        this.lastUpdatedOn = lastUpdatedOn;
    }

    public void setKey(UserNoticesDtoKey key) {
        this.key = key;
    }

    public String getNoticeTypeName() {
        return noticeTypeName;
    }

    public void setNoticeTypeName(String noticeTypeName) {
        this.noticeTypeName = noticeTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DateTime getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(DateTime lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }

    @Override
    public UserNoticesDtoKey getKey() {
        return key;
    }
}
