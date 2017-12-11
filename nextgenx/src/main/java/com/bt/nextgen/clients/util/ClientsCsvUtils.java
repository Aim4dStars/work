package com.bt.nextgen.clients.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bt.nextgen.core.web.ApiFormatter;
import org.apache.commons.lang3.ObjectUtils;
import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.clients.web.model.ClientModel;
import com.bt.nextgen.core.web.model.FatHeaderModel;
import com.bt.nextgen.portfolio.web.model.CashAccountModel;
import com.bt.nextgen.portfolio.web.model.PortfolioInterface;
import com.bt.nextgen.transactions.web.model.TransactionInterface;
import com.bt.nextgen.web.controller.cash.util.Attribute;


public class ClientsCsvUtils {


    private static final Logger logger = LoggerFactory.getLogger(ClientsCsvUtils.class);

	private static final String CLIENT_LIST = "CLIENT LIST";
	
	private static final String CLIENTLIST_COMMON_HEADER = "Adviser Name, Client Name, Account Name, Account ID,Account Type,Available Cash Balance,Term Deposit Balance,Portfolio Value,Primary Contact Name,Primary Contact Residential Address,Primary Contact Postal Address,Primary Contact Email Address,Primary Contact Mobile Number ";
	
	private static final String TERM_DEPOSIT_TRANSACTIONS_REPORT = "Term deposit transactions report";
	private static final String CREATED_ON = "Created on";
	private static final String FROM_DATE = "From date";
	private static final String TO_DATE = "To date";
	private static final String ACCOUNT_ID = "Account ID";
    private static final String ACCOUNT_NAME = "Account name";
	private static final String ACCOUNT_TYPE = "Account type";
	private static final String ADVISER_NAME = "Adviser name";
	private static final String PRIMARY_CONTACT = "Primary contact";
	private static final String TERM_DEPOSIT_TRANSACTIONS_COMMON_HEADER = "Date,Transaction type,Description,Amount";
	private static final String EMPTY = "";
	private static final String COMMA = ",";
	private static final String NEW_LINE = "\n";
	private static final String SPACE = " ";


	//TODO This should be refactored - There should a single list of objects here (ClientModel) as this contains all of the necessary information
	public static String getCsvClientList(List<PortfolioInterface> portfolioModel, List<CashAccountModel> cashModel,List<ClientModel> clients)throws Exception
	{
		
		StringBuilder sb = new StringBuilder();
		//TODO - Remove this count as it is used as an iteration counter to support iterations of multiple objects
		int count=0;
		sb.append(CLIENT_LIST);
		sb.append(NEW_LINE);

        if(portfolioModel.size()!=0)
		    sb.append("As at" + SPACE + portfolioModel.get(0).getGenerateDate());
		else
            logger.warn("portfolioModel appears to be empty, generated Date will be missing");
        
		sb.append(NEW_LINE);
		sb.append(NEW_LINE);
		sb.append(NEW_LINE);
		sb.append(CLIENTLIST_COMMON_HEADER).append(NEW_LINE);
		
		if(!clients.isEmpty())
		{	
		 for (ClientModel clientModel : clients)
		  {
			//TODO Iterating the client model in conjunction with the other lists is really bad code, please revise
				sb.append(clientModel.getWrapAccounts().get(0).getAdviser() !=null? "\"" +clientModel.getWrapAccounts().get(0).getAdviser()+ "\"" :"" ).append(COMMA);
				sb.append(clientModel.getClientName() !=null? "\"" + clientModel.getClientName() + "\"":"").append(COMMA);
			 
				sb.append(clientModel.getWrapAccounts().get(0).getAccountName()!=null? "\"" + clientModel.getWrapAccounts().get(0).getAccountName() + "\"":"").append(COMMA);
			 		 
				sb.append(cashModel.get(count).getCashAccountNumber()!=null?cashModel.get(count).getCashAccountNumber():"").append(COMMA);
				
				sb.append(clientModel.getWrapAccounts().get(0).getAccountType() != null ? clientModel.getWrapAccounts().get(0).getAccountType():"").append(COMMA);
			 
				sb.append(cashModel.get(count).getAvailableBalance()!=null? cashModel.get(count).getAvailableBalance().replaceAll(COMMA, EMPTY):"$0.00").append(COMMA);
				sb.append(portfolioModel.get(count).getTotalTermDepositBalance() !=null?portfolioModel.get(count).getTotalTermDepositBalance().replaceAll(COMMA, EMPTY):"$0.00").append(COMMA);
				sb.append(portfolioModel.get(count).getPortfolioBalance()!=null?"$" + portfolioModel.get(count).getPortfolioBalance().replaceAll(COMMA, EMPTY):"$0.00").append(COMMA);
				sb.append(clientModel.getWrapAccounts().get(0).getPrimaryHolder().getClientName() !=null? "\"" + clientModel.getWrapAccounts().get(0).getPrimaryHolder().getClientName()+"\"":"").append(COMMA);
				sb.append(cashModel.get(count).getAddressLine1()!=null? cashModel.get(count).getAddressLine1().replaceAll(COMMA, EMPTY):"").append(COMMA);
				sb.append(cashModel.get(count).getAddressLine2()!=null? cashModel.get(count).getAddressLine2().replaceAll(COMMA, EMPTY):"").append(COMMA);
				sb.append(clientModel.getEmail()!=null?clientModel.getEmail():"").append(COMMA);
				
				try{
					sb.append("\"" + portfolioModel.get(count).getMobileList().get(0).getPhoneNumber() + "\"").append(NEW_LINE);	
				}catch(Exception e){
					logger.error("NPE caught when setting the mobile number", e);
					sb.append("\"" + "" + "\"").append(NEW_LINE);
				}
				
			 //sb.append(clientModel.getWrapAccounts().get(0).getHolderPermission()).append("\n");
			//sb.append(portfolioModel.get(count).getAdviserPermission()).append(NEW_LINE);
			 count++;
			
		  }
		}
		
		sb.append(NEW_LINE);
		//sb.append(addDisclaimer(disclaimer));
		return sb.toString();
	}
	
	public static String getCsvTermDepositTransactionsReport(FatHeaderModel fatHeaderModel, List<TransactionInterface> transactions,Map<String, String[]> searchParametersMap,String disclaimer)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(TERM_DEPOSIT_TRANSACTIONS_REPORT);
		sb.append(NEW_LINE);
		sb.append(CREATED_ON).append(COMMA).append(ApiFormatter.formatCurrentDate());
		sb.append(NEW_LINE);
		retrieveDates(searchParametersMap,sb);
		sb.append(ACCOUNT_ID).append(COMMA).append(ObjectUtils.firstNonNull(fatHeaderModel.getPortfolioModel().getAccountId(),EMPTY));
		sb.append(NEW_LINE);
		sb.append(ACCOUNT_NAME).append(COMMA).append(ObjectUtils.firstNonNull(fatHeaderModel.getPortfolioModel().getAccountName(),EMPTY));
		sb.append(NEW_LINE);
		sb.append(ACCOUNT_TYPE).append(COMMA).append(ObjectUtils.firstNonNull(fatHeaderModel.getPortfolioModel().getAccountType(),EMPTY));
		sb.append(NEW_LINE);
		sb.append(ADVISER_NAME).append(COMMA).append(ObjectUtils.firstNonNull(fatHeaderModel.getPortfolioModel().getAdviser(),EMPTY));
		sb.append(NEW_LINE);
		sb.append(PRIMARY_CONTACT).append(COMMA).append(ObjectUtils.firstNonNull(fatHeaderModel.getPrimaryContactName(),EMPTY));
		sb.append(NEW_LINE); 
		sb.append(NEW_LINE);
		
		sb.append(TERM_DEPOSIT_TRANSACTIONS_COMMON_HEADER).append(NEW_LINE);
		if(!transactions.isEmpty())
		{	
		 for (TransactionInterface transactionModel : transactions)
		  {
			sb.append(ObjectUtils.firstNonNull(ApiFormatter.asShortDate(transactionModel.getTransactionDate()),EMPTY)).append(COMMA);
			sb.append(ObjectUtils.firstNonNull(transactionModel.getTransactionType(),EMPTY)).append(COMMA);
			sb.append(ObjectUtils.firstNonNull(transactionModel.getBrandName().replaceAll(COMMA, EMPTY),EMPTY)).append(SPACE);
			sb.append(ObjectUtils.firstNonNull(transactionModel.getDescriptionMain().replaceAll(COMMA, EMPTY),EMPTY)).append(SPACE);
			sb.append(ObjectUtils.firstNonNull(transactionModel.getDescriptionMinor().replaceAll(COMMA, EMPTY),EMPTY)).append(COMMA);
			sb.append(ObjectUtils.firstNonNull(transactionModel.getTdPrincipal().replaceAll(COMMA, EMPTY) ,EMPTY)).append(NEW_LINE);
		   }
		}
		sb.append(NEW_LINE);
		if(!StringUtils.isNullOrEmpty(disclaimer))
		addDisclaimer(disclaimer.replace(COMMA,EMPTY),sb);
		return sb.toString();
	}
	
	
	private static StringBuilder retrieveDates(Map<String, String[]> searchParametersMap,StringBuilder sb)
	{
		String fromDate = Attribute.EMPTY_STRING;
		String toDate = Attribute.EMPTY_STRING;
		for(Entry<String, String[]> entrySet: searchParametersMap.entrySet())
		{
			if(Attribute.FROM_TERM_DATE.equalsIgnoreCase(entrySet.getKey()))
			{
				String[] strFromDate = entrySet.getValue();
			    fromDate = strFromDate[0];
			}
			if(Attribute.TO_TERM_DATE.equalsIgnoreCase(entrySet.getKey()))
			{
				String[] strToDate = entrySet.getValue();
				toDate = strToDate[0];
			}
		}
		sb.append(FROM_DATE).append(COMMA).append(ObjectUtils.firstNonNull(fromDate,EMPTY)).append(NEW_LINE);
		sb.append(TO_DATE).append(COMMA).append(ObjectUtils.firstNonNull(toDate,EMPTY)).append(NEW_LINE);
	   return sb;
	}
	
	
	private static StringBuilder addDisclaimer(String disclaimer,StringBuilder sb)
	{
		sb.append(NEW_LINE);
		sb.append(disclaimer);
		return sb;
	}
}
