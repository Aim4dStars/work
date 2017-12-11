package com.bt.nextgen.api.corporateaction.v1.service;

import ch.lambdaj.function.convert.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionApprovalDecisionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionApprovalDecisionListDto;
import com.bt.nextgen.core.security.UserRole;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionApprovalDecisionGroupImpl;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionApprovalDecisionImpl;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionApprovalDecision;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionApprovalDecisionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionApprovalIntegrationService;
import com.bt.nextgen.service.integration.trustee.IrgApprovalStatus;
import com.bt.nextgen.service.integration.trustee.TrusteeApprovalStatus;

import static ch.lambdaj.Lambda.convert;

/**
 * This is the class to handle account election submission.
 */

@Service
@Transactional(value = "springJpaTransactionManager")
public class CorporateActionApprovalDtoServiceImpl implements CorporateActionApprovalDtoService {
    @Autowired
    private CorporateActionApprovalIntegrationService approvalIntegrationService;

    @Autowired
    private CorporateActionHelper helper;

    @Override
    public CorporateActionApprovalDecisionListDto submit(CorporateActionApprovalDecisionListDto corporateActionApprovalDecisionListDto,
                                                         ServiceErrors serviceErrors) {
        boolean irgUser = helper.hasUserRole(serviceErrors, UserRole.IRG_BASIC);

//        if (irgUser) {
//            // Update holding limit.  This has no dependency to approval decision
//            // TODO: update holding limit
//        }

        CorporateActionApprovalDecisionGroup corporateActionApprovalDecisionGroup = new CorporateActionApprovalDecisionGroupImpl(
                convert(corporateActionApprovalDecisionListDto.getCorporateActionApprovalDecisions(),
                        new ApprovalDecisionDtoConverter(irgUser)));

        return convertToApprovalDecisionListDto(
                approvalIntegrationService.submitApprovalDecisionGroup(corporateActionApprovalDecisionGroup, serviceErrors));
    }

    private CorporateActionApprovalDecisionListDto convertToApprovalDecisionListDto(CorporateActionApprovalDecisionGroup decisionGroup) {
        return new CorporateActionApprovalDecisionListDto(decisionGroup.getResponseCode());
    }

    private class ApprovalDecisionDtoConverter implements Converter<CorporateActionApprovalDecisionDto, CorporateActionApprovalDecision> {
        private boolean irg;

        public ApprovalDecisionDtoConverter(boolean irg) {
            this.irg = irg;
        }

        public CorporateActionApprovalDecision convert(CorporateActionApprovalDecisionDto dto) {
            IrgApprovalStatus irgApprovalStatus = null;
            TrusteeApprovalStatus trusteeApprovalStatus = null;

            if (irg) {
                irgApprovalStatus = IrgApprovalStatus.forName(dto.getApprovalDecision());

                if (irgApprovalStatus == IrgApprovalStatus.DECLINED) {
                    trusteeApprovalStatus = TrusteeApprovalStatus.DECLINED;
                }
            } else {
                trusteeApprovalStatus = TrusteeApprovalStatus.forName(dto.getApprovalDecision());
            }

            return new CorporateActionApprovalDecisionImpl(EncodedString.toPlainText(dto.getId()), irgApprovalStatus,
                    trusteeApprovalStatus);
        }
    }
}
