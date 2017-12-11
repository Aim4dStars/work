package com.bt.nextgen.api.beneficiary.controller;

import com.bt.nextgen.api.beneficiary.model.Beneficiary;
import com.bt.nextgen.api.beneficiary.model.BeneficiaryDto;
import com.bt.nextgen.api.beneficiary.service.BeneficiaryDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.beneficiary.RelationshipType;
import com.bt.nextgen.service.integration.domain.Gender;
import org.hamcrest.Matchers;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit Test Case for BeneficiaryApiController
 * Created by M035995 on 12/07/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class BeneficiaryApiControllerTest {

    @InjectMocks
    BeneficiaryApiController beneficiaryApiController;

    @Mock
    BeneficiaryDtoService beneficiaryDtoService;

    private static List<BeneficiaryDto> beneficiaryDtos;

    private final String ACCOUNT_ID = "676AA77A418C5BC1AB5E2DEBC7E023DA15A6C416331D7421";

    @BeforeClass
    public static void init() {

        final List<Beneficiary> beneficiaryList = new ArrayList<>();
        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setAllocationPercent("50.00");
        beneficiary.setDateOfBirth("05 May 1980");
        beneficiary.setEmail("unittest@beneficiary.com");
        beneficiary.setFirstName("Rahul");
        beneficiary.setLastName("Grover");
        beneficiary.setNominationType("nomn_bind_nlaps_trustd");
        beneficiary.setPhoneNumber("0436526889");
        beneficiary.setRelationshipType(RelationshipType.INTERDEPENDENT.getAvaloqInternalId());
        beneficiary.setGender(Gender.MALE.toString());

        beneficiaryList.add(beneficiary);

        beneficiary = new Beneficiary();
        beneficiary.setAllocationPercent("50.00");
        beneficiary.setDateOfBirth("20 May 1950");
        beneficiary.setEmail("unittest2@beneficiary.com");
        beneficiary.setFirstName("Georgia");
        beneficiary.setLastName("Damon");
        beneficiary.setNominationType("nomn_nbind_sis");
        beneficiary.setPhoneNumber("0436526898");
        beneficiary.setRelationshipType(RelationshipType.CHILD.getAvaloqInternalId());
        beneficiary.setGender(Gender.FEMALE.toString());

        beneficiaryList.add(beneficiary);

        beneficiaryDtos = getBeneficiaryDto(beneficiaryList);
    }

    private static List<BeneficiaryDto> getBeneficiaryDto(List<Beneficiary> beneficiaryList) {
        List<BeneficiaryDto> beneficiaryDtos = new ArrayList<>();
        BeneficiaryDto beneficiaryDto = new BeneficiaryDto();
        beneficiaryDto.setBeneficiariesLastUpdatedTime(new DateTime("2016-11-05"));
        beneficiaryDto.setTotalAllocationPercent("100.00");
        beneficiaryDto.setBeneficiaries(beneficiaryList);
        beneficiaryDtos.add(beneficiaryDto);
        return beneficiaryDtos;
    }

    @Test(expected = IllegalArgumentException.class)
    public void getBeneficiaryDetailsWithEmptyAccountId() {
        beneficiaryApiController.getBeneficiaryDetails(null, null);
    }


    @Test(expected = EncryptionOperationNotPossibleException.class)
    public void testForInvalidAccount() {
        ApiResponse apiResponse = beneficiaryApiController.getBeneficiaryDetails("123", "false");
    }

    @Test
    public void testBeneficiaryDetails() {
        when(beneficiaryDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), any(ServiceErrorsImpl.class))).thenReturn(beneficiaryDtos);

        ApiResponse apiResponse = beneficiaryApiController.getBeneficiaryDetailsForAccountList("false", "");
        verify(beneficiaryDtoService, times(1)).search(Mockito.anyListOf(ApiSearchCriteria.class), any(ServiceErrors.class));
        assertThat(apiResponse, is(notNullValue()));

        ResultListDto<BeneficiaryDto> resultList = (ResultListDto<BeneficiaryDto>) apiResponse.getData();

        BeneficiaryDto resultDtoObject = resultList.getResultList().get(0);

        assertThat("List size", resultDtoObject.getBeneficiaries().size(), is(2));
        assertThat("Last updated date - Beneficiary", resultDtoObject.getBeneficiariesLastUpdatedTime(), is(equalTo(new DateTime("2016-11-05"))));

        assertThat("Total Allocation Percentage", resultDtoObject.getTotalAllocationPercent(), is(equalTo("100.00")));

        Beneficiary beneficiary = resultDtoObject.getBeneficiaries().get(0);
        assertThat("Beneficiary Details 0 - NominationType", beneficiary.getNominationType(), is(equalTo("nomn_bind_nlaps_trustd")));
        assertThat("Beneficiary Details 0 - AllocationPercent", beneficiary.getAllocationPercent(), Matchers.is(equalTo("50.00")));
        assertThat("Beneficiary Details 0 - RelationshipType", beneficiary.getRelationshipType(), Matchers.is(equalTo(RelationshipType.INTERDEPENDENT.toString())));
        assertThat("Beneficiary Details 0 - FirstName", beneficiary.getFirstName(), Matchers.is(equalTo("Rahul")));
        assertThat("Beneficiary Details 0 - LastName", beneficiary.getLastName(), Matchers.is(equalTo("Grover")));
        assertThat("Beneficiary Details 0 - DOB", beneficiary.getDateOfBirth(), Matchers.is(equalTo("05 May 1980")));
        assertThat("Beneficiary Details 0 - gender", beneficiary.getGender(), equalTo(Gender.MALE.toString()));
        assertThat("Beneficiary Details 0 - phone number", beneficiary.getPhoneNumber(), Matchers.is(equalTo("0436526889")));
        assertThat("Beneficiary Details 0 - email", beneficiary.getEmail(), Matchers.is(equalTo("unittest@beneficiary.com")));

        beneficiary = resultDtoObject.getBeneficiaries().get(1);
        assertThat("Beneficiary Details 0 - NominationType", beneficiary.getNominationType(), is(equalTo("nomn_nbind_sis")));
        assertThat("Beneficiary Details 0 - AllocationPercent", beneficiary.getAllocationPercent(), Matchers.is(equalTo("50.00")));
        assertThat("Beneficiary Details 0 - RelationshipType", beneficiary.getRelationshipType(), Matchers.is(equalTo(RelationshipType.CHILD.toString())));
        assertThat("Beneficiary Details 0 - FirstName", beneficiary.getFirstName(), Matchers.is(equalTo("Georgia")));
        assertThat("Beneficiary Details 0 - LastName", beneficiary.getLastName(), Matchers.is(equalTo("Damon")));
        assertThat("Beneficiary Details 0 - DOB", beneficiary.getDateOfBirth(), Matchers.is(equalTo("20 May 1950")));
        assertThat("Beneficiary Details 0 - gender", beneficiary.getGender(), equalTo(Gender.FEMALE.toString()));
        assertThat("Beneficiary Details 0 - phone number", beneficiary.getPhoneNumber(), Matchers.is(equalTo("0436526898")));
        assertThat("Beneficiary Details 0 - email", beneficiary.getEmail(), Matchers.is(equalTo("unittest2@beneficiary.com")));
    }



    @Test
    public void testBeneficiaryDetailsForAccountList() {
        when(beneficiaryDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), any(ServiceErrorsImpl.class))).thenReturn(beneficiaryDtos);

        ApiResponse apiResponse = beneficiaryApiController.getBeneficiaryDetails(ACCOUNT_ID, "false");
        verify(beneficiaryDtoService, times(1)).search(Mockito.anyListOf(ApiSearchCriteria.class), any(ServiceErrors.class));
        assertThat(apiResponse, is(notNullValue()));

        BeneficiaryDto resultDtoObject = (BeneficiaryDto) apiResponse.getData();
        assertThat("List size", resultDtoObject.getBeneficiaries().size(), is(2));
        assertThat("Last updated date - Beneficiary", resultDtoObject.getBeneficiariesLastUpdatedTime(), is(equalTo(new DateTime("2016-11-05"))));

        assertThat("Total Allocation Percentage", resultDtoObject.getTotalAllocationPercent(), is(equalTo("100.00")));

        Beneficiary beneficiary = resultDtoObject.getBeneficiaries().get(0);
        assertThat("Beneficiary Details 0 - NominationType", beneficiary.getNominationType(), is(equalTo("nomn_bind_nlaps_trustd")));
        assertThat("Beneficiary Details 0 - AllocationPercent", beneficiary.getAllocationPercent(), Matchers.is(equalTo("50.00")));
        assertThat("Beneficiary Details 0 - RelationshipType", beneficiary.getRelationshipType(), Matchers.is(equalTo(RelationshipType.INTERDEPENDENT.toString())));
        assertThat("Beneficiary Details 0 - FirstName", beneficiary.getFirstName(), Matchers.is(equalTo("Rahul")));
        assertThat("Beneficiary Details 0 - LastName", beneficiary.getLastName(), Matchers.is(equalTo("Grover")));
        assertThat("Beneficiary Details 0 - DOB", beneficiary.getDateOfBirth(), Matchers.is(equalTo("05 May 1980")));
        assertThat("Beneficiary Details 0 - gender", beneficiary.getGender(), equalTo(Gender.MALE.toString()));
        assertThat("Beneficiary Details 0 - phone number", beneficiary.getPhoneNumber(), Matchers.is(equalTo("0436526889")));
        assertThat("Beneficiary Details 0 - email", beneficiary.getEmail(), Matchers.is(equalTo("unittest@beneficiary.com")));

        beneficiary = resultDtoObject.getBeneficiaries().get(1);
        assertThat("Beneficiary Details 0 - NominationType", beneficiary.getNominationType(), is(equalTo("nomn_nbind_sis")));
        assertThat("Beneficiary Details 0 - AllocationPercent", beneficiary.getAllocationPercent(), Matchers.is(equalTo("50.00")));
        assertThat("Beneficiary Details 0 - RelationshipType", beneficiary.getRelationshipType(), Matchers.is(equalTo(RelationshipType.CHILD.toString())));
        assertThat("Beneficiary Details 0 - FirstName", beneficiary.getFirstName(), Matchers.is(equalTo("Georgia")));
        assertThat("Beneficiary Details 0 - LastName", beneficiary.getLastName(), Matchers.is(equalTo("Damon")));
        assertThat("Beneficiary Details 0 - DOB", beneficiary.getDateOfBirth(), Matchers.is(equalTo("20 May 1950")));
        assertThat("Beneficiary Details 0 - gender", beneficiary.getGender(), equalTo(Gender.FEMALE.toString()));
        assertThat("Beneficiary Details 0 - phone number", beneficiary.getPhoneNumber(), Matchers.is(equalTo("0436526898")));
        assertThat("Beneficiary Details 0 - email", beneficiary.getEmail(), Matchers.is(equalTo("unittest2@beneficiary.com")));
    }

}
