package com.bt.nextgen.service.avaloq;

import com.bt.nextgen.core.repository.PartialInvalidationRequestRegister;
import com.bt.nextgen.core.repository.PartialInvalidationRequestRegisterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;

import java.util.Date;
import java.util.List;

@EnableScheduling
@Configuration
@SuppressWarnings({"squid:S1166","checkstyle:com.puppycrawl.tools.checkstyle.checks.coding.IllegalCatchCheck","squid:S1068"})
public class RequestRegisterScheduler {

    private static final Logger logger = LoggerFactory.getLogger(RequestRegisterScheduler.class);

    @Autowired
    private BeanFactoryTransactionAttributeSourceAdvisor transactionAnnotationWaiter;

    @Autowired
    private PartialInvalidationRequestRegisterRepository partialInvalidationRequestRegisterRepository;

    @Scheduled(cron = "${reset.request.register.cron}")
    @Transactional(value = "springJpaTransactionManager")
    public void removePartialInvalidationRecords()
    {
        try {
            logger.info("Performing scheduled cleanup of PartialInvalidationRequestRegister");
            List<PartialInvalidationRequestRegister> partialInvalidationRequestRegisters = partialInvalidationRequestRegisterRepository.fetchPartialInvalidationRequestRegisterEntry();


            for (PartialInvalidationRequestRegister register : partialInvalidationRequestRegisters) {
                logger.warn("No response was received for JMS partial invalidation request with correlationId {} - Cleaning the entry", register.getCorrelationId());
            }

            if(!partialInvalidationRequestRegisters.isEmpty())
                partialInvalidationRequestRegisterRepository.removeAllEntriesBeforeGivenDate(new Date());

            logger.info("Request register cleanup completed");
        }
        catch(Exception e){
            logger.warn("Exception occurred while performing cleanup of the partial invalidation register");
        }
    }

}
