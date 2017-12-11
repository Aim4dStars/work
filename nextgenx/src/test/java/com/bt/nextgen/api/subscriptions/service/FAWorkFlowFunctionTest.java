package com.bt.nextgen.api.subscriptions.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.subscriptions.model.SubscriptionDto;
import com.bt.nextgen.api.subscriptions.model.WorkFlowStatusDto;
import com.bt.nextgen.service.avaloq.accountactivation.ApplicationDocumentImpl;
import com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;

/**
 * Created by L081224 on 21/12/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class FAWorkFlowFunctionTest {

    @Mock
    private WorkFlowStatusDto workflowStatusdto;

    @InjectMocks
    private FAWorkFlowFunction faworkflowFunction ;

    private final static String IN_PROGRESS = "In progress";
    private final static String COMPLETE = "Complete";


    @Test
    public void testApply () throws Exception {

        // If Any One of the Date in the states is null then the status will be InProgress
        ApplicationDocumentImpl applicationDocument = new ApplicationDocumentImpl();
        applicationDocument.setAppState(ApplicationStatus.AWAITING_DOCUMENTS);
        SubscriptionDto dto =new SubscriptionDto();
        dto= faworkflowFunction.apply(dto,applicationDocument);

        Assert.assertNotNull(dto.getStates());
        Assert.assertEquals(1, Lambda.select(dto.getStates(), Lambda.having(Lambda.on(WorkFlowStatusDto.class).getStatus(),
                equalTo("Awaiting documents"))).size());

        // To check the special condition of Application Status="Not Applicable"
        ApplicationDocumentImpl applicationDocument1 = new ApplicationDocumentImpl();
        applicationDocument1.setAppSubmitDate(new Date());
        applicationDocument1.setSignedDate(new Date());
        applicationDocument1.setAppState( ApplicationStatus.FA_SUBSCR_NEW_SMSF);
        SubscriptionDto dto1 =new SubscriptionDto();
        FAWorkFlowFunction faworkflowFunction=new FAWorkFlowFunction();
        dto= faworkflowFunction.apply(dto1,applicationDocument1);
        Assert.assertEquals(1, Lambda.select(dto.getStates(), Lambda.having(Lambda.on(WorkFlowStatusDto.class).getStatus(),
                equalTo("Not applicable"))).size());

        //since we have set 2 dates (App Submit Date and signed Date) 2 states will be "Complete"
        Assert.assertEquals(2, Lambda.select(dto.getStates(), Lambda.having(Lambda.on(WorkFlowStatusDto.class).getStatus(),
                equalTo("Complete"))).size());
    }


    @Test
    public void testWorkFlowStatusDto () throws Exception {

      WorkFlowStatusDto workFlowStatusDto=faworkflowFunction.getWorkFlowStatusDto("Application requested",new Date(), "Help-IP-0156");
        Assert.assertNotNull(workFlowStatusDto);
        Assert.assertEquals(workFlowStatusDto.getState(),"Application requested");
        Assert.assertEquals(workFlowStatusDto.getHelpId(),"Help-IP-0156");
    }

    @Test
    public void testWorkFlowStatesOrder () throws Exception {

        ApplicationDocumentImpl applicationDocument = new ApplicationDocumentImpl();
        applicationDocument.setAppState(ApplicationStatus.AWAITING_DOCUMENTS);
        SubscriptionDto dto =new SubscriptionDto();
        dto= faworkflowFunction.apply(dto,applicationDocument);
        Assert.assertNotNull(dto.getStates());
        Assert.assertEquals(dto.getStates().get(0).getState(),"Application requested");
        Assert.assertEquals(dto.getStates().get(1).getState(),"Service agreement received");
        Assert.assertEquals(dto.getStates().get(2).getState(),"Trust deed upgraded (if applicable)");
        Assert.assertEquals(dto.getStates().get(3).getState(),"Fund transferred");
        Assert.assertEquals(dto.getStates().get(4).getState(),"Complete");
    }

    @Test
    public void testTrustDeedInProgress_TransStatus () throws Exception {
        ApplicationDocumentImpl applicationDocument = new ApplicationDocumentImpl();
        applicationDocument.setAppSubmitDate(new Date());
        applicationDocument.setSignedDate(new Date());
        applicationDocument.setTrustDeedDate(null);
        applicationDocument.setTrustFundDate(new Date());
        applicationDocument.setAppState(ApplicationStatus.FA_SUBSCR_TRANS_SMSF);
        SubscriptionDto dto = new SubscriptionDto();
        dto = faworkflowFunction.apply(dto,applicationDocument);
        Assert.assertNotNull(dto.getStates());
        Assert.assertEquals(5, dto.getStates().size());
        WorkFlowStatusDto fundTrans = selectFirst(dto.getStates(), having(on(WorkFlowStatusDto.class).getState(), equalTo("Fund transferred")));
        WorkFlowStatusDto trustDeed = selectFirst(dto.getStates(), having(on(WorkFlowStatusDto.class).getState(), equalTo("Trust deed upgraded (if applicable)")));
        Assert.assertEquals(fundTrans.getStatus(),COMPLETE);
        Assert.assertNotNull(fundTrans.getDate());
        Assert.assertEquals(trustDeed.getStatus(),IN_PROGRESS);
        Assert.assertNull(trustDeed.getDate());
    }

    @Test
    public void testTrustDeedFundTrans_InProgress_TransStatus () throws Exception {
        ApplicationDocumentImpl applicationDocument = new ApplicationDocumentImpl();
        applicationDocument.setAppSubmitDate(new Date());
        applicationDocument.setSignedDate(new Date());
        applicationDocument.setTrustDeedDate(null);
        applicationDocument.setTrustFundDate(null);
        applicationDocument.setAppState(ApplicationStatus.FA_SUBSCR_TRANS_SMSF);
        SubscriptionDto dto = new SubscriptionDto();
        dto = faworkflowFunction.apply(dto,applicationDocument);
        Assert.assertNotNull(dto.getStates());
        Assert.assertEquals(5, dto.getStates().size());
        WorkFlowStatusDto fundTrans = selectFirst(dto.getStates(), having(on(WorkFlowStatusDto.class).getState(), equalTo("Fund transferred")));
        WorkFlowStatusDto trustDeed = selectFirst(dto.getStates(), having(on(WorkFlowStatusDto.class).getState(), equalTo("Trust deed upgraded (if applicable)")));
        Assert.assertEquals(fundTrans.getStatus(),IN_PROGRESS);
        Assert.assertNull(fundTrans.getDate());
        Assert.assertEquals(trustDeed.getStatus(),IN_PROGRESS);
        Assert.assertNull(trustDeed.getDate());
    }

    @Test
    public void testTrustDeedNA_NewProcess_DoneProcDocStatus () throws Exception {
        ApplicationDocumentImpl applicationDocument = new ApplicationDocumentImpl();
        applicationDocument.setAppSubmitDate(new Date());
        applicationDocument.setSignedDate(new Date());
        applicationDocument.setTrustDeedDate(new Date());
        applicationDocument.setTrustFundDate(new Date());
        applicationDocument.setAppState(ApplicationStatus.FA_SUBSCR_NEW_SMSF);
        SubscriptionDto dto = new SubscriptionDto();
        dto = faworkflowFunction.apply(dto,applicationDocument);
        Assert.assertNotNull(dto.getStates());
        Assert.assertEquals(5, dto.getStates().size());
        WorkFlowStatusDto fundTrans = selectFirst(dto.getStates(), having(on(WorkFlowStatusDto.class).getState(), equalTo("Fund transferred")));
        WorkFlowStatusDto trustDeed = selectFirst(dto.getStates(), having(on(WorkFlowStatusDto.class).getState(), equalTo("Trust deed upgraded (if applicable)")));
        Assert.assertEquals(fundTrans.getStatus(),IN_PROGRESS);
        Assert.assertNull(fundTrans.getDate());
        Assert.assertEquals(trustDeed.getStatus(),"Not applicable");
        Assert.assertNull(trustDeed.getDate());
    }

    @Test
    public void testFundTransInProgress_DoneProcDocStatus () throws Exception {
        ApplicationDocumentImpl applicationDocument = new ApplicationDocumentImpl();
        applicationDocument.setAppSubmitDate(new Date());
        applicationDocument.setSignedDate(new Date());
        applicationDocument.setTrustDeedDate(new Date());
        applicationDocument.setTrustFundDate(new Date());
        applicationDocument.setAppState(ApplicationStatus.DONE_GENERATING_DOC);
        SubscriptionDto dto = new SubscriptionDto();
        dto = faworkflowFunction.apply(dto,applicationDocument);
        Assert.assertNotNull(dto.getStates());
        Assert.assertEquals(5, dto.getStates().size());
        WorkFlowStatusDto fundTrans = selectFirst(dto.getStates(), having(on(WorkFlowStatusDto.class).getState(), equalTo("Fund transferred")));
        WorkFlowStatusDto trustDeed = selectFirst(dto.getStates(), having(on(WorkFlowStatusDto.class).getState(), equalTo("Trust deed upgraded (if applicable)")));
        Assert.assertEquals(fundTrans.getStatus(),IN_PROGRESS);
        Assert.assertNull(fundTrans.getDate());
        Assert.assertEquals(trustDeed.getStatus(),COMPLETE);
        Assert.assertNotNull(trustDeed.getDate());
    }

    @Test
    public void testWorkFlow_DoneStatus () throws Exception {
        ApplicationDocumentImpl applicationDocument = new ApplicationDocumentImpl();
        applicationDocument.setAppState(ApplicationStatus.DONE);
        SubscriptionDto dto = new SubscriptionDto();
        dto = faworkflowFunction.apply(dto,applicationDocument);
        Assert.assertNotNull(dto.getStates());
        Assert.assertEquals(dto.getStates().get(0).getState(),"Application requested");
        Assert.assertEquals(dto.getStates().get(1).getState(),"Service agreement received");
        Assert.assertEquals(dto.getStates().get(2).getState(),"Trust deed upgraded (if applicable)");
        Assert.assertEquals(dto.getStates().get(3).getState(),"Fund transferred");
        Assert.assertEquals(dto.getStates().get(4).getState(),"Complete");
        Assert.assertEquals(5, Lambda.select(dto.getStates(), Lambda.having(Lambda.on(WorkFlowStatusDto.class).getStatus(),
                equalTo("Complete"))).size());
    }
}
