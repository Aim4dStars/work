package com.bt.nextgen.api.draftaccount.model.form;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by m040398 on 14/03/2016.
 */
public interface IFeesForm {

    BigDecimal getEstablishmentFee();
    boolean hasOngoingFees();
    IFeesComponentsForm getOngoingFeesComponent();
    boolean hasLicenseeFees();
    IFeesComponentsForm getLicenseeFeesComponent();
    boolean hasContributionFees();
    IFeesComponentsForm getContributionFeesComponent();


    /**
     * This method returns the object which stores the fees details (the old Map<String, Object> or the new auto-generated Schema POJO Fees)
     * @return
     */
    public Object getFees();
}
