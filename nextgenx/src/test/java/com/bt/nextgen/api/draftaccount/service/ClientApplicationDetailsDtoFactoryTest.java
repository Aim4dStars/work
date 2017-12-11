package com.bt.nextgen.api.draftaccount.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.bt.nextgen.api.draftaccount.model.AccountSettingsDto;
import org.junit.Assert;
import org.junit.Test;

import com.bt.nextgen.api.account.v2.model.LinkedAccountDto;
import com.bt.nextgen.api.account.v2.model.PersonRelationDto;
import com.bt.nextgen.api.broker.model.BrokerDto;
import com.bt.nextgen.api.client.model.CompanyDto;
import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.api.client.model.InvestorDto;
import com.bt.nextgen.api.client.model.SmsfDto;
import com.bt.nextgen.api.client.model.TrustDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.CompanyDetailsDto;
import com.bt.nextgen.api.draftaccount.model.CorporateSmsfApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.IndividualDirectApplicationsDetailsDto;
import com.bt.nextgen.api.draftaccount.model.IndividualOrJointApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.IndividualSmsfApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.SuperPensionApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.TrustApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;

public class ClientApplicationDetailsDtoFactoryTest {
    @Test
    public void constructClientApplicationDTO_should_return_IndividualOrJointApplicationDetailsDto_for_Individual_Application() {
        ClientApplicationDetailsDto clientApplicationDetailsDto = constructClientApplicationDtoCallHelper(IClientApplicationForm.AccountType.INDIVIDUAL.value());
        Assert.assertTrue(clientApplicationDetailsDto instanceof IndividualOrJointApplicationDetailsDto);
    }

    @Test
    public void constructClientApplicationDTO_should_return_IndividualOrJointApplicationDetailsDto_for_Joint_Application() {
        ClientApplicationDetailsDto clientApplicationDetailsDto = constructClientApplicationDtoCallHelper(IClientApplicationForm.AccountType.JOINT.value());
        Assert.assertTrue(clientApplicationDetailsDto instanceof IndividualOrJointApplicationDetailsDto);
    }

    @Test
    public void constructClientApplicationDTO_should_return_CorporateSmsfApplicationDetailsDto_for_Corporate_SMSF_Application() {
        ClientApplicationDetailsDto clientApplicationDetailsDto = constructClientApplicationDtoCallHelper(IClientApplicationForm.AccountType.CORPORATE_SMSF.value());
        Assert.assertTrue(clientApplicationDetailsDto instanceof CorporateSmsfApplicationDetailsDto);
    }

    @Test
    public void constructClientApplicationDTO_should_return_TrustApplicationDetailsDto_for_Corporate_Trust_Application() {
        ClientApplicationDetailsDto clientApplicationDetailsDto = constructClientApplicationDtoCallHelper(IClientApplicationForm.AccountType.CORPORATE_TRUST.value());
        Assert.assertTrue(clientApplicationDetailsDto instanceof TrustApplicationDetailsDto);
    }

    @Test
    public void constructClientApplicationDTO_should_return_TrustApplicationDetailsDto_for_Individual_Trust_Application() {
        TrustApplicationDetailsDto clientApplicationDetailsDto = (TrustApplicationDetailsDto)constructClientApplicationDtoCallHelper(IClientApplicationForm.AccountType.INDIVIDUAL_TRUST.value());
        Assert.assertThat(clientApplicationDetailsDto.getShareholdersAndMembers().size(), is(1));
    }

    @Test
    public void constructClientApplicationDTOFromFormData_should_return_CorporateSMSFApplicationDetailsDto() {
        ClientApplicationDetailsDto clientApplicationDetailsDto = constructClientApplicationDtoCallHelperFromForm(IClientApplicationForm.AccountType.CORPORATE_SMSF.value());
        Assert.assertTrue(clientApplicationDetailsDto instanceof CorporateSmsfApplicationDetailsDto);
    }

    @Test
    public void constructClientApplicationDTOFromFormDataFor_NewSMSF_should_return_CorporateSMSFApplicationDetailsDto() {
        ClientApplicationDetailsDto clientApplicationDetailsDto = constructClientApplicationDtoCallHelperFromForm(IClientApplicationForm.AccountType.NEW_CORPORATE_SMSF.value());
        Assert.assertTrue(clientApplicationDetailsDto instanceof CorporateSmsfApplicationDetailsDto);
    }

    @Test
    public void constructClientApplicationDTOFromFormData_should_return_Dto_withApprovalType() {
        ClientApplicationDetailsDto clientApplicationDetailsDto = constructClientApplicationDtoCallHelperFromForm(IClientApplicationForm.AccountType.INDIVIDUAL.value());
        assertEquals("Online",clientApplicationDetailsDto.getApprovalType());
        assertEquals("individual", clientApplicationDetailsDto.getInvestorAccountType());
    }

    @Test
    public void constructClientApplicationDTOFromFormData_should_return_IndividualApplicationDetailsDto() {
        ClientApplicationDetailsDto clientApplicationDetailsDto = constructClientApplicationDtoCallHelperFromForm(IClientApplicationForm.AccountType.INDIVIDUAL.value());
        Assert.assertTrue(clientApplicationDetailsDto instanceof IndividualOrJointApplicationDetailsDto);
    }

    @Test
    public void constructClientApplicationDTOFromFormData_should_return_JointApplicationDetailsDto() {
        ClientApplicationDetailsDto clientApplicationDetailsDto = constructClientApplicationDtoCallHelperFromForm(IClientApplicationForm
        .AccountType.JOINT.value());
        Assert.assertTrue(clientApplicationDetailsDto instanceof IndividualOrJointApplicationDetailsDto);
    }

    @Test
    public void constructClientApplicationDTO_should_return_CompanyDetailsDto() {
        ClientApplicationDetailsDto clientApplicationDetailsDto = constructClientApplicationDtoCallHelper(IClientApplicationForm
        .AccountType.COMPANY.value());
        Assert.assertTrue(clientApplicationDetailsDto instanceof CompanyDetailsDto);
    }

    @Test
    public void constructClientApplicationDTO_should_return_newIndividualSMSFDetails() {
        ClientApplicationDetailsDto clientApplicationDetailsDto = constructClientApplicationDtoCallHelper(IClientApplicationForm.AccountType.NEW_INDIVIDUAL_SMSF.value());
        Assert.assertTrue(clientApplicationDetailsDto instanceof IndividualSmsfApplicationDetailsDto);
    }

    @Test
    public void constructClientApplicationDTO_should_return_existingIndividualSMSFDetails() {
        ClientApplicationDetailsDto clientApplicationDetailsDto = constructClientApplicationDtoCallHelper(IClientApplicationForm.AccountType.INDIVIDUAL_SMSF.value());
        Assert.assertTrue(clientApplicationDetailsDto instanceof IndividualSmsfApplicationDetailsDto);
    }

    @Test
    public void constructClientApplicationDTO_should_return_individualDirectDto() {
        ClientApplicationDetailsDto clientApplicationDetailsDto = constructClientApplicationDetailsDtoForDirect();
        Assert.assertTrue(clientApplicationDetailsDto instanceof IndividualDirectApplicationsDetailsDto);
    }

    @Test
    public void constructClientApplicationDTO_should_return_superPension_withEligibilityDetails() {
        ClientApplicationDetailsDto clientApplicationDetailsDto = constructClientApplicationDtoCallHelper(IClientApplicationForm.AccountType.SUPER_PENSION.value());
        Assert.assertTrue(clientApplicationDetailsDto instanceof SuperPensionApplicationDetailsDto);
    }

    private ClientApplicationDetailsDto constructClientApplicationDtoCallHelper(String accountType) {
        List<InvestorDto> investors = new ArrayList<>();

        if (IClientApplicationForm.AccountType.CORPORATE_SMSF.value().equals(accountType) ||
        IClientApplicationForm.AccountType.INDIVIDUAL_SMSF.value().equals(accountType) ||
        IClientApplicationForm.AccountType.NEW_INDIVIDUAL_SMSF.value().equals(accountType) ||
        IClientApplicationForm.AccountType.SUPER_PENSION.equals(accountType)) {
            SmsfDto sampleSMSF = new SmsfDto();
            sampleSMSF.setLinkedClients(new ArrayList<InvestorDto>());
            investors.add(sampleSMSF);
        } else if (IClientApplicationForm.AccountType.CORPORATE_TRUST.value().equals(accountType) ||
                IClientApplicationForm.AccountType.INDIVIDUAL_TRUST.value().equals(accountType)) {
            TrustDto sampleTrust = new TrustDto();
            sampleTrust.setLinkedClients(new ArrayList<InvestorDto>());
            sampleTrust.setBeneficiaries(new ArrayList<IndividualDto>());
            investors.add(sampleTrust);
        } else if(IClientApplicationForm.AccountType.COMPANY.value().equals(accountType)) {
            CompanyDto companyDto = new CompanyDto();
            companyDto.setLinkedClients(new ArrayList<InvestorDto>());
            investors.add(companyDto);
        }

        return ClientApplicationDetailsDtoFactory.make()
                .withOnboardingApplicationKey("DUMMY_APPLICATION_KEY")
                .withAccountKey("DUMMY_ACCOUNT_KEY")
                .withInvestorsDirectorsTrustees(investors)
                .withAccountType(accountType)
                .withApprovalType("Online")
                .withApplicationOriginType(IClientApplicationForm.ApplicationOriginType.BT_PANORAMA.value())
                .withAccountName("DUMMY_ACCOUNT_NAME")
                .withAccountSettings(new AccountSettingsDto())
                .withAdditionalPersons(Arrays.asList(new InvestorDto()))
                .withLinkedAccounts(new ArrayList<LinkedAccountDto>())
                .withDraftFees(new HashMap<String, Object>())
                .withAdviser(new BrokerDto())
                .withAccountAvaloqStatus("ACCOUNT_AVALAQ_STATUS")
                .withReferenceNumber("REFERENCE_NUMBER")
                .withProductName("PRODUCT_NAME")
                .withPdsUrl("PDS_URL")
                .collect();
    }

    private ClientApplicationDetailsDto constructClientApplicationDtoCallHelperFromForm(String accountType) {
        List<InvestorDto> investors = new ArrayList<>();

        if (IClientApplicationForm.AccountType.CORPORATE_SMSF.value().equals(accountType) ||
                IClientApplicationForm.AccountType.NEW_CORPORATE_SMSF.value().equals(accountType)) {
            SmsfDto sampleSMSF = new SmsfDto();
            sampleSMSF.setLinkedClients(new ArrayList<InvestorDto>());
            investors.add(sampleSMSF);
        } else if (IClientApplicationForm.AccountType.CORPORATE_TRUST.value().equals(accountType) ||
                IClientApplicationForm.AccountType.INDIVIDUAL_TRUST.value().equals(accountType)) {
            TrustDto sampleTrust = new TrustDto();
            sampleTrust.setLinkedClients(new ArrayList<InvestorDto>());
            sampleTrust.setBeneficiaries(new ArrayList<IndividualDto>());
            investors.add(sampleTrust);
        }


        return ClientApplicationDetailsDtoFactory.make()
                .withOnboardingApplicationKey("DUMMY_APPLICATION_KEY")
                .withAccountKey("DUMMY_ACCOUNT_KEY")
                .withInvestorsDirectorsTrustees(investors)
                .withApprovalType("Online")
                .withAccountType(accountType)
                .withAccountName("DUMMY_ACCOUNT_NAME")
                .withAccountSettings(new AccountSettingsDto())
                .withLinkedAccounts(new ArrayList<LinkedAccountDto>())
                .withDraftFees(new HashMap<String, Object>())
                .withAdviser(new BrokerDto())
                .withAccountAvaloqStatus("ACCOUNT_AVALAQ_STATUS")
                .withReferenceNumber("REFERENCE_NUMBER")
                .withProductName("PRODUCT_NAME")
                .withPdsUrl("PDS_URL")
                .withApplicationOriginType(IClientApplicationForm.ApplicationOriginType.BT_PANORAMA.value())
                .collect();
    }

    private ClientApplicationDetailsDto constructClientApplicationDetailsDtoForDirect() {
        String accountType = IClientApplicationForm.AccountType.INDIVIDUAL.value();
        List<InvestorDto> investors = new ArrayList<>();

        return ClientApplicationDetailsDtoFactory.make()
                .withInvestorsDirectorsTrustees(investors)
                .withAccountType(accountType)
                .withApplicationOriginType(IClientApplicationForm.ApplicationOriginType.WESTPAC_LIVE.value())
                .collect();
    }

}
