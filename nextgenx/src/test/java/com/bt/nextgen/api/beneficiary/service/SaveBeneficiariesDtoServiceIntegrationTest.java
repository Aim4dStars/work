package com.bt.nextgen.api.beneficiary.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.beneficiary.model.Beneficiary;
import com.bt.nextgen.api.beneficiary.model.BeneficiaryTrxnDto;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Integration test class for {@link com.bt.nextgen.api.beneficiary.service.SaveBeneficiariesDtoService}
 * Created by L067218 on 16/09/2016.
 */
public class SaveBeneficiariesDtoServiceIntegrationTest extends BaseSecureIntegrationTest {

    @Autowired
    private SaveBeneficiariesDtoService saveBeneficiariesDtoService;

    private static BeneficiaryTrxnDto beneficiaryTrxnDto;

    @Before
    public void init() {
        beneficiaryTrxnDto = new BeneficiaryTrxnDto();
        AccountKey key = new AccountKey("15E67EC04FBD44111C4EC74731CF06E74D5C8FD5E765FAB3");
        beneficiaryTrxnDto.setKey(key);

        List<Beneficiary> benefList = new ArrayList<>();
        Beneficiary benef1 = new Beneficiary();
        benef1.setNominationType("nomn_bind_nlaps_trustd");
        benef1.setRelationshipType("fin_dep");
        benef1.setPhoneNumber("1221321");
        benef1.setLastName("Richard");
        benef1.setFirstName("Dennis");
        benef1.setAllocationPercent("60");
        benef1.setEmail("abcd@gmail.com");
        benef1.setGender("male");
        benef1.setDateOfBirth("05 May 1980");
        benefList.add(benef1);


        Beneficiary benef2 = new Beneficiary();
        benef2.setNominationType("nomn_nbind_sis");
        benef2.setRelationshipType("spouse");
        benef2.setPhoneNumber("159869");
        benef2.setLastName("Steele");
        benef2.setFirstName("Anastasia");
        benef2.setAllocationPercent("40");
        benef2.setEmail("ana@gmail.com");
        benef2.setGender("female");
        benef2.setDateOfBirth("04 Nov 1990");
        benefList.add(benef2);

        beneficiaryTrxnDto.setBeneficiaries(benefList);

    }

    @Test
    public void testSaveBeneficiaries() {
        BeneficiaryTrxnDto beneficiaryDto = saveBeneficiariesDtoService.submit(beneficiaryTrxnDto, new ServiceErrorsImpl());
        assertThat(beneficiaryDto, is(notNullValue()));

        assertThat("Beneficiary Dto Status", beneficiaryDto.getTransactionStatus(), is(equalTo("saved")));
        // just validate if the list size is 2
        assertThat("Beneficiary List size", beneficiaryDto.getBeneficiaries().size(), is(equalTo(2)));
        assertThat("Beneficiary 1 Nomination Type", beneficiaryDto.getBeneficiaries().get(0).getNominationType(), is(equalTo("nomn_bind_nlaps_trustd")));
        assertThat("Beneficiary 1 Relationship type", beneficiaryDto.getBeneficiaries().get(0).getRelationshipType(), is(equalTo("fin_dep")));
        assertThat("Beneficiary 1 Phone number", beneficiaryDto.getBeneficiaries().get(0).getPhoneNumber(), is(equalTo("1221321")));
        assertThat("Beneficiary 1 email id", beneficiaryDto.getBeneficiaries().get(0).getEmail(), is(equalTo("abcd@gmail.com")));
        assertThat("Beneficiary 1 first name", beneficiaryDto.getBeneficiaries().get(0).getFirstName(), is(equalTo("Dennis")));
        assertThat("Beneficiary 1 last name", beneficiaryDto.getBeneficiaries().get(0).getLastName(), is(equalTo("Richard")));
        assertThat("Beneficiary 1 allocation percent", beneficiaryDto.getBeneficiaries().get(0).getAllocationPercent(), is(equalTo("60")));
        assertThat("Beneficiary 1 DOB", beneficiaryDto.getBeneficiaries().get(0).getDateOfBirth(), is(equalTo("05 May 1980")));

        assertThat("Beneficiary 2 Nomination Type", beneficiaryDto.getBeneficiaries().get(1).getNominationType(), is(equalTo("nomn_nbind_sis")));
        assertThat("Beneficiary 2 Relationship type", beneficiaryDto.getBeneficiaries().get(1).getRelationshipType(), is(equalTo("spouse")));
        assertThat("Beneficiary 2 Phone number", beneficiaryDto.getBeneficiaries().get(1).getPhoneNumber(), is(equalTo("159869")));
        assertThat("Beneficiary 2 email id", beneficiaryDto.getBeneficiaries().get(1).getEmail(), is(equalTo("ana@gmail.com")));
        assertThat("Beneficiary 2 first name", beneficiaryDto.getBeneficiaries().get(1).getFirstName(), is(equalTo("Anastasia")));
        assertThat("Beneficiary 2 last name", beneficiaryDto.getBeneficiaries().get(1).getLastName(), is(equalTo("Steele")));
        assertThat("Beneficiary 2 allocation percent", beneficiaryDto.getBeneficiaries().get(1).getAllocationPercent(), is(equalTo("40")));
        assertThat("Beneficiary 2 DOB", beneficiaryDto.getBeneficiaries().get(1).getDateOfBirth(), is(equalTo("04 Nov 1990")));

    }
}
