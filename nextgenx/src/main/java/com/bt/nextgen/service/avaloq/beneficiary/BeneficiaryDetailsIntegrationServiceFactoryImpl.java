package com.bt.nextgen.service.avaloq.beneficiary;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created by L067218 on 16/08/2016.
 */
@Service
public class BeneficiaryDetailsIntegrationServiceFactoryImpl implements BeneficiaryDetailsIntegrationServiceFactory {

    @Autowired
    @Qualifier("CacheBeneficiaryDetailsIntegrationServiceImpl")
    private BeneficiaryDetailsIntegrationService cacheBeneficiaryDetailsIntegrationServiceImpl;

    @Autowired
    @Qualifier("BeneficiaryDetailsIntegrationServiceImpl")
    private BeneficiaryDetailsIntegrationService beneficiaryDetailsIntegrationServiceImpl;


    public BeneficiaryDetailsIntegrationService getInstance(String type)
    {
        if (!StringUtils.isEmpty(type) && "CACHE".equalsIgnoreCase(type))
        {
            return cacheBeneficiaryDetailsIntegrationServiceImpl;
        }
        else
        {
            return beneficiaryDetailsIntegrationServiceImpl;
        }
    }
}
