package com.bt.nextgen.service.integration.termsandconditions.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.io.Serializable;

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "TNC_REF")
public class TermsAndConditions implements Serializable
{
    private static final long serialVersionUID = -57950251941603983L;

    @EmbeddedId
    private TermsAndConditionsKey userTermsAndConditionsKey;

    @Column(name = "MODIFY_DATETIME")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime lastModified;

    @Column(name = "DESCRIPTION")
    private String description;

    public TermsAndConditionsKey getUserTermsAndConditionsKey() {
        return userTermsAndConditionsKey;
    }

    public DateTime getLastModified() {
        return lastModified.toDateTime();
    }

    public String getDescription() {
        return description;
    }
}