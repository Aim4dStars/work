package com.bt.nextgen.service.provisio;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import static com.bt.nextgen.core.util.Properties.getString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({URL.class, URLConnection.class, ProvisioServiceImpl.class})
public class ProvisioServiceTest {

    @InjectMocks
    private ProvisioServiceImpl provisioIntegrationService;

    private static final String PROVISIO_ENDPOINT = "provisio.endpoint";
    private static final String PROVISO_USER = "provisio.user";
    private static final String PROVISO_PASS = "provisio.password";

    private String url;

    @Before
    public void setUp() throws Exception {
        url = getString(PROVISIO_ENDPOINT) + "/" + getString(PROVISO_USER) + "/" + getString(PROVISO_PASS);
    }

    @Test
    public void getProvisioToken() throws Exception {
        URL mockedURL = PowerMockito.mock(URL.class);
        whenNew(URL.class).withParameterTypes(String.class).withArguments(url).thenReturn(mockedURL);

        URLConnection huc = PowerMockito.mock(URLConnection.class);
        when(huc.getInputStream()).thenReturn(new ByteArrayInputStream("token".getBytes()));
        when(mockedURL.openConnection()).thenReturn(huc);

        String result = provisioIntegrationService.getProvisioToken();
        assertNotNull(result);
    }

    @Test
    public void getProvisioToken_failure() throws Exception {
        URL mockedURL = PowerMockito.mock(URL.class);
        whenNew(URL.class).withParameterTypes(String.class).withArguments(url).thenReturn(mockedURL);

        URLConnection huc = PowerMockito.mock(URLConnection.class);
        when(huc.getInputStream()).thenThrow(new IOException());

        when(mockedURL.openConnection()).thenReturn(huc);

        String result = provisioIntegrationService.getProvisioToken();
        assertNull(result);
    }
}