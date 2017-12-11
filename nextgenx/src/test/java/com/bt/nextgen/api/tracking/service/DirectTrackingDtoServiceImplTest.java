package com.bt.nextgen.api.tracking.service;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.tracking.model.DirectTrackingDto;
import com.bt.nextgen.api.tracking.model.TrackingDto;
import com.bt.nextgen.core.exception.ParseException;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingApplicationStatus;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplicationRepository;
import com.bt.nextgen.draftaccount.repository.ClientApplicationStatus;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DirectTrackingDtoServiceImplTest {

    @Mock
    private ClientApplicationRepository repository;

    @Mock
    private ServiceErrors serviceErrors;

    @Mock
    private TrackingDtoConverterService trackingDtoConverter;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    TrackingDtoService trackingDtoService;

    @InjectMocks
    private DirectTrackingDtoServiceImpl directTrackingDtoService;

    @Test
    public void findShouldReturnStatusForDirectAccount() throws ParseException {
        String adviserId = "ADVISER_ID";
        String orderId = "someOrderId";
        final String encryptedId = "EncryptedId";

        ClientApplication clientApplication = createApplication(adviserId, 1, orderId,
                ClientApplicationStatus.processing);
        ClientApplicationKey key = new ClientApplicationKey(1L);
        TrackingDto trackingDto = new TrackingDto(DateTime.now(), "individual", key);
        trackingDto.setStatus(OnboardingApplicationStatus.active);
        trackingDto.setEncryptedBpId(encryptedId);

        when(repository.find(Mockito.anyLong())).thenReturn(clientApplication);

        when(trackingDtoService.getTrackingDtos(eq(Arrays.asList(clientApplication)), eq(true), eq(false), eq(serviceErrors)))
                .thenReturn(Arrays.asList(trackingDto));
        DirectTrackingDto dto = directTrackingDtoService.find(key, serviceErrors);

        assertThat(dto.getEncryptedAccountId(), is(encryptedId));
        assertThat(dto.getStatus(), is(OnboardingApplicationStatus.active));
    }

    @Test
    public void findShouldReturnEmptyAccountIdIfStatusIsNotActive() throws ParseException {
        String adviserId = "ADVISER_ID";
        String orderId = "someOrderId";

        ClientApplication clientApplication = createApplication(adviserId, 1, orderId,
                ClientApplicationStatus.processing);
        ClientApplicationKey key = new ClientApplicationKey(1L);
        TrackingDto trackingDto = new TrackingDto(DateTime.now(), "individual", key);
        trackingDto.setStatus(OnboardingApplicationStatus.processing);

        when(repository.find(Mockito.anyLong())).thenReturn(clientApplication);

        when(trackingDtoService.getTrackingDtos(eq(Arrays.asList(clientApplication)), eq(true), eq(false), eq(serviceErrors)))
                .thenReturn(Arrays.asList(trackingDto));
        DirectTrackingDto dto = directTrackingDtoService.find(key, serviceErrors);

        assertNull(dto.getEncryptedAccountId());
        assertThat(dto.getStatus(), is(OnboardingApplicationStatus.processing));
    }

    private ClientApplication createApplication(String adviserId, long id, String orderId, ClientApplicationStatus status) {
        ClientApplication clientApplication = mock(ClientApplication.class);
        when(clientApplication.getAdviserPositionId()).thenReturn(adviserId);
        when(clientApplication.getId()).thenReturn(id);
        when(clientApplication.getStatus()).thenReturn(status);
        OnBoardingApplication onBoardingApplication = mock(OnBoardingApplication.class);
        when(onBoardingApplication.getAvaloqOrderId()).thenReturn(orderId);
        when(clientApplication.getOnboardingApplication()).thenReturn(onBoardingApplication);
        when(onBoardingApplication.getKey()).thenReturn(OnboardingApplicationKey.valueOf(id));
        return clientApplication;
    }
}