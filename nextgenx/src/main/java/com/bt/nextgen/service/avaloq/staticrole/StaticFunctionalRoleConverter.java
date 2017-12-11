package com.bt.nextgen.service.avaloq.staticrole;

import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticFunctionalRoleConverter
{
    private static Logger logger = LoggerFactory.getLogger(StaticFunctionalRoleConverter.class);

    /**
     * This method creates a map of JobRole and FunctionalRole on the basis of service response
     * @param roles
     * @return
     */
    public static Map<String, List<FunctionalRole>> toFunctionalRoleMap(List<StaticFuntionalRole> roles)
    {
        Map<String, List<FunctionalRole>> roleMap = new HashMap<>();

        for (StaticFuntionalRole role : roles)
        {
            try
            {
                roleMap.put(role.getPrimaryRole(), extractFunctionalRole(role.getFunctionRoleList()));
            }
            catch (Exception e)
            {
                logger.warn("Error evaluating role", e);
            }

        }
        return roleMap;
    }

    private static List<FunctionalRole> extractFunctionalRole(List<RoleMapping> mappingList)
    {
        List<FunctionalRole> functionalRole = new ArrayList<>();
        for (RoleMapping mapping : mappingList)
        {
            if (mapping.getRoleName() != null)
            {
                functionalRole.add(mapping.getRoleName());
            }
        }
        return functionalRole;
    }
}
