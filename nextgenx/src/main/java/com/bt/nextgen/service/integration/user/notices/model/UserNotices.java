package com.bt.nextgen.service.integration.user.notices.model;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "USER_NOTICES")
public class UserNotices implements Serializable {

    @EmbeddedId
    private UserNoticesKey userNoticesKey;

    @Column(name = "LAST_VIEWED_ON")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime lastViewedOn;

    public UserNotices() {
        // default constructor for hibernate
    }

    public UserNotices(UserNoticesKey userNoticesKey) {
        this.userNoticesKey = userNoticesKey;
        this.lastViewedOn = new LocalDateTime();
    }

    public UserNoticesKey getUserNoticesKey() {
        return userNoticesKey;
    }

    public DateTime getLastViewedOn() {
        if (lastViewedOn == null)
            return null;
        return lastViewedOn.toDateTime();
    }
}
