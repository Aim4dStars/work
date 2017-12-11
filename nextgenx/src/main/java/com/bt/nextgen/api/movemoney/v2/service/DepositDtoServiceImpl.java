package com.bt.nextgen.api.movemoney.v2.service;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.bt.nextgen.api.account.v3.model.LinkedAccountStatusDto;
import com.bt.nextgen.api.movemoney.v2.util.TransactionReceiptHelper;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.draftaccount.LoggingConstants;
import com.bt.nextgen.api.movemoney.v2.model.DepositDto;
import com.bt.nextgen.api.movemoney.v2.model.PayeeDto;
import com.bt.nextgen.api.movemoney.v2.util.DepositUtil;
import com.bt.nextgen.api.movemoney.v3.util.DepositUtils;
import com.bt.nextgen.api.movemoney.v2.validation.MovemoneyDtoErrorMapper;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto.ErrorType;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.avaloq.PayAnyoneAccountDetailsImpl;
import com.bt.nextgen.service.avaloq.movemoney.DepositDetailsImpl;
import com.bt.nextgen.service.avaloq.movemoney.RecurringDepositDetailsImpl;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.btfin.panorama.service.integration.account.LinkedAccount;
import com.bt.nextgen.service.integration.movemoney.DepositDetails;
import com.bt.nextgen.service.integration.movemoney.DepositIntegrationService;
import com.bt.nextgen.service.integration.movemoney.RecurringDepositDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetailsIntegrationService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

@Service("DepositDtoServiceV2")
@SuppressWarnings("all")
public class DepositDtoServiceImpl implements DepositDtoService {
    private static final Logger LOGGER = getLogger(DepositDtoServiceImpl.class);

    @Autowired
    private DepositIntegrationService depositIntegrationService;

    @Autowired
    private PayeeDetailsIntegrationService payeeDetailsIntegrationService;

    @Autowired
    private MovemoneyDtoErrorMapper movemoneyDtoErrorMapper;

    @Autowired
    private TransactionReceiptHelper transactionReceiptHelper;

    @Autowired
    protected StaticIntegrationService staticIntegrationService;

    private Map<String, DepositDto> receiptObj = new HashMap<String, DepositDto>();

    private List<DepositDto> toDepositPayeesDto(List<LinkedAccount> linkedAccountList, PayeeDetails payeeDetails) {
        final List<DepositDto> depositPayeesList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(linkedAccountList)) {
            for (LinkedAccount linkedAccountModel : linkedAccountList) {
                final DepositDto depositDto = new DepositDto();

                if (null != linkedAccountModel) {
                    PayeeDto payeeDto = new PayeeDto();
                    payeeDto.setAccountId(linkedAccountModel.getAccountNumber());
                    payeeDto.setAccountName(linkedAccountModel.getName());
                    payeeDto.setCode(linkedAccountModel.getBsb());

                    if (null != linkedAccountModel.getNickName()) {
                        payeeDto.setNickname(linkedAccountModel.getNickName());
                    }

                    payeeDto.setPrimary(linkedAccountModel.isPrimary());
                    payeeDto.setType("Deposit");
                    LinkedAccountStatusDto linkedaccountStatus = DepositUtils.linkedAccountStatus(linkedAccountModel,staticIntegrationService);
                    payeeDto.setLinkedAccountStatus(linkedaccountStatus);
                    depositDto.setFromPayDto(payeeDto);

                    PayeeDto toPayeeDto = new PayeeDto();
                    toPayeeDto.setAccountId(payeeDetails.getCashAccount().getAccountNumber());
                    toPayeeDto.setAccountName(payeeDetails.getCashAccount().getAccountName());
                    toPayeeDto.setCode(payeeDetails.getCashAccount().getBsb());
                    depositDto.setToPayeeDto(toPayeeDto);

                    depositPayeesList.add(depositDto);
                }
            }
            return DepositUtil.movePrimaryOnTop(depositPayeesList);
        }

        return depositPayeesList;
    }

    @Override
    public List<DepositDto> search(AccountKey key, ServiceErrors serviceErrors) {
        return loadPayeesForDeposits(key);
    }

    @Override
    public List<DepositDto> loadPayeesForDeposits(AccountKey key) {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        final ArrayList<LinkedAccount> payeeList = new ArrayList<>();
        final WrapAccountIdentifierImpl wrapAccountIdentifierImpl = new WrapAccountIdentifierImpl();
        wrapAccountIdentifierImpl.setBpId(EncodedString.toPlainText(key.getAccountId()));

        final PayeeDetails payeeDetails = payeeDetailsIntegrationService.loadPayeeDetails(wrapAccountIdentifierImpl,
                serviceErrors);
        if (payeeDetails != null && CollectionUtils.isNotEmpty(payeeDetails.getLinkedAccountList())) {
            for (LinkedAccount linkedModel : payeeDetails.getLinkedAccountList()) {
                if (null != linkedModel) {
                    payeeList.add(linkedModel);
                }
            }
        }
        return toDepositPayeesDto(payeeList, payeeDetails);
    }

    public MoneyAccountIdentifier getMoneyAccountIdentifier(DepositDto keyedObject, ServiceErrors serviceErrors) {
        MoneyAccountIdentifier macId = new MoneyAccountIdentifierImpl();
        try {
            WrapAccountIdentifierImpl wrapAccountIdentifierImpl = new WrapAccountIdentifierImpl();
            wrapAccountIdentifierImpl.setBpId(EncodedString.toPlainText(keyedObject.getKey().getAccountId()));
            PayeeDetails payeeDetails = payeeDetailsIntegrationService.loadPayeeDetails(wrapAccountIdentifierImpl, serviceErrors);
            macId.setMoneyAccountId(payeeDetails.getMoneyAccountIdentifier().getMoneyAccountId());
        } catch (Exception e) {
            LOGGER.error("Error in getting money account Id for portfolioId {}");
        }

        return macId;
    }

    @Override
    public DepositDto validate(DepositDto depositDto, ServiceErrors serviceErrors) {
        DepositDto confirmDepositDto;
        MoneyAccountIdentifier moneyAccountIdentifier = getMoneyAccountIdentifier(depositDto, serviceErrors);
        PayAnyoneAccountDetails payAnyOneAccounts = new PayAnyoneAccountDetailsImpl();
        payAnyOneAccounts.setAccount(depositDto.getFromPayDto().getAccountId());
        payAnyOneAccounts.setBsb(depositDto.getFromPayDto().getCode());
        ServiceErrors errors = new ServiceErrorsImpl();

        if (depositDto.getIsRecurring()) {
            RecurringDepositDetails recurringDepositDetails = DepositUtil.populateRecurDepositDetailsReq(depositDto,
                    payAnyOneAccounts, moneyAccountIdentifier);
            recurringDepositDetails = depositIntegrationService.validateDeposit(recurringDepositDetails, errors);
            confirmDepositDto = DepositUtil.toDepositDto(recurringDepositDetails, depositDto);
            processErrorsAndWarnings(confirmDepositDto, recurringDepositDetails);
        } else {
            DepositDetails depositDetails = DepositUtil.populateDepositDetailsReq(depositDto, payAnyOneAccounts,
                    moneyAccountIdentifier);
            depositDetails = depositIntegrationService.validateDeposit(depositDetails, errors);
            confirmDepositDto = DepositUtil.toDepositDto(depositDetails, depositDto);
            processErrorsAndWarnings(confirmDepositDto, depositDetails);
        }

        return confirmDepositDto;
    }

    @Override
    public DepositDto submit(DepositDto depositDto, ServiceErrors serviceErrors) {
        DepositDto submitDepositDto = new DepositDto();
        ServiceErrors errors = new ServiceErrorsImpl();
        MoneyAccountIdentifier moneyAccountIdentifier = new MoneyAccountIdentifierImpl();
        WrapAccountIdentifierImpl wrapAccountIdentifierImpl = new WrapAccountIdentifierImpl();
        wrapAccountIdentifierImpl.setBpId(EncodedString.toPlainText(depositDto.getKey().getAccountId()));
        PayeeDetails payeeDetails = payeeDetailsIntegrationService.loadPayeeDetails(wrapAccountIdentifierImpl, serviceErrors);

        if (isValidTransaction(payeeDetails, depositDto.getFromPayDto())) {
            moneyAccountIdentifier.setMoneyAccountId(payeeDetails.getMoneyAccountIdentifier().getMoneyAccountId());
            PayAnyoneAccountDetails payAnyOneAccounts = new PayAnyoneAccountDetailsImpl();
            payAnyOneAccounts.setAccount(depositDto.getFromPayDto().getAccountId());
            payAnyOneAccounts.setBsb(depositDto.getFromPayDto().getCode());

            if (depositDto.getIsRecurring()) {
                RecurringDepositDetails recurringDepositDetails = DepositUtil.populateRecurDepositDetailsReq(depositDto,
                        payAnyOneAccounts, moneyAccountIdentifier);
                if (depositDto.getWarnings() != null) {
                    ((RecurringDepositDetailsImpl) recurringDepositDetails)
                            .setWarnings(movemoneyDtoErrorMapper.mapWarnings(depositDto.getWarnings()));
                }
                recurringDepositDetails = depositIntegrationService.submitDeposit(recurringDepositDetails, errors);
                submitDepositDto = DepositUtil.toDepositDto(recurringDepositDetails, depositDto);
                processErrorsAndWarnings(submitDepositDto, recurringDepositDetails);
            } else {
                DepositDetails depositDetails = DepositUtil.populateDepositDetailsReq(depositDto, payAnyOneAccounts,
                        moneyAccountIdentifier);
                if (depositDto.getWarnings() != null) {
                    ((DepositDetailsImpl) depositDetails)
                            .setWarnings(movemoneyDtoErrorMapper.mapWarnings(depositDto.getWarnings()));
                }
                depositDetails = depositIntegrationService.submitDeposit(depositDetails, errors);
                submitDepositDto = DepositUtil.toDepositDto(depositDetails, depositDto);
                processErrorsAndWarnings(submitDepositDto, depositDetails);
            }
            logDepositDetails(depositDto, submitDepositDto);
            transactionReceiptHelper.storeReceiptData(submitDepositDto);
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
        return createDeposit(depositDto, serviceErrors);
    }

    @Override
    public DepositDto update(DepositDto depositDto, ServiceErrors serviceErrors) {
        return createDeposit(depositDto, serviceErrors);
    }

    public DepositDto createDeposit(DepositDto depositDto, ServiceErrors serviceErrors) {
        DepositDto createDepositDto = new DepositDto();
        ServiceErrors errors = new ServiceErrorsImpl();
        MoneyAccountIdentifier moneyAccountIdentifier = new MoneyAccountIdentifierImpl();
        WrapAccountIdentifierImpl wrapAccountIdentifierImpl = new WrapAccountIdentifierImpl();
        wrapAccountIdentifierImpl.setBpId(EncodedString.toPlainText(depositDto.getKey().getAccountId()));
        PayeeDetails payeeDetails = payeeDetailsIntegrationService.loadPayeeDetails(wrapAccountIdentifierImpl, serviceErrors);

        moneyAccountIdentifier.setMoneyAccountId(payeeDetails.getMoneyAccountIdentifier().getMoneyAccountId());
        PayAnyoneAccountDetails payAnyOneAccounts = new PayAnyoneAccountDetailsImpl();
        payAnyOneAccounts.setAccount(depositDto.getFromPayDto().getAccountId());
        payAnyOneAccounts.setBsb(depositDto.getFromPayDto().getCode());

        if (depositDto.getIsRecurring()) {
            RecurringDepositDetails recurringDepositDetails = DepositUtil.populateRecurDepositDetailsReq(depositDto,
                    payAnyOneAccounts, moneyAccountIdentifier);
            recurringDepositDetails = depositIntegrationService.createDeposit(recurringDepositDetails, errors);
            createDepositDto = DepositUtil.toDepositDto(recurringDepositDetails, depositDto);
        } else {
            DepositDetails depositDetails = DepositUtil.populateDepositDetailsReq(depositDto, payAnyOneAccounts,
                    moneyAccountIdentifier);
            depositDetails = depositIntegrationService.createDeposit(depositDetails, errors);
            createDepositDto = DepositUtil.toDepositDto(depositDetails, depositDto);
        }

        return createDepositDto;
    }

    @Override
    public Map<String, DepositDto> loadDepositReceipts() {
        return receiptObj;
    }

    private void logDepositDetails(DepositDto confirmDepositDto, DepositDto submitDepositDto) {
        LOGGER.info(LoggingConstants.DEPOSITS_SUBMIT + "BEGIN");
        if (confirmDepositDto.getFromPayDto() != null && confirmDepositDto.getToPayeeDto() != null) {
            LOGGER.info("FROM_ACCOUNT_ID" + LoggingConstants.PAYMENTS_DELIMITER + confirmDepositDto.getFromPayDto().getAccountId()
                    + LoggingConstants.ONBOARDING_DELIMITER + "FROM_ACCOUNT_BSB" + LoggingConstants.PAYMENTS_DELIMITER
                    + confirmDepositDto.getFromPayDto().getCode() + LoggingConstants.ONBOARDING_DELIMITER + "TO_ACCOUNT_ID"
                    + LoggingConstants.PAYMENTS_DELIMITER + confirmDepositDto.getToPayeeDto().getAccountId()
                    + LoggingConstants.ONBOARDING_DELIMITER + "TO_ACCOUNT_BSB" + LoggingConstants.PAYMENTS_DELIMITER
                    + confirmDepositDto.getToPayeeDto().getCode() + LoggingConstants.ONBOARDING_DELIMITER + "DEPOSIT_AMOUNT"
                    + LoggingConstants.PAYMENTS_DELIMITER + confirmDepositDto.getAmount() + LoggingConstants.ONBOARDING_DELIMITER
                    + "PAYEE_TYPE" + LoggingConstants.PAYMENTS_DELIMITER + confirmDepositDto.getToPayeeDto().getPayeeType()
                    + LoggingConstants.ONBOARDING_DELIMITER + "TRANSACTION_DATE" + LoggingConstants.PAYMENTS_DELIMITER
                    + submitDepositDto.getTransactionDate());
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
        if (depositDetails instanceof RecurringDepositDetails) {
            if (((RecurringDepositDetails) depositDetails).getErrors() != null) {
                depositDto.setErrors(movemoneyDtoErrorMapper.map(((RecurringDepositDetails) depositDetails).getErrors()));
            }
            if (((RecurringDepositDetails) depositDetails).getWarnings() != null) {
                depositDto.setWarnings(movemoneyDtoErrorMapper.map(((RecurringDepositDetails) depositDetails).getWarnings()));
            }
        } else {
            if (depositDetails.getErrors() != null) {
                depositDto.setErrors(movemoneyDtoErrorMapper.map(depositDetails.getErrors()));
            }
            if (depositDetails.getWarnings() != null) {
                depositDto.setWarnings(movemoneyDtoErrorMapper.map(depositDetails.getWarnings()));
            }
        }
    }

    @Override
    public DepositDto find(AccountKey key, ServiceErrors serviceErrors) {
        // required by SearchByKeyDtoService interface
        return null;
    }
}
