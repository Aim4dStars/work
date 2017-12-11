package com.bt.nextgen.core.webservice.toggle;

import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.CorrelationIdWrapper;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.integration.xml.extractor.ResponseExtractor;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ws.client.core.WebServiceTemplate;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertSame;

@RunWith(MockitoJUnitRunner.class)
public class ToggledWebServiceProviderTest {

    @Mock
    private WebServiceProvider provider;

    @Mock
    private FeatureTogglesService service;

    @Mock
    private ServiceErrors errors;

    private FeatureToggles toggles;

    private ToggledWebServiceProvider toggledProvider;

    private Object request;

    private CorrelatedResponse response;

    private SamlToken token;

    @Before
    public void initToggledProvider() {
        request = new Object();
        response = new CorrelatedResponse(new CorrelationIdWrapper(), new Object());
        token = new SamlToken("");
        toggles = new FeatureToggles();
        when(service.findOne(any(ServiceErrors.class))).thenReturn(toggles);
        toggledProvider = new ToggledWebServiceProvider(provider, service);
    }

    @Test
    public void getWebServiceTemplateWithUnmatchedToggle() {
        final WebServiceTemplate template = new WebServiceTemplate();
        when(provider.getWebServiceTemplate("test")).thenReturn(template);
        assertSame(template, toggledProvider.getWebServiceTemplate("test"));
    }

    @Test
    public void getWebServiceTemplateWithMatchedToggleAndToggleOn() {
        toggledProvider.addServiceToggle("test", "toggle");
        toggles.setFeatureToggle("toggle", true);
        final WebServiceTemplate template = new WebServiceTemplate();
        when(provider.getWebServiceTemplate("test")).thenReturn(template);
        assertSame(template, toggledProvider.getWebServiceTemplate("test"));
    }

    @Test
    public void getWebServiceTemplateWithMatchedToggleAndToggleOff() {
        toggledProvider.addServiceToggle("test", "toggle");
        toggles.setFeatureToggle("toggle", false);
        final WebServiceTemplate template = new WebServiceTemplate();
        when(provider.getWebServiceTemplate("test-old")).thenReturn(template);
        assertSame(template, toggledProvider.getWebServiceTemplate("test"));
    }

    @Test
    public void sendWebService() {
        toggledProvider.addServiceToggle("test", "toggle");
        toggles.setFeatureToggle("toggle", false);
        when(provider.sendWebService("test-old", request)).thenReturn(response);
        assertSame(response, toggledProvider.sendWebService("test", request));
    }

    @Test
    public void sendWebServiceWithSecurityHeader() {
        toggledProvider.addServiceToggle("test", "toggle");
        toggles.setFeatureToggle("toggle", true);
        when(provider.sendWebServiceWithSecurityHeader(token, "test", request)).thenReturn(response);
        assertSame(response, toggledProvider.sendWebServiceWithSecurityHeader(token, "test", request));
    }

    @Test
    public void sendWebServiceWithSecurityHeaderAndResponseCallback() {
        when(provider.sendWebServiceWithSecurityHeaderAndResponseCallback(token, "test", request, errors)).thenReturn(response);
        assertSame(response, toggledProvider.sendWebServiceWithSecurityHeaderAndResponseCallback(token, "test", request, errors));
    }

    @Test
    public void sendWebServiceWithSecurityHeaderAndResponseCallbackWithHeaderValues() {
        toggledProvider.addServiceToggle("test", "toggle");
        toggles.setFeatureToggle("toggle", false);
        when(provider.sendWebServiceWithSecurityHeaderAndResponseCallbackWithHeaderValues(token, "test-old", request, errors)).thenReturn(response);
        assertSame(response, toggledProvider.sendWebServiceWithSecurityHeaderAndResponseCallbackWithHeaderValues(token, "test", request, errors));
    }

    @Test
    public void sendWebServiceWithResponseCallback() {
        toggledProvider.addServiceToggle("test", "toggle");
        toggles.setFeatureToggle("toggle", true);
        when(provider.sendWebServiceWithResponseCallback("test", request)).thenReturn(response);
        assertSame(response, toggledProvider.sendWebServiceWithResponseCallback("test", request));
    }

    @Test
    public void parseSendAndReceiveToDomain() {
        @SuppressWarnings("unchecked")
        final ResponseExtractor<Object> extractor = mock(ResponseExtractor.class);
        when(provider.parseSendAndReceiveToDomain(token, "test", request, extractor)).thenReturn(response);
        assertSame(response, toggledProvider.parseSendAndReceiveToDomain(token, "test", request, extractor));
    }
}
