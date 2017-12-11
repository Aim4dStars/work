package com.bt.nextgen.service.integration.staticrole;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;

import java.util.List;
import java.util.Map;

public interface StaticRoleIntegrationService
{
    Map<String, List<FunctionalRole>> loadStaticRoles(ServiceErrors serviceErrors);

    List<FunctionalRole> loadFunctionalRoles(String role, ServiceErrors serviceErrors);

}