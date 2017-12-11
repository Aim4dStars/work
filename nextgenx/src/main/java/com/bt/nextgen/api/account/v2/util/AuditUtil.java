package com.bt.nextgen.api.account.v2.util;

import com.bt.nextgen.api.account.v2.model.TermDepositAccountDto;
import com.bt.nextgen.core.util.LogMarkers;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.portfolio.web.model.PortfolioModel;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.portfolio.valuation.TermDepositHoldingImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.termdeposit.web.model.TermDepositAccountModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public final class AuditUtil {
    private static final Logger logger = LoggerFactory.getLogger(AuditUtil.class);

    private AuditUtil() {
    }

    public static void auditMaturityChange(AccountIntegrationService accountService, AccountKey accountKey,
            TermDepositAccountDto termDepositAccountDto, TermDepositHoldingImpl tdHolding, ServiceErrors serviceErrors) {
        // Audit update term deposit
        WrapAccountDetail accDetails = accountService.loadWrapAccountDetail(accountKey, serviceErrors);
        PortfolioModel accModel = new PortfolioModel();
        accModel.setAccountId(termDepositAccountDto.getKey().getAccountId());
        accModel.setAccountName(accDetails.getAccountName());
        accModel.setAccountType(accDetails.getAccountType().name());
        TermDepositAccountModel tdModel = new TermDepositAccountModel();
        tdModel.setInvestmentAmount(tdHolding.getBalance().toString());
        tdModel.setTermDuration(ApiFormatter.asShortDate(tdHolding.getMaturityDate()));
        LogMarkers.audit_changing_maturityInstruction(accModel, LogMarkers.Status.SUCCESS, tdModel, logger);
        termDepositAccountDto.setInvestmentAmount(tdModel.getInvestmentAmount());
        termDepositAccountDto.setTermDuration(tdModel.getTermDuration());
    }
}
