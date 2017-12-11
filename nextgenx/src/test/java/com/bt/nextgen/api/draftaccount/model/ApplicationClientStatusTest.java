package com.bt.nextgen.api.draftaccount.model;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ApplicationClientStatusTest {

    @Test
    public void shouldReturnApprovedWhenClientIsAnApproverAndHasApproved() throws Exception {
        assertThat(ApplicationClientStatus.getStatus(true, true, true), is(ApplicationClientStatus.APPROVED));
    }

    @Test
    public void shouldReturnAwaitingApprovalWhenClientIsAnApproverAndIsRegisteredAndHasNotApprovedYet() throws Exception {
        assertThat(ApplicationClientStatus.getStatus(true, true, false), is(ApplicationClientStatus.AWAITING_APPROVAL));
    }

    @Test
    public void shouldReturnNotRegisteredWhenClientIsAnApproverAndHasNotRegisteredAndHasNotApprovedYet() throws Exception {
        assertThat(ApplicationClientStatus.getStatus(true, false, false), is(ApplicationClientStatus.NOT_REGISTERED));
    }

    @Test
    public void shouldReturnNotRegisteredWhenClientIsANonApproverAndHasNotRegistered() throws Exception {
        assertThat(ApplicationClientStatus.getStatus(false, false, false), is(ApplicationClientStatus.NOT_REGISTERED));
    }

    @Test
    public void shouldReturnRegisteredWhenClientIsANonApproverAndHasRegistered() throws Exception {
        assertThat(ApplicationClientStatus.getStatus(false, true, false), is(ApplicationClientStatus.REGISTERED));
    }
}