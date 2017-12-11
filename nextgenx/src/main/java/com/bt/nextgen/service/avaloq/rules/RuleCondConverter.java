package com.bt.nextgen.service.avaloq.rules;

import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.conversion.StaticCodeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by M041926 on 5/10/2016.
 */
@Component
public class RuleCondConverter implements Converter<String, RuleCond> {

    @Autowired
    private StaticCodeConverter staticCodeConverter;

    @Override
    public RuleCond convert(String code) {
        String intlCode = staticCodeConverter.convert(code, CodeCategory.RULE_CONDITION.name());
        return RuleCond.fromIntlCode(intlCode);
    }
}
