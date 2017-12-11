package com.bt.nextgen.service.avaloq.contributionhistory;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.base.AvaloqAccountIntegrationService;
import com.bt.nextgen.service.integration.base.MigrationAttribute;
import com.bt.nextgen.service.integration.base.ThirdPartyDetails;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 *  Created by M044576
 *  This is class created to act as a object factory and produce the object on the basis of different strategy.
 */
@Profile("WrapOffThreadImplementation")
@Service
public class ThirdPartyIntegrationServiceFactoryImpl implements ThirdPartyIntegrationServiceFactory {

    @Autowired
    @Qualifier("ThirdPartyAvaloqAccountIntegrationService")
    private AvaloqAccountIntegrationService avaloqAccountIntegrationService;

    @Resource
    private Map<MigrationAttribute, Map<String, Object>> attributeMap;


    /**
     * Returns the desired object as a response on the basis of attribute, type and account key.
     * @param clazz : The service class you want to cast to.
     * @param migrationAttribute : This the calling attribute used to differentiate between the functionality.
     * @param type : This is another sub attribute you can use to differ in between the object.
     * @param accountKey : This is the key used to fetch the migration details of an account.
     * @param <T>
     * @return
     */
    public <T> T getInstance(Class<T> clazz, MigrationAttribute migrationAttribute, String type, AccountKey accountKey)
    {
        if(migrationAttribute != null)
        {
            return clazz.cast(handlebean(attributeMap.get(migrationAttribute), type, accountKey));
        }
        return null;
    }


    /**
     * Strategy method which handles the bean output on the basis of different conditions
     * @param objectMap
     * @param type
     * @param accountKey
     * @param <T>
     * @return
     */
    private <T> T handlebean(Map<String, Object> objectMap, String type, AccountKey accountKey)
    {
        if (!StringUtils.isEmpty(type) && Attribute.CACHE.equalsIgnoreCase(type))
        {
            return (T) objectMap.get(Attribute.CACHE);
        }
        else if (accountKey != null)
        {
            ThirdPartyDetails thirdPartyDetails = avaloqAccountIntegrationService.getThirdPartySystemDetails(accountKey, new FailFastErrorsImpl());
            if(thirdPartyDetails != null && thirdPartyDetails.getSystemType() != null) {
                return (T) objectMap.get(thirdPartyDetails.getSystemType().getName());
            }
        }
        return (T) objectMap.get(Attribute.DEFAULT);
    }
}
