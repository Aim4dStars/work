package com.bt.nextgen.config;

import com.bt.nextgen.core.cache.DiskCacheLoadingService;
import com.bt.nextgen.core.jms.JmsIntegrationService;
import com.bt.nextgen.core.jms.ListenerNotRunning;
import com.bt.nextgen.core.jms.delegate.JmsInitializedEvent;
import com.bt.nextgen.core.jms.listener.ChunkListenerContainer;
import com.bt.nextgen.core.repository.RequestRegisterRepository;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.avaloq.AvaloqRequestBuilderUtil;
import com.bt.nextgen.service.avaloq.DataInitialization;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.code.StaticCodeEnumTemplate;
import com.bt.nextgen.service.avaloq.gateway.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@EnableAsync
@Component
public class DataInitializationService implements ApplicationListener<JmsInitializedEvent> {
    //TODO - XXX - This is to enforce that caching happens before autowiring in this instance. Improvement Required
    @Autowired
    BeanFactoryCacheOperationSourceAdvisor waitForCachingAspect;

    @Autowired
    DataInitialization dataInitialization;

    @Autowired
    private DiskCacheLoadingService diskCacheLoadingService;

    @Autowired
    private JmsIntegrationService jmsIntegrationService;

    @Autowired
    private RequestRegisterRepository requestRegisterRepository;

    @Autowired
    @Qualifier("messageListenerContainer")
    private ChunkListenerContainer messageListenerContainer;

    @Autowired
    @Qualifier("invMessageListenerContainer")
    private ChunkListenerContainer invMessageListenerContainer;

    private static Logger logger = LoggerFactory.getLogger(DataInitializationService.class);

    @Override
    public void onApplicationEvent(JmsInitializedEvent event) {
        populateCache();
    }


    /**
     * The PostConstruct annotation is used on a method that needs to be executed after dependency injection is done to perform any initialization.
     * This method MUST be invoked before the class is put into service. This annotation MUST be supported on all classes that support dependency injection.
     * The method annotated with PostConstruct MUST be invoked even if the class does not request any resources to be injected.
     * Only one method can be annotated with this annotation. The method on which the PostConstruct annotation is applied MUST full-fill all of the following criteria - -
     * 1. The method MUST NOT have any parameters except in the case of EJB interceptors in which case it takes an InvocationContext object as defined by the EJB specification.
     * 2. The return type of the method MUST be void.
     * 3. The method MUST NOT throw a checked exception.
     * 4. The method on which PostConstruct is applied MAY be public, protected, package private or private.
     * 5. The method MUST NOT be static except for the application client.
     * 6. The method MAY be final.
     * 7. If the method throws an unchecked exception the class MUST NOT be put into service except in the case of EJBs where the EJB can handle exceptions and even recover from them.
     */
    @PostConstruct
    public void start() {
        ///load from disk
        logger.info("Loading the cache using local file data");
        loadCacheFromDisk();
        //start listerners

        boolean isJmsListeningEnabled = Properties.getSafeBoolean(JmsConfig.JMS_LISTENING_ENABLED_PROPERTY);

        if (!isJmsListeningEnabled) {
            populateCache();
        }
        else {
            messageListenerContainer.start();
            invMessageListenerContainer.start();
        }
    }


    //Initializes data caches
    public void populateCache() {

        logger.info("Starting to populate data cache");
        logger.info("ThreadId before creating dedicated thread for data initialization {}", Thread.currentThread().getId());

        //TODO: This is the fix to skip the check of request-entry in registry at start-up.
        //Not the best solution but fix the current issue impacting least. For the proper implementation, the request should contain the event type
        removeStartupEntry();

        try {
            logger.info("Preparing to load avaloq version information");
            dataInitialization.loadAvaloqVersionInformation();


            loadAvaloqServices();

        } catch (ListenerNotRunning listenerNotRunning) {
            logger.info("Error populating cache via JMS", listenerNotRunning);
        } catch (Exception e) {
            logger.info("Error populating cache", e);
        }
    }

    private void loadCacheFromDisk() {
        try {
            diskCacheLoadingService.loadCache();
        } catch (Exception exception) {
            logger.error("Loading from Local files failed {} ", exception);
        }
    }

    private void removeStartupEntry() {
        try {
            requestRegisterRepository.removeStartupEntry(Template.STATIC_CODES.getName(), EventType.STARTUP.toString());
        } catch (Exception ex) {
            logger.error("Error cleaning u the startup service entries.", ex);
        }
    }

    private void loadAvaloqServices() throws ListenerNotRunning {
        if (AvaloqRequestBuilderUtil.isTemplateJmsEnabled(StaticCodeEnumTemplate.STATIC_CODES)) {
                /* Call only the STATIC CODES */
            logger.info("JMS is enabled for static codes");
            jmsIntegrationService.loadData();
        } else {
                /* Call everything */
            logger.info("JMS is not enabled, Calling all the Avaloq data services");
            dataInitialization.loadAllCaches();
        }

    }

    public void setMessageListenerContainer(ChunkListenerContainer messageListenerContainer) {
        this.messageListenerContainer = messageListenerContainer;
    }

    public void setInvMessageListenerContainer(ChunkListenerContainer invMessageListenerContainer) {
        this.invMessageListenerContainer = invMessageListenerContainer;
    }

    public void setDiskCacheLoadingService(DiskCacheLoadingService diskCacheLoadingService) {
        this.diskCacheLoadingService = diskCacheLoadingService;
    }
}
