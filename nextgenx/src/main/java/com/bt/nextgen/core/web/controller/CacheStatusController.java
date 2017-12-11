package com.bt.nextgen.core.web.controller;

import com.bt.nextgen.core.cache.CacheType;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.core.jms.listener.ChunkListenerContainer;
import com.bt.nextgen.core.repository.RequestKey;
import com.bt.nextgen.core.repository.RequestRegister;
import com.bt.nextgen.core.repository.RequestRegisterRepository;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.web.interceptor.Spring3CorsFilter;
import com.bt.nextgen.service.avaloq.DataInitialization;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.asset.aal.AalEnumTemplate;
import com.bt.nextgen.service.avaloq.asset.aal.AalIndexJmsObjectHandler;
import com.bt.nextgen.service.avaloq.asset.aal.BrokerProductAssetJmsObjectHandler;
import com.bt.nextgen.service.avaloq.asset.aal.IndexAssetJmsObjectHandler;
import com.bt.nextgen.service.avaloq.broker.JmsBrokerObjectHandler;
import com.bt.nextgen.service.avaloq.code.StaticCodeJmsObjectHandler;
import com.bt.nextgen.service.avaloq.gateway.EventType;
import com.bt.nextgen.service.avaloq.product.AplJmsObjectHandler;
import com.bt.nextgen.service.integration.termdeposit.TermDepositRatesJmsObjectHandler;
import com.bt.nextgen.util.Environment;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.bt.nextgen.core.api.UriMappingConstants.*;

@Controller
public class CacheStatusController
{
	private static final Logger logger = LoggerFactory.getLogger(CacheStatusController.class);

    @Autowired
	private ChunkListenerContainer messageListenerContainer;

	@Autowired
	private RequestRegisterRepository requestRegisterRepository;

    @Autowired
    private ServletContext context;

	public static final Map<String, String> CACHE_NAME_MAP = new HashMap<String, String>() {{
		put(CacheType.STATIC_CODE_CACHE.getId(), Template.ASSET_DETAILS.getName());
		put(CacheType.ADVISER_PRODUCT_LIST_CACHE.getId(), Template.ADVISOR_PRODUCTS.getName());
		put(CacheType.JOB_USER_BROKER_CACHE.getId(), Template.BROKER_HIERARCHY.getName());
		put(CacheType.ASSET_DETAILS.getId(), Template.ASSET_DETAILS.getName());
        put(CacheType.TERM_DEPOSIT_ASSET_RATE_CACHE.getId(), Template.TD_ASSET_RATES.getName());
        put(CacheType.TERM_DEPOSIT_PRODUCT_RATE_CACHE.getId(), Template.TD_PRODUCT_RATES.getName());
        put(CacheType.AVAILABLE_ASSET_LIST_BROKER_CACHE.getId(), AalEnumTemplate.BROKER_PRODUCT_ASSETS.getTemplateName());
        put(CacheType.AVAILABLE_ASSET_LIST_INDEX_CACHE.getId(), AalEnumTemplate.AAL_INDEX.getTemplateName());
        put(CacheType.INDEX_ASSET_CACHE.getId(), AalEnumTemplate.INDEX_ASSET.getTemplateName());
    }};

    // Doing this because we want the String values
    private static final Set<String> EVENT_NAMES = new HashSet<String>() {{
        add(EventType.STARTUP.toString());
        add(EventType.MANUAL.toString());
        add(EventType.SCHEDULED.toString());
        add(EventType.CACHE_INVALIDATION.toString());
    }};

    protected static final String STATUS_NOT_STARTED = "NOT STARTED";

    protected static final String STATUS_LOADING = "LOADING";

    protected static final String STATUS_OK = "OK";

    protected static final String STATUS_TIMEOUT_ERROR = "TIMEOUT ERROR";


    /**
	 * Show a list of config key values to help indicate whether the caches are configured correctly
	 *
	 * @return String with CORS headers enabled showing String of related configuration values
	 */
	@Spring3CorsFilter
	@RequestMapping(value={"/public/cachestatus/config", NEXTGEN_MODULE_VERSION+"/cachestatus/config"},
			method = RequestMethod.GET, produces = "text/html")
	@ResponseBody
	public String showCacheConfigStatus() throws IOException
	{
        logger.info("/public/cachestatus/config called");
        if(Environment.isProduction()){
            throw new AccessDeniedException("You are not authorised");
        }

        String app = context.getContextPath();

        String result = "";

        result += "<h3>Queue Configuration</h3>";
        result += "<p style=\"font-style: italic;\">What are the 17 things I need to check to make sure my queue works?</p>";
        result += "<p><a href='"+app+NEXTGEN_MODULE_VERSION+"/dashboard/dashboard"+"'>Back to the Dashboard dashboard</a>";

        result += "<pre style=\"word-wrap: break-word; white-space: pre-wrap;\">";
		String JMS_PROVIDER="jms.provider";
		String jmsProvider = Properties.get(JMS_PROVIDER);
		result += "jms.provider: " + jmsProvider + "\n";

		String stubMode = Properties.get("avaloq.webservice.filestub");
		result += "avaloq.webservice.filestub: " + stubMode + "\n";

		result += "jms.enabled: " + Properties.get("jms.enabled") + "\n";

		result += "jms.sending.enabled: " + Properties.getSafeBoolean("jms.sending.enabled") + "\n";

		String loadAllCachesCalled = System.getProperties().getProperty("loadAllCachesCalled");
		String loadAllCachesCalledResult = (loadAllCachesCalled == null) ? "not using jdbc but jms" : loadAllCachesCalled;
		result += "DataInitialization.loadAllCaches() called: " + loadAllCachesCalledResult + "\n";

		String loadDataCaches = System.getProperties().getProperty("loadDataCaches");
		String loadDataCachesResult = (loadAllCachesCalled == null) ? "not using jdbc but jms" : loadDataCaches;
		result += "DataInitialization.loadDataCaches() called: " + loadDataCachesResult + "\n";

		result += "messageListenerContainer: " + messageListenerContainer  +"\n";

		if (messageListenerContainer != null) {
			result += "messageListenerContainer.isRunning(): " + messageListenerContainer.isRunning() + "\n";
		}

		result += "feature.disk.cache.serialization: " + Properties.get("disk.cache.serialization.enabled") +"\n";

		result += "cache.enabled.services: " + Properties.get("cache.enabled.services") + "\n";


		result += "jms.ssl.enabled: " + Properties.get("jms.ssl.enabled") +"\n";
		result += "jms.aq.topic: " + Properties.get("jms.aq.topic") +"\n";

		result += "jms.credentialLocator: " + Properties.get("jms.credentialLocator") +"\n";
		result += "jms.credentialName: " + Properties.get("jms.credentialName") +"\n";
		result += "jms.destination: " + Properties.get("jms.destination") +"\n";
		result += "jms.inv.destination: " + Properties.get("jms.inv.destination") +"\n";

		result += "jms.url: " + Properties.get("jms.url") +"\n";

		result += "\n\n";

		String stubModeStatic = Properties.get("avaloqStatic.webservice.filestub");
		result += "avaloqStatic.webservice.filestub: " + stubModeStatic + "\n";

		result += "jms.enabled.for.STATIC_CODES: " + Properties.get("jms.enabled.for.STATIC_CODES") + "\n";

		// From here on for testing for now
        result += "</pre>";

		return result;
	}

    /**
     * Show a list of register statuses to help indicate if the caches are loaded up or not
     *
     * @return String with CORS headers enabled showing String of related configuration values
     */
    @Spring3CorsFilter
    @RequestMapping(value={NEXTGEN_MODULE_VERSION+"/cachestatus/request"},
            method = RequestMethod.GET, produces = "text/html")
    @ResponseBody
    public String showCacheRegisterStatus() throws IOException {
        logger.info("/public/cachestatus/request called");
        if (Environment.isProduction()) {
            throw new AccessDeniedException("You are not authorised");
        }

        String app = context.getContextPath();

        String result = "";

        result += "<h3>Cache Load Durations (EPS)</h3>";
        result += "<p style=\"font-style: italic;\">How long has my cache taken to load?</p>";
        result += "<p><a href='"+app+NEXTGEN_MODULE_VERSION+"/dashboard/dashboard"+"'>Back to the Dashboard dashboard</a>";

        result += "<pre style=\"word-wrap: break-word; white-space: pre-wrap;\">";

        result += getCacheStartedInfo(CacheType.ADVISER_PRODUCT_LIST_CACHE.getId());
        result += getCacheStartedInfo(CacheType.AVAILABLE_ASSET_LIST_CACHE.getId()); //deprecated
        result += getCacheStartedInfo(CacheType.JOB_USER_BROKER_CACHE.getId());
        result += getCacheStartedInfo(CacheType.STATIC_CODE_CACHE.getId());
        result += getCacheStartedInfo(CacheType.TERM_DEPOSIT_RATES_CACHE.getId());
        result += getCacheStartedInfo(CacheType.AVAILABLE_ASSET_LIST_CACHE.getId());

        result += getCacheStartedInfo(CacheType.AVAILABLE_ASSET_LIST_BROKER_CACHE.getId());
        result += getCacheStartedInfo(CacheType.AVAILABLE_ASSET_LIST_ISSUER_CACHE.getId());
        result += getCacheStartedInfo(CacheType.AVAILABLE_ASSET_LIST_INDEX_CACHE.getId());
        result += getCacheStartedInfo(CacheType.INDEX_ASSET_CACHE.getId());

        result += "</pre>";

        return result;

    }

    @RequestMapping(value = "/public/cachestatus/request", method = RequestMethod.GET)
    public ModelAndView showCacheStatusRedirect(){
        return new ModelAndView("redirect:" + NEXTGEN_MODULE_VERSION + "/cachestatus/request");
    }


    /**
     * Show a list of property statuses to help indicate if the caches are loaded up or not
     * This one is for native queues - where is appears the Oracle native queue readers on
     * nextgen are not working properly - and the register is getting screwed up
     *
     * @return String with CORS headers enabled showing String of related configuration values
     */
    @Spring3CorsFilter
    @RequestMapping(value={"/public/cachestatus/properties",NEXTGEN_MODULE_VERSION+"/cachestatus/properties"},
            method = RequestMethod.GET, produces = "text/html")
    @ResponseBody
    public String showCachePropertyStatus() throws IOException {
        logger.info("/public/cachestatus/properties called");
        if (Environment.isProduction()) {
            throw new AccessDeniedException("You are not authorised");
        }

        String app = context.getContextPath();

        String result = "";

        result += "<h3>Cache Load Durations (Local)</h3>";
        result += "<p style=\"font-style: italic;\">How long has my cache taken to load?</p>";
        result += "<p><a href='"+app+NEXTGEN_MODULE_VERSION+"/dashboard/dashboard"+"'>Back to the Dashboard dashboard</a>";

        result += "<pre style=\"word-wrap: break-word; white-space: pre-wrap;\">";

        result += "\n\n";

        String StaticRequestCalled = System.getProperties().getProperty(DataInitialization.STATIC_CODE_REQUEST_CALLED);
        result += "Static Request Called: " + StaticRequestCalled + "\n";

        Object StaticRequestTime = System.getProperties().get(DataInitialization.STATIC_CODE_REQUEST_TIME);
        result += "Static Request Time: " + StaticRequestTime + "\n";

        //requestid here

        String staticHandleCalled = System.getProperties().getProperty(StaticCodeJmsObjectHandler.STATIC_CODE_HANDLE_CALLED);
        result += "Static Handle Called: " + staticHandleCalled + "\n";

        String staticRequestId = System.getProperties().getProperty(StaticCodeJmsObjectHandler.STATIC_CODE_HANDLE_REQUESTID);
        result += "Static Request ID: " + staticRequestId + "\n";

        Object staticResponseTime = System.getProperties().get(StaticCodeJmsObjectHandler.STATIC_CODE_HANDLE_RESPONSE_TIME);
        result += "Static Response Time: " + staticResponseTime + "\n";

        result += "\n\n";

        String aplRequestCalled = System.getProperties().getProperty(DataInitialization.ADVISER_PRODUCT_LIST_REQUEST_CALLED);
        result += "APL Request Called: " + aplRequestCalled + "\n";

        Object aplRequestTime = System.getProperties().get(DataInitialization.ADVISER_PRODUCT_LIST_REQUEST_TIME);
        result += "APL Request Time: " + aplRequestTime + "\n";

        //String APLRequestIdOrig = System.getProperties().getProperty("APLHandleRequestIDOrig");
        //result += "APL Request ID Orig: " + APLRequestIdOrig + "\n";

        String aplHandleCalled = System.getProperties().getProperty(AplJmsObjectHandler.ADVISER_PRODUCT_LIST_HANDLE_CALLED);
        result += "APL Handle Called: " + aplHandleCalled + "\n";

        String aplRequestId = System.getProperties().getProperty(AplJmsObjectHandler.ADVISER_PRODUCT_LIST_REQUESTID);
        result += "APL Request ID: " + aplRequestId + "\n";

        Object aplResponseTime = System.getProperties().get(AplJmsObjectHandler.ADVISER_PRODUCT_LIST_HANDLE_RESPONSE_TIME);
        result += "APL Response Time: " + aplResponseTime + "\n";

        result += "\n\n";

        String brokerRequestCalled = System.getProperties().getProperty(DataInitialization.BROKER_REQUEST_CALLED);
        result += "Broker Request Called: " + brokerRequestCalled + "\n";

        Object brokerRequestTime = System.getProperties().get(DataInitialization.BROKER_REQUEST_TIME);
        result += "Broker Request Time: " + brokerRequestTime + "\n";

        //String BrokerRequestIdOrig = System.getProperties().getProperty("BrokerHandleRequestIDOrig");
        //result += "Broker Request ID Orig: " + BrokerRequestIdOrig + "\n";

        // Can't do these until Broker Hierarchy Impl lib migration is done
        String brokerHandleCalled = System.getProperties().getProperty(JmsBrokerObjectHandler.BROKER_HANDLE_CALLED);
        result += "Broker Handle Called: " + brokerHandleCalled + "\n";

        String brokerRequestId = System.getProperties().getProperty(JmsBrokerObjectHandler.BROKER_HANDLE_REQUESTID);
        result += "Broker Request ID: " + brokerRequestId + "\n";

        Object brokerResponseTime = System.getProperties().get(JmsBrokerObjectHandler.BROKER_HANDLE_RESPONSE_TIME);
        result += "Broker Response Time: " + brokerResponseTime + "\n";

        result += "\n\n";
/*
        String AALRequestCalled = System.getProperties().getProperty("AALRequestCalled");
        result += "AAL Request Called: " + AALRequestCalled + "\n";

        Object AALRequestTime = System.getProperties().get("AALRequestTime");
        result += "AAL Request Time: " + AALRequestTime + "\n";

        //String AALRequestIdOrig = System.getProperties().getProperty("AALHandleRequestIDOrig");
        //result += "AAL Request ID Orig: " + AALRequestIdOrig + "\n";

        String AALHandleCalled = System.getProperties().getProperty("AALHandleCalled");
        result += "AAL Handle Called: " + AALHandleCalled + "\n";

        String AALRequestId = System.getProperties().getProperty("AALHandleRequestID");
        result += "AAL Request ID: " + AALRequestId + "\n";

        Object AALResponseTime = System.getProperties().get("AALHandleResponseTime");
        result += "AAL Response Time: " + AALResponseTime + "\n";

        result += "\n\n";
        */
        String aaBrokerRequestCalled = System.getProperties()
                .getProperty(DataInitialization.AVAILABLE_ASSET_BROKER_REQUEST_CALLED);
        result += "AABroker Request Called: " + aaBrokerRequestCalled + "\n";

        Object aaBrokerRequestTime = System.getProperties().get(DataInitialization.AVAILABLE_ASSET_BROKER_REQUEST_TIME);
        result += "AABroker Request Time: " + aaBrokerRequestTime + "\n";

        //String AABrokerRequestIdOrig = System.getProperties().getProperty("AABrokerHandleRequestIDOrig");
        //result += "AABroker Request ID Orig: " + AABrokerRequestIdOrig + "\n";

        String aaBrokerHandleCalled = System.getProperties()
                .getProperty(BrokerProductAssetJmsObjectHandler.AVAILABLE_ASSET_BROKER_HANDLE_CALLED);
        result += "AABroker Handle Called: " + aaBrokerHandleCalled + "\n";

        String aaBrokerRequestId = System.getProperties()
                .getProperty(BrokerProductAssetJmsObjectHandler.AVAILABLE_ASSET_BROKER_HANDLE_REQUESTID);
        result += "AABroker Request ID: " + aaBrokerRequestId + "\n";

        Object aaBrokerResponseTime = System.getProperties()
                .get(BrokerProductAssetJmsObjectHandler.AVAILABLE_ASSET_BROKER_HANDLE_RESPONSE_TIME);
        result += "AABroker Response Time: " + aaBrokerResponseTime + "\n";

        result += "\n\n";

        String aaIssuerRequestCalled = System.getProperties()
                .getProperty(DataInitialization.AVAILABLE_ASSET_ISSUER_REQUEST_CALLED);
        result += "AAIssuer Request Called: " + aaIssuerRequestCalled + "\n";

        Object aaIssuerRequestTime = System.getProperties().get(DataInitialization.AVAILABLE_ASSET_ISSUER_REQUEST_TIME);
        result += "AAIssuer Request Time: " + aaIssuerRequestTime + "\n";

        //String AAIssuerRequestIdOrig = System.getProperties().getProperty("AAIssuerHandleRequestIDOrig");
        //result += "AAIssuer Request ID Orig: " + AAIssuerRequestIdOrig + "\n";

        result += "\n\n";

        String aaIndexRequestCalled = System.getProperties().getProperty(DataInitialization.AVAILABLE_ASSET_INDEX_REQUEST_CALLED);
        result += "AAIndex Request Called: " + aaIndexRequestCalled + "\n";

        Object aaIndexRequestTime = System.getProperties().get(DataInitialization.AVAILABLE_ASSET_INDEX_REQUEST_TIME);
        result += "AAIndex Request Time: " + aaIndexRequestTime + "\n";

        //String AAIssuerRequestIdOrig = System.getProperties().getProperty("AAIssuerHandleRequestIDOrig");
        //result += "AAIssuer Request ID Orig: " + AAIssuerRequestIdOrig + "\n";

        String aaIndexHandleCalled = System.getProperties()
                .getProperty(AalIndexJmsObjectHandler.AVAILABLE_ASSET_LIST_INDEX_HANDLE_CALLED);
        result += "AAIndex Handle Called: " + aaIndexHandleCalled + "\n";

        String aaIndexRequestId = System.getProperties()
                .getProperty(AalIndexJmsObjectHandler.AVAILABLE_ASSET_LIST_INDEX_REQUESTID);
        result += "AAIndex Request ID: " + aaIndexRequestId + "\n";

        Object aaIndexResponseTime = System.getProperties()
                .get(AalIndexJmsObjectHandler.AVAILABLE_ASSET_LIST_INDEX_HANDLE_RESPONSE_TIME);
        result += "AAIndex Response Time: " + aaIndexResponseTime + "\n";

        result += "\n\n";

        String indexAssetRequestCalled = System.getProperties().getProperty(DataInitialization.INDEX_ASSET_REQUEST_CALLED);
        result += "IndexAsset Request Called: " + indexAssetRequestCalled + "\n";

        Object indexAssetRequestTime = System.getProperties().get(DataInitialization.INDEX_ASSET_REQUEST_TIME);
        result += "IndexAsset Request Time: " + indexAssetRequestTime + "\n";

        //String IndexAssetRequestIdOrig = System.getProperties().getProperty("IndexAssetHandleRequestIDOrig");
        //result += "IndexAsset Request ID Orig: " + IndexAssetRequestIdOrig + "\n";

        String indexAssetHandleCalled = System.getProperties().getProperty(IndexAssetJmsObjectHandler.INDEX_ASSET_HANDLE_CALLED);
        result += "IndexAsset Handle Called: " + indexAssetHandleCalled + "\n";

        String indexAssetRequestId = System.getProperties().getProperty(IndexAssetJmsObjectHandler.INDEX_ASSET_HANDLE_REQUESTID);
        result += "IndexAsset Request ID: " + indexAssetRequestId + "\n";

        Object indexAssetResponseTime = System.getProperties().get(IndexAssetJmsObjectHandler.INDEX_ASSET_HANDLE_RESPONSE_TIME);
        result += "IndexAsset Response Time: " + indexAssetResponseTime + "\n";

        result += "\n\n";

        String tdAssetRequestCalled = System.getProperties().getProperty(DataInitialization.TERM_DEPOSIT_ASSET_REQUEST_CALLED);
        result += "TD Product Request Called: " + tdAssetRequestCalled + "\n";

        Object tdAssetRequestTime = System.getProperties().get(DataInitialization.TERM_DEPOSIT_ASSET_REQUEST_TIME);
        result += "TD Product Request Time: " + tdAssetRequestTime + "\n";

        String tdProductRequestCalled = System.getProperties().getProperty(DataInitialization.TERM_DEPOSIT_PRODUCT_REQUEST_CALLED);
        result += "TD Product Request Called: " + tdProductRequestCalled + "\n";

        Object tdProductRequestTime = System.getProperties().get(DataInitialization.TERM_DEPOSIT_PRODUCT_REQUEST_TIME);
        result += "TD Product Request Time: " + tdProductRequestTime + "\n";

        String tdHandleCalled = System.getProperties()
                .getProperty(TermDepositRatesJmsObjectHandler.TERM_DEPOSIT_RATES_HANDLE_CALLED);
        result += "TD Handle Called: " + tdHandleCalled + "\n";

        String tdRequestId = System.getProperties()
                .getProperty(TermDepositRatesJmsObjectHandler.TERM_DEPOSIT_RATES_HANDLE_REQUESTID);
        result += "TD Request ID: " + tdRequestId + "\n";

        Object tdResponseTime = System.getProperties()
                .get(TermDepositRatesJmsObjectHandler.TERM_DEPOSIT_RATES_HANDLE_RESPONSE_TIME);
        result += "TD Response Time: " + tdResponseTime + "\n";

        result += "\n\n";

        result += "</pre>";

        return result;
    }

    /**
     * For a given cache name - return startup start and finish and other times as a String for a report
     * @param cacheName
     * @return
     */
    private String getCacheStartedInfo(String cacheName) {
		String result = "";

		result += "\n\n";

		String mappedCacheName = CACHE_NAME_MAP.get(cacheName);

		RequestRegister requestRegisterStarting = requestRegisterRepository.findRequestEntry(
				new RequestKey(mappedCacheName,
						EventType.STARTUP.toString()));

		result += cacheName   + ":\n";

		result += "requestRegisterStarting: " + requestRegisterStarting + "\n";

		if(requestRegisterStarting != null ) {
			result += "STARTUP.requestRegisterStarting.getSentTime(): " + requestRegisterStarting.getSentTime() + "\n";

			result += "STARTUP.requestRegisterStarting.getReceivedTime(): " + requestRegisterStarting.getReceivedTime() + "\n";
		}

		return result;
	}

    /**
     * For a given cache and event type, if started loading, show the duration of the load
     * http://localhost:9080/ng/public/cacheloadtime/AvailableAsset/STARTUP
     * Note - this is designed to run in production as per requirements
     * @param cacheName
     * @param eventName
     * @param response
     * @throws IOException
     */
    @RequestMapping(value="/public/cacheloadtime/{cacheName}/{eventName}",
                    method = RequestMethod.GET, produces = "text/plain")
    public void showCacheRegisterLoadTime(@PathVariable("cacheName") String cacheName,
                                      @PathVariable("eventName") String eventName,
                                      HttpServletResponse response) throws IOException {
        String result = "";

        //Check the cacheName
        boolean validCacheName = CACHE_NAME_MAP.keySet().contains(cacheName);
        if (!validCacheName) {
            result += "Invalid cache name\n\n";
        }
        boolean validEventName = EVENT_NAMES.contains(eventName);
        if (!EVENT_NAMES.contains(eventName)) {
            result += "Invalid event name\n\n";
        }

        String mappedCacheName = CACHE_NAME_MAP.get(cacheName);

        RequestRegister requestRegisterEntry = requestRegisterRepository.findRequestEntry(
                new RequestKey(mappedCacheName, eventName));

        DateTime jvmStartTime = new DateTime(ManagementFactory.getRuntimeMXBean().getStartTime());
        boolean validRequestEntry = (requestRegisterEntry != null);
        //Handle the case of old STARTUP entries persisted from the previous JVM instance hanging around.
        if (validRequestEntry) {
            DateTime requestSentTime =  new DateTime(requestRegisterEntry.getSentTime());
            if (requestSentTime.isBefore(jvmStartTime)) {
                validRequestEntry = false;
            }
        }
        if (!validRequestEntry) {
            result += "Not started - no entry\n\n";
        }

        if (validCacheName && validEventName && validRequestEntry) {
            DateTime now = new DateTime();
            Date startTime = requestRegisterEntry.getSentTime();
            Date finishTime = requestRegisterEntry.getReceivedTime();
            boolean validStartTime = (startTime != null);
            boolean validFinishTime = (finishTime != null);
            if (!validStartTime) {
                result += "Not started - has entry\n\n";
            }
            DateTime cacheStartTime;
            DateTime cacheFinishTime;
            if (validStartTime) {
                cacheStartTime = new DateTime(startTime);
                if (validFinishTime) {
                    cacheFinishTime = new DateTime(finishTime);
                    result += getDifferenceReport(cacheStartTime, cacheFinishTime);
                } else {
                    result += getDifferenceReport(cacheStartTime, now);
                }
            }
        }

        //Handle status methods calling this with a null value
        if (response != null) {
            // Don't use the @Spring3CorsFilter  here because we want to use this in production - until we get a better pattern
            setCorsHeaders(response);
            response.getWriter().write(result);
        }
    }

    /**
     * For a given cache and event type, if started loading, show the status of the cache
     * http://localhost:9080/ng/public/cachestatus/AvailableAsset/STARTUP
     * Note - this is designed to run in production as per requirements
     * @param cacheName
     * @param eventName
     * @param response
     * @throws IOException
     */
    @RequestMapping(value="/public/cachestatus/{cacheName}/{eventName}",
            method = RequestMethod.GET, produces = "text/plain")
    public void showCacheStatus(@PathVariable("cacheName") String cacheName,
                                          @PathVariable("eventName") String eventName,
                                          HttpServletResponse response) throws IOException {
        String result = showCacheStatusResult(cacheName, eventName);

        //Handle status methods calling this with a null value
        if (response != null) {
            // Don't use the @Spring3CorsFilter  here because we want to use this in production - until we get a better pattern
            setCorsHeaders(response);
            response.getWriter().write(result);
        }
    }

    /**
     * Partition this method from the parent response handler for testing and reuse
     * @param cacheName
     * @param eventName
     * @return
     */
    public String showCacheStatusResult(String cacheName, String eventName) {
        String result = "";

        //Check the cacheName
        boolean validCacheName = CACHE_NAME_MAP.keySet().contains(cacheName);
        if (!validCacheName) {
            result += "Invalid cache name\n\n";
        }
        boolean validEventName = EVENT_NAMES.contains(eventName);
        if (!EVENT_NAMES.contains(eventName)) {
            result += "Invalid event name\n\n";
        }

        String mappedCacheName = CACHE_NAME_MAP.get(cacheName);

        RequestRegister requestRegisterEntry = requestRegisterRepository.findRequestEntry(
                new RequestKey(mappedCacheName, eventName));

        DateTime jvmStartTime = new DateTime(ManagementFactory.getRuntimeMXBean().getStartTime());
        boolean validRequestEntry = (requestRegisterEntry != null);
        //Handle the case of old STARTUP entries persisted from the previous JVM instance hanging around.
        if (validRequestEntry) {
            DateTime requestSentTime =  new DateTime(requestRegisterEntry.getSentTime());
            if (requestSentTime.isBefore(jvmStartTime)) {
                validRequestEntry = false;
            }
        }

        if (!validRequestEntry) {
            result += STATUS_NOT_STARTED;
        }

        if (validCacheName && validEventName && validRequestEntry) {
            DateTime now = new DateTime();
            Date startTime = requestRegisterEntry.getSentTime();
            Date finishTime = requestRegisterEntry.getReceivedTime();
            boolean validStartTime = (startTime != null);
            boolean validFinishTime = (finishTime != null);
            if (!validStartTime) {
                result += STATUS_NOT_STARTED;
            }
            DateTime cacheStartTime;

            if (validStartTime) {
                cacheStartTime = new DateTime(startTime);
                if (validFinishTime) {
                    result += STATUS_OK;
                } else {
                    Duration duration = new Duration(cacheStartTime, now);
                    long durationMinutes = duration.getStandardMinutes();
                    long expectedMinutes = Properties.getInteger("cache.loadtime."+cacheName);
                    if (durationMinutes < expectedMinutes) {
                        result += STATUS_LOADING;
                    } else {
                        result += STATUS_TIMEOUT_ERROR;
                    }
                }
            }
        }

        return  result;
    }

    private String getDifferenceReport(DateTime cacheStartTime, DateTime cacheFinishTime) {
        String result = "";
        Duration duration = new Duration(cacheStartTime, cacheFinishTime);
        result += duration.getStandardMinutes() + " minutes\n";
        return result;
    }

    private void setCorsHeaders(HttpServletResponse response) {
        if (!Environment.isProduction()) {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        }
    }

    // For testing
    public void setMessageListenerContainer(ChunkListenerContainer messageListenerContainer) {
        this.messageListenerContainer = messageListenerContainer;
    }

}