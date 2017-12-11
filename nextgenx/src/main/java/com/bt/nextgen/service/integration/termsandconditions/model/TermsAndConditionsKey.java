package com.bt.nextgen.service.integration.termsandconditions.model;

import com.bt.nextgen.core.domain.key.AbstractKey;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import java.io.Serializable;

@Embeddable
public class TermsAndConditionsKey extends AbstractKey implements Serializable {
    private static final long serialVersionUID = 5723233911417941653L;

    @Column(name = "TNC_ID")
    @Enumerated(EnumType.STRING)
    private TermsAndConditionsType tncId;

    @Column(name = "VERSION")
    private Integer version;

    public TermsAndConditionsKey() {
        // default constructor for hibernate
    }

    public TermsAndConditionsKey(TermsAndConditionsType tncId, Integer version) {
        this.tncId = tncId;
        this.version = version;
    }

    public TermsAndConditionsType getTncId() {
        return tncId;
    }

    public Integer getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((tncId == null) ? 0 : tncId.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }

    @Override
    // ide generated
    @SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142",
            "checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.NPathComplexityCheck" })
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TermsAndConditionsKey other = (TermsAndConditionsKey) obj;
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
