package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.LoggingConstants;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationSubmitDto;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.PermittedClientApplicationRepository;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.encryption.EncodedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by L070813 on 21/07/2016.
 */
@Service
@Transactional("springJpaTransactionManager")
public class ClientApplicationSubmitDtoServiceImpl implements ClientApplicationSubmitDtoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientApplicationSubmitDtoServiceImpl.class);

    @Autowired
    private PermittedClientApplicationRepository clientApplicationRepository;

    @Autowired
    private ClientApplicationDtoHelperService clientApplicationDtoHelperService;

    @Autowired
    private ClientApplicationDtoConverterService clientApplicationDtoConverterService;

    @Override
    public ClientApplicationSubmitDto submit(ClientApplicationSubmitDto keyedDto, ServiceErrors serviceErrors) {
        ClientApplication draftAccount = clientApplicationRepository.find(keyedDto.getKey().getClientApplicationKey());
        draftAccount.assertCanBeModified();
        clientApplicationDtoHelperService.checkProductIdAndAdviserIdAreAllowedForLoggedInUser(draftAccount.getAdviserPositionId(), draftAccount.getProductId());

        String keyedDtoAdviserId = EncodedString.toPlainText(keyedDto.getAdviserId());
        String keyedDtoProductId = EncodedString.toPlainText(keyedDto.getProductId());
        if(!(keyedDtoAdviserId.equals(draftAccount.getAdviserPositionId()) && keyedDtoProductId.equals(draftAccount.getProductId()))) {
            clientApplicationDtoHelperService.checkProductIdAndAdviserIdAreAllowedForLoggedInUser(keyedDtoAdviserId, keyedDtoProductId);
        }

        LOGGER.info(LoggingConstants.ONBOARDING_SUBMIT + "begin");
        ClientApplicationDto clientApplicationDto = clientApplicationDtoConverterService.convertToMinimalDto(draftAccount);
        clientApplicationDto.setOffline(keyedDto.isOffline());
        clientApplicationDto = clientApplicationDtoHelperService.submitDraftAccount(clientApplicationDto, serviceErrors, draftAccount);
        return new ClientApplicationSubmitDto(clientApplicationDto.getKey());
    }
}
