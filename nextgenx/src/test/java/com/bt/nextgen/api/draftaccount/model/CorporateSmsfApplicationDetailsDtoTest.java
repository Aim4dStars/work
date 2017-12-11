package com.bt.nextgen.api.draftaccount.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import com.bt.nextgen.api.account.v2.model.LinkedAccountDto;
import com.bt.nextgen.api.account.v2.model.PersonRelationDto;
import com.bt.nextgen.api.broker.model.BrokerDto;
import com.bt.nextgen.api.client.model.InvestorDto;
import com.bt.nextgen.api.client.model.SmsfDto;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.service.integration.domain.InvestorRole;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CorporateSmsfApplicationDetailsDtoTest {
    @Test
    public void directors_and_shareholdersAndMembers_should_be_returned_in_their_respective_getters() {
        CorporateSmsfApplicationDetailsDto dto = (CorporateSmsfApplicationDetailsDto) new CorporateSmsfApplicationDetailsDto()
                .withSmsf(createSmsf())
                .withDirectors(createDirectors())
                .withShareholdersAndMembers(createAdditionalMembers())
                .withAccountSettings(new AccountSettingsDto())
                .withLinkedAccounts(new ArrayList<LinkedAccountDto>())
                .withFees(new HashMap<String, Object>())
                .withAdviser(new BrokerDto())
                .withAccountAvaloqStatus("ACCOUNT_AVALAQ_STATUS")
                .withReferenceNumber("REFERENCE_NUMBER")
                .withProductName("PRODUCT_NAME")
                .withPdsUrl("PDS_URL")
                .withOnboardingApplicationKey("DUMMY_APPLICATION_KEY")
                .withAccountKey("DUMMY_ACCOUNT_KEY")
                .withAccountType(IClientApplicationForm.AccountType.CORPORATE_SMSF.value())
                .withAccountName("DUMMY_ACCOUNT_NAME");
        
        assertNotNull(dto.getSmsf());
        assertEquals("Expected 3 directors. Returned " + dto.getDirectors().size(), 3, dto.getDirectors().size());
        assertEquals("Expected 6 share holders and members. Returned " + dto.getShareholdersAndMembers().size(), 6, dto.getShareholdersAndMembers().size());
    }
    
    

    private List<InvestorDto> createDirectors() {
        InvestorDto director1 = new InvestorDto();
        InvestorDto director2 = new InvestorDto();
        InvestorDto director3 = new InvestorDto();
      

        director1.setPersonRoles(Arrays.asList(InvestorRole.Director));
        director2.setPersonRoles(Arrays.asList(InvestorRole.Director, InvestorRole.Shareholder));
        director3.setPersonRoles(Arrays.asList(InvestorRole.Director, InvestorRole.Shareholder));
        

        return Arrays.asList(director1, director2, director3);
    }

    public List<InvestorDto> createAdditionalMembers() {  InvestorDto shareHolder1 = new InvestorDto();
        InvestorDto shareHolder2 = new InvestorDto();
        InvestorDto shareHolder3 = new InvestorDto();
        InvestorDto shareHolder4 = new InvestorDto();
        InvestorDto member1 = new InvestorDto();
        InvestorDto member2 = new InvestorDto();

        shareHolder1.setPersonRoles(Arrays.asList(InvestorRole.Shareholder));
        shareHolder2.setPersonRoles(Arrays.asList(InvestorRole.Shareholder, InvestorRole.Beneficiary));
        shareHolder3.setPersonRoles(Arrays.asList(InvestorRole.Shareholder, InvestorRole.Other_Contact));
        shareHolder4.setPersonRoles(Arrays.asList(InvestorRole.Shareholder, InvestorRole.Member));
        member1.setPersonRoles(Arrays.asList(InvestorRole.Member));
        member2.setPersonRoles(Arrays.asList(InvestorRole.Member, InvestorRole.Shareholder));
        
        return Arrays.asList(shareHolder1, shareHolder2, shareHolder3, shareHolder4, member1, member2);
    }

    public SmsfDto createSmsf() {
        SmsfDto smsfDTO = new SmsfDto();
        smsfDTO.setAbn("MY_ABN");
        
        return smsfDTO;
    }
}
