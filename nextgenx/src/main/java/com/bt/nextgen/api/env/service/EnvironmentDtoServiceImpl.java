package com.bt.nextgen.api.env.service;

import com.bt.nextgen.api.env.model.EnvironmentDto;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.provisio.ProvisioService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class EnvironmentDtoServiceImpl implements EnvironmentDtoService {

    @Autowired
    private BankDateIntegrationService bankDateService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ProvisioService provisioService;

    @Override
    public EnvironmentDto findOne(ServiceErrors serviceErrors) {
        final EnvironmentDto env = new EnvironmentDto();

        env.setEnvironment(Properties.get("environment"));
        env.setLivePersonId(Properties.get("livePerson.id"));
        env.setAddressValidationQasApi(Properties.get("addressValidation.qasApi"));
        env.setAppDynamicsKey(Properties.getString("appDynamics.key"));

        if (!Properties.getBoolean("aem.mock.content") && Properties.getBoolean("aem.useCmsUrl")) {
            env.setCmsHostForAem(Properties.get("aem.cms.url"));
        }

        if (userProfileService.isEmulating()) {
            env.setCmsHostForAem(Properties.get("aem.service.ops.url"));
            env.setProvisioHost(Properties.get("provisio.service.ops.url"));
        }

        env.setBankDate(bankDateService.getBankDate(serviceErrors));
        env.setBankTimeOffsetInMillis(bankDateService.getTime(serviceErrors).getZone().getOffset(0));
        env.setProvisioToken(provisioService.getProvisioToken());
        return env;
    }
}
