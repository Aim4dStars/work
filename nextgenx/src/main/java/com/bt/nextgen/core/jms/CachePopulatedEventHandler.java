package com.bt.nextgen.core.jms;

import com.bt.nextgen.core.IServiceStatus;
import com.bt.nextgen.core.cache.CacheType;
import com.bt.nextgen.core.cache.EhCacheInfo;
import com.bt.nextgen.core.jms.delegate.CachePopulatedEvent;
import com.bt.nextgen.core.repository.RequestRegisterRepository;
import com.bt.nextgen.service.avaloq.DataInitialization;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.asset.aal.AalEnumTemplate;
import com.bt.nextgen.service.avaloq.broker.BrokerEnumTemplate;
import com.bt.nextgen.service.avaloq.gateway.EventType;
import net.sf.ehcache.Cache;
import net.sf.ehcache.TransactionController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by L054821 on 11/12/2014. This is a event handler which will be listening to the CachePopulatedEvent and will
 * perform required actions once the event is triggered
 */
@Component
public class CachePopulatedEventHandler implements ApplicationListener<CachePopulatedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(CachePopulatedEventHandler.class);

    @Autowired
    private DataInitialization dataInitialization;

    @Autowired
    private IServiceStatus IApplicationStatusServiceCollector;

    @Autowired
    private RequestRegisterRepository requestRegisterRepository;

    @Autowired
    private EhCacheInfo cacheInfo;

    @Autowired
    private Environment environment;

    @Value("${cache.enabled.services}")
    private String cacheTypes;

    /**
     * This method will be invoked when CachePopulatedEvent is triggered. It will load the brokers and assets for the
     * application.
     */
    @Override
    public void onApplicationEvent(CachePopulatedEvent event) {
        logger.info("CachePopulatedEvent published : Executing the handler process");
        //check if profile is OnThreadImplementation
        //If OffThreadImplementation, Check cache status of STATIC CODE type and
        // Check if all other type of caches are populated or not..?
        boolean cachePopulated = isOnThread() ? IApplicationStatusServiceCollector
                .checkCacheStatus() : staticCodeCachePopulatedStatus() && IApplicationStatusServiceCollector
                .checkCacheStatus();
        if (!cachePopulated) {
            removeCacheUpdateRequestEntryIfCompleted();
        }
        logger.info("Preparing to load other data caches after loading static codes.");
        dataInitialization.loadDataCaches();
    }

    private void removeCacheUpdateRequestEntryIfCompleted() {
        requestRegisterRepository.removeIfCompleted(BrokerEnumTemplate.PAGINATED_BROKER_HIERARCHY.getTemplateName(), EventType.STARTUP.toString());
        requestRegisterRepository.removeIfCompleted(BrokerEnumTemplate.PAGINATED_JOB_HIERARCHY.getTemplateName(), EventType.STARTUP.toString());
        requestRegisterRepository.removeIfCompleted(AalEnumTemplate.BROKER_PRODUCT_ASSETS.getTemplateName(),
                EventType.STARTUP.toString());
        requestRegisterRepository
                .removeIfCompleted(AalEnumTemplate.AAL_INDEX.getTemplateName(), EventType.STARTUP.toString());
        requestRegisterRepository
                .removeIfCompleted(AalEnumTemplate.INDEX_ASSET.getTemplateName(), EventType.STARTUP.toString());
        requestRegisterRepository.removeIfCompleted(Template.ADVISOR_PRODUCTS.getName(), EventType.STARTUP.toString());
        requestRegisterRepository.removeIfCompleted(Template.ASSET_DETAILS.getName(), EventType.STARTUP.toString());
        requestRegisterRepository.removeIfCompleted(Template.TD_ASSET_RATES.getName(), EventType.STARTUP.toString());
        requestRegisterRepository.removeIfCompleted(Template.TD_PRODUCT_RATES.getName(), EventType.STARTUP.toString());
    }

    private boolean staticCodeCachePopulatedStatus() {
        logger.debug("Started checking cache status for Static Code");
        boolean cachePopulated = true;
        List<String> cacheTypeList = Arrays.asList(cacheTypes.split(","));
        for (String cacheType : cacheTypeList) {
            Cache cache = cacheInfo.getCache(CacheType.valueOf(cacheType));
            final TransactionController transactionController = cache.getCacheManager().getTransactionController();
            int elementsInMemory = 0;
            try {
                transactionController.begin();
                elementsInMemory = cache.getSize();
                logger.info("Cached elements in memory for {} are {}", cacheType, elementsInMemory);
            } finally {
                transactionController.rollback();
            }
            if (elementsInMemory == 0) {
                cachePopulated = false;
            }
            logger.debug("Completed checking cache status for {} ", cacheType);
        }
        return cachePopulated;
    }

    public boolean isOnThread() {
        boolean isOnThread = false;
        List<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());
        logger.debug("number of active profiles={}", activeProfiles.size());
        if (!activeProfiles.contains("OffThreadImplementation")) {
            logger.info("Running OnThreadImplementation");
            isOnThread = true;
            return isOnThread;
        } else {
            logger.info("Running OffThreadImplementation");
            return isOnThread;
        }
    }
}
