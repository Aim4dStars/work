package com.bt.nextgen.api.subscriptions.util;

import com.bt.nextgen.api.draftaccount.service.OrderType;
import com.bt.nextgen.api.subscriptions.model.Offer;
import com.bt.nextgen.api.subscriptions.model.SubscriptionDto;
import com.bt.nextgen.api.subscriptions.model.WorkFlowStatusDto;
import com.bt.nextgen.api.subscriptions.service.Subscriptions;
import com.bt.nextgen.core.Function;
import com.bt.nextgen.service.avaloq.accountactivation.ApplicationDocumentImpl;
import com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.code.Code;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by L062329 on 22/11/2015.
 */
public class ConvertersTest {

    @Test
    public void testDtoCoverterFromApplication() throws Exception {

    }

    @Test
    public void testDtoConverterFromOffer() throws Exception {
        Offer offer = new Offer(Subscriptions.FA);
        SubscriptionDto dto = Converters.dtoConverterFromOffer().convert(offer);
        Assert.assertEquals(offer.getType().getName(), dto.getServiceName());
        Assert.assertEquals(offer.getType().name(), dto.getServiceType());
    }

    @Test
    public void testSetWorkFlow() throws Exception {
       ApplicationDocumentImpl applicationDocument=new ApplicationDocumentImpl();
        List<Code> codeList = new ArrayList<>();
        codeList.add(new CodeImpl("80000019", "19", "FORM#BTFG$CUSTR: ABR Submitted", "abr_subm"));
        applicationDocument.setOrderType(OrderType.FundAdmin.getOrderType());
        applicationDocument.setBpid(AccountKey.valueOf("2132423423"));
        applicationDocument.setAppNumber("7657657");
        applicationDocument.setAppState(ApplicationStatus.ABR_SUBMITTED);
        SubscriptionDto dto=Converters.setWorkFlow(applicationDocument);
        Assert.assertNotNull(dto);
        Assert.assertEquals(dto.getServiceType(),"FA");
    }

    @Test
    public void testGetWorkFlowFunction() throws Exception {
        Function function = Converters.getWorkFlowFunction("form#btfg$custr_o_fa");
        Assert.assertThat("Subscription ", function.toString(), equalTo("FundAdminWorkFunction"));
        function = Converters.getWorkFlowFunction("not a type");
        Assert.assertThat("Subscription ", function.toString(), equalTo("DefaultWorkflowFunction"));
    }

    @Test
    public void testFundAdminWorkflow() throws Exception {
        SubscriptionDto dto = new SubscriptionDto();
        ApplicationDocument document = new ApplicationDocumentImpl();
        document.setOrderType(Subscriptions.FA.getOrderType());
        document.setAppState(ApplicationStatus.ABR_SUBMITTED);
        Date date = new Date();
        document.setAppSubmitDate(date);
        document.setLastTransactionDate(date);
        dto = Converters.fundAdminWorkflow().apply(dto, document);
        Assert.assertEquals(new DateTime(document.getLastTransactionDate()), dto.getLastUpdateDate());
        List<WorkFlowStatusDto> states = dto.getStates();
        WorkFlowStatusDto statusDto = states.get(0);
        Assert.assertEquals("Application requested", statusDto.getState());
//        Assert.assertEquals(document.getAppSubmitDate().toString(), statusDto.getDate());
    }

    @Test
    public void testDefaultWorkflow() throws Exception {
        SubscriptionDto dto = new SubscriptionDto();
        Function<SubscriptionDto, ApplicationDocument, SubscriptionDto> function = Converters.defaultWorkflow();
        SubscriptionDto actual = function.apply(dto, new ApplicationDocumentImpl());
        Assert.assertThat(actual, equalTo(dto));
    }
}