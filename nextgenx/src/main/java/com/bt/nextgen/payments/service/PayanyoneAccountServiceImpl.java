package com.bt.nextgen.payments.service;

import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.payments.repository.PayAnyonePayee;
import com.bt.nextgen.payments.repository.Payee;
import com.bt.nextgen.payments.repository.PayeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayanyoneAccountServiceImpl implements PayanyoneAccountService
{
	private static final Logger logger = LoggerFactory.getLogger(PayanyoneAccountServiceImpl.class);

	//private static String PAYANYONE_ACCOUNT_SERVICE_NAME = "PayAnyOneAccountRequest";

	@Autowired
	private WebServiceProvider provider;
	@Autowired
	private PayeeRepository payeeRepository;

	public List <PayAnyonePayee> load(String cashAccountId)
	{
		logger.trace("Start Of Method :load");
		logger.info("cashAccountId {} ", cashAccountId);

		//fetching values form repository i.e local address book
		List <PayAnyonePayee> payanyoneList = payeeRepository.loadAllPayanyone(cashAccountId);

		//TODO the values are fetched from AVALOQ
		/*List <Payee> payanyoneList = null;
		payanyoneList = new ArrayList <Payee>();
		try
		{
			ObjectFactory of = new ObjectFactory();
			LoadPayanyoneAccountRequest request = of.createLoadPayanyoneAccountRequest();
			request.setCashAccountNumber(new Integer(cashAccountId));
			WebServiceMessageCallback callback = new SoapActionCallback(PAYANYONE_ACCOUNT_SERVICE_NAME);
			LoadPayanyoneListResponse response = (LoadPayanyoneListResponse)provider.getDefaultWebServiceTemplate()
				.marshalSendAndReceive(request, callback);

			List <PayanyoneAccountType> responseList = response.getPayanyoneAccount();

			for (PayanyoneAccountType payAnyOneAccount : responseList)
			{
				PayAnyonePayee domainObject = null;
				domainObject = new PayAnyonePayee();
				//converting response to domain object
				domainObject.setName(payAnyOneAccount.getAccountName());
				domainObject.setNickname(payAnyOneAccount.getNickName());
				domainObject.setAccountNumber(payAnyOneAccount.getAccountNumber());
				domainObject.setPayeeType(PayeeType.PAY_ANYONE);
				domainObject.setBsb(new Bsb(payAnyOneAccount.getBsb()));
				payanyoneList.add(domainObject);
			}

		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			logger.error(exc.getMessage());
		}*/
		logger.trace("End Of Method :load");
		return payanyoneList;//payanyoneList;
	}

	@Override
	public String add(PayAnyonePayee payanyone)
	{
		//to save to local address book, address book
		//Changes as the part of defect:789
		//Payee payee = payeeRepository.findPayanyone(payanyone.getCashAccountId(), payanyone.getAccountNumber());
		Payee payee = payeeRepository.findPayanyone(payanyone.getBsb(), payanyone.getAccountNumber());

		String status = "";
		if (payee == null)
		{
			logger.info("adding Payanyone Account , NickName {}", payanyone.getNickname());
			payeeRepository.save(payanyone);
			status = "SUCCESS";

		}
		else
		{
			logger.info("Payanyone Account already present , NickName {}", payanyone.getNickname());
			status = "FAIL";
		}
		//TODO sending details to add in AVALOQ
		/*ObjectFactory of = new ObjectFactory();
		AddPayanyoneAccountRequest request = of.createAddPayanyoneAccountRequest();
		PayanyoneAccountType payanyoneType = of.createPayanyoneAccountType();
		payanyoneType.setAccountName(payanyone.getName());
		payanyoneType.setAccountNumber(payanyone.getAccountNumber());
		payanyoneType.setBsb(payanyone.getBsb().toString());
		payanyoneType.setNickName(payanyone.getNickname());
		request.setPayanyoneAccount(payanyoneType);
		WebServiceMessageCallback callback = new SoapActionCallback(PAYANYONE_ACCOUNT_SERVICE_NAME);
		AddPayanyoneAccountResponse response = (AddPayanyoneAccountResponse)provider.getDefaultWebServiceTemplate()
			.marshalSendAndReceive(request, callback);
		String succesStatus = response.getSuccess();

		if (succesStatus.equalsIgnoreCase("SUCCESS"))
		{
			status = "SUCCESS";
		}
		else
		{
			status = "FAIL";
		}*/
		return status;
	}

	@Override
	public String delete(PayAnyonePayee payanyone)
	{
		//to save to local address book, address book
		String status = "";
		int deleteCount = payeeRepository.deletePayAnyone(payanyone);

		if (deleteCount > 0)
		{
			logger.info("Deleting Payanyone Account, NickName {}", payanyone.getNickname());
			status = "SUCCESS";
		}
		else
		{
			logger.info("Payanyone Account not present in repository , NickName {}", payanyone.getNickname());
			status = "FAIL";
		}
		//TODO sending details to add in AVALOQ
		/*ObjectFactory of = new ObjectFactory();
		DeletePayanyoneAccountRequest request = of.createDeletePayanyoneAccountRequest();
		PayanyoneAccountType payanyoneType = of.createPayanyoneAccountType();
		payanyoneType.setAccountName(payanyone.getName());
		payanyoneType.setAccountNumber(payanyone.getAccountNumber());
		payanyoneType.setBsb(payanyone.getBsb().toString());
		payanyoneType.setNickName(payanyone.getNickname());
		request.setPayanyoneAccount(payanyoneType);
		WebServiceMessageCallback callback = new SoapActionCallback(PAYANYONE_ACCOUNT_SERVICE_NAME);
		DeletePayanyoneAccountResponse response = (DeletePayanyoneAccountResponse)provider.getDefaultWebServiceTemplate()
			.marshalSendAndReceive(request, callback);
		String succesStatus = response.getSuccess();

		if (succesStatus.equalsIgnoreCase("SUCCESS"))
		{
			status = "SUCCESS";
		}
		else
		{
			status = "FAIL";
		}*/
		return status;
	}

	@Override
	public String update(PayAnyonePayee payanyone)
	{
		//to save to local address book, address book
		int updatecount = payeeRepository.updatePayanyone(payanyone);
		String status = "";
		if (updatecount > 0)
		{
			status = "SUCCESS";
		}
		else
		{
			status = "FAIL";
		}
		//TODO sending details to add in AVALOQ
		/*ObjectFactory of = new ObjectFactory();
		UpdatePayanyoneAccountRequest request = of.createUpdatePayanyoneAccountRequest();
		PayanyoneAccountType payanyoneType = of.createPayanyoneAccountType();
		payanyoneType.setAccountName(payanyone.getName());
		payanyoneType.setAccountNumber(payanyone.getAccountNumber());
		payanyoneType.setBsb(payanyone.getBsb().toString());
		payanyoneType.setNickName(payanyone.getNickname());
		request.setPayanyoneAccount(payanyoneType);
		WebServiceMessageCallback callback = new SoapActionCallback(PAYANYONE_ACCOUNT_SERVICE_NAME);
		UpdatePayanyoneAccountResponse response = (UpdatePayanyoneAccountResponse)provider.getDefaultWebServiceTemplate()
			.marshalSendAndReceive(request, callback);
		String succesStatus = response.getSuccess();

		if (succesStatus.equalsIgnoreCase("SUCCESS"))
		{
			status = "SUCCESS";
		}
		else
		{
			status = "FAIL";
		}*/
		return status;

	}
}
