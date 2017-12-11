package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.client.model.*;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.avaloq.domain.existingclient.IndividualWithAccountDataImpl;
import com.bt.nextgen.service.avaloq.domain.existingclient.LegalClient;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.IdentityVerificationStatus;
import com.btfin.panorama.service.avaloq.domain.existingclient.Client;
import org.joda.time.format.DateTimeFormat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * Class implemented for OffThread return type.
 */
public class ExistingClientOffThreadDtoConverter implements DTOConverter {

    @Override
    public Object toDTO(Object object) {
        final IndividualWithAccountDataImpl individual = (IndividualWithAccountDataImpl) object;
        if (!individual.getAccountData().isEmpty()){
            toIndividualWithAdvisersDto(individual);
        }
        LegalClient client = (LegalClient) object;
        ExistingClientSearchDto clientDto = new ExistingClientSearchDto();

        if (client.getLegalForm() != null) {
            clientDto.setDisplayName(client.getFullName());
        }
        clientDto.setInvestorType("Legal");

        setCommonClientAttributes(clientDto, client);
        return clientDto;
    }

    protected void setCommonClientAttributes(ExistingClientSearchDto clientDto, Client client) {
        ClientKey key = new ClientKey(EncodedString.fromPlainText(client.getClientKey().getId()).toString());
        clientDto.setKey(key);
        clientDto.setFullName(client.getFullName());

        Address address = client.getAddresses().get(0);
        clientDto.setAddresses(Arrays.asList(addressDto(address)));

        clientDto.setIdVerified(isVerified(client.getIdentityVerificationStatus()));
    }

    private boolean isVerified(IdentityVerificationStatus identityVerificationStatus) {
        return identityVerificationStatus == IdentityVerificationStatus.Completed;
    }
    private AddressDto addressDto(Address address) {
        AddressDto addressDto = new AddressDto();
        addressDto.setSuburb(address.getSuburb());
        addressDto.setState(address.getState());
        addressDto.setPostcode(address.getPostCode());
        addressDto.setCountry(address.getCountry());
        return addressDto;
    }

    public Object toIndividualWithAdvisersDto(IndividualWithAccountDataImpl client) {
        IndividualWithAccountDataImpl individual = (IndividualWithAccountDataImpl) client;
        IndividualWithAdvisersDto individualDto =  new IndividualWithAdvisersDto();

        setCommonClientAttributes(individualDto, individual);

        if (individual.getDateOfBirth() != null) {
            individualDto.setDateOfBirth(individual.getDateOfBirth().toString());
            individualDto.setDateOfBirthForDisplay(DateTimeFormat.forPattern("dd MMM yyyy").print(individual.getDateOfBirth()));
        }

        individualDto.setFirstName(individual.getFirstName());
        individualDto.setLastName(individual.getLastName());
        individualDto.setDisplayName(individualDto.getLastName() + ", " + individualDto.getFirstName());

        individualDto.setInvestorType("Individual");

        Set<String> adviserPositionIds = new HashSet<>();
        adviserPositionIds.addAll(((IndividualWithAccountDataImpl) client).getAdviserPositionIds());
        individualDto.setAdviserPositionIds(adviserPositionIds);
        individualDto.setIndividualInvestor(isIndividualInvestor(individual));
        return individualDto;
    }

    /**
     * This method will check whether the individual is investor based on primary email and mobile.
     * Primary email and mobiles are not captured for shareholder / BO
     * @param individual
     * @return true if the individual is investor and not shareholder / beneficial owner
     */
    private boolean isIndividualInvestor(IndividualWithAccountDataImpl individual) {
        return isNotEmpty(individual.getPrimaryEmail()) && isNotEmpty(individual.getPrimaryMobile());
    }


}
