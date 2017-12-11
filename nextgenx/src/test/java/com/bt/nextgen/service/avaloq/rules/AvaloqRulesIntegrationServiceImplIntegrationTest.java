package com.bt.nextgen.service.avaloq.rules;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.concurrent.Future;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.util.AssertionErrors.assertEquals;

/**
 * Created by M041926 on 5/10/2016.
 */
public class AvaloqRulesIntegrationServiceImplIntegrationTest extends BaseSecureIntegrationTest {

    @Autowired
    private AvaloqRulesIntegrationService rulesIntegrationService;

    @Test
    public void retrieveTwoFaRule() throws Exception {
        RuleImpl rule = rulesIntegrationService.retrieveTwoFaRule(RuleType.ACC_ACTIV, Collections.singletonMap(RuleCond.GCM_ID, "123456789"), new FailFastErrorsImpl());
        assertNotNull(rule);
        assertNotNull("Expect rule id is set", rule.getRuleId());
        assertNotNull("Expect action is set", rule.getAction());
        assertEquals("Expect CHECK action", rule.getAction(), RuleAction.CHK);
    }

    @Test
    public void ruleNotExists() throws Exception {
        RuleImpl rule = rulesIntegrationService.retrieveTwoFaRule(RuleType.ACC_ACTIV, Collections.singletonMap(RuleCond.GCM_ID,"201617777"), new FailFastErrorsImpl());
        assertNull("Expect rule is null", rule);
    }

    @Ignore
    @Test
    public void testUpdateAvaloqRuleAsync() throws Exception {
        Future<RuleUpdateStatus> f = rulesIntegrationService.updateAvaloqRuleAsync("123456789", Collections.singletonMap(RuleUpdateParams.STATUS, Boolean.valueOf(true)));
        RuleUpdateStatus resp = f.get();
        assertNotNull(resp);
        assertTrue("Expect rule to be found in avaloq", resp.isRuleFound());
    }

    @Test
    public void testUpdateAvaloqRule() throws Exception {
        RuleUpdateStatus f = rulesIntegrationService.updateAvaloqRule("123456789", Collections.singletonMap(RuleUpdateParams.STATUS, Boolean.valueOf(true)), new FailFastErrorsImpl());
        assertNotNull(f);
        assertTrue("Expect rule to be found in avaloq",  f.isRuleFound());
    }
}