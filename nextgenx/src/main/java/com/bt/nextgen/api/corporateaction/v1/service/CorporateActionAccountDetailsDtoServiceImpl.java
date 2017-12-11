package com.bt.nextgen.api.corporateaction.v1.service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.api.corporateaction.v1.permission.CorporateActionPermissionService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionStatus;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class CorporateActionAccountDetailsDtoServiceImpl implements CorporateActionAccountDetailsDtoService {
    @Autowired
    private CorporateActionAccountDetailsConverter accountDetailsConverter;

    @Autowired
    private CorporateActionTransactionDetailsConverter transactionDetailsConverter;

    @Autowired
    private CorporateActionCommonService commonService;

    @Autowired
    private CorporateActionPermissionService corporateActionPermissionService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CorporateActionAccountDetailsDto> toCorporateActionAccountDtoList(CorporateActionContext context,
                                                                                  List<CorporateActionAccount> accounts,
                                                                                  CorporateActionSavedDetails savedDetails,
                                                                                  ServiceErrors serviceErrors) {
        final List<CorporateActionAccountDetailsDto> result = new ArrayList<>();

        if (accounts != null) {
            CorporateActionSupplementaryDetails supplementaryDetails = new CorporateActionSupplementaryDetails();
            supplementaryDetails.setClientAccountDetails(commonService.loadClientAccountDetails(context, accounts, serviceErrors));
            supplementaryDetails.setTransactionStatus(CorporateActionTransactionStatus.PRE_EX_DATE);

            if (CorporateActionGroup.MANDATORY.equals(context.getCorporateActionDetails().getCorporateActionType().getGroup()) &&
                    (CorporateActionStatus.OPEN.equals(context.getCorporateActionDetails().getCorporateActionStatus()) ||
                            CorporateActionStatus.PENDING.equals(context.getCorporateActionDetails().getCorporateActionStatus())) &&
                    context.getCorporateActionDetails().getExDate().isBefore(new DateTime())) {
                supplementaryDetails.setTransactionStatus(CorporateActionTransactionStatus.POST_EX_DATE);
            }

            if (CorporateActionStatus.CLOSED.equals(context.getCorporateActionDetails().getCorporateActionStatus())) {
                supplementaryDetails.setTransactionDetails(getTransactionDetails(context, serviceErrors));
            }

            // Go through each account and create the account dto object
            for (CorporateActionAccount caa : accounts) {
                if (corporateActionPermissionService.checkInvestorPermission(caa.getAccountId())) {
                    // If customer is not direct or ASIM add to the list same as it was working previously
                    // If logged in customer is investor and account is direct/asim account add to list or filter it.
                    final CorporateActionAccountDetailsDto dto = accountDetailsConverter
                            .createAccountDetailsDto(context, supplementaryDetails, caa, savedDetails, serviceErrors);

                    if (dto != null) {
                        result.add(dto);
                    }
                }
            }
        }

        return result;
    }

    private List<CorporateActionTransactionDetails> getTransactionDetails(CorporateActionContext context,
            ServiceErrors serviceErrors) {

        List<CorporateActionTransactionDetails> transactionDetailsList = null;
        boolean isMandatory = CorporateActionGroup.MANDATORY.equals(context.getCorporateActionDetails().getCorporateActionType()
                .getGroup());
        boolean isVoluntary = CorporateActionGroup.VOLUNTARY.equals(context.getCorporateActionDetails().getCorporateActionType()
                .getGroup());
        boolean isPastExDate = context.getCorporateActionDetails().getExDate().isBeforeNow();

        if (isVoluntary || (isMandatory && isPastExDate)) {
                transactionDetailsList = context.isDealerGroup() ? transactionDetailsConverter.loadTransactionDetailsForIm(
                        context, serviceErrors) : transactionDetailsConverter.loadTransactionDetails(context, serviceErrors);
        }

        return transactionDetailsList;
    }
}
