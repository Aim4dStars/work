package com.bt.nextgen.api.client.v2.controller;

import com.bt.nextgen.api.client.model.ClientIdentificationDto;
import com.bt.nextgen.api.client.model.ExistingClientSearchDto;
import com.bt.nextgen.api.client.service.ClientSearchDtoServiceImpl;
import com.bt.nextgen.api.client.service.ExistingClientSearchDtoServiceImpl;
import com.bt.nextgen.api.client.v2.model.IndividualWithAdvisersDto;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.avaloq.broker.BrokerIdentifierImpl;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.bind.WebDataBinder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static com.googlecode.totallylazy.matchers.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientListApiControllerTest
{
    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private UserProfileService profileService;

    @Mock
    private ClientSearchDtoServiceImpl clientSearchDtoService;

    @Mock
    private ExistingClientSearchDtoServiceImpl existingClientSearchDtoService;

	@InjectMocks
	private ClientListApiController clientListApiController;

    @Test
    public void getSearchedClient_shouldReturnOnlyClientsAssociatedToTheAdviser() {
        List<ClientIdentificationDto> clientIdentificationDtos = Arrays.<ClientIdentificationDto>asList(
                buildMockIndividuals("pitterson", Arrays.asList("ADVISER1", "ADV2"), true, true),
                buildMockIndividuals("piter", Arrays.asList("ADV3", "ADVISER_ID"), true, true));

        when(existingClientSearchDtoService.search(any(List.class), any(ServiceErrors.class))).thenReturn(clientIdentificationDtos);
        when(profileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        when(brokerIntegrationService.getAdvisersForUser(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(Arrays.<BrokerIdentifier>asList(new BrokerIdentifierImpl(), new BrokerIdentifierImpl()));

        ApiResponse response = clientListApiController.getSearchedClients("pit", EncodedString.fromPlainText("ADVISER_ID").toString());
        ResultListDto<ExistingClientSearchDto> clientListDto = (ResultListDto<ExistingClientSearchDto>) response.getData();
        assertThat(clientListDto.getResultList().size(), is(1));
        assertThat(clientListDto.getResultList().get(0).getDisplayName(), is("piter"));
    }

    @Test
    public void getSearchedClient_shouldReturnOnlyClientsAssociatedToTheAdviserMatchingQuery() {
        List<ClientIdentificationDto> clientIdentificationDtos = Arrays.<ClientIdentificationDto>asList(
                buildMockIndividuals("piter", Arrays.asList("ADVISER_ID"), true, true),
                buildMockIndividuals("pitterly", Arrays.asList("ADVISER_ID"), true, true));

        when(existingClientSearchDtoService.search(any(List.class), any(ServiceErrors.class))).thenReturn(clientIdentificationDtos);
        when(profileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        when(brokerIntegrationService.getAdvisersForUser(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(Arrays.<BrokerIdentifier>asList(new BrokerIdentifierImpl(), new BrokerIdentifierImpl()));

        ApiResponse response = clientListApiController.getSearchedClients("pit", EncodedString.fromPlainText("ADVISER_ID").toString());
        ResultListDto<ExistingClientSearchDto> clientListDto = (ResultListDto<ExistingClientSearchDto>) response.getData();
        assertThat(clientListDto.getResultList().size(), is(2));
        assertThat(clientListDto.getResultList().get(0).getDisplayName(), is("piter"));
        assertThat(clientListDto.getResultList().get(1).getDisplayName(), is("pitterly"));
    }

    @Test
    public void getSearchedClient_shouldReturnOnlyIndividualClients() {
        List<ClientIdentificationDto> clientIdentificationDtos = Arrays.<ClientIdentificationDto>asList(
                buildMockLegalPerson("piter", true),
                buildMockIndividuals("pitterly", Arrays.asList("ADVISER_ID"), true, true));

        when(existingClientSearchDtoService.search(any(List.class), any(ServiceErrors.class))).thenReturn(clientIdentificationDtos);
        when(profileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        when(brokerIntegrationService.getAdvisersForUser(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(Arrays.<BrokerIdentifier>asList(new BrokerIdentifierImpl(), new BrokerIdentifierImpl()));

        ApiResponse response = clientListApiController.getSearchedClients("pit", EncodedString.fromPlainText("ADVISER_ID").toString());
        ResultListDto<ExistingClientSearchDto> clientListDto = (ResultListDto<ExistingClientSearchDto>) response.getData();
        assertThat(clientListDto.getResultList().size(), is(1));
        assertThat(clientListDto.getResultList().get(0).getDisplayName(), is("pitterly"));
    }

    @Test
    public void getSearchedClient_shouldReturnOnlyIndividualClientsWithVerifiedId() {
        List<ClientIdentificationDto> clientIdentificationDtos = Arrays.<ClientIdentificationDto>asList(
                buildMockIndividuals("pitterly", Arrays.asList("ADVISER_ID"), true, true),
                buildMockIndividuals("pitter", Arrays.asList("ADVISER_ID"), false, true));

        when(existingClientSearchDtoService.search(any(List.class), any(ServiceErrors.class))).thenReturn(clientIdentificationDtos);
        when(profileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        when(brokerIntegrationService.getAdvisersForUser(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(Arrays.<BrokerIdentifier>asList(new BrokerIdentifierImpl(), new BrokerIdentifierImpl()));

        ApiResponse response = clientListApiController.getSearchedClients("pit", EncodedString.fromPlainText("ADVISER_ID").toString());
        ResultListDto<ExistingClientSearchDto> clientListDto = (ResultListDto<ExistingClientSearchDto>) response.getData();
        assertThat(clientListDto.getResultList().size(), is(1));
        assertThat(clientListDto.getResultList().get(0).getDisplayName(), is("pitterly"));
    }

    /**
     * individualInvestor will be true if the investor is individual and not legal entity and has provisional online access
     * The check is on whether the investor has primary email address and primary mobile or not.
     */
    @Test
    public void getSearchedClient_shouldReturnOnlyIndividualClientsWhoAreIndivialInvestor() {
        List<ClientIdentificationDto> clientIdentificationDtos = Arrays.<ClientIdentificationDto>asList(
                buildMockIndividuals("pitterly", Arrays.asList("ADVISER_ID"), true, true),
                buildMockIndividuals("pitter", Arrays.asList("ADVISER_ID"), false, true),
                buildMockIndividuals("jack", Arrays.asList("ADVISER_ID"), true, false));

        when(existingClientSearchDtoService.search(any(List.class), any(ServiceErrors.class))).thenReturn(clientIdentificationDtos);
        when(profileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        when(brokerIntegrationService.getAdvisersForUser(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(Arrays.<BrokerIdentifier>asList(new BrokerIdentifierImpl(), new BrokerIdentifierImpl()));

        ApiResponse response = clientListApiController.getSearchedClients("pit", EncodedString.fromPlainText("ADVISER_ID").toString());
        ResultListDto<ExistingClientSearchDto> clientListDto = (ResultListDto<ExistingClientSearchDto>) response.getData();
        assertThat(clientListDto.getResultList().size(), is(1));
        assertThat(clientListDto.getResultList().get(0).getDisplayName(), is("pitterly"));
    }

    @Test
    public void getSearchedClient_shouldReturnAllIndividualClients_whenThereIsOnlyOneAdviser() {
        List<ClientIdentificationDto> clientIdentificationDtos = Arrays.<ClientIdentificationDto>asList(
                buildMockIndividuals("pitterly", Arrays.asList("ADVISER_ID1"), true, true),
                buildMockIndividuals("pitter", Arrays.asList("ADVISER_ID2"), true, true));

        when(existingClientSearchDtoService.search(any(List.class), any(ServiceErrors.class))).thenReturn(clientIdentificationDtos);
        when(profileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        when(brokerIntegrationService.getAdvisersForUser(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(Arrays.<BrokerIdentifier>asList(new BrokerIdentifierImpl()));

        ApiResponse response = clientListApiController.getSearchedClients("pit", EncodedString.fromPlainText("ADVISER_ID").toString());
        ResultListDto<ExistingClientSearchDto> clientListDto = (ResultListDto<ExistingClientSearchDto>) response.getData();
        assertThat(clientListDto.getResultList().size(), is(2));
        assertThat(clientListDto.getResultList().get(0).getDisplayName(), is("pitterly"));
        assertThat(clientListDto.getResultList().get(1).getDisplayName(), is("pitter"));
    }

    @Test
    public void UpdateBinder_test() {
        WebDataBinder binder = mock(WebDataBinder.class);
        clientListApiController.updateInitBinder(binder);
        verify(binder, times(1)).setAllowedFields((String[]) Mockito.anyVararg());
    }

    private IndividualWithAdvisersDto buildMockIndividuals(String displayName, List<String> adviserPositionIds, boolean idVerified, boolean individualInvestor) {
        IndividualWithAdvisersDto client = mock(IndividualWithAdvisersDto.class);
        HashSet<String> adivsers = new HashSet<String>();
        adivsers.addAll(adviserPositionIds);
        when(client.getAdviserPositionIds()).thenReturn(adivsers);
        when(client.getDisplayName()).thenReturn(displayName);
        when(client.isIdVerified()).thenReturn(idVerified);
        when(client.getInvestorType()).thenReturn("Individual");
        when(client.isIndividualInvestor()).thenReturn(individualInvestor);
        return client;
    }

    private ExistingClientSearchDto buildMockLegalPerson(String displayName, boolean idVerified) {
        ExistingClientSearchDto client = mock(ExistingClientSearchDto.class);
        when(client.getDisplayName()).thenReturn(displayName);
        when(client.isIdVerified()).thenReturn(idVerified);
        return client;
    }

}
