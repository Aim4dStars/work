package com.bt.nextgen.api.draftaccount.model.form;


import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;

import java.util.List;
import java.util.Map;

/**
 * @deprecated Should be using the v1 version of this class instead.
 */
@Deprecated
class FeesComponentsForm implements IFeesComponentsForm {

    private final List<Map<String, Object>> feesComponentMap;

    public FeesComponentsForm(List<Map<String, Object>> feesComponentMap) {
        this.feesComponentMap = feesComponentMap;
    }

    public List<IFeeComponentForm> getElements() {
        return Lambda.convert(feesComponentMap, new Converter<Map<String, Object>, IFeeComponentForm>() {
            public IFeeComponentForm convert(Map<String, Object> feeElement) {
                return new FeeComponentForm(feeElement);
            }
        });
    }

}
