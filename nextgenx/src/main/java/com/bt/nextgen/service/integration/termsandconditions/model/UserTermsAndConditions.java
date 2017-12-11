package com.bt.nextgen.service.integration.termsandconditions.model;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.io.Serializable;


@Entity
@Table(name = "USER_TNC")
@SuppressWarnings({"findbugs:EI_EXPOSE_REP2"})
public class UserTermsAndConditions implements Serializable
{
	@EmbeddedId
    private UserTermsAndConditionsKey userTermsAndConditionsKey;

    @Column(name = "TNC_ACCEPTED_ON")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime tncAcceptedOn;

    public UserTermsAndConditions() {
        // default constructor for hibernate
    }

    public UserTermsAndConditions(UserTermsAndConditionsKey userTermsAndConditionsKey) {
        this.userTermsAndConditionsKey = userTermsAndConditionsKey;
        this.tncAcceptedOn = new LocalDateTime();
    }

    public UserTermsAndConditionsKey getUserTermsAndConditionsKey() {
        return userTermsAndConditionsKey;
    }

    public DateTime getTncAcceptedOn()
	{
        if (tncAcceptedOn == null)
            return null;
        return tncAcceptedOn.toDateTime();
	}
}