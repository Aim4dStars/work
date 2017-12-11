package com.bt.nextgen.service.avaloq.staticrole;

import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import com.bt.nextgen.service.integration.userinformation.CacheManagedUserInformationService;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Caching service for the static JobRole to FunctionalRole mapping.
 * @author L054821
 */

@Service
public class CacheManagedStaticRoleService extends AbstractAvaloqIntegrationService
{

    private static final Logger logger = LoggerFactory.getLogger(CacheManagedUserInformationService.class);

    @Autowired
    private AvaloqReportService avaloqService;

      /**
     * This method returns the list of applicable functional roles for the given primary role.
     * This is a cached service and the implementation of BTFG$UI_SEC_USER_LIST.USER_ROLE#FUNCT_ROLE avaloq service
     * @param
     * @return UserInformation
     */
    @Cacheable(key = "#root.target.getSingletonCacheKey()",
            value = "com.bt.nextgen.service.avaloq.userinformation.AvaloqUserInformationService.staticRole")
    public Map<String, List<FunctionalRole>> loadStaticRoles(ServiceErrors serviceErrors)
    {

        AvaloqRequest avaloqReportRequest= new AvaloqReportRequestImpl(StaticRoleEnumTemplate.STATIC_FUNCTIONAL_ROLE);
        avaloqReportRequest=avaloqReportRequest.asApplicationUser();
        StaticRoleHolder staticRole = avaloqService.executeReportRequestToDomain(
                avaloqReportRequest, StaticRoleHolder.class, serviceErrors);
        return StaticFunctionalRoleConverter.toFunctionalRoleMap(staticRole.getStaticFunctionalRoleList());

    }

    /**
     * This method creates the fake cache key
     * @return String
     */
    public String getSingletonCacheKey()
    {
        return "singleton";
    }
}
