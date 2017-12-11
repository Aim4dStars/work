package com.bt.nextgen.api.movemoney.v2.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.btfin.panorama.service.integration.RecurringFrequency;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.bt.nextgen.api.movemoney.v2.model.DepositDto;
import com.bt.nextgen.api.movemoney.v2.model.PayeeDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.avaloq.movemoney.DepositDetailsImpl;
import com.bt.nextgen.service.avaloq.movemoney.RecurringDepositDetailsImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.bt.nextgen.service.integration.movemoney.ContributionType;
import com.bt.nextgen.service.integration.movemoney.DepositDetails;
import com.bt.nextgen.service.integration.movemoney.RecurringDepositDetails;

public class DepositUtil {
    private static String format = "dd MMM yyyy";

    public static RecurringDepositDetails populateRecurDepositDetailsReq(DepositDto depositDto,
            PayAnyoneAccountDetails payAnyOneAccounts, MoneyAccountIdentifier moneyAccountIdentifier) {

        RecurringFrequency recurringFrequency = RecurringFrequency.getRecurringFrequencyByDescription(depositDto.getFrequency());

        Integer maxCount = null;
        DateTime endDate = null;
        DateTime transactionDate = null;
        if (StringUtils.equalsIgnoreCase(depositDto.getEndRepeat(), "setNumber")
                && null != depositDto.getEndRepeatNumber()) {
            maxCount = Integer.parseInt(depositDto.getEndRepeatNumber());
        } else if (StringUtils.equalsIgnoreCase(depositDto.getEndRepeat(), "setDate")
                && null != depositDto.getRepeatEndDate()) {
            endDate = DateTime.parse(depositDto.getRepeatEndDate(), DateTimeFormat.forPattern(format));
        }

        if (depositDto.getTransactionDate() != null) {
            transactionDate = DateTime.parse(depositDto.getTransactionDate(), DateTimeFormat.forPattern(format));
        }

        ContributionType contributiontype = null;
        if (!StringUtils.isEmpty(depositDto.getDepositType())) {
            contributiontype = ContributionType.forName(depositDto.getDepositType());
        }

        // Set Request Object for recurring deposit
        RecurringDepositDetails recurringDepositDetails = new RecurringDepositDetailsImpl(moneyAccountIdentifier,
                payAnyOneAccounts, depositDto.getAmount(), CurrencyType.AustralianDollar, depositDto.getDescription(),
                transactionDate, contributiontype, recurringFrequency, endDate, maxCount);

        return recurringDepositDetails;
    }

    public static DepositDetails populateDepositDetailsReq(DepositDto depositDto, PayAnyoneAccountDetails payAnyOneAccounts,
            MoneyAccountIdentifier moneyAccountIdentifier) {

        DateTime transactionDate = null;
        if (depositDto.getTransactionDate() != null) {
            transactionDate = DateTime.parse(depositDto.getTransactionDate(), DateTimeFormat.forPattern(format));
        }

        ContributionType contributiontype = null;
        if (!StringUtils.isEmpty(depositDto.getDepositType())) {
            contributiontype = ContributionType.forName(depositDto.getDepositType());
        }

        DepositDetails depositDetails = new DepositDetailsImpl(moneyAccountIdentifier, payAnyOneAccounts, depositDto.getAmount(),
                CurrencyType.AustralianDollar, depositDto.getDescription(), transactionDate, contributiontype);

        return depositDetails;
    }

    public static DepositDto toDepositDto(RecurringDepositDetails deposit, DepositDto depositDtoKeyedObj) {
        DepositDto depositDto = new DepositDto();
        if (null != deposit) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            depositDto = new DepositDto(depositDtoKeyedObj);
            depositDto.setAmount(deposit.getDepositAmount());
            if (deposit.getRecurringFrequency() != null) {
                depositDto.setFrequency(deposit.getRecurringFrequency().getDescription());
            }
            depositDto.setDescription(deposit.getDescription());
            depositDto.setReceiptNumber(deposit.getReceiptNumber());
            depositDto.setReceiptId(EncodedString.fromPlainText(deposit.getReceiptNumber()).toString());
            if (deposit.getTransactionDate() != null)
                depositDto.setTransactionDate(dateFormat.format(deposit.getTransactionDate().toDate()));
            if (deposit.getMaxCount() != null) {
                depositDto.setEndRepeatNumber(deposit.getMaxCount().toString());
            }
            if (deposit.getEndDate() != null)
                depositDto.setRepeatEndDate(dateFormat.format(deposit.getEndDate().toDate()));
            if (deposit.getStartDate() != null)
                depositDto.setTransactionDate(dateFormat.format(deposit.getStartDate().toDate()));
            if (deposit.getContributionType() != null)
                depositDto.setDepositType(deposit.getContributionType().getDisplayName());
        }
        return depositDto;
    }

    public static DepositDto toDepositDto(DepositDetails deposit, DepositDto depositDtoKeyedObj) {
        DepositDto depositDto = new DepositDto();
        if (null != deposit) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            depositDto = new DepositDto(depositDtoKeyedObj);
            depositDto.setAmount(deposit.getDepositAmount());
            depositDto.setDescription(deposit.getDescription());
            depositDto.setReceiptNumber(deposit.getReceiptNumber());
            depositDto.setReceiptId(EncodedString.fromPlainText(deposit.getReceiptNumber()).toString());
            if (deposit.getTransactionDate() != null)
                depositDto.setTransactionDate(dateFormat.format(deposit.getTransactionDate().toDate()));
            if (deposit.getContributionType() != null)
                depositDto.setDepositType(deposit.getContributionType().getDisplayName());
        }
        return depositDto;
    }

    public static List<DepositDto> movePrimaryOnTop(List<DepositDto> payeeList) {
        int index = 0;
        List<DepositDto> copy;
        final List<DepositDto> items = sortPayeeList(payeeList);
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

    private DepositUtil() {
        // Private Constructor - to hide the public implicit one
    }

    /**
     * @param payeeList
     *            - List of the payees for the deposits
     * @return Returns the sorted list of the payees
     */
    public static List<DepositDto> sortPayeeList(List<DepositDto> payeeList) {
        Collections.sort(payeeList, new Comparator<DepositDto>() {
            @Override
            public int compare(DepositDto d1, DepositDto d2) {
                // Skip sorting if there is no fromPayDto
                if (d1 == null || d2 == null || d1.getFromPayDto() == null || d2.getFromPayDto() == null) {
                    return 0;
                } else {
                    final PayeeDto f1 = d1.getFromPayDto();
                    final PayeeDto f2 = d2.getFromPayDto();
                    if (null == f1.getNickname() && null == f2.getNickname())
                        return f1.getAccountName().compareToIgnoreCase(f2.getAccountName());
                    else if (null != f1.getNickname() && null == f2.getNickname())
                        return f1.getNickname().compareToIgnoreCase(f2.getAccountName());
                    else if (null == f1.getNickname() && null != f2.getNickname())
                        return f1.getAccountName().compareToIgnoreCase(f2.getNickname());
                    else
                        return f1.getNickname().compareToIgnoreCase(f2.getNickname());
                }
            }
        });
        return payeeList;
    }
}
