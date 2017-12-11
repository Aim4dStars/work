package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationDetailsDto;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.PermittedClientApplicationRepository;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.account.ApplicationDocumentDetailImpl;
import com.bt.nextgen.service.avaloq.accountactivation.ApprovalType;
import com.bt.nextgen.service.avaloq.accountactivation.LinkedPortfolioDetails;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.ApplicationDocumentDetail;
import com.bt.nextgen.service.integration.account.ApplicationDocumentIntegrationService;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by F058391 on 10/08/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class ViewClientApplicationDetailsServiceTest {


    @Mock
    PermittedClientApplicationRepository permittedClientApplicationRepository;
    @Mock
    private ClientApplicationDetailsDtoConverterService clientApplicationDetailsDtoConverterService;
    @Mock
    private ApplicationDocumentIntegrationService applicationDocumentIntegrationService;
    @Mock
    private BrokerHelperService brokerHelperService;

    @InjectMocks
    ViewClientApplicationDetailsServiceImpl viewClientApplicationDetailsServiceImpl;

    @Test
    public void viewClientApplicationById_returnsNullIfOnboardingApplicationIsNull() {
        ClientApplication clientApplication = mock(ClientApplication.class);
        long clientApplicationId = 123L;
        when(permittedClientApplicationRepository.findByClientApplicationId(eq(clientApplicationId))).thenReturn(clientApplication);

        assertNull(viewClientApplicationDetailsServiceImpl.viewClientApplicationById(clientApplicationId, new ServiceErrorsImpl()));
    }

    @Test
    public void viewClientApplicationById_returnsClientApplicationDetails() {
        long clientApplicationId = 123L;
        ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        ClientApplication clientApplication = mock(ClientApplication.class);
        when(clientApplication.getOnboardingApplication()).thenReturn(mock(OnBoardingApplication.class));
        when(permittedClientApplicationRepository.findByClientApplicationId(eq(clientApplicationId))).thenReturn(clientApplication);
        when(clientApplicationDetailsDtoConverterService.convert(eq(clientApplication), eq(serviceErrors))).thenReturn(mock(ClientApplicationDetailsDto.class));

        assertNotNull(viewClientApplicationDetailsServiceImpl.viewClientApplicationById(clientApplicationId, serviceErrors));
    }

    @Test
    public void viewClientApplicationByAccountNumber_returnsNullIfApplicationDocListIsEmpty() {
        String accountNumber = "accountNumber";
        assertNull(viewClientApplicationDetailsServiceImpl.viewClientApplicationByAccountNumber(accountNumber, new ServiceErrorsImpl()));
    }

    @Test
    public void viewClientApplicationByAccountNumber_returnsClientApplicationDetailsForAsim() {
        String accountNumber = "accountNumber";
        String applicationOrigin = "applicationOrigin";
        ApplicationDocumentDetail applicationDocumentDetail = mock(ApplicationDocumentDetail.class);
        LinkedPortfolioDetails linkedPortfolioDetails = mock(LinkedPortfolioDetails.class);
        when(linkedPortfolioDetails.getAccountNumber()).thenReturn("xyz");
        when(applicationDocumentDetail.getPortfolio()).thenReturn(Arrays.asList(linkedPortfolioDetails));
        ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        ClientApplicationDetailsDto clientApplicationDetailsDto = new ClientApplicationDetailsDto();
        when(applicationDocumentIntegrationService.loadApplicationDocuments(eq(newArrayList(accountNumber)), eq(serviceErrors)))
                .thenReturn(newArrayList(applicationDocumentDetail));

        when(brokerHelperService.getUserExperience(eq(applicationDocumentDetail),eq(serviceErrors))).thenReturn(UserExperience.ASIM);
        when(clientApplicationDetailsDtoConverterService.convert(eq(applicationDocumentDetail), eq(serviceErrors), eq(UserExperience.ASIM)))
                .thenReturn(clientApplicationDetailsDto);

        ClientApplicationDetailsDto clientApplicationDetailsDtoResult = viewClientApplicationDetailsServiceImpl.viewClientApplicationByAccountNumber(accountNumber, serviceErrors);
        assertNotNull(clientApplicationDetailsDtoResult);
        assertThat(clientApplicationDetailsDtoResult.getAccountKey(), Is.is("xyz"));

    }

    @Test
    public void viewClientApplicationByAccountNumbers_filterOfflineAccounts() {
        String accountNumber = "accountNumber";
        ApplicationDocumentDetailImpl applicationDocumentDetailImpl1 = mock(ApplicationDocumentDetailImpl.class);
        when(applicationDocumentDetailImpl1.getApprovalType()).thenReturn(ApprovalType.OFFLINE);
        ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        when(applicationDocumentIntegrationService.loadApplicationDocuments(eq(newArrayList(accountNumber)), eq(serviceErrors)))
                .thenReturn(newArrayList((ApplicationDocumentDetail)applicationDocumentDetailImpl1));
        viewClientApplicationDetailsServiceImpl.viewOnlineClientApplicationByAccountNumbers(newArrayList(accountNumber), serviceErrors);
        verify(brokerHelperService, times(0)).getUserExperience(eq(applicationDocumentDetailImpl1),eq(serviceErrors));
        verify(clientApplicationDetailsDtoConverterService, times(0)).convert(eq(applicationDocumentDetailImpl1), eq(serviceErrors),eq(UserExperience.ADVISED));
    }

    @Test
    public void viewClientApplicationByAccountNumbers_callWithOnlineAccounts() {
        String accountNumber = "accountNumber";
        ApplicationDocumentDetailImpl applicationDocumentDetailImpl = mock(ApplicationDocumentDetailImpl.class);
        LinkedPortfolioDetails linkedPortfolioDetails = mock(LinkedPortfolioDetails.class);
        when(linkedPortfolioDetails.getAccountNumber()).thenReturn("xyz");
        when(applicationDocumentDetailImpl.getPortfolio()).thenReturn(Arrays.asList(linkedPortfolioDetails));
        when(applicationDocumentDetailImpl.getApprovalType()).thenReturn(ApprovalType.ONLINE);
        ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        ClientApplicationDetailsDto clientApplicationDetailsDto = new ClientApplicationDetailsDto();
        when(applicationDocumentIntegrationService.loadApplicationDocuments(eq(newArrayList(accountNumber)), eq(serviceErrors)))
                .thenReturn(newArrayList((ApplicationDocumentDetail)applicationDocumentDetailImpl));
        when(brokerHelperService.getUserExperience(eq(applicationDocumentDetailImpl),eq(serviceErrors))).thenReturn(UserExperience.ADVISED);
        when(clientApplicationDetailsDtoConverterService.convert(eq(applicationDocumentDetailImpl), eq(serviceErrors), eq(UserExperience.ADVISED)))
                .thenReturn(clientApplicationDetailsDto);
        ClientApplicationDetailsDto clientApplicationDetailsDtoResult = viewClientApplicationDetailsServiceImpl.viewOnlineClientApplicationByAccountNumbers(newArrayList(accountNumber), serviceErrors);
        verify(brokerHelperService, times(1)).getUserExperience(eq(applicationDocumentDetailImpl),eq(serviceErrors));
        verify(clientApplicationDetailsDtoConverterService, times(1)).convert(eq(applicationDocumentDetailImpl), eq(serviceErrors),eq(UserExperience.ADVISED));
        assertThat(clientApplicationDetailsDtoResult.getAccountKey(), Is.is("xyz"));
    }

    @Test
    public void viewClientApplicationByAccountNumber_returnsClientApplicationDetailsForNonAsim() {
        String accountNumber = "accountNumber";
        String applicationOrigin = "applicationOrigin";
        ApplicationDocumentDetail applicationDocumentDetail = mock(ApplicationDocumentDetail.class);
        LinkedPortfolioDetails linkedPortfolioDetails = mock(LinkedPortfolioDetails.class);
        when(linkedPortfolioDetails.getAccountNumber()).thenReturn("xyz");
        when(applicationDocumentDetail.getPortfolio()).thenReturn(Arrays.asList(linkedPortfolioDetails));
        ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        ClientApplicationDetailsDto clientApplicationDetailsDto = new ClientApplicationDetailsDto();
        when(applicationDocumentIntegrationService.loadApplicationDocuments(eq(newArrayList(accountNumber)), eq(serviceErrors)))
                .thenReturn(newArrayList(applicationDocumentDetail));
        when(brokerHelperService.getUserExperience(eq(applicationDocumentDetail),eq(serviceErrors))).thenReturn(UserExperience.DIRECT);
        when(clientApplicationDetailsDtoConverterService.convert(eq(applicationDocumentDetail), eq(serviceErrors), eq(UserExperience.DIRECT)))
                .thenReturn(clientApplicationDetailsDto);

        ClientApplicationDetailsDto clientApplicationDetailsDtoResult = viewClientApplicationDetailsServiceImpl.viewClientApplicationByAccountNumber(accountNumber, serviceErrors);
        assertNotNull(clientApplicationDetailsDtoResult);
        assertThat(clientApplicationDetailsDtoResult.getAccountKey(), Is.is("xyz"));
    }
}