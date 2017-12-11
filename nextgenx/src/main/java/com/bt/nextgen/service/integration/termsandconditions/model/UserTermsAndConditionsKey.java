package com.bt.nextgen.service.integration.termsandconditions.model;


import com.bt.nextgen.core.domain.key.AbstractKey;

import javax.jdo.annotations.Inheritance;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import java.io.Serializable;

@Embeddable
@Inheritance
public class UserTermsAndConditionsKey extends AbstractKey implements Serializable 
{
    private static final long serialVersionUID = -4368307849236515377L;

    @Column(name = "TNC_ID")
    @Enumerated(EnumType.STRING)
    private TermsAndConditionsType tncId;

    @Column(name = "VERSION")
    private Integer version;

    @Column(name = "USER_ID")
	private String gcmId;

    public UserTermsAndConditionsKey() {
        // default constructor for hibernate
    }

    public UserTermsAndConditionsKey(String gcmId, TermsAndConditionsType tncId, Integer version)
	{
        this.tncId = tncId;
        this.version = version;
        this.gcmId = gcmId;
	}

    public TermsAndConditionsType getTncId() {
        return tncId;
    }

    public Integer getVersion() {
        return version;
    }

    public String getGcmId() {
        return gcmId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((gcmId == null) ? 0 : gcmId.hashCode());
        result = prime * result + ((tncId == null) ? 0 : tncId.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142" })
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserTermsAndConditionsKey other = (UserTermsAndConditionsKey) obj;
        if (gcmId == null) {
            if (other.gcmId != null)
                return false;
        } else if (!gcmId.equals(other.gcmId))
            return false;
        if (tncId != other.tncId)
            return false;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        return true;
    }
}
