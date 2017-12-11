package com.bt.nextgen.api.draftaccount.model.form.v1;

import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.draftaccount.model.form.IFeeComponentForm;
import com.bt.nextgen.api.draftaccount.model.form.IFeeComponentTier;
import com.bt.nextgen.api.draftaccount.schemas.v1.fees.FeeComponent;

import java.util.List;

import static ch.lambdaj.Lambda.convert;

/**
 * Single component of a fee structure.
 */
class FeeComponentForm implements IFeeComponentForm {

    /** Converter. */
    public static final Converter<FeeComponent, IFeeComponentForm> CONVERTER = new Converter<FeeComponent, IFeeComponentForm>() {
        @Override
        public IFeeComponentForm convert(FeeComponent component) {
            return new FeeComponentForm(component);
        }
    };

    private final FeeComponent component;

    private FeeComponentForm(FeeComponent component) {
        this.component = component;
    }

    @Override
    public String getLabel() {
        return component.getLabel();
    }

    @Override
    public String getManagedFund() {
        return component.getManagedFund();
    }

    @Override
    public String getManagedPortfolio() {
        return component.getManagedPortfolio();
    }

    @Override
    public String getTermDeposit() {
        return component.getTermDeposit();
    }

    @Override
    public String getCashFunds() {
        return component.getCash();
    }

    @Override
    public String getAmount() {
        return component.getAmount();
    }

    @Override
    public String getListedSecurities() {
        return component.getListedSecurities();
    }

    @Override
    public String getDeposit() {
        return component.getDeposit();
    }

    @Override
    public String getPersonalContribution() {
        return component.getPersonal();
    }

    @Override
    public String getEmployerContribution() {
        return component.getEmployer();
    }

    @Override
    public String getSpouseContribution() {
        return component.getSpouse();
    }

    @Override
    public Boolean isCpiIndexed() {
        return component.getCpiindex();
    }

    @Override
    public Boolean isForManagedFund() {
        return Boolean.TRUE.toString().equals(component.getManagedFund());
    }

    @Override
    public Boolean isForManagedPortfolio() {
        return Boolean.TRUE.toString().equals(component.getManagedPortfolio());
    }

    @Override
    public Boolean isForCash() {
        return Boolean.TRUE.toString().equals(component.getCash());
    }

    @Override
    public Boolean isForTermDeposit() {
        return Boolean.TRUE.toString().equals(component.getTermDeposit());
    }

    @Override
    public Boolean isForListedSecurities() {
        return Boolean.TRUE.toString().equals(component.getListedSecurities());
    }

    @Override
    public List<IFeeComponentTier> getSlidingScaleFeeTiers() {
        return convert(component.getSlidingScaleFeeTier(), FeeComponentTier.CONVERTER);
    }
}
