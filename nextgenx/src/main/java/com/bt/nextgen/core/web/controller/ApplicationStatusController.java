package com.bt.nextgen.core.web.controller;

import com.bt.nextgen.core.IServiceStatus;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.cache.CacheType;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.core.repository.RequestKey;
import com.bt.nextgen.core.repository.RequestRegister;
import com.bt.nextgen.core.repository.RequestRegisterRepository;
import com.bt.nextgen.core.web.interceptor.Spring3CorsFilter;
import com.bt.nextgen.core.web.model.ApplicationServiceStatusDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.asset.aal.AalEnumTemplate;
import com.bt.nextgen.service.avaloq.gateway.EventType;
import com.bt.nextgen.service.integration.bgp.BackGroundProcess;
import com.bt.nextgen.service.integration.bgp.BackGroundProcessIntegrationService;
import com.bt.nextgen.util.Environment;
import com.btfin.panorama.service.client.status.ServiceStatus;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bt.nextgen.core.api.UriMappingConstants.CURRENT_VERSION_API;
import static com.bt.nextgen.core.api.UriMappingConstants.NEXTGEN_MODULE_VERSION;
import static com.bt.nextgen.core.api.UriMappingConstants.NEXTGEN_WEB;

@Controller
class ApplicationStatusController {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationStatusController.class);

    private static final String STATUS_OK = "OK";

    private static final String STATUS_ERROR = "NOT OK";

    private static final String STATUS_WARNING = "WARNING";
    private static final String STARTED = "Started";
    private static final String STARTING = "Starting";
    private static final String SERVICE_UNAVAILABLE = "Service Unavailable";

    @Autowired
    private ServletContext context;

    @Autowired
    private RequestRegisterRepository requestRegisterRepository;

    @Autowired
    private IServiceStatus serviceStatus;

    @Autowired
    private BackGroundProcessIntegrationService backGroundProcessIntegrationService;

    private Map<String, String> cacheNameMap = new HashMap<String, String>() {
        {
            put(CacheType.STATIC_CODE_CACHE.getId(), Template.ASSET_DETAILS.getName());
            put(CacheType.AVAILABLE_ASSET_LIST_BROKER_CACHE.getId(),
                    AalEnumTemplate.BROKER_PRODUCT_ASSETS.getTemplateName());
            put(CacheType.AVAILABLE_ASSET_LIST_INDEX_CACHE.getId(), AalEnumTemplate.AAL_INDEX.getTemplateName());
            put(CacheType.INDEX_ASSET_CACHE.getId(), AalEnumTemplate.INDEX_ASSET.getTemplateName());
            put(CacheType.ADVISER_PRODUCT_LIST_CACHE.getId(), Template.ADVISOR_PRODUCTS.getName());
            put(CacheType.JOB_USER_BROKER_CACHE.getId(), Template.BROKER_HIERARCHY.getName());
            put(CacheType.ASSET_DETAILS.getId(), Template.ASSET_DETAILS.getName());
            put(CacheType.TERM_DEPOSIT_ASSET_RATE_CACHE.getId(), Template.TD_ASSET_RATES.getName());
            put(CacheType.TERM_DEPOSIT_PRODUCT_RATE_CACHE.getId(), Template.TD_PRODUCT_RATES.getName());
        }
    };

    /**
     * Return if the application is ready for login in a format suitable for a dashboard.
     *
     * @return String of simple response
     */
    @Spring3CorsFilter
    @RequestMapping(value = "/public/applicationstatus/login", method = RequestMethod.GET, produces = "text/plain")
    @ResponseBody
    String showLoginStatus() throws IOException {
        logger.info("/public/applicationstatus/login/ called");
        if (Environment.isProduction()) {
            throw new AccessDeniedException("You are not authorised");
        }
        boolean cacheStatus = areCachesLoaded();
        String result;
        if (cacheStatus) {
            result = STATUS_OK;
        } else {
            result = STATUS_ERROR;
        }
        return result;
    }

    /**
     * For each of the caches that the login cares about - see if they are loaded, and return true
     * only if they all are.
     *
     * @return boolean if loaded
     */
    private boolean areCachesLoaded() {
        boolean isAdviserProductLoaded = checkCacheLoaded(CacheType.ADVISER_PRODUCT_LIST_CACHE.getId());
        boolean isAvailableAssetBrokerCacheLoaded = checkCacheLoaded(
                CacheType.AVAILABLE_ASSET_LIST_BROKER_CACHE.getId());
        boolean isJobUserBrokerLoaded = checkCacheLoaded(CacheType.JOB_USER_BROKER_CACHE.getId());
        boolean isStaticCodeLoaded = checkCacheLoaded(CacheType.STATIC_CODE_CACHE.getId());
        boolean isTermDepositAssetRatesLoaded = checkCacheLoaded(CacheType.TERM_DEPOSIT_ASSET_RATE_CACHE.getId());
        boolean isTermDepositProductRatesLoaded = checkCacheLoaded(CacheType.TERM_DEPOSIT_PRODUCT_RATE_CACHE.getId());
        return isAdviserProductLoaded && isAvailableAssetBrokerCacheLoaded && isJobUserBrokerLoaded &&
                isStaticCodeLoaded && isTermDepositAssetRatesLoaded && isTermDepositProductRatesLoaded;
    }

    /**
     * For a given cache name - a boolean true if the cache has finished loading
     *
     * @param cacheName Particular cache to load
     * @return boolean showing whether the cache is loaded
     */
    private boolean checkCacheLoaded(String cacheName) {
        boolean result = false;
        String mappedCacheName = cacheNameMap.get(cacheName);
        RequestRegister requestRegisterStarting = requestRegisterRepository
                .findRequestEntry(new RequestKey(mappedCacheName, EventType.STARTUP.toString()));
        DateTime jvmStartTime = new DateTime(ManagementFactory.getRuntimeMXBean().getStartTime());
        if (requestRegisterStarting != null) {
            if (requestRegisterStarting.getReceivedTime() != null) {
                DateTime receivedTime = new DateTime(requestRegisterStarting.getReceivedTime());
                if (receivedTime.isAfter(jvmStartTime)) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Return a list of links to the various dashboards for diagnostics.
     *
     * @return String of simple response
     */
    @Spring3CorsFilter
    @RequestMapping(value = {NEXTGEN_WEB + "dashboard/dashboard"}, method = RequestMethod.GET, produces = "text/html")
    @ResponseBody
    String showDashboardDashboard() throws IOException {
        logger.info(NEXTGEN_WEB + "dashboard/dashboard called");
        if (Environment.isProduction()) {
            throw new AccessDeniedException("You are not authorised");
        }
        String app = context.getContextPath();
        String result = "";
        result += "<h3>This is the Dashboard Dashboard</h3>";
        result += "<p>Here you will find a series of links to the other dashboards for diagnostic purposes.</p>";
        result += "<p><a href='" + app + NEXTGEN_MODULE_VERSION + "/cachestatus/config" + "'>Queue Configuration</a>"
                + " - <span style=\"font-style: italic;\">" + "What are the 17 things I need to check to make sure my" +
                " queue works?</span></p>";
        result += "<p><a href='" + app + NEXTGEN_MODULE_VERSION + "/cachestatus/request" + "'>Cache Load Durations " +
                "(EPS)</a>" + " - <span style=\"font-style: italic;\">" + "How long has my cache taken to " +
                "load?</span></p>";
        result += "<p><a href='" + app + NEXTGEN_MODULE_VERSION + "/cachestatus/properties" + "'>Cache Load Durations" +
                " (Local)</a>" + " - <span style=\"font-style: italic;\">" + "How long has my cache taken to " +
                "load?</span></p>";
        result += "<p><a href='" + app + NEXTGEN_MODULE_VERSION + "/properties/settings" + "'>Panorama " +
                "Properties</a>" + " - <span style=\"font-style: italic;\">" + "How did the values from my properties" +
                " file(s) get loaded up at runtime?</span></p>";
        result += "<p><a href='" + app + NEXTGEN_MODULE_VERSION + "/environment/variables" + "'>Operating System " +
                "Environment Variables</a>" + " - <span style=\"font-style: italic;\">" + "What are the memory " +
                "settings and what other values exist in the OS Environment context?</span></p>";
        result += "<p><a href='" + app + NEXTGEN_MODULE_VERSION + "/properties/environment" + "'>Java " +
                "Properties</span></a>" + " - <span style=\"font-style: italic;\">" + "What are the values in System" +
                ".util.Properties? (not in the Panorama Properties object)</span></p>";
        result += "<p><a href='" + app + NEXTGEN_MODULE_VERSION + "/ramusage/report" + "'>RAM Usage</a>" + " - <span " +
                "style=\"font-style: italic;\">" + "How much RAM {did we set?; is being used?; is committed?; is " +
                "max?; is in permgen?} </span></p>";
        result += "<p><a href='" + app + NEXTGEN_MODULE_VERSION + "/bgpstatus/report" + "'>BGP Status</a>" + " - " +
                "<span style=\"font-style: italic;\">" + "What are all the background processes running in avaloq? " +
                "Are they running? </span></p>";
        result += "<p><a href='" + app + NEXTGEN_WEB + "releasenotes" + "'>Release notes - Panorama Java " +
                "Application</a>" + " - <span style=\"font-style: italic;\">" + "Has my change gotten into this " +
                "kit?</span></p>";
        return result;
    }

    @RequestMapping(value = "/public/dashboard", method = RequestMethod.GET)
    ModelAndView showDashBoardOldRedirect() {
        return new ModelAndView("redirect:" + NEXTGEN_WEB + "dashboard/dashboard?reveal");
    }

    @RequestMapping(value = NEXTGEN_MODULE_VERSION + "/dashboard/dashboard", method = RequestMethod.GET)
    ModelAndView showDashboardNewerRedirect() {
        return new ModelAndView("redirect:" + NEXTGEN_WEB + "dashboard/dashboard?reveal");
    }

    /**
     * Return if the permGen is sufficient to allow login or will cause errors responding to requests
     *
     * @return String of simple response
     */
    @Spring3CorsFilter
    @RequestMapping(value = {NEXTGEN_MODULE_VERSION + "/ramusage/permgenstatus"}, method = RequestMethod.GET,
            produces = "text/plain")
    @ResponseBody
    String showPermGenStatus() throws IOException {
        logger.info(NEXTGEN_MODULE_VERSION + "/ramusage/permgenstatus");
        if (Environment.isProduction()) {
            throw new AccessDeniedException("You are not authorised");
        }
        MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
        long permgenUsage = mem.getNonHeapMemoryUsage().getUsed();
        long permgenMax = mem.getNonHeapMemoryUsage().getMax();
        long usagePercent = permgenUsage / permgenMax;
        String result;
        if (usagePercent > 0.9) {
            result = STATUS_ERROR;
        } else if (usagePercent > 0.8) {
            result = STATUS_WARNING;
        } else {
            result = STATUS_OK;
        }
        return result;
    }

    /**
     * Return a live RAM usage report
     *
     * @return String showing report
     */
    @Spring3CorsFilter
    @RequestMapping(value = {NEXTGEN_MODULE_VERSION + "/ramusage/report"}, method = RequestMethod.GET, produces =
            "text/html")
    @ResponseBody
    String getPermGenReport() {
        logger.info(NEXTGEN_MODULE_VERSION + "/ramusage/report");
        if (Environment.isProduction()) {
            throw new AccessDeniedException("You are not authorised");
        }
        String app = context.getContextPath();
        String result = "";
        result += "<h3>RAM Usage</h3>";
        result += "<p style=\"font-style: italic;\">How much RAM {did we set?; is being used?; is committed?; is " +
                "max?; is in permgen?} </p>";
        result += "<p><a href='" + app + NEXTGEN_WEB + "dashboard/dashboard" + "'>Back to the Dashboard dashboard</a>";
        result += "<pre style=\"word-wrap: break-word; white-space: pre-wrap;\">";
        result += "\n\n";
        MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
        result += "Heap:\n";
        result += "Init: " + humanReadableByteCount(mem.getHeapMemoryUsage().getInit(), false) + " (" + mem
                .getHeapMemoryUsage().getInit() + ")\n";
        result += "Used: " + humanReadableByteCount(mem.getHeapMemoryUsage().getUsed(), false) + " (" + mem
                .getHeapMemoryUsage().getUsed() + ")\n";
        result += "Committed: " + humanReadableByteCount(mem.getHeapMemoryUsage().getCommitted(), false) + " (" + mem
                .getHeapMemoryUsage().getCommitted() + ")\n";
        result += "Max: " + humanReadableByteCount(mem.getHeapMemoryUsage().getMax(), false) + " (" + mem
                .getHeapMemoryUsage().getMax() + ")\n";
        result += "\n";
        result += "Non-Heap:\n";
        result += "Init: " + humanReadableByteCount(mem.getNonHeapMemoryUsage().getInit(), false) + " (" + mem
                .getNonHeapMemoryUsage().getInit() + ")\n";
        result += "Used: " + humanReadableByteCount(mem.getNonHeapMemoryUsage().getUsed(), false) + " (" + mem
                .getNonHeapMemoryUsage().getUsed() + ")\n";
        result += "Committed: " + humanReadableByteCount(mem.getNonHeapMemoryUsage().getCommitted(), false) + " (" + mem
                .getNonHeapMemoryUsage().getCommitted() + ")\n";
        result += "Max: " + humanReadableByteCount(mem.getNonHeapMemoryUsage().getMax(), false) + " (" + mem
                .getNonHeapMemoryUsage().getMax() + ")\n";
        result += "\n";
        result += "\n\n";
        result += "</pre>";
        return result;
    }

    /**
     * Return a set of release notes based on the git commits of the last four weeks
     *
     * @return String showing report
     */
    @Spring3CorsFilter
    @RequestMapping(value = {NEXTGEN_WEB + "releasenotes"}, method = RequestMethod.GET, produces = "text/html")
    @ResponseBody
    String getReleaseNotes() {
        logger.info(NEXTGEN_WEB + "releasenotes");
        if (Environment.isProduction()) {
            throw new AccessDeniedException("You are not authorised");
        }
        String app = context.getContextPath();
        String result = "";
        result += "<h3>Release notes - Panorama Java Application</h3>";
        result += "<p style=\"font-style: italic;\">Has my change gotten into this kit? </p>";
        result += "<p><a href='" + app + NEXTGEN_WEB + "dashboard/dashboard" + "'>Back to the Dashboard dashboard</a>";
        result += "<pre style=\"word-wrap: break-word; white-space: pre-wrap;\">";
        result += "\n\n";
        final ClassPathResource classPathResource = new ClassPathResource("/ReleaseNotes.txt");
        String content = "";
        try {
            content = FileCopyUtils.copyToString(new InputStreamReader(classPathResource.getInputStream()));
        } catch (IOException e) {
            logger.warn("An error occurred loading the file", classPathResource, e);
            content = "error occurred - see logs";
        }
        result += content;
        result += "\n\n";
        result += "</pre>";
        return result;
    }

    /**
     * Return a BGP status report
     *
     * @return String showing report
     */
    @Spring3CorsFilter
    @RequestMapping(value = {NEXTGEN_MODULE_VERSION + "/bgpstatus/report"}, method = RequestMethod.GET, produces =
            "text/html")
    @ResponseBody
    String getBGPReport() {
        logger.info(NEXTGEN_MODULE_VERSION + "/bgpstatus/report");
        // Switching off until we migrate to Service Ops for prod - want this public for the test environments
        //if(Environment.isProduction()){
        //    throw new AccessDeniedException("You are not authorised");
        //}
        String app = context.getContextPath();
        String result = "";
        result += "<h3>BGP Status</h3>";
        result += "<p style=\"font-style: italic;\">What are all the background processes running in avaloq? Are they" +
                " running? </p>";
        result += "<p><a href='" + app + NEXTGEN_WEB + "dashboard/dashboard" + "'>Back to the Dashboard dashboard</a>";
        result += "<pre style=\"word-wrap: break-word; white-space: pre-wrap;\">";
        result += "\n\n";
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        DateTime datetime = backGroundProcessIntegrationService.getCurrentTime(serviceErrors);
        result += "Background Process Master Time: " + datetime + "\n\n";
        List<BackGroundProcess> bgps = backGroundProcessIntegrationService.getBackGroundProcesses(serviceErrors);
        for (BackGroundProcess bgp : bgps) {
            result += "    BGPId: " + bgp.getBGPId() + "\n";
            result += "    BGP Instance: " + bgp.getBGPInstance() + "\n";
            result += "    BGP Name: " + bgp.getBGPName() + "\n";
            result += "    BGP SID: " + bgp.getSID() + "\n";
            result += "    BGP Current Time: " + bgp.getCurrentTime() + "\n";
            result += "    BGP is running: " + bgp.isBGPValid() + "\n";
            result += "    \n\n";
        }
        result += "</pre>";
        return result;
    }

    /**
     * Returns the overall status of the application as well as the detailed statuses of the micorservices and their
     * respective caches
     *
     * @return String of simple response
     */
    @Spring3CorsFilter
    @RequestMapping(value = {NEXTGEN_MODULE_VERSION + "/services/status"}, method = RequestMethod.GET, produces =
            "application/json")
    @ResponseBody
    ApiResponse getDetailedApplicationStatus() {
        if (Environment.isProduction()) {
            throw new AccessDeniedException("You are not authorised");
        }
        List<ServiceStatus> serviceStatuses = serviceStatus.getServiceStatus();
        String applicationStatus = getStatus(serviceStatuses);
        ApplicationServiceStatusDto serviceStatus = new ApplicationServiceStatusDto("NEXTGEN", applicationStatus,
                serviceStatuses);
        ApiResponse apiResponse = new ApiResponse(CURRENT_VERSION_API, serviceStatus);
        return apiResponse;
    }

    /**
     * Returns single status of nextgen application. This status is used to check nextgen health.
     *
     * @return String of simple response
     */
    @Spring3CorsFilter
    @RequestMapping(value = {NEXTGEN_MODULE_VERSION + "/status"}, method = RequestMethod.GET, produces =
            "application/json")
    @ResponseBody
    String getApplicationStatus() {
        String applicationStatus = getStatus(serviceStatus.getServiceStatus());
        return applicationStatus;
    }

    private String getStatus(List<ServiceStatus> serviceStatuses) {
        String applicationStatus = STARTED;
        for (ServiceStatus serviceStatus : serviceStatuses) {
            if (serviceStatus.getServiceStatus().equals(SERVICE_UNAVAILABLE)) {
                applicationStatus = SERVICE_UNAVAILABLE;
                break;
            } else if (serviceStatus.getServiceStatus().equals(STARTING)) {
                applicationStatus = STARTING;
            }
        }
        return applicationStatus;
    }

    private static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}