package com.bt.nextgen.service.integration.superannuation.caps;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.superannuation.caps.model.ContributionCaps;
import com.bt.nextgen.service.avaloq.superannuation.caps.service.ContributionCapIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static junit.framework.Assert.assertEquals;


public class ContributionCapsIntegrationServiceTest extends BaseSecureIntegrationTest {
    @Autowired
    ContributionCapIntegrationService contributionCapIntegrationService;


    @Test
    public void testGetContributionCaps() {
        final DateTime financialYearStartDate = new DateTime("2016-07-01T00:00:00+10:00");
        final ContributionCaps cap = contributionCapIntegrationService.getContributionCaps(AccountKey.valueOf("12345"),
                financialYearStartDate, new ServiceErrorsImpl());

        assertEquals(cap.getConcessionalCap(), new BigDecimal("35000"));
        assertEquals(cap.getNonConcessionalCap(), new BigDecimal("180000"));
        assertEquals(cap.getFinancialYearStartDate(), financialYearStartDate);
    }
}