package com.bt.nextgen.service.integration.options.model;

import com.bt.nextgen.core.domain.key.AbstractKey;

import javax.jdo.annotations.Inheritance;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
@Inheritance
public class OptionKey extends AbstractKey implements Serializable {
    private static final long serialVersionUID = 3275634872566583493L;

    @Column(name = "OPTION_NAME")
    private String optionName;

    private OptionKey() {
        // for jpa;
    }

    private OptionKey(String optionName) {
        this.optionName = optionName;
    }

    public static OptionKey valueOf(String optionName) {
        if (optionName == null)
            return null;
        else
            return new OptionKey(optionName);
    }

    public String getOptionName() {
        return optionName;
    }

    public void getOptionName(String optionName) {
        this.optionName = optionName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((optionName == null) ? 0 : optionName.hashCode());
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
        OptionKey other = (OptionKey) obj;
        if (optionName == null) {
            if (other.optionName != null)
                return false;
        } else if (!optionName.equals(other.optionName))
            return false;
        return true;
    }

}
