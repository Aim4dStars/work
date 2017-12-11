package com.bt.nextgen.clients.util;

import com.bt.nextgen.clients.api.model.AddressDto;
import com.bt.nextgen.clients.domain.ClientDomain;
import com.bt.nextgen.clients.web.model.ClientModel;
import com.bt.nextgen.core.type.DateUtil;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.core.web.Format;
import com.bt.nextgen.core.web.model.AddressModel;
import com.bt.nextgen.portfolio.domain.PortfolioDomain;
import com.bt.nextgen.portfolio.web.model.AccountModel;
import com.bt.nextgen.portfolio.web.model.CashAccountModel;
import com.bt.nextgen.portfolio.web.model.PortfolioModel;
import com.bt.nextgen.portfolio.web.model.StatementDetailModel;
import com.bt.nextgen.portfolio.web.model.StatementTypeModel;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.bassil.BasilDocumentTypeValueEnum;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.btfin.panorama.core.util.StringUtil.*;

public class ClientUtil
{
	private static final Logger logger = LoggerFactory.getLogger(ClientUtil.class);

	private static final int NO_OF_QUARTERS = 3;
	private static final String DEFAULT_FILTER_VALUE = "0";
	public static final String DEFAULT_PRODUCT_VALUE = "All Products";
	public static final String GST_OPTION_YES = "Yes";
	public static final String GST_OPTION_NO = "No";

	public static void convertClientStatementDomainToModel(String documentType, String documentId, Date effectiveDate,
		Map <String, StatementTypeModel> statementTypeModelMap) throws Exception
	{
		logger.info("inside convertClientStatementDomainToModel method:");
		try
		{
			String statementType = BasilDocumentTypeValueEnum.valueOf(documentType).getDocumentType();
			getStatementTypeModel(statementType, effectiveDate, documentId, statementTypeModelMap);
		}
		catch (IllegalArgumentException e)
		{
			logger.error("No enum avaliable for this document type", e);
		}
	}

	public static List <ClientModel> toClientModel(List <ClientDomain> clientDomains) throws Exception
	{
		logger.info("inside toClientModel method:");
		List <ClientModel> clientModels = new ArrayList <>();
		for (ClientDomain clientDomain : clientDomains)
		{
			ClientModel clientModel = new ClientModel();
			clientModel.setClientId(clientDomain.getClientId());
			clientModel.setFirstName(clientDomain.getFirstName());
			clientModel.setLastName(clientDomain.getLastName());
			clientModel.setClientIdEncoded(EncodedString.fromPlainText(clientDomain.getClientIdEncoded()));
			clientModel.setAdviserName(clientDomain.getAdviserName());
			clientModel.setClientName(clientDomain.getClientName());
			clientModel.setContactName(clientDomain.getPrimaryContactName());
			clientModel.setEmail(clientDomain.getEmail());

			if (clientDomain.getWrapAccounts() != null)
			{
				List <PortfolioModel> portfolioModels = new ArrayList <>();
				for (PortfolioDomain wrapAccount : clientDomain.getWrapAccounts())
				{
					PortfolioModel portfolioModel = new PortfolioModel();

					portfolioModel.setAccountId(wrapAccount.getAccountId());
					portfolioModel.setAccountName(wrapAccount.getAccountName());
					portfolioModel.setAccountType(wrapAccount.getAccountType());
					portfolioModel.setRegisterDate(wrapAccount.getRegisterDate());
					portfolioModel.setAdviserFirstName(wrapAccount.getAdviserFirstName());
					portfolioModel.setAdviserLastName(wrapAccount.getAdviserLastName());
					portfolioModel.setAdviserId(wrapAccount.getAdviserId());
					portfolioModel.setClientId(EncodedString.fromPlainText(wrapAccount.getClientId()));
					portfolioModel.setPortfolioId(EncodedString.fromPlainText(wrapAccount.getPortfolioId()));
					portfolioModel.setAdviserPermission(wrapAccount.getAdviserPermission());
					portfolioModel.setBalance(wrapAccount.getBalance());
					CashAccountModel cashAccount = new CashAccountModel();
					cashAccount.setCashAccountNumber(wrapAccount.getCashAccountNumber());
					cashAccount.setAvailableBalance(Format.asCurrency(wrapAccount.getAvailableBalance()));
					portfolioModel.setCashAccount(cashAccount);
					portfolioModels.add(portfolioModel);
				}
				clientModel.setWrapAccounts(portfolioModels);
			}
			clientModels.add(clientModel);
		}
		return clientModels;
	}

	private static void getStatementTypeModel(String statementType, Date effectiveDate, String documentId,
		Map <String, StatementTypeModel> statementTypeModelMap) throws Exception
	{
		StatementTypeModel statementTypeModel = null;
		String accountConfirmationStatementType = Constants.EMPTY_STRING;
		String annualReportStatementsType = Constants.EMPTY_STRING;
		if (statementType.equals(Attribute.FAILURE_NOTIFICATION) || statementType.equals(Attribute.EXIT_STMTS))
		{
			accountConfirmationStatementType = statementType;
			statementType = Attribute.ACCOUNT_CONFIRMATIONS;
		}
		else if (Attribute.ANNUAL_INVESTOR_STMT.equals(statementType) || Attribute.ANNUAL_TAX_STMT.equals(statementType))
		{
			annualReportStatementsType = statementType;
			statementType = Attribute.ANNUAL_STMTS;
		}
		if (statementTypeModelMap.containsKey(statementType))
		{
			statementTypeModel = statementTypeModelMap.get(statementType);
		}
		else
		{
			statementTypeModel = new StatementTypeModel();
			statementTypeModelMap.put(statementType, statementTypeModel);
		}

		List <StatementDetailModel> statementDetailList = statementTypeModel.getStatementDetailList();
		StatementDetailModel statementDetailModel = new StatementDetailModel();
		statementDetailModel.setDocumentId(documentId);
		statementDetailModel.setReportSource(Attribute.REPORT_SOURCE_BASIL);
		if (effectiveDate != null
			&& (Attribute.QRTLY_STMTS.equals(statementType) || Attribute.QRTLY_PAYG_STMTS.equals(statementType) || Attribute.ANNUAL_STMTS.equals(statementType)))
		{
			String middleString = " ";
			switch (statementType)
			{
				case Attribute.QRTLY_PAYG_STMTS:
					middleString = " PAYG ";
				case Attribute.QRTLY_STMTS:
					QuarterEnum quarterEnum = ClientUtil.getCompleteQuarterDetails(effectiveDate);
					statementDetailModel.setPeriodTypeStatement(quarterEnum.getQuarterYear() + middleString
						+ quarterEnum.getQuarterName());
					statementDetailModel.setPeriodFromDate(quarterEnum.getQuarterStartDate());
					statementDetailModel.setPeriodToDate(quarterEnum.getQuarterEndDate());
					break;
				case Attribute.ANNUAL_STMTS:
					statementDetailModel.setPeriodTypeStatement(DateUtil.getFinPeriodStartYear(effectiveDate) + "-"
						+ DateUtil.getFinPeriodEndYear(effectiveDate) + middleString + annualReportStatementsType);
					statementDetailModel.setPeriodFromDate(DateUtil.getFinYearStartDate(effectiveDate));
					statementDetailModel.setPeriodToDate(DateUtil.getFinYearEndDate(effectiveDate));
					break;
				default:
					break;
			}
		}
		else
		{
			statementDetailModel.setPeriodTypeStatement(accountConfirmationStatementType);
		}
		statementDetailList.add(statementDetailModel);
		statementTypeModel.setStatementType(statementType);
		statementTypeModel.setStatementDetailList(statementDetailList);
	}

	private static QuarterEnum getCompleteQuarterDetails(Date effectiveDate) throws Exception
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(effectiveDate);
		int month = calendar.get(Calendar.MONTH);
		int quarterNumber = (1 + (month / 3));
		return QuarterEnum.decodeStartEndDate(new DateTime(effectiveDate), quarterNumber);
	}

	//
	public static AddressDto getAddressDetails(AddressModel address)
	{

		AddressDto addressDtoObject = new AddressDto();
		addressDtoObject.setAddressCategory(address.getAddressCategory());
		addressDtoObject.setAddressKind(address.getAddressKind());
		addressDtoObject.setAddressLine1(address.getAddressLine1());
		addressDtoObject.setAddressLine2(address.getAddressLine2());
		addressDtoObject.setAddressMedium(address.getAddressMedium());
		addressDtoObject.setBoxPrefix(address.getBoxPrefix());
		addressDtoObject.setBuildingName(address.getBuildingName());
		addressDtoObject.setCity(address.getCity());
		addressDtoObject.setCountry(address.getCountry());
		addressDtoObject.setFloorNumber(address.getFloorNumber());
		addressDtoObject.setFullAddress(address.getFullAddress());
		addressDtoObject.setIsDomicileAddress(address.getIsDomicileAddress());
		addressDtoObject.setIsMailingAddress(address.getIsMailingAddress());
		addressDtoObject.setPin(address.getPin());
		addressDtoObject.setPoBoxNumber(address.getPoBoxNumber());
		addressDtoObject.setPostcode(address.getPostcode());
		addressDtoObject.setProfession(address.getProfession());
		addressDtoObject.setState(address.getState());
		addressDtoObject.setStreet(address.getStreet());
		addressDtoObject.setStreetNumber(address.getStreetNumber());
		addressDtoObject.setStreetType(address.getStreetType());
		addressDtoObject.setSuburb(address.getSuburb());
		addressDtoObject.setAddressType(address.getType());
		addressDtoObject.setUnitNumber(address.getUnitNumber());
		return addressDtoObject;
	}

	public static AddressModel getAddressModelDetails(AddressDto address)
	{

		AddressModel addressModel = new AddressModel();
		addressModel.setAddressCategory(address.getAddressCategory());
		addressModel.setAddressKind(address.getAddressKind());
		addressModel.setAddressLine1(address.getAddressLine1());
		addressModel.setAddressLine2(address.getAddressLine2());
		addressModel.setAddressMedium(address.getAddressMedium());
		addressModel.setBoxPrefix(address.getBoxPrefix());
		addressModel.setBuildingName(address.getBuildingName());
		addressModel.setCity(address.getCity());
		addressModel.setCountry(address.getCountry());
		addressModel.setFloorNumber(address.getFloorNumber());
		addressModel.setFullAddress(address.getFullAddress());
		addressModel.setIsDomicileAddress(address.getIsDomicileAddress());
		addressModel.setIsMailingAddress(address.getIsMailingAddress());
		addressModel.setPin(address.getPin());
		addressModel.setPoBoxNumber(address.getPoBoxNumber());
		addressModel.setPostcode(address.getPostcode());
		addressModel.setProfession(address.getProfession());
		addressModel.setState(address.getState());
		addressModel.setStreet(address.getStreet());
		addressModel.setStreetNumber(address.getStreetNumber());
		addressModel.setStreetType(address.getStreetType());
		addressModel.setSuburb(address.getSuburb());
		addressModel.setType(address.getAddressType());
		addressModel.setUnitNumber(address.getUnitNumber());
		return addressModel;
	}

}

enum QuarterEnum
{
	QUARTER1("March Quarter"), QUARTER2("June Quarter"), QUARTER3("September Quarter"), QUARTER4("December Quarter");

	private static String quarterStartDate = "";
	private static String quarterEndDate = "";
	private static String quarterYear = "";
	private final String quarterName;

	private QuarterEnum(String quarterName)
	{
		this.quarterName = quarterName;
	}

	private static String quarterEndDate(DateTime date, int qtr)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date.toDate());
		cal.set(Calendar.MONTH, (qtr * 3 - 1));
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
		return ApiFormatter.asShortDate(cal.getTime());
	}

	private static String quarterStartDate(DateTime date, int qtr)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date.toDate());
		cal.set(Calendar.MONTH, (qtr * 3 - 3));
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return ApiFormatter.asShortDate(cal.getTime());
	}

	private static String quarterYear(DateTime date)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date.toDate());
		return String.valueOf(cal.get(Calendar.YEAR));
	}

	public static QuarterEnum decodeStartEndDate(DateTime effectiveDate, int quarter)
	{
		QuarterEnum quarterEnum = null;
		quarterStartDate = quarterStartDate(effectiveDate, quarter);
		quarterEndDate = quarterEndDate(effectiveDate, quarter);
		quarterYear = quarterYear(effectiveDate);
		switch (quarter)
		{
			case 1:
				quarterEnum = QuarterEnum.QUARTER1;
				break;
			case 2:
				quarterEnum = QuarterEnum.QUARTER2;
				break;
			case 3:
				quarterEnum = QuarterEnum.QUARTER3;
				break;
			case 4:
				quarterEnum = QuarterEnum.QUARTER4;
				break;
		}
		return quarterEnum;
	}

	public String getQuarterStartDate()
	{
		return quarterStartDate;
	}

	public String getQuarterEndDate()
	{
		return quarterEndDate;
	}

	public String getQuarterName()
	{
		return quarterName;
	}

	public String getQuarterYear()
	{
		return quarterYear;
	}
}

enum ACCOUNTMODEL_SORTBY
{
	ACCOUNT_NAME_IN_ASC("Account name in ascending order"), ACCOUNT_NUMBER_IN_ASC("Account number in ascending order");

	private final String sortBy;

	public String getSortBy()
	{
		return sortBy;
	}

	ACCOUNTMODEL_SORTBY(String sortBy)
	{
		this.sortBy = sortBy;
	}
}

/**
 * Comparator to compare two argument of type AccountModel.
 *  
 */
class SearchAccountModelComparator implements Comparator <AccountModel>
{
	private ACCOUNTMODEL_SORTBY sortBy;

	public SearchAccountModelComparator(ACCOUNTMODEL_SORTBY sortBy)
	{
		this.sortBy = sortBy;
	}

	@Override
	public int compare(AccountModel searchAccountModel1, AccountModel searchAccountModel2)
	{
		int returnValue = 0;
		switch (this.sortBy)
		{
			case ACCOUNT_NAME_IN_ASC:
				returnValue = (isNotNullorEmpty(searchAccountModel1.getAccountName()) && isNotNullorEmpty(searchAccountModel2.getAccountName()))
					? searchAccountModel1.getAccountName().compareTo(searchAccountModel2.getAccountName())
					: 0;
				break;
			case ACCOUNT_NUMBER_IN_ASC:
				returnValue = isNotNullorEmpty(searchAccountModel1.getCashAccountNumber())
					&& isNotNullorEmpty(searchAccountModel2.getCashAccountNumber())
					? (new BigDecimal(searchAccountModel1.getCashAccountNumber())).compareTo(new BigDecimal(searchAccountModel2.getCashAccountNumber()))
					: 0;
				break;
			default:
				break;
		}
		return returnValue;
	}

}