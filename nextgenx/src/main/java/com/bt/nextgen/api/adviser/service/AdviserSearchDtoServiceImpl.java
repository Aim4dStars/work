package com.bt.nextgen.api.adviser.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.adviser.model.AdviserSearchDto;
import com.bt.nextgen.api.adviser.model.AdviserSearchDtoKey;
import com.bt.nextgen.api.adviser.model.EntityTypeEnum;
import com.bt.nextgen.api.util.SearchUtil;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.NotAllowedException;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerRole;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.broker.BrokerWrapper;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class AdviserSearchDtoServiceImpl implements AdviserSearchDtoService
{
    @Autowired
    private BrokerIntegrationService brokerService;

    @Autowired
    private StaticIntegrationService staticService;

    @Autowired
    private UserProfileService profileService;

    private static final Logger logger = LoggerFactory.getLogger(AdviserSearchDtoServiceImpl.class);


    @Override
    public List<AdviserSearchDto> search(AdviserSearchDtoKey key, ServiceErrors serviceErrors)
    {
        List<AdviserSearchDto> adviserList = new ArrayList<>();
        Collection<BrokerIdentifier> brokerList = brokerService.getAdvisersForUser(profileService.getActiveProfile(),
            serviceErrors);
        if (brokerList != null && brokerList.size() > 0)
        {
            adviserList.addAll(toAdviserSearchDto(brokerList, key, serviceErrors));
        }
        return adviserList;
    }

    private List<AdviserSearchDto> toAdviserSearchDto(Collection<BrokerIdentifier> brokerList, AdviserSearchDtoKey key,
                                                      ServiceErrors serviceErrors) {
        List<AdviserSearchDto> adviserList = new ArrayList<>();
        List<BrokerKey> brokerKeys = Lambda.extract(brokerList, Lambda.on(BrokerIdentifier.class).getKey());
        Map<BrokerKey, BrokerWrapper> brokerWrapperMap = brokerService.getAdviserBrokerUser(brokerKeys, serviceErrors);
        if(null != brokerWrapperMap){
            for (Map.Entry brokerWrapper : brokerWrapperMap.entrySet()) {
                BrokerUser brokerUser = ((BrokerWrapper) brokerWrapper.getValue()).getBrokerUser();
                try {
                    adviserList.add(toAdviserSearchDto(serviceErrors, brokerUser));
                } catch (IllegalArgumentException | IllegalStateException ex) {
                    logger.error("BrokerKey not mapped to unique Adviser BrokerUser: " + ex.getMessage(), ex);
                }
            }
        }
        return filter(adviserList, key);
    }

    private AdviserSearchDto toAdviserSearchDto(ServiceErrors serviceErrors, BrokerUser brokerUser) {
        AdviserSearchDto adviser = new AdviserSearchDto();
        adviser.setFullName(brokerUser.getFirstName() + " " + brokerUser.getLastName());
        adviser.setFirstName(brokerUser.getFirstName());
        adviser.setLastName(brokerUser.getLastName());
        adviser.setCity(brokerUser.getAddresses() != null ? getCity(brokerUser.getAddresses()) : "");
        adviser.setState(brokerUser.getAddresses() != null ? getState(brokerUser.getAddresses()) : "");
        adviser.setPracticeName(getPracticeName(brokerUser, serviceErrors));
        adviser.setAdviserPositionId(EncodedString.fromPlainText(getPositionId(brokerUser)).toString());
        return adviser;
    }

    private List<AdviserSearchDto> filter(List<AdviserSearchDto> adviserList, AdviserSearchDtoKey key)
    {
        for (Iterator<AdviserSearchDto> iterator = adviserList.iterator(); iterator.hasNext(); )
        {
            AdviserSearchDto adviser = iterator.next();
            if (!SearchUtil.matches(SearchUtil.getPattern(key.getSearch()), adviser.getFullName()))
            {
                iterator.remove();
            }
        }
        return adviserList;
    }

    private String getCity(List<Address> addresses)
    {
        for (Address address : addresses)
        {
            if (address.isDomicile())
            {
                return address.getCity();
            }
        }
        return "";
    }

    private String getState(List<Address> addresses)
    {
        for (Address address : addresses)
        {
            if (address.isDomicile())
            {
                return address.getState();
            }
        }
        return "";
    }

    private String getPracticeName(BrokerUser broker, ServiceErrors serviceErrors)
    {
        if (broker != null && StringUtils.isNotBlank(broker.getEntityId()))
        {
            Collection<Code> entities = staticService.loadCodes(CodeCategory.ENTITY_TYPE, serviceErrors);
            for (Code entity : entities)
            {
                if (entity.getUserId().equals(EntityTypeEnum.PRACTICE.name()))
                {
                    if (broker.getEntityId().equals(entity.getCodeId()) && StringUtils.isNotBlank(broker.getPracticeName()))
                    {
                        return broker.getPracticeName();
                    }
                    break;
                }
            }
        }
        return "";
    }

    private String getPositionId(BrokerUser broker)
    {
        if (broker == null)
        {
            return null;
        }
        Collection<BrokerRole> brokerRoles = broker.getRoles();
        if (brokerRoles != null && !brokerRoles.isEmpty())
        {
            for (BrokerRole brokerRole : brokerRoles)
            {
                if (JobRole.ADVISER.equals(brokerRole.getRole()))
                {
                    return brokerRole.getKey().getId();
                }
            }
        }
        return null;
    }

    private boolean canViewAdviserData(final String adviserPositionId){
        List<ApiSearchCriteria> apiSearchCriterias = new ArrayList<>();
        List<AdviserSearchDto> advisersList = search(apiSearchCriterias,new FailFastErrorsImpl());
        AdviserSearchDto matchedAdviser = Lambda.selectFirst(advisersList, new LambdaMatcher<AdviserSearchDto>() {
            @Override
            protected boolean matchesSafely(AdviserSearchDto adviser) {
                return EncodedString.toPlainText(adviser.getAdviserPositionId()).equals(adviserPositionId);
            }
        });
        return matchedAdviser != null;
    }

    public AdviserSearchDto find(AdviserSearchDtoKey key, ServiceErrors serviceErrors)
    {
        String positionId = EncodedString.toPlainText(key.getSearch());
        if(!canViewAdviserData(positionId)) {
            throw new NotAllowedException(ApiVersion.CURRENT_VERSION, "The user cannot view data for this adviser");
        }
        BrokerUser brokerUser = brokerService.getAdviserBrokerUser(BrokerKey.valueOf(positionId), serviceErrors);
        return createAdviserSearchDto(brokerUser, positionId);
    }

    private AdviserSearchDto createAdviserSearchDto(BrokerUser brokerUser, String positionId)
    {
        AdviserSearchDto dto = new AdviserSearchDto();
        dto.setAdviserPositionId(EncodedString.fromPlainText(positionId).toString());
        dto.setFirstName(brokerUser.getFirstName());
        dto.setLastName(brokerUser.getLastName());
        dto.setPracticeName(brokerUser.getPracticeName());
        List<Address> brokerAddresses = brokerUser.getAddresses();
        if (brokerAddresses != null && !brokerAddresses.isEmpty())
        {
            Address address = brokerAddresses.get(0);
            dto.setState(address.getState());
        }
        return dto;
    }

    /**
     * Added a criteria consistent flag to return consistent encoded brokerid/positionid of an adviser
     * @param criteriaList
     * @param serviceErrors
     * @return
     */
    @Override
    public List<AdviserSearchDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        JobProfileIdentifier jobProfile = profileService.getActiveProfile();
        List<AdviserSearchDto> resultList = new ArrayList();
        Collection <BrokerIdentifier> adviserList= brokerService.getAdvisersForUser(jobProfile,serviceErrors);

        String consistentId = "";
        for (ApiSearchCriteria searchCriteria : criteriaList) {
            if (Attribute.CONSISTENT_ID_FLAG.equals(searchCriteria.getProperty())) {
                consistentId = searchCriteria.getValue();
            }
        }

        List<BrokerKey> brokerKeys = Lambda.extract(adviserList, Lambda.on(BrokerIdentifier.class).getKey());
        Map<BrokerKey, BrokerWrapper> brokerWrapperMap = brokerService.getAdviserBrokerUser(brokerKeys, serviceErrors);
        if (null != brokerWrapperMap) {
            for (Map.Entry brokerWrapper : brokerWrapperMap.entrySet()) {
                AdviserSearchDto dto = new AdviserSearchDto();
                BrokerUser adviser = ((BrokerWrapper) brokerWrapper.getValue()).getBrokerUser();
                dto.setFirstName(adviser.getFirstName());
                dto.setLastName(adviser.getLastName());
                if (consistentId.equalsIgnoreCase("true")) {
                    dto.setAdviserPositionId(ConsistentEncodedString.fromPlainText(getPositionId(adviser)).toString());
                } else {
                    dto.setAdviserPositionId(EncodedString.fromPlainText(getPositionId(adviser)).toString());
                }
                List<Address> advsierAddresses = adviser.getAddresses();
                if (CollectionUtils.isNotEmpty(advsierAddresses)) {
                    Address address = advsierAddresses.get(0);
                    dto.setCity(address.getCity());
                    dto.setState(address.getState());
                }
                resultList.add(dto);
            }
        }
        return resultList;
    }
}