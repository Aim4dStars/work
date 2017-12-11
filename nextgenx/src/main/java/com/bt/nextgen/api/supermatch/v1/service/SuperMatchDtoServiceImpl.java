package com.bt.nextgen.api.supermatch.v1.service;

import com.bt.nextgen.api.supermatch.v1.model.SuperMatchDto;
import com.bt.nextgen.api.supermatch.v1.model.SuperMatchDtoKey;
import com.bt.nextgen.api.supermatch.v1.model.UpdateType;
import com.bt.nextgen.api.supermatch.v1.util.SuperMatchDtoConverter;
import com.bt.nextgen.api.supermatch.v1.util.SuperMatchDtoHelper;
import com.bt.nextgen.api.supermatch.v1.validation.SuperMatchErrorMapper;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.supermatch.StatusSummary;
import com.bt.nextgen.service.integration.supermatch.SuperFundAccount;
import com.bt.nextgen.service.integration.supermatch.SuperMatchDetails;
import com.bt.nextgen.service.integration.supermatch.SuperMatchIntegrationService;
import com.bt.nextgen.service.integration.supernotification.SuperNotificationIntegrationService;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.btfin.panorama.core.security.integration.domain.IndividualDetail;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SuperMatchDtoServiceImpl implements SuperMatchDtoService {

    @Autowired
    private SuperMatchIntegrationService superMatchIntegrationService;

    @Autowired
    private SuperNotificationIntegrationService superNotificationIntegrationService;

    @Autowired
    private SuperMatchDtoHelper superMatchDtoHelper;

    @Autowired
    private SuperMatchErrorMapper errorMapper;

    private static final Logger logger = LoggerFactory.getLogger(SuperMatchDtoServiceImpl.class);

    /**
     * Gets the super match fund details for an account
     *
     * @param superMatchDtoKey - Super match dto key
     * @param serviceErrors    - Object to capture service errors
     */
    @Override
    public SuperMatchDto find(SuperMatchDtoKey superMatchDtoKey, ServiceErrors serviceErrors) {
        final Client client = superMatchDtoHelper.getClient(serviceErrors);
        final String customerId = ((IndividualDetail) client).getCISKey().getId();

        final ServiceErrors domainErrors = new ServiceErrorsImpl();
        final SuperFundAccount superFundAccount = superMatchDtoHelper.getSuperFundAccount(customerId, superMatchDtoKey, serviceErrors);
        final List<SuperMatchDetails> superMatchDetails = superMatchIntegrationService.retrieveSuperDetails(customerId, superFundAccount, domainErrors);

        return processResponse(new SuperMatchDto(superMatchDtoKey), superMatchDetails, domainErrors);
    }

    /**
     * Updates the Supermatch details - consent,acknowledgement,rollover funds, member details
     *
     * @param requestSuperMatchDto - {@link SuperMatchDto} with the request values
     * @param serviceErrors        - Object to capture service errors
     */
    @Override
    public SuperMatchDto update(SuperMatchDto requestSuperMatchDto, ServiceErrors serviceErrors) {
        final UpdateType updateType = requestSuperMatchDto.getKey().getUpdateType();
        if (UpdateType.INVALID_INPUT.equals(updateType)) {
            logger.error("Invalid update type in the request: {}", requestSuperMatchDto.getKey().getUpdateTypeId());
            return null;
        }

        final Client client = superMatchDtoHelper.getClient(serviceErrors);
        final String customerId = ((IndividualDetail) client).getCISKey().getId();
        final SuperFundAccount superFundAccount = superMatchDtoHelper.getSuperFundAccount(customerId, requestSuperMatchDto.getKey(), serviceErrors);

        final List<SuperMatchDetails> superMatchDetails = new ArrayList<>();
        final ServiceErrors domainErrors = new ServiceErrorsImpl();

        switch (updateType) {
            case CONSENT:
                superMatchDetails.addAll(superMatchIntegrationService.updateConsentStatus(customerId, superFundAccount,
                        requestSuperMatchDto.isConsentProvided(), domainErrors));
                break;
            case ACKNOWLEDGEMENT:
                superMatchDetails.addAll(superMatchIntegrationService.updateAcknowledgementStatus(customerId, superFundAccount, domainErrors));
                break;
            case ROLLOVER:
                superMatchDetails.addAll(superMatchIntegrationService.updateRollOverStatus(customerId, superFundAccount,
                        superMatchDtoHelper.createUpdateRollOverRequest(requestSuperMatchDto), domainErrors));
                break;
            case CREATE_MEMBER:
                superMatchDtoHelper.setMemberDetails(superFundAccount, client, null);
                superMatchIntegrationService.createMember(customerId, superFundAccount, domainErrors);
                break;
            default:
                break;
        }

        return processResponse(requestSuperMatchDto, superMatchDetails, domainErrors);
    }

    private SuperMatchDto processResponse(SuperMatchDto requestSuperMatchDto, List<SuperMatchDetails> superMatchDetails, ServiceErrors domainErrors) {
        final SuperMatchDto superMatchDto = SuperMatchDtoConverter.convertToDto(requestSuperMatchDto.getKey(), superMatchDetails);
        superMatchDto.setErrors(errorMapper.map(domainErrors.getErrorList()));
        return superMatchDto;
    }

    /**
     * Triggers a request to send the SG(Super Guarantee) letter to the user
     *
     * @param accountId     - current account identifier
     * @param emailAddress  - email address to send the SG letter to
     * @param serviceErrors - Object to capture service errors
     */
    @Override
    public boolean notifyCustomer(String accountId, String emailAddress, ServiceErrors serviceErrors) {
        final Client client = superMatchDtoHelper.getClient(serviceErrors);
        final String customerId = ((IndividualDetail) client).getCISKey().getId();

        final SuperFundAccount superFundAccount = superMatchDtoHelper.getSuperFundAccount(customerId, new SuperMatchDtoKey(accountId), serviceErrors);
        superMatchDtoHelper.setMemberDetails(superFundAccount, client, emailAddress);

        return superNotificationIntegrationService.notifyCustomer(customerId, superFundAccount, serviceErrors);
    }
}