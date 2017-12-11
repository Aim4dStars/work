package com.bt.nextgen.api.uar.service;

import com.bt.nextgen.api.uar.model.UarDetailsDto;

import com.bt.nextgen.api.uar.util.UarFilterUtil;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.uar.UarIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by L069552 on 16/09/2015.
 */
@Service("UarClientService")
public class UarClientDtoServiceImpl implements UarClientDtoService {

    @Autowired
    private UarIntegrationService uarIntegrationService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;


    @Override
      public List <UarDetailsDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        UarFilterUtil uarFilterUtil = new UarFilterUtil(uarIntegrationService, userProfileService, brokerIntegrationService);
        return uarFilterUtil.getUarClients(serviceErrors);
    }

    @Override
    public UarDetailsDto submit(UarDetailsDto userDetailsDto, ServiceErrors serviceErrors) {

        UarFilterUtil uarFilterUtil = new UarFilterUtil(uarIntegrationService, userProfileService, brokerIntegrationService);

        return uarFilterUtil.submitUarRecords(userDetailsDto,serviceErrors);

    }


}
