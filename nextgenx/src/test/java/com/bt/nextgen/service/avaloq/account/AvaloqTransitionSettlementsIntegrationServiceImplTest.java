package com.bt.nextgen.service.avaloq.account;

import com.avaloq.abs.bb.fld_def.IdFld;
import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.TransitionSettlementsHolder;
import com.bt.nextgen.service.request.AvaloqRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AvaloqTransitionSettlementsIntegrationServiceImplTest {

    @Mock
    private ServiceErrors serviceErrors = null;

    @Mock
    private AvaloqReportService avaloqService;

    @InjectMocks
    private AvaloqTransitionSettlementsIntegrationServiceImpl avaloqTransitionSettlementsIntegrationService;

    @Captor
    ArgumentCaptor<AvaloqRequest> requestArgument;

    @Captor
    ArgumentCaptor<ServiceErrors> serviceErrorsArgument;

    @Captor
    ArgumentCaptor<Class<TransitionSettlementsHolder>> classArgumentCaptor;

    private AccountKey accountKey;
    private String accountId;


    @Before
    public void setUp() throws Exception {
        accountId = "12345";
        accountKey = AccountKey.valueOf(accountId);
    }

    @Test
    public void getAssetTransferStatusShouldUseTheCorrectRequestObjectWhenInvokingABSServices() {

        TransitionSettlementsHolder transitionSettlementsHolder = avaloqTransitionSettlementsIntegrationService.
                getAssetTransferStatus(accountKey, serviceErrors);

        Mockito.verify(avaloqService).executeReportRequestToDomain(requestArgument.capture(),
                classArgumentCaptor.capture() , serviceErrorsArgument.capture());

        assertThat(((IdFld)requestArgument.getValue().getRequestObject().getTask().getParamList().getParam().get(0).getVal()).getVal() ,is(accountId));
        assertThat(requestArgument.getValue().getTemplate().getTemplateName(),is("BTFG$UI_SETTLE_DOC_LIST.BP#TRANS_SETTLE_DOC"));
        assertThat(requestArgument.getValue().getRequestObject().getTask().getParamList().getParam().size(),is(1));
        assertThat(requestArgument.getValue().getRequestObject().getTask().getParamList().getParam().get(0).getName(),is("bp_list_id"));
    }





}