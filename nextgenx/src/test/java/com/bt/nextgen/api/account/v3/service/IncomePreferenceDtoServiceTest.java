package com.bt.nextgen.api.account.v3.service;

import com.bt.nextgen.api.account.v3.model.IncomePreferenceDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.IncomePreference;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class IncomePreferenceDtoServiceTest {
    @InjectMocks
    private IncomePreferenceDtoServiceImpl incomePrefService;

    @Mock
    private AccountIntegrationService accountIntegrationService;


    @Test
    public void testUpdate_incomePreference() {
        String accId = EncodedString.fromPlainText("accountId").toString();        
        SubAccountKey key = SubAccountKey.valueOf(accId);
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        IncomePreferenceDto dto = new IncomePreferenceDto(key, "REINVEST");
        
        Assert.assertEquals(IncomePreference.REINVEST, dto.getIncomePreference());
        IncomePreferenceDto result = incomePrefService.update(dto, serviceErrors);
        Assert.assertNotNull(result);
        Assert.assertEquals(dto.getIncomePreference(), result.getIncomePreference());
        Assert.assertEquals(dto.getKey(), result.getKey());
    }


}
