package com.bt.nextgen.api.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.btfin.panorama.service.integration.RecurringFrequency;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.api.account.v1.model.DepositDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.avaloq.deposit.DepositDetailsImpl;
import com.bt.nextgen.service.avaloq.deposit.RecurringDepositDetailsImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.bt.nextgen.service.integration.deposit.DepositDetails;
import com.bt.nextgen.service.integration.deposit.RecurringDepositDetails;

/**
 * @deprecated Use V2
 */
@Deprecated
public class DepositUtil {
    private static final Logger logger = LoggerFactory.getLogger(DepositUtil.class);

    public static final SimpleDateFormat DEFAULT_FORMAT = new SimpleDateFormat(ApiConstants.DATE_FORMAT);

    public static RecurringDepositDetails populateRecurDepositDetailsReq(DepositDto keyedObject,
            PayAnyoneAccountDetails payAnyOneAccounts, MoneyAccountIdentifier moneyAccountIdentifier) {

        // Get recurring frequency
        RecurringFrequency recurringFrequency = RecurringFrequency.getRecurringFrequencyByDescription(keyedObject.getFrequency());

        // Set Request Object for recurring deposit
        RecurringDepositDetails confirmDepositConversationRecurr = new RecurringDepositDetailsImpl();
        confirmDepositConversationRecurr.setCurrencyType(CurrencyType.AustralianDollar);
        confirmDepositConversationRecurr.setPayAnyoneAccountDetails(payAnyOneAccounts);
        confirmDepositConversationRecurr.setMoneyAccountIdentifier(moneyAccountIdentifier);
        confirmDepositConversationRecurr.setDescription(keyedObject.getDescription());
        confirmDepositConversationRecurr.setDepositAmount(keyedObject.getAmount());
        confirmDepositConversationRecurr.setTransactionDate(new Date(keyedObject.getTransactionDate()));
        confirmDepositConversationRecurr.setRecurringFrequency(recurringFrequency);
        if (StringUtils.equalsIgnoreCase(keyedObject.getEndRepeat(), "setNumber")
                && null != keyedObject.getEndRepeatNumber())
            confirmDepositConversationRecurr.setMaxCount(Integer.parseInt(keyedObject.getEndRepeatNumber()));
        else if (StringUtils.equalsIgnoreCase(keyedObject.getEndRepeat(), "setDate")
                && null != keyedObject.getRepeatEndDate())
            confirmDepositConversationRecurr.setEndDate(new Date(keyedObject.getRepeatEndDate()));

        return confirmDepositConversationRecurr;
    }

    public static DepositDetails populateDepositDetailsReq(DepositDto keyedObject, PayAnyoneAccountDetails payAnyOneAccounts,
            MoneyAccountIdentifier moneyAccountIdentifier) {

        DepositDetails confirmDepositConversation = new DepositDetailsImpl();
        confirmDepositConversation.setCurrencyType(CurrencyType.AustralianDollar);
        confirmDepositConversation.setPayAnyoneAccountDetails(payAnyOneAccounts);
        confirmDepositConversation.setMoneyAccountIdentifier(moneyAccountIdentifier);
        confirmDepositConversation.setDescription(keyedObject.getDescription());
        confirmDepositConversation.setDepositAmount(keyedObject.getAmount());
        confirmDepositConversation.setTransactionDate(new Date(keyedObject.getTransactionDate()));

        return confirmDepositConversation;
    }

    public static DepositDto toDepositDto(RecurringDepositDetails deposit, DepositDto depositDtoKeyedObj) {
        DepositDto depositDto = new DepositDto();
        if (null != deposit) {
            try {
                depositDto = new DepositDto(depositDtoKeyedObj);
                depositDto.setAmount(deposit.getDepositAmount());
                depositDto.setFrequency(deposit.getRecurringFrequency().getDescription());
                depositDto.setDescription(deposit.getDescription());
                depositDto.setRecieptNumber(deposit.getRecieptNumber());
                depositDto.setRecieptId(EncodedString.fromPlainText(deposit.getRecieptNumber()).toString());
                depositDto.setTransactionDate(DEFAULT_FORMAT.format(deposit.getTransactionDate()));
                if (null != deposit.getEndDate())
                    depositDto.setRepeatEndDate(DEFAULT_FORMAT.format(deposit.getEndDate()));
                if (null != deposit.getStartDate())
                    depositDto.setTransactionDate(DEFAULT_FORMAT.format(deposit.getStartDate()));
            } catch (Exception e) {
                logger.error("Error in preparing Recurring Deposit response object", e);
                e.printStackTrace();
            }
        }
        return depositDto;
    }

    public static DepositDto toDepositDto(DepositDetails deposit, DepositDto depositDtoKeyedObj) {
        DepositDto depositDto = new DepositDto();
        if (null != deposit) {
            try {
                depositDto = new DepositDto(depositDtoKeyedObj);
                depositDto.setAmount(deposit.getDepositAmount());
                depositDto.setDescription(deposit.getDescription());
                depositDto.setRecieptNumber(deposit.getRecieptNumber());
                depositDto.setRecieptId(EncodedString.fromPlainText(deposit.getRecieptNumber()).toString());
                depositDto.setTransactionDate(DEFAULT_FORMAT.format(deposit.getTransactionDate()));
            } catch (Exception e) {
                logger.error("Error in preparing Deposit response object", e);
                e.printStackTrace();
            }
        }
        return depositDto;
    }

    public static List<DepositDto> movePrimaryOnTop(List<DepositDto> items) {
        int index = 0;
        List<DepositDto> copy;
        for (DepositDto item : items)
            if (item.getFromPayDto().isPrimary()) {
                index = items.indexOf(item);
            }
        if (index >= 0) {
            copy = new ArrayList<DepositDto>(items.size());
            copy.addAll(items.subList(0, index));
            copy.add(0, items.get(index));
            copy.addAll(items.subList(index + 1, items.size()));
        } else {
            copy = new ArrayList<DepositDto>(items);
        }
        return copy;
    }
}
