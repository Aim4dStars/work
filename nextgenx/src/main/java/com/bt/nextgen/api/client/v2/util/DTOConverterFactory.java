package com.bt.nextgen.api.client.v2.util;

import com.bt.nextgen.api.client.util.DTOConverter;
import com.bt.nextgen.api.client.util.ExistingClientSearchDtoConverter;
import com.bt.nextgen.api.client.util.IndividualDtoConverter;
import com.bt.nextgen.api.client.util.IndividualWithAdvisersDtoConverter;
import com.bt.nextgen.api.client.util.ExistingClientOffThreadDtoConverter;
import com.bt.nextgen.service.avaloq.client.ClientListImpl;
import com.bt.nextgen.service.avaloq.domain.CompanyImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualImpl;
import com.bt.nextgen.service.avaloq.domain.SmsfImpl;
import com.bt.nextgen.service.avaloq.domain.TrustImpl;
import com.bt.nextgen.service.avaloq.domain.existingclient.IndividualWithAccountDataImpl;
import com.bt.nextgen.service.avaloq.domain.existingclient.LegalClient;
import com.btfin.panorama.service.client.dto.account.ClientImpl;
import com.btfin.panorama.service.client.dto.client.ExistingClientImpl;

import java.util.HashMap;
import java.util.Map;

public final class DTOConverterFactory {

    private static Map<Class, DTOConverter> domainConverterMap = new HashMap<>();

    private DTOConverterFactory() {

    }

    static {
        domainConverterMap.put(IndividualImpl.class, new IndividualDtoConverter());
        domainConverterMap.put(SmsfImpl.class, new ClientDtoConverter());
        domainConverterMap.put(TrustImpl.class, new ClientDtoConverter());
        domainConverterMap.put(CompanyImpl.class, new ClientDtoConverter());
        domainConverterMap.put(ClientListImpl.class, new ClientDtoConverter());
        domainConverterMap.put(LegalClient.class, new ExistingClientSearchDtoConverter());
        domainConverterMap.put(IndividualWithAccountDataImpl.class, new IndividualWithAdvisersDtoConverter());

        // For Offthread Implementation
        //TODO: need to the find way to send exact type of Client from client-account service and remove this
        domainConverterMap.put(ClientImpl.class, new ClientOffThreadDtoConverter());
        domainConverterMap.put(ExistingClientImpl.class, new ExistingClientOffThreadDtoConverter());




    }

    public static DTOConverter getConverter(Object domainClazz) {
        return domainConverterMap.get(domainClazz.getClass());
    }
}
