package com.bt.nextgen.api.draftaccount.model.form;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;

import java.util.List;
import java.util.Map;

/**
 * @deprecated Should be using the v1 version of this class instead.
 */
@Deprecated
class FeeComponentForm implements IFeeComponentForm{

    private final Map<String, Object> map;

    public FeeComponentForm(Map<String, Object> map) {
        this.map = map;
    }

    public String getLabel() {
        return (String) map.get("label");
    }

    public String getManagedFund() {
        return (String) map.get("managedFund");
    }

    public String getManagedPortfolio() {
        return (String) map.get("managedPortfolio");
    }

    public String getTermDeposit() {
        return (String) map.get("termDeposit");
    }

    public String getCashFunds() {
        return (String) map.get("cash");
    }

    public String getAmount() {
        return (String) map.get("amount");
    }

    public String getListedSecurities() {
        return (String) map.get("listedSecurities");
    }

    @Override
    public String getDeposit() {
        throw new IllegalStateException("this method should not be called directly on old draft applications");
    }

    @Override
    public String getPersonalContribution() {
        throw new IllegalStateException("this method should not be called directly on old draft applications");
    }

    @Override
    public String getEmployerContribution() {
        throw new IllegalStateException("this method should not be called directly on old draft applications");
    }

    @Override
    public String getSpouseContribution() {
        throw new IllegalStateException("this method should not be called directly on old draft applications");
    }

    public Boolean isCpiIndexed() {
        return (Boolean) map.get("cpiindex");
    }

    public Boolean isForManagedFund() {
        return Boolean.parseBoolean((String) map.get("managedFund"));
    }

    public Boolean isForManagedPortfolio() {
        return Boolean.parseBoolean((String) map.get("managedPortfolio"));
    }

    public Boolean isForCash() {
        return Boolean.parseBoolean((String) map.get("cash"));
    }

    public Boolean isForTermDeposit() {
        return Boolean.parseBoolean((String) map.get("termDeposit"));
    }

    public Boolean isForListedSecurities() {
        return Boolean.parseBoolean((String) map.get("listedSecurities"));
    }

    public List<IFeeComponentTier> getSlidingScaleFeeTiers() {
        return Lambda.convert(map.get("slidingScaleFeeTier"), new Converter<Map<String, String>, IFeeComponentTier>() {
            @Override
            public IFeeComponentTier convert(Map<String, String> map) {
                return new FeeComponentTier(map);
            }
        });
    }
}
