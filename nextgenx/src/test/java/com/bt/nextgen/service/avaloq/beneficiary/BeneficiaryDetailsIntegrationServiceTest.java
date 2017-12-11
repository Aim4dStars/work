package com.bt.nextgen.service.avaloq.beneficiary;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.domain.Gender;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * This is the integration test class for Beneficiary Details functionality - view/edit/delete.
 * Created by M035995 on 9/07/2016.
 */
public class BeneficiaryDetailsIntegrationServiceTest extends BaseSecureIntegrationTest {

    @Autowired
    @Qualifier("BeneficiaryDetailsIntegrationServiceImpl")
    BeneficiaryDetailsIntegrationService beneficiaryDetailsIntegrationService;

    @Test
    public void testViewBeneficiaries() {
        BeneficiaryDetailsResponseHolderImpl beneficiaryDetailsResponseHolder = beneficiaryDetailsIntegrationService.
                getBeneficiaryDetails(new ArrayList<>(Arrays.asList("400000014")), new ServiceErrorsImpl());

        assertNotNull(beneficiaryDetailsResponseHolder);
        assertNotNull(beneficiaryDetailsResponseHolder.getBeneficiaryDetailsList());
        AccountBeneficiaryDetailsResponseImpl beneficiaryDetailsResponse = beneficiaryDetailsResponseHolder.getBeneficiaryDetailsList().get(0);
        List<BeneficiaryDetails> beneficiaryDetailsList = beneficiaryDetailsResponse.getBeneficiaryDetails();

        assertThat("Beneficiary Details - LastUpdatedDate", beneficiaryDetailsResponse.getLastUpdatedDate(),
                is(equalTo(new DateTime("2016-05-11"))));

        BeneficiaryDetails beneficiaryDetails = beneficiaryDetailsList.get(0);
        assertThat("Beneficiary Details 0 - NominationType", beneficiaryDetails.getNominationType().getIntlId(), is(equalTo("nomn_bind_nlaps_trustd")));
        assertThat("Beneficiary Details 0 - AllocationPercent", beneficiaryDetails.getAllocationPercent(), is(equalTo(new BigDecimal("50"))));
        assertThat("Beneficiary Details 0 - RelationshipType", beneficiaryDetails.getRelationshipType(), is(equalTo(RelationshipType.SPOUSE)));
        assertThat("Beneficiary Details 0 - FirstName", beneficiaryDetails.getFirstName(), is(equalTo("test")));
        assertThat("Beneficiary Details 0 - LastName", beneficiaryDetails.getLastName(), is(equalTo("lastname")));
        assertThat("Beneficiary Details 0 - DOB", beneficiaryDetails.getDateOfBirth(), is(equalTo(new DateTime("1980-05-01"))));
        assertThat("Beneficiary Details 0 - gender", beneficiaryDetails.getGender(), equalTo(Gender.MALE));
        assertThat("Beneficiary Details 0 - phone number", beneficiaryDetails.getPhoneNumber(), is(equalTo("0410274222")));
        assertThat("Beneficiary Details 0 - email", beneficiaryDetails.getEmail(), is(equalTo("abcd@gmail.com")));


        beneficiaryDetails = beneficiaryDetailsList.get(1);
        assertThat("Beneficiary Details 1 - NominationType", beneficiaryDetails.getNominationType().getIntlId(), is(equalTo("nomn_nbind_sis")));
        assertThat("Beneficiary Details 1 - AllocationPercent", beneficiaryDetails.getAllocationPercent(), is(equalTo(new BigDecimal("50"))));
        assertThat("Beneficiary Details 1 - RelationshipType", beneficiaryDetails.getRelationshipType(), is(equalTo(RelationshipType.INTERDEPENDENT)));
        assertThat("Beneficiary Details 1 - FirstName", beneficiaryDetails.getFirstName(), is(equalTo("test2")));
        assertThat("Beneficiary Details 1 - LastName", beneficiaryDetails.getLastName(), is(equalTo("lastname2")));
        assertThat("Beneficiary Details 1 - DOB", beneficiaryDetails.getDateOfBirth(), is(equalTo(new DateTime("2006-05-11"))));
        assertThat("Beneficiary Details 1 - gender", beneficiaryDetails.getGender(), equalTo(Gender.FEMALE));
        assertThat("Beneficiary Details 1 - phone number", beneficiaryDetails.getPhoneNumber(), is(equalTo("0410274222")));
        assertThat("Beneficiary Details 1 - email", beneficiaryDetails.getEmail(), is(equalTo("xyz@gmail.com")));

    }

    @SecureTestContext
    @Test
    public void saveNewBeneficiary() throws Exception {
        SaveBeneficiariesDetails benefDetails = new SaveBeneficiariesDetailsImpl();
        List<BeneficiaryDetails> beneficiaryDetails = new ArrayList<>();
        BeneficiaryDetails detail = new BeneficiaryDetailsImpl();
        detail.setDateOfBirth(new DateTime("2001-04-30"));
        detail.setRelationshipType(RelationshipType.CHILD);
        detail.setPhoneNumber("0410799123");
        detail.setAllocationPercent(new BigDecimal(100));
        detail.setFirstName("Chloe Moretz");
        detail.setNominationTypeinAvaloqFormat("nomn_nbind_sis");
        detail.setEmail("dafs@yahoo.com");
        detail.setGender(Gender.FEMALE);
        beneficiaryDetails.add(detail);
        benefDetails.setAccountKey(new com.bt.nextgen.api.account.v2.model.AccountKey("15E67EC04FBD44111C4EC74731CF06E74D5C8FD5E765FAB3"));

        benefDetails.setBeneficiaries(beneficiaryDetails);
        benefDetails.setModificationSeq("1");

        TransactionStatus transactionStatus = beneficiaryDetailsIntegrationService.saveOrUpdate(benefDetails);

        assertEquals(true, transactionStatus.isSuccessful());
    }


}
