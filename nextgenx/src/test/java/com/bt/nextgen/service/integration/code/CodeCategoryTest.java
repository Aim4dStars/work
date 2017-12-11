package com.bt.nextgen.service.integration.code;

import com.btfin.panorama.core.conversion.CodeCategory;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class CodeCategoryTest {

    @Test
    public void noDuplicateCodes() {
        final Map<String, CodeCategory> codes = new HashMap<>();
        for (CodeCategory category : CodeCategory.values()) {
            final String code = category.getCode();
            final CodeCategory existing = codes.get(code);
            if (existing != null) {
                Assert.assertSame("Duplicate non-aliased code \"" + code + "\" for both " + existing + " and " + category,
                        existing, category.getAlias());
            }
            codes.put(code, category);
        }
    }

    @Test
    public void allAliasedCategoriesHaveSameCodeAsTheirAlias() {
        for (CodeCategory category : CodeCategory.values()) {
            final CodeCategory alias = category.getAlias();
            if (alias != null) {
                assertEquals(category + " aliased to " + alias + ", but does not have the same code!",
                        alias.getCode(), category.getCode());
            }
        }
    }
}
