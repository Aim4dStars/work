package com.bt.nextgen.service.prm.pojo;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.integration.user.CISKey;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;


/**
 * Created by L081012-Rishi Gupta on 5/02/2016.
 */
public class PrmDto extends BaseDto {

    /** Msg RquId*/
    private String msgRquId;
    /** Msg RcrdSrcId*/
    private BigInteger msgRcrdSrcId;
    /** Event Detail*/
    private String eventDetail;
    /** Risk Msg Type*/
    private String riskMsgType;
    /** Tran Code*/
    private String tranCode;
    /** Customer Cis Key*/
    private String customerCisKey;
    /** User Id*/
    private String userId;
    /** Sqnc Num*/
    private String sqncNum;
    /** Bank ID*/
    private String bankId;
    /** Channel ID*/
    private String channelId;
    /** Session ID*/
    private String sessionId;
    /** Profile Owner*/
    private String profileOwner;
    /** Client IP */
    private String clientIp ;
    /** Event Type*/
    private PrmEventType eventType;
    /** Event Initiator ID */
    private String eventInitiatorId;
    /**Tran Date time of event**/
    private XMLGregorianCalendar tranDateTime;
    /**Is Operation succesful , always set to True **/
    private String isSucceded;
    /**Initiator type : CUSTOMER/STAFF**/
    private String eventInitiatorType;
    /** Payee Name for Add/Update and Delete Payee Events and old limit for Increase Daily Limit event*/
    private String utilString4;
    /** Payee BSB for Add/Update and Delete Payee Events and Reason in case of sign in locked by operator event */
    private String utilString5;
    /** Device type in case of 2fa Event */
    private String utilString6;
    /** Payee Account Number in case of Add/Update/Delete Payee Event */
    private String utilString7;
    /** Device Num in case of 2fa Event */
    private String utilString8;
    /** Device Provisioning Status in case of 2fa Event */
    private String utilString9;
    /** PayeeType in case of Add/Update/Delete Payee event */
    private String utilString10;


    public String getEmployeeIdExtension() { return employeeIdExtension; }

    public void setEmployeeIdExtension(String employeeIdExtension) { this.employeeIdExtension = employeeIdExtension; }

    private String employeeIdExtension;

    private String utilString3;

    public String getUtilString9() {
        return utilString9;
    }

    public void setUtilString9(String utilString9) {
        this.utilString9 = utilString9;
    }

    public String getUtilString10() {
        return utilString10;
    }

    public void setUtilString10(String utilString10) {
        this.utilString10 = utilString10;
    }

    public String getUtilString8() {
        return utilString8;
    }

    public void setUtilString8(String utilString8) {
        this.utilString8 = utilString8;
    }

    public String getUtilString7() {
        return utilString7;
    }

    public void setUtilString7(String utilString7) {
        this.utilString7 = utilString7;
    }

    public String getUtilString4() {
        return utilString4;
    }

    public void setUtilString4(String utilString4) {
        this.utilString4 = utilString4;
    }

    public String getEventInitiatorId() {
        return eventInitiatorId;
    }

    public void setEventInitiatorId(String eventInitiatorId) {
        this.eventInitiatorId = eventInitiatorId;
    }

    public String isSucceded() {
        return isSucceded;
    }

    public void setSucceded(String isSucceded) {
        this.isSucceded = isSucceded;
    }

    public String getEventInitiatorType() {
        return eventInitiatorType;
    }

    public void setEventInitiatorType(String eventInitiatorType) {
        this.eventInitiatorType = eventInitiatorType;
    }

    public XMLGregorianCalendar getTranDateTime() {
        return tranDateTime;
    }

    public void setTranDateTime(XMLGregorianCalendar tranDateTime) {
        this.tranDateTime = tranDateTime;
    }
    public String getUtilString5() {
        return utilString5;
    }

    public void setUtilString5(String utilString5) {
        this.utilString5 = utilString5;
    }

    public String getUtilString3() {
        return utilString3;
    }

    public void setUtilString3(String utilString3) {
        this.utilString3 = utilString3;
    }

    public String getUtilString6() {
        return utilString6;
    }

    public void setUtilString6(String utilString6) {
        this.utilString6 = utilString6;
    }

    public String getChannelId() {
        return channelId;
    }
    public void setChannelId(String channelId) { this.channelId = channelId;}
    public String getBankId() {
        return bankId;
    }
    public void setBankId(String bankId) {
        this.bankId = bankId;
    }
    public String getSqncNum() {return sqncNum;}
    public void setSqncNum(String sqncNum) {
        this.sqncNum = sqncNum;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getCustomerCisKey() {
        return customerCisKey;
    }
    public void setCustomerCisKey(String customerCisKey) {
        this.customerCisKey = customerCisKey;
    }
    public String getTranCode() {
        return tranCode;
    }
    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }
    public String getRiskMsgType() {
        return riskMsgType;
    }
    public void setRiskMsgType(String riskMsgType) {
        this.riskMsgType = riskMsgType;
    }
    public String getEventDetail() {
        return eventDetail;
    }
    public void setEventDetail(String eventDetail) {
        this.eventDetail = eventDetail;
    }
    public BigInteger getMsgRcrdSrcId() {
        return msgRcrdSrcId;
    }
    public void setMsgRcrdSrcId(BigInteger msgRcrdSrcId) {
        this.msgRcrdSrcId = msgRcrdSrcId;
    }
    public String getMsgRquId() {
        return msgRquId;
    }
    public void setMsgRquId(String msgRquId) {this.msgRquId = msgRquId;}
    public PrmEventType getEventType() {
        return eventType;
    }
    public void setEventType(PrmEventType eventType) {
        this.eventType = eventType;
    }
    public String getSessionId() {return sessionId;}
    public void setSessionId(String sessionId) {this.sessionId = sessionId;}
    public String getProfileOwner() {return profileOwner;}
    public void setProfileOwner(String profileOwner) {this.profileOwner = profileOwner;}
    public String getClientIp() {return clientIp;}
    public void setClientIp(String clientIp) {this.clientIp = clientIp;}

    @Override
    /*For Testing until GESB Service is available*/
    public String toString(){
        return "1";
    }
}
