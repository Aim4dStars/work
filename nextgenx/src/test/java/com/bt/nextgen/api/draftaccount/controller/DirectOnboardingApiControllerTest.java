package com.bt.nextgen.api.draftaccount.controller;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDtoDirectImpl;
import com.bt.nextgen.api.draftaccount.service.DirectOnboardingDtoService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.apache.struts.mock.MockHttpSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by F058391 on 18/04/2016.
 */

@RunWith(MockitoJUnitRunner.class)
public class DirectOnboardingApiControllerTest {

    @InjectMocks
    DirectOnboardingApiController directOnboardingApiController;

    @Mock
    private DirectOnboardingDtoService directOnboardingDtoService;

    @Test
    public void submitShouldUpdateTheSessionWithOriginatingSystem() {
        MockHttpSession session = new MockHttpSession();
        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoDirectImpl();
        when(directOnboardingDtoService.submit(any(ClientApplicationDto.class), any(ServiceErrorsImpl.class))).thenReturn(clientApplicationDto);
        directOnboardingApiController.submit(session,clientApplicationDto);
        assertThat(session.getAttribute("originatingSystem").toString(), is("WLIVE"));
    }

}