/**
 * 
 */
package com.bt.nextgen.service.gesb.maintainidvdetail.v5;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.IdentityVerificationAssessment;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.RequestAction;

/**
 * @author L081050
 */
public class MaintainIdvRequest {
    private RequestAction requestAction;

    private IdentityVerificationAssessment identityVerificationAssessment;

    public RequestAction getRequestAction() {
        return requestAction;
    }

    public void setRequestAction(RequestAction requestAction) {
        this.requestAction = requestAction;
    }

    public IdentityVerificationAssessment getIdentityVerificationAssessment() {
        return identityVerificationAssessment;
    }

    public void setIdentityVerificationAssessment(IdentityVerificationAssessment identityVerificationAssessment) {
        this.identityVerificationAssessment = identityVerificationAssessment;
    }
}
