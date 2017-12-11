package com.bt.nextgen.service.integration.regularinvestment;

import com.bt.nextgen.api.order.model.OrderGroupKey;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;

import java.util.List;

public interface RegularInvestmentDelegateService {
    public RegularInvestment validateRegularInvestment(RegularInvestment regularInvestment, ServiceErrors serviceErrorsDD,
            ServiceErrors serviceErrorsRIP);

    public RegularInvestment submitRegularInvestment(RegularInvestment regularInvestment, ServiceErrors errorsDD,
            ServiceErrors errorsRIP);

    /**
     * Suspend a regular investment. Any linked account transaction will also be stopped via the INPAY service.
     * 
     * @param key
     *            Order group key identifier
     * @param serviceErrors
     * @return An instance of the updated regularInvestment.
     */
    public RegularInvestment suspendRegularInvestment(OrderGroupKey key, ServiceErrors serviceErrors);

    /**
     * Cancel an existing Regular Investment. Any linked account transaction will also be stopped via the INPAY service.
     * 
     * @param key
     *            Order group key identifier
     * @param serviceErrors
     * @return An instance of the updated regularInvestment.
     */
    public RegularInvestment cancelRegularInvestment(OrderGroupKey key, ServiceErrors serviceErrors);

    /**
     * Resume a suspended Regular Investment. Previously linked account transaction will need to be recreated.
     * 
     * @param key
     * @param serviceErrorsDD
     * @param serviceErrorsRIP
     * @return An instance of the updated regularInvestment.
     */
    public RegularInvestment resumeRegularInvestment(OrderGroupKey key, ServiceErrors serviceErrorsDD,
            ServiceErrors serviceErrorsRIP);

    List<RegularInvestmentTransaction> loadRegularInvestments(AccountKey accountKey, ServiceErrors serviceErrors, String mode);

    RegularInvestment loadRegularInvestment(AccountKey accountKey, String ripId, ServiceErrors serviceErrorsDD,
            ServiceErrors serviceErrorsRip);

    public RegularInvestment saveRegularInvestment(RegularInvestment regularInvestment, ServiceErrors serviceErrorsDD,
                                                   ServiceErrors serviceErrorsRIP);

}
