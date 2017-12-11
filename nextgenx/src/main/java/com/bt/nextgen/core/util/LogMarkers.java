package com.bt.nextgen.core.util;

import com.bt.nextgen.addressbook.PayeeModel;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.login.web.model.RegistrationModel;
import com.bt.nextgen.login.web.model.SmsCodeModel;
import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.web.model.DepositInterface;
import com.bt.nextgen.payments.web.model.PaymentInterface;
import com.bt.nextgen.portfolio.web.model.PortfolioInterface;
import com.bt.nextgen.termdeposit.web.model.TermDepositAccountModel;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Formatter;
import java.util.Locale;

/*
LogMarkers is a Utility class for AUDITING/LOGGING purpose.
It abstract away all the Auditing information into common place.
*/
@Component
public class LogMarkers
{
	public static final Marker AUDIT = MarkerFactory.getMarker("AUDIT");

	public enum Status
	{
		SUCCESS, FAILED
	}

	static UserProfileService userProfileService;

	@Autowired
	public void setUserDetailsService(UserProfileService udetailService)
	{
		userProfileService = udetailService;
	}

	//this will cover deleting BPAY(4.e.ix), PAYANYONE (4.e.vii)
	public static void audit_delete_payee(PortfolioInterface portfolioModel, PayeeModel payee, Status status, Logger logger,
		String... failureReason)
	{
		try
		{
			String headerInfo = getHeader(portfolioModel);
			String statusInfo = getStaus(status, failureReason);

			switch (payee.getPayeeType())
			{
				case BPAY:
					logger.info(AUDIT,
						"{} Deleting a [{}] account, Biller code [{}], CRN [{}], Biller nickname [{}] {}", headerInfo,
						payee.getPayeeType().name(), filter(payee.getCode()), filter(payee.getReference()),
						payee.getNickname(), statusInfo);
					break;
				default:
					logger.info(AUDIT,
						"{} Deleting a [{}] account, Account name [{}], BSB [{}], Account number [{}], Nickname [{}] {}",
						headerInfo, payee.getPayeeType().name(), payee.getName(), filter(payee.getCode()),
						filter(payee.getReference()), payee.getNickname(), statusInfo);
			}
		}
		catch (Exception ex)
		{
			logger.error("failed to AUDIT", ex);
		}
	}

	//Adding a 'BPAY' account 4.d, 4.e.iii
	public static void audit_add_payee(PortfolioInterface portfolioModel, PayeeModel payee, Status status, Logger logger,
		String... failureReason)
	{
		try
		{
			String headerInfo = getHeader(portfolioModel);
			String statusInfo = getStaus(status, failureReason);

			switch (payee.getPayeeType())
			{
				case BPAY:
					logger.info(AUDIT, "{} Adding a [{}] account, Biller code [{}] CRN [{}] Biller nickname [{}] {}",
						headerInfo, payee.getPayeeType().name(), filter(payee.getCode()),
						filter(payee.getCode()), payee.getNickname(), statusInfo);
					break;
				default:
					logger.info(AUDIT,
						"{} Adding a [{}] account, Account name [{}], BSB [{}], Account number [{}], Nickname [{}]",
						headerInfo, payee.getPayeeType().name(), payee.getName(), filter(payee.getCode()),
						filter(payee.getCode()), payee.getNickname(), statusInfo);
			}
		}
		catch (Exception ex)
		{
			logger.error("failed to AUDIT", ex);
		}
	}

	//Adding a 'Linked' Account 4.e(ii)
	public static void audit_linkedAccount(PortfolioInterface portfolioModel, String operationType, PayeeModel payee,
		Status status, Logger logger, String... failureReason)
	{
		try
		{
			String headerInfo = getHeader(portfolioModel);
			String statusInfo = getStaus(status, failureReason);

			logger.info(AUDIT,
				"{} {} a [{}] account, Account name [{}], BSB [{}], Account number [{}], Nickname [{}] {}", headerInfo,
				operationType, payee.getPayeeType().name(), payee.getName(), filter(payee.getCode()),
				filter(payee.getReference()), payee.getNickname(), statusInfo);
		}
		catch (Exception ex)
		{
			logger.error("failed to AUDIT", ex);
		}
	}

	//Adding a 'Pay Anyone' account 4.c & 4.e.i (PayeeController)
	public static void audit_add_PayAnyone(PortfolioInterface portfolioModel, PayeeModel payee, Status status,
		Logger logger, String... failureReason)
	{
		try
		{
			String headerInfo = getHeader(portfolioModel);
			String statusInfo = getStaus(status, failureReason);
			logger.info(AUDIT,
				"{} Adding a [{}] account, Account name [{}], BSB [{}], Account number [{}], Nickname [{}] {}",
				headerInfo, payee.getPayeeType().name(), payee.getName(), filter(payee.getCode()),
				filter(payee.getReference()), payee.getNickname(), statusInfo);
		}
		catch (Exception ex)
		{
			logger.error("Failed to AUDIT", ex);
		}
	}

	//deposits: 4.b
	public static void audit_submitDeposit(PortfolioInterface portfolioModel, DepositInterface deposit,
		Status status, Logger logger, String... failureReason)
	{
		try
		{
			String headerInfo = getHeader(portfolioModel);
			String statusInfo = getStaus(status, failureReason);

			logger.info(AUDIT, "{} Deposit: From (Account name [{}] BSB [{}] Account number [{}]) Amount: [{}] {}",
				headerInfo, deposit.getAccount().getIdpsAccountName(), filter(deposit.getAccount().getBsb()),
				filter(deposit.getAccount().getIdpsAccountName()), deposit.getAmount(), statusInfo);
		}
		catch (Exception ex)
		{
			logger.error("failed to AUDIT", ex);
		}
	}

	//payments: 4.a
	public static void audit_submitPayment(PortfolioInterface portfolioModel, PaymentInterface payment, Status status,
		Logger logger, String... failureReason)
	{
		try
		{
			String headerInfo = getHeader(portfolioModel);
			String statusInfo = getStaus(status, failureReason);

			switch (payment.getTo().getPayeeType())
			{
				case BPAY:
					logger.info(AUDIT, "{} payments: To (Biller Name [{}] Biller Code [{}] CRN [{}] Amount: [{}]) {}",
						headerInfo, payment.getTo().getName(), payment.getTo().getCode(),
						payment.getTo().getReference(), payment.getAmount(), statusInfo);
					break;
				default:
					logger.info(AUDIT,
						"{} payments: To (Account name [{}] BSB [{}] Account number [{}] Amount: [{}]) {}", headerInfo,
						payment.getTo().getName(), payment.getTo().getCode(), payment.getTo().getReference(),
						payment.getAmount(), statusInfo);
			}
		}
		catch (Exception ex)
		{
			logger.error("failed to AUDIT", ex);
		}
	}

	//Change daily payment limit - PayAnyone / BPAY 4.e.(iv), 4.e.(v)
	public static void audit_changeDailyLimit(PortfolioInterface portfolioModel, Status status, String oldDailyLimit,
		String newDailyLimit, PayeeType PayeeType, Logger logger, String... failureReason)
	{
		try
		{
			String headerInfo = getHeader(portfolioModel);
			String statusInfo = getStaus(status, failureReason);

			logger.info(LogMarkers.AUDIT, "{} Changing payment limit for [{}] From amount [{}] To amount [{}] {}",
				headerInfo, PayeeType.name(), oldDailyLimit, newDailyLimit, statusInfo);
		}
		catch (Exception ex)
		{
			logger.error("failed to AUDIT", ex);
		}
	}

	//Term Deposit Early withdraw ( 5.b)
	public static void audit_withdraw_termDeposit(PortfolioInterface portfolioModel, Status status, String tdAccountId,
		TermDepositAccountModel tdacc, Logger logger, String... failureReason)
	{
		try
		{
			String headerInfo = getHeader(portfolioModel);
			String statusInfo = getStaus(status, failureReason);

			logger.info(AUDIT,
				"{} Early withdrawal: Term Deposit: TD Id [{}] TD break date [{}] Brand [{}] Maturity amount [{}] Tenure(term) [{}] Rate [{}], Amount invested [{}], interest paid type [{}] {}",
				headerInfo, tdAccountId, tdacc.getWithdrawnDate(), tdacc.getBrandName(),
				tdacc.getMaturityInstructionAmount(), tdacc.getTermDuration(), tdacc.getInterestRate(),
				tdacc.getInvestmentAmount(), tdacc.getInterestPaid(), statusInfo);

		}
		catch (Exception ex)
		{
			logger.error("failed to AUDIT", ex);
		}
	}

	//Tern Deposit 5.c.Changing maturity instruction
	public static void audit_changing_maturityInstruction(PortfolioInterface portfolioModel, Status status,
		TermDepositAccountModel tdacc, Logger logger, String... failureReason)
	{
		try
		{
			String headerInfo = getHeader(portfolioModel);
			String statusInfo = getStaus(status, failureReason);

			logger.info(AUDIT,
				"{} Changing maturity instructions: edit date [{}]  Tenure(term) [{}] Amount invested [{}] {}",
				headerInfo, ApiFormatter.asShortDate(new DateTime()), tdacc.getTermDuration(), tdacc.getInvestmentAmount(),
				statusInfo);
		}
		catch (Exception ex)
		{
			logger.error("Failed to AUDIT", ex);
		}
	}

	public static void audit_register_smsCode(SmsCodeModel smsCodeModel, Logger logger)
	{
		try
		{
			logger.info(AUDIT, "Register: Registration number [{}] Last name [{}] PostCode [{}] SmsCode[{}]",
				filter(smsCodeModel.getUserCode()), smsCodeModel.getLastName(), smsCodeModel.getPostcode(),
				filter(smsCodeModel.getSmsCode()));
		}
		catch (Exception ex)
		{
			logger.error("failed to AUDIT", ex);
		}
	}

	public static void audit_registerUser(RegistrationModel registrationModel, Logger logger)
	{
		try
		{
			logger.info(AUDIT, "Register step2: User Name [{}] ", filter(registrationModel.getUserCode()));
		}
		catch (Exception ex)
		{
			logger.error("failed to AUDIT", ex);
		}
	}

	public static void audit_forgottenPassword(Logger logger, String userName, String lastName, String postCode,
		String smsCode, Status status, String... failureReason)
	{
		try
		{
			String statusInfo = getStaus(status, failureReason);
			logger.info(AUDIT, "Forgotten Password Step1: UserName [{}] LastName [{}] PostCode [{}] SmsCode [{}] {}",
				userName, lastName, postCode, smsCode, statusInfo);
		}
		catch (Exception ex)
		{
			logger.error("failed to AUDIT", ex);
		}
	}

	public static void audit_registration(Logger logger, String userName, String lastName, String postCode,
		String smsCode, Status status, String... failureReason)
	{
		try
		{
			String statusInfo = getStaus(status, failureReason);
			logger.info(AUDIT, "Registration Step1: UserName [{}] LastName [{}] PostCode [{}] SmsCode [{}] {}",
				userName, lastName, postCode, smsCode, statusInfo);
		}
		catch (Exception ex)
		{
			logger.error("failed to AUDIT", ex);
		}
	}

	public static void audit_resetUser(String username, Status status, Logger logger, String... failureReason)
	{
		String statusInfo = getStaus(status, failureReason);
		logger.info(AUDIT, "username [{}] {}", filter(username), statusInfo);
	}

	public static void audit_serviceOperation(Logger logger, String action, String clientId, String firstName,
		String lastName)
	{
		try
		{
			String headerInfo = getHeader();
			logger.info(AUDIT, "{}  client/intermediary ID [{}] first name [{}] last name [{}] action performed [{}]",
				headerInfo, clientId, firstName, lastName, action);
		}
		catch (Exception ex)
		{
			logger.error("failed to AUDIT", ex);
		}
	}

	private static Formatter formatter()
	{
		StringBuilder sb = new StringBuilder();
		return new Formatter(sb, Locale.UK);
	}

	//todo: we may implement masking or encryption
	private static String filter(String value)
	{
		return value;
	}

	/*
	audit_header method is common for almost all the audit operations.
	It prints username, surname, (Account Name, Account Type, Account ID from PortfolioModel(FatHeader)) and time & date
	 */
	private static String getHeader(PortfolioInterface portfolioModel)
	{
		if (userProfileService != null)
		{
			return formatter().format(
				"User name [%s] First Name [%s] Surname [%s] account name [%s] Account type [%s] Account ID [%s]",
				userProfileService.getUsername(), userProfileService.getFirstName(), userProfileService.getLastName(),
				portfolioModel.getAccountName(), portfolioModel.getAccountType(),
				portfolioModel.getAccountId()).toString();
		}
		return "";
	}

	private static String getStaus(Status status, String... failureReason)
	{
		String statusInfo = "";
		switch (status)
		{
			case SUCCESS:
				statusInfo = formatter().format("STATUS: [%s]", "SUCCESS").toString();
				break;
			case FAILED:
				statusInfo = formatter().format("STATUS: [%s] Failed Reason:[%s] ", "FAILED",
					Arrays.toString(failureReason)).toString();
				break;
			default:
		}
		return statusInfo;
	}

	private static String getHeader()
	{
		if (userProfileService != null)
		{
			return formatter().format("Service Operator: Salary ID [%s] User name [%s] First Name [%s] Surname [%s]",
				userProfileService.getUserId(), userProfileService.getUsername(), userProfileService.getFirstName(),
				userProfileService.getLastName()).toString();
		}
		return "";
	}
}
