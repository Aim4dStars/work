package com.bt.nextgen.core.reporting;

import java.io.InputStream;
import java.io.OutputStream;

import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.account.PersonRelation;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bt.nextgen.reports.web.model.ReportRequestDto;
import com.bt.nextgen.reports.web.model.ReportRequestPackDto;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.CacheAccountIntegrationService;
import com.bt.nextgen.service.integration.client.CacheClientIntegrationService;
import com.bt.nextgen.service.integration.portfolio.CachePortfolioIntegrationService;
import com.bt.nextgen.service.integration.userinformation.ClientKey;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.core.IsEqual.equalTo;

@Service("accountPdfReportPackService")
public class AccountPdfReportPackServiceImpl extends PdfReportPackServiceDtoImpl {
    private static final String ACCOUNT_ID = "account-id";
    private static final String EFFECTIVE_DATE = "effective-date";

    @Autowired
    private CacheAccountIntegrationService cacheAccountIntegrationService;

    @Autowired
    private CacheClientIntegrationService cacheClientIntegrationService;

    @Autowired
    @Qualifier("cacheAvaloqPortfolioIntegrationService")
    private CachePortfolioIntegrationService cachedPortfolioIntegrationService;

    public void create(ReportRequestPackDto reportRequestPackDto, InputStream coverLetter, OutputStream outputStream) {
        ReportRequestDto reportRequestDto = reportRequestPackDto.getReportRequestDtos().iterator().next();

        String accountId = (String) reportRequestDto.getParams().get(ACCOUNT_ID);
        String effectiveDate = (String) reportRequestDto.getParams().get(EFFECTIVE_DATE);

        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(accountId));

        // Clear account cache
        cacheAccountIntegrationService.clearWrapAccountDetailCache(accountKey);

        // Clear primary client details cache
        ClientKey clientKey = getClientKey(accountKey);
        cacheClientIntegrationService.clearClientDetailsCache(clientKey);

        // Clear portfolio valuation caches
        DateTime valuationDate = new DateTime(effectiveDate);
        cachedPortfolioIntegrationService.clearAccountValuationCache(accountKey, valuationDate);
        cachedPortfolioIntegrationService.clearAccountValuationCache(accountKey, valuationDate, true);
        cachedPortfolioIntegrationService.clearAccountValuationCache(accountKey, valuationDate, false);

        super.create(reportRequestPackDto, coverLetter, outputStream);
    }

    @Override
    public String getReportFileName(AccountKey accountKey) {
        WrapAccountDetail accountDetail = cacheAccountIntegrationService.loadWrapAccountDetail(accountKey, new FailFastErrorsImpl());
        return accountDetail.getAccountName() + " " + accountDetail.getAccountNumber();
    }

    private ClientKey getClientKey(AccountKey accountKey) {
        WrapAccountDetail wrapAccountDetail = cacheAccountIntegrationService.loadWrapAccountDetail(accountKey, new FailFastErrorsImpl());

        PersonRelation personRelation = selectFirst(wrapAccountDetail.getAssociatedPersons().values(),
                having(on(PersonRelation.class).isPrimaryContact(), equalTo(true)));

        return personRelation.getClientKey();
    }
}
