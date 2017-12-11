/**
 * 
 */
package com.bt.nextgen.service.groupesb.createorganisationip.v5;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.Organisation;
import au.com.westpac.gn.utility.xsd.pagination.v1.PaginationInstruction;

/**
 * @author L081050
 * 
 */
public class CreateOrganisationIPReq {
	private PaginationInstruction paginationInstruction;

	public PaginationInstruction getPaginationInstruction() {
		return paginationInstruction;
	}

	public void setPaginationInstruction(
			PaginationInstruction paginationInstruction) {
		this.paginationInstruction = paginationInstruction;
	}

	
	private Organisation oragnaisation;

	public Organisation getOragnaisation() {
		return oragnaisation;
	}

	public void setOragnaisation(Organisation oragnaisation) {
		this.oragnaisation = oragnaisation;
	}
	
	
	
	
}
