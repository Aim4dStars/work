package com.bt.nextgen.service.integration.options.repository;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.integration.options.model.Option;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.model.OptionType;

@TransactionConfiguration
public class OptionRepositoryTest extends BaseSecureIntegrationTest {
    @Autowired
    private OptionRepository repo;

    @Test
    @Transactional
    public void testLoadTestToggleOption() throws Exception {
        Collection<Option<Boolean>> result = repo.searchToggleOptions(OptionType.PRODUCT_TOGGLE);
        Assert.assertNotNull(result);
        Option<Boolean> testOption = null;
        for (Option<Boolean> toggleOption : result) {
            if (toggleOption.getOptionKey().equals(OptionKey.valueOf("declaration.investorguide"))) {
                testOption = toggleOption;
            }
            Assert.assertEquals(OptionType.PRODUCT_TOGGLE, toggleOption.getOptionType());
        }
        Assert.assertNotNull(testOption);
        Assert.assertTrue(testOption.getDefaultValue());
    }

    @Test
    @Transactional
    public void testLoadTestStringOption() throws Exception {
        Collection<Option<String>> result = repo.searchStringOptions(OptionType.PRODUCT_OPTION);
        Assert.assertNotNull(result);
        Option<String> testOption = null;
        for (Option<String> toggleOption : result) {
            if (toggleOption.getOptionKey().equals(OptionKey.valueOf("cash.account.description"))) {
                testOption = toggleOption;
            }
            Assert.assertEquals(OptionType.PRODUCT_OPTION, toggleOption.getOptionType());
        }
        Assert.assertNotNull(testOption);
        Assert.assertEquals("Cash", testOption.getDefaultValue());
        Assert.assertTrue(testOption.getOptionType() == OptionType.PRODUCT_OPTION);
    }

}
