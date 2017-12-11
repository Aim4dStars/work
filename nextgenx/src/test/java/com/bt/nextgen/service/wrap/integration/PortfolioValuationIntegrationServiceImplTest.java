package com.bt.nextgen.service.wrap.integration;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.TermDepositAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.WrapAccountValuationImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.base.AvaloqAccountIntegrationService;
import com.bt.nextgen.service.integration.base.SystemType;
import com.bt.nextgen.service.integration.base.ThirdPartyDetails;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.bt.nextgen.service.wrap.integration.portfolio.WrapPortfolioIntegrationService;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class PortfolioValuationIntegrationServiceImplTest {

    @InjectMocks
    private PortfolioValuationIntegrationServiceImpl portfolioValuationIntegrationService;

    @Mock
    private PortfolioIntegrationService portfolioIntegrationService;

    @Mock
    private WrapPortfolioIntegrationService wrapPortfolioIntegrationService;

    @Mock
    private AvaloqAccountIntegrationService avaloqAccountIntegrationService;

    @Before
    public void setUp() {
        WrapAccountValuationImpl wrapAccountValuation = new WrapAccountValuationImpl();
        WrapAccountValuationImpl avaloqAccountValuation = new WrapAccountValuationImpl();

        List<SubAccountValuation> wrapSubAccountValuations = new ArrayList<>();
        wrapSubAccountValuations.add(new CashAccountValuationImpl());

        List<SubAccountValuation> avaloqSubAccountValuations = new ArrayList<>();
        avaloqSubAccountValuations.add(new TermDepositAccountValuationImpl());

        wrapAccountValuation.setSubAccountValuations(wrapSubAccountValuations);
        avaloqAccountValuation.setSubAccountValuations(avaloqSubAccountValuations);

        Mockito.when(
                portfolioIntegrationService.loadWrapAccountValuation(
                        Mockito.any(AccountKey.class), Mockito.any(DateTime.class), Mockito.any(Boolean.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(avaloqAccountValuation);

        Mockito.when(
                wrapPortfolioIntegrationService.loadWrapAccountValuation(
                        Mockito.any(String.class), Mockito.any(DateTime.class), Mockito.any(Boolean.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(wrapAccountValuation);
    }

    @Test
    public void testLoadWrapAccountValuation_asOfDateBeforeMigrationDate() {
        ThirdPartyDetails thirdPartyDetails = new ThirdPartyDetails();
        thirdPartyDetails.setSystemType(SystemType.WRAP);
        thirdPartyDetails.setMigrationKey("M02744476");
        thirdPartyDetails.setMigrationDate(new DateTime(2017, Calendar.FEBRUARY, 11, 0, 0, 0));
        Mockito.when(avaloqAccountIntegrationService.getThirdPartySystemDetails(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class))).thenReturn(thirdPartyDetails);

        DateTime asOfDate = new DateTime(2017, Calendar.FEBRUARY, 10, 0, 0, 0);
        WrapAccountValuation wrapAccountValuation =
                portfolioValuationIntegrationService.loadWrapAccountValuation(AccountKey.valueOf("23659"), asOfDate, false, new ServiceErrorsImpl());
        Assert.assertNotNull(wrapAccountValuation);
        Assert.assertNotNull(wrapAccountValuation.getSubAccountValuations());
        Assert.assertTrue(wrapAccountValuation.getSubAccountValuations().size() == 1);
    }

    @Test
    public void testLoadWrapAccountValuation_asOfDateAfterMigrationDate() {
        ThirdPartyDetails thirdPartyDetails = new ThirdPartyDetails();
        thirdPartyDetails.setSystemType(SystemType.WRAP);
        thirdPartyDetails.setMigrationKey("M02744476");
        thirdPartyDetails.setMigrationDate(new DateTime(2017, Calendar.FEBRUARY, 11, 0, 0, 0));
        Mockito.when(avaloqAccountIntegrationService.getThirdPartySystemDetails(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class))).thenReturn(thirdPartyDetails);

        DateTime asOfDate = new DateTime(2017, Calendar.FEBRUARY, 13, 0, 0, 0);
        WrapAccountValuation wrapAccountValuation =
                portfolioValuationIntegrationService.loadWrapAccountValuation(AccountKey.valueOf("23659"), asOfDate, false, new ServiceErrorsImpl());
        Assert.assertNotNull(wrapAccountValuation);
        Assert.assertNotNull(wrapAccountValuation.getSubAccountValuations());
        Assert.assertTrue(wrapAccountValuation.getSubAccountValuations().size() == 1);
    }

    @Test
    public void testLoadWrapAccountValuation_asOfDateEqualMigrationDate() {
        ThirdPartyDetails thirdPartyDetails = new ThirdPartyDetails();
        thirdPartyDetails.setSystemType(SystemType.WRAP);
        thirdPartyDetails.setMigrationKey("M02744476");
        thirdPartyDetails.setMigrationDate(new DateTime(2017, Calendar.FEBRUARY, 11, 0, 0, 0));
        Mockito.when(avaloqAccountIntegrationService.getThirdPartySystemDetails(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class))).thenReturn(thirdPartyDetails);

        DateTime asOfDate = new DateTime(2017, Calendar.FEBRUARY, 11, 0, 0, 0);
        WrapAccountValuation wrapAccountValuation =
                portfolioValuationIntegrationService.loadWrapAccountValuation(AccountKey.valueOf("23659"), asOfDate, false, new ServiceErrorsImpl());
        Assert.assertNotNull(wrapAccountValuation);
        Assert.assertNotNull(wrapAccountValuation.getSubAccountValuations());
        Assert.assertTrue(wrapAccountValuation.getSubAccountValuations().size() == 1);
    }

    @Test
    public void testLoadWrapAccountValuation_migrationDateIsNull() {
        ThirdPartyDetails thirdPartyDetails = new ThirdPartyDetails();
        thirdPartyDetails.setSystemType(SystemType.WRAP);
        thirdPartyDetails.setMigrationKey(null);
        thirdPartyDetails.setMigrationDate(null);
        Mockito.when(avaloqAccountIntegrationService.getThirdPartySystemDetails(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class))).thenReturn(thirdPartyDetails);

        DateTime asOfDate = new DateTime(2017, Calendar.FEBRUARY, 11, 0, 0, 0);
        WrapAccountValuation wrapAccountValuation =
                portfolioValuationIntegrationService.loadWrapAccountValuation(AccountKey.valueOf("23659"), asOfDate, false, new ServiceErrorsImpl());
        Assert.assertNotNull(wrapAccountValuation);
        Assert.assertNotNull(wrapAccountValuation.getSubAccountValuations());
        Assert.assertTrue(wrapAccountValuation.getSubAccountValuations().size() == 1);
    }
}
