package com.bt.nextgen.api.subscriptions.service;

import com.bt.nextgen.api.subscriptions.model.SubscriptionDto;
import com.bt.nextgen.api.subscriptions.model.WorkFlowStatusDto;
import com.bt.nextgen.core.Function;
import com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * FA workflow progress and status
 *
 */
public class FAWorkFlowFunction implements Function<SubscriptionDto, ApplicationDocument, SubscriptionDto> {

    private final static String TRUSTEE_DEED = "Trust deed upgraded (if applicable)";
    private final static String SERVICE_AGREEMENT = "Service agreement received";
    private final static String IN_PROGRESS = "In progress";
    private final static String AWAITING_DOCUMENT = "Awaiting documents";
    private final static String COMPLETE = "Complete";

    public void setWorkFlowInitialStepStatus(String uiStatus, WorkFlowStatusDto signedAgreement, WorkFlowStatusDto trustDeed, WorkFlowStatusDto fundTransferred, WorkFlowStatusDto complete) {
        signedAgreement.setStatus(uiStatus);
        trustDeed.setDate(null);
        fundTransferred.setDate(null);
        complete.setDate(null);
    }

    public void setWorkFlowTransStatus(String uiStatus, WorkFlowStatusDto signedAgreement, WorkFlowStatusDto trustDeed, WorkFlowStatusDto fundTransferred, WorkFlowStatusDto complete) {
        signedAgreement.setStatus(COMPLETE);
        String trustDeedDate = trustDeed.getDate();
        String fundTransfDate = fundTransferred.getDate();
        if (trustDeedDate!=null){
            trustDeed.setStatus(COMPLETE);
        }else {
            trustDeed.setStatus(uiStatus);
        }
        if (fundTransfDate!=null && trustDeedDate==null){
            fundTransferred.setStatus(COMPLETE);
        }else {
            fundTransferred.setStatus(uiStatus);
            fundTransferred.setDate(null);
        }
        complete.setDate(null);
    }

    public void setWorkFlowNewProcStatus(String uiStatus, WorkFlowStatusDto signedAgreement, WorkFlowStatusDto trustDeed, WorkFlowStatusDto fundTransferred, WorkFlowStatusDto complete) {
        signedAgreement.setStatus(COMPLETE);
        trustDeed.setStatus("Not applicable");
        trustDeed.setDate(null);
        fundTransferred.setStatus(uiStatus);
        fundTransferred.setDate(null);
        complete.setDate(null);
    }

    public void setWorkFlowDoneGenDocStatus(ApplicationStatus applnStatus, String uiStatus, WorkFlowStatusDto signedAgreement, WorkFlowStatusDto trustDeed, WorkFlowStatusDto fundTransferred, WorkFlowStatusDto complete) {
        signedAgreement.setStatus(COMPLETE);
        trustDeed.setStatus(COMPLETE);
        fundTransferred.setStatus(uiStatus);
        fundTransferred.setDate(null);
        if (ApplicationStatus.FA_SUBSCR_NEW_SMSF.equals(applnStatus)){
            trustDeed.setDate(null);
            trustDeed.setStatus("Not applicable");
        }
        complete.setDate(null);
    }

    public void setWorkFlowDone(WorkFlowStatusDto signedAgreement, WorkFlowStatusDto trustDeed, WorkFlowStatusDto fundTransferred, WorkFlowStatusDto complete) {
        signedAgreement.setStatus(COMPLETE);
        trustDeed.setStatus(COMPLETE);
        fundTransferred.setStatus(COMPLETE);
        complete.setStatus(COMPLETE);
    }

    public void getWFStatus(List<WorkFlowStatusDto> states , ApplicationDocument document) {
        ApplicationStatus status = document.getAppState();
        String uiStatus = getUIStatus(status);

        WorkFlowStatusDto subscribed = getWorkFlowStatusDto("Application requested", document.getAppSubmitDate(), null);
        WorkFlowStatusDto signedAgreement = getWorkFlowStatusDto(SERVICE_AGREEMENT, document.getSignedDate(), null);
        WorkFlowStatusDto trustDeed = getWorkFlowStatusDto(TRUSTEE_DEED, document.getTrustDeedDate(), "Help-IP-0155");
        WorkFlowStatusDto fundTransferred = getWorkFlowStatusDto("Fund transferred", document.getTrustFundDate(), "Help-IP-0156");
        WorkFlowStatusDto complete = getWorkFlowStatusDto(COMPLETE, document.getLastTransactionDate(), null);
        subscribed.setStatus(COMPLETE); //Complete on subscribing, default first step

        if (ApplicationStatus.AWAITING_DOCUMENTS.equals(status) || ApplicationStatus.DOCUMENTS_RECIEVED.equals(status)){
            setWorkFlowInitialStepStatus(uiStatus, signedAgreement, trustDeed, fundTransferred, complete);
        }
        if (ApplicationStatus.FA_SUBSCR_TRANS_SMSF.equals(status)){
            setWorkFlowTransStatus(uiStatus, signedAgreement, trustDeed, fundTransferred, complete);
        }
        if (ApplicationStatus.FA_SUBSCR_NEW_SMSF.equals(status)){
            setWorkFlowNewProcStatus(uiStatus, signedAgreement, trustDeed, fundTransferred, complete);
        }
        if (ApplicationStatus.DONE_GENERATING_DOC.equals(status)){
            setWorkFlowDoneGenDocStatus(document.getLastWFStatus(), uiStatus, signedAgreement, trustDeed, fundTransferred, complete);
        }
        if (ApplicationStatus.DONE.equals(status)){
            setWorkFlowDone(signedAgreement, trustDeed, fundTransferred, complete);
        }
        states.add(subscribed);
        states.add(signedAgreement);
        states.add(trustDeed);
        states.add(fundTransferred);
        states.add(complete);
    }


    @Override
    public SubscriptionDto apply(SubscriptionDto subscriptionDto, ApplicationDocument document) {
        List<WorkFlowStatusDto> states = new ArrayList<>();
        ApplicationStatus status = document.getAppState();
        subscriptionDto.setStatus(status.name());
        subscriptionDto.setLastUpdateDate(new DateTime(document.getLastTransactionDate()));
        //Note: Don't change the sequence of WorkFlowStatusDto object creation it impact the UI
        getWFStatus(states, document);
        subscriptionDto.setStates(states);
        return subscriptionDto;
    }

    public String getUIStatus(ApplicationStatus status) {
        return ApplicationStatus.AWAITING_DOCUMENTS.equals(status) ? AWAITING_DOCUMENT : IN_PROGRESS;
    }


    /**
     * WorkFlowStatusDto builder
     *
     * @param state
     * @param date
     * @param helpId
     * @return
     */
    public WorkFlowStatusDto getWorkFlowStatusDto(String state, Date date, String helpId) {
        WorkFlowStatusDto workFlowStatus = new WorkFlowStatusDto();
        workFlowStatus.setState(state);
        workFlowStatus.setHelpId(helpId);
        if (date != null) {
            workFlowStatus.setDate(new DateTime(date).toString());
        }
        return workFlowStatus;
    }

    @Override
    public String toString() {
        return "FundAdminWorkFunction";
    }
}