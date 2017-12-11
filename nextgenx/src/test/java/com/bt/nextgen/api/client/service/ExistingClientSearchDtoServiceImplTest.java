package com.bt.nextgen.api.client.service;

import com.bt.nextgen.api.client.model.ClientIdentificationDto;
import com.bt.nextgen.api.client.model.ExistingClientSearchDto;
import com.bt.nextgen.api.client.model.IndividualWithAdvisersDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.domain.AddressImpl;
import com.bt.nextgen.service.avaloq.domain.existingclient.IndividualWithAccountDataImpl;
import com.bt.nextgen.service.avaloq.domain.existingclient.LegalClient;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.IdentityVerificationStatus;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExistingClientSearchDtoServiceImplTest
{
	@Mock
	private ClientListDtoServiceImpl clientListDtoService;

    @Mock
    private ClientIntegrationService clientIntegrationService;

	@InjectMocks
	private ExistingClientSearchDtoServiceImpl existingClientSearchDtoService;

	private ServiceErrors serviceErrors = new FailFastErrorsImpl();
	private IndividualWithAccountDataImpl individual;

	@Before
	public void setup()
	{
	    individual = new IndividualWithAccountDataImpl();
        individual.setClientKey(ClientKey.valueOf("1234"));
        individual.setFullName("D'ennis Beecham");
        AddressImpl address = new AddressImpl();
        individual.setAddresses(Arrays.<Address>asList(address));
        individual.setFirstName("D'ennis");
        individual.setLastName("Beecham");

        LegalClient company = new LegalClient();
        company.setClientKey(ClientKey.valueOf("COMPANY_CLIENT_KEY"));
        company.setIdentityVerificationStatus(IdentityVerificationStatus.Completed);
        company.setLegalForm(InvestorType.COMPANY);
        company.setFullName("A VERY BIG COMPANY");
        company.setAddresses(Arrays.<Address>asList(address));

        when(clientIntegrationService.loadClientsForExistingClientSearch(serviceErrors, "AnyName")).thenReturn(Arrays.<com.btfin.panorama.service.avaloq.domain.existingclient.Client>asList(individual, company));
	}

    @Ignore
    @Test
    public void search_shouldFilterClientsBasedOnTheQueryCriteria() {
        ApiSearchCriteria queryMatchCriteria = new ApiSearchCriteria(Attribute.CLIENTS, ApiSearchCriteria.SearchOperation.CONTAINS, "comp", ApiSearchCriteria.OperationType.STRING);
        List<ClientIdentificationDto> result = existingClientSearchDtoService.search(Arrays.asList(queryMatchCriteria), serviceErrors);
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getClass().getCanonicalName(), is(ExistingClientSearchDto.class.getCanonicalName()));
        assertThat(((ExistingClientSearchDto) result.get(0)).getDisplayName(), is("A VERY BIG COMPANY"));
    }

    @Ignore
    @Test
    public void search_shouldReturnUnfilteredList_WhenCriteriaIsNotDefined() {
        List<ClientIdentificationDto> result = existingClientSearchDtoService.search(null, serviceErrors);
        assertThat(result.size(), is(2));
        assertThat(result.get(0).getClass().getCanonicalName(), is(IndividualWithAdvisersDto.class.getCanonicalName()));
        assertThat(result.get(1).getClass().getCanonicalName(), is(ExistingClientSearchDto.class.getCanonicalName()));
    }

    @Ignore
    @Test
    public void search_shouldReturnUnfilteredList_WhenCriteriaIsEmpty() {
        List<ClientIdentificationDto> result = existingClientSearchDtoService.search(new ArrayList<ApiSearchCriteria>(), serviceErrors);
        assertThat(result.size(), is(2));
        assertThat(result.get(0).getClass().getCanonicalName(), is(IndividualWithAdvisersDto.class.getCanonicalName()));
        assertThat(result.get(1).getClass().getCanonicalName(), is(ExistingClientSearchDto.class.getCanonicalName()));
    }
}
