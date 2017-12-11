package com.bt.nextgen.service.integration.onboardinglog.service;

import com.bt.nextgen.service.integration.onboardinglog.model.OnboardingLog;
import com.bt.nextgen.service.integration.onboardinglog.repository.OnboardingLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;

import java.util.List;

/**
 * This service retrieves and updates onboarding events which require logging
 */
@Service
@Transactional(value = "springJpaTransactionManager")
public class OnboardingLogServiceImpl implements OnboardingLogService {

    private static final Logger logger = LoggerFactory.getLogger(OnboardingLogServiceImpl.class);

    @Autowired
    private OnboardingLogRepository onboardingLogRepository;

    @Override
    public void logEvents() {
        try {
            List<OnboardingLog> unloggedEvents = onboardingLogRepository.findAll();

            for (OnboardingLog event : unloggedEvents) {
                String failure = getFailureMessage(event);
                logger.error(failure);

                event.setHasBeenLogged(true);
            }
        } catch (PersistenceException p) {
            logger.warn("Tried to log entries from ONBOARDING_LOG but another thread has it locked", p);
        }
    }

    private String getFailureMessage(OnboardingLog change) {
        StringBuilder sb = new StringBuilder();
        sb.append("Onboarding application failed: Application ID ");
        sb.append(change.getApplicationId());

        if (change.getClientGcmId() != null) {
            sb.append(" for Client GCM ID ");
            sb.append(change.getClientGcmId());
        }

        if (change.getStatus() != null) {
            sb.append(", Error status: ");
            sb.append(change.getStatus());
        }

        if (change.getFailureMessage() != null) {
            sb.append(", Error message: ");
            sb.append(change.getFailureMessage());
        }

        return sb.toString();
    }
}
