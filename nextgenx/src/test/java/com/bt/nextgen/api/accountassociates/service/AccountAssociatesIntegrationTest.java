package com.bt.nextgen.api.accountassociates.service;

import com.bt.nextgen.api.accountassociates.model.AccountAssociateDto;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by L078878 on 15/12/2015.
 */
/**
 * This test is disabled while a race condition in the multithreaded load is investigated.
 *
 */
@Ignore
public class AccountAssociatesIntegrationTest extends BaseSecureIntegrationTest {

    @Autowired
    private AccountAssociatesDtoService accountAssociatesDtoService;

    private ServiceErrors errors = new ServiceErrorsImpl();

    @SecureTestContext(authorities = {
            "ROLE_ADVISER" }, username = "notification", jobId = "1231224112", customerId = "133331113", profileId = "144412321", jobRole = "ADVISER")
    @Test
    public void testGetAllAccountsWithAssociatedClients() {
        List<AccountAssociateDto> response = accountAssociatesDtoService.findAll(errors);
        assertNotNull(response);
        Assert.assertThat(response.size(), Is.is(4));
        Assert.assertThat(response.get(0).getClientName(), Is.is("person-120_286person-120_286person-120_286"));
        Assert.assertThat(response.get(1).getClientName(), Is.is("person-120_4606person-120_4606person-120_4606"));
        Assert.assertThat(response.get(2).getClientName(), Is.is("person-120_4208person-120_4208person-120_4208"));
        Assert.assertThat(response.get(3).getClientName(), Is.is("DDtest UAT"));

    }
}
