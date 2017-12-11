package com.bt.nextgen.api.draftaccount.model.form;

import java.util.List;

/**
 * Created by m040398 on 14/03/2016.
 */
public interface IFeeComponentForm {

    String getLabel();

    String getManagedFund();

    String getManagedPortfolio();

    String getTermDeposit();

    String getCashFunds();

    String getAmount();

    String getListedSecurities();

    String getDeposit();

    String getPersonalContribution();

    String getEmployerContribution();

    String getSpouseContribution();

    Boolean isCpiIndexed();

    Boolean isForManagedFund();

    Boolean isForManagedPortfolio();

    Boolean isForCash();

    Boolean isForTermDeposit();

    Boolean isForListedSecurities();

    List<IFeeComponentTier> getSlidingScaleFeeTiers();

}
