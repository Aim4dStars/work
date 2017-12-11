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
public class CategoryKey extends AbstractKey implements Serializable {

    @Column(name = "CATEGORY_TYPE")
    @Enumerated(EnumType.STRING)
    private CategoryType category;

    @Column(name = "CATEGORY_KEY")
    private String categoryId;

    private CategoryKey(CategoryType category, String categoryId) {
        super();
        this.category = category;
        this.categoryId = categoryId;
    }

    public CategoryType getCategory() {
        return category;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public static CategoryKey valueOf(CategoryType category, String categoryId) {
        if (category == null || categoryId == null) {
            return null;
        }
        return new CategoryKey(category, categoryId);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((categoryId == null) ? 0 : categoryId.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142" }) // IDE Generated
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CategoryKey other = (CategoryKey) obj;
        if (category != other.category)
            return false;
        if (categoryId == null) {
            if (other.categoryId != null)
                return false;
        } else if (!categoryId.equals(other.categoryId))
            return false;
        return true;
    }

}
