package com.bt.nextgen.api.corporateaction.v1.service.converter;

import java.util.ArrayList;
import java.util.List;

import com.btfin.panorama.core.conversion.CodeCategory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionsDto;
import com.bt.nextgen.api.corporateaction.v1.service.stereotype.CorporateActionRequestConverter;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionDecisionImpl;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionElectionGroupImpl;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionPosition;

@CorporateActionRequestConverter("CA_SHARE_PURCHASE_PLAN_REQUEST")
public class SharePurchasePlanRequestConverterServiceImpl extends AbstractCorporateActionRequestConverterServiceImpl {
    @Autowired
    private StaticIntegrationService staticCodeService;

    @Override
    protected CorporateActionElectionGroup createElectionGroupCommon(CorporateActionContext context,
                                                                     CorporateActionSelectedOptionsDto optionsDto,
                                                                     List<CorporateActionPosition> positions) {
        CorporateActionSelectedOptionDto optionDto = optionsDto.getPrimarySelectedOption();

        CorporateActionElectionGroup electionGroup =
                new CorporateActionElectionGroupImpl(context.getCorporateActionDetails().getOrderNumber(), optionDto, positions,
                        new ArrayList<CorporateActionOption>());

        String avaloqOptionId = toAvaloqOptionId(optionDto.getOptionId());

        if (avaloqOptionId != null) {
            electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.SUBSCRIBED_OPTION.getCode(),
                    avaloqOptionId));
        }

        return electionGroup;
    }

    @Override
    protected int getMaxOptions() {
        return CorporateActionConverterConstants.MAX_SHARE_PURCHASE_PLAN_OPTIONS;
    }

    private String toAvaloqOptionId(int optionId) {
        String optionValue = CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_ID == optionId ?
                             CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_VALUE :
                             CorporateActionConverterConstants.OPTION_PREFIX + optionId;

        final Code code = staticCodeService.loadCodeByUserId(CodeCategory.CA_SHARE_PURCHASE_PLAN_OPTION, optionValue, null);

        return code != null ? code.getCodeId() : null;
    }
}
