package com.bt.nextgen.service.avaloq.account;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.TransitionAccountDetailHolder;
import com.bt.nextgen.service.integration.account.TransitionAccountIntegrationService;
import com.bt.nextgen.service.integration.account.TransitionStatus;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class AvaloqTransitionAccountIntegrationTest extends BaseSecureIntegrationTest {

    @Autowired
    @Qualifier("avaloqTransitionAccountIntegrationService")
    private TransitionAccountIntegrationService transitionAccountIntegrationService;

    private ServiceErrors serviceErrors = null;
    private DateTime dateFrom;
    private DateTime dateTo;
    private BrokerKey brokerKey;

    @Before
    public void setUp() throws Exception {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy");
        dateFrom = dtf.parseDateTime("08/01/2015");
        dateTo = dtf.parseDateTime("09/01/2015");
        brokerKey = BrokerKey.valueOf("100747");
    }

    @SecureTestContext(username = "transitionAccounts")
    @Test
    public void testGetTransitionAccountsByBrokerKey() {

        TransitionAccountDetailHolder response = transitionAccountIntegrationService.getTransitionAccounts(brokerKey, serviceErrors);
        assertNotNull(response);
        assertThat(response.getTransitionAccountDetailList().get(0).getBrokerId(), is("100747"));
        assertThat(response.getTransitionAccountDetailList().get(0).getBrokerName(), is("OE Darryl  Gunther (Adviser Position)"));
        assertThat(response.getTransitionAccountDetailList().get(0).getTransitionAccountBPDetailList().get(0).getAccountKey().getId(), is("173133"));
        assertThat(response.getTransitionAccountDetailList().get(0).getTransitionAccountBPDetailList().get(0).getExpectedCashAmount().intValue(), is(50000));
        assertThat(response.getTransitionAccountDetailList().get(0).getTransitionAccountBPDetailList().get(0).getExpectedAssetAmount().intValue(), is(150000));
        assertThat(response.getTransitionAccountDetailList().get(0).getTransitionAccountBPDetailList().get(0).getTransitionStatus(), is(TransitionStatus.INITIATE_TRANSFER));
        assertThat(response.getTransitionAccountDetailList().get(0).getTransitionAccountBPDetailList().get(0).getTransferType(), is("Internal Transfer"));

    }

    @SecureTestContext(username = "transitionAccounts")
    @Test
    public void testGetAccountsByADateRange() {

        TransitionAccountDetailHolder response = transitionAccountIntegrationService.getTransitionAccounts(dateFrom,dateTo, serviceErrors);
        assertNotNull(response);
        assertThat(response.getTransitionAccountDetailList().get(0).getBrokerId(), is("100747"));
        assertThat(response.getTransitionAccountDetailList().get(0).getBrokerName(), is("OE Darryl  Gunther (Adviser Position)"));
        assertThat(response.getTransitionAccountDetailList().get(0).getTransitionAccountBPDetailList().get(0).getAccountKey().getId(), is("173133"));
        assertThat(response.getTransitionAccountDetailList().get(0).getTransitionAccountBPDetailList().get(0).getExpectedCashAmount().intValue(), is(50000));
        assertThat(response.getTransitionAccountDetailList().get(0).getTransitionAccountBPDetailList().get(0).getExpectedAssetAmount().intValue(), is(150000));
        assertThat(response.getTransitionAccountDetailList().get(0).getTransitionAccountBPDetailList().get(0).getTransitionStatus(), is(TransitionStatus.INITIATE_TRANSFER));
        assertThat(response.getTransitionAccountDetailList().get(0).getTransitionAccountBPDetailList().get(0).getTransferType(), is("Internal Transfer"));

    }

    @SecureTestContext(username = "transitionAccounts")
    @Test
    public void testGetAccountsByAllFields() {
        TransitionAccountDetailHolder response = transitionAccountIntegrationService.getTransitionAccounts(brokerKey,dateFrom,dateTo,serviceErrors);
        assertNotNull(response);
        assertThat(response.getTransitionAccountDetailList().get(0).getBrokerId(), is("100747"));
        assertThat(response.getTransitionAccountDetailList().get(0).getBrokerName(), is("OE Darryl  Gunther (Adviser Position)"));
        assertThat(response.getTransitionAccountDetailList().get(0).getTransitionAccountBPDetailList().get(0).getAccountKey().getId(), is("173133"));
        assertThat(response.getTransitionAccountDetailList().get(0).getTransitionAccountBPDetailList().get(0).getExpectedCashAmount().intValue(), is(50000));
        assertThat(response.getTransitionAccountDetailList().get(0).getTransitionAccountBPDetailList().get(0).getExpectedAssetAmount().intValue(), is(150000));
        assertThat(response.getTransitionAccountDetailList().get(0).getTransitionAccountBPDetailList().get(0).getTransitionStatus(), is(TransitionStatus.INITIATE_TRANSFER));
        assertThat(response.getTransitionAccountDetailList().get(0).getTransitionAccountBPDetailList().get(0).getTransferType(), is("Internal Transfer"));

    }

    @SecureTestContext(username = "transitionAccounts")
    @Test
    public void testGetAllTransitionAccounts() {
        ServiceErrors serviceErrors = null;
        TransitionAccountDetailHolder response = transitionAccountIntegrationService.getAllTransitionAccounts(serviceErrors);
        assertNotNull(response);
        assertThat(response.getTransitionAccountDetailList().size(), is(7));
    }

}
