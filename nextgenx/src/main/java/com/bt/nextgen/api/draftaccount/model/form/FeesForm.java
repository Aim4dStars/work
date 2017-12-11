package com.bt.nextgen.api.draftaccount.model.form;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @deprecated Should be using the v1 version of this class instead.
 */
@Deprecated
class FeesForm implements IFeesForm {

    private final Map<String, Object> feesMap;

    public FeesForm(Map<String, Object> feesMap) {
        this.feesMap = feesMap;
    }

    public BigDecimal getEstablishmentFee() {
        return new BigDecimal((String) feesMap.get("estamount"));
    }

    public boolean hasOngoingFees() {
        return hasFeeType("ongoingFees");
    }

    public FeesComponentsForm getOngoingFeesComponent() {
        return getFeesComponent("ongoingFees");
    }

    public boolean hasLicenseeFees() {
        return hasFeeType("licenseeFees");
    }

    public FeesComponentsForm getLicenseeFeesComponent() {
        return getFeesComponent("licenseeFees");
    }

    private FeesComponentsForm getFeesComponent(String feeType) {
        return new FeesComponentsForm(getFeesComponentMap(feeType));
    }

    private boolean hasFeeType(String feeType) {
        return (feesMap.get(feeType) != null) && isPresent(getFeesComponentMap(feeType));
    }

    private List<Map<String, Object>> getFeesComponentMap(String feeType) {
        return (List<Map<String, Object>>) ((Map) feesMap.get(feeType)).get("feesComponent");
    }

    private boolean isPresent(Collection c) {
        return (c != null) && !c.isEmpty();
    }

    @Override
    public Map<String, Object> getFees() {
        return feesMap;
    }

    @Override
    public boolean hasContributionFees() {
        return hasFeeType("contributionFees");
    }

    @Override
    public IFeesComponentsForm getContributionFeesComponent() {
        throw new IllegalStateException("this method should not be called directly on old draft applications");
    }
}
