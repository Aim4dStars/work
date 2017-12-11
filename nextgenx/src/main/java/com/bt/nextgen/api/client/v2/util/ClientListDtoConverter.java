package com.bt.nextgen.api.client.v2.util;

import com.bt.nextgen.api.client.util.DTOConverter;
import com.bt.nextgen.api.client.v2.model.ClientDto;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClientListDtoConverter {
    private Collection<Client> clients;

    public ClientListDtoConverter(Collection<Client> clients) {
        this.clients = clients;
    }

    public Map<ClientKey, ClientDto> convert() {
        final Map<ClientKey, ClientDto> clientDtoMap = new HashMap<>();
        for (Client client : clients) {
            final ClientDto clientDto = toClientDto(client);
            clientDtoMap.put(client.getClientKey(), clientDto);
        }
        return clientDtoMap;
    }

    protected ClientDto toClientDto(Client client) {
        final DTOConverter converter = DTOConverterFactory.getConverter(client);
        return (ClientDto) converter.toDTO(client);
    }

}
