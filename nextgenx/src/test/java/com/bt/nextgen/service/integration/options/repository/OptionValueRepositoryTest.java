package com.bt.nextgen.service.integration.options.repository;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.integration.options.model.CategoryKey;
import com.bt.nextgen.service.integration.options.model.CategoryType;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.model.OptionType;
import com.bt.nextgen.service.integration.options.model.OptionValue;
import com.bt.nextgen.service.integration.options.model.OptionValueKey;

@TransactionConfiguration
public class OptionValueRepositoryTest extends BaseSecureIntegrationTest {
    @Autowired
    private OptionValueRepository repo;

    @Test
    @Transactional
    public void testFindToggleOption() throws Exception {
        OptionValue<Boolean> result = repo
.findToggleOptionValue(OptionValueKey.valueOf(CategoryType.PRODUCT, "SUP",
                "report.accountdetail.bankaccount"));
        Assert.assertNotNull(result);
        Assert.assertFalse(result.getValue());
    }

    @Test
    @Transactional
    public void testFindStringOption() throws Exception {
        OptionValue<String> result = repo
.findStringOptionValue(OptionValueKey.valueOf(CategoryType.PRODUCT, "SUP",
                "cash.account.description"));
        Assert.assertEquals("Cash account", result.getValue());
    }

    @Test
    @Transactional
    public void testSearchToggleOption() throws Exception {
        Collection<OptionValue<Boolean>> result = repo.searchToggleOptions(OptionType.PRODUCT_TOGGLE,
                CategoryKey.valueOf(CategoryType.PRODUCT, "SUP"));
        Assert.assertNotNull(result);
        OptionValue<Boolean> testOption = null;
        for (OptionValue<Boolean> toggleOption : result) {
            if (toggleOption.getOptionValueKey().getOptionKey().equals(OptionKey.valueOf("report.accountdetail.bankaccount"))) {
                testOption = toggleOption;
            }
        }
        Assert.assertNotNull(testOption);
        Assert.assertFalse(testOption.getValue());
    }

    @Test
    @Transactional
    public void testSearchStringOption() throws Exception {
        Collection<OptionValue<String>> result = repo.searchStringOptions(OptionType.PRODUCT_OPTION,
                CategoryKey.valueOf(CategoryType.PRODUCT, "SUP"));
        Assert.assertNotNull(result);
        OptionValue<String> testOption = null;
        for (OptionValue<String> toggleOption : result) {
            if (toggleOption.getOptionValueKey().getOptionKey().equals(OptionKey.valueOf("cash.account.description"))) {
                testOption = toggleOption;
            }
        }
        Assert.assertNotNull(testOption);
        Assert.assertEquals("Cash account", testOption.getValue());
    }
}
