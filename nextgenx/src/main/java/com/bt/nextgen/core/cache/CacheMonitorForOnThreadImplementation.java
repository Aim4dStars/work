package com.bt.nextgen.core.cache;

import com.bt.nextgen.core.ServiceStatusImpl;
import com.bt.nextgen.core.jms.JmsIntegrationService;
import com.bt.nextgen.core.jms.ListenerNotRunning;
import com.bt.nextgen.core.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This scheduler will check the status for cache for OnThread Implementation.
 * Created by l070589 on 17/02/2017.
 */

@Component
@Profile({"default", "OnThreadImplementation"})
public class CacheMonitorForOnThreadImplementation {

    private static final Logger logger = LoggerFactory.getLogger(CacheMonitorForOnThreadImplementation.class);

    private TaskScheduler scheduler;

    private ScheduledFuture scheduledOnTimeCacheCheckTask;
    private ScheduledFuture continuousRunningMonitor;

    private static final String CACHE_CHECK_ONE_TIME_INT_MIN = "cache.check.onetime.interval.minutes";
    private static final String CACHE_CHECK_ONE_TIME_START_DELAY = "cache.check.onetime.start.delay";
    private static final String CACHE_CHECK_CONT_INT_MINUTES = "cache.check.cont.interval.minutes";
    private static final String CACHE_CHECK_CONT_START_DELAY = "cache.check.cont.start.delay";
    private static final String MAX_RETRY_COUNT = "max.retry.count.for.serialized.cache.population";

    private static final long ONE_MINUTE_IN_MILLIS = 60000L;//milliseconds
    private AtomicInteger counter = new AtomicInteger(0);


    @Autowired
    private ServiceStatusImpl serviceStatus;

    @Autowired
    private DiskCacheLoadingService diskCacheLoadingService;

    @Autowired
    private DiskCacheSerialization diskCacheSerialization;

    @Autowired
    private JmsIntegrationService jmsIntegrationService;

    /**
     * This is a continuos monitoring task to run every CACHE_CHECK_CONT_INT_MINUTES minutes with a delay of  CACHE_CHECK_CONT_START_DELAY min
     */
    @PostConstruct
    public void scheduleContinuosMonitoringTask() {
        logger.info("Scheduling One time timer task");
        scheduleCacheCheckTaskAtStartUp();
        ScheduledExecutorService localExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduler = new ConcurrentTaskScheduler(localExecutor);
        Calendar date = Calendar.getInstance();
        long t = date.getTimeInMillis();
        Date stopTime = new Date(t + (Properties.getInteger(CACHE_CHECK_CONT_START_DELAY) * ONE_MINUTE_IN_MILLIS));
        continuousRunningMonitor = scheduler.scheduleAtFixedRate(continuousCacheMonitorTask, stopTime, Properties.getInteger(CACHE_CHECK_CONT_INT_MINUTES) * ONE_MINUTE_IN_MILLIS);
        logger.info("Continuos Monitoring initiated", stopTime);
    }


    /**
     * Schedules the start up task check to run after CACHE_CHECK_ONE_TIME_INT_MIN minutes with a delay of CACHE_CHECK_ONE_TIME_START_DELAY mins.
     */
    public void scheduleCacheCheckTaskAtStartUp() {
        ScheduledExecutorService localExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduler = new ConcurrentTaskScheduler(localExecutor);

        Calendar date = Calendar.getInstance();
        long t = date.getTimeInMillis();
        Date stopTime = new Date(t + (Properties.getInteger(CACHE_CHECK_ONE_TIME_START_DELAY) * ONE_MINUTE_IN_MILLIS));
        scheduledOnTimeCacheCheckTask = scheduler.scheduleAtFixedRate(cacheCheckTaskAtStartUp, stopTime, Properties.getInteger(CACHE_CHECK_ONE_TIME_INT_MIN) * ONE_MINUTE_IN_MILLIS);
        logger.info("Start Up Scheduler initiated", stopTime);
    }

    /**
     * Check periodically that the cache is present or not. Load the cache from disk if not present otherwise persist it.
     */
    protected Runnable continuousCacheMonitorTask = new Runnable() {
        @Override
        public void run() {
            try {
                if (!serviceStatus.checkCacheStatus()) {
                    logger.error("CACHE_MISSING missing some essential caches. Thus locading from disk");
                    diskCacheLoadingService.loadCache();
                    logger.info("Placing Request to Load From JMS again ");
                    jmsIntegrationService.loadData();
                } else {
                    diskCacheSerialization.persistCacheOnDisk();
                    logger.info("Data Persisting Process Compeleted");
                }
            }
            catch (Exception e) {
                logger.info("Error Occurred in the task : {}", e);
            }
        }
    };


    /**
     * Performs a cache check, cancels itself once the overall status of all caches is populated or once the cache is loade from disk
     */
    protected Runnable cacheCheckTaskAtStartUp = new Runnable() {
        @Override
        public void run() {
            startUpCacheCheck();
        }
    };


    private void startUpCacheCheck()
    {
        logger.info("Value for counter is {} Is cache populated {}", counter.intValue(), serviceStatus.checkCacheStatus());
        int maxRetry = Properties.getInteger(MAX_RETRY_COUNT);
        if (counter.intValue() < maxRetry) {
            try {
                if (!serviceStatus.checkCacheStatus()) {
                    if (counter.incrementAndGet() == maxRetry) {
                        logger.error("CACHE_NOT_LOADED Services have not started within an acceptable time period. Thus loading from disk");
                        diskCacheLoadingService.loadCache();
                        jmsIntegrationService.loadData();
                        scheduledOnTimeCacheCheckTask.cancel(true);
                    }
                } else {
                    diskCacheSerialization.persistCacheOnDisk();
                    scheduledOnTimeCacheCheckTask.cancel(true);
                }
            } catch (Exception e) {
                logger.info("Error Occurred in the task : {}", e);
            }
        }
    }
}
