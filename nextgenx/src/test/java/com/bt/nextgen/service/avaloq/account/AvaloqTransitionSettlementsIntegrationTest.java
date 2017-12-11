package com.bt.nextgen.service.avaloq.account;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.TransitionSettlementsHolder;
import com.bt.nextgen.service.integration.account.TransitionSettlementsIntegrationService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class AvaloqTransitionSettlementsIntegrationTest extends BaseSecureIntegrationTest {

    @Autowired
    @Qualifier("avaloqTransitionSettlementsIntegrationService")
    private TransitionSettlementsIntegrationService transitionSettlementsIntegrationService;

    private ServiceErrors serviceErrors = null;
    private AccountKey accountKey;
    private String accountId = "176058";

    @Before
    public void setUp() throws Exception {
        accountKey = AccountKey.valueOf(accountId);
    }

    @SecureTestContext
    @Test
    public void getAssetTransferStatusShouldBeProvidedForAnAccountKey() {
        TransitionSettlementsHolder response = transitionSettlementsIntegrationService.getAssetTransferStatus(accountKey, serviceErrors);
        assertNotNull(response);
        assertNotNull(response.getAccountKey().getId());
        assertThat(response.getAccountKey().getId(), is(accountId));
        assertThat(response.getTransitionSettlements().size(),is(1));

        assertThat(response.getTransitionSettlements().get(0).getAmount().doubleValue(),is(35.47));
        assertThat(response.getTransitionSettlements().get(0).getQuantity(),is("35"));
        assertThat(response.getTransitionSettlements().get(0).getOrderNumber() ,is("1152430"));
        assertThat(response.getTransitionSettlements().get(0).getAssetKey().getId() ,is("121308"));
        assertThat(response.getTransitionSettlements().get(0).getTransitionDate().toLocalDate().toString(),is("2015-09-28"));
        assertThat(response.getTransitionSettlements().get(0).getTransitionWorkflowStatus(),is(ApplicationStatus.DONE));
    }
}
