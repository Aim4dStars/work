package com.bt.nextgen.payments.service;

import com.bt.nextgen.payments.repository.PayAnyonePayee;

import java.util.List;

public interface PayanyoneAccountService

{
	List <PayAnyonePayee> load(String cashAccountId);

	String add(PayAnyonePayee payanyone);

	String delete(PayAnyonePayee payanyone);

	String update(PayAnyonePayee payanyone);

}
