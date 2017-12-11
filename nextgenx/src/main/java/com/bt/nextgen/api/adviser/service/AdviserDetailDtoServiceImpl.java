package com.bt.nextgen.api.adviser.service;

import com.bt.nextgen.api.adviser.model.AdviserDetailDto;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.Person;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import com.btfin.panorama.core.security.integration.domain.IndividualDetail;
import com.bt.nextgen.service.integration.domain.Phone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Impl class to retrieve the Adviser Details from Broker Integration Service
 */
@Service
public class AdviserDetailDtoServiceImpl implements AdviserDetailDtoService
{

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientIntegrationService;

    private static final Logger logger = LoggerFactory.getLogger(AdviserDetailDtoServiceImpl.class);

    @Override
    public AdviserDetailDto find(ClientKey clientKey, ServiceErrors serviceErrors) {

        if (!userProfileService.getActiveProfile().getClientKey().equals(clientKey)) {
            serviceErrors.addError(new ServiceErrorImpl("User does not have permission to view detail"));
            return null;
        }

        Person brokerUser= brokerIntegrationService.getPersonDetailsOfBrokerUser(clientKey, serviceErrors);
        if(null != brokerUser) {

            ClientKey personClientKey = brokerUser.getClientKey();

            ClientDetail clientDetail = clientIntegrationService.loadClientDetails(personClientKey, serviceErrors);

            AdviserDetailDto adviserDetailDto = convertBrokerUsertoAdviserDetailDto(brokerUser, clientDetail, serviceErrors);
            return adviserDetailDto;
        }
      return null;
    }


    private AdviserDetailDto convertBrokerUsertoAdviserDetailDto(Person person, ClientDetail individualDetail, ServiceErrors serviceErrors)
    {
        AdviserDetailDto adviserDetailDto= new AdviserDetailDto();

        if(individualDetail instanceof IndividualDetail ) {
            String personTitle = ((IndividualDetail)individualDetail).getTitle();
            adviserDetailDto.setTitle(personTitle!=null ? personTitle : "");
        }

        StringBuilder fullName= new StringBuilder().append(person.getFirstName()).append(" ").append(person.getMiddleName()).append(" ").append(person.getLastName());
        adviserDetailDto.setFullName(fullName.toString());
        adviserDetailDto.setUserId(person.getBankReferenceId());
        adviserDetailDto.setOpenDate(((BrokerUser)person).getReferenceStartDate());
        List<Phone> listPhone=  person.getPhones();
        if(null != listPhone) {
            for (Phone phone : listPhone) {
                if (phone.getType().equals(AddressMedium.MOBILE_PHONE_PRIMARY))
                    adviserDetailDto.setPrimaryMobilePhone(phone);
                else if (phone.getType().equals(AddressMedium.PERSONAL_TELEPHONE))
                    adviserDetailDto.setHomePhone(phone);
                else if (phone.getType().equals(AddressMedium.BUSINESS_TELEPHONE))
                    adviserDetailDto.setPrimaryBusinessPhone(phone);
            }
        }
        List<Address>listAddress= individualDetail.getAddresses();
        if(null != listAddress) {
            for (Address address : listAddress) {
                //OE- addr_kind_id is avaloq for telling its primary or not. value is 1000
                if (address.isDomicile() && address.getAddressType().equals(AddressMedium.POSTAL)) {
                        adviserDetailDto.setPrimaryAddress(address);
                   }
            }
        }

       List<Email> listEmail=  person.getEmails();
        if(null != listEmail) {
            for (Email email : listEmail) {
                if (email.getType().equals(AddressMedium.EMAIL_PRIMARY)) {
                    adviserDetailDto.setPrimaryEmail(email);
                }
            }
        }
        if(person.getClientKey().getId().equals(userProfileService.getActiveProfile().getClientKey().getId()))
        {
            //TODO - UPS REFACTOR1
                 Broker dealerGroup = userProfileService.getDealerGroupBroker();
                 if(dealerGroup!=null) {
                     adviserDetailDto.setDealerGroupName(dealerGroup.getPositionName());
                 }
                 adviserDetailDto.setUserName(userProfileService.getUsername());
                 logger.debug("User Name in Adviser Impl is : {} ", userProfileService.getUsername());
        }
        return adviserDetailDto;

    }

}
