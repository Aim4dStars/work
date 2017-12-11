package com.bt.nextgen.service.security;

import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.clients.web.model.ClientModel;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.login.service.AccountStatus;
import com.bt.nextgen.login.service.ClientDetail;
import com.bt.nextgen.login.service.RegistrationService;
import com.bt.nextgen.login.web.model.AccountStatusModel;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository("investorOnboardingRepository")
public class InvestorServiceImpl implements InvestorService
{
	@Autowired WebServiceProvider provider;
	
	@Autowired RegistrationService registrationService;
	
	private static final Logger logger = LoggerFactory.getLogger(InvestorServiceImpl.class);

	private static final String testXmlFile = "/webservices/response/AccountActivationResponse.xml";
	
	public boolean isExistingInvestor(String userCode)
	{
		//Document response = soapClient.sendAndReceive(SEARCH_USER_SERVICE_NAME, SEARCH_USER_TEMPLATE, model);
		//return "OK".equals(SoapUtil.getXpathNode(response).queryForText("//ns1:UserSearchResponse/ns1:Username"));
		if (userCode.equalsIgnoreCase("Test"))
		{
			return true;
		}
		return false;
	}

	public boolean isValidPassword(String password)
	{
		//Document response = soapClient.sendAndReceive(PASSWORD_POLICY_SERVICE_NAME, PASSWORD_POLICY_TEMPLATE, model);
		//return "OK".equals(SoapUtil.getXpathNode(response).queryForText("//ns1:PasswordPolicyResponse/ns1:ValidPassword"));
		//		if (password.equalsIgnoreCase("today123"))
		//			return false;

		return true;
	}

	//ToDo -- not sure what the service will be and what it would return so just hard coding
	public boolean sendSmsCode(String registrationCode, String lastName, String postcode) throws Exception
	{	
		//Todo will need to invoke service when ready-- will move them in stub
		return validCombinationForSms(registrationCode, lastName, postcode);
	}
	
	private boolean validCombinationForSms(String registrationCode, String lastName, String postcode) throws Exception
	{
		Thread.sleep(500);
		//ToDo - will go in service later
		if (StringUtils.isBlank(registrationCode) || StringUtils.isBlank(lastName) || StringUtils.isBlank(postcode))
		{
			return false;
		}
		
		//TODO: this will be removed one get the actual implementation with EAM service
		if(registrationCode.equals("1"))
		{
			return (registrationCode.equals("1") && lastName.equalsIgnoreCase("Test")) && postcode.equals("1111") ? true : false;	
		}
		if(registrationCode.equals("5"))
		{
			return (registrationCode.equals("5") && lastName.equalsIgnoreCase("Demo")) && postcode.equals("5555") ? true : false;	
		}
		
		if(registrationCode.equals("6"))
		{
			return (registrationCode.equals("6") && lastName.equalsIgnoreCase("User")) && postcode.equals("6666") ? true : false;	
		}
		return false;
	}
		
	//ToDO - need to finalize interface with Webseal
	public String createInvestor(String usercode, String password)
	{
		//Document response = soapClient.sendAndReceive(SEARCH_USER_SERVICE_NAME, SEARCH_USER_TEMPLATE, model);
		//return "OK".equals(SoapUtil.getXpathNode(response).queryForText("//ns1:InvestorResponse/ns1:AdviserId"));
		return "MTIzNDU2Nw==";
	}

	public boolean activateAccount(String userId)
	{
		return true;
	}

	@Override
	public AccountStatusModel activationStatus(String userId, String accountId)
	{
		//Implement the actual service call TODO: AccountStatusModel will be fetch from webservice call 

		AccountStatus accountStatus = getAccountStatus(userId, accountId);

		AccountStatusModel accountStatusModel = new AccountStatusModel();
		accountStatusModel.setAccountType(accountStatus.getAccountType());
		accountStatusModel.setAccountDescription(accountStatus.getAccountDescription());
		accountStatusModel.setInitiatedDate(accountStatus.getInitiatedDate());
		accountStatusModel.setAdviserId(accountStatus.getAdviserId());
		accountStatusModel.setEmail(accountStatus.getEmail());
		accountStatusModel.setPhone(accountStatus.getPhone());

		List <ClientModel> clientModelList = new ArrayList <ClientModel>();
		for (ClientDetail client : accountStatus.getClientMembers())
		{
			ClientModel clientModel = new ClientModel();
			clientModel.setSalutation(client.getSalutation());
			clientModel.setFirstName(client.getFirstName());
			clientModel.setLastName(client.getLastName());
			clientModel.setPhone(String.valueOf(client.getPhone()));
			clientModel.setEmail(client.getEmail());
			clientModel.setActivationStatus(client.getStatus());
			clientModel.setAccountHolderType(client.getAccountHolderType());
			clientModelList.add(clientModel);
		}

		accountStatusModel.setClientList(clientModelList);

		return accountStatusModel;
	}

	private AccountStatus getAccountStatus(String userId, String accountId)
	{
		AccountStatus accountStatus = null;
		try
		{
			accountStatus = JaxbUtil.<AccountStatus> unmarshall(testXmlFile, AccountStatus.class);
		}

		catch (Exception e)
		{
			logger.error(e.getMessage());
		}
		return accountStatus;
	}	
}