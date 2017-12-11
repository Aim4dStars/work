package com.bt.nextgen.api.staticreference.service;

import com.bt.nextgen.api.staticreference.model.StaticReference;
import com.bt.nextgen.api.staticreference.model.StaticReferenceDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.insurance.model.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


@Service
public class StaticReferenceDtoServiceImpl implements StaticReferenceDtoService {
    private static final HashMap<String, StaticReference[]> staticReferenceMap = new HashMap<>();

    static {
        staticReferenceMap.put("policyType", PolicyType.values());
        staticReferenceMap.put("policyStatus", PolicyStatusCode.values());
        staticReferenceMap.put("benefitType", BenefitType.values());
        staticReferenceMap.put("premiumType", PremiumType.values());
        staticReferenceMap.put("benefitOption", BenefitOptionType.values());
        staticReferenceMap.put("policySubType", PolicySubType.values());
        staticReferenceMap.put("tpdDefinition", TPDBenefitDefinitionCode.values());
        staticReferenceMap.put("premiumFrequency", PremiumFrequencyType.values());
    }

    @Override
    public List<StaticReferenceDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        String referenceKeys = "";
        final List<StaticReferenceDto> staticReferenceDtoList = new ArrayList<>();
        final List<String> referenceKeyList = new ArrayList<>();

        for (ApiSearchCriteria criteria : criteriaList) {
            if ("category".equalsIgnoreCase(criteria.getProperty())) {
                referenceKeys = criteria.getValue();
            }
        }

        if (StringUtils.isNotBlank(referenceKeys)) {
            if (referenceKeys.equalsIgnoreCase("All")) {
                referenceKeyList.addAll(staticReferenceMap.keySet());
            } else {
                referenceKeyList.addAll(Arrays.asList(referenceKeys.split(",")));
            }

            for (String key : referenceKeyList) {
                StaticReference[] references = staticReferenceMap.get(key);

                if (references != null) {
                    for (StaticReference referenceObject : references) {
                        StaticReferenceDto staticRefDto = new StaticReferenceDto(referenceObject.getCode(), referenceObject.getCode(), referenceObject.getLabel());
                        staticRefDto.setCategory(key);
                        staticReferenceDtoList.add(staticRefDto);
                    }
                }
            }
        }

        return staticReferenceDtoList;
    }
}