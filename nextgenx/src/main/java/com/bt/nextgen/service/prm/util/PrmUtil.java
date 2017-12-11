package com.bt.nextgen.service.prm.util;

import au.com.westpac.gn.common.xsd.commontypes.v3.Event;
import au.com.westpac.gn.riskmanagement.services.riskmonitoring.xsd.notifyeventforfraudassessment.v1.svc0525.NotifyEventForFraudAssessmentRequest;
import au.com.westpac.gn.utility.xsd.eventheader.v1.*;
import com.aciworldwide.risk.gateway.formatter.xml.ObjectFactory;
import com.aciworldwide.risk.gateway.formatter.xml.gesb.ApplicationData;
import com.aciworldwide.risk.gateway.formatter.xml.gesb.EMPLID;
import com.aciworldwide.risk.gateway.formatter.xml.gesb.MsgProp;
import com.aciworldwide.risk.gateway.formatter.xml.gesb.NonFinTran;
import com.bt.nextgen.service.prm.pojo.PrmDto;
import com.bt.nextgen.service.prm.pojo.PrmEventType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by l081361 on 22/09/2016.
 */
public class PrmUtil {
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger("PrmUtil");

    private final static  String DATEFORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private final static  String CUSTOMER = "CUSTOMER";

    public static XMLGregorianCalendar getTranDateTime() {
        XMLGregorianCalendar tranDateTime = null;
        try {
            Date txnDate=new Date();
            GregorianCalendar calendar = new GregorianCalendar();
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
            XMLGregorianCalendar transDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
            String dateString = dateFormat.format(transDate.toGregorianCalendar().getTime());
            txnDate=dateFormat.parse(dateString);
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(txnDate);
           tranDateTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH),transDate.getHour(),transDate.getMinute(),transDate.getSecond(),transDate.getMillisecond(), DatatypeConstants.FIELD_UNDEFINED);

        } catch (DatatypeConfigurationException | ParseException e) {
            logger.error("PrmServiceImpl Date Exception :"+e);
        }

        return tranDateTime;
    }

    public static NotifyEventForFraudAssessmentRequest getGESBRequest( PrmDto prmDto) {
        au.com.westpac.gn.riskmanagement.services.riskmonitoring.xsd.notifyeventforfraudassessment.v1.svc0525.ObjectFactory obf = new au.com.westpac.gn.riskmanagement.services.riskmonitoring.xsd.notifyeventforfraudassessment.v1.svc0525.ObjectFactory();
        NotifyEventForFraudAssessmentRequest requestPayLoad = obf.createNotifyEventForFraudAssessmentRequest();
        Event event = new Event();
        event.setEventHeader(getEventHeader());
        MsgProp msgProp = new MsgProp();
        msgProp.setMsgRcrdSrcId(prmDto.getMsgRcrdSrcId().toString());
        com.aciworldwide.risk.gateway.formatter.xml.gesb.ObjectFactory gesbOf = new com.aciworldwide.risk.gateway.formatter.xml.gesb.ObjectFactory();
        NonFinTran nonFinTranRequestType = gesbOf.createNonFinTran();
        nonFinTranRequestType.setEventDetail1(prmDto.getEventType().getEventTypetext());
        nonFinTranRequestType.setRiskMsgType(prmDto.getRiskMsgType());
        nonFinTranRequestType.setTranCode(prmDto.getTranCode());
        nonFinTranRequestType.setClientIP(prmDto.getClientIp());
        nonFinTranRequestType.setAcctNum(prmDto.getCustomerCisKey());
        nonFinTranRequestType.setSqncNum(prmDto.getSqncNum());
        nonFinTranRequestType.setBankID(prmDto.getBankId());
        nonFinTranRequestType.setChannelID(prmDto.getChannelId());
        nonFinTranRequestType.setCustString1(prmDto.isSucceded());
        nonFinTranRequestType.setCustString2(prmDto.getEventInitiatorType());
        nonFinTranRequestType.setCustString3(prmDto.getUtilString3());
        nonFinTranRequestType.setCustString4(prmDto.getUtilString4());
        nonFinTranRequestType.setCustString5(prmDto.getUtilString5());
        nonFinTranRequestType.setCustString6(prmDto.getUtilString6());
        nonFinTranRequestType.setCustString7(prmDto.getUtilString7());
        nonFinTranRequestType.setCustString8(prmDto.getUtilString8());
        nonFinTranRequestType.setCustString9(prmDto.getUtilString9());
        nonFinTranRequestType.setCustString10(prmDto.getUtilString10());
        nonFinTranRequestType.setTranDateTime(getTranDateTime().toXMLFormat());
        nonFinTranRequestType.setUserAcctId(prmDto.getEventInitiatorId());
        nonFinTranRequestType.setProfileOwner(prmDto.getProfileOwner());
        nonFinTranRequestType.setCustLargeString1(prmDto.getSessionId());
        EMPLID emplid = new EMPLID();
        emplid.setEMPLID(getEmployeeID(prmDto));
        nonFinTranRequestType.setCommonTransaction(emplid);
        com.aciworldwide.risk.gateway.formatter.xml.gesb.XFRqst xfRqst = gesbOf.createXFRqst();
        xfRqst.setNonFinTran(nonFinTranRequestType);
        xfRqst.setMsgProp(msgProp);
        com.aciworldwide.risk.gateway.formatter.xml.gesb.ApplicationData data = new ApplicationData();
        data.setXFRqst(xfRqst);
        xfRqst.setMsgProp(msgProp);
        event.setApplicationData(data);
        requestPayLoad.setEvent(event);
        return requestPayLoad;
    }

    private static String getEmployeeID(PrmDto prmDto) {
        if(!prmDto.getEventInitiatorType().equalsIgnoreCase(CUSTOMER)) {
            return prmDto.getEmployeeIdExtension() + prmDto.getEventInitiatorId();
        }
        return null;
    }

    private static EventHeader getEventHeader() {
        EventHeader eventHeader = new EventHeader();
        EventIdentity eventIdentity = new EventIdentity();
        eventIdentity.setEventHeaderSchemaVersion("1.0");
        eventIdentity.setEventContentType(EventContentType.APPLICATION_XML);
        eventIdentity.setEventName("Android Pay Enrolment");
        eventIdentity.setEventTopic("Non-Value Android Pay Enrolment");
        eventIdentity.setEventId("AL-001.12345678901234567890123456789012.12345678901.123456789");
        EventSource eventSource = new EventSource();
        eventSource.setBrand("WPAC");
        eventSource.setChannelType(ChannelType.ONLINE);
        eventSource.setEventOriginatingSystemId("AL-001");
        eventSource.setProcessOriginatingSystemId("AL-001");
        EventSourceOriginatorType eventSourceOriginatorType = new EventSourceOriginatorType();
        eventSourceOriginatorType.setOriginatorType(OriginatorType.CUSTOMER);
        eventSource.getEventSourceOriginator().add(eventSourceOriginatorType);
        EventSequence eventSequence = new EventSequence();
        eventSequence.setCreationTime(getTranDateTime());
        eventHeader.setEventSequence(eventSequence);
        eventHeader.setEventIdentity(eventIdentity);
        eventHeader.setEventSource(eventSource);
        return eventHeader;
    }

}
