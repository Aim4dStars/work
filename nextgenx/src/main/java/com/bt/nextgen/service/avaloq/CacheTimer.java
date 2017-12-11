package com.bt.nextgen.service.avaloq;


import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.web.controller.CacheStatusController;
import com.bt.nextgen.service.avaloq.gateway.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
class CacheTimer {

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    private static Logger logger = LoggerFactory.getLogger(CacheTimer.class);

    @Autowired
    CacheStatusController cacheStatusController;

    void startTimer()  {
        final Callable<String> cacheCheck = new Callable<String>() {
            public String call() { return checkCaches();}
        };

        int waitDuration = Properties.getInteger("cache.checker.waitingtime");
        logger.info("Wait duration loaded: " + waitDuration);

        FutureTask<String> futureTask = new FutureTask<>(cacheCheck);
        executor.schedule(futureTask, waitDuration, TimeUnit.MINUTES);
        logger.info("Scheduled to run in : " + waitDuration + " minutes");
    }

    private String checkCaches() {
        for (String cacheName: CacheStatusController.CACHE_NAME_MAP.keySet()) {
            boolean cacheOK = checkCache(cacheName);
            if (!cacheOK) {
                logger.error("SplunkException: Cache " + cacheName + " has timed out - please investigate!");
            } else {
                logger.info("Cache timer for " + cacheName + " loaded ok.");
            }
        }
        return "Done";
    }

    private boolean checkCache(String cacheName) {
        String result = cacheStatusController.showCacheStatusResult(cacheName, EventType.STARTUP.toString());
        logger.info("Checked cache " + cacheName + " with status " + result);
        return (result != null && result.equalsIgnoreCase("OK"));
    }
}
