package com.bt.nextgen.api.movemoney.v2.controller;

import com.bt.nextgen.api.movemoney.v2.service.PayeeDtoService;
import com.bt.nextgen.api.movemoney.v2.service.PaymentDtoService;
import com.bt.nextgen.api.movemoney.v2.service.SavedPaymentDtoService;
import com.bt.nextgen.api.movemoney.v2.validation.MovemoneyDtoErrorMapper;
import com.bt.nextgen.api.safi.facade.TwoFactorAuthenticationServiceImpl;
import com.bt.nextgen.cms.service.CmsService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.payments.repository.BsbCodeRepository;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.safi.TwoFactorAuthenticationIntegrationService;
import com.bt.nextgen.service.security.service.TwoFactorAuthenticationHttpRequestValidator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

/**
 * Created by M041926 on 8/12/2016.
 */
public class PaymentApiControllerTestConfig {

    @Bean
    CmsService cmsService() {
        return mock(CmsService.class);
    }

    @Bean
    @Qualifier("avaloqClientIntegrationService")
    ClientIntegrationService clientIntegrationService() {
        return mock(ClientIntegrationService.class);
    }

    @Bean
    PaymentDtoService paymentDtoService() {
        return mock(PaymentDtoService.class);
    }

    @Bean
    SavedPaymentDtoService savedPaymentDtoService() {
        return mock(SavedPaymentDtoService.class);
    }

    @Bean
    PayeeDtoService payeeDtoService() {
        return mock(PayeeDtoService.class);
    }

    @Bean
    TwoFactorAuthenticationServiceImpl twoFactorAuthenticationService() {
        return mock(TwoFactorAuthenticationServiceImpl.class);
    }

    @Bean
    TwoFactorAuthenticationIntegrationService twoFactorAuthenticationIntegrationService() {
        return mock(TwoFactorAuthenticationIntegrationService.class);
    }

    @Bean
    TwoFactorAuthenticationHttpRequestValidator twoFactorAuthenticationHttpRequestValidator() {
        return mock(TwoFactorAuthenticationHttpRequestValidator.class);
    }

    @Bean
    UserProfileService userProfileService() {
        return mock(UserProfileService.class);
    }

    @Bean
    MovemoneyDtoErrorMapper movemoneyDtoErrorMapper() {
        return mock(MovemoneyDtoErrorMapper.class);
    }

    @Bean
    BsbCodeRepository bsbCodeRepository() {
        return mock(BsbCodeRepository.class);
    }

    @Bean
    StaticIntegrationService staticIntegrationService() { return mock(StaticIntegrationService.class); }
}
