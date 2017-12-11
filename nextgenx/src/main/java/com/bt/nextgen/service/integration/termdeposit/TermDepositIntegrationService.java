package com.bt.nextgen.service.integration.termdeposit;

import java.util.List;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.TermDepositInterface;
import com.bt.nextgen.service.integration.broker.BrokerKey;

public interface TermDepositIntegrationService
{
     /**
     * Method to fetch adviser term deposits based on OE id.
     * @param brokerKey: Adviser oe id to pass in the service.
     * @param serviceErrors
     * */
    public List <TermDeposit> loadTermDeposit(BrokerKey brokerKey, ServiceErrors serviceErrors);

    /**
     * Method to fetch adviser term deposits based on OE ids.
     * @param brokerKeys: Adviser oe ids to pass in the service.
     * @param serviceErrors
     * */
    public List <TermDeposit> loadTermDeposit(List<BrokerKey> brokerKeys, ServiceErrors serviceErrors);

	/**
	 * Method to perform termdeposit transaction requests.
	 * @param action :  VALIDATE_ADD_TERM_DEPOSIT, BREAK_TERM_DEPOSIT, UPDATE_TERM_DEPOSIT, ADD_TERM_DEPOSIT
	 * @param request : VALIDATE_ADD_TERM_DEPOSIT(Amount, Asset, CurrencyCode, Portfolio),  BREAK_TERM_DEPOSIT(Asset, Portfolio), 
	 * 					UPDATE_TERM_DEPOSIT(Asset, Portfolio, RenewMode), ADD_TERM_DEPOSIT(Amount, Asset, CurrencyCode, Portfolio)
	 * @param serviceErrors
	 * @return
	 */
	public boolean termDeposit(TermDepositAction action, TermDepositTrxRequest request, ServiceErrors serviceErrors);
	
	/**
	 * Method to validate breaking of a term deposit.
	 * @param request : Asset, Portfolio
	 * @param serviceErrors
	 * @return Withdrawal Interest Rate, Withdrawal Prinicpal, Percent Term Elapsed.
	 */
	public TermDepositTrx validateBreakTermDeposit(TermDepositTrxRequest request, ServiceErrors serviceErrors);

    /**
     * Method to update a term deposit.
     * @param termDepositRequest
     * @param serviceErrors
     * @return boolean.
     */
    boolean updateTermDeposit(TermDepositInterface termDepositRequest, ServiceErrors serviceErrors);

    /**
     * Method to submit breaking of a term deposit.
     * @param request : Asset, Portfolio
     * @param serviceErrors
     * @return Withdrawal Interest Rate, Withdrawal Prinicpal, Percent Term Elapsed.
     */
    public TermDepositTrx submitBreakTermDeposit(TermDepositTrxRequest request, ServiceErrors serviceErrors);
}
