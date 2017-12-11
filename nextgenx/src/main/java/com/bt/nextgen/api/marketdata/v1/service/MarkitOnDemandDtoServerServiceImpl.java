package com.bt.nextgen.api.marketdata.v1.service;

import com.bt.nextgen.api.marketdata.v1.model.ServerUrlDto;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.ServiceErrors;
import org.springframework.stereotype.Service;

/**
 * Created by L070589 on 7/10/2015.
 */
@Service
public class MarkitOnDemandDtoServerServiceImpl implements  MarkitOnDemandDtoServerService{


    private static final String serverUrl= "markit.on.demand.env.url";

    @Override
    public ServerUrlDto findOne(ServiceErrors serviceErrors) {
        String url=Properties.get(serverUrl);
        ServerUrlDto serverUrlDto =new ServerUrlDto();
        serverUrlDto.setServerUrl(url);

        return serverUrlDto ;
    }
}
