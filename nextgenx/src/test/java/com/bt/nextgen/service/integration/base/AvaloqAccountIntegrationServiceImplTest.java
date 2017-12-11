package com.bt.nextgen.service.integration.base;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by L075207 on 28/09/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class AvaloqAccountIntegrationServiceImplTest {

    @InjectMocks
    private AvaloqAccountIntegrationServiceImpl avaloqAccountIntegrationService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Test
    public void getThirdPartySystemDetails() {
        final WrapAccountDetailImpl account = new WrapAccountDetailImpl();
        final ThirdPartyDetails thirdPartyDetails;
        account.setMigrationSourceId(SystemType.WRAP);
        account.setMigrationDate(new DateTime());
        account.setMigrationKey("M00721465");

        when(accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(account);
        thirdPartyDetails = avaloqAccountIntegrationService.getThirdPartySystemDetails(any(AccountKey.class), any(ServiceErrors.class));
        assertEquals(thirdPartyDetails.getSystemType(), account.getMigrationSourceId());
        assertEquals(thirdPartyDetails.getMigrationKey(), account.getMigrationKey());
        assertEquals(thirdPartyDetails.getMigrationDate(), account.getMigrationDate());
    }


}