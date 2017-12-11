package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.model.form.*;
import ns.btfin_com.party.v3_0.PurposeOfBusinessRelationshipIndType;
import ns.btfin_com.party.v3_0.PurposeOfBusinessRelationshipOrgType;
import ns.btfin_com.party.v3_0.SourceOfFundsIndType;
import ns.btfin_com.party.v3_0.SourceOfFundsOrgType;
import ns.btfin_com.party.v3_0.SourceOfWealthIndType;
import ns.btfin_com.party.v3_0.SourceOfWealthOrgType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static ns.btfin_com.party.v3_0.PurposeOfBusinessType.WEALTH;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for the {@code PurposeOfBusinessRelationshipTypeBuilder}
 */
@RunWith(MockitoJUnitRunner.class)
public class PurposeOfBusinessRelationshipTypeBuilderTest {

    @Mock
    private IPersonDetailsForm personForm;

    @Mock
    private IOrganisationForm organisationForm;

    @Mock
    private IAccountSettingsForm accountSettingsForm;

    private PurposeOfBusinessRelationshipTypeBuilder purposeOfBusinessRelationshipTypeBuilder = new PurposeOfBusinessRelationshipTypeBuilder();

    @Before
    public void initBuilder() {
        when(accountSettingsForm.hasSourceOfFunds()).thenReturn(true);
    }

    @Test
    public void individualPurposeWithAdditionalSOF() {
        when(accountSettingsForm.getSourceOfFunds()).thenReturn("Additional Sources");
        when(accountSettingsForm.getAdditionalSourceOfFunds()).thenReturn("additional sof text");
        when(personForm.hasSourceOfWealth()).thenReturn(true);
        when(personForm.getSourceOfWealth()).thenReturn("Compensation payment");

        final PurposeOfBusinessRelationshipIndType purpose = purposeOfBusinessRelationshipTypeBuilder.purpose(personForm, accountSettingsForm);
        assertThat(purpose.getPurposeOfBusiness(), contains(WEALTH));

        assertThat(purpose.getSourceOfWealth(), contains(SourceOfWealthIndType.COMPENSATION_PAYMENT));
        assertThat(purpose.getSourceOfFunds(), contains(SourceOfFundsIndType.ADDITIONAL_SOURCES));
        assertThat(purpose.getAdditionalSourceOfFunds(), is("additional sof text"));
    }

    @Test
    public void individualPurposeWithAdditionalSOFAndAdditionalSOW() {
        when(accountSettingsForm.getSourceOfFunds()).thenReturn("Additional Sources");
        when(accountSettingsForm.getAdditionalSourceOfFunds()).thenReturn("ADDITIONAL SOF TEXT");
        when(personForm.hasSourceOfWealth()).thenReturn(true);
        when(personForm.getSourceOfWealth()).thenReturn("Additional Sources");
        when(personForm.getAdditionalSourceOfWealth()).thenReturn("ADD SOW");

        final PurposeOfBusinessRelationshipIndType purpose = purposeOfBusinessRelationshipTypeBuilder.purpose(personForm, accountSettingsForm);
        assertThat(purpose.getPurposeOfBusiness(), contains(WEALTH));

        assertThat(purpose.getSourceOfWealth(), contains(SourceOfWealthIndType.ADDITIONAL_SOURCES));
        assertThat(purpose.getAdditionalSourceOfWealth(), is("ADD SOW"));

        assertThat(purpose.getSourceOfFunds(), contains(SourceOfFundsIndType.ADDITIONAL_SOURCES));
        assertThat(purpose.getAdditionalSourceOfFunds(), is("ADDITIONAL SOF TEXT"));
    }

    @Test
    public void individualDirectorPurposeOfBusiness() {
        IDirectorDetailsForm directorDetailsForm = mock(IDirectorDetailsForm.class);

        final PurposeOfBusinessRelationshipIndType purpose = purposeOfBusinessRelationshipTypeBuilder.purpose(directorDetailsForm, accountSettingsForm);
        assertThat(purpose.getPurposeOfBusiness(), contains(WEALTH));

        assertThat(purpose.getSourceOfWealth(), contains(SourceOfWealthIndType.SIGNATORY));
        assertThat(purpose.getSourceOfFunds(), contains(SourceOfFundsIndType.SIGNATORY));
    }

    @Test
    public void individualDirectorPurposeOfBusinessWhenGCMRetrieved(){
        IDirectorDetailsForm directorDetailsForm = mock(IDirectorDetailsForm.class);
        when(directorDetailsForm.isGcmRetrievedPerson()).thenReturn(true);

        final PurposeOfBusinessRelationshipIndType purpose = purposeOfBusinessRelationshipTypeBuilder.purpose(directorDetailsForm, accountSettingsForm);
        assertNull(purpose);

    }

    @Test
    public void individualTrusteePurposeOfBusiness() {
        ITrusteeDetailsForm trusteeDetailsForm = mock(ITrusteeDetailsForm.class);

        final PurposeOfBusinessRelationshipIndType purpose = purposeOfBusinessRelationshipTypeBuilder.purpose(trusteeDetailsForm, accountSettingsForm);
        assertThat(purpose.getPurposeOfBusiness(), contains(WEALTH));

        assertThat(purpose.getSourceOfWealth(), contains(SourceOfWealthIndType.SIGNATORY));
        assertThat(purpose.getSourceOfFunds(), contains(SourceOfFundsIndType.SIGNATORY));
    }

    @Test
    public void individualPersonWithNoSofOrSow() {
        when(personForm.getSourceOfWealth()).thenReturn(null);
        final PurposeOfBusinessRelationshipIndType purpose = purposeOfBusinessRelationshipTypeBuilder.purpose(personForm, accountSettingsForm);

        assertNull(purpose);
    }


    @Test
    public void individualPurposeWithNoAdditionalSources() {
        when(accountSettingsForm.getSourceOfFunds()).thenReturn("Inheritance");

        when(personForm.hasSourceOfWealth()).thenReturn(true);
        when(personForm.getSourceOfWealth()).thenReturn("Compensation payment");

        final PurposeOfBusinessRelationshipIndType purpose = purposeOfBusinessRelationshipTypeBuilder.purpose(personForm, accountSettingsForm);
        assertThat(purpose.getPurposeOfBusiness(), contains(WEALTH));

        assertThat(purpose.getSourceOfWealth(), contains(SourceOfWealthIndType.COMPENSATION_PAYMENT));
        assertNull(purpose.getAdditionalSourceOfWealth());

        assertThat(purpose.getSourceOfFunds(), contains(SourceOfFundsIndType.INHERITANCE));
        assertNull(purpose.getAdditionalSourceOfFunds());
    }

    @Test
    public void organisationPurposeWithAdditionalSOF() {
        when(accountSettingsForm.getSourceOfFunds()).thenReturn("Additional Sources");
        when(accountSettingsForm.getAdditionalSourceOfFunds()).thenReturn("additional sof text");
        when(organisationForm.hasSourceOfWealth()).thenReturn(true);
        when(organisationForm.getSourceOfWealth()).thenReturn("Compensation payment");

        final PurposeOfBusinessRelationshipOrgType purpose = purposeOfBusinessRelationshipTypeBuilder.purpose(organisationForm, accountSettingsForm);
        assertThat(purpose.getPurposeOfBusiness(), contains(WEALTH));

        assertThat(purpose.getSourceOfWealth(), contains(SourceOfWealthOrgType.COMPENSATION_PAYMENT));
        assertNull(purpose.getAdditionalSourceOfWealth());

        assertThat(purpose.getSourceOfFunds(), contains(SourceOfFundsOrgType.ADDITIONAL_SOURCES));
        assertThat(purpose.getAdditionalSourceOfFunds(), is("additional sof text"));
    }

    @Test
    public void organisationPurposeWithAdditionalSOFAndAdditionalSOW() {
        when(accountSettingsForm.getSourceOfFunds()).thenReturn("Additional Sources");
        when(accountSettingsForm.getAdditionalSourceOfFunds()).thenReturn("additional sof text");
        when(organisationForm.hasSourceOfWealth()).thenReturn(true);
        when(organisationForm.getSourceOfWealth()).thenReturn("Additional Sources");
        when(organisationForm.getAdditionalSourceOfWealth()).thenReturn("additional sow");

        final PurposeOfBusinessRelationshipOrgType purpose = purposeOfBusinessRelationshipTypeBuilder.purpose(organisationForm, accountSettingsForm);
        assertThat(purpose.getPurposeOfBusiness(), contains(WEALTH));

        assertThat(purpose.getSourceOfWealth(), contains(SourceOfWealthOrgType.ADDITIONAL_SOURCES));
        assertThat(purpose.getAdditionalSourceOfWealth(), is("additional sow"));

        assertThat(purpose.getSourceOfFunds(), contains(SourceOfFundsOrgType.ADDITIONAL_SOURCES));
        assertThat(purpose.getAdditionalSourceOfFunds(), is("additional sof text"));
    }

    @Test
    public void organisationPurposeWithNoAdditionalSources() {
        when(accountSettingsForm.getSourceOfFunds()).thenReturn("Corporate investments earnings");
        when(organisationForm.hasSourceOfWealth()).thenReturn(true);
        when(organisationForm.getSourceOfWealth()).thenReturn("Compensation payment");

        final PurposeOfBusinessRelationshipOrgType purpose = purposeOfBusinessRelationshipTypeBuilder.purpose(organisationForm, accountSettingsForm);
        assertThat(purpose.getPurposeOfBusiness(), contains(WEALTH));

        assertThat(purpose.getSourceOfWealth(), contains(SourceOfWealthOrgType.COMPENSATION_PAYMENT));
        assertNull(purpose.getAdditionalSourceOfWealth());

        assertThat(purpose.getSourceOfFunds(), contains(SourceOfFundsOrgType.CORPORATE_INVESTMENTS_EARNINGS));
        assertNull(purpose.getAdditionalSourceOfFunds());
    }
}
