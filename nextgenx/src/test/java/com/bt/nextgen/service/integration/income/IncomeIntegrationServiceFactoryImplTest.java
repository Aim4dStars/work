package com.bt.nextgen.service.integration.income;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.income.SubAccountIncomeDetailsImpl;
import com.bt.nextgen.service.avaloq.income.WrapAccountIncomeDetailsImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.base.AvaloqAccountIntegrationService;
import com.bt.nextgen.service.integration.base.ThirdPartyDetails;
import com.bt.nextgen.service.wrap.integration.income.WrapIncomeIntegrationService;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import com.btfin.panorama.service.exception.ServiceException;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by L067221 on 19/09/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class IncomeIntegrationServiceFactoryImplTest {

    @InjectMocks
    IncomeIntegrationServiceFactoryImpl factory;

    @Mock
    private IncomeIntegrationService incomeIntegrationService;

    @Mock
    private WrapIncomeIntegrationService wrapIncomeIntegrationService;

    @Mock
    private AvaloqAccountIntegrationService avaloqAccountIntegrationService;

    private AccountKey accountKey;
    private DateTime startDate;
    private DateTime endDate;
    private ServiceErrorsImpl serviceError;

    @Before
    public void setup() {
        serviceError = new ServiceErrorsImpl();
        List<SubAccountIncomeDetails> wrapSubAccountIncomeDetailsList = new ArrayList<>();
        SubAccountIncomeDetails subAccountIncomeDetails = new SubAccountIncomeDetailsImpl();
        wrapSubAccountIncomeDetailsList.add(subAccountIncomeDetails);


        List<WrapAccountIncomeDetails> wrapAccountIncomeDetailsList = new ArrayList<>();
        WrapAccountIncomeDetails details = new WrapAccountIncomeDetailsImpl();
        ((WrapAccountIncomeDetailsImpl) details).setSubAccountIncomeDetailsList(wrapSubAccountIncomeDetailsList);
        wrapAccountIncomeDetailsList.add(details);

        accountKey = AccountKey.valueOf("A1234");
        when(incomeIntegrationService.loadIncomeReceivedDetails((AccountKey) anyObject(), any(DateTime.class),
                any(DateTime.class), any(ServiceErrors.class))).thenReturn(wrapAccountIncomeDetailsList);

        when(wrapIncomeIntegrationService.loadIncomeReceivedDetails(anyString(),
                any(DateTime.class), any(DateTime.class), any(ServiceErrors.class)))
                .thenReturn(wrapSubAccountIncomeDetailsList);
        ThirdPartyDetails thirdPartyDetails = new ThirdPartyDetails();
        thirdPartyDetails.setMigrationDate(new DateTime("2018-01-01"));
        thirdPartyDetails.setMigrationKey("M00000027");
        when(avaloqAccountIntegrationService.getThirdPartySystemDetails((AccountKey) anyObject(), any(ServiceErrors.class)))
                .thenReturn(thirdPartyDetails);
    }

    @Test
    public void testLoadIncomeReceivedDetails_WhenStartDateAfterMigrationDate() {
        //Retrieve information from Avaloq
        startDate = new DateTime("2019-01-01");
        endDate = new DateTime("2019-01-30");
        List<WrapAccountIncomeDetails> incomeDetailsList = factory.loadIncomeReceivedDetails(accountKey, startDate,
                endDate, serviceError);
        assertNotNull(incomeDetailsList);
        assertThat(incomeDetailsList.size(), equalTo(1));
        assertNotNull(incomeDetailsList.get(0).getSubAccountIncomeDetailsList());
        assertThat(incomeDetailsList.get(0).getSubAccountIncomeDetailsList().size(), equalTo(1));
        verify(incomeIntegrationService, times(1)).loadIncomeReceivedDetails(accountKey, startDate, endDate, serviceError);
        verify(wrapIncomeIntegrationService, never()).loadIncomeReceivedDetails(anyString(), eq(startDate), eq(endDate), eq(serviceError));
    }

    @Test
    public void testLoadIncomeReceivedDetails_WhenStartDateEqualsMigrationDate() {
        //Retrieve information from Avaloq
        startDate = new DateTime("2018-01-01");
        endDate = new DateTime("2019-01-30");
        List<WrapAccountIncomeDetails> incomeDetailsList = factory.loadIncomeReceivedDetails(accountKey, startDate,
                endDate, serviceError);
        assertNotNull(incomeDetailsList);
        assertThat(incomeDetailsList.size(), equalTo(1));
        assertNotNull(incomeDetailsList.get(0).getSubAccountIncomeDetailsList());
        assertThat(incomeDetailsList.get(0).getSubAccountIncomeDetailsList().size(), equalTo(1));
        verify(incomeIntegrationService, times(1)).loadIncomeReceivedDetails(accountKey, startDate, endDate, serviceError);
        verify(wrapIncomeIntegrationService, never()).loadIncomeReceivedDetails(anyString(), eq(startDate), eq(endDate), eq(serviceError));
    }

    @Test
    public void testLoadIncomeReceivedDetails_WhenEndDateEqualsMigrationDate() {
        //Retrieve information from Avaloq and Wrap
        startDate = new DateTime("2017-01-01");
        endDate = new DateTime("2018-01-01");
        List<WrapAccountIncomeDetails> incomeDetailsList = factory.loadIncomeReceivedDetails(accountKey, startDate,
                endDate, serviceError);
        assertNotNull(incomeDetailsList);
        assertThat(incomeDetailsList.size(), equalTo(1));
        assertNotNull(incomeDetailsList.get(0).getSubAccountIncomeDetailsList());
        assertThat(incomeDetailsList.get(0).getSubAccountIncomeDetailsList().size(), equalTo(2));
        verify(incomeIntegrationService, times(1)).loadIncomeReceivedDetails(accountKey, startDate, endDate, serviceError);
        verify(wrapIncomeIntegrationService, times(1)).loadIncomeReceivedDetails(anyString(), eq(startDate), eq(endDate), eq(serviceError));
    }

    @Test
    public void testLoadIncomeReceivedDetails_WhenMigrationDateIsNull() {
        //Retrieve information from Avaloq
        ThirdPartyDetails thirdPartyDetails = new ThirdPartyDetails();
        thirdPartyDetails.setMigrationDate(null);
        thirdPartyDetails.setMigrationKey("M00000027");
        when(avaloqAccountIntegrationService.getThirdPartySystemDetails((AccountKey) anyObject(), any(ServiceErrors.class)))
                .thenReturn(thirdPartyDetails);

        startDate = new DateTime("2019-01-01");
        endDate = new DateTime("2019-01-30");
        List<WrapAccountIncomeDetails> incomeDetailsList = factory.loadIncomeReceivedDetails(accountKey, startDate,
                endDate, serviceError);

        assertNotNull(incomeDetailsList);
        assertThat(incomeDetailsList.size(), equalTo(1));
        assertNotNull(incomeDetailsList.get(0).getSubAccountIncomeDetailsList());
        assertThat(incomeDetailsList.get(0).getSubAccountIncomeDetailsList().size(), equalTo(1));
        verify(incomeIntegrationService, times(1)).loadIncomeReceivedDetails(accountKey, startDate, endDate, serviceError);
        verify(wrapIncomeIntegrationService, never()).loadIncomeReceivedDetails(anyString(), eq(startDate), eq(endDate), eq(serviceError));
    }


    @Test
    public void testLoadIncomeReceivedDetails_WhenEndDateBeforeMigrationDate() {
        //Retrieve information from Wrap
        startDate = new DateTime("2017-01-01");
        endDate = new DateTime("2017-01-30");
        List<WrapAccountIncomeDetails> incomeDetailsList = factory.loadIncomeReceivedDetails(accountKey, startDate,
                endDate, serviceError);
        assertNotNull(incomeDetailsList);
        assertThat(incomeDetailsList.size(), equalTo(1));
        assertNotNull(incomeDetailsList.get(0).getSubAccountIncomeDetailsList());
        assertThat(incomeDetailsList.get(0).getSubAccountIncomeDetailsList().size(), equalTo(1));
        verify(incomeIntegrationService, never()).loadIncomeReceivedDetails(accountKey, startDate, endDate, serviceError);
        verify(wrapIncomeIntegrationService, times(1)).loadIncomeReceivedDetails("M00000027", startDate, endDate, serviceError);
    }


    @Test
    public void testLoadIncomeReceivedDetails_WhenEndDateAfterMigrationDate_AndStartDateBeforeMigrationDate() {
        //Retrieve information from Avaloq and Wrap
        startDate = new DateTime("2017-01-01");
        endDate = new DateTime("2019-01-30");
        List<WrapAccountIncomeDetails> incomeDetailsList = factory.loadIncomeReceivedDetails(accountKey, startDate,
                endDate, serviceError);
        assertNotNull(incomeDetailsList);
        assertThat(incomeDetailsList.size(), equalTo(1));
        assertNotNull(incomeDetailsList.get(0).getSubAccountIncomeDetailsList());
        assertThat(incomeDetailsList.get(0).getSubAccountIncomeDetailsList().size(), equalTo(2));
        verify(incomeIntegrationService, times(1)).loadIncomeReceivedDetails(accountKey, startDate, endDate, serviceError);
        verify(wrapIncomeIntegrationService, times(1)).loadIncomeReceivedDetails("M00000027", startDate, endDate, serviceError);
    }

    @Test(expected = ServiceException.class)
    public void testLoadIncomeReceivedDetails_WhenEndDateAfterMigrationDate_AndStartDateBeforeMigrationDate_throwsException() {
        startDate = new DateTime("2017-01-01");
        endDate = new DateTime("2017-01-30");
        when(wrapIncomeIntegrationService.loadIncomeReceivedDetails(anyString(),
                any(DateTime.class), any(DateTime.class), any(ServiceErrors.class)))
                .thenThrow(new ServiceException("exception"));
        List<WrapAccountIncomeDetails> incomeDetailsList = factory.loadIncomeReceivedDetails(accountKey, startDate,
                    endDate, serviceError);
    }

}
