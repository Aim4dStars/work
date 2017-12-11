package com.bt.nextgen.core.jms;

import com.bt.nextgen.core.jms.listener.ChunkListenerContainer;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.DataInitialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * This is a integration service which will be initializing the data that needs to be loaded at the server startup. Reference data
 * is loaded for StaticCodes, Broker Hierarchy and Asset List.
 */
@Service
public class JmsIntegrationService extends AbstractAvaloqIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(JmsIntegrationService.class);

    @Autowired
    @Qualifier("invMessageListenerContainer")
    private ChunkListenerContainer invMessageListenerContainer;

    @Autowired
    DataInitialization dataInitialization;

    private static String RETRY_JMS_LISTENER_PROPERTY = "jms.listener.retry";

    private static int counterOfFailure = 0;

    /**
     * This method will be invoked when the initialization data is to be loaded through JMS.
     *
     * @throws ListenerNotRunning
     */
    @Async
    public void loadData() throws ListenerNotRunning {


        if (checkJmsStatus()) {
            logger.info("JMS Listener RUNNING");

            logger.info("Preparing to load StaticCodes");
            dataInitialization.loadAllStaticCodes();

        } else {
            logger.error("JMS Listener NOT RUNNING : Retry was not successful : Exiting");
            throw new ListenerNotRunning();

        }

    }

	/**
	 * Recursive method that tries a number of times to see if the JMS queue has been setup by spring
     * @return
     */
    private boolean checkJmsStatus()
    {
        Integer maxAttempts = Properties.getInteger(RETRY_JMS_LISTENER_PROPERTY);
        boolean jmsRunning = false;
        if (invMessageListenerContainer.isRunning()) {
            jmsRunning =  true;
        } else {
            counterOfFailure++;

            // Retry 3 times for the JMS server startup
            if (counterOfFailure < maxAttempts) {
                logger.warn("JMS Listener NOT RUNNING : RETRYING : attempt " + counterOfFailure);
                try {
                    // Put the thread to sleep to give startup time to the JMS Server.
                    // This will be replaced via @Retryable (It needs Spring version 4)
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    logger.debug("Sleep interrupt");
                }

                // Self calling
                jmsRunning = checkJmsStatus();
            }
        }
        return jmsRunning;
    }


}
