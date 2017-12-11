package com.bt.nextgen.config;

import static com.bt.nextgen.core.util.SETTINGS.WEB_SERVICE_MAX_CONNECTIONS;
import static com.bt.nextgen.core.util.SETTINGS.WEB_SERVICE_MAX_CONNECTIONS_PER_HOST_;
import static com.bt.nextgen.core.webservice.toggle.ToggledWebServiceProvider.OLD_SUFFIX;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.SSLContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.ws.security.WSSConfig;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.security.wss4j.Wss4jSecurityInterceptor;
import org.springframework.ws.soap.security.wss4j.support.CryptoFactoryBean;
import org.springframework.ws.transport.WebServiceMessageSender;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import com.bt.nextgen.core.security.SecurityTokenRequest;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.webservice.interceptor.ApplicationSubmissionEsbHeaderInterceptor;
import com.bt.nextgen.core.webservice.interceptor.ChannelEsbHeaderInterceptor;
import com.bt.nextgen.core.webservice.interceptor.ClientDetailsEsbHeaderInterceptor;
import com.bt.nextgen.core.webservice.interceptor.ClientPayloadLoggingInterceptor;
import com.bt.nextgen.core.webservice.interceptor.EsbHeaderAdderInterceptor;
import com.bt.nextgen.core.webservice.interceptor.PassThroughInterceptor;
import com.bt.nextgen.core.webservice.interceptor.ServiceOpsEsbHeaderInterceptor;
import com.bt.nextgen.core.webservice.interceptor.SoapActionInterceptor;
import com.bt.nextgen.core.webservice.provider.EndPointType;
import com.bt.nextgen.core.webservice.provider.SpringWebServiceTemplateProvider;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.core.webservice.toggle.ToggledWebServiceProvider;
import com.bt.nextgen.core.webservice.validation.JaxbSchemaValidationWebServiceProvider;
import com.bt.nextgen.core.ws.MockWebServiceResponseFactory;
import com.btfin.panorama.core.util.StringUtil;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Configuration
// Note that the properties mentioned below are specifically ordered.
// Properties defined in files on the right take precedence over ones defined earlier on the left when there are key clashes.
// Therefore, entries in env.properties (specific to an environment) supersede anything in the base common.properties
@PropertySource(value =
        {
                "classpath:/version-app.properties", "classpath:/common.properties", "classpath:/env.properties"
        })
@SuppressWarnings("squid:S00112")
public class WebServiceConfig {
    private static final Logger LOGGER = getLogger(WebServiceConfig.class);

    private static final String MARSHALLER_CONTEXT = "jaxb.marshaller.contextpath";
    private static final String UNMARSHALLER_PACKAGE = "jaxb.unmarhsaller.packagescan";
    private static final String WEBSERVICE_PROVIDER_SERVICES = "webservice.provider.services";
    private static final String MARSHALLER_BEAN = ".marshaller";
    private static final String UNMARSHALLER_BEAN = ".unmarshaller";
    private static final String WEBSERVICE_INTERCEPTORS = ".interceptors";
    private static final String SOAP_VERSION = ".soap.version";
    private static final String ENDPOINT_TYPE = ".webservice.endpoint.type";
    private static final String ENDPOINT_KEY = ".webservice.endpoint.key";
    private static final String SOAP_ACTION = ".webservice.soap.action";
    private static final String WEBSERVICE_TOGGLE = ".webservice.toggle";
    private static final String SCHEMA_URI = ".schema.uri";
    private static final String SCHEMA_VALIDATE = ".schema.validate";

    private static final String WEBSERVICE_FILESTUB = ".webservice.filestub";
    private static final String WEBSERVICE_HTTP_CREDENTIALS = ".webservice.http.credentials";
    private static final String WEBSERVICE_TIMEOUT = "webservice.timeout";
    private static final String UDDI_ENDPOINT = "uddi.endpoint";

    private static final String KEYSTORE_ENABLED = "ssl.keyStore.enabled";
    private static final String KEYSTORE = "ssl.keystore";
    private static final String KEYSTORE_PASSWORD = "ssl.keystore.password";
    private static final String CERTIFICATE_NAME = "ssl.certificate.name";
    private static final String DIRECT_SSL_CONFIG = "ssl.config.direct";

    private static final String KEYSTORE_TYPE = "ssl.keystore.type";
    private static final String TRUSTSTORE_TYPE = "ssl.truststore.type";

    //Configuration options for STS
    private static final String STR_ENDPOINT_PREFIX = "sts.avaloq.";

    com.ibm.websphere.ssl.JSSEHelper jsseHelper = com.ibm.websphere.ssl.JSSEHelper.getInstance();

    private static final String SSL_CONFIG_ALIAS = "ssl.config.alias";

    String KEY_SSL_PROP_KEYSTORE_TYPE = "com.ibm.ssl.keyStoreType";
    private static final String KEY_SSL_PROP_KEYSTORE_LOCATION = "com.ibm.ssl.keyStore";
    private static final String KEY_SSL_PROP_KEYSTORE_PWD = "com.ibm.ssl.keyStorePassword";
    private static final String KEY_SSL_PROP_TRUSTSTORE_LOCATION = "com.ibm.ssl.trustStore";
    private static final String KEY_SSL_PROP_TRUSTSTORE_PWD = "com.ibm.ssl.trustStorePassword";

    // check certificate expiry
    private static final int CHECK_MONTHS_AHEAD = 5;
    public static final String CERTIFICATE_CHECK_LOGGING = "CERTIFICATE_CHECK_LOGGING";
    public static final String CERTIFICATE_CHECK_LOGGING_DELIMITER = "::";
    private static final String NOTICE_ME_LOG_STRING = CERTIFICATE_CHECK_LOGGING + CERTIFICATE_CHECK_LOGGING_DELIMITER + " ";


    private enum CertificateStoreType {
        KEYSTORE, TRUSTSTORE;
    }

    @Autowired
    private ApplicationContextProvider context;

    @Autowired
    private FeatureTogglesService featureTogglesService;

    @Bean(name = "marshaller")
    public Jaxb2Marshaller marshaller() throws Exception {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(Properties.get(MARSHALLER_CONTEXT));

		/*HashMap<String,Object> props = new HashMap<>();
        props.put("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");
		marshaller.setJaxbContextProperties(props);*/

        return marshaller;
    }

    @Bean(name = "jaxbMtomMarshaller")
    public Jaxb2Marshaller mtomMarshaller() throws JAXBException {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(Properties.get(MARSHALLER_CONTEXT));
        marshaller.setMtomEnabled(true);
        return marshaller;
    }

    @Bean(name = "unmarshaller")
    public Jaxb2Marshaller unmarshaller() throws Exception {
        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setPackagesToScan(Properties.getArray(UNMARSHALLER_PACKAGE));
        unmarshaller.setMtomEnabled(true);
		/*HashMap<String,Object> props = new HashMap<>();
		props.put("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");
		unmarshaller.setJaxbContextProperties(props);*/
        return unmarshaller;
    }

    @Bean(name = "saajSOAP12MessageFactory")
    public SoapMessageFactory saajSOAP12() {

        SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory();
        messageFactory.setSoapVersion(SoapVersion.SOAP_12);
        return messageFactory;
    }

    @Bean(name = "saajSoap11MessageFactory")
    public SoapMessageFactory saajSOAP11() {
        //org.apache.axis2.saaj.MessageFactoryImpl apacheSaajFactory = new org.apache.axis2.saaj.MessageFactoryImpl();
        SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory();
        messageFactory.setSoapVersion(SoapVersion.SOAP_11);
        return messageFactory;
    }

    @Bean(name = "httpMessageSender")
    public WebServiceMessageSender httpMessageSender() {
        HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender();
        int timeOut = Properties.getInteger(WEBSERVICE_TIMEOUT);
        messageSender.setReadTimeout(timeOut);
        messageSender.setConnectionTimeout(timeOut);
        messageSender.setHttpClient(buildApacheHttpClient());
        return messageSender;
    }

    /**
     * Sets the default settings per host.
     *
     * @return
     */
    private PoolingHttpClientConnectionManager applyMaxConnectionsPerHost(
            PoolingHttpClientConnectionManager pooledConnectionManager) {
        try {
            pooledConnectionManager.setMaxTotal(Integer.parseInt(WEB_SERVICE_MAX_CONNECTIONS.value()));
            pooledConnectionManager.setDefaultMaxPerRoute(Integer.parseInt(WEB_SERVICE_MAX_CONNECTIONS_PER_HOST_.value()));
        } catch (Exception e) {
            throw new RuntimeException("Unable to set the maximum connections on the httpclient", e);
        }

        return pooledConnectionManager;
    }

    /**
     * Builds the httpclient object required that is used to establish connection to endpoint
     *
     * @return
     */
	@SuppressFBWarnings(value = "squid:S00112")
	@SuppressWarnings("squid:S00112")
    public HttpClient buildApacheHttpClient() {
        LOGGER.info("Building Apache HttpComponent object for Spring webServiceTemplate");

        HttpClient httpclient = null;

        // Grab trust store details
        KeyStoreProperties truststoreProps = getKeyStoreProperties(CertificateStoreType.TRUSTSTORE);
        String truststoreLocation = truststoreProps.getKeystoreLocation();
        String truststorePwd = truststoreProps.getKeystorePassword();

        LOGGER.info("Truststore location: {}", truststoreLocation);
        LOGGER.info("Truststore password: {}", (truststorePwd == null ? "[Not Specified]" : "Specified"));

        // Grab key store details
        KeyStoreProperties keyStoreProps = getKeyStoreProperties(CertificateStoreType.KEYSTORE);
        String keystoreLocation = keyStoreProps.getKeystoreLocation();
        String keystorePassword = keyStoreProps.getKeystorePassword();

        LOGGER.info("Keystore location: {}", keystoreLocation);
        LOGGER.info("Keystore password: {}", (keystorePassword == null ? "[Not Specified]" : "Specified"));

        // If properties for truststore location and password cannot be found, then abort ssl setup
        if (Properties.getSafeBoolean(KEYSTORE_ENABLED) == true
                && (StringUtils.isEmpty(truststoreLocation) || StringUtils.isEmpty(truststorePwd))) {
            throw new IllegalStateException("TrustStore or KeyStore properties not found");
        }

        try {
            SSLContext sslcontext = null;

            // If remote servers require mutual ssl
            // Load all trust certs (in truststore) and severs own public key (in keystore)
            if (Properties.getSafeBoolean(KEYSTORE_ENABLED) == true) {
                final KeyStore trustStore = KeyStore.getInstance(Properties.getString(TRUSTSTORE_TYPE));
                final KeyStore keyStore = KeyStore.getInstance(Properties.getString(KEYSTORE_TYPE));

                LOGGER.debug("Loading truststore from {}", truststoreLocation);
                try (FileInputStream instream = new FileInputStream(new File(truststoreLocation))) {
                    trustStore.load(instream, truststorePwd.toCharArray());
                } catch (Exception e) {
                    LOGGER.error("Unable to load trust store", e);
                }

                LOGGER.debug("Loading keystore from {}", keystoreLocation);
                try (FileInputStream instream2 = new FileInputStream(new File(keystoreLocation))) {
                    keyStore.load(instream2, keystorePassword.toCharArray());
                } catch (Exception e) {
                    LOGGER.error("Unable to load key store", e);
                }

                certificateExpiryEarlyWarning(trustStore, "trustStore");
                certificateExpiryEarlyWarning(keyStore, "keyStore");


                sslcontext = SSLContexts.custom()
                        .loadTrustMaterial(trustStore)
                        .loadKeyMaterial(keyStore, keystorePassword.toCharArray())
                        .build();
            }
            // Otherwise no need to load anything
            else {
                sslcontext = SSLContexts.custom().loadTrustMaterial(null).build();
            }

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
            PlainConnectionSocketFactory connectionSocketFactory = new PlainConnectionSocketFactory();

            // Create registry to be used by PoolingHttpClientConnectionManager
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("https", sslsf)
                    .register("http", connectionSocketFactory)
                    .build();

            PoolingHttpClientConnectionManager pooledConnection = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            pooledConnection = applyMaxConnectionsPerHost(pooledConnection);

            httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .addInterceptorFirst(new ContentLengthHeaderRemover())
                    .setConnectionManager(pooledConnection)
                    .build();

            LOGGER.info("Finished building Apache HttpComponent object for Spring webServiceTemplate");
        } catch (Exception e) {
            LOGGER.error("Error creating httpMessageSender: {}", e.getMessage(), e);
        }

        return httpclient;
    }

    /**
     * Log an early warning expiry exception. Paranoid checking for NPEs, this is a warning only.
     * http://dwgps0026/twiki/bin/view/NextGen/CertificateChecker
     *
     * @return false if the certificate has expired or will expire soon.
     */
    @SuppressWarnings("checkstyle:com.puppycrawl.tools.checkstyle.checks.coding.IllegalCatchCheck")
    // totally *do* want to swallow that exception
    private boolean certificateExpiryEarlyWarning(@Nullable final KeyStore ks, @Nonnull final String storeName) {
        boolean returnValue = true;
        try {
            Enumeration<String> aliases = ks.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Certificate certificate = ks.getCertificate(alias);
                if ("X.509".equalsIgnoreCase(certificate.getType())) {
                    X509Certificate x509Certificate = (X509Certificate) certificate;
                    Date notAfterDate = x509Certificate.getNotAfter();
                    String shortTitle = short509CertificateTitle(storeName, alias, x509Certificate);
                    LOGGER.info("checking expiry of " + shortTitle + x509Certificate);
                    Date now = new Date();
                    Calendar calendarMonthsAhead = Calendar.getInstance();
                    calendarMonthsAhead.add(Calendar.MONTH, CHECK_MONTHS_AHEAD);
                    if (notAfterDate.before(now)) {
                        LOGGER.warn(NOTICE_ME_LOG_STRING
                                + shortTitle
                                + "has expired before now, whatever bad things will happen ARE happening, not much point warning you.");
                    } else if (notAfterDate.before(calendarMonthsAhead.getTime())) {
                        LOGGER.warn(NOTICE_ME_LOG_STRING + shortTitle + "will expire within " + CHECK_MONTHS_AHEAD
                                + " months log a warning.");
                        returnValue = false;
                    }
                } else {
                    LOGGER.warn(NOTICE_ME_LOG_STRING + storeName + "--> <" + alias + "> don't know about this type="
                            + certificate.getType() + " of certificate we picked up: " + certificate);
                    returnValue = false;
                }
            }
        } catch (Exception e) {
            LOGGER.warn(NOTICE_ME_LOG_STRING + "failed certificate expiry checking", e);
            returnValue = false;
        }
        return returnValue;
    }

    private
    @Nonnull
    String short509CertificateTitle(@Nonnull final String storeName, @Nonnull final String alias,
                                    @Nonnull final X509Certificate x509Certificate) {
        Date notAfterDate = x509Certificate.getNotAfter();
        int days = Days.daysBetween(new DateTime(), new DateTime(notAfterDate)).getDays();
        return storeName + "--> <" + alias + "> X.509 certificate notAfterDate=" + x509Certificate.getNotAfter()
                + ", daysRemaining=" + days + " ";
    }

    @Bean(name = "esbHeaderInterceptor")
    public ClientInterceptor esbHeaderInterceptor() {
        return new EsbHeaderAdderInterceptor();
    }

    @Bean(name = "clientDetailsEsbHeaderInterceptor")
    public ClientInterceptor clientDetailsEsbHeaderInterceptor() {
        return new ClientDetailsEsbHeaderInterceptor();
    }

    @Bean(name = "applicationSubmissionEsbHeaderInterceptor")
    public ClientInterceptor applicationSubmissionEsbHeaderInterceptor() {
        return new ApplicationSubmissionEsbHeaderInterceptor();
    }

    @Bean(name = "channelEsbHeaderInterceptor")
    public ClientInterceptor channelEsbHeaderInterceptor() {
        return new ChannelEsbHeaderInterceptor();
    }

    @Bean(name = "serviceOpsEsbHeaderInterceptor")
    public ClientInterceptor serviceOpsEsbHeaderInterceptor() {
        return new ServiceOpsEsbHeaderInterceptor();
    }

    //TODO change to be loaded by reflection
    @Bean(name = "avaloqGatewayTokenRequest")
    public SecurityTokenRequest createAvaloqGatewayRequest() {
        SecurityTokenRequest request = new SecurityTokenRequest();
        request.setEndPointAlias(STR_ENDPOINT_PREFIX);
        return request;
    }

    @Bean(name = "loggingInterceptor")
    public ClientInterceptor loggingInterceptor() {
        return new ClientPayloadLoggingInterceptor();

    }

    private static class KeyStoreProperties {
        String keystoreLocation = null;
        String keystorePassword = null;

        public String getKeystoreLocation() {
            return keystoreLocation;
        }

        public void setKeystoreLocation(String keystoreLocation) {
            this.keystoreLocation = keystoreLocation;
        }

        public String getKeystorePassword() {
            return keystorePassword;
        }

        public void setKeystorePassword(String keystorePassword) {
            this.keystorePassword = keystorePassword;
        }
    }

    private KeyStoreProperties getKeyStoreProperties(CertificateStoreType type) {
        String keystoreLocation = null;
        String keystorePwd = null;

        if (Properties.getSafeBoolean(KEYSTORE_ENABLED)) {

            if (Properties.getSafeBoolean(DIRECT_SSL_CONFIG)) {
                keystoreLocation = Properties.get(KEYSTORE);
                keystorePwd = Properties.get(KEYSTORE_PASSWORD);
            } else {

                java.util.Properties sslProps = jsseHelper.getProperties(Properties.get(SSL_CONFIG_ALIAS));

                if (sslProps == null) {
                    LOGGER.error("No ssl properties available for this server, STS securement interceptor will not work");
                } else {
                    if (type == CertificateStoreType.KEYSTORE) {
                        keystoreLocation = sslProps.getProperty(KEY_SSL_PROP_KEYSTORE_LOCATION);
                        keystorePwd = sslProps.getProperty(KEY_SSL_PROP_KEYSTORE_PWD);
                    } else {
                        keystoreLocation = sslProps.getProperty(KEY_SSL_PROP_TRUSTSTORE_LOCATION);
                        keystorePwd = sslProps.getProperty(KEY_SSL_PROP_TRUSTSTORE_PWD);
                    }
                }
            }
        }

        KeyStoreProperties storeProps = new KeyStoreProperties();

        if (keystoreLocation != null)
            storeProps.setKeystoreLocation(keystoreLocation);
        if (keystorePwd != null)
            storeProps.setKeystorePassword(keystorePwd);

        return storeProps;
    }

    @Bean(name = "cryptoFactoryBean")
    public CryptoFactoryBean cryptoFactoryBean() throws Exception {

        CryptoFactoryBean cryptoFactoryBean = new CryptoFactoryBean();
        if (Properties.getSafeBoolean(KEYSTORE_ENABLED)) {
            String keystoreLocation = null;
            String keystorePwd = null;
            if (Properties.getSafeBoolean(DIRECT_SSL_CONFIG)) {
                keystoreLocation = Properties.get(KEYSTORE);
                keystorePwd = Properties.get(KEYSTORE_PASSWORD);
            } else {

                java.util.Properties sslProps = jsseHelper.getProperties(Properties.get(SSL_CONFIG_ALIAS));

                if (sslProps == null) {
                    LOGGER.error("No ssl properties available for this server, STS securement interceptor will not work");
                } else {
                    keystoreLocation = sslProps.getProperty(KEY_SSL_PROP_KEYSTORE_LOCATION);
                    keystorePwd = sslProps.getProperty(KEY_SSL_PROP_KEYSTORE_PWD);
                }
            }

            if (keystorePwd == null)
                LOGGER.error("Could not load password for keystore");
            if (keystoreLocation == null)
                LOGGER.error("Could not load location for keystore");

            cryptoFactoryBean.setKeyStoreLocation(new FileSystemResource(keystoreLocation));
            cryptoFactoryBean.setKeyStorePassword(keystorePwd);

        }
        return cryptoFactoryBean;
    }

    @Bean(name = "certificateInterceptor")
    public ClientInterceptor certificateSecurityInterceptor() throws Exception {
        if (Properties.getSafeBoolean(KEYSTORE_ENABLED)) {

            WSSConfig.setAddJceProviders(false);
            WSSConfig.addXMLDSigRIInternal();
            WSSConfig config = WSSConfig.getNewInstance();

            Wss4jSecurityInterceptor securityInterceptor = new Wss4jSecurityInterceptor();
            //securityInterceptor.setWssConfig();
			/*securityInterceptor.setSecurementActions("Timestamp Signature");
			securityInterceptor.setSecurementSignatureKeyIdentifier("DirectReference");
			securityInterceptor.setSecurementPassword(Properties.get(KEYSTORE_PASSWORD));
			securityInterceptor.setSecurementUsername(Properties.getString(CERTIFICATE_NAME));
			securityInterceptor.setSecurementSignatureCrypto(cryptoFactoryBean().getObject());*/

            String keystorePwd = null;
            if (Properties.getSafeBoolean(DIRECT_SSL_CONFIG)) {
                keystorePwd = Properties.get(KEYSTORE_PASSWORD);
            } else {
                java.util.Properties sslProps = jsseHelper.getProperties(Properties.get(SSL_CONFIG_ALIAS));

                if (sslProps == null) {
                    LOGGER.error("No ssl properties available for this server, STS securement interceptor will not work");
                } else {
                    keystorePwd = sslProps.getProperty(KEY_SSL_PROP_KEYSTORE_PWD);
                }
            }

            if (keystorePwd == null)
                LOGGER.error("Could not load password for keystore");

            securityInterceptor.setSecurementActions("Timestamp Signature");
            securityInterceptor.setSecurementSignatureKeyIdentifier("DirectReference");
            securityInterceptor.setSecurementPassword(keystorePwd);
            securityInterceptor.setSecurementUsername(Properties.getString(CERTIFICATE_NAME));
            securityInterceptor.setSecurementSignatureCrypto(cryptoFactoryBean().getObject());
            securityInterceptor.setWssConfig(config);

            return securityInterceptor;
        } else
            return new PassThroughInterceptor();

    }

    public ClientInterceptor createSoapActionInterceptor(String soapAction) {
        try {
            LOGGER.info("creating new soapAction interceptor with soapAction {}", soapAction);
            return new SoapActionInterceptor(soapAction);
        } catch (Exception err) {
            LOGGER.warn("Failed to create soapAction interceptor for {}", soapAction, err);
        }
        return new PassThroughInterceptor();
    }

    @Bean(name = "interceptor")
    public ClientInterceptor[] interceptor() {
        return new ClientInterceptor[]
                {
                        new ClientPayloadLoggingInterceptor()
                };
    }

    private static class ContentLengthHeaderRemover implements HttpRequestInterceptor {
        @Override
        public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
            request.removeHeaders(HTTP.CONTENT_LEN);// fighting org.apache.http.protocol.RequestContent's ProtocolException("Content-Length header already present");
        }
    }

    @Bean(name = "webServiceTemplateProvider")
    public WebServiceProvider webServiceTemplateProvider() throws Exception {
        //Set Defalut Responses
        MockWebServiceResponseFactory.setDefaultResponses();

        //UDDI - without this, cant create a webservice with uddi type
        final String uddi_endpoint = Properties.get(UDDI_ENDPOINT);
        if (isBlank(uddi_endpoint)) {
            throw new IllegalStateException("No UDDI defined, check for the value of " + UDDI_ENDPOINT);
        }

        final SpringWebServiceTemplateProvider factory = springWebServiceTemplateProvider(uddi_endpoint);
        final String[] templates = Properties.getSafeArray(WEBSERVICE_PROVIDER_SERVICES);
        if (templates.length == 0) {
            LOGGER.warn("No WebService defined - check the properties file");
            return factory;
        }

        final Map<String, String> validationUris = new HashMap<>();
        final Map<String, String> serviceToggles = new HashMap<>();
        for (String templateName : templates) {
            try {
                createTemplate(templateName, factory);
                final String schemaUri = Properties.get(templateName + SCHEMA_URI);
                final Boolean schemaValidate = Properties.getBoolean(templateName + SCHEMA_VALIDATE);
                if (schemaValidate && isNotBlank(schemaUri)) {
                    validationUris.put(templateName, schemaUri);
                }
                final String toggleName = Properties.get(templateName + WEBSERVICE_TOGGLE);
                if (isNotBlank(toggleName)) {
                    LOGGER.info("Webservice {} will be switched based on feature toggle: {}", templateName, toggleName);
                    serviceToggles.put(templateName, toggleName);
                }
            } catch (Exception err) {
                LOGGER.error("Failed to load end point {}", templateName, err);
            }
        }

        WebServiceProvider provider = factory;
        if (!validationUris.isEmpty()) {
            final JaxbSchemaValidationWebServiceProvider validation = new JaxbSchemaValidationWebServiceProvider(provider);
            for (Map.Entry<String, String> e : validationUris.entrySet()) {
                validation.addSchema(e.getKey(), e.getValue());
            }
            provider = validation;
        }
        if (!serviceToggles.isEmpty()) {
            checkToggledWebServices(serviceToggles.keySet(), factory);
            provider = new ToggledWebServiceProvider(provider, featureTogglesService, serviceToggles);
        }
        return provider;
    }

    private SpringWebServiceTemplateProvider springWebServiceTemplateProvider(final String uddiEndpoint) throws Exception {
        final SpringWebServiceTemplateProvider factory = new SpringWebServiceTemplateProvider();
        factory.setMarshaller(marshaller());
        factory.setUnmarshaller(unmarshaller());
        factory.setSender(httpMessageSender());
        factory.setMessageFactory(saajSOAP12());
        factory.setInterceptors(interceptor());
        factory.createUDDIResolver(uddiEndpoint, MockWebServiceResponseFactory.getDefaultResponses(SpringWebServiceTemplateProvider.SERVICE_UDDI));
        return factory;
    }

    private void createTemplate(final String templateName, final SpringWebServiceTemplateProvider factory) {

        //File stub
        boolean filestub = Properties.getSafeBoolean(templateName + WEBSERVICE_FILESTUB);
        String endpoint_type = Properties.get(templateName + ENDPOINT_TYPE);
        String endpoint_key = Properties.get(templateName + ENDPOINT_KEY);
        EndPointType endPointType = EndPointType.getEndPointType(endpoint_type);

        String[] interceptors = Properties.getSafeArray(templateName + WEBSERVICE_INTERCEPTORS);
        ArrayList<ClientInterceptor> interceptorBeanList = new ArrayList<>();
        for (String interceptor : interceptors) {
            LOGGER.info("({}) Adding interceptor {}", templateName, interceptor);
            interceptorBeanList.add((ClientInterceptor) ApplicationContextProvider.getApplicationContext()
                    .getBean(interceptor));
        }

        //Add the soapAction interceptor if one is required, this has to be the last action as it appears to make the final message immutable
        String soapAction = Properties.get(templateName + SOAP_ACTION);
        if (StringUtil.isNotNullorEmpty(soapAction)) {
            LOGGER.info("({}) Soap Action is {}", templateName, soapAction);
            interceptorBeanList.add(createSoapActionInterceptor(soapAction));
        } else {
            LOGGER.warn("({}) Soap Action not set - backend call may fail due to permission related problems.", templateName);
        }

        String marshaller = Properties.get(templateName + MARSHALLER_BEAN);
        Jaxb2Marshaller marshallerBean = (marshaller != null)
                ? (Jaxb2Marshaller) ApplicationContextProvider.getApplicationContext().getBean(marshaller)
                : null;

        String unmarshaller = Properties.get(templateName + UNMARSHALLER_BEAN);
        Jaxb2Marshaller unmarshallerBean = (unmarshaller != null)
                ? (Jaxb2Marshaller) ApplicationContextProvider.getApplicationContext().getBean(unmarshaller)
                : null;

        String soapVersion = Properties.get(templateName + SOAP_VERSION);
        SoapMessageFactory soapMessageFactoryBean = (soapVersion != null)
                ? (SoapMessageFactory) ApplicationContextProvider.getApplicationContext().getBean(soapVersion)
                : null;

        String httpCrentials = Properties.get(templateName + WEBSERVICE_HTTP_CREDENTIALS);
        WebServiceMessageSender webServiceMessageSender = (httpCrentials != null)
                ? (WebServiceMessageSender) ApplicationContextProvider.getApplicationContext().getBean(httpCrentials)
                : null;

        factory.createTemplate(templateName,
                endPointType,
                endpoint_key,
                filestub,
                soapMessageFactoryBean,
                webServiceMessageSender,
                marshallerBean,
                unmarshallerBean,
                interceptorBeanList.toArray(new ClientInterceptor[interceptorBeanList.size()]), MockWebServiceResponseFactory.getDefaultResponses(templateName));
    }

    /**
     * If any templates are marked as toggled, then ensure that a corresponding template with the "-old" suffix
     * is present in the template registry.
     *
     * @param templateNames collection of service names that are toggled.
     * @param factory       the webservice template container that should contain all the "-old" service definitions.
     */
    private void checkToggledWebServices(Collection<String> templateNames, SpringWebServiceTemplateProvider factory) {
        final WebServiceTemplate defaultService = factory.getDefaultWebServiceTemplate();
        for (String templateName : templateNames) {
            final WebServiceTemplate oldService = factory.getWebServiceTemplate(templateName + OLD_SUFFIX);
            if (oldService.equals(defaultService)) {
                throw new IllegalStateException("WebServiceTemplate " + templateName
                        + " is marked as toggled, but has no corresponding " + templateName + OLD_SUFFIX
                        + " template defined");
            }
        }
    }
}
