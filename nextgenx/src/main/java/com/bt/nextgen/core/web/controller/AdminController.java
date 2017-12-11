package com.bt.nextgen.core.web.controller;

import com.bt.nextgen.api.user.v1.model.UserNoticesDto;
import com.bt.nextgen.api.user.v1.service.UserNoticesDtoService;
import com.bt.nextgen.api.version.model.MobileAppVersionDto;
import com.bt.nextgen.api.version.service.MobileAppVersionDtoService;
import com.bt.nextgen.api.version.service.VersionService;
import com.bt.nextgen.core.cache.CacheType;
import com.bt.nextgen.core.cache.EhCacheInfoImpl;
import com.bt.nextgen.core.cache.GenericCache;
import com.bt.nextgen.core.log.performance.Settings;
import com.bt.nextgen.core.log.performance.contrib.MethodInvocationEvent;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.web.interceptor.CrossSiteReqForgeryFilter;
import com.bt.nextgen.core.web.util.View;
import com.bt.nextgen.service.avaloq.CacheSearch;
import com.btfin.panorama.service.client.util.cache.StaticDataLoaderValue;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.DataLoader;
import com.bt.nextgen.service.avaloq.StaticDataLoader;
import com.bt.nextgen.service.avaloq.StaticDataLoaderValueImpl;
import com.bt.nextgen.service.integration.user.notices.model.NoticeType;
import com.bt.nextgen.service.integration.user.notices.model.Notices;
import com.bt.nextgen.service.integration.user.notices.model.NoticesKey;
import com.bt.nextgen.service.integration.user.notices.repository.NoticesRepository;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Statistics;
import net.sf.ehcache.TransactionController;
import net.sf.ehcache.config.CacheConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static com.bt.nextgen.core.web.interceptor.CrossSiteReqForgeryFilter.CSRF_TOKEN;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class AdminController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private net.sf.ehcache.CacheManager ehCacheManager;

    private CacheManager cacheManager;
    private String machineName;

    @Autowired
    private FeatureTogglesService togglesService;

    @Autowired
    private DataLoader dataLoader;

    @Autowired
    private CacheSearch cacheSearch;

    @Autowired
    private EhCacheInfoImpl ehCacheInfo;

    @Autowired
    private GenericCache genericCache;

    @Autowired
    private VersionService versionService;

    @Autowired
    private MobileAppVersionDtoService mobileAppVersionDtoService;

    @Autowired
    @Qualifier("UserUpdatesDtoServiceV1")
    private UserNoticesDtoService userNoticesDtoService;

    @Autowired
    private NoticesRepository noticesRepository;


    /**
     * Ehcache specific info.
     */
    public static class EhCacheInfo {
        private final String name;
        private final Statistics statistics;

        public EhCacheInfo(String name, Statistics statistics) {
            this.name = name;
            this.statistics = statistics;
        }

        public String getName() {
            return name;
        }

        public Statistics getStatistics() {
            return statistics;
        }
    }

    /**
     * Interface for executing Ehcache task, which may require transaction.
     */
    private static interface EhCacheTask {
        /**
         * Get Ehcache instance.
         * <p/>
         * This is used to check if cache operations need to be enclosed in a transaction.
         */
        net.sf.ehcache.Cache getCache();

        /**
         * Execute the cache operation.
         *
         * @return Result of cache operation.
         */
        Object execute();
    }

    @Autowired
    public AdminController(CacheManager cacheManager, net.sf.ehcache.CacheManager ehCacheManager) {
        this.cacheManager = cacheManager;
        this.ehCacheManager = ehCacheManager;

        try {
            this.machineName = parseMachineName(Inet4Address.getLocalHost().getHostName());

        } catch (Exception err) {
            logger.error("Could not find server name for location", err);
            this.machineName = "NG-" + Properties.getString("environment");
        }

    }

    @RequestMapping(value = { "/secure/page/admin/home", "/secure/page/serviceOps/admin/home" }, method = GET)
    public String showHome(ModelMap model, HttpSession session) {
        addSecurityToken(model, session);
        return "adminHome";
    }

    @RequestMapping(value = { "/secure/page/admin/invocation",
            "/secure/page/serviceOps/admin/invocation" }, method = GET)
    public ModelAndView displayInvocationTracker(ModelMap model, HttpServletRequest request) {
        Map<String, Map<String, MethodInvocationEvent>> beanHealthReport = new TreeMap<String, Map<String, MethodInvocationEvent>>();

        for (String beanName : Settings.invocationTracker.getBeanNames()) {
            beanHealthReport.put(beanName, Settings.invocationTracker.getInvocations(beanName));
        }
        model.put("report", beanHealthReport);
        return new ModelAndView("invocationTracker", model);
    }

    @RequestMapping(value = { "/secure/page/admin/stats", "/secure/page/serviceOps/admin/stats" }, method = GET)
    public ModelAndView displayStatsTracker(ModelMap model, HttpServletRequest request) {
        model.put("report", Settings.statsTracker.getStats());
        return new ModelAndView("statsTracker", model);
    }

    @RequestMapping(value = { "/secure/page/serviceOps/admin/cacheMaintenance",
            "/secure/page/admin/cacheMaintenance" }, method = GET)
    public ModelAndView displayCaches(ModelMap model, HttpServletRequest request, HttpSession session) {
        List<Object> cacheStatistics = new ArrayList<>();
        List<String> names;

        if (cacheManager instanceof EhCacheCacheManager) {
            names = Arrays.asList(((EhCacheCacheManager) cacheManager).getCacheManager().getCacheNames());
        } else {
            names = new ArrayList<>(cacheManager.getCacheNames());
        }

        Collections.sort(names);
        for (final String cacheName : names) {
            final Object cacheObject = cacheManager.getCache(cacheName).getNativeCache();

            if (cacheObject instanceof net.sf.ehcache.Cache) {
                final net.sf.ehcache.Cache ehCache = (net.sf.ehcache.Cache) cacheObject;

                try {
                    cacheStatistics.add(executeEhCacheTask(new EhCacheTask() {
                        @Override
                        public net.sf.ehcache.Cache getCache() {
                            return ehCache;
                        }

                        @Override
                        public Object execute() {
                            ehCache.setStatisticsAccuracy(Statistics.STATISTICS_ACCURACY_GUARANTEED);
                            return new EhCacheInfo(cacheName, ehCache.getStatistics());
                        }
                    }));
                } catch (Exception e) {
                    logger.error("Ignoring exception raised while trying to get statistics from Ehcache {}", cacheName, e);
                }
            }

            else {
                cacheStatistics.add(cacheObject);
            }
        }

        addSecurityToken(model, session);
        model.put("caches", cacheStatistics);
        model.put("staticDataCacheNames", getStaticDataCacheNames());

        return new ModelAndView("cacheMaintenance", model);
    }

    @RequestMapping(value = { "/secure/page/admin/cacheMaintenance/{cacheName}/clearCache",
            "/secure/page/serviceOps/admin/cacheMaintenance/{cacheName}/clearCache" }, method = POST)
    public String clearCache(ModelMap model, @PathVariable String cacheName, HttpSession session) {
        internalClearCache(cacheName);
        addSecurityToken(model, session);
        return "redirect:/secure/page/serviceOps/admin/cacheMaintenance";
    }

    @RequestMapping(value = { "/secure/page/admin/cacheMaintenance/clearAllCaches",
            "/secure/page/serviceOps/admin/cacheMaintenance/clearAllCaches" }, method = POST)
    public String clearAllCaches(ModelMap model, HttpSession session) {
        final Set<String> staticDataCacheNames = ehCacheInfo.getCacheNames();

        for (String cacheName : cacheManager.getCacheNames()) {
            if (!staticDataCacheNames.contains(cacheName)) {
                internalClearCache(cacheName);
            }
        }

        addSecurityToken(model, session);

        return "redirect:/secure/page/serviceOps/admin/cacheMaintenance";
    }

    @RequestMapping(value = { "/secure/page/serviceOps/admin/dataMaintenance",
            "/secure/page/admin/dataMaintenance" }, method = GET)
    public ModelAndView displayStaticDataList(ModelMap model, HttpServletRequest request) {
        final Map<CacheType, List<String>> cacheSearchAttributes = new HashMap<>();
        final List<StaticDataLoader> staticDataLoaders = dataLoader.getStaticDataLoaders();

        for (StaticDataLoader loader : staticDataLoaders) {
            final CacheType cacheType = loader.getCacheType();
            final List<String> searchAttributeNames = genericCache.getSearchAttributeNames(cacheType);

            if (searchAttributeNames != null && searchAttributeNames.size() > 0) {
                cacheSearchAttributes.put(cacheType, searchAttributeNames);
            }
        }

        model.put("dataLoaders", staticDataLoaders);
        model.put("cacheSearchAttributes", cacheSearchAttributes);

        return new ModelAndView("dataMaintenance", model);
    }

    @RequestMapping(value = { "/secure/page/admin/dataMaintenance/{name}/reload",
            "/secure/page/serviceOps/admin/dataMaintenance/{name}/reload" }, method = POST)
    public String reloadStaticData(ModelMap model, @PathVariable String name, HttpSession session) {
        final StaticDataLoader loader = dataLoader.getStaticDataLoader(name);

        if (loader != null) {
            logger.info("Service Operator triggering reload of cache:{}", name);
            loader.getLoaderTask().load();
        }

        addSecurityToken(model, session);

        return "redirect:/secure/page/serviceOps/admin/dataMaintenance";
    }

    /**
     * Search cache.
     */
    @RequestMapping(value = { "/secure/page/serviceOps/admin/cacheQuery" }, method = POST)
    public ModelAndView searchCache(ModelMap model, @RequestParam("name") String name,
            @RequestParam(value = "query") final String query, HttpServletRequest request, HttpSession session) {
        final StaticDataLoader loader;

        if (StringUtils.isBlank(name) || StringUtils.isBlank(query)) {
            model.put("searchError", "Static data name and query must be specified");
        }

        else {
            loader = dataLoader.getStaticDataLoader(name);

            try {
                logger.info("Cache Type is {} with name {}",loader.getCacheType(),name);
                logger.info("Cache Query is {}",query);
                List<StaticDataLoaderValue> searchResults = new ArrayList<>();
                searchResults=cacheSearch.searchElements(loader.getCacheType(),query);

                logger.info("Size of result set is {}",searchResults);
                model.put("searchResults", searchResults);
            } catch (Exception e) {
                logger.error("searchError", e);
                model.put("searchError", e.getMessage());
            }
        }

        model.put("staticDataName", name);
        model.put("staticDataQuery", query);

        addSecurityToken(model, session);

        return displayStaticDataList(model, request);
    }

    /**
     * Look up cache for an entry with a specified key.
     */
    @RequestMapping(value = { "/secure/page/serviceOps/admin/cacheLookUp" }, method = POST)
    public ModelAndView lookUpCache(ModelMap model, @RequestParam("name") String name,
            @RequestParam(value = "key", required = false) final String key, HttpServletRequest request, HttpSession session) {
        final Cache cache = cacheManager.getCache(name);

        if (key != null) {
            final Object cacheObject = cache.getNativeCache();

            if (cache != null) {
                final Object cacheElement;

                if (cacheObject instanceof net.sf.ehcache.Cache) {
                    final net.sf.ehcache.Cache ehCache = (net.sf.ehcache.Cache) cacheObject;

                    cacheElement = executeEhCacheTask(new EhCacheTask() {
                        @Override
                        public net.sf.ehcache.Cache getCache() {
                            return ehCache;
                        }

                        @Override
                        public Object execute() {
                            final Element element = ehCache.get(key);

                            return (element != null ? element.getObjectValue() : null);
                        }
                    });
                }

                else {
                    cacheElement = cache.get(key);
                }

                model.put("cacheKey", key);
                model.put("cacheValue", cacheElement);
            }
        }

        model.put("cacheName", name);

        return displayCaches(model, request, session);
    }

    private void internalClearCache(String cacheName) {
        final Cache cache = cacheManager.getCache(cacheName);
        final Object cacheObject = cache.getNativeCache();

        if (cacheObject instanceof net.sf.ehcache.Cache) {
            final net.sf.ehcache.Cache ehCache = (net.sf.ehcache.Cache) cacheObject;

            executeEhCacheTask(new EhCacheTask() {
                @Override
                public net.sf.ehcache.Cache getCache() {
                    return ehCache;
                }

                @Override
                public Object execute() {
                    cache.clear();

                    return null;
                }
            });
        }

        else {
            cache.clear();
        }

        logger.info("Cleared cache: {}", cacheName);
    }

    @ModelAttribute(value = "machineName")
    private String getSafeMachineName() {
        return this.machineName;

    }

    public String parseMachineName(String machineName) throws Exception {
        if (machineName.contains("."))
            machineName = machineName.substring(0, machineName.indexOf("."));

        return machineName;
    }

    private Object executeEhCacheTask(EhCacheTask task) {
        final net.sf.ehcache.Cache cache = task.getCache();
        final CacheConfiguration cacheConfig = cache.getCacheConfiguration();
        final boolean transactional = (cacheConfig.isLocalTransactional() || cacheConfig.isXaStrictTransactional()
                || cacheConfig.isXaTransactional());
        final Object retval;

        if (transactional) {
            TransactionController tc = cache.getCacheManager().getTransactionController();

            try {
                tc.begin();
                retval = task.execute();
                tc.commit();
            } catch (Exception e) {
                tc.rollback();
                throw e;
            }
        }

        else {
            retval = task.execute();
        }

        return retval;
    }

    /**
     * Get names of static data caches as a string that can be used to check for existence using JSTL.
     *
     * @return Concatenated names of static data caches. Each cache name is prefixed and suffixed by '##'.
     */
    private String getStaticDataCacheNames() {
        String retval = "";

        for (String name : ehCacheInfo.getCacheNames()) {
            retval += "##" + name + "##";
        }

        return retval;
    }

    private void addSecurityToken(ModelMap model, HttpSession session) {
        model.put(CSRF_TOKEN, CrossSiteReqForgeryFilter.getTokenFromSession(session));
    }

    @RequestMapping(value = { "/secure/page/admin/cacheMemory",
            "/secure/page/serviceOps/admin/cacheMemory" }, method = GET)
    public ModelAndView cacheMemoryData(ModelMap model, HttpSession session) {

        String[] names = this.ehCacheManager.getCacheNames();
        Collection<Ehcache> caches = new LinkedHashSet<Ehcache>(names.length);
        for (String name : names) {
            caches.add(this.ehCacheManager.getEhcache(name));
        }

        Map<String, String> cacheSizes = new LinkedHashMap<>();
        for (Ehcache cache : caches) {
            cacheSizes.put(cache.getName(), humanReadable(cache.calculateInMemorySize()));
            logger.debug("Printing size of {}: {}", cache.getName(), humanReadable(cache.calculateInMemorySize()));
        }

        addSecurityToken(model, session);
        model.put("cacheSizes", cacheSizes);

        return new ModelAndView("cacheMemory", model);
    }

    private String humanReadable(long bytes) {
        if (bytes < 1024)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    @RequestMapping(value ={"/secure/page/serviceOps/admin/avaloqVersion"}, method = GET)
    public ModelAndView avaloqVersionDescription(ModelMap model)
    {
        model.addAttribute("avaloqVersion",versionService.getAvaloqInstallationInformation());
        return new ModelAndView("avaloqVersion", model);
    }

    @RequestMapping(value = { "/secure/page/admin/toggles", "/secure/page/serviceOps/admin/toggles" }, method = GET)
    public ModelAndView featureToggles(ModelMap model) {
        model.addAttribute("toggles", getFeatureToggles().getMap());
        model.addAttribute("machineName", machineName);
        return new ModelAndView("toggles", model);
    }

    @RequestMapping(value = { "/secure/page/admin/toggles/add", "/secure/page/serviceOps/admin/toggles/add" }, method = POST)
    public String addFeatureToggle(@RequestParam String name, @RequestParam(required = false) boolean toggle) {
        return setFeatureToggle(name, toggle);
    }

    @RequestMapping(value = { "/secure/page/admin/toggles/{name}/set", "/secure/page/serviceOps/admin/toggles/{name}/set" }, method = POST)
    public String setFeatureToggle(@PathVariable String name, @RequestParam boolean toggle) {
        logger.info("Setting feature toggle {} = {}", name, toggle);
        getFeatureToggles().setFeatureToggle(name, toggle);
        return "redirect:/secure/page/admin/toggles";
    }

    private FeatureToggles getFeatureToggles() {
        return togglesService.findOne(new FailFastErrorsImpl());
    }

    @RequestMapping(value = {"/secure/page/admin/mobileAppVersions","/secure/page/serviceOps/admin/mobileAppVersions"}, method = RequestMethod.GET)
    public ModelAndView getMobileAppVersions() {
        return new ModelAndView(
                View.MOBILE_APP_VERSION, "mobileAppVersions", mobileAppVersionDtoService.findAll(new ServiceErrorsImpl()));
    }

    @RequestMapping(value = {"/secure/page/admin/updateMobileAppVersion","/secure/page/serviceOps/admin/updateMobileAppVersion"}, method = RequestMethod.POST)
    public String updateMobileAppVersion(
            @RequestParam String platform, @RequestParam String version) {
        mobileAppVersionDtoService.update(new MobileAppVersionDto(platform, version), new ServiceErrorsImpl());
        return "redirect:/secure/page/serviceOps/admin/mobileAppVersions";
    }

    @RequestMapping(value = {"/secure/page/admin/userNotices", "/secure/page/serviceOps/admin/userNotices"}, method = GET)
    public ModelAndView getUserUpdates(@RequestParam(required = false) String userId) {
        List<UserNoticesDto> userNoticesList = new ArrayList<>();
        if (userId != null) {
            userNoticesList.addAll(userNoticesDtoService.findAll(new ServiceErrorsImpl()));
        }
        return new ModelAndView("userNotices", "userNotices", userNoticesList)
                .addObject("userId", userId).addObject("availableNotices", noticesRepository.getLatestUpdatesMap().values());
    }

    @RequestMapping(value = {"/secure/page/admin/userNotices", "/secure/page/serviceOps/admin/userNotices"}, method = POST)
    public ModelAndView modifyUserUpdates(@RequestParam String noticeId, @RequestParam Integer version,
                                          @RequestParam(required = false) String description) {
        final Integer newVersion = version + Integer.valueOf(1);
        final Notices modifiedNotice = noticesRepository.save(
                new Notices(new NoticesKey(NoticeType.forNoticeId(noticeId), newVersion), description));
        return new ModelAndView("userNotices", "modifiedNotice", modifiedNotice)
                .addObject("availableNotices", noticesRepository.getLatestUpdatesMap().values());
    }
}
