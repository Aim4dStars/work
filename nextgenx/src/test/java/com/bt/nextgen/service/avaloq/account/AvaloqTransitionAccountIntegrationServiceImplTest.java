package com.bt.nextgen.service.avaloq.account;

import com.avaloq.abs.bb.fld_def.DateFld;
import com.avaloq.abs.bb.fld_def.IdFld;
import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.TransitionAccountDetailHolder;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.request.AvaloqRequest;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AvaloqTransitionAccountIntegrationServiceImplTest {

    @Mock
    private ServiceErrors serviceErrors = null;

    @Mock
    private AvaloqReportService avaloqService;

    @InjectMocks
    private AvaloqTransitionAccountIntegrationServiceImpl transitionAccountIntegrationService;

    @Captor
    ArgumentCaptor<AvaloqRequest> requestArgument;

    @Captor
    ArgumentCaptor<ServiceErrors> serviceErrorsArgument;

    @Captor
    ArgumentCaptor<Class<TransitionAccountDetailHolder>> classArgumentCaptor;

    private TransitionAccountDetailHolder response;
    private BrokerKey brokerKey;
    private DateTime dateFrom = new DateTime("2015-08-01");
    private DateTime dateTo = new DateTime("2020-12-01");
    private String brokerId = "12345";
    private String symbolicKey = "ABCD.EFG";

    @Before
    public void setUp() throws Exception {
        brokerKey = BrokerKey.valueOf(brokerId);
    }

    @Test
    public void getTransitAccountsByBrokerIdShouldBuildTheRequestProperlyBeforeSendingItToABS() {

        response = transitionAccountIntegrationService.getTransitionAccounts(brokerKey,serviceErrors);
        Mockito.verify(avaloqService).executeReportRequestToDomain(requestArgument.capture(), classArgumentCaptor.capture() , serviceErrorsArgument.capture());
        assertThat(((IdFld)requestArgument.getValue().getRequestObject().getTask().getParamList().getParam().get(0).getVal()).getVal() ,is(brokerId));
        assertThat(requestArgument.getValue().getTemplate().getTemplateName(),is("btfg$ui_trans_bp_list.status"));
        assertThat(requestArgument.getValue().getRequestObject().getTask().getParamList().getParam().size(),is(1));
        assertThat(requestArgument.getValue().getRequestObject().getTask().getParamList().getParam().get(0).getName(),is("avsr_incl_list"));

    }

    @Test
    public void getTransitAccountsByDateRangeShouldBuildTheRequestProperlyBeforeSendingItToABS() {

        response = transitionAccountIntegrationService.getTransitionAccounts(dateFrom,dateTo,serviceErrors);
        Mockito.verify(avaloqService).executeReportRequestToDomain(requestArgument.capture(), classArgumentCaptor.capture() , serviceErrorsArgument.capture());

        assertThat(requestArgument.getValue().getTemplate().getTemplateName(),is("btfg$ui_trans_bp_list.status"));
        assertThat(requestArgument.getValue().getRequestObject().getTask().getParamList().getParam().size(),is(2));
        assertThat(requestArgument.getValue().getRequestObject().getTask().getParamList().getParam().get(0).getName(),is("open_date_from"));
        assertThat(requestArgument.getValue().getRequestObject().getTask().getParamList().getParam().get(1).getName(),is("open_date_to"));

        Date startingDate = ((DateFld)requestArgument.getValue().getRequestObject().getTask().getParamList().getParam().get(0).getVal()).getVal().toGregorianCalendar().getTime();
        DateTime startingDateTime = new DateTime(startingDate);
        assertThat(startingDateTime.toLocalDate(),is(dateFrom.toLocalDate()));
        Date endingDate = ((DateFld)requestArgument.getValue().getRequestObject().getTask().getParamList().getParam().get(1).getVal()).getVal().toGregorianCalendar().getTime();
        DateTime endingDateTime = new DateTime(endingDate);
        assertThat(endingDateTime.toLocalDate(),is(dateTo.toLocalDate()));
    }
    @Test
    public void getTransitAccountsByBrokerIdAndDateRangeShouldBuildTheRequestProperlyBeforeSendingItToABS() {

        response = transitionAccountIntegrationService.getTransitionAccounts(brokerKey,dateFrom,dateTo,serviceErrors);
        Mockito.verify(avaloqService).executeReportRequestToDomain(requestArgument.capture(), classArgumentCaptor.capture() , serviceErrorsArgument.capture());

        assertThat(requestArgument.getValue().getTemplate().getTemplateName(),is("btfg$ui_trans_bp_list.status"));
        assertThat(requestArgument.getValue().getRequestObject().getTask().getParamList().getParam().size(),is(3));
        assertThat(requestArgument.getValue().getRequestObject().getTask().getParamList().getParam().get(0).getName(),is("avsr_incl_list"));
        assertThat(requestArgument.getValue().getRequestObject().getTask().getParamList().getParam().get(1).getName(),is("open_date_from"));
        assertThat(requestArgument.getValue().getRequestObject().getTask().getParamList().getParam().get(2).getName(),is("open_date_to"));

        assertThat(((IdFld)requestArgument.getValue().getRequestObject().getTask().getParamList().getParam().get(0).getVal()).getVal() ,is(brokerId));
        Date startingDate = ((DateFld)requestArgument.getValue().getRequestObject().getTask().getParamList().getParam().get(1).getVal()).getVal().toGregorianCalendar().getTime();
        DateTime startingDateTime = new DateTime(startingDate);
        assertThat(startingDateTime.toLocalDate(),is(dateFrom.toLocalDate()));
        Date endingDate = ((DateFld)requestArgument.getValue().getRequestObject().getTask().getParamList().getParam().get(2).getVal()).getVal().toGregorianCalendar().getTime();
        DateTime endingDateTime = new DateTime(endingDate);
        assertThat(endingDateTime.toLocalDate(),is(dateTo.toLocalDate()));
    }

}