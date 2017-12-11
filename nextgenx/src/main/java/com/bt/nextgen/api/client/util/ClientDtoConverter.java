package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.client.model.ClientDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.userinformation.Client;

public class ClientDtoConverter implements DTOConverter {

    @Override
    public Object toDTO(Object object) {
        Client client = (Client) object;
        ClientDto clientDto = new ClientDto();
        String clientId = EncodedString.fromPlainText(client.getClientKey().getId()).toString();
        com.bt.nextgen.api.client.model.ClientKey key = new com.bt.nextgen.api.client.model.ClientKey(clientId);
        clientDto.setKey(key);
        clientDto.setFullName(client.getFullName());
        clientDto.setFirstName(client.getFirstName());
        clientDto.setLastName(client.getLastName());
        if (client.getLegalForm() != null && !InvestorType.INDIVIDUAL.equals(client.getLegalForm())) {
            clientDto.setDisplayName(client.getFullName());
        } else {
            clientDto.setDisplayName(client.getLastName() + Constants.COMMA + Constants.SPACE_STRING + client.getFirstName());
        }
        for (Address address : client.getAddresses()) {
            clientDto.setState(address.getState());
            clientDto.setCountry(address.getCountry());
        }
        clientDto.setRegisteredOnline(client.isRegistrationOnline());
        return clientDto;
    }
}
