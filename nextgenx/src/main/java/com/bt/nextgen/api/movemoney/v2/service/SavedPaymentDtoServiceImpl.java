package com.bt.nextgen.api.movemoney.v2.service;

import com.bt.nextgen.api.transaction.model.TransactionDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.util.ApplicationProperties;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.transaction.SavedPayment;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.movemoney.IndexationType;
import com.bt.nextgen.service.integration.movemoney.PensionOrderType;
import com.bt.nextgen.service.integration.movemoney.SavedPaymentIntegrationService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by L067218 on 27/01/2017.
 */
@Service
public class SavedPaymentDtoServiceImpl implements SavedPaymentDtoService {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private SavedPaymentIntegrationService savedPaymentIntegrationService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;


    @Override
    public List<TransactionDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        List<TransactionDto> transactionList = new ArrayList<>();
        String accountId = null;

        for (ApiSearchCriteria criteria : criteriaList) {
            if ("account-id".equalsIgnoreCase(criteria.getProperty())) {
                accountId = criteria.getValue();
                break;
            }
        }

        final WrapAccount account = accountService.loadWrapAccount(AccountKey.valueOf(accountId), serviceErrors);
        if (account != null) {
            transactionList = getPensionPaymentDetails(account.getAccountNumber(), serviceErrors);
        }

        return transactionList;
    }


    /**
     * Gets saved pension payments for the Super account
     *
     * @param accountNumber account number
     * @param serviceErrors Object to store errors.
     *
     * @return
     */
    private List<TransactionDto> getPensionPaymentDetails(String accountNumber, ServiceErrors serviceErrors) {
        final List<String> orderTypes = new ArrayList<>();
        for (PensionOrderType orderType : PensionOrderType.values()) {
            orderTypes.add(orderType.getId());
        }
        final List<SavedPayment> pensionPayment = savedPaymentIntegrationService.loadSavedPensionPayments(accountNumber,
                orderTypes, serviceErrors);

        return toTransactionDto(pensionPayment);
    }

    private List<TransactionDto> toTransactionDto(List<SavedPayment> savedPayments) {
        final List<TransactionDto> dtoList = new ArrayList<>();

        for (SavedPayment savedPayment : savedPayments) {
            final TransactionDto dto = new TransactionDto();
            if (savedPayment.getStordPos() != null){
                dto.setStordPosId(EncodedString.fromPlainText(savedPayment.getStordPos()).toString());
            }
            dto.setRecieptNumber(new BigDecimal(savedPayment.getTransactionId()));
            dto.setOrderType(savedPayment.getOrderType());
            dto.setFirstPayment(savedPayment.getFirstDate());
            dto.setPayee(savedPayment.getPayee());
            dto.setPayeeBsb(savedPayment.getPayeeBsb());
            dto.setPayeeAccount(savedPayment.getPayeeAccount());
            dto.setDescription(savedPayment.getDescription());
            dto.setFrequency(savedPayment.getFrequency() != null ? savedPayment.getFrequency().getDescription()
                    : Attribute.EMPTY_STRING);

            if (savedPayment.getPensionIndexationType() != null) {
                dto.setIndexationType(savedPayment.getPensionIndexationType().getLabel());

                if (savedPayment.getPensionIndexationType() == IndexationType.PERCENTAGE) {
                    dto.setIndexationAmount(savedPayment.getPensionIndexationPercent());
                }
                else {
                    dto.setIndexationAmount(savedPayment.getPensionIndexationAmount());
                }
            }
            dto.setNetAmount(savedPayment.getAmount());

            dto.setTransactionStatus(savedPayment.getTransactionStatus());
            dto.setTransSeqNo(savedPayment.getTransSeqNo());

            if (savedPayment.getPensionPaymentType() != null) {
                dto.setPensionPaymentType(savedPayment.getPensionPaymentType().getLabel());
            }

            dtoList.add(dto);
        }

        return dtoList;
    }
}
