package com.bt.nextgen.service.integration.termsandconditions.repository;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.integration.termsandconditions.model.TermsAndConditions;
import com.bt.nextgen.service.integration.termsandconditions.model.TermsAndConditionsType;
import com.bt.nextgen.service.integration.termsandconditions.model.UserTermsAndConditions;
import com.bt.nextgen.service.integration.termsandconditions.model.UserTermsAndConditionsKey;
import com.bt.nextgen.service.integration.user.UserKey;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@TransactionConfiguration
public class TermsAndConditionsRepositoryTest extends BaseSecureIntegrationTest {
    @Autowired
    private TermsAndConditionsRepository tncRepository;

    @Autowired
    private UserTermsAndConditionsRepository userRepository;

    @Test
    @Transactional
    @Rollback(true)
    public void testTermAndConditions() throws Exception {
        List<TermsAndConditions> result = tncRepository.findAll();
        Assert.assertEquals(1, result.size());

        Assert.assertEquals(TermsAndConditionsType.TAILORED_PORTFOLIOS, result.get(0).getUserTermsAndConditionsKey().getTncId());
        Assert.assertEquals(Integer.valueOf(1), result.get(0).getUserTermsAndConditionsKey().getVersion());
        Assert.assertEquals("Terms and conditions for access to the tailored portfolios portion of the panorama application",
                result.get(0).getDescription());
        Assert.assertEquals(new DateTime("2016-05-01").toDate(), result.get(0).getLastModified().toDate());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUserTermsAndConditions() throws Exception {
        List<UserTermsAndConditions> userTncs = userRepository.search(UserKey.valueOf("201677777"));
        Assert.assertTrue(userTncs.isEmpty());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testSaveNewUserRole() {
        UserTermsAndConditions tnc = new UserTermsAndConditions(
                new UserTermsAndConditionsKey("201677777", TermsAndConditionsType.TAILORED_PORTFOLIOS, Integer.valueOf(1)));
        DateTime acceptDate = tnc.getTncAcceptedOn();
        userRepository.save(tnc);
        List<UserTermsAndConditions> userTncs = userRepository.search(UserKey.valueOf("201677777"));

        Assert.assertEquals(1, userTncs.size());
        UserTermsAndConditions userTnc = userTncs.get(0);
        Assert.assertEquals("201677777", userTnc.getUserTermsAndConditionsKey().getGcmId());
        Assert.assertEquals(TermsAndConditionsType.TAILORED_PORTFOLIOS, userTnc.getUserTermsAndConditionsKey().getTncId());
        Assert.assertEquals(Integer.valueOf(1), userTnc.getUserTermsAndConditionsKey().getVersion());
        Assert.assertEquals(acceptDate, userTnc.getTncAcceptedOn());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdateUserRole() {
        UserTermsAndConditions tnc = new UserTermsAndConditions(
                new UserTermsAndConditionsKey("201677777", TermsAndConditionsType.TAILORED_PORTFOLIOS, Integer.valueOf(1)));
        userRepository.save(tnc);

        UserTermsAndConditions tnc2 = new UserTermsAndConditions(
                new UserTermsAndConditionsKey("201677777", TermsAndConditionsType.TAILORED_PORTFOLIOS, Integer.valueOf(1)));
        userRepository.save(tnc2);

        DateTime acceptDate = tnc2.getTncAcceptedOn();

        List<UserTermsAndConditions> userTncs = userRepository.search(UserKey.valueOf("201677777"));
        Assert.assertEquals(1, userTncs.size());
        UserTermsAndConditions userTnc = userTncs.get(0);
        Assert.assertEquals("201677777", userTnc.getUserTermsAndConditionsKey().getGcmId());
        Assert.assertEquals(TermsAndConditionsType.TAILORED_PORTFOLIOS, userTnc.getUserTermsAndConditionsKey().getTncId());
        Assert.assertEquals(Integer.valueOf(1), userTnc.getUserTermsAndConditionsKey().getVersion());
        Assert.assertEquals(acceptDate, userTnc.getTncAcceptedOn());
    }
}
