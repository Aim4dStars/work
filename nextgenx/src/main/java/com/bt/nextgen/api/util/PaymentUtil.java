package com.bt.nextgen.api.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.bt.nextgen.core.web.ApiFormatter;
import org.apache.commons.lang.StringUtils;

import com.bt.nextgen.api.account.v1.model.PaymentDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.domain.PaymentRepeats;
import com.bt.nextgen.payments.domain.PaymentRepeatsEnd;
import com.bt.nextgen.service.avaloq.PayAnyoneAccountDetailsImpl;
import com.bt.nextgen.service.avaloq.payments.BpayBillerImpl;
import com.bt.nextgen.service.avaloq.payments.PaymentDetailsImpl;
import com.bt.nextgen.service.avaloq.payments.RecurringPaymentDetailsImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.btfin.panorama.service.integration.RecurringFrequency;
import com.btfin.panorama.service.integration.account.Biller;
import com.btfin.panorama.service.integration.account.LinkedAccount;
import com.bt.nextgen.service.integration.account.PayAnyOne;
import com.bt.nextgen.service.integration.payments.BpayBiller;
import com.bt.nextgen.service.integration.payments.PaymentDetails;
import com.bt.nextgen.service.integration.payments.RecurringPaymentDetails;

/**
 * @deprecated Use V2
 */
@Deprecated
@SuppressWarnings("squid:MethodCyclomaticComplexity")
public class PaymentUtil {

    public static final SimpleDateFormat DEFAULT_FORMAT = new SimpleDateFormat(ApiConstants.DATE_FORMAT);

    public static RecurringPaymentDetails populateRecurringPaymentDetails(MoneyAccountIdentifier moneyAccountIdentifier,
            PaymentDto paymentDtoTrx) {
        RecurringPaymentDetails recurringPaymentDetails = new RecurringPaymentDetailsImpl();
        PayAnyoneAccountDetails payAnyOneAccountDetails = new PayAnyoneAccountDetailsImpl();
        BpayBiller bpayBiller = new BpayBillerImpl();

        if (paymentDtoTrx.getToPayteeDto().getPayeeType().equalsIgnoreCase("BPAY")) {
            bpayBiller.setPayeeName(paymentDtoTrx.getToPayteeDto().getAccountName());
            bpayBiller.setBillerCode(paymentDtoTrx.getToPayteeDto().getCode());
            bpayBiller.setCustomerReferenceNo(paymentDtoTrx.getToPayteeDto().getCrn());
            recurringPaymentDetails.setBpayBiller(bpayBiller);
        } else {
            payAnyOneAccountDetails.setAccount(EncodedString.toPlainText(paymentDtoTrx.getToPayteeDto().getAccountKey()));
            payAnyOneAccountDetails.setBsb(!paymentDtoTrx.getToPayteeDto().getPayeeType().equals(PayeeType.BPAY)
                    ? ApiFormatter.formatBsb(paymentDtoTrx.getToPayteeDto().getCode()) : paymentDtoTrx.getToPayteeDto().getCode());
            recurringPaymentDetails.setPayAnyoneBeneficiary(payAnyOneAccountDetails);
        }

        recurringPaymentDetails.setAmount(paymentDtoTrx.getAmount());

        recurringPaymentDetails.setBenefeciaryInfo(paymentDtoTrx.getDescription());
        recurringPaymentDetails.setCurrencyType(CurrencyType.AustralianDollar);
        recurringPaymentDetails.setMoneyAccount(moneyAccountIdentifier);
        // TODO Seriously!
        recurringPaymentDetails.setTransactionDate(new Date(paymentDtoTrx.getTransactionDate()));
        recurringPaymentDetails.setRecurringFrequency(RecurringFrequency.getRecurringFrequencyByDescription(paymentDtoTrx.getFrequency()));
        // TODO Seriously!
        recurringPaymentDetails.setPaymentDate(new Date(paymentDtoTrx.getTransactionDate()));
        String payeeName= getPayeeName(paymentDtoTrx);
        recurringPaymentDetails.setPayeeName(payeeName);
        recurringPaymentDetails.setBusinessChannel(paymentDtoTrx.getBusinessChannel());
        recurringPaymentDetails.setClientIp(paymentDtoTrx.getClientIp());

        String paymentRepeats = PaymentRepeats.getPaymentRepeat(paymentDtoTrx.getEndRepeat());
        if (null != paymentRepeats && paymentRepeats.equalsIgnoreCase(PaymentRepeatsEnd.REPEAT_END_DATE.toString()))
            recurringPaymentDetails.setEndDate(new Date(paymentDtoTrx.getRepeatEndDate()));
        else if (null != paymentRepeats && paymentRepeats.equalsIgnoreCase(PaymentRepeatsEnd.REPEAT_NUMBER.toString()))
            recurringPaymentDetails.setMaxCount(Integer.valueOf(paymentDtoTrx.getEndRepeatNumber()));

        return recurringPaymentDetails;
    }

    public static PaymentDetails populatePaymentDetails(MoneyAccountIdentifier moneyAccountIdentifier, PaymentDto paymentDtoTrx) {
        PaymentDetails paymentDetails = new PaymentDetailsImpl();
        PayAnyoneAccountDetails payAnyOneAccountDetails = new PayAnyoneAccountDetailsImpl();
        BpayBiller bpayBiller = new BpayBillerImpl();

        if (paymentDtoTrx.getToPayteeDto().getPayeeType().equalsIgnoreCase("BPAY")) {
            bpayBiller.setPayeeName(paymentDtoTrx.getToPayteeDto().getAccountName());
            bpayBiller.setBillerCode(paymentDtoTrx.getToPayteeDto().getCode());
            bpayBiller.setCustomerReferenceNo(paymentDtoTrx.getToPayteeDto().getCrn());
            paymentDetails.setBpayBiller(bpayBiller);
        } else {
            payAnyOneAccountDetails.setAccount(EncodedString.toPlainText(paymentDtoTrx.getToPayteeDto().getAccountKey()));
            payAnyOneAccountDetails.setBsb(!paymentDtoTrx.getToPayteeDto().getPayeeType().equals(PayeeType.BPAY)
                    ? ApiFormatter.formatBsb(paymentDtoTrx.getToPayteeDto().getCode()) : paymentDtoTrx.getToPayteeDto().getCode());
            paymentDetails.setPayAnyoneBeneficiary(payAnyOneAccountDetails);
        }

        paymentDetails.setAmount(paymentDtoTrx.getAmount());
        //Setting description in PayeeName as the fields are swapped in ABS for payment orders
        String payeeName= getPayeeName(paymentDtoTrx);
        paymentDetails.setPayeeName(payeeName);
        paymentDetails.setCurrencyType(CurrencyType.AustralianDollar);
        paymentDetails.setMoneyAccount(moneyAccountIdentifier);
        paymentDetails.setPayAnyoneBeneficiary(payAnyOneAccountDetails);
        paymentDetails.setTransactionDate(new Date(paymentDtoTrx.getTransactionDate()));
        //Setting payeeName in beneficiaryInfo as the fields are swapped in ABS for payment orders
        paymentDetails.setBenefeciaryInfo(paymentDtoTrx.getDescription());
        paymentDetails.setClientIp(paymentDtoTrx.getClientIp());
        paymentDetails.setBusinessChannel(paymentDtoTrx.getBusinessChannel());

        return paymentDetails;
    }

    public static PaymentDto toConfirmPaymentDto(RecurringPaymentDetails paymentDetails, PaymentDto paymentDtoKeyedObj) {
        PaymentDto confirmPaymentDto = new PaymentDto();
        if (null != paymentDetails) {
            confirmPaymentDto = toConfirmPaymentDto((PaymentDetails) paymentDetails, paymentDtoKeyedObj);

            if (null != paymentDetails.getEndDate())
                confirmPaymentDto.setRepeatEndDate(ApiFormatter.asShortDate(paymentDetails.getEndDate()));
            if (null != paymentDetails.getStartDate())
                confirmPaymentDto.setTransactionDate(ApiFormatter.asShortDate(paymentDetails.getStartDate()));
        }
        return confirmPaymentDto;
    }

    public static PaymentDto toConfirmPaymentDto(PaymentDetails paymentDetails, PaymentDto paymentDtoKeyedObj) {
        PaymentDto confirmPaymentDto = new PaymentDto();
        if (null != paymentDetails) {
            confirmPaymentDto = new PaymentDto(paymentDtoKeyedObj);
            confirmPaymentDto.setRecieptNumber(paymentDetails.getReceiptNumber());
            confirmPaymentDto.setReceiptId(EncodedString.fromPlainText(paymentDetails.getReceiptNumber()).toString());
            confirmPaymentDto.setAmount(paymentDetails.getAmount());
            confirmPaymentDto.setTransactionDate(DEFAULT_FORMAT.format(paymentDetails.getTransactionDate()));
        }
        return confirmPaymentDto;
    }

    public static List<LinkedAccount> movePrimaryOnTop(List<LinkedAccount> items) {
        int index = 0;
        List<LinkedAccount> copy;
        for (LinkedAccount item : items)
            if (item.isPrimary()) {
                index = items.indexOf(item);
            }
        if (index >= 0) {
            copy = new ArrayList<LinkedAccount>(items.size());
            copy.addAll(items.subList(0, index));
            copy.add(0, items.get(index));
            copy.addAll(items.subList(index + 1, items.size()));
        } else {
            copy = new ArrayList<LinkedAccount>(items);
        }
        return copy;
    }

    public static List<LinkedAccount> sortLinkedAccount(List<LinkedAccount> lstLinkedAccounts, final String model) {
        Collections.sort(lstLinkedAccounts, new Comparator<LinkedAccount>() {
            @Override
            public int compare(LinkedAccount l1, LinkedAccount l2) {

                if ((null == l1.getNickName() && null == l2.getNickName()) || null != model)
                    return l1.getName().compareToIgnoreCase(l2.getName());
                else if (null != l1.getNickName() && null == l2.getNickName())
                    return l1.getNickName().compareToIgnoreCase(l2.getName());
                else if (null == l1.getNickName() && null != l2.getNickName())
                    return l1.getName().compareToIgnoreCase(l2.getNickName());
                else
                    return l1.getNickName().compareToIgnoreCase(l2.getNickName());
            }
        });
        return lstLinkedAccounts;
    }

    public static List<PayAnyOne> sortPayAnyoneAccount(List<PayAnyOne> lstPayAnyOneAccounts, final String model) {
        Collections.sort(lstPayAnyOneAccounts, new Comparator<PayAnyOne>() {
            @Override
            public int compare(PayAnyOne p1, PayAnyOne p2) {
                if ((null == p1.getNickName() && null == p2.getNickName()) || null != model)
                    return p1.getName().compareToIgnoreCase(p2.getName());
                else if (null != p1.getNickName() && null == p2.getNickName())
                    return p1.getNickName().compareToIgnoreCase(p2.getName());
                else if (null == p1.getNickName() && null != p2.getNickName())
                    return p1.getName().compareToIgnoreCase(p2.getNickName());
                else
                    return p1.getNickName().compareToIgnoreCase(p2.getNickName());
            }
        });

        return lstPayAnyOneAccounts;
    }

    public static List<Biller> sortBPayAccount(List<Biller> lstBPAYAccounts, final String model) {
        Collections.sort(lstBPAYAccounts, new Comparator<Biller>() {
            @Override
            public int compare(Biller b1, Biller b2) {
                if ((null == b1.getNickName() && null == b2.getNickName()) || null != model)
                    return b1.getName().compareToIgnoreCase(b2.getName());
                else if (null != b1.getNickName() && null == b2.getNickName())
                    return b1.getNickName().compareToIgnoreCase(b2.getName());
                else if (null == b1.getNickName() && null != b2.getNickName())
                    return b1.getName().compareToIgnoreCase(b2.getNickName());
                else
                    return b1.getNickName().compareToIgnoreCase(b2.getNickName());
            }
        });
        return lstBPAYAccounts;
    }

    private static String getPayeeName(PaymentDto paymentDtoTrx){
        String payeeName=StringUtils.isNotBlank(paymentDtoTrx.getToPayteeDto().getAccountName())
                ? paymentDtoTrx.getToPayteeDto().getAccountName() : paymentDtoTrx.getToPayteeDto().getNickname();
        return payeeName;
    }

}
