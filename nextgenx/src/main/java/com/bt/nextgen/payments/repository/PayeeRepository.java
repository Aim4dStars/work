package com.bt.nextgen.payments.repository;

import java.util.List;

public interface PayeeRepository
{
	Payee load(Long id);

	List <Payee> loadAll(String cashAccountId);

	void save(Payee payee);

	void delete(Long id);

	Payee find(Payee payee);

	List <PayAnyonePayee> loadAllPayanyone(String cashAccountId);

	PayAnyonePayee findPayanyone(String cashAccountId, String accountNumber);
	
	PayAnyonePayee findPayanyone(Bsb bsb, String accountNumber);

	int deletePayAnyone(PayAnyonePayee payee);

	int updatePayanyone(PayAnyonePayee payee);
	
	Payee update(Long payeeId, String nickname);
		
	List<LinkedAccount> loadAllLinkedAccount(String cashAccountId);
	
	Payee updateLinkedAccountType(String cashAccountId, String payeeId);

}
