package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.form.IFeesComponentsForm;
import com.bt.nextgen.api.draftaccount.model.form.IFeesForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.fees.Fees;
import com.bt.nextgen.api.draftaccount.schemas.v1.fees.FeesComponentType;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * Implementation of the {@code IFeesForm} interface.
 */
class FeesForm implements IFeesForm {

    private final Fees fees;

    /**
     * Existing fees constructor.
     * @param fees fees component from the application form JSON.
     */
    public FeesForm(Fees fees) {
        this.fees = fees == null ? new Fees() : fees;
    }

    @Override
    public BigDecimal getEstablishmentFee() {
        final String estAmount = fees.getEstamount();
        return estAmount == null ? ZERO : new BigDecimal(estAmount);
    }

    @Override
    public boolean hasOngoingFees() {
        return notEmpty(fees.getOngoingFees());
    }

    @Override
    public IFeesComponentsForm getOngoingFeesComponent() {
        return new FeesComponentsForm(fees.getOngoingFees());
    }

    @Override
    public boolean hasLicenseeFees() {
        return notEmpty(fees.getLicenseeFees());
    }

    @Override
    public IFeesComponentsForm getLicenseeFeesComponent() {
        return new FeesComponentsForm(fees.getLicenseeFees());
    }

    @Override
    public boolean hasContributionFees() {
        return notEmpty(fees.getContributionFees());
    }

    @Override
    public IFeesComponentsForm getContributionFeesComponent() {
        return new FeesComponentsForm(fees.getContributionFees());
    }

    @Override
    public Fees getFees() {
        return fees;
    }

    private boolean notEmpty(FeesComponentType component) {
        return component != null && isNotEmpty(component.getFeesComponent());
    }
}
