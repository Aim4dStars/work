package com.bt.nextgen.api.termdeposit.service;

import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorKey;
import com.bt.nextgen.api.termdeposit.model.TermDepositRateDetails;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.integration.broker.Broker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by M044020 on 3/08/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class TermDepositReportServiceImplTest {

    @InjectMocks
    private TermDepositReportServiceImpl reportService;

    @Mock
    private TermDepositRateCalculatorCsvUtils csvUtils;

    @Mock
    private TermDepositRateCalculatorDtoService rateCalculatorDtoService;

    @Mock
    private List<TermDepositInterestRate> termDepositInterestRates;

    @Mock
    private BrokerHelperService brokerHelperService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    private Broker dealerGroup;
    private UserProfile userProfile;
    private Product product;

    @Before
    public void setUp() throws Exception {
        TermDepositRateDetails termDepositRateDetails = new TermDepositRateDetails();
        termDepositRateDetails.setTermDepositInterestRates(termDepositInterestRates);
        when(rateCalculatorDtoService
                .getTermDepositInterestRatesWithBadges(any(TermDepositCalculatorKey.class),any(Set.class),any(ServiceErrors.class)))
                .thenReturn(termDepositRateDetails);
        when(csvUtils.getTermDepositRatesCsv(anyString(), any(List.class))).thenReturn("Successfully generated");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTermDepositRatesAsCsvForAllNull() throws Exception {
        reportService.getTermDepositRatesAsCsv(null, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTermDepositRatesAsCsvForBrandNull() throws Exception {
        ConsistentEncodedString encodedString = ConsistentEncodedString.fromPlainText("121");
        reportService.getTermDepositRatesAsCsv(null, "direct", encodedString.toString(), "123456789");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTermDepositRatesAsCsvForProductNull() throws Exception {
        reportService.getTermDepositRatesAsCsv("10602", "direct", null, "123456789");
    }

    @Test
    public void testGetTermDepositRatesAsCsvForChannelNullWithAccountId() throws Exception {
        product = mock(Product.class);
        when(product.getProductKey()).thenReturn(ProductKey.valueOf("108285"));
        when(product.getProductName()).thenReturn("BT Panorama Investments");
        dealerGroup = mock(Broker.class);
        when(dealerGroup.getPositionName()).thenReturn("BT Invest Dealer group name");
        when(dealerGroup.getDealerKey()).thenReturn(BrokerKey.valueOf("99971"));
        when(userProfileService.getDealerGroupBroker()).thenReturn(dealerGroup);
        ConsistentEncodedString encodedString = ConsistentEncodedString.fromPlainText("1");
        String result = reportService.getTermDepositRatesAsCsv("10602", null, encodedString.toString(), "123456789");
        assertThat(result, is("Successfully generated"));
    }

    @Test
    public void testGetTermDepositRatesAsCsvForDirectChannel() throws Exception {
        product = mock(Product.class);
        when(product.getProductKey()).thenReturn(ProductKey.valueOf("108285"));
        when(product.getProductName()).thenReturn("BT Panorama Investments");
        dealerGroup = mock(Broker.class);
        when(dealerGroup.getPositionName()).thenReturn("BT Invest Dealer group name");
        when(dealerGroup.getDealerKey()).thenReturn(BrokerKey.valueOf("Direct"));
        when(dealerGroup.isDirectInvestment()).thenReturn(true);
        when(brokerHelperService.getDealerGroupsforInvestor(any(ServiceErrors.class))).thenReturn(Collections.singleton(dealerGroup));
        ConsistentEncodedString encodedString = ConsistentEncodedString.fromPlainText("1");
        String result = reportService.getTermDepositRatesAsCsv("10602", "direct", encodedString.toString(), "123456789");
        assertThat(result, is("Successfully generated"));
    }

    @Test
    public void testGetTermDepositRatesAsCsvForAdviser() throws Exception {
        product = mock(Product.class);
        when(product.getProductName()).thenReturn("BT Panorama Investments");
        dealerGroup = mock(Broker.class);
        when(dealerGroup.getPositionName()).thenReturn("BT Invest Dealer group name");
        when(dealerGroup.getDealerKey()).thenReturn(BrokerKey.valueOf("99971"));
        when(userProfileService.getDealerGroupBroker()).thenReturn(dealerGroup);
        String result = reportService.getTermDepositRatesAsCsv("10602", null, EncodedString.fromPlainText("108285").toString(), null);
        assertThat(result, is("Successfully generated"));
    }

    @Test
    public void testGetTermDepositRatesAsCsvForAdviser_ImproperProductId() throws Exception {
        product = mock(Product.class);
        when(product.getProductName()).thenReturn("BT Panorama Investments");
        dealerGroup = mock(Broker.class);
        when(dealerGroup.getPositionName()).thenReturn("BT Invest Dealer group name");
        when(dealerGroup.getDealerKey()).thenReturn(BrokerKey.valueOf("99971"));
        when(userProfileService.getDealerGroupBroker()).thenReturn(dealerGroup);
        String result = reportService.getTermDepositRatesAsCsv("10602", null, EncodedString.toPlainText("F3CD5DF7D0B00AC11A21EAFD7E00126474AA0D4DEB403308").toString(), null);
        assertThat(result, is("Successfully generated"));
    }
}