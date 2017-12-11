package com.bt.nextgen.service.prm;

import au.com.westpac.gn.common.xsd.commontypes.v3.Event;
import au.com.westpac.gn.riskmanagement.services.riskmonitoring.xsd.notifyeventforfraudassessment.v1.svc0525.NotifyEventForFraudAssessmentRequest;
import com.aciworldwide.risk.gateway.formatter.xml.gesb.NonFinTran;
import com.aciworldwide.risk.gateway.formatter.xml.gesb.XFRqst;
import com.bt.nextgen.service.prm.pojo.PrmDto;
import com.bt.nextgen.service.prm.pojo.PrmEventType;
import com.bt.nextgen.service.prm.util.PrmUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;

import static com.bt.nextgen.service.prm.util.PrmUtil.getGESBRequest;

/**
 * Created by l081361 on 6/10/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class PrmUtilTest {

    private PrmDto prmDto = new PrmDto();
    private PrmDto prmDtoCustomer = new PrmDto();

    @Before
    public void setup() {
        prmDto.setMsgRcrdSrcId(BigInteger.valueOf(17));
        prmDto.setEventType(PrmEventType.ADDACCOUNT);
        prmDto.setTranCode("111");
        prmDto.setProfileOwner("userProfile");
        prmDto.setClientIp("1:1:1:1");
        prmDto.setBankId("AVL");
        prmDto.setCustomerCisKey("123456");
        prmDto.setUserId("12345");
        prmDto.setSucceded("true");
        prmDto.setRiskMsgType("risk");
        prmDto.setSqncNum("123456789");
        prmDto.setChannelId("avaloq");
        prmDto.setSessionId("session");
        prmDto.setEventInitiatorId("12345");
        prmDto.setEventInitiatorType("adviser");
        prmDto.setUtilString4("4");
        prmDto.setUtilString5("5");
        prmDto.setUtilString6("6");
        prmDto.setUtilString7("7");
        prmDto.setUtilString8("8");
        prmDto.setUtilString9("9");
        prmDto.setUtilString10("10");
        prmDto.setEmployeeIdExtension("WBC");
    }

    @Before
    public void setupPrmCustomer() {
        prmDtoCustomer.setMsgRcrdSrcId(BigInteger.valueOf(17));
        prmDtoCustomer.setEventType(PrmEventType.ADDACCOUNT);
        prmDtoCustomer.setTranCode("111");
        prmDtoCustomer.setProfileOwner("userProfile");
        prmDtoCustomer.setClientIp("1:1:1:1");
        prmDtoCustomer.setBankId("AVL");
        prmDtoCustomer.setCustomerCisKey("123456");
        prmDtoCustomer.setUserId("12345");
        prmDtoCustomer.setSucceded("true");
        prmDtoCustomer.setRiskMsgType("risk");
        prmDtoCustomer.setSqncNum("123456789");
        prmDtoCustomer.setChannelId("avaloq");
        prmDtoCustomer.setSessionId("session");
        prmDtoCustomer.setEventInitiatorId("12345");
        prmDtoCustomer.setEventInitiatorType("customer");
        prmDtoCustomer.setUtilString4("4");
        prmDtoCustomer.setUtilString5("5");
        prmDtoCustomer.setUtilString6("6");
        prmDtoCustomer.setUtilString7("7");
        prmDtoCustomer.setUtilString8("8");
        prmDtoCustomer.setUtilString9("9");
        prmDtoCustomer.setUtilString10("10");
        prmDtoCustomer.setEmployeeIdExtension("WBC");
    }

    @Test
    public void testGetGESBRequest() {
        NotifyEventForFraudAssessmentRequest request = getGESBRequest(prmDto);
        Event event = request.getEvent();
        XFRqst xfrqst = event.getApplicationData().getXFRqst();
        NonFinTran nonFinTranRequestType = xfrqst.getNonFinTran();
        Assert.assertEquals(xfrqst.getMsgProp().getMsgRcrdSrcId(),prmDto.getMsgRcrdSrcId().toString());
        Assert.assertEquals(nonFinTranRequestType.getBankID(),prmDto.getBankId());
        Assert.assertEquals(nonFinTranRequestType.getTranCode(),prmDto.getTranCode());
        ///Assert.assertEquals(nonFinTranRequestType.getProfileOwner(),prmDto.getProfileOwner());
        Assert.assertEquals(nonFinTranRequestType.getClientIP(),prmDto.getClientIp());
        Assert.assertEquals(nonFinTranRequestType.getCommonTransaction().getEMPLID(),"WBC12345");
        Assert.assertEquals(nonFinTranRequestType.getCustString4(),prmDto.getUtilString4());
        Assert.assertEquals(nonFinTranRequestType.getCustString5(),prmDto.getUtilString5());
        Assert.assertEquals(nonFinTranRequestType.getCustString6(),prmDto.getUtilString6());
        Assert.assertEquals(nonFinTranRequestType.getCustString7(),prmDto.getUtilString7());
        Assert.assertEquals(nonFinTranRequestType.getCustString8(),prmDto.getUtilString8());
        Assert.assertEquals(nonFinTranRequestType.getCustString9(),prmDto.getUtilString9());
        Assert.assertEquals(nonFinTranRequestType.getCustString10(),prmDto.getUtilString10());
        Assert.assertEquals(nonFinTranRequestType.getSqncNum(),prmDto.getSqncNum());
        //Assert.assertEquals(nonFinTranRequestType.getCustLargeString1(),prmDto.getSessionId());
        Assert.assertEquals(nonFinTranRequestType.getChannelID(),prmDto.getChannelId());
        Assert.assertEquals(nonFinTranRequestType.getRiskMsgType(),prmDto.getRiskMsgType());
        Assert.assertEquals(nonFinTranRequestType.getCustString1(),prmDto.isSucceded());
        Assert.assertEquals(nonFinTranRequestType.getAcctNum(),prmDto.getCustomerCisKey());
    }

    @Test
    public void testGetGESBRequestForCustomer() {
        NotifyEventForFraudAssessmentRequest request = getGESBRequest(prmDtoCustomer);
        Event event = request.getEvent();
        XFRqst xfrqst = event.getApplicationData().getXFRqst();
        NonFinTran nonFinTranRequestType = xfrqst.getNonFinTran();
        Assert.assertEquals(xfrqst.getMsgProp().getMsgRcrdSrcId(),prmDto.getMsgRcrdSrcId().toString());
        Assert.assertEquals(nonFinTranRequestType.getBankID(),prmDto.getBankId());
        Assert.assertEquals(nonFinTranRequestType.getTranCode(),prmDto.getTranCode());
        ///Assert.assertEquals(nonFinTranRequestType.getProfileOwner(),prmDto.getProfileOwner());
        Assert.assertEquals(nonFinTranRequestType.getClientIP(),prmDto.getClientIp());
        Assert.assertEquals(null, nonFinTranRequestType.getCommonTransaction().getEMPLID());
        Assert.assertEquals(nonFinTranRequestType.getCustString4(),prmDto.getUtilString4());
        Assert.assertEquals(nonFinTranRequestType.getCustString5(),prmDto.getUtilString5());
        Assert.assertEquals(nonFinTranRequestType.getCustString6(),prmDto.getUtilString6());
        Assert.assertEquals(nonFinTranRequestType.getCustString7(),prmDto.getUtilString7());
        Assert.assertEquals(nonFinTranRequestType.getCustString8(),prmDto.getUtilString8());
        Assert.assertEquals(nonFinTranRequestType.getCustString9(),prmDto.getUtilString9());
        Assert.assertEquals(nonFinTranRequestType.getCustString10(),prmDto.getUtilString10());
        Assert.assertEquals(nonFinTranRequestType.getSqncNum(),prmDto.getSqncNum());
        //Assert.assertEquals(nonFinTranRequestType.getCustLargeString1(),prmDto.getSessionId());
        Assert.assertEquals(nonFinTranRequestType.getChannelID(),prmDto.getChannelId());
        Assert.assertEquals(nonFinTranRequestType.getRiskMsgType(),prmDto.getRiskMsgType());
        Assert.assertEquals(nonFinTranRequestType.getCustString1(),prmDto.isSucceded());
        Assert.assertEquals(nonFinTranRequestType.getAcctNum(),prmDto.getCustomerCisKey());
    }

    @Test
    public void testGetTranDateTime() {
        XMLGregorianCalendar tranDateTime =PrmUtil.getTranDateTime();
        Assert.assertEquals(tranDateTime.isValid(),true);
     }

}
