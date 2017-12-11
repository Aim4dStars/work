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
@Table(name = "NOTICE_TYPE_REF")
public class Notices implements Serializable{

    @EmbeddedId
    private NoticesKey noticesKey;

    @Column(name = "LAST_UPDATED_ON")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime lastUpdatedOn;

    @Column(name = "DESCRIPTION")
    private String description;

    public Notices() {
        // for hibernate
    }

    public Notices(NoticesKey noticesKey, String description) {
        this.noticesKey = noticesKey;
        this.lastUpdatedOn = new LocalDateTime();
        this.description = description;
    }

    public NoticesKey getNoticesKey() {
        return noticesKey;
    }

    public DateTime getLastUpdatedOn() {
        if (lastUpdatedOn == null) {
            return null;
        }
        return lastUpdatedOn.toDateTime();
    }

    public String getDescription() {
        return description;
    }
}

