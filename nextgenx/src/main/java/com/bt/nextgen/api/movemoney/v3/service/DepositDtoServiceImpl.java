package com.bt.nextgen.api.movemoney.v3.service;

import com.bt.nextgen.api.draftaccount.LoggingConstants;
import com.bt.nextgen.api.movemoney.v3.model.DepositDto;
import com.bt.nextgen.api.movemoney.v3.model.DepositKey;
import com.bt.nextgen.api.movemoney.v3.model.PayeeDto;
import com.bt.nextgen.api.movemoney.v3.model.RecurringDepositKey;
import com.bt.nextgen.api.movemoney.v3.validation.MovemoneyDtoErrorMapper;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto.ErrorType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.avaloq.PayAnyoneAccountDetailsImpl;
import com.bt.nextgen.service.avaloq.movemoney.DepositDetailsImpl;
import com.bt.nextgen.service.avaloq.movemoney.RecurringDepositDetailsImpl;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.btfin.panorama.service.integration.account.LinkedAccount;
import com.bt.nextgen.service.integration.movemoney.ContributionType;
import com.bt.nextgen.service.integration.movemoney.DepositDetails;
import com.bt.nextgen.service.integration.movemoney.DepositIntegrationService;
import com.bt.nextgen.service.integration.movemoney.RecurringDepositDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetailsIntegrationService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.RecurringFrequency;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@SuppressWarnings("squid:S1200")
@Service("DepositDtoServiceV3")
public class DepositDtoServiceImpl implements DepositDtoService {
    private static final Logger LOGGER = getLogger(DepositDtoServiceImpl.class);
    public static final String format = "dd MMM yyyy";
    public static final FastDateFormat dateFormat = FastDateFormat.getInstance(format);

    @Autowired
    private DepositIntegrationService depositIntegrationService;

    @Autowired
    private PayeeDetailsIntegrationService payeeDetailsIntegrationService;

    @Autowired
    private MovemoneyDtoErrorMapper movemoneyDtoErrorMapper;

    @Override
    public List<DepositDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        List<DepositDto> depositDtos = new ArrayList<>();

        WrapAccountIdentifier identifier = new WrapAccountIdentifierImpl();
        for (ApiSearchCriteria criteria : criteriaList) {
            switch (criteria.getProperty()) {
                case Attribute.ACCOUNT_ID:
                    identifier.setBpId(new EncodedString(criteria.getValue()).plainText());
                    break;
                default:
                    break;
            }
        }

        List<DepositDetails> deposits = depositIntegrationService.loadSavedDeposits(identifier, serviceErrors);

        for (DepositDetails deposit : deposits) {
            depositDtos.add(new DepositDto(deposit));
        }

        return depositDtos;
    }

    @Override
    public DepositDto find(DepositKey key, ServiceErrors serviceErrors) {
        DepositDetails deposit = depositIntegrationService.loadSavedDeposit(key.getDepositId(), serviceErrors);
        return new DepositDto(deposit);
    }

    @Override
    public void delete(DepositKey key, ServiceErrors serviceErrors) {
        if (key instanceof RecurringDepositKey) {
            depositIntegrationService.deleteRecurringDeposit(key.getDepositId(), serviceErrors);
        } else {
            depositIntegrationService.deleteDeposit(key.getDepositId(), serviceErrors);
        }
    }

    @Override
    public DepositDto validate(DepositDto depositDto, ServiceErrors serviceErrors) {
        DepositDto confirmDepositDto;
        ServiceErrors errors = new ServiceErrorsImpl();

        if (depositDto.getIsRecurring()) {
            RecurringDepositDetails recurringDepositDetails = toRecurringDepositDetails(depositDto, serviceErrors);
            recurringDepositDetails = depositIntegrationService.validateDeposit(recurringDepositDetails, errors);
            confirmDepositDto = toDepositDto(recurringDepositDetails, depositDto);
            processErrorsAndWarnings(confirmDepositDto, recurringDepositDetails);
        } else {
            DepositDetails depositDetails = toDepositDetails(depositDto, serviceErrors);
            depositDetails = depositIntegrationService.validateDeposit(depositDetails, errors);
            confirmDepositDto = toDepositDto(depositDetails, depositDto);
            processErrorsAndWarnings(confirmDepositDto, depositDetails);
        }

        return confirmDepositDto;
    }

    @Override
    public DepositDto submit(DepositDto depositDto, ServiceErrors serviceErrors) {
        DepositDto submitDepositDto = new DepositDto();
        ServiceErrors errors = new ServiceErrorsImpl();
        PayeeDetails payeeDetails = getPayeeDetails(depositDto, serviceErrors);

        if (isValidTransaction(payeeDetails, depositDto.getFromPayDto())) {
            if (depositDto.getIsRecurring()) {
                RecurringDepositDetails recurringDepositDetails = toRecurringDepositDetails(depositDto, serviceErrors);
                recurringDepositDetails = depositIntegrationService.submitDeposit(recurringDepositDetails, errors);
                submitDepositDto = toDepositDto(recurringDepositDetails, depositDto);
                processErrorsAndWarnings(submitDepositDto, recurringDepositDetails);
            } else {
                DepositDetails depositDetails = toDepositDetails(depositDto, serviceErrors);
                depositDetails = depositIntegrationService.submitDeposit(depositDetails, errors);
                submitDepositDto = toDepositDto(depositDetails, depositDto);
                processErrorsAndWarnings(submitDepositDto, depositDetails);
            }
            logDepositDetails(depositDto, submitDepositDto);
        } else {
            List<DomainApiErrorDto> errorList = new ArrayList<>();
            DomainApiErrorDto error = new DomainApiErrorDto(Constants.ACCT_NOT_IN_PAYEE_LIST, null, null, null, ErrorType.ERROR);
            errorList.add(error);
            submitDepositDto.setErrors(errorList);
        }

        return submitDepositDto;
    }

    @Override
    public DepositDto create(DepositDto depositDto, ServiceErrors serviceErrors) {
        DepositDto createDepositDto = new DepositDto();
        ServiceErrors errors = new ServiceErrorsImpl();

        if (depositDto.getIsRecurring()) {
            RecurringDepositDetails recurringDepositDetails = toRecurringDepositDetails(depositDto, serviceErrors);
            recurringDepositDetails = depositIntegrationService.createDeposit(recurringDepositDetails, errors);
            createDepositDto = toDepositDto(recurringDepositDetails, depositDto);
        } else {
            DepositDetails depositDetails = toDepositDetails(depositDto, serviceErrors);
            depositDetails = depositIntegrationService.createDeposit(depositDetails, errors);
            createDepositDto = toDepositDto(depositDetails, depositDto);
        }

        return createDepositDto;
    }

    @Override
    public DepositDto update(DepositDto depositDto, ServiceErrors serviceErrors) {
        DepositDto updateDepositDto = new DepositDto();
        ServiceErrors errors = new ServiceErrorsImpl();

        if (depositDto.getIsRecurring()) {
            RecurringDepositDetails recurringDepositDetails = toRecurringDepositDetails(depositDto, serviceErrors);
            recurringDepositDetails = depositIntegrationService.updateDeposit(recurringDepositDetails, errors);
            updateDepositDto = toDepositDto(recurringDepositDetails, depositDto);
        } else {
            DepositDetails depositDetails = toDepositDetails(depositDto, serviceErrors);
            depositDetails = depositIntegrationService.updateDeposit(depositDetails, errors);
            updateDepositDto = toDepositDto(depositDetails, depositDto);
        }

        return updateDepositDto;
    }

    protected PayeeDetails getPayeeDetails(DepositDto keyedObject, ServiceErrors serviceErrors) {
        WrapAccountIdentifierImpl wrapAccountIdentifierImpl = new WrapAccountIdentifierImpl();
        wrapAccountIdentifierImpl.setBpId(EncodedString.toPlainText(keyedObject.getAccountKey().getAccountId()));
        PayeeDetails payeeDetails = payeeDetailsIntegrationService.loadPayeeDetails(wrapAccountIdentifierImpl, serviceErrors);
        return payeeDetails;
    }

    protected RecurringDepositDetails toRecurringDepositDetails(DepositDto depositDto, ServiceErrors serviceErrors) {
        RecurringFrequency recurringFrequency = RecurringFrequency.valueOf(depositDto.getFrequency());
        Integer maxCount = null;
        DateTime endDate = null;
        if (depositDto.getEndRepeat() != null && ("setNumber").equalsIgnoreCase(depositDto.getEndRepeat())
                && null != depositDto.getEndRepeatNumber()) {
            maxCount = Integer.parseInt(depositDto.getEndRepeatNumber());
        } else if (null != depositDto.getEndRepeat() && ("setDate").equalsIgnoreCase(depositDto.getEndRepeat())
                && null != depositDto.getRepeatEndDate()) {
            endDate = depositDto.getRepeatEndDate();
        }

        ContributionType contributiontype = null;
        if (!StringUtils.isEmpty(depositDto.getDepositType())) {
            contributiontype = ContributionType.forName(depositDto.getDepositType());
        }

        PayeeDetails payee = getPayeeDetails(depositDto, serviceErrors);
        MoneyAccountIdentifier toAccount = new MoneyAccountIdentifierImpl();
        toAccount.setMoneyAccountId(payee.getMoneyAccountIdentifier().getMoneyAccountId());

        PayAnyoneAccountDetails fromAccount = new PayAnyoneAccountDetailsImpl();
        fromAccount.setAccount(depositDto.getFromPayDto().getAccountId());
        fromAccount.setBsb(depositDto.getFromPayDto().getCode());

        String depositId = null;
        if (depositDto.getKey() != null) {
            depositId = depositDto.getKey().getDepositId();
        }

        String transactionSeq = getTransactionSeq(depositDto);

        // Set Request Object for recurring deposit
        RecurringDepositDetails recurringDepositDetails = new RecurringDepositDetailsImpl(toAccount, fromAccount,
                depositDto.getAmount(), CurrencyType.AustralianDollar, depositDto.getDescription(),
                depositDto.getTransactionDate(), contributiontype, recurringFrequency, endDate, maxCount,
                movemoneyDtoErrorMapper.mapWarnings(depositDto.getWarnings()), depositId, transactionSeq);

        return recurringDepositDetails;
    }

    protected DepositDetails toDepositDetails(DepositDto depositDto, ServiceErrors serviceErrors) {
        ContributionType contributiontype = null;
        if (!StringUtils.isEmpty(depositDto.getDepositType())) {
            contributiontype = ContributionType.forName(depositDto.getDepositType());
        }

        PayeeDetails payee = getPayeeDetails(depositDto, serviceErrors);
        MoneyAccountIdentifier toAccount = new MoneyAccountIdentifierImpl();
        toAccount.setMoneyAccountId(payee.getMoneyAccountIdentifier().getMoneyAccountId());

        PayAnyoneAccountDetails fromAccount = new PayAnyoneAccountDetailsImpl();
        fromAccount.setAccount(depositDto.getFromPayDto().getAccountId());
        fromAccount.setBsb(depositDto.getFromPayDto().getCode());

        String depositId = null;
        if (depositDto.getKey() != null) {
            depositId = depositDto.getKey().getDepositId();
        }

        String transactionSeq = getTransactionSeq(depositDto);

        DepositDetails depositDetails = new DepositDetailsImpl(toAccount, fromAccount, depositDto.getAmount(),
                CurrencyType.AustralianDollar, depositDto.getDescription(), depositDto.getTransactionDate(), depositId,
                contributiontype, null, movemoneyDtoErrorMapper.mapWarnings(depositDto.getWarnings()), transactionSeq);

        return depositDetails;
    }

    protected DepositDto toDepositDto(DepositDetails deposit, DepositDto depositDtoKeyedObj) {
        DepositDto depositDto = new DepositDto();
        if (deposit != null) {
            depositDto = new DepositDto(depositDtoKeyedObj);
            depositDto.setAmount(deposit.getDepositAmount());
            depositDto.setDescription(deposit.getDescription());
            depositDto.setReceiptNumber(deposit.getReceiptNumber());
            if (deposit.getTransactionDate() != null)
                depositDto.setTransactionDate(deposit.getTransactionDate());
            if (deposit.getContributionType() != null)
                depositDto.setDepositType(deposit.getContributionType().getDisplayName());
            if (depositDto.getKey() == null)
                depositDto.setKey(new DepositKey(deposit.getReceiptNumber()));
            if (deposit.getRecurringFrequency() != null)
                depositDto.setFrequency(deposit.getRecurringFrequency().name());

            if (deposit instanceof RecurringDepositDetails) {
                RecurringDepositDetails recurringDeposit = (RecurringDepositDetails) deposit;
                if (recurringDeposit.getEndDate() != null)
                    depositDto.setRepeatEndDate(recurringDeposit.getEndDate());
                if (recurringDeposit.getStartDate() != null)
                    depositDto.setTransactionDate(recurringDeposit.getStartDate());
            }
        }
        return depositDto;
    }

    private void logDepositDetails(DepositDto confirmDepositDto, DepositDto submitDepositDto) {
        LOGGER.info(LoggingConstants.DEPOSITS_SUBMIT + "BEGIN");
        if (confirmDepositDto.getFromPayDto() != null) {
            LOGGER.info("FROM_ACCOUNT_ID" + LoggingConstants.PAYMENTS_DELIMITER + confirmDepositDto.getFromPayDto().getAccountId()
                    + LoggingConstants.ONBOARDING_DELIMITER + "FROM_ACCOUNT_BSB" + LoggingConstants.PAYMENTS_DELIMITER
                    + confirmDepositDto.getFromPayDto().getCode() + LoggingConstants.ONBOARDING_DELIMITER + "DEPOSIT_AMOUNT"
                    + LoggingConstants.PAYMENTS_DELIMITER + confirmDepositDto.getAmount() + LoggingConstants.ONBOARDING_DELIMITER
                    + "TRANSACTION_DATE" + LoggingConstants.PAYMENTS_DELIMITER + submitDepositDto.getTransactionDate());
        }
        LOGGER.info(LoggingConstants.DEPOSITS_SUBMIT + "END");
    }

    private boolean isValidTransaction(PayeeDetails payeeDetails, PayeeDto payee) {
        boolean isValid = false;
        List<LinkedAccount> linkedAccounts = payeeDetails.getLinkedAccountList();
        if (linkedAccounts != null) {
            for (LinkedAccount account : linkedAccounts) {
                if (account.getBsb().equals(payee.getCode()) && account.getAccountNumber().equals(payee.getAccountId())) {
                    isValid = true;
                    break;
                }
            }
        }

        return isValid;
    }

    private void processErrorsAndWarnings(DepositDto depositDto, DepositDetails depositDetails) {
        depositDto.setErrors(movemoneyDtoErrorMapper.map(depositDetails.getErrors()));
        depositDto.setWarnings(movemoneyDtoErrorMapper.map(depositDetails.getWarnings()));
    }

    private String getTransactionSeq(DepositDto depositDto) {
        String transactionSeq = null;
        if (!StringUtils.isEmpty(depositDto.getTransactionSeq())) {
            transactionSeq = depositDto.getTransactionSeq();
        }
        return transactionSeq;
    }
}
