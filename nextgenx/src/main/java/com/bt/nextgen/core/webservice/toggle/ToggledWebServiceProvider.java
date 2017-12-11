package com.bt.nextgen.core.webservice.toggle;

import com.btfin.panorama.core.security.saml.SamlTokenInterface;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import org.slf4j.Logger;
import org.springframework.ws.client.core.SourceExtractor;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Facade implementation of the {@code WebServiceProvider} service that is chained to an underlying implementation.
 * Based on a set of pre-configured mapping of service name to toggle name, this provider will provide either the
 * &quot;new&quot; version of the web service, or the &quot;old&quot; one. New versions are expected to simply have
 * the regular service name, old versions will have the same name with a {@code -old} suffix.
 * @see com.bt.nextgen.core.toggle.FeatureToggles
 */
public class ToggledWebServiceProvider implements WebServiceProvider {

    /** The suffix expected to be appended to the key of the &quot;old&quot; version of the web service template. */
    public static final String OLD_SUFFIX = "-old";

    /** Logger. */
    private static final Logger LOGGER = getLogger(ToggledWebServiceProvider.class);

    /** The underlying {@code WebServiceProvider} that will actually be handling requests. */
    private final WebServiceProvider provider;

    /** Feature toggles service that we will use to retrieve feature toggles. */
    private final FeatureTogglesService togglesService;

    /** Map of web service keys to toggle names. */
    private final Map<String, String> serviceToggles;

    /**
     * Full constructor.
     * @param provider underlying actual web service provider.
     * @param togglesService the toggles service used to check.
     * @param serviceToggles map of service toggles to use.
     */
    public ToggledWebServiceProvider(WebServiceProvider provider, FeatureTogglesService togglesService,
            Map<String, String> serviceToggles) {
        this.provider = provider;
        this.togglesService = togglesService;
        this.serviceToggles = serviceToggles;
    }

    /**
     * Default toggle map constructor.
     * @param provider underlying actual web service provider.
     * @param togglesService the toggles service used to check.
     */
    public ToggledWebServiceProvider(WebServiceProvider provider, FeatureTogglesService togglesService) {
        this(provider, togglesService, new HashMap<String, String>());
    }

    /**
     * Add a toggle to a web service key.
     * @param serviceKey the web service that needs to be toggled between different versions.
     * @param toggleName the name of the toggle to check whether to use the correct name, or the &quot;old&quot; name
     *                   in case the feature toggle is currently dormant.
     */
    public void addServiceToggle(String serviceKey, String toggleName) {
        serviceToggles.put(serviceKey, toggleName);
    }

    /**
     * Checks to see whether the specified service key is flagged as toggled, and will replace it with the relevant
     * key if this is the case.
     * @param serviceKey the service key to check.
     * @param errors service errors to use.
     * @return the &quot;old&quot; version of the service key, if it is flagged as toggled, and that toggle is
     *   currently off.
     */
    private String toggled(final String serviceKey, final ServiceErrors errors) {
        final String toggleName = serviceToggles.get(serviceKey);
        if (toggleName != null) {
            final FeatureToggles toggles = togglesService.findOne(errors);
            final boolean toggle = toggles.getFeatureToggle(toggleName);
            if (!toggle) {
                LOGGER.info("Feature toggle \"{}\" is inactive; toggling -old version of web service key: {}", toggleName, serviceKey);
                return serviceKey + OLD_SUFFIX;
            }
        }
        return serviceKey;
    }

    /**
     * Checks to see whether the specified service key is flagged as toggled, and will replace it with the relevant
     * key if this is the case.
     * @param serviceKey the service key to check.
     * @return the &quot;old&quot; version of the service key, if it is flagged as toggled, and that toggle is
     *   currently off.
     */
    private String toggled(final String serviceKey) {
        return toggled(serviceKey, new FailFastErrorsImpl());
    }

    @Override
    public Object sendWebService(String serviceKey, Object request) {
        return provider.sendWebService(toggled(serviceKey), request);
    }

    @Override
    public WebServiceTemplate getWebServiceTemplate(String serviceKey) {
        return provider.getWebServiceTemplate(toggled(serviceKey));
    }

    @Override
    public Object sendWebServiceWithSecurityHeader(SamlTokenInterface credentials, String serviceKey, Object request) {
        return provider.sendWebServiceWithSecurityHeader(credentials, toggled(serviceKey), request);
    }

    @Override
    public CorrelatedResponse sendWebServiceWithSecurityHeaderAndResponseCallback(SamlTokenInterface credentials, String serviceKey, Object request, ServiceErrors errors) {
        return provider.sendWebServiceWithSecurityHeaderAndResponseCallback(credentials, toggled(serviceKey, errors), request, errors);
    }

    @Override
    public CorrelatedResponse sendWebServiceWithSecurityHeaderAndResponseCallbackWithHeaderValues(SamlTokenInterface credentials, String serviceKey, Object request, ServiceErrors errors) {
        return provider.sendWebServiceWithSecurityHeaderAndResponseCallbackWithHeaderValues(credentials, toggled(serviceKey, errors), request, errors);
    }

    @Override
    public CorrelatedResponse sendWebServiceWithResponseCallback(String serviceKey, Object request) {
        return provider.sendWebServiceWithResponseCallback(toggled(serviceKey), request);
    }

    @Override
    public <T> T parseSendAndReceiveToDomain(SamlTokenInterface credentials, String serviceKey, Object request, SourceExtractor<T> extractor) {
        return provider.parseSendAndReceiveToDomain(credentials, toggled(serviceKey), request, extractor);
    }
}
