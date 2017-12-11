package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.IPensionEligibilityForm;
import com.bt.nextgen.api.draftaccount.model.form.v1.PensionEligibilityForm;
import ns.btfin_com.product.superannuationretirement.superannuationaccount.v3_0.ConditionsOfReleaseType;
import ns.btfin_com.product.superannuationretirement.superannuationaccount.v3_0.EligibilityTypeType;
import ns.btfin_com.product.superannuationretirement.superannuationaccount.v3_0.SuperAccountType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountPurposeBuilderTest {


    @Test
    public void accountPurposeForAccumulation() {
        IClientApplicationForm form = mock(IClientApplicationForm.class);
        when(form.getAccountType()).thenReturn(IClientApplicationForm.AccountType.SUPER_ACCUMULATION);

        final SuperAccountType accountPurpose = AccountPurposeBuilder.getAccountPurpose(form);
        assertNotNull(accountPurpose.getAccountType().getRetirementScheme());
    }

    @Test
    public void accountPurposeForOtherAccountTypes() {
        IClientApplicationForm form = mock(IClientApplicationForm.class);
        when(form.getAccountType()).thenReturn(IClientApplicationForm.AccountType.INDIVIDUAL);
        final SuperAccountType accountPurpose = AccountPurposeBuilder.getAccountPurpose(form);
        assertNull(accountPurpose);
    }

    @Test
    public void accountPurposeForPension_OnlyEligibilityCriteria() {
        IClientApplicationForm form = getClientApplicationForm();
        when(form.getPensionEligibility().getEligibilityCriteria()).thenReturn("OVER_65");

        SuperAccountType accountPurpose = AccountPurposeBuilder.getAccountPurpose(form);
        assertNotNull(accountPurpose.getAccountType().getPensionFundOrdinary());
        assertThat(accountPurpose.getAccountType().getPensionFundOrdinary().getEligibility(),is(EligibilityTypeType.AGE_65));
        assertNull(accountPurpose.getAccountType().getPensionFundOrdinary().getConditionsOfRelease());
    }

    @Test
    public void accountPurposeForPension_EligibilityCriteriaAndCondOfRel1() {
        IClientApplicationForm form = getClientApplicationForm();
        when(form.getPensionEligibility().getEligibilityCriteria()).thenReturn("UNPSV");
        when(form.getPensionEligibility().getConditionRelease()).thenReturn("TURN_AGE_65");

        SuperAccountType accountPurpose = AccountPurposeBuilder.getAccountPurpose(form);
        assertNotNull(accountPurpose.getAccountType().getPensionFundOrdinary());
        assertThat(accountPurpose.getAccountType().getPensionFundOrdinary().getEligibility(),is(EligibilityTypeType.UNRESTRICTED_NON_PRESERVE));
        assertThat(accountPurpose.getAccountType().getPensionFundOrdinary().getConditionsOfRelease(),is(ConditionsOfReleaseType.AGE_65));
    }

    @Test
    public void accountPurposeForPension_EligibilityCriteriaAndCondOfRel2() {
        IClientApplicationForm form = getClientApplicationForm();
        when(form.getPensionEligibility().getEligibilityCriteria()).thenReturn("UNPSV");
        when(form.getPensionEligibility().getConditionRelease()).thenReturn("LESS_200_LOST_FOUND");

        SuperAccountType accountPurpose = AccountPurposeBuilder.getAccountPurpose(form);
        assertNotNull(accountPurpose.getAccountType().getPensionFundOrdinary());
        assertThat(accountPurpose.getAccountType().getPensionFundOrdinary().getEligibility(),is(EligibilityTypeType.UNRESTRICTED_NON_PRESERVE));
        assertThat(accountPurpose.getAccountType().getPensionFundOrdinary().getConditionsOfRelease(),is(ConditionsOfReleaseType.LOST_AND_FOUND_LESS_THAN_200));
    }

    @Test
    public void accountPurposeForPension_EligibilityCriteriaAndCondOfRel3() {
        IClientApplicationForm form = getClientApplicationForm();
        when(form.getPensionEligibility().getEligibilityCriteria()).thenReturn("UNPSV");
        when(form.getPensionEligibility().getConditionRelease()).thenReturn("OTH");

        SuperAccountType accountPurpose = AccountPurposeBuilder.getAccountPurpose(form);
        assertNotNull(accountPurpose.getAccountType().getPensionFundOrdinary());
        assertThat(accountPurpose.getAccountType().getPensionFundOrdinary().getEligibility(),is(EligibilityTypeType.UNRESTRICTED_NON_PRESERVE));
        assertThat(accountPurpose.getAccountType().getPensionFundOrdinary().getConditionsOfRelease(),is(ConditionsOfReleaseType.OTHER));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void accountPurposeForPension_throwExceptionWhenEligibilityCriteriaIsIncorrect(){
        IClientApplicationForm form = getClientApplicationForm();
        when(form.getPensionEligibility().getEligibilityCriteria()).thenReturn("Something");

        AccountPurposeBuilder.getAccountPurpose(form);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void accountPurposeForPension_throwExceptionWhenCondOfRelIsIncorrect(){
        IClientApplicationForm form = getClientApplicationForm();
        when(form.getPensionEligibility().getEligibilityCriteria()).thenReturn("UNPSV");
        when(form.getPensionEligibility().getConditionRelease()).thenReturn("something");

        AccountPurposeBuilder.getAccountPurpose(form);
    }

    private IClientApplicationForm getClientApplicationForm() {
        IClientApplicationForm form = mock(IClientApplicationForm.class);
        when(form.getAccountType()).thenReturn(IClientApplicationForm.AccountType.SUPER_PENSION);
        IPensionEligibilityForm pensionEligibilityForm = Mockito.mock(PensionEligibilityForm.class);
        when(form.getPensionEligibility()).thenReturn(pensionEligibilityForm);
        return form;
    }


}