package com.bt.nextgen.service.integration.onboardinglog;

import com.bt.nextgen.service.integration.onboardinglog.service.OnboardingLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Poll the personalisation database to detect and log onboarding application failures
 */
@Component
public class OnboardingStatusListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(OnboardingStatusListener.class);

    @Autowired
    private OnboardingLogService onboardingLogService;

    private ScheduledExecutorService executor;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (executor == null) {
            executor = Executors.newSingleThreadScheduledExecutor();
            Runnable loggingTask = getLoggingTask();
            executor.scheduleAtFixedRate(loggingTask, 0, 10, TimeUnit.MINUTES);

            logger.info("Started thread to poll for onboarding application failures.");
        }
    }

    private Runnable getLoggingTask() {
        return new Runnable() {
            @Override
            public void run() {
                onboardingLogService.logEvents();
            }
        };
    }
}
