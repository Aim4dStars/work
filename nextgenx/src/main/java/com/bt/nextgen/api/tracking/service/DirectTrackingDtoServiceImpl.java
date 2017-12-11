package com.bt.nextgen.api.tracking.service;

import com.bt.nextgen.api.draftaccount.LoggingConstants;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.tracking.model.DirectTrackingDto;
import com.bt.nextgen.api.tracking.model.TrackingDto;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplicationRepository;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional(value = "springJpaTransactionManager")
public class DirectTrackingDtoServiceImpl implements DirectTrackingDtoService {

    @Autowired
    private ClientApplicationRepository clientApplicationsRepository;
    @Autowired
    private TrackingDtoService trackingDtoService;
    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;


    private static final Logger LOGGER = LoggerFactory.getLogger(DirectTrackingDtoService.class);

    /**
     * Get status and accountId for direct
     *
     * @param clientApplicationKey the ApplicationId of the submission
     * @return DirectTrackingDto Indicate status
     */
    public DirectTrackingDto find(ClientApplicationKey clientApplicationKey, final ServiceErrors serviceErrors) {
        LOGGER.info(LoggingConstants.DIRECT_ONBOARDING_TRACKING + "ClientApplication Key " + clientApplicationKey);
        Long applicationId = clientApplicationKey.getClientApplicationKey();
        ClientApplication clientApplication = clientApplicationsRepository.find(applicationId);
        List<TrackingDto> trackingDtos = trackingDtoService.getTrackingDtos(Arrays.asList(clientApplication), true, false, serviceErrors);
        final TrackingDto trackingDto = trackingDtos.get(0);
        //Returning this minimal dto since wpl direct customer doesn't need a lot of details.
        DirectTrackingDto directTrackingDto = createDirectTrackingDto(trackingDto);
        return directTrackingDto;
    }

    private DirectTrackingDto createDirectTrackingDto(TrackingDto trackingDto) {
        DirectTrackingDto directTrackingDto = new DirectTrackingDto();
        directTrackingDto.setKey(trackingDto.getClientApplicationId());
        directTrackingDto.setStatus(trackingDto.getStatus());
        directTrackingDto.setEncryptedAccountId(trackingDto.getEncryptedBpId());
        return directTrackingDto;
    }
}
