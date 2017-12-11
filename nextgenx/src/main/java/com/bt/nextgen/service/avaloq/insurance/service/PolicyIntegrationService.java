package com.bt.nextgen.service.avaloq.insurance.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.insurance.model.*;

import java.util.List;

public interface PolicyIntegrationService
{
    /**
     * Retrieve all insurance policies using account number
     *
     * @param accountNumber
     * @param serviceErrors
     * @return
     */
    List<Policy> retrievePoliciesByAccountNumber(String accountNumber, ServiceErrors serviceErrors);

    /**
     * Retrieve a specific policy using a policy number
     *
     * @param policyNumber
     * @param serviceErrors
     * @return
     */
    List<Policy> retrievePolicyByPolicyNumber(String policyNumber, ServiceErrors serviceErrors);

    /**
     * Retrieve f numbers for advisers
     *
     * @param ppid
     * @param serviceErrors
     * @return
     */
    List<PolicyTracking> getFNumbers(String ppid, ServiceErrors serviceErrors);

    /**
     * Retrieve policy summery details for f number
     *
     * @param fNumber
     * @param serviceErrors
     * @return
     */
    List<PolicyTracking> getPoliciesForAdviser(String fNumber, ServiceErrors serviceErrors);

    /**
     * Retrieve policy details for application tracking
     *
     * @param fNumbers
     * @param customerNumber
     * @param serviceErrors
     * @return
     */
    List<PolicyTracking> getPolicyByCustomerNumber(List <String> fNumbers, String customerNumber, ServiceErrors serviceErrors);

    /**
     * Retrieve policy application tracking details
     *
     * @param fNumbers
     * @param serviceErrors
     * @return
     */
    List<PolicyApplications> getRecentLivesInsured(List <String> fNumbers, ServiceErrors serviceErrors);

    /**
     * Retrieve under writing notes
     *
     * @param policyNumber
     * @param serviceErrors
     * @return
     */
    PolicyUnderwriting getUnderwritingNotes(String policyNumber, ServiceErrors serviceErrors);
}