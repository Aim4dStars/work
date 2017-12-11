package com.bt.nextgen.service.avaloq.payeedetails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.account.VersionedObjectIdentifier;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetails;

public class PayeeDetailsConverter
{
	private static final Logger logger = LoggerFactory.getLogger(ContainerTypeConverter.class);

	/**
	 * This method filters the users own money account from the list of linked money accounts (managed portfolio(s) money account). 
	 * @param payee
	 * @return payee
	 */
	public static PayeeDetails setMoneyAccountDetailsAndSequence(PayeeDetails payee)
	{
		if (payee != null && payee.getLinkedCashAccounts().size() > 0)
		{
			for (LinkedCashAccount cashAccount : payee.getLinkedCashAccounts())
			{
				if (null!=cashAccount && null!=cashAccount.getCashAccountType() &&  cashAccount.getCashAccountType().equalsIgnoreCase(ContainerType.DIRECT.getCode()))
				{
					PayeeDetailsImpl payeeDetails = (PayeeDetailsImpl)payee;
					
					//set the money account details
					MoneyAccountIdentifierImpl moneyAccount = new MoneyAccountIdentifierImpl();
					moneyAccount.setMoneyAccountId(cashAccount.getMaccId());
					
					payeeDetails.setMoneyAccountIdentifier(moneyAccount);
					break;
				}
			}
		}
		
		//Setting the VersionedModifierSequence number
		if(payee.getModifierSeqNumber() != null){
			VersionedObjectIdentifier identifier = new VersionedObjectIdentifierImpl();
			identifier.setModificationIdentifier(payee.getModifierSeqNumber());
			((PayeeDetailsImpl)payee).setModifierSequenceNumber(identifier);
		}
		
		return payee;
	}
}
