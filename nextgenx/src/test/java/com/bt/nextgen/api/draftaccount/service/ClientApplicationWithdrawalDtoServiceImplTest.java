package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationWithdrawalDto;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingParty;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.PermittedClientApplicationRepository;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.accountactivation.AccountActivationRequest;
import com.bt.nextgen.service.integration.accountactivation.ActivationAccountIntegrationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientApplicationWithdrawalDtoServiceImplTest {

    @Mock
    private PermittedClientApplicationRepository permittedClientApplicationRepository;

    @Mock
    ActivationAccountIntegrationService activationAccountIntegrationService;

    @InjectMocks
    ClientApplicationWithdrawalDtoServiceImpl clientApplicationWithdrawalDtoService;

    ServiceErrors serviceErrors;

    @Test
    public void submitShouldWithdrawTheApplicationWithTheGivenAvaloqID(){
        Long clientApplicationId =123L;
        ClientApplication clientApplication = createClientApplicationMock(clientApplicationId);
        serviceErrors = new ServiceErrorsImpl();
        ClientApplicationWithdrawalDto clientApplicationWithdrawalDto = new ClientApplicationWithdrawalDto(new ClientApplicationKey(clientApplicationId));
        when(permittedClientApplicationRepository.find(eq(clientApplicationId))).thenReturn(clientApplication);


        ArgumentCaptor<AccountActivationRequest> captor = ArgumentCaptor.forClass(AccountActivationRequest.class);
        clientApplicationWithdrawalDtoService.submit(clientApplicationWithdrawalDto,serviceErrors);
        verify(activationAccountIntegrationService).withdrawAccount(captor.capture(), eq(serviceErrors));

        AccountActivationRequest accountActivationRequest = captor.getValue();
        assertThat(accountActivationRequest.getOrderId(), is("avaloqId"));
        assertThat(accountActivationRequest.getGcmId(), is("InvestorGcmId1"));
    }

    @Test
    public void submitShouldSetTheWithdrawalStatusReturnedByActivationAccountService(){
        Long clientApplicationId =123L;
        ClientApplication clientApplication = createClientApplicationMock(clientApplicationId);
        serviceErrors = new ServiceErrorsImpl();
        ClientApplicationWithdrawalDto clientApplicationWithdrawalDto = new ClientApplicationWithdrawalDto(new ClientApplicationKey(clientApplicationId));
        when(permittedClientApplicationRepository.find(eq(clientApplicationId))).thenReturn(clientApplication);

        ClientApplicationWithdrawalDto returnedDto = clientApplicationWithdrawalDtoService.submit(clientApplicationWithdrawalDto, serviceErrors);
        when(activationAccountIntegrationService.withdrawAccount(any(AccountActivationRequest.class), eq(serviceErrors))).thenReturn(false);
        assertThat(returnedDto.isWithdrawn(), is(false));
    }

    ClientApplication createClientApplicationMock(Long id){
        OnBoardingApplication onboardingapplication = new OnBoardingApplication(null, "avaloqId");
        ClientApplication clientApplication = mock(ClientApplication.class);
        when(clientApplication.getId()).thenReturn(id);
        when(clientApplication.getOnboardingApplication()).thenReturn(onboardingapplication);

        OnboardingParty party1 = new OnboardingParty();
        party1.setGcmPan("InvestorGcmId1");

        OnboardingParty party2 = new OnboardingParty();
        party2.setGcmPan("InvestorGcmId2");

        onboardingapplication.setParties(Arrays.asList(party1,party2));

        return clientApplication;
    }

}