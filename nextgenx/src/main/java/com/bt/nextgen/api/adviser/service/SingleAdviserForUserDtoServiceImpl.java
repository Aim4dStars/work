package com.bt.nextgen.api.adviser.service;

import com.bt.nextgen.api.adviser.model.SingleAdviserForUserDto;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class SingleAdviserForUserDtoServiceImpl implements SingleAdviserForUserDtoService {

    @Autowired
    private BrokerIntegrationService brokerService;

    @Autowired
    private UserProfileService userProfileService;

    private static final Logger logger = LoggerFactory.getLogger(SingleAdviserForUserDtoServiceImpl.class);

    @Override
    public SingleAdviserForUserDto findOne(ServiceErrors serviceErrors) {

        if (userProfileService.isAdviser()) {
            Collection<Broker> brokers = brokerService.getBrokersForJob(userProfileService.getActiveProfile(), serviceErrors);
            for (Broker broker : brokers) {
                if (BrokerType.ADVISER.equals(broker.getBrokerType())) {
                    return createSingleAdviserForUserDto(broker.getKey(), true, serviceErrors);
                }
            }
        }
        UserInformation activeProfile = userProfileService.getActiveProfile();
        Collection<BrokerIdentifier> advisersForUser = brokerService.getAdvisersForUser(activeProfile, serviceErrors);
        if (advisersForUser != null && advisersForUser.size() == 1) {
            BrokerIdentifier foundAdviser = advisersForUser.iterator().next();
            return createSingleAdviserForUserDto(foundAdviser.getKey(), true,serviceErrors);
        }
        return createSingleAdviserForUserDto(null, false, serviceErrors);
    }

    private SingleAdviserForUserDto createSingleAdviserForUserDto(BrokerKey key, boolean hasSingleAdviser, ServiceErrors serviceErrors) {
        SingleAdviserForUserDto dto = new SingleAdviserForUserDto();
        if(hasSingleAdviser) {
            dto.setAdviserPositionId(EncodedString.fromPlainText(key.getId()).toString());
            try {
                BrokerUser brokerUser = brokerService.getAdviserBrokerUser(key, serviceErrors);
                toSingleAdviserForUserDto(dto,brokerUser);
            } catch (IllegalArgumentException|IllegalStateException ex) {
                logger.error("BrokerKey not mapped to unique Adviser BrokerUser: "+ex.getMessage(), ex);
            }
        }
        dto.setSingleAdviser(hasSingleAdviser);
        return dto;
    }

    private SingleAdviserForUserDto  toSingleAdviserForUserDto(SingleAdviserForUserDto singleAdviserForUserDto, BrokerUser brokerUser){
        singleAdviserForUserDto.setFullName(brokerUser.getFirstName() + " " + brokerUser.getLastName());
        singleAdviserForUserDto.setFirstName(brokerUser.getFirstName());
        singleAdviserForUserDto.setLastName(brokerUser.getLastName());
        return singleAdviserForUserDto;
    }
}
