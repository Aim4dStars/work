package com.bt.nextgen.api.account.v2.service;

import com.bt.nextgen.api.account.v2.model.TermDepositAccountDto;
import com.bt.nextgen.api.account.v2.util.AuditUtil;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.TermDepositInterfaceModel;
import com.bt.nextgen.service.avaloq.portfolio.valuation.TermDepositHoldingImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.TermDepositAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.bt.nextgen.service.integration.termdeposit.TermDepositIntegrationService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Deprecated
@Service("TermDepositAccountDtoServiceV2")
public class TermDepositAccountDtoServiceImpl implements TermDepositAccountDtoService {
    @Autowired
    private TermDepositIntegrationService termDepositIntegrationService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    @Qualifier("avaloqPortfolioIntegrationService")
    private PortfolioIntegrationService portfolioService;

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    protected TermDepositHoldingImpl findTermDepositAccount(AccountKey accountKey, String tdAccountId, ServiceErrors serviceErrors) {
        WrapAccountValuation valuation = portfolioService.loadWrapAccountValuation(accountKey, new DateTime(), serviceErrors);

        String accId = EncodedString.toPlainText(tdAccountId);
        for (SubAccountValuation subAccount : valuation.getSubAccountValuations()) {
            if (subAccount.getAssetType() == AssetType.TERM_DEPOSIT) {
                TermDepositAccountValuation termDepositAccount = (TermDepositAccountValuation) subAccount;
                for (AccountHolding holding : termDepositAccount.getHoldings()) {
                    TermDepositHoldingImpl td = (TermDepositHoldingImpl) holding;
                    String tdAccId = td.getHoldingKey().getHid().getId();
                    if (tdAccId.equals(accId)) {
                        return td;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public TermDepositAccountDto update(TermDepositAccountDto termDepositAccountDto, ServiceErrors serviceErrors) {
        TermDepositInterfaceModel termDepositRequest = new TermDepositInterfaceModel();
        termDepositRequest.setPortfolioId(EncodedString.toPlainText(termDepositAccountDto.getKey().getAccountId()));
        termDepositRequest.setTdAccountId(EncodedString.toPlainText(termDepositAccountDto.getTdAccountId()));
        final Code code = staticIntegrationService.loadCodeByUserId(CodeCategory.TD_RENEW_MODE,
                termDepositAccountDto.getRenewModeId(), serviceErrors);
        if (code != null) {
            termDepositRequest.setRenewModeId(code.getCodeId());
        }
        boolean success = termDepositIntegrationService.updateTermDeposit(termDepositRequest, serviceErrors);

        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(termDepositAccountDto.getKey().getAccountId()));

        TermDepositHoldingImpl tdHolding = findTermDepositAccount(accountKey, termDepositAccountDto.getTdAccountId(),
                serviceErrors);

        if (success) {
            AuditUtil.auditMaturityChange(accountService, accountKey, termDepositAccountDto, tdHolding, serviceErrors);
            return termDepositAccountDto;
        }
        return termDepositAccountDto;

    }
}
