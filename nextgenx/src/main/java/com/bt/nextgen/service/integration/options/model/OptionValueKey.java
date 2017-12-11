package com.bt.nextgen.service.integration.options.model;

import com.bt.nextgen.core.domain.key.AbstractKey;

import javax.jdo.annotations.Inheritance;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import java.io.Serializable;

@Embeddable
@Inheritance
public class OptionValueKey extends AbstractKey implements Serializable {
    @Column(name = "CATEGORY_TYPE")
    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;

    @Column(name = "CATEGORY_KEY")
    private String categoryId;

    @Column(name = "OPTION_NAME")
    private String optionName;

    private OptionValueKey() {
        // for jpa
    }

    public OptionValueKey(CategoryType categoryType, String categoryKey, String optionName) {
        super();
        this.categoryType = categoryType;
        this.categoryId = categoryKey;
        this.optionName = optionName;
    }

    public CategoryKey getCategoryKey() {
        return CategoryKey.valueOf(categoryType, categoryId);
    }

    public OptionKey getOptionKey() {
        return OptionKey.valueOf(optionName);
    }

    public static OptionValueKey valueOf(CategoryType category, String categoryId, String optionName) {
        if (optionName == null) {
            return null;
        }
        return new OptionValueKey(category, categoryId, optionName);
    }

    public static OptionValueKey valueOf(CategoryType category, CategoryKey categoryKey, OptionKey optionKey) {
        if (optionKey == null) {
            return null;
        }
        String categoryId = null;
        if (categoryKey != null) {
            categoryId = categoryKey.getCategoryId();
        }
        return new OptionValueKey(category, categoryId, optionKey.getOptionName());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((categoryType == null) ? 0 : categoryType.hashCode());
        result = prime * result + ((categoryId == null) ? 0 : categoryId.hashCode());
        result = prime * result + ((optionName == null) ? 0 : optionName.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142",
            "checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.NPathComplexityCheck" }) // IDE Generated
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OptionValueKey other = (OptionValueKey) obj;
        if (categoryType != other.categoryType)
            return false;
        if (categoryId == null) {
            if (other.categoryId != null)
                return false;
        } else if (!categoryId.equals(other.categoryId))
            return false;
        if (optionName == null) {
            if (other.optionName != null)
                return false;
        } else if (!optionName.equals(other.optionName))
            return false;
        return true;
    }

}
