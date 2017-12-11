package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.account.v1.model.AccountDto;
import com.bt.nextgen.api.account.v1.model.AccountKey;

import com.bt.nextgen.api.client.model.ClientDto;
import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.api.util.ApiConstants;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation.CONTAINS;
import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation.EQUALS;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ClientSearchFilterTest {

    private static final String ADVISER_ID = "893EDC36B73C7067EBF7751E2C916E417456ACF9603CE305";

    private ClientSearchFilter clientSearchFilter;

    private ClientDto client;

    @Before
    public void initDefaultFilter() throws Exception {
        clientSearchFilter = initClientSearchFilter(
                new ApiSearchCriteria(ApiConstants.DISPLAY_NAME, CONTAINS, "Any"),
                new ApiSearchCriteria(ApiConstants.ADVISERID, EQUALS, ADVISER_ID),
                new ApiSearchCriteria(Attribute.INVESTOR_TYPE, EQUALS, "Individual"));
        client = new ClientDto();
    }

    @Test
    public void matchesReturnsFalseIfIdIsNotVerified(){
        clientSearchFilter = initClientSearchFilter();
        assertFalse(clientSearchFilter.matches(client));
    }

    @Test
    public void matchesReturnsTrueIfIdIsVerified(){
        clientSearchFilter = initClientSearchFilter();
        client.setIdVerified(true);
        assertTrue(clientSearchFilter.matches(client));
    }

    @Test
    public void matchesReturnsFalseIfNoRelevantSearchCriteriaIsPresent(){
        // Relevant search criteria is DisplayName, AdviserId, InvestorType
        clientSearchFilter = initClientSearchFilter(new ApiSearchCriteria(ApiConstants.ACCOUNT, CONTAINS, "Any"));
        assertFalse(clientSearchFilter.matches(client));
    }

    @Test
    public void matchesReturnsFalseIfDisplayDoesNotMatch() throws Exception {
        client = createClientDto("NotMatchingFN", "NotMatchingLN", ADVISER_ID);
        assertFalse(clientSearchFilter.matches(client));
    }

    @Test
    public void matchesReturnsFalseIfAdviserIdDoesNotMatch() throws Exception {
        client = createClientDto("AnyClientFN", "AnyClientLN", "56715A6079FD10C7557182F2AEA9CEEA4EB785641CFA68BF");
        assertFalse(clientSearchFilter.matches(client));
    }

    @Test
    public void matchesReturnsFalseIfInvestorTypeDoestNotMatch() throws Exception {
        client = createClientDto("AnyClientFN", "AnyClientLN", ADVISER_ID);
        assertFalse(clientSearchFilter.matches(client));
    }

    @Test
    public void matchesReturnsTrueIfAllCriteriaMatches() throws Exception {
        IndividualDto individualDto = createIndividualDto("AnyClientFN", "AnyClientLN", ADVISER_ID);
        assertTrue(clientSearchFilter.matches(individualDto));
    }

    private static ClientSearchFilter initClientSearchFilter(ApiSearchCriteria... criteria) {
        return new ClientSearchFilter(asList(criteria));
    }

    private static IndividualDto createIndividualDto(String firstName, String lastName, String adviserId) {
        IndividualDto individualDto = new IndividualDto();
        individualDto.setFirstName(firstName);
        individualDto.setLastName(lastName);
        individualDto.setDisplayName(firstName +" "+lastName);
        individualDto.setAccounts(createAccountsWithAdviser(adviserId));
        individualDto.setInvestorType("Individual");
        individualDto.setIdVerified(true);
        return individualDto;
    }

    private static ClientDto createClientDto(String firstName, String lastName, String adviserId){
        ClientDto clientDto= new ClientDto();
        clientDto.setFirstName(firstName);
        clientDto.setLastName(lastName);
        clientDto.setDisplayName(firstName + " " + lastName);
        clientDto.setAccounts(createAccountsWithAdviser(adviserId));
        return clientDto;
    }

    private static List<AccountDto> createAccountsWithAdviser(String adviserId) {
        List<AccountDto> accountDtos = new ArrayList<>();
        AccountDto accountDto = new AccountDto(new AccountKey("AnyAccount"));
        accountDto.setAdviserId(adviserId);
        accountDtos.add(accountDto);
        return accountDtos;
    }
}