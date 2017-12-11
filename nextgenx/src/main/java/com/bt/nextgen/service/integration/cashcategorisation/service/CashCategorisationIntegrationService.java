package com.bt.nextgen.service.integration.cashcategorisation.service;

import java.util.Date;
import java.util.List;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationAction;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationType;
import com.bt.nextgen.service.integration.cashcategorisation.model.CategorisableCashTransaction;
import com.bt.nextgen.service.integration.cashcategorisation.model.Contribution;

public interface CashCategorisationIntegrationService
{
	/**
	 * Execute Save or update cash categorisation requests.
	 *
	 * @param action
	 * @param cashTransToSplit
	 * @return
	 */
	public TransactionStatus saveOrUpdate(CashCategorisationAction action, CategorisableCashTransaction cashTransToSplit);

	/**
	 * Returns list of categorised contributions for a specific transaction
	 *
	 * @param depositId Deposit id to load categorised contributions for
	 * @param serviceErrors Object where errors will be persisted for caller inspection
	 * @return List of categorised contributions that the deposit is for
	 */
	public List <Contribution> loadCashContributionsForTransaction(String depositId, ServiceErrors serviceErrors);

	/**
	 * Returns a list of all categorised contributions for an account, during a financial year
	 *
	 * @param accountKey account key to retrieve contributions for
	 * @param financialYearDate daterepresenting the financial year to retrieve data for. e.g. for 2015/16, "2015-07-01"
	 * @return List of categorised contributions that the deposit is for
	 * @return List of categorised contributions for the account during the given financial year
	 */
	public List <Contribution> loadCashContributionsForAccount(AccountKey accountKey, Date financialYearDate, CashCategorisationType category, ServiceErrors serviceErrors);
}
