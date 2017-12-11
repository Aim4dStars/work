package com.bt.nextgen.service.avaloq.staticrole;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import com.bt.nextgen.service.integration.staticrole.StaticRoleIntegrationService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * This service returns the mapping of Avaloq role to applicable Functional roles.
 * @author L054821
 */

@Service
public class StaticRoleIntegrationServiceImpl implements StaticRoleIntegrationService
{
    @Autowired
    CacheManagedStaticRoleService cacheService;

    @Autowired
    private AvaloqGatewayHelperService webserviceClient;

    /**
     * This method returns the mapping of avaloq role to applicable functional roles.
     * This service is the implementation of BTFG$UI_SEC_USER_LIST.USER_ROLE#FUNCT_ROLE avaloq service
     * @return Map of JobRole enum and FunctionalRole enum
     */
    public Map<String, List<FunctionalRole>> loadStaticRoles(ServiceErrors serviceErrors)
    {
        return cacheService.loadStaticRoles(serviceErrors);
    }

    /**
     * This method returns the list of FunctionalRole for the given JobRole.
     *  This service is the implementation of BTFG$UI_SEC_USER_LIST.USER_ROLE#FUNCT_ROLE avaloq service
     * @return List of FunctionalRole
     */
    @Override
    public List<FunctionalRole> loadFunctionalRoles(String role, ServiceErrors serviceErrors)
    {
        Map<String, List<FunctionalRole>> roles = cacheService.loadStaticRoles(serviceErrors);

        return roles.get(role);
    }

}
