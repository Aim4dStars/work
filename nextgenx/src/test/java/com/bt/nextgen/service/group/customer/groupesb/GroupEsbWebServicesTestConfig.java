package com.bt.nextgen.service.group.customer.groupesb;

import com.bt.nextgen.config.ApplicationContextProvider;
import com.bt.nextgen.core.security.profile.InvestorProfileService;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.util.DatabaseManager;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

/**
 * Created by M041926 on 9/06/2016.
 */
public class GroupEsbWebServicesTestConfig {

    @Bean
    public ApplicationContextProvider createApplicationContextProvider() {
        DatabaseManager databaseManager = mock(DatabaseManager.class);
        return new ApplicationContextProvider(databaseManager);
    }

    @Bean
    public FeatureTogglesService createFeatureTogglesService() {
        return mock(FeatureTogglesService.class);
    }

    @Bean(name = "userDetailsService")
    InvestorProfileService getProfileService() {
        return mock(InvestorProfileService.class);

    }
}
