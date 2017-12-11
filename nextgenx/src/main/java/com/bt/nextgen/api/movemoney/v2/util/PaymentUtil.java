package com.bt.nextgen.api.movemoney.v2.util;

import com.btfin.panorama.service.integration.account.Biller;
import com.btfin.panorama.service.integration.account.LinkedAccount;
import com.bt.nextgen.service.integration.account.PayAnyOne;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("squid:MethodCyclomaticComplexity")
public class PaymentUtil {
    public static final SimpleDateFormat DEFAULT_FORMAT = new SimpleDateFormat("dd MMM yyyy");

    private PaymentUtil() {
        // Util class
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

    public static List<Biller> sortBPayAccount(List<Biller> lstBpayAccounts, final String model) {
        Collections.sort(lstBpayAccounts, new Comparator<Biller>() {
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
        return lstBpayAccounts;
    }

}
