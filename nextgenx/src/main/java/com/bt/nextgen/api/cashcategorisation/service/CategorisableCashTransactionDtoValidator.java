package com.bt.nextgen.api.cashcategorisation.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.cashcategorisation.model.CategorisableCashTransactionDto;
import com.bt.nextgen.api.cashcategorisation.model.CategorisedTransactionDto;
import com.bt.nextgen.api.transactionhistory.model.SmsfMembersDto;
import com.bt.nextgen.api.transactionhistory.service.RetrieveSmsfMembersDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationSubtype;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Validator
 */
@SuppressWarnings({"findbugs:DLS_DEAD_LOCAL_STORE", "squid:S1481", "squid:S1066"})
@Component
public class CategorisableCashTransactionDtoValidator implements Validator
{
	@Autowired
	private RetrieveSmsfMembersDtoService smsfMemberDtoService;

	private static final Logger logger = LoggerFactory.getLogger(CategorisableCashTransactionDtoValidator.class);

	@Override
	public boolean supports(Class<?> aClass)
	{
		return aClass.isAssignableFrom(CategorisableCashTransactionDto.class);
	}

	@Override
	public void validate(Object o, Errors errors)
	{
		CategorisableCashTransactionDto cashTransaction = (CategorisableCashTransactionDto) o;

		checkCategorisationTypeIsValid(cashTransaction.getTransactionCategory(), errors);

		if ("member".equalsIgnoreCase(cashTransaction.getCategorisationLevel()) || "membersubcat".equalsIgnoreCase(cashTransaction.getCategorisationLevel()))
		{
			if (checkContributionSplitsAreForValidPersons(cashTransaction.getKey(), cashTransaction.getMemberContributionDtoList()) == false) {
				errors.reject("One or more person id's nominated do not belong to this smsf account");
			}
		}

		if (validateTotalContributionsMatchesDepositAmount(cashTransaction) == false)
		{
				errors.reject("Amounts do not add up to deposit total");
		}

		if ("fund".equalsIgnoreCase(cashTransaction.getCategorisationLevel()) || "membersubcat".equalsIgnoreCase(cashTransaction.getCategorisationLevel()))
		{
			checkCategorisationSplitsHaveValidSubtypes(cashTransaction.getMemberContributionDtoList(), errors);
		}
	}

	/*private boolean categorisationSplitEqualsTransactionAmount(String categorisationLevel, BigDecimal depositAmount, List<CategorisedTransactionDto> splits, Errors errors) {
		if (depositAmount == null || depositAmount.compareTo(BigDecimal.ZERO) <= 0) {
			errors.reject("Deposit amount is less than or equal to zero");
		}

		BigDecimal splitTotalAmount = BigDecimal.ZERO;

		for (CategorisedTransactionDto split : splits) {
			if (checkIfValidAmount(split.getAmount(), errors)) {
				BigDecimal amount = split.getAmount();
				splitTotalAmount = splitTotalAmount.add(amount);
			}
		}

		if (!splitTotalAmount.equals(depositAmount))
		{
			logger.info("transaction value: {} does not equal the total split amount: {}", depositAmount, splitTotalAmount);
			return false;
		}

		return true;
	}*/


	private boolean validateTotalContributionsMatchesDepositAmount(CategorisableCashTransactionDto transaction)
	{
		BigDecimal amountToCompare;

		if (transaction.getAmount() == null)
		{
			throw new IllegalStateException("Deposit amount does not exist");
		}
		else
		{
			amountToCompare = transaction.getAmount().setScale(2, RoundingMode.HALF_UP);
		}

		BigDecimal totalContributions = new BigDecimal(0);

		for (CategorisedTransactionDto contribution : transaction.getMemberContributionDtoList())
		{
			BigDecimal contributionAmount = contribution.getAmount().setScale(2, RoundingMode.HALF_UP);
			totalContributions = totalContributions.add(contributionAmount);
		}

		if (!amountToCompare.abs().equals(totalContributions))
		{
			logger.error("Deposit (doc id {}) with amount {} does not add up to total contributions ({})",
					transaction.getDepositId(), transaction.getAmount(), totalContributions);
			return false;
		}

		return true;
	}


	/**
	 * Validate all persons (personId) nominated in the contribution split belong to the actual account
	 * @param accountKey smsf account id
	 * @param splits list of contribution splits
	 * @return true if all person ids belong to the account, otherwise false.
	 */
	private boolean checkContributionSplitsAreForValidPersons(AccountKey accountKey, List<CategorisedTransactionDto> splits)
	{
		boolean allValidPersons = true;

		for (CategorisedTransactionDto splitDto : splits)
		{
			boolean belongsToAccount = false;

			belongsToAccount = checkIfPersonIdBelongsToAccount(accountKey.getAccountId(), splitDto.getPersonId());
			logger.debug("Validating person: {} belongs to account: {} --> {}", splitDto.getPersonId(), accountKey.getAccountId(), belongsToAccount);

			if (belongsToAccount == false)
			{
				allValidPersons = false;
			}
		}

		return allValidPersons;
	}

	private boolean checkIfPersonIdBelongsToAccount(String accountId, String personId)
	{
		ApiSearchCriteria criteria = new ApiSearchCriteria("accountId", SearchOperation.EQUALS,
															EncodedString.fromPlainText(accountId).toString(), OperationType.STRING);

		List<ApiSearchCriteria> criteriaList = new ArrayList<>();
		criteriaList.add(criteria);

		List<SmsfMembersDto> members = smsfMemberDtoService.search(criteriaList, new ServiceErrorsImpl());

		for (SmsfMembersDto member : members)
		{
			if (personId != null && member.getPersonId().equalsIgnoreCase(personId))
			{
				return true;
			}
		}

		return false;
	}

	/*private boolean checkIfValidAmount(BigDecimal amount, Errors errors)
	{
		if (amount != null)
		{
			BigDecimal contributionAmount = BigDecimal.ZERO;

			try
			{
				contributionAmount = amount;
				return true;
			}
			catch (NumberFormatException nfe)
			{
				errors.rejectValue("amount", "Contribution amount is not valid");
			}
		}

		return false;
	}
*/
	private boolean checkCategorisationSplitsHaveValidSubtypes(List<CategorisedTransactionDto> splits, Errors errors)
	{
		for (CategorisedTransactionDto member : splits)
		{
			if (StringUtils.isNotEmpty(member.getContributionSubType()))
			{
				if (CashCategorisationSubtype.getByAvaloqInternalId(member.getContributionSubType()) != null)
				{
					return true;
				}
			}
		}

		errors.rejectValue("CashCategorisationSubType", "Cash Categorisation Subtype is not valid");
		return false;
	}

	private boolean checkCategorisationTypeIsValid(String categorisationType, Errors error)
	{
		if (!StringUtils.isEmpty(categorisationType))
		{
			if (CashCategorisationType.getByAvaloqInternalId(categorisationType) != null)
			{
				return true;
			}
		}

		error.reject("categorisation type is not valid");
		return false;
	}
}
