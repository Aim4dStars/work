/**
 * 
 */
package com.bt.nextgen.service.group.customer.groupesb.maintainarrangementandiparrangementrelationships;

import java.util.List;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrieveiptoiprelationships.v4.svc0260.InvolvedPartyRelationshipQuery;
import au.com.westpac.gn.utility.xsd.pagination.v1.PaginationInstruction;

/**
 * @author L081050
 * 
 */
public class RetriveIPToIPRequest {
	private PaginationInstruction paginationInstruction;

	public PaginationInstruction getPaginationInstruction() {
		return paginationInstruction;
	}

	public void setPaginationInstruction(
			PaginationInstruction paginationInstruction) {
		this.paginationInstruction = paginationInstruction;
	}

	public List<InvolvedPartyRelationshipQuery> getInvolvedPartyRelationshipQuery() {
		return involvedPartyRelationshipQuery;
	}

	public void setInvolvedPartyRelationshipQuery(
			List<InvolvedPartyRelationshipQuery> involvedPartyRelationshipQuery) {
		this.involvedPartyRelationshipQuery = involvedPartyRelationshipQuery;
	}

	private List<InvolvedPartyRelationshipQuery> involvedPartyRelationshipQuery;
}
