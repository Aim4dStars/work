/**
 * 
 */
package com.bt.nextgen.service.group.customer.groupesb.retriveidvdetails.v6;

import java.math.BigInteger;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import au.com.westpac.gn.common.xsd.identifiers.v1.CustomerIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.InvolvedPartyType;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrieveiptoiprelationships.v4.svc0260.InvolvedPartyRelationshipQuery;
import au.com.westpac.gn.utility.xsd.pagination.v1.PaginationInstruction;

/**
 * @author L081050
 */
public class RetriveIDVDtlRequest {
    protected BigInteger numberOfRecords;

    protected String idvId;

    protected InvolvedPartyType involvedPartyType;

    protected List<InvolvedPartyIdentifier> involvedPartyIdentifier;

    protected CustomerIdentifier customerIdentifier;

    public BigInteger getNumberOfRecords() {
        return numberOfRecords;
    }

    public void setNumberOfRecords(BigInteger numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }

    public String getIdvId() {
        return idvId;
    }

    public void setIdvId(String idvId) {
        this.idvId = idvId;
    }

    public InvolvedPartyType getInvolvedPartyType() {
        return involvedPartyType;
    }

    public void setInvolvedPartyType(InvolvedPartyType involvedPartyType) {
        this.involvedPartyType = involvedPartyType;
    }

    public List<InvolvedPartyIdentifier> getInvolvedPartyIdentifier() {
        return involvedPartyIdentifier;
    }

    public void setInvolvedPartyIdentifier(List<InvolvedPartyIdentifier> involvedPartyIdentifier) {
        this.involvedPartyIdentifier = involvedPartyIdentifier;
    }

    public CustomerIdentifier getCustomerIdentifier() {
        return customerIdentifier;
    }

    public void setCustomerIdentifier(CustomerIdentifier customerIdentifier) {
        this.customerIdentifier = customerIdentifier;
    }

}
