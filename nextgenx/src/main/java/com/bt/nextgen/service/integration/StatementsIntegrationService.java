package com.bt.nextgen.service.integration;

import com.bt.nextgen.portfolio.web.model.ClientStatementsInterface;
import com.bt.nextgen.service.ServiceErrors;

import ns.btfin_com.sharedservices.bpm.image.imageservice.imagereply.v1_0.SearchImagesResponseMsgType;

/**
 * Provides operation to fetch client statements. Implemented by underlying Image server (Basil, Sybil..).
 * The known implementation {@link ImageServerStatementsService}. 
 *
 * @author Rajeev Kumar
 * @see ImageServerStatementsService
 * @since Cash5
 */
public interface StatementsIntegrationService
{
	
	/**
	 * Fetches client statements(Annual, monthly etc..) from underlying image server.
	 *
	 * @param portfolioId         person/investor portfolio identification
	 * @return ClientStatementsInterface  all the statements 
	 */
	ClientStatementsInterface getStatements(String portfolioId, ServiceErrors serviceErrors);
}
