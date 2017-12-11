package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.AbstractJsonObjectMapperTest;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.DirectClientApplicationFormData;
import org.junit.Test;

import java.io.IOException;

import static com.bt.nextgen.api.draftaccount.model.form.v1.ClientApplicationFormFactoryV1.getNewDirectClientApplicationForm;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class DirectClientApplicationFormTest extends AbstractJsonObjectMapperTest<DirectClientApplicationFormData> {

    private IClientApplicationForm form;

    public DirectClientApplicationFormTest() {
        super(DirectClientApplicationFormData.class);
    }

    @Test
    public void forDirectApplications_ApplicationApprovalTypeShouldBeOnline() throws IOException {
        this.form = getNewDirectClientApplicationForm(readJsonResource("individual-direct"));
        assertThat(form.getApplicationApprovalType(),is(IClientApplicationForm.ApprovalType.ONLINE));
        assertThat(form.getAccountType(),is(IClientApplicationForm.AccountType.INDIVIDUAL));
    }

    @Test
    public void forDirectApplications_AccountTypePassedSuperAccumulation() throws IOException {
        this.form = getNewDirectClientApplicationForm(readJsonResource("individual-direct-superAccumulation"));
        assertThat(form.getAccountType(),is(IClientApplicationForm.AccountType.SUPER_ACCUMULATION));
        assertNull(form.getPensionEligibility());
    }

    @Test
    public void forDirectApplications_AccountTypePassedSuperPension() throws IOException {
        this.form = getNewDirectClientApplicationForm(readJsonResource("individual-direct-superPension"));
        assertThat(form.getAccountType(),is(IClientApplicationForm.AccountType.SUPER_PENSION));
        assertThat(form.getPensionEligibility().getEligibilityCriteria(),is("UNPSV"));
        assertThat(form.getPensionEligibility().getConditionRelease(),is("OTH"));

    }

    @Test
    public void forDirectApplications_AccountTypeNotPassed() throws IOException {
        this.form = getNewDirectClientApplicationForm(readJsonResource("individual-direct_noAccountTypePassed"));
        assertThat(form.getAccountType(),is(IClientApplicationForm.AccountType.INDIVIDUAL));
    }

    @Test
    public void forDirectApplicationsWithManuallyEntered_PrimaryLinkedAccountShouldHaveAccountManuallyEnteredFlag() throws IOException {
        this.form = getNewDirectClientApplicationForm(readJsonResource("individual-direct-manually-entered"));
        assertThat(form.getApplicationApprovalType(),is(IClientApplicationForm.ApprovalType.ONLINE));
        assertThat(form.getLinkedAccounts().getPrimaryLinkedAccount().isAccountManuallyEntered(),is(true));

    }

}