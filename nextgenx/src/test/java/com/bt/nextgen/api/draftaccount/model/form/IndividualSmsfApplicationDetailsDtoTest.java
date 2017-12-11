package com.bt.nextgen.api.draftaccount.model.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.bt.nextgen.api.draftaccount.model.AccountSettingsDto;
import org.junit.Test;

import com.bt.nextgen.api.account.v2.model.LinkedAccountDto;
import com.bt.nextgen.api.account.v2.model.PersonRelationDto;
import com.bt.nextgen.api.broker.model.BrokerDto;
import com.bt.nextgen.api.client.model.InvestorDto;
import com.bt.nextgen.api.client.model.SmsfDto;
import com.bt.nextgen.api.draftaccount.model.IndividualSmsfApplicationDetailsDto;
import com.bt.nextgen.service.integration.domain.InvestorRole;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class IndividualSmsfApplicationDetailsDtoTest {
    @Test
    public void directors_and_shareholdersAndMembers_should_be_returned_in_their_respective_getters() {
        IndividualSmsfApplicationDetailsDto dto = (IndividualSmsfApplicationDetailsDto) new IndividualSmsfApplicationDetailsDto()
                .withSmsf(createSMSF())
                .withTrustees(createTrustees())
                .withMembers(createMembers())
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
                .withAccountType(ClientApplicationForm.AccountType.INDIVIDUAL_SMSF.value())
                .withAccountName("DUMMY_ACCOUNT_NAME");

        assertNotNull(dto.getSmsf());
        assertEquals("Expected 3 trustees. Returned " + dto.getTrustees().size(), 3, dto.getTrustees().size());
        assertEquals("Expected 6 members. Returned " + dto.getMembers().size(), 6, dto.getMembers().size());
    }

    private List<InvestorDto> createMembers() {
        InvestorDto memberA1 = new InvestorDto();
        InvestorDto memberA2 = new InvestorDto();
        InvestorDto memberA3 = new InvestorDto();
        InvestorDto memberA4 = new InvestorDto();
        InvestorDto member1 = new InvestorDto();
        InvestorDto member2 = new InvestorDto();


        memberA1.setPersonRoles(Arrays.asList(InvestorRole.Member));
        memberA2.setPersonRoles(Arrays.asList(InvestorRole.Member, InvestorRole.Beneficiary));
        memberA3.setPersonRoles(Arrays.asList(InvestorRole.Member, InvestorRole.Other_Contact));
        memberA4.setPersonRoles(Arrays.asList(InvestorRole.Member, InvestorRole.Member));
        member1.setPersonRoles(Arrays.asList(InvestorRole.Member));
        member2.setPersonRoles(Arrays.asList(InvestorRole.Member));

        return Arrays.asList(memberA1, memberA2, memberA3, memberA4, member1, member2);
    }

    private SmsfDto createSMSF() {
        SmsfDto smsf = new SmsfDto();
        smsf.setAbn("MY_ABN");
        return smsf;
    }


    public List<InvestorDto> createTrustees() {
        InvestorDto trustee1 = new InvestorDto();
        InvestorDto trustee2 = new InvestorDto();
        InvestorDto trustee3 = new InvestorDto();
        
        trustee1.setPersonRoles(Arrays.asList(InvestorRole.Trustee));
        trustee2.setPersonRoles(Arrays.asList(InvestorRole.Trustee, InvestorRole.Member));
        trustee3.setPersonRoles(Arrays.asList(InvestorRole.Trustee, InvestorRole.Member));

        
        return Arrays.asList(trustee1, trustee2, trustee3);
    }
}
