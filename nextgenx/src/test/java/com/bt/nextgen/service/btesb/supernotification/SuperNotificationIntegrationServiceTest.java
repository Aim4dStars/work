package com.bt.nextgen.service.btesb.supernotification;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.btesb.supermatch.model.MemberImpl;
import com.bt.nextgen.service.btesb.supermatch.model.SuperFundAccountImpl;
import com.bt.nextgen.service.integration.supermatch.Member;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;

@Ignore
public class SuperNotificationIntegrationServiceTest extends BaseSecureIntegrationTest {

    @Autowired
    private SuperNotificationIntegrationServiceImpl superNotificationService;

    @Test
    @SecureTestContext(authorities = {"ROLE_INVESTOR"}, username = "notification", jobId = "1231224112", customerId = "133331113", profileId = "144412321", jobRole = "INVESTOR")
    public void notifyCustomer() throws Exception {
        String customerId = "74061351";
        SuperFundAccountImpl superFundAccount = new SuperFundAccountImpl();
        final MemberImpl member = new MemberImpl();

        member.setCustomerId(customerId);
        member.setIssuer("WestpacLegacy");
        member.setFirstName("Jon");
        member.setLastName("Smith");
        member.setDateOfBirth(new DateTime("1990-01-01"));

        List<String> emails = new ArrayList<>();
        emails.add("abcd@gmail.com");
        member.setEmailAddresses(emails);

        superFundAccount.setMembers(Collections.<Member>singletonList(member));
        assertTrue(superNotificationService.notifyCustomer(customerId, superFundAccount, new ServiceErrorsImpl()));
    }
}